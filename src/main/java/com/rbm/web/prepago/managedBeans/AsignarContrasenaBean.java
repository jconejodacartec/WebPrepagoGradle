/**
 * 
 */
package com.rbm.web.prepago.managedBeans;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.component.html.HtmlInputSecret;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;
//import javax.xml.bind.annotation.XmlElement;

import org.primefaces.component.calendar.Calendar;
import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.component.dialog.Dialog;
import org.primefaces.component.graphicimage.GraphicImage;
import org.primefaces.component.messages.Messages;
import org.primefaces.component.panel.Panel;
import org.primefaces.component.panelgrid.PanelGrid;
import org.primefaces.context.RequestContext;

import com.ibm.ams.correo.configuracion.Adjunto;
import com.ibm.ams.correo.configuracion.InformacionCorreoDTO;
import com.ibm.ams.correo.generadorcorreo.EmailSender;
import com.ibm.ams.ldap.dto.Usuario;
import com.ibm.ams.ldap.gestores.GestorUsuarios;
import com.ibm.ams.log.Logger;
import com.rbm.web.prepago.configuracion.Configuracion;
import com.rbm.web.prepago.dto.Preguntas;
import com.rbm.web.prepago.exception.ExceptionWebPrepago;
import com.rbm.web.prepago.gestores.GestorBD;
import com.rbm.web.prepago.gestores.GestorCache;
import com.rbm.web.prepago.gestores.GestorConfiguracionLDAP;
import com.rbm.web.prepago.pagecode.PageCodeBase;
import com.rbm.web.prepago.util.ConstantesWebPrepago;

import nl.captcha.Captcha;

/**
 * @author jmcastel
 *
 */
