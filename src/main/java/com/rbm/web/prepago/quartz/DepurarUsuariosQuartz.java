package com.rbm.web.prepago.quartz;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ibm.ams.audit.dto.EventSuccessEnum;
import com.ibm.ams.audit.dto.EventTypeEnum;
import com.ibm.ams.audit.dto.SeverityEnum;
import com.ibm.ams.correo.configuracion.Adjunto;
import com.ibm.ams.correo.configuracion.InformacionCorreoDTO;
import com.ibm.ams.correo.generadorcorreo.EmailSender;
import com.ibm.ams.db.ManejadorDeDatosDB;
import com.ibm.ams.excepcion.XmlToDtoException;
import com.ibm.ams.ldap.gestores.GestorUsuarios;
import com.ibm.ams.log.Logger;
import com.rbm.web.prepago.configuracion.Configuracion;
import com.rbm.web.prepago.dto.Historico;
import com.rbm.web.prepago.dto.Parametro;
import com.rbm.web.prepago.dto.UsuarioInactivo;
import com.rbm.web.prepago.gestores.GestorAuditoria;
import com.rbm.web.prepago.gestores.GestorBD;
import com.rbm.web.prepago.gestores.GestorConfiguracionLDAP;
import com.rbm.web.prepago.pagecode.PageCodeBase;
import com.rbm.web.prepago.util.ConstantesWebPrepago;

public class DepurarUsuariosQuartz extends PageCodeBase implements Job{

	private static Logger logger = Logger.getInstance(ConstantesWebPrepago.NOMBRE_APLICACION, ConstantesWebPrepago.DIRECTORIO_CONFIGURACION);
	private GestorUsuarios gestorUsuarios = GestorUsuarios.getInstance(getConfigParamLdap(), getConfigMappingLdap());
	private String diasNotificar;
	private String diasEliminar;
	
	
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		logger.debug(this.getClass().getName(), "execute", "Invoca el metodo para depurar usuarios");
		List<Parametro> listaParametros = new ArrayList<Parametro>();
		List<UsuarioInactivo> usuariosNotificar = new ArrayList<>();
		List<UsuarioInactivo> usuariosEliminar = new ArrayList<>();
		
		String correosInforme;
		try {
			listaParametros = GestorBD.getParametros();
			this.diasNotificar = consultarValorParametro(listaParametros, ConstantesWebPrepago.PARAMETRO_DIAS_NOTIFICAR_TAREA);
			this.diasEliminar = consultarValorParametro(listaParametros, ConstantesWebPrepago.PARAMETRO_DIAS_ELIMINAR_TAREA);
			correosInforme = consultarValorParametro(listaParametros, ConstantesWebPrepago.PARAMETRO_CORREOS_TAREA);
			
			/**Metodos para la notificacion de inactividad*/
			usuariosNotificar = GestorBD.getUsuariosInactivos(Integer.parseInt(this.diasNotificar));
			if(usuariosNotificar.size() > 0) {
				notificarUsuarios(usuariosNotificar, correosInforme);
			}
			
			/**Metodos para la eliminación por inactividad*/
			usuariosEliminar = GestorBD.getUsuariosInactivosTarea(Integer.parseInt(this.diasEliminar));
			if(usuariosEliminar.size() > 0) {
				eliminarUsuarios(usuariosEliminar, correosInforme);
			}
			//gestorUsuarios.eliminarUsuario();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	private void notificarUsuarios(List<UsuarioInactivo> usuarios, String correosInforme) {
		List<UsuarioInactivo> listaInformeUsuInactivos = new ArrayList<UsuarioInactivo>();
		try {
			for(UsuarioInactivo usuInact: usuarios) {
				if(usuInact.getFechaNotificacion() == null || usuInact.getFechaNotificacion().isEmpty()) {
					enviarCorreoNotificar(usuInact,false);
					GestorBD.ActualizarUsuarioNotificacion(usuInact.getUsuario());
					listaInformeUsuInactivos.add(usuInact);
				}
			}
			if(listaInformeUsuInactivos.size() > 0) {
				enviarCorreoAdmin(correosInforme, false, usuarios);
			}
		} catch (Exception e) {
			logger.error(this.getClass().getName(), "notificarUsuarios", "Error notificando usuarios inactivos", e);
		}
		
	}

	private String consultarValorParametro(List<Parametro> parametros, String parametro) {
		String valor = null;
		for(Parametro param: parametros) {
			if(param.getParametroNombre().equals(parametro)) {
				valor = param.getParametroValor();
				break;
			}
		}
		return valor;
	}
	
