package com.rbm.web.prepago.managedBeans;

import java.io.File;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.ibm.ams.audit.dto.EventSuccessEnum;
import com.ibm.ams.audit.dto.EventTypeEnum;
import com.ibm.ams.audit.dto.SeverityEnum;
import com.ibm.ams.correo.configuracion.Adjunto;
import com.ibm.ams.correo.configuracion.InformacionCorreoDTO;
import com.ibm.ams.correo.generadorcorreo.EmailSender;
import com.ibm.ams.ldap.gestores.GestorUsuarios;
import com.ibm.ams.log.Logger;
import com.rbm.web.prepago.configuracion.Configuracion;
import com.rbm.web.prepago.dto.Preguntas;
import com.rbm.web.prepago.dto.TipoDocumento;
import com.rbm.web.prepago.dto.Usuario;
import com.rbm.web.prepago.gestores.GestorAuditoria;
import com.rbm.web.prepago.gestores.GestorBD;
import com.rbm.web.prepago.gestores.GestorCache;
import com.rbm.web.prepago.gestores.GestorConfiguracionLDAP;
import com.rbm.web.prepago.pagecode.PageCodeBase;
import com.rbm.web.prepago.util.ConstantesWebPrepago;
import org.apache.commons.codec.binary.Hex;
import org.primefaces.context.RequestContext;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * @author JuanPabloRobayoCasti
 *
 */
public class ModificarInfoPersonalBean extends PageCodeBase {

	private static Logger logger = Logger.getInstance(ConstantesWebPrepago.NOMBRE_APLICACION, ConstantesWebPrepago.DIRECTORIO_CONFIGURACION);
	
	private String ip;
	private int tipoDocumentoSelected;
	private List<TipoDocumento> listaTipoDoc = new ArrayList<TipoDocumento>();
	private String numeroDocumento;
	private String nombres;
	private String apellidos;
	private String correoElectronico;
	private String confirmarCorreo;
	private String telefono;
	private int pregunta1Selected;
	private String respuestaUno;
	private int pregunta2Selected;
	private String respuestaDos;
	private int pregunta3;
	private String respuestaTres;
	private List<Preguntas> listaPreguntas1 = new ArrayList<Preguntas>();
	private List<Preguntas> listaPreguntas2= new ArrayList<Preguntas>();
	private List<Preguntas> listaPreguntas3= new ArrayList<Preguntas>();
	private List<Preguntas> preguntas = new ArrayList<Preguntas>();
	