@ManagedBean
@SessionScoped
public class AsignarContrasenaBean extends PageCodeBase implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Panel PanelRegistro;
	protected Calendar CalfechaRegistro;
	protected Panel PanelUsuario;
	protected Panel PanelValidacionPreguntas;
	protected Panel PanelAsignarContraseña;
	protected HtmlInputSecret ConfnuevaContrasenaId;
	protected HtmlInputSecret ConfnuevaContrasenaIdR;
	private String ip;
	private String id;
	private boolean formRender;
	private long tiempo=1491867497350L;
	protected CommandButton BotonCrear;
	private String abreviatura;


	//	private ConfiguracionParametrosLdap configParamLdap = new ConfiguracionParametrosLdap();
	//	private ConfiguracionMapeoLdap configMappingLdap = new ConfiguracionMapeoLdap();
	protected Panel PanelComplementoRegistro;
	protected Panel PanleOlvidoContrasena;
	protected HtmlInputSecret ConfnuevaContrasenaIdRecuperar;
	protected HtmlForm formParametro;
	protected Panel PanelOlvidoContrasena;



	private Usuario usuarioLDAP;
	private com.rbm.web.prepago.dto.Usuario usuarioDB;
	private com.rbm.web.prepago.dto.Usuario usuarioLink;
	private String numeroDocumento;
	private String contrasena;
	private String confirmacionContrasena;
	private String numeroDocumentoSolicitar;
	private String numeroDocumentoRecuperar;
	private String contrasenaRecuperar;
	private String confirmacionContrasenaRecuperar;


	private String preg1;
	private String preg2;
	private String preg3;
	private String resp1;
	private String resp2;
	private String resp3;

	private boolean mostrarPreguntas;
	private boolean mostrarPanelSolicitar;
	private boolean mostrarPanelOlvido;
	private boolean mostrarPanelComplemento;
	private String valorCaptcha1;
	private String valorCaptcha2;
	private String valorCaptcha3;
	private String captchaValue;
	private static Captcha captcha;

	protected Panel PanelSolicitarContrasena;
	private List<Preguntas> preguntas;
	/** Clase logger para gestionar los menajes de logs. */
	private static Logger logger = Logger.getInstance(ConstantesWebPrepago.NOMBRE_APLICACION, ConstantesWebPrepago.DIRECTORIO_CONFIGURACION);
	private Configuracion conf = null;
	private String urlLogin;
	private String publickey;
	protected CommandButton Aceptar;
	protected CommandButton paramBack;
	protected HtmlInputText IDValue;
	protected Messages mensajesInner;
	protected HtmlInputText numDocumento;
	protected HtmlInputSecret nuevaContrasenaIdR;
	protected CommandButton botonAsignar;
	protected HtmlInputText numDocumentoSolicitar;
	protected CommandButton botonSolicitar;
	protected HtmlInputText pregunta1;
	protected HtmlInputText respuesta1;
	protected HtmlInputText pregunta2;
	protected HtmlInputText respuesta2;
	protected HtmlInputText pregunta3;
	protected HtmlInputText respuesta3;
	protected CommandButton botonEnviar;
	protected HtmlOutputText numDocumentoRecuperar;
	protected HtmlInputSecret nuevaContrasenaIdRecuperar;
	protected CommandButton botonCambioContrasena;
	protected PanelGrid displayConfCrearContra;
	protected CommandButton idAceptarCreacion;
	protected PanelGrid displayConfirmacion;
	protected CommandButton idAceptar;
	protected HtmlForm asignarContrasenaForm;
	protected HtmlInputHidden IPValue;
	protected CommandButton botonCancelar;
	protected CommandButton botonCancelar1;
	protected HtmlOutputText idUsuarioRecuperar;
	protected CommandButton botonCancelarPreguntas;
	protected CommandButton botonCancelar2;
	protected Dialog dialogoConfCrearContra;
	protected Dialog dialogoConfirmacion;
	protected HtmlPanelGrid captcha3;
	protected HtmlPanelGrid captcha2;
	protected GraphicImage idCaptcha1;
	protected GraphicImage idCaptcha2;
	protected GraphicImage idCaptcha3;
	public AsignarContrasenaBean(){
		try {
		this.setCaptchaValue("/customCaptchaImg?view=asignarPass");
		this.usuarioLDAP = new Usuario();
		this.usuarioDB = new com.rbm.web.prepago.dto.Usuario();
		this.usuarioLink = new com.rbm.web.prepago.dto.Usuario();
		this.preguntas = new ArrayList<Preguntas>();
		conf = GestorConfiguracionLDAP.obtenerConfiguracion();
		//setPublickey(conf.getPublicKeyRecaptcha());
		this.urlLogin = conf.getUrlLogin();
		this.preguntas = GestorCache.getPreguntas();
		if(id==null){
			id=FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
		}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ConstantesWebPrepago.ASIGNAR_CONTRASENA_ME007, ""));
		}
	}
	
	public void init() {
		try {
			if(id==null){
				id=FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
			}
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			if(id==null){
				id=request.getParameter("id");
			}
			
			formRender=true;
			if (this.id==null && this.preg1==null) {
				this.mostrarPreguntas = false;
				this.mostrarPanelOlvido = false;
				this.mostrarPanelComplemento = false;
				this.mostrarPanelSolicitar = true;
			}else if(this.id!=null){
				existeTimeout();
				this.numeroDocumento="";
				this.mostrarPreguntas = false;
				this.mostrarPanelOlvido = false;
				this.mostrarPanelComplemento = true;
				this.mostrarPanelSolicitar = false;
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
			formRender=false;
			logger.error(this.getClass().getName(), "init", "Error en el init", e);
			FacesContext.getCurrentInstance().getExternalContext().invalidateSession();

		}
	}

	/**
	 * Existe timeout.
	 * @return true, if successful
	 * @throws ValidacionCamposException
	 */
	
	public void existeTimeout() throws ExceptionWebPrepago {
		
		try{
			this.usuarioLink = GestorBD.consultarUsuario(this.id,null);
			long total = System.currentTimeMillis() - this.usuarioLink.getFechaCreacion().getTime();
	
			long horas =(8*3600*1000) ;
			if (total >horas || this.usuarioLink==null){
				throw new ExceptionWebPrepago(ConstantesWebPrepago.ASIGNAR_CONTRASENA_MI002);
			}
		}catch(Exception e){
			throw new ExceptionWebPrepago(ConstantesWebPrepago.ASIGNAR_CONTRASENA_MI002);
		}
	}


	/**
	 * Registra un usuario en el LDAP la primera vez que va a asignar su contraseña
	 * @throws Exception 
	 */
	public void registrarUsuario () throws Exception{
		
		RequestContext context = RequestContext.getCurrentInstance();

		String ruta = ConstantesWebPrepago.PATH_NEW_USER_PERSONAS;

		abreviatura=conf.getAbreviatura();

		if (validarCamposAsignarContrasena() && validarCaptcha(this.valorCaptcha1)) {
			if (this.contrasena.equals(this.confirmacionContrasena)) {
				try {
					GestorUsuarios gestorUsuarios = GestorUsuarios.getInstance(getConfigParamLdap(), getConfigMappingLdap());
					Usuario usuario = null;
					usuario = gestorUsuarios.consultarUsuario(this.numeroDocumento) ;
					if(usuario !=null){
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,ConstantesWebPrepago.USUARIO_EXISTE_EN_LDAP, null));
						return;
					}
					
					com.rbm.web.prepago.dto.Usuario usuarioConsulta = GestorBD.consultarUsuarioPorTipoUsuario(this.numeroDocumento, ConstantesWebPrepago.USUARIO_TEMPORAL,null);
					if (usuarioConsulta == null || !usuarioConsulta.getNumDocumento().equals(usuarioLink.getNumDocumento())) {
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,ConstantesWebPrepago.ASIGNAR_CONTRASENA_ME004, null));
					}else{

						this.usuarioLDAP.setLogin(this.numeroDocumento);
//						this.usuarioLDAP.setPassword(Encriptador.encriptar(this.contrasena));
//						this.usuarioLDAP.setPasswordConfirm(Encriptador.encriptar(this.confirmacionContrasena));
						this.usuarioLDAP.setPassword(this.contrasena);
						this.usuarioLDAP.setPasswordConfirm(this.confirmacionContrasena);
//						this.usuarioLDAP.setApellidos(this.usuarioLink.getApellidos());
						
						this.usuarioLDAP.setNombres(this.usuarioLink.getNombres().replaceAll(" +", " ").trim() + " " + this.usuarioLink.getApellidos().replaceAll(" +", " ").trim());
						this.usuarioLDAP.setCorreo(this.usuarioLink.getCorreo());
						if(String.valueOf(this.usuarioLink.getTelefono()).equals("") ||
								this.usuarioLink.getTelefono()==null || this.usuarioLink.getTelefono().isEmpty()) {
							this.usuarioLDAP.setTelefono(null);
						}else {
							this.usuarioLDAP.setTelefono(String.valueOf(this.usuarioLink.getTelefono()));
						}
						this.usuarioLDAP.setApellidos(abreviatura);
						this.usuarioLDAP.setNombreComercio(this.numeroDocumento);
						
//						String RutaGrupos = ConstantesWebPrepago.PATH_SEARCH_GROUP;
						String RutaGrupos = conf.getGrupo();
						List<String> grupos = new ArrayList<String>();
						grupos.add(RutaGrupos);
						this.usuarioLDAP.setGrupos(grupos);
						//this.usuarioLDAP.setEncriptado("true");
						
						gestorUsuarios.crearUsuarioNuevo(this.usuarioLDAP, ruta);
						logger.info(this.getClass().getName(), "RegistrarUsuario", "Se creal el usuario exitosamente en el LDAP");
						
						GestorBD.ActualizarUltimaConexion(this.numeroDocumento);
						logger.info(this.getClass().getName(), "RegistrarUsuario", "Se actualiza conexion en BD");
						for (String grupoDn : this.usuarioLDAP.getGrupos()) {

							String[] split = grupoDn.split(",");
							gestorUsuarios.asociarGrupoWP(this.usuarioLDAP.getLogin(),
									split[0].substring(3, split[0].length()), this.usuarioLDAP.getTipoCliente(), RutaGrupos);
							logger.debug( "registrarUsuario","Asosia grupo en la ruta: %s", RutaGrupos);
						}

						gestorUsuarios.modificar(this.usuarioLDAP);
						logger.info(this.getClass().getName(), "RegistrarUsuario", "Actualizacion de usuario en LDAP");
						GestorBD.cambiarUsuarioAPermanente(this.usuarioLDAP.getLogin(), ConstantesWebPrepago.USUARIO_PREMANENTE);
						enviarCorreo(usuarioConsulta);
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ConstantesWebPrepago.ASIGNAR_CONTRASENA_MI001, ""));
						limpiarCampos();
						context.execute("dialogoConfCrearContra.show()");
					}
				} catch (Exception e) {
					e.getStackTrace();
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ConstantesWebPrepago.ASIGNAR_CONTRASENA_ME007, ""));
					logger.error(this.getClass().getName(), "RegistrarUsuario", "Error registrando usuario", e);

				}
			}else{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ConstantesWebPrepago.ASIGNAR_CONTRASENA_ME001, ""));
				throw new Exception();
			}
		}
		this.valorCaptcha1="";
	}


	private boolean validarCaptcha(String valorCap) {
		boolean captchaPassed = false;
		try {
			if(captcha != null) {
				captchaPassed = captcha.isCorrect(valorCap);
			}
			if(!captchaPassed){
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo Captcha invalido", ""));
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ConstantesWebPrepago.MSG_ERROR_CAPTCHA, ""));
			logger.info(this.getClass().getName(), "registrarUsuario", "error validando el captcha ");
		}
		return captchaPassed;
	}



	/**
	 * Valida que el usuario exista en el LDAP y asigna las preguntas que estan en la BD a la pantalla de preguntas
	 * Luego, esconde el panel de olvido contraseña y muestra el panel de preguntas
	 */
	@SuppressWarnings("unused")
	public void solicitarCambioContrasena(){

		GestorUsuarios gestorUsuarios = GestorUsuarios.getInstance(getConfigParamLdap(), getConfigMappingLdap());
		String ruta = ConstantesWebPrepago.PATH_NEW_USER_PERSONAS;
		RequestContext context = RequestContext.getCurrentInstance();
		FacesContext fc = FacesContext.getCurrentInstance();
		Map<String,String> params = fc.getExternalContext().getRequestParameterMap();

		if (!this.numeroDocumentoSolicitar.isEmpty() && this.numeroDocumentoSolicitar!=null && !this.valorCaptcha2.isEmpty() && this.valorCaptcha2 != null) {//valida captcha
			if(validarCaptcha(this.valorCaptcha2)){
				try {
					usuarioDB = GestorBD.consultarUsuarioPorTipoUsuario(this.numeroDocumentoSolicitar, ConstantesWebPrepago.USUARIO_PREMANENTE,null);

				if (usuarioDB == null) {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,ConstantesWebPrepago.ASIGNAR_CONTRASENA_ME004, null));
				}
				else{

					this.usuarioLDAP.setLogin(this.numeroDocumentoRecuperar);
					this.usuarioLDAP.setPassword(this.contrasenaRecuperar);
					this.usuarioLDAP.setPasswordConfirm(confirmacionContrasenaRecuperar);

					//Se asignan las preguntas a la pantalla
					for (int i = 0; i < preguntas.size(); i++) {
						if(preguntas.get(i).getId()==usuarioDB.getPregunta1()){
							this.preg1=preguntas.get(i).getPregunta();
						}else if(preguntas.get(i).getId()==usuarioDB.getPregunta2()){
							this.preg2=preguntas.get(i).getPregunta();
						} else if(preguntas.get(i).getId()==usuarioDB.getPregunta3()){
							this.preg3=preguntas.get(i).getPregunta();
						}
					}
					
					

					this.mostrarPanelSolicitar= false;
					this.mostrarPanelOlvido = false;
					this.mostrarPreguntas = true;
					
					this.numeroDocumentoRecuperar= this.numeroDocumentoSolicitar;
					
					this.resp1="";
					this.resp2="";
					this.resp3="";

					context.update(":asignarContrasenaForm:PanelSolicitarContrasena");
					context.update("PanelSolicitarContrasena");
					context.update("PanelValidacionPreguntas");
					context.update(":asignarContrasenaForm:PanelValidacionPreguntas");
					

					}
				} catch (ExceptionWebPrepago e) {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ConstantesWebPrepago.ASIGNAR_CONTRASENA_ME007, ""));
					logger.error(this.getClass().getName(), "SolicitarCambioContraseña", "Error al cambiar la contraseña: ", e);
				} catch (SQLException e) {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ConstantesWebPrepago.ASIGNAR_CONTRASENA_ME007, ""));
					logger.error(this.getClass().getName(), "SolicitarCambioContraseña", "Error en la BD: ", e);
				}
			}
		} else {
			String mensaje="";
			if(this.numeroDocumentoSolicitar==null || this.numeroDocumentoSolicitar.isEmpty()){
				mensaje+=" Usuario,";
			}
			if(this.valorCaptcha2==null || this.valorCaptcha2.isEmpty()){
				mensaje+=" Captcha";
			}
			if(!mensaje.isEmpty()){
				mensaje=mensaje.substring(0, mensaje.length()-1);
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ConstantesWebPrepago.ASIGNAR_CONTRASENA_ME003+mensaje, ""));
			
		}
		this.valorCaptcha2 ="";
		RequestContext.getCurrentInstance().update("idCaptcha");
		FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("asignarContrasenaForm:idCaptcha");

	}



 public void limpiarCampos(){
	this.preg1 =null;
	this.preg2=null;
	this.preg3=null;
	this.id=null;
	this.numeroDocumento =null;
	this.numeroDocumentoRecuperar=null;
	this.numeroDocumentoSolicitar=null;
	this.confirmacionContrasena=null;
	this.confirmacionContrasenaRecuperar=null;
	this.contrasena=null;
 }



	/**
	 * Cambia la contraseña de un usuario permanente
	 */
	public void cambiarContrasena (){

		try {
			usuarioDB = GestorBD.consultarUsuarioPorTipoUsuario(this.numeroDocumentoRecuperar, ConstantesWebPrepago.USUARIO_PREMANENTE,null);

			if(validarPreguntas()){
				if (validarCamposOlvidoContrasena() && validarCaptcha(this.valorCaptcha3)) {
					if (this.contrasenaRecuperar.equals(this.confirmacionContrasenaRecuperar)) {
						GestorUsuarios gestorUsuarios = GestorUsuarios.getInstance(getConfigParamLdap(), getConfigMappingLdap());
						try {
							Usuario usuarioConsulta = gestorUsuarios.consultarUsuario(this.numeroDocumentoRecuperar);
							if (usuarioConsulta == null) {
								FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,ConstantesWebPrepago.ASIGNAR_CONTRASENA_ME004, null));
							}else{
								
								this.usuarioLDAP.setLogin(this.numeroDocumentoRecuperar);
//								this.usuarioLDAP.setEncriptado("true");
//								this.usuarioLDAP.setPassword(Encriptador.encriptar(this.contrasenaRecuperar));
//								this.usuarioLDAP.setPasswordConfirm(Encriptador.encriptar(this.confirmacionContrasenaRecuperar));
								this.usuarioLDAP.setPassword(this.contrasenaRecuperar);
								this.usuarioLDAP.setPasswordConfirm(this.confirmacionContrasenaRecuperar);

								gestorUsuarios.cambiarPassword(usuarioLDAP.getLogin(), usuarioLDAP.getPassword());
								FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ConstantesWebPrepago.ASIGNAR_CONTRASENA_MI003, ""));
								limpiarCampos();
								RequestContext.getCurrentInstance().execute("window.location='"+this.urlLogin+"'");

							}
						} catch (Exception e) {
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ConstantesWebPrepago.ASIGNAR_CONTRASENA_ME007, ""));
							logger.error(this.getClass().getName(), "CambiarContrasena", "Error cambiando la contraseña: ", e);
						}
					}else{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ConstantesWebPrepago.ASIGNAR_CONTRASENA_ME001, ""));
					}
				}
			}

		} catch (ExceptionWebPrepago e1) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ConstantesWebPrepago.ASIGNAR_CONTRASENA_ME007, ""));
			logger.error(this.getClass().getName(), "CambiarContrasena", "Error cambiando contraseña, excepcion WebPrepago:", e1);
		} catch (SQLException e1) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ConstantesWebPrepago.ASIGNAR_CONTRASENA_ME007, ""));
			logger.error(this.getClass().getName(), "CambiarContrasena", "Error en la BD:", e1);
		}

		this.valorCaptcha3="";

	}






	/**
	 * Valida que las respuestas ingresadas por el usuario sean iguales a las respuestas que estan en la base de datos
	 * @return
	 */
	public boolean validarPreguntas() {

		String respuesta1 = usuarioDB.getRespuesta1();
		String respuesta2 = usuarioDB.getRespuesta2();
		String respuesta3 = usuarioDB.getRespuesta3();
		
		boolean campos =validarCamposRespuesta();
			if(respuesta1.equals(this.resp1.toUpperCase()) && respuesta2.equals(this.resp2.toUpperCase()) && respuesta3.equals(this.resp3.toUpperCase()) && campos){
				this.mostrarPanelOlvido = true;
				this.mostrarPreguntas = false;
				this.numeroDocumentoRecuperar= this.numeroDocumentoSolicitar;
				return true;
			}else{

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ConstantesWebPrepago.ASIGNAR_CONTRASENA_ME005,""));

			return false;
			}

	}



	private boolean validarCamposRespuesta() {
		boolean validar = true;
		String mensaje="";
		
		if (this.resp1.isEmpty() || this.resp1==null) {
			mensaje+="Respuesta 1, ";
			validar=false;
		}
		if (this.resp2.isEmpty() || this.resp2==null) {
			mensaje+="Respuesta 2, ";
			validar=false;
		}
		if (this.resp3.isEmpty() || this.resp3==null) {
			mensaje+="Respuesta 3, ";
			validar=false;
		}
		if (!validar) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ConstantesWebPrepago.ASIGNAR_CONTRASENA_ME003+" "+mensaje,""));
		}
		return validar;
	}



	public void submit(ActionEvent event) {
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correct", "Correct");

		FacesContext.getCurrentInstance().addMessage(null, msg);
	}



	public boolean validarCamposAsignarContrasena() {
		boolean validar = true;
		String mensaje="";		
		
		if (this.numeroDocumento == null || numeroDocumento.isEmpty()) {
			mensaje +=" Numero de documento,";
			validar = false;
		}
		if (this.contrasena == null || contrasena.isEmpty()) {
			mensaje +=" Contraseña,";
			validar = false;
		}
		if(this.confirmacionContrasena == null || confirmacionContrasena.isEmpty()){
			mensaje +=" Confirmacion de contraseña,";
			validar=false;
		}
		if(this.valorCaptcha1== null || this.valorCaptcha1.isEmpty()){
			mensaje +=" Captcha";
			validar=false;
		}
		
		if(!mensaje.isEmpty()){
			mensaje=mensaje.substring(0, mensaje.length()-1);
		}
		if(!validar){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ConstantesWebPrepago.ASIGNAR_CONTRASENA_ME003+ mensaje,""));
		}
		else{
//			if(this.numeroDocumento.length() > 12){
//				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ConstantesWebPrepago.ASIGNAR_CONTRASENA_ME003+ mensaje,""));
//				validar = false;
//			}
			if(this.contrasena.length() > 16 || this.contrasena.length() < 8){
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ConstantesWebPrepago.ASIGNAR_CONTRASENA_ME002+ mensaje,""));
				validar = false;
			}else if(!contrasena.matches(ConstantesWebPrepago.EXP_VALIDAR_CLAVE)){
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ConstantesWebPrepago.ASIGNAR_CONTRASENA_ME002+ mensaje,""));
				validar = false;
			}
		}
		return validar;
	}


	public boolean validarCamposOlvidoContrasena() {
		boolean validar = true;
		String mensaje="";		
		
		if (this.numeroDocumentoRecuperar == null || numeroDocumentoRecuperar.isEmpty()) {
			mensaje +=" Numero de documento,";
			validar = false;
		}
		if (this.contrasenaRecuperar == null || contrasenaRecuperar.isEmpty()) {
			mensaje +=" Nueva Contraseña,";
			validar = false;
		}
		if(this.confirmacionContrasenaRecuperar == null || confirmacionContrasenaRecuperar.isEmpty()){
			mensaje +=" Confirmacion de contraseña,";
			validar=false;
		}
		if(this.valorCaptcha3 == null || this.valorCaptcha3.isEmpty()){
			mensaje +=" Captcha";
			validar=false;
		}
		
		if(!mensaje.isEmpty()){
			mensaje=mensaje.substring(0, mensaje.length()-1);
		}
		if(!validar){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ConstantesWebPrepago.ASIGNAR_CONTRASENA_ME003+ mensaje,""));
		}
		else{
//			if(this.numeroDocumentoRecuperar.length() > 12){
//				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ConstantesWebPrepago.ASIGNAR_CONTRASENA_ME003+ mensaje,""));
//				validar = false;
//			}
			if(this.contrasenaRecuperar.length() > 16 || this.contrasenaRecuperar.length() < 8){
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ConstantesWebPrepago.ASIGNAR_CONTRASENA_ME002+ mensaje,""));
				validar = false;
			}else if(!contrasenaRecuperar.matches(ConstantesWebPrepago.EXP_VALIDAR_CLAVE)){
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ConstantesWebPrepago.ASIGNAR_CONTRASENA_ME002+ mensaje,""));
				validar = false;
			}
		}

		return validar;
	}



	private void enviarCorreo(com.rbm.web.prepago.dto.Usuario usuario) throws IOException {
		Map<String, Object> informacionCorreo = new HashMap<String, Object>();
		informacionCorreo.put("numeroCedula", usuario.getNumDocumento());
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
			sender.enviarCorreo(correo, ConstantesWebPrepago.PLANTILLA_CORREO_WEBPREPAGO_ASIGNACION_CONTRASENA);
			logger.info(this.getClass().getName(), "enviarCorreo", "Correo electrónico  enviado exitosamente");
		} catch (Exception e) {
			logger.error(this.getClass().getName(), "enviarCorreo", "Error enviando correo", e);
		}

	}
	
	
	public void cancelarPreguntas(){
		this.mostrarPreguntas=false;
		this.mostrarPanelSolicitar=true;
	}

	public void actualizarCaptcha(int num){
		RequestContext contexto = RequestContext.getCurrentInstance();
		switch (num) {
		case 1:
			valorCaptcha1="";
			contexto.update(":asignarContrasenaForm:captcha");
			break;
		case 2:
			valorCaptcha2="";
			contexto.update(":asignarContrasenaForm:captcha2");
			break;
		case 3:
			valorCaptcha3="";
			contexto.update(":asignarContrasenaForm:captcha3");
			break;
		}
	}

	/**
	 * @return the formRender
	 */
	public boolean isFormRender() {
		return formRender;
	}

	/**
	 * @param formRender the formRender to set
	 */
	public void setFormRender(boolean formRender) {
		this.formRender = formRender;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
	}

	public String getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

	public String getContrasena() {
		return contrasena;
	}

	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}

	public String getConfirmacionContrasena() {
		return confirmacionContrasena;
	}

	public void setConfirmacionContrasena(String confirmacionContrasena) {
		this.confirmacionContrasena = confirmacionContrasena;
	}

	protected HtmlForm getFormParametro() {
		if (formParametro == null) {
			formParametro = (HtmlForm) findComponentInRoot("formParametro");
		}
		return formParametro;
	}

	public String getNumeroDocumentoRecuperar() {
		return numeroDocumentoRecuperar;
	}

	public void setNumeroDocumentoRecuperar(String numeroDocumentoRecuperar) {
		this.numeroDocumentoRecuperar = numeroDocumentoRecuperar;
	}

	public String getContrasenaRecuperar() {
		return contrasenaRecuperar;
	}

	public void setContrasenaRecuperar(String contrasenaRecuperar) {
		this.contrasenaRecuperar = contrasenaRecuperar;
	}

	public String getConfirmacionContrasenaRecuperar() {
		return confirmacionContrasenaRecuperar;
	}

	public void setConfirmacionContrasenaRecuperar(
			String confirmacionContrasenaRecuperar) {
		this.confirmacionContrasenaRecuperar = confirmacionContrasenaRecuperar;
	}

	public boolean isMostrarPreguntas() {
		return mostrarPreguntas;
	}

	public void setMostrarPreguntas(boolean mostrarPreguntas) {
		this.mostrarPreguntas = mostrarPreguntas;
	}

	public boolean isMostrarPanelOlvido() {
		return mostrarPanelOlvido;
	}



	public void setMostrarPanelOlvido(boolean mostrarPanelOlvido) {
		this.mostrarPanelOlvido = mostrarPanelOlvido;
	}



	public boolean isMostrarPanelComplemento() {
		return mostrarPanelComplemento;
	}



	public void setMostrarPanelComplemento(boolean mostrarPanelComplemento) {
		this.mostrarPanelComplemento = mostrarPanelComplemento;
	}



	public Usuario getUsuarioLDAP() {
		return usuarioLDAP;
	}



	public void setUsuarioLDAP(Usuario usuarioLDAP) {
		this.usuarioLDAP = usuarioLDAP;
	}



	public com.rbm.web.prepago.dto.Usuario getUsuarioDB() {
		return usuarioDB;
	}



	public void setUsuarioDB(com.rbm.web.prepago.dto.Usuario usuarioDB) {
		this.usuarioDB = usuarioDB;
	}



	public String getNumeroDocumentoSolicitar() {
		return numeroDocumentoSolicitar;
	}



	public void setNumeroDocumentoSolicitar(String numeroDocumentoSolicitar) {
		this.numeroDocumentoSolicitar = numeroDocumentoSolicitar;
	}



	public String getPreg1() {
		return preg1;
	}



	public void setPreg1(String preg1) {
		this.preg1 = preg1;
	}



	public String getPreg2() {
		return preg2;
	}



	public void setPreg2(String preg2) {
		this.preg2 = preg2;
	}



	public String getPreg3() {
		return preg3;
	}



	public void setPreg3(String preg3) {
		this.preg3 = preg3;
	}



	public String getResp1() {
		return resp1;
	}



	public void setResp1(String resp1) {
		this.resp1 = resp1;
	}



	public String getResp2() {
		return resp2;
	}



	public void setResp2(String resp2) {
		this.resp2 = resp2;
	}



	public String getResp3() {
		return resp3;
	}



	public void setResp3(String resp3) {
		this.resp3 = resp3;
	}



	public boolean isMostrarPanelSolicitar() {
		return mostrarPanelSolicitar;
	}



	public void setMostrarPanelSolicitar(boolean mostrarPanelSolicitar) {
		this.mostrarPanelSolicitar = mostrarPanelSolicitar;
	}



	public String getUrlLogin() {
		return urlLogin;
	}



	public void setUrlLogin(String urlLogin) {
		this.urlLogin = urlLogin;
	}



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public com.rbm.web.prepago.dto.Usuario getUsuarioLink() {
		return usuarioLink;
	}



	public void setUsuarioLink(com.rbm.web.prepago.dto.Usuario usuarioLink) {
		this.usuarioLink = usuarioLink;
	}



	/**
	 * @return the publickey
	 */
	public String getPublickey() {
		return publickey;
	}



	/**
	 * @param publickey the publickey to set
	 */
	public void setPublickey(String publickey) {
		this.publickey = publickey;
	}



	public long getTiempo() {
		return tiempo;
	}



	public void setTiempo(long tiempo) {
		this.tiempo = tiempo;
	}



	protected CommandButton getParamBack() {
		if (paramBack == null) {
			paramBack = (CommandButton) findComponentInRoot("paramBack");
		}
		return paramBack;
	}

	protected HtmlInputText getIDValue() {
		if (IDValue == null) {
			IDValue = (HtmlInputText) findComponentInRoot("IDValue");
		}
		return IDValue;
	}

	protected Messages getMensajesInner() {
		if (mensajesInner == null) {
			mensajesInner = (Messages) findComponentInRoot("mensajesInner");
		}
		return mensajesInner;
	}

	protected HtmlInputText getNumDocumento() {
		if (numDocumento == null) {
			numDocumento = (HtmlInputText) findComponentInRoot("numDocumento");
		}
		return numDocumento;
	}

	protected HtmlInputSecret getnuevaContrasenaIdR() {
		if (nuevaContrasenaIdR == null) {
			nuevaContrasenaIdR = (HtmlInputSecret) findComponentInRoot("nuevaContrasenaIdR");
		}
		return nuevaContrasenaIdR;
	}

	protected CommandButton getBotonAsignar() {
		if (botonAsignar == null) {
			botonAsignar = (CommandButton) findComponentInRoot("botonAsignar");
		}
		return botonAsignar;
	}

	protected HtmlInputText getNumDocumentoSolicitar() {
		if (numDocumentoSolicitar == null) {
			numDocumentoSolicitar = (HtmlInputText) findComponentInRoot("numDocumentoSolicitar");
		}
		return numDocumentoSolicitar;
	}

	protected CommandButton getBotonSolicitar() {
		if (botonSolicitar == null) {
			botonSolicitar = (CommandButton) findComponentInRoot("botonSolicitar");
		}
		return botonSolicitar;
	}

	protected HtmlInputText getPregunta1() {
		if (pregunta1 == null) {
			pregunta1 = (HtmlInputText) findComponentInRoot("pregunta1");
		}
		return pregunta1;
	}

	protected HtmlInputText getRespuesta1() {
		if (respuesta1 == null) {
			respuesta1 = (HtmlInputText) findComponentInRoot("respuesta1");
		}
		return respuesta1;
	}

	protected HtmlInputText getPregunta2() {
		if (pregunta2 == null) {
			pregunta2 = (HtmlInputText) findComponentInRoot("pregunta2");
		}
		return pregunta2;
	}

	protected HtmlInputText getRespuesta2() {
		if (respuesta2 == null) {
			respuesta2 = (HtmlInputText) findComponentInRoot("respuesta2");
		}
		return respuesta2;
	}

	protected HtmlInputText getPregunta3() {
		if (pregunta3 == null) {
			pregunta3 = (HtmlInputText) findComponentInRoot("pregunta3");
		}
		return pregunta3;
	}

	protected HtmlInputText getRespuesta3() {
		if (respuesta3 == null) {
			respuesta3 = (HtmlInputText) findComponentInRoot("respuesta3");
		}
		return respuesta3;
	}

	protected CommandButton getBotonEnviar() {
		if (botonEnviar == null) {
			botonEnviar = (CommandButton) findComponentInRoot("botonEnviar");
		}
		return botonEnviar;
	}

	protected HtmlOutputText getNumDocumentoRecuperar() {
		if (numDocumentoRecuperar == null) {
			numDocumentoRecuperar = (HtmlOutputText) findComponentInRoot("numDocumentoRecuperar");
		}
		return numDocumentoRecuperar;
	}

	protected HtmlInputSecret getnuevaContrasenaIdRecuperar() {
		if (nuevaContrasenaIdRecuperar == null) {
			nuevaContrasenaIdRecuperar = (HtmlInputSecret) findComponentInRoot("nuevaContrasenaIdRecuperar");
		}
		return nuevaContrasenaIdRecuperar;
	}

	protected CommandButton getBotonCambioContrasena() {
		if (botonCambioContrasena == null) {
			botonCambioContrasena = (CommandButton) findComponentInRoot("botonCambioContrasena");
		}
		return botonCambioContrasena;
	}

	protected PanelGrid getDisplayConfCrearContra() {
		if (displayConfCrearContra == null) {
			displayConfCrearContra = (PanelGrid) findComponentInRoot("displayConfCrearContra");
		}
		return displayConfCrearContra;
	}

	protected CommandButton getIdAceptarCreacion() {
		if (idAceptarCreacion == null) {
			idAceptarCreacion = (CommandButton) findComponentInRoot("idAceptarCreacion");
		}
		return idAceptarCreacion;
	}

	protected PanelGrid getDisplayConfirmacion() {
		if (displayConfirmacion == null) {
			displayConfirmacion = (PanelGrid) findComponentInRoot("displayConfirmacion");
		}
		return displayConfirmacion;
	}

	protected CommandButton getIdAceptar() {
		if (idAceptar == null) {
			idAceptar = (CommandButton) findComponentInRoot("idAceptar");
		}
		return idAceptar;
	}

	protected HtmlForm getAsignarContrasenaForm() {
		if (asignarContrasenaForm == null) {
			asignarContrasenaForm = (HtmlForm) findComponentInRoot("asignarContrasenaForm");
		}
		return asignarContrasenaForm;
	}

	protected HtmlInputHidden getIPValue() {
		if (IPValue == null) {
			IPValue = (HtmlInputHidden) findComponentInRoot("IPValue");
		}
		return IPValue;
	}

	protected CommandButton getBotonCancelar() {
		if (botonCancelar == null) {
			botonCancelar = (CommandButton) findComponentInRoot("botonCancelar");
		}
		return botonCancelar;
	}

	protected CommandButton getBotonCancelar1() {
		if (botonCancelar1 == null) {
			botonCancelar1 = (CommandButton) findComponentInRoot("botonCancelar1");
		}
		return botonCancelar1;
	}

	protected HtmlOutputText getIdUsuarioRecuperar() {
		if (idUsuarioRecuperar == null) {
			idUsuarioRecuperar = (HtmlOutputText) findComponentInRoot("idUsuarioRecuperar");
		}
		return idUsuarioRecuperar;
	}

	protected CommandButton getBotonCancelarPreguntas() {
		if (botonCancelarPreguntas == null) {
			botonCancelarPreguntas = (CommandButton) findComponentInRoot("botonCancelarPreguntas");
		}
		return botonCancelarPreguntas;
	}

	protected CommandButton getBotonCancelar2() {
		if (botonCancelar2 == null) {
			botonCancelar2 = (CommandButton) findComponentInRoot("botonCancelar2");
		}
		return botonCancelar2;
	}

	protected Dialog getDialogoConfCrearContra() {
		if (dialogoConfCrearContra == null) {
			dialogoConfCrearContra = (Dialog) findComponentInRoot("dialogoConfCrearContra");
		}
		return dialogoConfCrearContra;
	}

	protected Dialog getDialogoConfirmacion() {
		if (dialogoConfirmacion == null) {
			dialogoConfirmacion = (Dialog) findComponentInRoot("dialogoConfirmacion");
		}
		return dialogoConfirmacion;
	}
	
	public String getValorCaptcha1() {
		return valorCaptcha1;
	}



	public void setValorCaptcha1(String valorCaptcha1) {
		this.valorCaptcha1 = valorCaptcha1;
	}



	public String getValorCaptcha2() {
		return valorCaptcha2;
	}



	public void setValorCaptcha2(String valorCaptcha2) {
		this.valorCaptcha2 = valorCaptcha2;
	}



	protected HtmlPanelGrid getCaptcha3() {
		if (captcha3 == null) {
			captcha3 = (HtmlPanelGrid) findComponentInRoot("captcha3");
		}
		return captcha3;
	}



	public String getValorCaptcha3() {
		return valorCaptcha3;
	}



	public void setValorCaptcha3(String valorCaptcha3) {
		this.valorCaptcha3 = valorCaptcha3;
	}



	protected HtmlPanelGrid getCaptcha2() {
		if (captcha2 == null) {
			captcha2 = (HtmlPanelGrid) findComponentInRoot("captcha2");
		}
		return captcha2;
	}

	public String getCaptchaValue() {
		return captchaValue;
	}

	public void setCaptchaValue(String captchaValue) {
		this.captchaValue = captchaValue;
	}

	public String getAbreviatura() {
		return abreviatura;
	}

	//@XmlElement(name="abreviatura")
	public void setAbreviatura(String abreviatura) {
		this.abreviatura = abreviatura;
	}

	public static void setCaptcha(Captcha valor) {
		captcha = valor;
	}

	protected GraphicImage getIdCaptcha1() {
		if (idCaptcha1 == null) {
			idCaptcha1 = (GraphicImage) findComponentInRoot("idCaptcha1");
		}
		return idCaptcha1;
	}

	protected GraphicImage getIdCaptcha2() {
		if (idCaptcha2 == null) {
			idCaptcha2 = (GraphicImage) findComponentInRoot("idCaptcha2");
		}
		return idCaptcha2;
	}

	protected GraphicImage getIdCaptcha3() {
		if (idCaptcha3 == null) {
			idCaptcha3 = (GraphicImage) findComponentInRoot("idCaptcha3");
		}
		return idCaptcha3;
	}

}