	private void enviarCorreoNotificar(UsuarioInactivo usuario, Boolean eliminar) throws IOException {
		Map<String, Object> informacionCorreo = new HashMap<String, Object>();
		informacionCorreo.put("usuario", usuario.getUsuario());
	    informacionCorreo.put("fecha", usuario.getFechaUltimaConexion());
	    informacionCorreo.put("correo", usuario.getCorreo());
		List<Adjunto> adjuntos=new ArrayList<Adjunto>();
		Adjunto adjunto = new Adjunto();
		adjunto.setNombre("logo_rbm.png");
		adjunto.setMimeType("image/png");
		adjunto.setContentId("mailLogo");
		adjunto.setData(getByte(System.getProperty(ConstantesWebPrepago.CONFIGURACION_DIR) + ConstantesWebPrepago.DIRECTORIO_CONFIGURACION + File.separatorChar + ConstantesWebPrepago.DIRECTORIO_IMAGENES + File.separatorChar + "logo_rbm.png"));
		adjunto.setInline(true);
		adjuntos.add(adjunto);

		InformacionCorreoDTO correo = new InformacionCorreoDTO();
		List<String> remitentes = new ArrayList<String>();
		remitentes.add(usuario.getCorreo());

		correo.setInformacionComercio(informacionCorreo);
		correo.setTo(remitentes);
		correo.setCc(null);
		correo.setBcc(null);
		correo.setAdjuntos(adjuntos);
		try {								
			EmailSender sender = new EmailSender();
			 if(!eliminar) {
		    	  sender.enviarCorreo(correo, ConstantesWebPrepago.PLANTILLA_CORREO_WEBPREPAGO_NOTIFICAR_INACTIVIDAD);
		      }else {
			      sender.enviarCorreo(correo, ConstantesWebPrepago.PLANTILLA_CORREO_WEBPREPAGO_NOTIFICAR_ELIMINACION);
		      }
			logger.info(this.getClass().getName(), "enviarCorreo", "Correo electrenico  enviado exitosamente");
		} catch (Exception e) {
			logger.error(this.getClass().getName(), "enviarCorreo", "Error enviando correo", e);
		}

	}