	private boolean mostrarDialogo;
	private Principal usuarioLogin;
	private Configuracion conf = null;
	private String campoPass;
	private Usuario usuarioModificar = new Usuario();
	private String key = "Bar12345Bar12345"; // 128 bit key
	private String initVector = "RandomInitVector"; // 16 bytes IV
    private String initVector2 = "12345678901234567890123456789012"; // 16 bytes IV
    private String numeroTelefonoValidacion="/[0-9]+/i";
	
	
	public ModificarInfoPersonalBean() {
		FacesContext fc = FacesContext.getCurrentInstance();
		this.usuarioLogin = fc.getExternalContext().getUserPrincipal();
		this.mostrarDialogo= false;
		
		try {
			this.listaTipoDoc.addAll(GestorCache.getTipoDocs());
			this.preguntas.addAll(GestorCache.getPreguntas());
			for (int i = 0; i < preguntas.size(); i++) {
				if (preguntas.get(i).getLista()==1) {
					listaPreguntas1.add(preguntas.get(i));
				}else if (preguntas.get(i).getLista()==2){
					listaPreguntas2.add(preguntas.get(i));
				}else  if (preguntas.get(i).getLista()==3){
					listaPreguntas3.add(preguntas.get(i));
				}
				
			}
			this.conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			cargarPantalla(this.usuarioLogin);
			logger.info(this.getClass().getName(), "ModificarInfoPersonalBean", "Se carga la data exitosa");
			logger.info(this.getClass().getName(), "ModificarInfoPersonalBean", "******IP: "+this.ip);
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ConstantesWebPrepago.ASIGNAR_CONTRASENA_ME007,""));
			GestorAuditoria.getInstance().registrarAuditoria(SeverityEnum.ERROR, EventSuccessEnum.SUCCESS.name(), 
					EventTypeEnum.ERROR.value(),ConstantesWebPrepago.ASIGNAR_CONTRASENA_ME007,this.usuarioLogin.getName(),this.ip,"WebPrepago");
			logger.error(this.getClass().getName(), "ModificarInfoPersonalBean", "Error cargando información en pantalla", e);
		}
	}
	
	private void cargarPantalla(Principal usrLogin) {
		Usuario usuario = new Usuario();
		try {
			usuario = GestorBD.consultarUsuarioPorTipoUsuario(usrLogin.getName(), ConstantesWebPrepago.USUARIO_PREMANENTE, null);
			this.tipoDocumentoSelected = usuario.getTipoDocumento();
			this.numeroDocumento = usuario.getNumDocumento();
			this.nombres = usuario.getNombres();
			this.apellidos = usuario.getApellidos();
			this.correoElectronico = usuario.getCorreo();
			this.confirmarCorreo = usuario.getCorreo();
			this.telefono = usuario.getTelefono();
			this.pregunta1Selected = usuario.getPregunta1();
			this.respuestaUno = usuario.getRespuesta1().substring(0, 1)+"******";
			this.pregunta2Selected = usuario.getPregunta2();
			this.respuestaDos = usuario.getRespuesta2().substring(0, 1)+"******";
			this.pregunta3 = usuario.getPregunta3();
			this.respuestaTres = usuario.getRespuesta3().substring(0, 1)+"******";
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ConstantesWebPrepago.MODIFICAR_INFO_USR_ME001,""));
			GestorAuditoria.getInstance().registrarAuditoria(SeverityEnum.ERROR, EventSuccessEnum.SUCCESS.name(), 
					EventTypeEnum.ERROR.value(),ConstantesWebPrepago.MODIFICAR_INFO_USR_ME001,this.usuarioLogin.getName(),this.ip,"WebPrepago");
			logger.error(this.getClass().getName(), "cargarPantalla", "Error cargando información en pantalla", e);
		}
	}
	
	public void validarDatos() {
		RequestContext context = RequestContext.getCurrentInstance();
		if(validarCampos()) {
			context.execute("varDialogConfirmar.show();");
			logger.info(this.getClass().getName(), "validarDatos", "Validación de campos exitosa");
		}
	}
	
	public void confirmarModificar() {
		com.ibm.ams.ldap.dto.UsuarioModificarWP usuarioLDAP = new com.ibm.ams.ldap.dto.UsuarioModificarWP();
		try {
			GestorUsuarios gestorUsuarios = GestorUsuarios.getInstance(getConfigParamLdap(), getConfigMappingLdap());
			boolean claveActualValida = gestorUsuarios.autenticarUsuario(this.numeroDocumento, this.campoPass.toCharArray());
			Usuario usuario = GestorBD.consultarUsuarioPorTipoUsuario(this.usuarioLogin.getName(), ConstantesWebPrepago.USUARIO_PREMANENTE, null);
			if(claveActualValida) {
				this.usuarioModificar = new Usuario();
				this.usuarioModificar.setNombres(this.nombres.toUpperCase());
				this.usuarioModificar.setApellidos(this.apellidos.toUpperCase());
				this.usuarioModificar.setCorreo(this.correoElectronico);
				this.usuarioModificar.setTelefono(this.telefono);
				this.usuarioModificar.setPregunta1(this.pregunta1Selected);
				this.respuestaUno = (this.respuestaUno.contains("*"))?usuario.getRespuesta1():this.respuestaUno;
				String claveResp1 = new String(Hex.encodeHexString(encryptPreguntas(key, initVector2, this.respuestaUno.toUpperCase())));
				this.usuarioModificar.setRespuesta1(claveResp1);
				this.usuarioModificar.setPregunta2(this.pregunta2Selected);
				this.respuestaDos = (this.respuestaDos.contains("*"))?usuario.getRespuesta1():this.respuestaDos;
				String claveResp2 = new String(Hex.encodeHexString(encryptPreguntas(key, initVector2, this.respuestaDos.toUpperCase())));
				this.usuarioModificar.setRespuesta2(claveResp2);
				this.usuarioModificar.setPregunta3(this.pregunta3);
				this.respuestaTres = (this.respuestaTres.contains("*"))?usuario.getRespuesta1():this.respuestaTres;
				String claveResp3 = new String(Hex.encodeHexString(encryptPreguntas(key, initVector2, this.respuestaTres.toUpperCase())));
				this.usuarioModificar.setRespuesta3(claveResp3);
				this.usuarioModificar.setNumDocumento(this.numeroDocumento);
				
				GestorBD.actualizarInfoPersonal(this.usuarioModificar);
				
				usuarioLDAP.setLogin(this.numeroDocumento);
				usuarioLDAP.setNombres(this.usuarioModificar.getNombres().replaceAll(" +", " ").trim() + " " + this.usuarioModificar.getApellidos().replaceAll(" +", " ").trim());
				usuarioLDAP.setTelefono(String.valueOf(this.usuarioModificar.getTelefono()));
				usuarioLDAP.setCorreo(this.usuarioModificar.getCorreo());
				
				gestorUsuarios.modificarUsuarioWP(usuarioLDAP);
				enviarCorreo(this.usuarioModificar.getCorreo());
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_MI001,""));
				this.campoPass=null;
				/**Registro de auditoria del cambio de información*/
				GestorAuditoria.getInstance().registrarAuditoria(SeverityEnum.INFORMATIONAL, EventSuccessEnum.SUCCESS.name(), 
						EventTypeEnum.ERROR.value(),ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_MI001+", de la modificación de información personal, para el usuario: "+
						this.numeroDocumento,this.usuarioLogin.getName(),this.ip,"WebPrepago");
				
			}else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ConstantesWebPrepago.ASIGNAR_CONTRASENA_ME001,""));
				logger.info(this.getClass().getName(), "confirmarModificar", "la contraseña no coincide");
			}
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ConstantesWebPrepago.MODIFICAR_INFO_USR_ME002,""));
			GestorAuditoria.getInstance().registrarAuditoria(SeverityEnum.ERROR, EventSuccessEnum.SUCCESS.name(), 
					EventTypeEnum.ERROR.value(),ConstantesWebPrepago.MODIFICAR_INFO_USR_ME002,this.usuarioLogin.getName(),this.ip,"WebPrepago");
			logger.error(this.getClass().getName(), "confirmarModificar", "Error modificando la información del usuario.",e);
		}
	}
	
	public static byte[] encryptPreguntas(String key, String initVector, String value) {
		try {
			IvParameterSpec iv = new IvParameterSpec(Hex.decodeHex(initVector.toCharArray()));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

			byte[] encrypted = cipher.doFinal(value.getBytes("UTF-8"));
			return encrypted;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}
	
	
	public void enviarCorreo(String correoUsr) {
		java.util.Date fecha = new Date();
		SimpleDateFormat format1 = new SimpleDateFormat(ConstantesWebPrepago.FORMATO_FECHA_XLS);
		try {	
			Map<String, Object> informacionCorreo = new HashMap<String, Object>();
			informacionCorreo.put("usr", this.numeroDocumento);
			informacionCorreo.put("fecha", format1.format(fecha));
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
			remitentes.add(correoUsr);
	
			correo.setInformacionComercio(informacionCorreo);
			correo.setTo(remitentes);
			correo.setCc(null);
			correo.setBcc(null);
			correo.setAdjuntos(adjuntos);
									
			EmailSender sender = new EmailSender();
			sender.enviarCorreo(correo, ConstantesWebPrepago.PLANTILLA_CORREO_MODIFICAR_INFO_PERSONAL);
			logger.info(this.getClass().getName(), "enviarCorreo", "Correo electrónico  enviado exitosamente");
		} catch (Exception e) {
			logger.error(this.getClass().getName(), "enviarCorreo", "Error enviando correo", e);
			GestorAuditoria.getInstance().registrarAuditoria(SeverityEnum.ERROR, EventSuccessEnum.SUCCESS.name(), 
					EventTypeEnum.ERROR.value(),"Error enviando correo","ModificarInfoPersonal-enviarCorreo",this.ip,"WebPrepago");
		}
	}
	
	public boolean validarCampos() {
		boolean validar = true;
		boolean valido = true;
		String mensaje="";
		Pattern patronEmail = Pattern.compile("^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@" +
		  	      "[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4})$");
		if (this.tipoDocumentoSelected == 0) {
			mensaje +=" Tipo de documento,";
			validar = false;
		}
		if (this.numeroDocumento==null) {
			mensaje +=" Número de documento,";
			validar = false;
		}
		if(this.nombres==null || nombres.isEmpty()){
			mensaje +=" Nombres,";
			validar=false;
		}
		if (this.apellidos==null || apellidos.isEmpty()) {
			mensaje +=" Apellidos,";
			validar = false;
		}
		if (this.correoElectronico==null || correoElectronico.isEmpty()) {
			mensaje +=" Correo Electronico,";
			validar = false;
		}else { 
			 Matcher mEmail = patronEmail.matcher(this.correoElectronico.toLowerCase());
	        if (!mEmail.matches()){
	        	validar = valido = false;
	        	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ConstantesWebPrepago.REGISTRAR_USUARIO_ME003_2,this.correoElectronico));
	        }
		}
		if (this.confirmarCorreo==null || confirmarCorreo.isEmpty()) {
			mensaje +=" Confirmar correo electronico,";
			validar = false;
		}else {
			Matcher mEmail = patronEmail.matcher(this.confirmarCorreo.toLowerCase());
	        if (!mEmail.matches()){
	        	validar = valido = false;
	        	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ConstantesWebPrepago.REGISTRAR_USUARIO_ME003_2,this.confirmarCorreo));
	        }else {
	        	if(this.correoElectronico!=null && !correoElectronico.isEmpty() && valido) {
					if(!this.confirmarCorreo.equals(this.correoElectronico)) {
						validar = valido = false;
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ConstantesWebPrepago.REGISTRAR_USUARIO_ME005,""));
					}
		        }
			}
		}
		if (this.telefono==null || this.telefono.isEmpty()){
			mensaje +=" Telefono,";
			validar = false;
		}else if(this.telefono.length()!=10 || this.telefono.charAt(0)!='3') {
			validar = valido = false;
        	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ConstantesWebPrepago.REGISTRAR_USUARIO_ME003_3 + this.telefono, this.telefono));
			}
		if (this.pregunta1Selected==0) {
			mensaje +=" Pregunta 1,";
			validar = false;
		}
		if (this.respuestaUno==null || respuestaUno.isEmpty()) {
			mensaje +=" Respuesta 1,";
			validar = false;
		}
		if (this.pregunta2Selected==0) {
			mensaje +=" Pregunta 2,";
			validar = false;
		}
		if (this.respuestaDos==null || respuestaDos.isEmpty()) {
			mensaje +=" Respuesta 2,";
			validar = false;
		}
		if (this.pregunta3==0) {
			mensaje +=" Pregunta 3,";
			validar = false;
		}
		if (this.respuestaTres==null || respuestaTres.isEmpty()) {
			mensaje +=" Respuesta 3,";
			validar = false;
		}
		if(!mensaje.isEmpty()){
			mensaje=mensaje.substring(0, mensaje.length()-1);
		}
		if(!validar && valido){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ConstantesWebPrepago.REGISTRAR_USUARIO_ME003+ mensaje,""));
		}
		
		return validar;
	}
	
	
	public void cancelarModificarInfo() {
		cargarPantalla(this.usuarioLogin);
	}
	

	public String getIp() {
		return ip;
	}


	public void setIp(String ip) {
		this.ip = ip;
	}


	public int getTipoDocumentoSelected() {
		return tipoDocumentoSelected;
	}


	public void setTipoDocumentoSelected(int tipoDocumentoSelected) {
		this.tipoDocumentoSelected = tipoDocumentoSelected;
	}


	public List<TipoDocumento> getListaTipoDoc() {
		return listaTipoDoc;
	}


	public void setListaTipoDoc(List<TipoDocumento> listaTipoDoc) {
		this.listaTipoDoc = listaTipoDoc;
	}


	public String getNumeroDocumento() {
		return numeroDocumento;
	}


	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}


	public String getNombres() {
		return nombres;
	}


	public void setNombres(String nombres) {
		this.nombres = nombres;
	}


	public String getApellidos() {
		return apellidos;
	}


	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}


	public String getCorreoElectronico() {
		return correoElectronico;
	}


	public void setCorreoElectronico(String correoElectronico) {
		this.correoElectronico = correoElectronico;
	}


	public String getConfirmarCorreo() {
		return confirmarCorreo;
	}


	public void setConfirmarCorreo(String confirmarCorreo) {
		this.confirmarCorreo = confirmarCorreo;
	}


	public String getTelefono() {
		return telefono;
	}


	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}


	public int getPregunta1Selected() {
		return pregunta1Selected;
	}


	public void setPregunta1Selected(int pregunta1Selected) {
		this.pregunta1Selected = pregunta1Selected;
	}


	public List<Preguntas> getListaPreguntas1() {
		return listaPreguntas1;
	}


	public void setListaPreguntas1(List<Preguntas> listaPreguntas1) {
		this.listaPreguntas1 = listaPreguntas1;
	}


	public String getRespuestaUno() {
		return respuestaUno;
	}


	public void setRespuestaUno(String respuestaUno) {
		this.respuestaUno = respuestaUno;
	}


	public int getPregunta2Selected() {
		return pregunta2Selected;
	}


	public void setPregunta2Selected(int pregunta2Selected) {
		this.pregunta2Selected = pregunta2Selected;
	}


	public String getRespuestaDos() {
		return respuestaDos;
	}


	public void setRespuestaDos(String respuestaDos) {
		this.respuestaDos = respuestaDos;
	}


	public int getPregunta3() {
		return pregunta3;
	}


	public void setPregunta3(int pregunta3) {
		this.pregunta3 = pregunta3;
	}


	public String getRespuestaTres() {
		return respuestaTres;
	}


	public void setRespuestaTres(String respuestaTres) {
		this.respuestaTres = respuestaTres;
	}


	public List<Preguntas> getListaPreguntas2() {
		return listaPreguntas2;
	}


	public void setListaPreguntas2(List<Preguntas> listaPreguntas2) {
		this.listaPreguntas2 = listaPreguntas2;
	}


	public List<Preguntas> getListaPreguntas3() {
		return listaPreguntas3;
	}


	public void setListaPreguntas3(List<Preguntas> listaPreguntas3) {
		this.listaPreguntas3 = listaPreguntas3;
	}


	public boolean isMostrarDialogo() {
		return mostrarDialogo;
	}


	public void setMostrarDialogo(boolean mostrarDialogo) {
		this.mostrarDialogo = mostrarDialogo;
	}



	public Principal getUsuarioLogin() {
		return usuarioLogin;
	}



	public void setUsuarioLogin(Principal usuarioLogin) {
		this.usuarioLogin = usuarioLogin;
	}



	public List<Preguntas> getPreguntas() {
		return preguntas;
	}



	public void setPreguntas(List<Preguntas> preguntas) {
		this.preguntas = preguntas;
	}



	public Configuracion getConf() {
		return conf;
	}



	public void setConf(Configuracion conf) {
		this.conf = conf;
	}

	public String getCampoPass() {
		return campoPass;
	}

	public void setCampoPass(String campoPass) {
		this.campoPass = campoPass;
	}

	public Usuario getUsuarioModificar() {
		return usuarioModificar;
	}

	public void setUsuarioModificar(Usuario usuarioModificar) {
		this.usuarioModificar = usuarioModificar;
	}

	public String getNumeroTelefonoValidacion() {
		return numeroTelefonoValidacion;
	}

	public void setNumeroTelefonoValidacion(String numeroTelefonoValidacion) {
		this.numeroTelefonoValidacion = numeroTelefonoValidacion;
	}

}