package com.rbm.web.prepago.managedBeans;
	
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
//import com.ibm.ws.batch.xJCL.beans.returnCodeExpression;
import com.rbm.web.prepago.dto.Historico;
import com.rbm.web.prepago.dto.Parametro;
import com.rbm.web.prepago.dto.TarjetaPrepago;
import com.rbm.web.prepago.dto.UsuarioInactivo;
import com.rbm.web.prepago.dto.UsuarioInfoErrada;
import com.rbm.web.prepago.exception.ExceptionWebPrepago;
import com.rbm.web.prepago.gestores.GestorAuditoria;
import com.rbm.web.prepago.gestores.GestorBD;
import com.rbm.web.prepago.managedBeans.AdministrarTarjetaBean;
import com.rbm.web.prepago.pagecode.PageCodeBase;
import com.rbm.web.prepago.util.ConstantesWebPrepago;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIOutput;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.view.Location;

import org.primefaces.component.calendar.Calendar;
import org.primefaces.component.column.Column;
import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.component.confirmdialog.ConfirmDialog;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.messages.Messages;
import org.primefaces.component.outputlabel.OutputLabel;
import org.primefaces.component.outputpanel.OutputPanel;
import org.primefaces.component.panel.Panel;
import org.primefaces.component.panelgrid.PanelGrid;
import org.primefaces.component.picklist.PickList;
import org.primefaces.context.RequestContext;
import org.primefaces.event.data.PageEvent;
	
	@ManagedBean
	@SessionScoped
	public class EliminarUsuariosInactivosBean extends PageCodeBase {
	  private static Logger logger = Logger.getInstance("webPrepago", "webPrepago");
	  
	  private List<UsuarioInactivo> usuarios = new ArrayList<>();
	  
	  private List<UsuarioInactivo> listaUsuarios = new ArrayList<>();
	  
	  private String ip;
	  
	  protected CommandButton BotonAdFranDisp;
	  
	  protected HtmlInputHidden IpValue;
	  
	  protected CommandButton procesar;
	  
	  protected HtmlOutputLabel lblRadicacion;
	  
	  protected CommandButton agregar;
	  
	  protected CommandButton comisiones;
	  
	  protected CommandButton limpiar;
	  
	  protected Messages mensajeCuenta;
	  
	  protected OutputLabel labelParametros;
	  
	  protected CommandButton aceptar;
	  
	  protected PickList pickList;
	  
	  protected CommandButton cancelar;
	  
	  protected Messages msgDlgComision;
	  
	  protected CommandButton aceptarComision;
	  
	  protected CommandButton cancelarComision;
	  
	  protected Panel PanelRegistro;
	  
	  protected Messages mensajes;
	  
	  protected PanelGrid displayConfirmacionInfoErrada;
	  
	  protected CommandButton confirmarCambiosInfo;
	  
	  protected HtmlForm eliminarUsuariosForm;
	  
	  protected Column columnUsuario;
	  
	  protected Column columnFechaCreacion;
	  
	  protected Column columnTiempoInactividad;
	  
	  protected Column columnFechaNotificacion;
	  
	  protected CommandButton deshacerCambiosInfo;
	  
	  protected HtmlInputHidden ipValue;
	  
	  protected UIOutput utils;
	  
	  protected DataTable depuracionInformacionErrada;
	  
	  protected Column columnNumTarjeta;
	  
	  protected Column columnEntidad;
	  
	  protected ConfirmDialog confirmacionEliminarTarjeta;
	  
	  protected ConfirmDialog confirmarSelec;
	  
	  protected CommandButton confirmarCambiosTarjeta;
	  
	  protected CommandButton deshacerCambiosTarjeta;
	  
	  protected PanelGrid displayConfirmacionTarjeta;
	  
	  protected Column columnFechaUltimaActividad;
	  
	  protected OutputPanel PanelValidacionPreguntas;
	  
	  protected ConfirmDialog confirmacionEliminarPorInfoErrada;
	  
	  protected DataTable usuariosInactivos;
	  
	  protected Panel panelInactivos;
	  
	  protected CommandButton botonEliminar;
	  
	  protected CommandButton botonNotificar;
	  
	  protected Column columnFechaRegistro;
	  
	  protected PanelGrid displayConfirmacion;
	  
	  protected CommandButton confirmarCambios;
	  
	  protected CommandButton deshacerCambios;
	  
	  private boolean mostrarPanelConsulta;
	  
	  private boolean mostrartablaUsuariosInfoErr;
	  
	  private boolean mostrarPanelUsuInactivos;
	  
	  private int tipoDepuracionSelected;
	  
	  private boolean mostrarBotonVolver;
	  
	  private int numUsuariosEliminar;
	  
	  private String campoIdUsuario;
	  
	  private String campoNumTarjera;
	  
	  private Date campoFechaRegistro;
	  
	  private List<UsuarioInfoErrada> listaUsuariosInfoErrada;
	  
	  private String numeroTarEliminar;
	  
	  private String numerotarEliminarEncrip;
	  
	  private UsuarioInfoErrada usuarioInfoErrada;
	  
	  private boolean mostrarCausal;
	  
	  private String mensajeCausal;
	  
	  private Integer diasInactividad;
	  
	  private Integer correoConfig;
	  
	  private Boolean botonesSelec;
	  
	  protected CommandButton botonProcesar;
	  
	  protected HtmlSelectOneMenu selectTipoDepuracion;
	  
	  protected HtmlInputText idUsuario;
	  
	  protected HtmlInputText NumTarjetaId;
	  
	  protected CommandButton botonConsultar;
	  
	  protected OutputPanel consultaInfoErr;
	  
	  protected OutputPanel panelTipoDepuracion;
	  
	  protected OutputPanel panelInfoErronea;
	  
	  protected OutputPanel panelUsuariosInactivos;
	  
	  protected CommandButton botonVolver;
	  
	  protected OutputPanel panelBtnVolver;
	  
	  protected Calendar FechaRegistro;
	  
	  protected ConfirmDialog confirmacionEliminar;
	  
	  private String key = "Bar12345Bar12345";
	  
	  private String initVector = "RandomInitVector";
	  
	  private GestorUsuarios gestorUsuarios = GestorUsuarios.getInstance(getConfigParamLdap(), getConfigMappingLdap());
	  
	  private boolean Eliminar;
	  
	  private boolean mostrarFiltroInactividad;
	  
	  private Integer filas;
	   
	  private int rowsPerPage = 5; //default value

	  private List<Parametro> listaParametros = new ArrayList<Parametro>();

	  private String correosNotficar;

	  private int pagina;
	  private String correoInicial;
	  private String paramInactividad;

	/**
		 * Ruta configurada en el servidor donde se ubican los archivos de configuracion de la aplicacion
		 **/
	
	  private String diasNotificasSelected;

	  private String diasEliminarSelected;

	  private List<UsuarioInactivo> usuariosEliminar = new ArrayList<>();

	  private List<UsuarioInactivo> usuariosNotificar = new ArrayList<>();
	  
	  public EliminarUsuariosInactivosBean() {
	    this.mostrarPanelConsulta = false;
	    this.mostrartablaUsuariosInfoErr = false;
	    this.mostrarPanelUsuInactivos = false;
	    this.mostrarBotonVolver = false;
	    this.listaUsuariosInfoErrada = new ArrayList<>();
	    setBotonesSelec(false);
	  }
	  
	  public void onTipoDepuracionChange() {
	    setMostrarFiltroInactividad(false);
	    switch (this.tipoDepuracionSelected) {
	      case 0:
	        this.mostrarPanelConsulta = false;
	        this.mostrartablaUsuariosInfoErr = false;
	        this.mostrarPanelUsuInactivos = false;
	        this.mostrarBotonVolver = false;
	        break;
	      case 1:
	        this.mostrarPanelConsulta = false;
	        this.mostrartablaUsuariosInfoErr = false;
	        this.mostrarBotonVolver = true;
	        this.usuarios = new ArrayList<>();
	        try {
	          this.filas=5;
	          this.usuarios = GestorBD.getUsuariosInactivos(null);
	          this.mostrarPanelUsuInactivos = true;
	          setMostrarFiltroInactividad(true);
	          for (UsuarioInactivo usuInac : this.usuarios) {
	            if (usuInac.getFechaNotificacion() == null) {
	              usuInac.setEliminar(false);
	              usuInac.setDeshabilitarEliminar(true);
	              usuInac.setDeshabilitarNotificar(false);
	              usuInac.setNotificar(true);
	              continue;
	            } 
	            usuInac.setEliminar(true);
	            usuInac.setDeshabilitarEliminar(false);
	            usuInac.setDeshabilitarNotificar(true);
	            usuInac.setNotificar(false);
	          } 
	          this.listaUsuarios = this.usuarios;
	          if(this.listaUsuarios.size()>=1) {
	        	  setBotonesSelec(true);
	          }
	          this.listaParametros=GestorBD.getParametros();
			  for(Parametro param: this.listaParametros) {
				  if(param.getParametroNombre().equals(ConstantesWebPrepago.PARAMETRO_DIAS_NOTIFICAR_TAREA)) {
						this.setDiasNotificasSelected(param.getParametroValor());
					}else if(param.getParametroNombre().equals(ConstantesWebPrepago.PARAMETRO_DIAS_ELIMINAR_TAREA)) {
						this.setDiasEliminarSelected(param.getParametroValor());
					}else if(param.getParametroNombre().equals(ConstantesWebPrepago.PARAMETRO_CORREOS_TAREA)) {
						this.setCorreosNotficar((param.getParametroValor().trim().equals(""))?"":param.getParametroValor());
						this.setCorreoInicial(this.getCorreosNotficar());
				  }else if(param.getParametroNombre().equals(ConstantesWebPrepago.PARAMETRO_DIAS_INACTIVIDAD)) {
					  this.setParamInactividad(param.getParametroValor());
					 
				  }
			  }
	        } catch (ExceptionWebPrepago e) {
	          FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Estamos presentando problemas tpor favor contacte al Administrador del Sistema", ""));
	          e.printStackTrace();
	          logger.info(getClass().getName(), "OnTipoDepuracionChange", "Error al cambiar el tipo de depuracion", (Throwable)e, new Object[0]);
	        } 
	        break;
	      case 2:
	        this.mostrarPanelConsulta = true;
	        this.mostrartablaUsuariosInfoErr = false;
	        this.mostrarPanelUsuInactivos = false;
	        this.mostrarBotonVolver = true;
	        break;
	    } 
	    RequestContext.getCurrentInstance().update("panelTipoDepuracion consultaInfoErr mensajes");
	  }
	  
	  public void botonVolver() {
	    this.mostrarPanelConsulta = false;
	    this.mostrartablaUsuariosInfoErr = false;
	    this.mostrarPanelUsuInactivos = false;
	    this.mostrarBotonVolver = false;
	    this.tipoDepuracionSelected = 0;
        setMostrarFiltroInactividad(false);
	    limpiarCampos();
	  }
	  
	  public void consultarInfoErronea() {
	    try {
	      this.listaUsuariosInfoErrada = new ArrayList<>();
	      if (validarCamposConsultarInfoErr()) {
	        String clave = new String(AdministrarTarjetaBean.encrypt(this.key, this.initVector, this.campoNumTarjera));
	        this.listaUsuariosInfoErrada = GestorBD.getUsuariosInfoErrada(this.campoIdUsuario, this.campoNumTarjera, this.campoFechaRegistro, clave);
	        if (this.listaUsuariosInfoErrada.isEmpty()) {
	          FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "No se han encontrado registros para la consulta", ""));
	          this.mostrartablaUsuariosInfoErr = false;
	        } else {
	          this.mostrartablaUsuariosInfoErr = true;
	        } 
	      } 
	    } catch (Exception e) {
	      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error realizando la consulta", ""));
	      logger.info(getClass().getName(), "ConsutaInfoErronea", "Error realizando la consulta", e, new Object[0]);
	    } 
	  }
	  
	  private boolean validarCamposConsultarInfoErr() {
	    boolean valida = true;
	    String mensaje = "";
	    if ((this.campoIdUsuario == null || this.campoIdUsuario.isEmpty()) && (this.campoNumTarjera == null || this.campoNumTarjera
	      .isEmpty()) && this.campoFechaRegistro == null) {
	      mensaje = "Debe ingresar al menos alguno de los siguientes campos: Usuario, Número de Tarjeta, Fecha de Registro";
	      valida = false;
	    } 
	    if (!valida)
	      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, mensaje, "")); 
	    return valida;
	  }
	  
	  public void notificarInactividad() {
		 this.setEliminar(false);
		List<UsuarioInactivo> listaUsuariosDia = new ArrayList<>();
		if(this.getCorreosNotficar()==null || this.getCorreosNotficar().isEmpty()) {
			 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "El campo correo es obligatorio para realizar esta operación", ""));
			 logger.info(this.getClass().getName(), "El campo correo es obligatorio", "error parametro","");
		}else {
			if(this.actualizarCorreo()) {	    
				int numero = 0;
			    try {
			      for (UsuarioInactivo usuInact : this.usuarios) {
			        if (usuInact.isNotificar()) {
			          numero++;
			          enviarCorreoNotificar(usuInact, usuInact.isEliminar());
			          GestorBD.ActualizarUsuarioNotificacion(usuInact.getUsuario());
				       listaUsuariosDia.add(usuInact);
			        } 
			      } 
			      this.usuariosNotificar=listaUsuariosDia;		    	    
		          enviarCorreo();
			      onTipoDepuracionChange();
			    } catch (ExceptionWebPrepago e) {
			      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error consultando usuario", ""));
			      logger.info(getClass().getName(), "notificarInactividad", "Error consultando usuario", (Throwable)e, new Object[0]);
			      e.printStackTrace();
			    } catch (IOException e) {
			      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error enviando correo", ""));
			      logger.info(getClass().getName(), "notificarInactividad", "Error enviando correo", e, new Object[0]);
			      e.printStackTrace();
			    } 
			    if (numero == 0) {
			      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe seleccionar al menos 1 usuario para notificar", "")); 
			    }else { 
			    	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Se notificaron los usuarios Correctamente", ""));
			    }
			}else {
				logger.info(this.getClass().getName(), "Validar estructura de arreglo de correos", "error registrando parametro", this.getCorreosNotficar());
			} 
			 
		  }

	  }
	  
	  private void enviarCorreoNotificar(UsuarioInactivo usuario, Boolean eliminar) throws IOException {
	    Map<String, Object> informacionCorreo = new HashMap<>();
	    informacionCorreo.put("usuario", usuario.getUsuario());
	    informacionCorreo.put("fecha", usuario.getFechaUltimaConexion());
	    informacionCorreo.put("correo", usuario.getCorreo());
	    List<Adjunto> adjuntos = new ArrayList<>();
	    Adjunto adjunto = new Adjunto();
	    adjunto.setNombre("logo_rbm.png");
	    adjunto.setMimeType("image/png");
	    adjunto.setContentId("mailLogo");
	    adjunto.setData(getByte(System.getProperty("SWF_PORTAL_CONFIGPATH") + "webPrepago" + File.separatorChar + "imagenes" + File.separatorChar + "logo_rbm.png"));
	    adjunto.setInline(true);
	    adjuntos.add(adjunto);
	    InformacionCorreoDTO correo = new InformacionCorreoDTO();
	    List<String> remitentes = new ArrayList<>();
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
	      logger.info(getClass().getName(), "enviarCorreo", "Correo electrenico  enviado exitosamente", new Object[0]);
	    } catch (Exception e) {
	      logger.info(getClass().getName(), "enviarCorreo", "Error enviando correo", e, new Object[0]);
	    } 
	  }
	  public boolean validarCampos() {
		  String [] items = this.correosNotficar.split(";");
		  List<String> container = Arrays.asList(items);
		  for(String email : container ) {
		  	Pattern patronEmail = Pattern.compile("^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@" +
		  	      "[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4})$");
	        Matcher mEmail = patronEmail.matcher(email.toLowerCase());
	        if (!mEmail.matches()){
	        	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Estructura invalida del correo", email));
				logger.info(this.getClass().getName(), "Estructura invalida ", "del correo", email);
	        	return false;
	        }
		  } return true;
	  }
	  public boolean actualizarCorreo() {
		try {
			if (validarCampos()) {
				if (!this.correoInicial.equals(this.getCorreosNotficar())) {
					GestorBD.guardarParametrosTarea(this.diasNotificasSelected, this.diasEliminarSelected, this.correosNotficar);
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Parámetros registrados exitosamente", this.getCorreosNotficar()));
				}
			}else return false;
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al registrar parametros", ""));
			logger.info(this.getClass().getName(), "agregarParametro", "error registrando parametro", e);
			GestorAuditoria.getInstance().registrarAuditoria(SeverityEnum.ERROR, EventSuccessEnum.SUCCESS.name(), 
					EventTypeEnum.ERROR.value(),"Error al cargar registros de Entidades","onParametroChange",this.ip,"WebPrepago");
			return false;
		}
		return true;  
	  }
	  
	  public void listaUsuariosEliminar() {
		  if(this.getCorreosNotficar() ==null || this.getCorreosNotficar().isEmpty()) {
			  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "El campo correo es obligatorio para realizar esta operación", ""));
			  logger.info(this.getClass().getName(), "El campo correo es obligatorio", "error parametro","");
		  }else {
			  if(this.actualizarCorreo()) {
				    RequestContext context = RequestContext.getCurrentInstance();
				    this.numUsuariosEliminar = 0;
				    for (UsuarioInactivo usuInact : this.usuarios) {
				      if (usuInact.isEliminar())
				        this.numUsuariosEliminar++; 
				    } 
				    if (this.numUsuariosEliminar == 0) {
				      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe seleccionar al menos 1 usuario para eliminar", ""));
					} else {
					  context.execute("confirmacionEliminarVar.show()");
					    } 
				}else {
					logger.info(this.getClass().getName(), "Validar estructura de arreglo de correos", "error registrando parametro", this.getCorreosNotficar());
				}
		  }
			  
	  }
	  
	  public void filtrarDiasInac() {
		  Boolean validar = false;
			  try {
				  this.usuarios = GestorBD.getUsuariosInactivos(this.diasInactividad);
			  }catch (Exception e) {
			      logger.info(getClass().getName(), "Obetenr Usuarios Inactivos", "Error obteniedndo usuarios inactivos", (Throwable)e, new Object[0]);
			  } 
			 if(this.usuarios!=null && !this.usuarios.isEmpty()) {
				 validar=true;
				  for (UsuarioInactivo usuInac : this.usuarios) {
		            if (usuInac.getFechaNotificacion() == null) {
		              usuInac.setEliminar(false);
		              usuInac.setDeshabilitarEliminar(true);
		              usuInac.setDeshabilitarNotificar(false);
		              usuInac.setNotificar(true);
		              continue;
		            } 
		            usuInac.setEliminar(true);
		            usuInac.setDeshabilitarEliminar(false);
		            usuInac.setDeshabilitarNotificar(true);
		            usuInac.setNotificar(false);
			      } 
				  this.listaUsuarios = this.usuarios;
				  if(this.listaUsuarios.size()>=1) {
		        	  setBotonesSelec(true);
		          }
			  }
	  	if(!validar) {
	  		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "No se han encontrado registros que coincidan con la búsqueda", ""));
	  		setBotonesSelec(false);
	  	}
	  }
	  public void page(PageEvent event) {	  
		 this.pagina = event.getPage();		  
	  }
	  
	  public void asignarOp(boolean eliminar) {
	    setEliminar(eliminar);
	  }
		 
	  public void seleccionarTodos(boolean pagina) {
		Integer pInicial = this.rowsPerPage*this.pagina;
		List<UsuarioInactivo> listaUsuariosDia = new ArrayList<>();
	    RequestContext context = RequestContext.getCurrentInstance();
	    Integer i = 0;
	    if (isEliminar()) {
	    	System.out.println("Ver Usuarios -- "+this.usuarios);
	      for (UsuarioInactivo item : this.usuarios) {
	        if (!item.isDeshabilitarEliminar()) {
		        if(!pagina||(i>=pInicial && i<pInicial+this.rowsPerPage)) {
		          if (!item.isEliminar() ) {
		            item.setEliminar(true);
		          } else if (item.isEliminar()) {
		            item.setEliminar(false);
		          } 
		        }
		        listaUsuariosDia.add(i, item);
		        i++;
	        }     
	      } 
	      this.usuarios = listaUsuariosDia;
	    } else {
	      for (UsuarioInactivo item : this.usuarios) {
	    	  if (!item.isDeshabilitarNotificar()) {
		        if(!pagina || (i>=pInicial && i<pInicial+this.rowsPerPage)) {
		          if (!item.isNotificar() ) {
		            item.setNotificar(true);
		          } else if (item.isNotificar()) {
		            item.setNotificar(false);
		          } 
		        }
		         listaUsuariosDia.add(i, item);
		         i++;
		      } 
	      } 
	      this.usuarios = listaUsuariosDia;
	    } 
	    if (listaUsuariosDia == null || listaUsuariosDia.isEmpty())
	      this.usuarios = this.listaUsuarios; 
	    context.execute("confirmarSelec.hide();");
	  }	
	  
	  private static List<UsuarioInactivo> formarEstructura(List<UsuarioInactivo> items){
			List<UsuarioInactivo> itemsP = new ArrayList<UsuarioInactivo>();
			if(items != null && !items.isEmpty()){
				for(UsuarioInactivo i : items){
					itemsP.add(i);
				}
			}
			return itemsP;
		}
	  
	  public void enviarCorreo() {
			ByteArrayOutputStream out = new ByteArrayOutputStream();	
			Map<String, Object> informacionCorreo = new HashMap<>();
			java.util.Date fecha = new Date();
			SimpleDateFormat format = new SimpleDateFormat(ConstantesWebPrepago.FORMATO_FECHA_DDMMYYYY);
			SimpleDateFormat format1 = new SimpleDateFormat(ConstantesWebPrepago.FORMATO_FECHA_XLS);
			
			try {
			    //informacionCorreo.put("Reporte de ",  isEliminar() ? " Eliminación de usuarios inactivos ":" Notificación de usuarios inactivos");
			    informacionCorreo.put("fecha", format1.format(fecha));
			    informacionCorreo.put("usuarios", isEliminar() ? this.getUsuariosEliminar().size() : this.getUsuariosNotificar().size());
			    informacionCorreo.put("operacion", isEliminar() ? "eliminaci&oacute;n" : "notificaci&oacute;n");
			    informacionCorreo.put("dias", this.diasInactividad !=null && this.diasInactividad>0 ? this.diasInactividad : this.paramInactividad);
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
				adjunto.setData(generarExcel(out));		
				adjunto.setInline(false);
				adjuntos.add(adjunto);
				
			    InformacionCorreoDTO correo = new InformacionCorreoDTO();
			    List<String> remitentes = new ArrayList<String>();
				for (String valor : this.getCorreosNotficar()
						.split(";")) {
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
	  public byte[] generarExcel(ByteArrayOutputStream out) {
			List<UsuarioInactivo> eliminar = formarEstructura(this.getUsuariosEliminar()); 
			List<UsuarioInactivo> notificar = formarEstructura(this.getUsuariosNotificar()); 		
			Map<String,Object> beans = new HashMap<String,Object>();
			java.util.Date fecha = new Date();
			SimpleDateFormat format = new SimpleDateFormat(ConstantesWebPrepago.FORMATO_FECHA_XLS);
			try {
				beans.put("registros", isEliminar() ? eliminar:notificar);
				beans.put("operacion", isEliminar() ? " Eliminación ":" Notificación ");
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
 
	  public void eliminarInactivos() {
		 this.setEliminar(true);
		List<UsuarioInactivo> listaUsuariosDia = new ArrayList<>();
		int numero = 0;
		if(this.actualizarCorreo()) {
		    try {
		      for (UsuarioInactivo usuInact : this.usuarios) {
		        if (usuInact.isEliminar()) {
			          numero++;
			          enviarCorreoNotificar(usuInact, usuInact.isEliminar() );
			          GestorBD.EliminarTarjetasUsuarioInacitvo(usuInact.getId());
			          GestorBD.EliminarUsuarioInactivo(usuInact.getId());
			          this.gestorUsuarios.eliminarUsuario(usuInact.getUsuario());
			          Historico his = new Historico();
			          his.setUsuario(usuInact.getUsuario());
			          his.setNumtarjeta(usuInact.getNumTarjetaEnmascarada());
			          his.setEntidad(usuInact.getEntidad());
			          his.setAccion("eliminar usuario inactivo");
			          GestorBD.registrarHistorico(his);
			          listaUsuariosDia.add(usuInact);
		        }    
		      } 
		      this.usuariosEliminar=listaUsuariosDia;
		      enviarCorreo();
		      onTipoDepuracionChange();
		    } catch (ExceptionWebPrepago e) {
		      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Error Eliminando usuarios inacitvos", ""));
		      e.printStackTrace();
		      logger.info(getClass().getName(), "EliminarInactivos", "Error eliminando usuarios inactivos", (Throwable)e, new Object[0]);
		      return;
		    } catch (IOException e) {
		      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error enviando correo", ""));
		      logger.info(getClass().getName(), "EliminarInactivos", "Error enviando correo", e, new Object[0]);
		      e.printStackTrace();
		      return;
		    }  if (numero == 0) {
			      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe seleccionar al menos 1 usuario para notificar", "")); 
		    }
	    }else {
	    	logger.info(this.getClass().getName(), "Validar estructura de arreglo de correos", "error registrando parametro", this.getCorreosNotficar());
		      return;
		}
	    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Se elimina " + this.numUsuariosEliminar + " usuario (s) correctamente", ""));
	  }
	  
	  public void eliminarTarjeta(UsuarioInfoErrada usu) {
	    this.numeroTarEliminar = usu.getNumTarjetaEnmascarado();
	    this.numerotarEliminarEncrip = usu.getNumTarjetaEncriptado();
	    this.usuarioInfoErrada = usu;
	  }
	  
	  public void confirmarEliminarTarjeta() {
	    try {
		      GestorBD.EliminarTarjeta(this.numerotarEliminarEncrip, null);
		      Historico his = new Historico();
		      his.setUsuario(this.usuarioInfoErrada.getUsuario());
		      his.setNumtarjeta(this.numeroTarEliminar);
		      his.setEntidad(this.usuarioInfoErrada.getEntidad());
		      his.setAccion("Eliminar Tarjeta");
		      GestorBD.registrarHistorico(his);
		      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Se elimina la tarjeta ", ""));
		      limpiarCampos();
		      this.mostrartablaUsuariosInfoErr = false;
	    } catch (ExceptionWebPrepago e) {
		      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al eliminar la tarjeta deseada", ""));
		      e.printStackTrace();
		      logger.info(getClass().getName(), "ConfirmarEliminarTarjeta", "Error eliminando tarjeta", (Throwable)e, new Object[0]);
	    } catch (SQLException e) {
	    	logger.error(this.getClass().getName(), "ConfirmarEliminarTarjeta","Error en BD", e);
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR,ConstantesWebPrepago.CONSULTAR_SALDO_ME001, ""));
		} 
	  }
	  
	  private void limpiarCampos() {
		RequestContext context = RequestContext.getCurrentInstance();
	    this.campoFechaRegistro = null;
	    this.campoIdUsuario = "";
	    this.campoNumTarjera = "";
	   // context.execute("location.reload()");	  
	    }
	  
	  public void eliminarUsuInfoErr() {
		    RequestContext context = RequestContext.getCurrentInstance();
		    this.numUsuariosEliminar = 0;
		    for (UsuarioInfoErrada usu : this.listaUsuariosInfoErrada) {
		      if (usu.isEliminar() || usu.isEliminarYnotificar())
		        this.numUsuariosEliminar++; 
		    } 
		    for (UsuarioInfoErrada usu : this.listaUsuariosInfoErrada) {
		      if (usu.isEliminarYnotificar()) {
		        this.mostrarCausal = true;
		        break;
		      } 
		      this.mostrarCausal = false;
		    } 
		    if (this.numUsuariosEliminar == 0) {
		      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe seleccionar al menos un usuario para procesar", ""));
		    } else {
		      context.execute("confirmacionEliminarInfoErradaVar.show()");
		    } 
	  }
	  
	public void confirmarEliminarUsuInfoErr() {
	    Connection conexion = null;
	    Connection conexionBpo = null;
	    try {
	      conexion = GestorBD.getConexion();
	      conexionBpo = GestorBD.getConexionBpo();
	      for (UsuarioInfoErrada usu : this.listaUsuariosInfoErrada) {
	        if (GestorBD.consultarUsuario(usu.getId() + "", conexion) != null) {
	          if (usu.isEliminar() || usu.isEliminarYnotificar()) {
	            List<TarjetaPrepago> tarjetas = GestorBD.consultarTarjetasPorUsuario(usu.getId(), conexion, conexionBpo, ConstantesWebPrepago.BEAN_USUARIOS_INACTIVOS);
	            for (TarjetaPrepago tarjeta : tarjetas) {
	              String clave = new String(AdministrarTarjetaBean.encrypt(this.key, this.initVector, tarjeta.getNumTarjeta()));
	              GestorBD.EliminarTarjeta(clave, conexion);
	              Historico historico = new Historico();
	              historico.setAccion("Eliminacitarjeta usuario Informacion errada");
	              historico.setEntidad(usu.getEntidad());
	              historico.setNumtarjeta(tarjeta.getNumeroTarjeta());
	              historico.setUsuario(usu.getUsuario());
	              GestorBD.registrarHistorico(historico);
	            } 
	            GestorBD.EliminarUsuarioInactivo(usu.getId());
	            this.gestorUsuarios.eliminarUsuario(usu.getUsuario());
	            Historico his = new Historico();
	            his.setAccion("Eliminaciusuario Informacion errada");
	            his.setEntidad(usu.getEntidad());
	            his.setNumtarjeta("");
	            his.setUsuario(usu.getUsuario());
	            GestorBD.registrarHistorico(his);
	          } 
	          if (usu.isEliminar() && !usu.isEliminarYnotificar()) {
	            enviarCorreoEliminarUsuario("  ", usu.getCorreo());
	            continue;
	          } 
	          if (usu.isEliminarYnotificar())
	            enviarCorreoEliminarUsuario(this.mensajeCausal.toString(), usu.getCorreo()); 
	        } 
	      } 
	      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Se elimina " + this.numUsuariosEliminar + " usuario (s) correctamente ", ""));
	      consultarInfoErronea();
	    } catch (Exception e) {
	      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al eliminar y/o notificar usuario(s)", ""));
	      logger.info(getClass().getName(), "ConfirmarEliminarUsuarioInfoErr", "Error elimiando usuario y/o notificando", e, new Object[0]);
	    } finally {
	      ManejadorDeDatosDB.cerrarConexion(conexion);
	      ManejadorDeDatosDB.cerrarConexion(conexionBpo);
	    } 
	  }
	  
	  private void enviarCorreoEliminarUsuario(String causa, String correousu) throws IOException {
		    Map<String, Object> informacionCorreo = new HashMap<>();
		    informacionCorreo.put("causal", causa);
		    List<Adjunto> adjuntos = new ArrayList<>();
		    Adjunto adjunto = new Adjunto();
		    adjunto.setNombre("logo_rbm.png");
		    adjunto.setMimeType("image/png");
		    adjunto.setContentId("mailLogo");
		    adjunto.setData(getByte(System.getProperty("SWF_PORTAL_CONFIGPATH") + "webPrepago" + File.separatorChar + "imagenes" + File.separatorChar + "logo_rbm.png"));
		    adjunto.setInline(true);
		    adjuntos.add(adjunto);
		    InformacionCorreoDTO correo = new InformacionCorreoDTO();
		    List<String> remitentes = new ArrayList<>();
		    remitentes.add(correousu);
		    correo.setInformacionComercio(informacionCorreo);
		    correo.setTo(remitentes);
		    correo.setCc(null);
		    correo.setBcc(null);
		    correo.setAdjuntos(adjuntos);
		    try {
			      EmailSender sender = new EmailSender();
			      sender.enviarCorreo(correo, "correoWebPrepagoEliminarInfoErr.vm");
			      logger.info(getClass().getName(), "enviarCorreo", "Correo electrenico  enviado exitosamente", new Object[0]);
		    } catch (Exception e) {
		    		logger.info(getClass().getName(), "enviarCorreo", "Error enviando correo", e, new Object[0]);
		    } 
	  }
	  
	  public List<UsuarioInactivo> getUsuarios() {
	    return this.usuarios;
	  }
	  
	  public void setUsuarios(List<UsuarioInactivo> usuarios) {
	    this.usuarios = usuarios;
	  }
	  
	  protected CommandButton getProcesar() {
	    if (this.procesar == null)
	      this.procesar = (CommandButton)findComponentInRoot("procesar"); 
	    return this.procesar;
	  }
	  
	  protected HtmlOutputLabel getLblRadicacion() {
	    if (this.lblRadicacion == null)
	      this.lblRadicacion = (HtmlOutputLabel)findComponentInRoot("lblRadicacion"); 
	    return this.lblRadicacion;
	  }
	  
	  protected CommandButton getAgregar() {
	    if (this.agregar == null)
	      this.agregar = (CommandButton)findComponentInRoot("agregar"); 
	    return this.agregar;
	  }
	  
	  protected CommandButton getComisiones() {
	    if (this.comisiones == null)
	      this.comisiones = (CommandButton)findComponentInRoot("comisiones"); 
	    return this.comisiones;
	  }
	  
	  protected CommandButton getLimpiar() {
	    if (this.limpiar == null)
	      this.limpiar = (CommandButton)findComponentInRoot("limpiar"); 
	    return this.limpiar;
	  }
	  
	  protected Messages getMensajeCuenta() {
	    if (this.mensajeCuenta == null)
	      this.mensajeCuenta = (Messages)findComponentInRoot("mensajeCuenta"); 
	    return this.mensajeCuenta;
	  }
	  
	  protected OutputLabel getLabelParametros() {
	    if (this.labelParametros == null)
	      this.labelParametros = (OutputLabel)findComponentInRoot("labelParametros"); 
	    return this.labelParametros;
	  }
	  
	  protected CommandButton getAceptar() {
	    if (this.aceptar == null)
	      this.aceptar = (CommandButton)findComponentInRoot("aceptar"); 
	    return this.aceptar;
	  }
	  
	  protected PickList getPickList() {
	    if (this.pickList == null)
	      this.pickList = (PickList)findComponentInRoot("pickList"); 
	    return this.pickList;
	  }
	  
	  protected CommandButton getCancelar() {
	    if (this.cancelar == null)
	      this.cancelar = (CommandButton)findComponentInRoot("cancelar"); 
	    return this.cancelar;
	  }
	  
	  protected Messages getMsgDlgComision() {
	    if (this.msgDlgComision == null)
	      this.msgDlgComision = (Messages)findComponentInRoot("msgDlgComision"); 
	    return this.msgDlgComision;
	  }
	  
	  protected CommandButton getAceptarComision() {
	    if (this.aceptarComision == null)
	      this.aceptarComision = (CommandButton)findComponentInRoot("aceptarComision"); 
	    return this.aceptarComision;
	  }
	  
	  protected CommandButton getCancelarComision() {
	    if (this.cancelarComision == null)
	      this.cancelarComision = (CommandButton)findComponentInRoot("cancelarComision"); 
	    return this.cancelarComision;
	  }
	  
	  protected Messages getMensajes() {
	    if (this.mensajes == null)
	      this.mensajes = (Messages)findComponentInRoot("mensajes"); 
	    return this.mensajes;
	  }
	  
	  protected PanelGrid getDisplayConfirmacionInfoErrada() {
	    if (this.displayConfirmacionInfoErrada == null)
	      this.displayConfirmacionInfoErrada = (PanelGrid)findComponentInRoot("displayConfirmacionInfoErrada"); 
	    return this.displayConfirmacionInfoErrada;
	  }
	  
	  protected CommandButton getConfirmarCambiosInfo() {
	    if (this.confirmarCambiosInfo == null)
	      this.confirmarCambiosInfo = (CommandButton)findComponentInRoot("confirmarCambiosInfo"); 
	    return this.confirmarCambiosInfo;
	  }
	  
	  protected HtmlForm getEliminarUsuariosForm() {
	    if (this.eliminarUsuariosForm == null)
	      this.eliminarUsuariosForm = (HtmlForm)findComponentInRoot("eliminarUsuariosForm"); 
	    return this.eliminarUsuariosForm;
	  }
	  
	  protected Column getColumnUsuario() {
	    if (this.columnUsuario == null)
	      this.columnUsuario = (Column)findComponentInRoot("columnUsuario"); 
	    return this.columnUsuario;
	  }
	  
	  protected Column getColumnFechaCreacion() {
	    if (this.columnFechaCreacion == null)
	      this.columnFechaCreacion = (Column)findComponentInRoot("columnFechaCreacion"); 
	    return this.columnFechaCreacion;
	  }
	  
	  protected Column getColumnTiempoInactividad() {
	    if (this.columnTiempoInactividad == null)
	      this.columnTiempoInactividad = (Column)findComponentInRoot("columnTiempoInactividad"); 
	    return this.columnTiempoInactividad;
	  }
	  
	  protected Column getColumnFechaNotificacion() {
	    if (this.columnFechaNotificacion == null)
	      this.columnFechaNotificacion = (Column)findComponentInRoot("columnFechaNotificacion"); 
	    return this.columnFechaNotificacion;
	  }
	  
	  protected CommandButton getDeshacerCambiosInfo() {
	    if (this.deshacerCambiosInfo == null)
	      this.deshacerCambiosInfo = (CommandButton)findComponentInRoot("deshacerCambiosInfo"); 
	    return this.deshacerCambiosInfo;
	  }
	  
	  protected HtmlInputHidden getIpValue() {
	    if (this.IpValue == null)
	      this.IpValue = (HtmlInputHidden)findComponentInRoot("IpValue"); 
	    return this.IpValue;
	  }
	  
	  public String getIp() {
	    return this.ip;
	  }
	  
	  public void setIp(String ip) {
	    this.ip = ip;
	  }
	  
	  protected UIOutput getUtils() {
	    if (this.utils == null)
	      this.utils = (UIOutput)findComponentInRoot("utils"); 
	    return this.utils;
	  }
	  
	  protected DataTable getDepuracionInformacionErrada() {
	    if (this.depuracionInformacionErrada == null)
	      this.depuracionInformacionErrada = (DataTable)findComponentInRoot("depuracionInformacionErrada"); 
	    return this.depuracionInformacionErrada;
	  }
	  
	  protected Column getColumnNumTarjeta() {
	    if (this.columnNumTarjeta == null)
	      this.columnNumTarjeta = (Column)findComponentInRoot("columnNumTarjeta"); 
	    return this.columnNumTarjeta;
	  }
	  
	  protected Column getColumnEntidad() {
	    if (this.columnEntidad == null)
	      this.columnEntidad = (Column)findComponentInRoot("columnEntidad"); 
	    return this.columnEntidad;
	  }
	  
	  protected ConfirmDialog getConfirmacionEliminarTarjeta() {
	    if (this.confirmacionEliminarTarjeta == null)
	      this.confirmacionEliminarTarjeta = (ConfirmDialog)findComponentInRoot("confirmacionEliminarTarjeta"); 
	    return this.confirmacionEliminarTarjeta;
	  }
	  
	  protected ConfirmDialog getConfirmarSelec() {
	    if (this.confirmarSelec == null)
	      this.confirmarSelec = (ConfirmDialog)findComponentInRoot("confirmarSelec"); 
	    return this.confirmarSelec;
	  }
	  
	  protected CommandButton getConfirmarCambiosTarjeta() {
	    if (this.confirmarCambiosTarjeta == null)
	      this.confirmarCambiosTarjeta = (CommandButton)findComponentInRoot("confirmarCambiosTarjeta"); 
	    return this.confirmarCambiosTarjeta;
	  }
	  
	  protected CommandButton getDeshacerCambiosTarjeta() {
	    if (this.deshacerCambiosTarjeta == null)
	      this.deshacerCambiosTarjeta = (CommandButton)findComponentInRoot("deshacerCambiosTarjeta"); 
	    return this.deshacerCambiosTarjeta;
	  }
	  
	  protected PanelGrid getDisplayConfirmacionTarjeta() {
	    if (this.displayConfirmacionTarjeta == null)
	      this.displayConfirmacionTarjeta = (PanelGrid)findComponentInRoot("displayConfirmacionTarjeta"); 
	    return this.displayConfirmacionTarjeta;
	  }
	  
	  protected Column getColumnFechaUltimaActividad() {
	    if (this.columnFechaUltimaActividad == null)
	      this.columnFechaUltimaActividad = (Column)findComponentInRoot("columnFechaUltimaActividad"); 
	    return this.columnFechaUltimaActividad;
	  }
	  
	  protected ConfirmDialog getConfirmacionEliminarPorInfoErrada() {
	    if (this.confirmacionEliminarPorInfoErrada == null)
	      this.confirmacionEliminarPorInfoErrada = (ConfirmDialog)findComponentInRoot("confirmacionEliminarPorInfoErrada"); 
	    return this.confirmacionEliminarPorInfoErrada;
	  }
	  
	  protected DataTable getUsuariosInactivos() {
	    if (this.usuariosInactivos == null)
	      this.usuariosInactivos = (DataTable)findComponentInRoot("usuariosInactivos"); 
	    return this.usuariosInactivos;
	  }
	  
	  protected Panel getPanelInactivos() {
	    if (this.panelInactivos == null)
	      this.panelInactivos = (Panel)findComponentInRoot("panelInactivos"); 
	    return this.panelInactivos;
	  }
	  
	  protected CommandButton getBotonEliminar() {
	    if (this.botonEliminar == null)
	      this.botonEliminar = (CommandButton)findComponentInRoot("botonEliminar"); 
	    return this.botonEliminar;
	  }
	  
	  protected CommandButton getBotonNotificar() {
	    if (this.botonNotificar == null)
	      this.botonNotificar = (CommandButton)findComponentInRoot("botonNotificar"); 
	    return this.botonNotificar;
	  }
	  
	  protected Column getColumnFechaRegistro() {
	    if (this.columnFechaRegistro == null)
	      this.columnFechaRegistro = (Column)findComponentInRoot("columnFechaRegistro"); 
	    return this.columnFechaRegistro;
	  }
	  
	  protected PanelGrid getDisplayConfirmacion() {
	    if (this.displayConfirmacion == null)
	      this.displayConfirmacion = (PanelGrid)findComponentInRoot("displayConfirmacion"); 
	    return this.displayConfirmacion;
	  }
	  
	  protected CommandButton getConfirmarCambios() {
	    if (this.confirmarCambios == null)
	      this.confirmarCambios = (CommandButton)findComponentInRoot("confirmarCambios"); 
	    return this.confirmarCambios;
	  }
	  
	  protected CommandButton getDeshacerCambios() {
	    if (this.deshacerCambios == null)
	      this.deshacerCambios = (CommandButton)findComponentInRoot("deshacerCambios"); 
	    return this.deshacerCambios;
	  }
	  
	  public boolean isMostrarPanelConsulta() {
	    return this.mostrarPanelConsulta;
	  }
	  
	  public void setMostrarPanelConsulta(boolean mostrarPanelConsulta) {
	    this.mostrarPanelConsulta = mostrarPanelConsulta;
	  }
	  
	  public boolean isMostrartablaUsuariosInfoErr() {
	    return this.mostrartablaUsuariosInfoErr;
	  }
	  
	  public void setMostrartablaUsuariosInfoErr(boolean mostrartablaUsuariosInfoErr) {
	    this.mostrartablaUsuariosInfoErr = mostrartablaUsuariosInfoErr;
	  }
	  
	  public boolean isMostrarPanelUsuInactivos() {
	    return this.mostrarPanelUsuInactivos;
	  }
	  
	  public void setMostrarPanelUsuInactivos(boolean mostrarPanelUsuInactivos) {
	    this.mostrarPanelUsuInactivos = mostrarPanelUsuInactivos;
	  }
	  
	  public int getTipoDepuracionSelected() {
	    return this.tipoDepuracionSelected;
	  }
	  
	  public void setTipoDepuracionSelected(int tipoDepuracionSelected) {
	    this.tipoDepuracionSelected = tipoDepuracionSelected;
	  }
	  
	  protected CommandButton getBotonProcesar() {
	    if (this.botonProcesar == null)
	      this.botonProcesar = (CommandButton)findComponentInRoot("botonProcesar"); 
	    return this.botonProcesar;
	  }
	  
	  public boolean isMostrarBotonVolver() {
	    return this.mostrarBotonVolver;
	  }
	  
	  public void setMostrarBotonVolver(boolean mostrarBotonVolver) {
	    this.mostrarBotonVolver = mostrarBotonVolver;
	  }
	  
	  protected HtmlSelectOneMenu getSelectTipoDepuracion() {
	    if (this.selectTipoDepuracion == null)
	      this.selectTipoDepuracion = (HtmlSelectOneMenu)findComponentInRoot("selectTipoDepuracion"); 
	    return this.selectTipoDepuracion;
	  }
	  
	  protected HtmlInputText getIdUsuario() {
	    if (this.idUsuario == null)
	      this.idUsuario = (HtmlInputText)findComponentInRoot("idUsuario"); 
	    return this.idUsuario;
	  }
	  
	  protected HtmlInputText getNumTarjetaId() {
	    if (this.NumTarjetaId == null)
	      this.NumTarjetaId = (HtmlInputText)findComponentInRoot("NumTarjetaId"); 
	    return this.NumTarjetaId;
	  }
	  
	  protected CommandButton getBotonConsultar() {
	    if (this.botonConsultar == null)
	      this.botonConsultar = (CommandButton)findComponentInRoot("botonConsultar"); 
	    return this.botonConsultar;
	  }
	  
	  protected OutputPanel getConsultaInfoErr() {
	    if (this.consultaInfoErr == null)
	      this.consultaInfoErr = (OutputPanel)findComponentInRoot("consultaInfoErr"); 
	    return this.consultaInfoErr;
	  }
	  
	  protected OutputPanel getPanelTipoDepuracion() {
	    if (this.panelTipoDepuracion == null)
	      this.panelTipoDepuracion = (OutputPanel)findComponentInRoot("panelTipoDepuracion"); 
	    return this.panelTipoDepuracion;
	  }
	  
	  protected OutputPanel getPanelInfoErronea() {
	    if (this.panelInfoErronea == null)
	      this.panelInfoErronea = (OutputPanel)findComponentInRoot("panelInfoErronea"); 
	    return this.panelInfoErronea;
	  }
	  
	  protected OutputPanel getPanelUsuariosInactivos() {
	    if (this.panelUsuariosInactivos == null)
	      this.panelUsuariosInactivos = (OutputPanel)findComponentInRoot("panelUsuariosInactivos"); 
	    return this.panelUsuariosInactivos;
	  }
	  
	  protected CommandButton getBotonVolver() {
	    if (this.botonVolver == null)
	      this.botonVolver = (CommandButton)findComponentInRoot("botonVolver"); 
	    return this.botonVolver;
	  }
	  
	  protected OutputPanel getPanelBtnVolver() {
	    if (this.panelBtnVolver == null)
	      this.panelBtnVolver = (OutputPanel)findComponentInRoot("panelBtnVolver"); 
	    return this.panelBtnVolver;
	  }
	  
	  public int getNumUsuariosEliminar() {
	    return this.numUsuariosEliminar;
	  }
	  
	  public void setNumUsuariosEliminar(int numUsuariosEliminar) {
	    this.numUsuariosEliminar = numUsuariosEliminar;
	  }
	  
	  public String getCampoIdUsuario() {
	    return this.campoIdUsuario;
	  }
	  
	  public void setCampoIdUsuario(String campoIdUsuario) {
	    this.campoIdUsuario = campoIdUsuario;
	  }
	  
	  public String getCampoNumTarjera() {
	    return this.campoNumTarjera;
	  }
	  
	  public void setCampoNumTarjera(String campoNumTarjera) {
	    this.campoNumTarjera = campoNumTarjera;
	  }
	  
	  protected Calendar getFechaRegistro() {
	    if (this.FechaRegistro == null)
	      this.FechaRegistro = (Calendar)findComponentInRoot("FechaRegistro"); 
	    return this.FechaRegistro;
	  }
	  
	  public Date getCampoFechaRegistro() {
	    return this.campoFechaRegistro;
	  }
	  
	  public void setCampoFechaRegistro(Date campoFechaRegistro) {
	    this.campoFechaRegistro = campoFechaRegistro;
	  }
	  
	  public List<UsuarioInfoErrada> getListaUsuariosInfoErrada() {
	    return this.listaUsuariosInfoErrada;
	  }
	  
	  public void setListaUsuariosInfoErrada(List<UsuarioInfoErrada> listaUsuariosInfoErrada) {
	    this.listaUsuariosInfoErrada = listaUsuariosInfoErrada;
	  }
	  
	  public String getNumeroTarEliminar() {
	    return this.numeroTarEliminar;
	  }
	  
	  public void setNumeroTarEliminar(String numeroTarEliminar) {
	    this.numeroTarEliminar = numeroTarEliminar;
	  }
	  
	  public String getNumerotarEliminarEncrip() {
	    return this.numerotarEliminarEncrip;
	  }
	  
	  public void setNumerotarEliminarEncrip(String numerotarEliminarEncrip) {
	    this.numerotarEliminarEncrip = numerotarEliminarEncrip;
	  }
	  
	  public UsuarioInfoErrada getUsuarioInfoErrada() {
	    return this.usuarioInfoErrada;
	  }
	  
	  public void setUsuarioInfoErrada(UsuarioInfoErrada usuarioInfoErrada) {
	    this.usuarioInfoErrada = usuarioInfoErrada;
	  }
	  
	  public boolean isMostrarCausal() {
	    return this.mostrarCausal;
	  }
	  
	  public void setMostrarCausal(boolean mostrarCausal) {
	    this.mostrarCausal = mostrarCausal;
	  }
	  
	  public String getMensajeCausal() {
	    return this.mensajeCausal;
	  }
	  
	  public void setMensajeCausal(String mensajeCausal) {
	    this.mensajeCausal = mensajeCausal;
	  }
	  
	  protected ConfirmDialog getConfirmacionEliminar() {
	    if (this.confirmacionEliminar == null)
	      this.confirmacionEliminar = (ConfirmDialog)findComponentInRoot("confirmacionEliminar"); 
	    return this.confirmacionEliminar;
	  }
	  
	  public Integer getDiasInactividad() {
	    return this.diasInactividad;
	  }
	  
	  public void setDiasInactividad(Integer diasInactividad) {
	    this.diasInactividad = diasInactividad;
	  }
	  
	  public Boolean getBotonesSelec() {
	    return this.botonesSelec;
	  }
	  
	  public void setBotonesSelec(Boolean botonesSelec) {
	    this.botonesSelec = botonesSelec;
	  }
	  
	  public boolean isEliminar() {
	    return this.Eliminar;
	  }
	  
	  public void setEliminar(boolean eliminar) {
	    this.Eliminar = eliminar;
	  }
	  
	  public boolean isMostrarFiltroInactividad() {
	    return this.mostrarFiltroInactividad;
	  }
	  
	  public void setMostrarFiltroInactividad(boolean mostrarFiltroInactividad) {
	    this.mostrarFiltroInactividad = mostrarFiltroInactividad;
	  }
	  
	  public List<UsuarioInactivo> getListaUsuarios() {
	    return this.listaUsuarios;
	  }
	  
	  public void setListaUsuarios(List<UsuarioInactivo> listaUsuarios) {
	    this.listaUsuarios = listaUsuarios;
	  }
	  
	  public Integer getCorreoConfig() {
	    return this.correoConfig;
	  }
	  
	  public void setCorreoConfig(Integer correoConfig) {
	    this.correoConfig = correoConfig;
	  }

		public Integer getFilas() {
			return filas;
		}
	
		public void setFilas(Integer filas) {
			this.filas = filas;
		}
	
		public String getCorreosNotficar() {
			return correosNotficar;
		}
	
		public void setCorreosNotficar(String correosNotficar) {
			this.correosNotficar = correosNotficar;
		}
	
		public List<Parametro> getListaParametros() {
			return listaParametros;
		}
	
		public void setListaParametros(List<Parametro> listaParametros) {
			this.listaParametros = listaParametros;
		}
	
		public String getCorreoInicial() {
			return correoInicial;
		}
	
		public void setCorreoInicial(String correoInicial) {
			this.correoInicial = correoInicial;
		}
		
		public int getRowsPerPage() {
	      return rowsPerPage;
	    }
	
	    public void setRowsPerPage(int rows){
	      this.rowsPerPage = rows; 
	    }

		public String getDiasNotificasSelected() {
			return diasNotificasSelected;
		}

		public void setDiasNotificasSelected(String diasNotificasSelected) {
			this.diasNotificasSelected = diasNotificasSelected;
		}

		public String getDiasEliminarSelected() {
			return diasEliminarSelected;
		}

		public void setDiasEliminarSelected(String diasEliminarSelected) {
			this.diasEliminarSelected = diasEliminarSelected;
		}

		public List<UsuarioInactivo> getUsuariosEliminar() {
			return usuariosEliminar;
		}

		public void setUsuariosEliminar(List<UsuarioInactivo> usuariosEliminar) {
			this.usuariosEliminar = usuariosEliminar;
		}

		public List<UsuarioInactivo> getUsuariosNotificar() {
			return usuariosNotificar;
		}

		public void setUsuariosNotificar(List<UsuarioInactivo> usuariosNotificar) {
			this.usuariosNotificar = usuariosNotificar;
		}
		public String getParamInactividad() {
			return paramInactividad;
		}
	
		public void setParamInactividad(String paramInactividad) {
			this.paramInactividad = paramInactividad;
		}

	}