	private void eliminarUsuarios(List<UsuarioInactivo> usuariosEliminar, String correosInforme) {
		List<UsuarioInactivo> listaFinalUsuarios = new ArrayList<UsuarioInactivo>();
		for (UsuarioInactivo usuInact : usuariosEliminar) {
			Connection conexion = null;
			Configuracion conf = null;
			
			try {
				conf = GestorConfiguracionLDAP.obtenerConfiguracion();
				conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf.getDataSource());
				conexion.setAutoCommit(false);
		        enviarCorreoNotificar(usuInact, true);
				GestorBD.EliminarTarjetasUsuarioInacitvoJob(usuInact.getId(),conexion);
				GestorBD.EliminarUsuarioInactivoJob(usuInact.getId(),conexion);
								
				Historico his = new Historico();
				his.setUsuario(usuInact.getUsuario());
				his.setNumtarjeta(usuInact.getNumTarjetaEnmascarada());
				his.setEntidad(usuInact.getEntidad());
				his.setAccion("eliminar usuario inactivo");
				GestorBD.registrarHistoricoJob(his,conexion);
				
				if(gestorUsuarios.eliminarUsuario(usuInact.getUsuario())) {
					conexion.commit();
					listaFinalUsuarios.add(usuInact);
				}else {
					conexion.rollback();
				}
			} catch (Exception e) {
				try {
					conexion.rollback();
					logger.error(this.getClass().getName(), "notificarUsuarios", "Error eliminando el usuario: "+usuInact.getNombres(), e);
				} catch (Exception e2) {
					logger.error(this.getClass().getName(), "notificarUsuarios", "Error roll back ,eliminando el usuario: "+usuInact.getNombres(), e);
				}
			}finally {
				if(conexion != null) {
					ManejadorDeDatosDB.cerrarConexion(conexion);
				}
			}		
		}
		enviarCorreoAdmin(correosInforme, true, listaFinalUsuarios);
	}
	
	public void enviarCorreoAdmin(String correos, boolean eliminados, List<UsuarioInactivo> listaUsuarios) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();	
		Map<String, Object> informacionCorreo = new HashMap<>();
		java.util.Date fecha = new Date();
		SimpleDateFormat format = new SimpleDateFormat(ConstantesWebPrepago.FORMATO_FECHA_DDMMYYYY);
		SimpleDateFormat format1 = new SimpleDateFormat(ConstantesWebPrepago.FORMATO_FECHA_XLS);
		try {
			informacionCorreo.put("fecha", format1.format(fecha));
		    informacionCorreo.put("usuarios", listaUsuarios.size());
		    informacionCorreo.put("operacion", eliminados ? "eliminaci&oacute;n" : "notificaci&oacute;n");
		    informacionCorreo.put("dias", eliminados ? this.diasEliminar : this.diasNotificar);
		    List<Adjunto> adjuntos = new ArrayList<>();
		    Adjunto adjunto = new Adjunto();
		    adjunto.setNombre("logo_rbm.png");
		    adjunto.setMimeType("image/png");
		    adjunto.setContentId("mailLogo");
			adjunto.setData(getByte(System.getProperty("SWF_PORTAL_CONFIGPATH") + "webPrepago" + File.separatorChar + "imagenes" + File.separatorChar + "logo_rbm.png"));
		    adjunto.setInline(true);
		    adjuntos.add(adjunto);
		    
		    adjunto = new Adjunto();
			adjunto.setNombre("Reporte usuarios inactivos " +format.format(fecha)+ ".xls");
			adjunto.setMimeType("application/xls");
			adjunto.setContentId("");
			adjunto.setData(generarExcel(out, listaUsuarios, eliminados));		
			adjunto.setInline(false);
			adjuntos.add(adjunto);
			
		    InformacionCorreoDTO correo = new InformacionCorreoDTO();
		    List<String> remitentes = new ArrayList<String>();
			for (String valor :correos.split(";")) {
				remitentes.add(valor);
			}
		    correo.setInformacionComercio(informacionCorreo);
		    correo.setTo(remitentes);
		    correo.setCc(null);
		    correo.setBcc(null);
		    correo.setAdjuntos(adjuntos);
		 
		    EmailSender sender = new EmailSender();
		    sender.enviarCorreo(correo, ConstantesWebPrepago.PLANTILLA_CORREO_WEBPREPAGO_INACTIVIDAD);
		    logger.info(getClass().getName(), "enviarCorreo", "Correo electrónico  enviado exitosamente", new Object[0]);
			    
		} catch (IOException e) {
			logger.error(this.getClass().getName(), "Enviar Correo ","", e);
			//Se registra evento de auditoria
			GestorAuditoria.getInstance().registrarAuditoria(SeverityEnum.ERROR, EventSuccessEnum.SUCCESS.name(), 
					EventTypeEnum.ERROR.value(), "Error al enviar correo ", " reporte Excel usuarios: " + e.getMessage(),  "", "");
		} catch (Exception e) {
		      logger.info(getClass().getName(), "enviarCorreo", "Error enviando correo", e, new Object[0]);
		    } 
	 
	}
	
	public byte[] generarExcel(ByteArrayOutputStream out, List<UsuarioInactivo> listaUsuarios, boolean eliminarUsuarios) {
		List<UsuarioInactivo> eliminar = listaUsuarios; 
		Map<String,Object> beans = new HashMap<String,Object>();
		java.util.Date fecha = new Date();
		SimpleDateFormat format = new SimpleDateFormat(ConstantesWebPrepago.FORMATO_FECHA_XLS);
		try {
			beans.put("registros", eliminar);
			beans.put("operacion", (eliminarUsuarios) ? " Eliminación ":" Notificación ");
			beans.put("fechaGeneracion", format.format(fecha));
			com.ibm.ams.reportes.GestorReportes reportes = new com.ibm.ams.reportes.GestorReportes(ConstantesWebPrepago.REPORTES_CONFIG_PATH);
			reportes.getReporte(out, ConstantesWebPrepago.PLANTILLA__REPORTE_USUARIOS_INACTIVOS, beans);
			//Se registra evento de auditoria
			GestorAuditoria.getInstance().registrarAuditoria(SeverityEnum.ERROR, EventSuccessEnum.SUCCESS.name(), 
					EventTypeEnum.ERROR.value(), "Crear reporte excel ", " ","","" );
		} catch (FileNotFoundException e) {
			logger.error(this.getClass().getName(), "exportarReporte","", e);
			//Se registra evento de auditoria
			GestorAuditoria.getInstance().registrarAuditoria(SeverityEnum.ERROR, EventSuccessEnum.SUCCESS.name(), 
					EventTypeEnum.ERROR.value(), "Error al crear ", " reporte Excel usuarios: " + e.getMessage(), "", "");
		} catch (IOException e) {
			logger.error(this.getClass().getName(), "exportarReporte","", e);

			//Se registra evento de auditoria
			GestorAuditoria.getInstance().registrarAuditoria(SeverityEnum.ERROR, EventSuccessEnum.SUCCESS.name(), 
					EventTypeEnum.ERROR.value(), "Error al crear ", " reporte Excel usuarios: " + e.getMessage(),  "", "");
		} catch (XmlToDtoException e) {
			logger.error(this.getClass().getName(), "exportarReporte","", e);
			
			//Se registra evento de auditoria
			GestorAuditoria.getInstance().registrarAuditoria(SeverityEnum.ERROR, EventSuccessEnum.SUCCESS.name(), 
					EventTypeEnum.ERROR.value(), "Error al crear ", " reporte Excel usuarios: " + e.getMessage(),  "", "");
		}
	return out.toByteArray();
 }

	
}
