/**
 * 
 */
package com.rbm.web.prepago.managedBeans;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.component.html.HtmlInputSecret;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.primefaces.component.calendar.Calendar;
import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.component.dialog.Dialog;
import org.primefaces.component.graphicimage.GraphicImage;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.component.messages.Messages;
import org.primefaces.component.outputpanel.OutputPanel;
import org.primefaces.component.panel.Panel;
import org.primefaces.component.panelgrid.PanelGrid;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.context.RequestContext;

import com.ibm.ams.audit.dto.EventSuccessEnum;
import com.ibm.ams.audit.dto.EventTypeEnum;
import com.ibm.ams.audit.dto.SeverityEnum;
import com.ibm.ams.correo.configuracion.Adjunto;
import com.ibm.ams.correo.configuracion.InformacionCorreoDTO;
import com.ibm.ams.correo.generadorcorreo.EmailSender;
import com.ibm.ams.log.Logger;
import com.rbm.web.prepago.configuracion.Configuracion;
import com.rbm.web.prepago.dto.Preguntas;
import com.rbm.web.prepago.dto.TipoDocumento;
import com.rbm.web.prepago.gestores.GestorAuditoria;
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
public class RegistrarUsuarioBean extends PageCodeBase implements Serializable{
	
	/** Clase logger para gestionar los menajes de logs. */
	private static Logger logger = Logger.getInstance(ConstantesWebPrepago.NOMBRE_APLICACION, ConstantesWebPrepago.DIRECTORIO_CONFIGURACION);
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Date fechaRegistro;
	private String ip;

	protected HtmlInputSecret Confirmpassword;
	protected Panel PanelRegistro;
	protected Calendar CalfechaRegistro;
	protected Messages mensajes;
	protected HtmlInputText numDocumento;
	protected HtmlInputText nombre;
	protected HtmlInputText apellido;
	protected HtmlInputText correo;
	protected HtmlInputText confirmaciónCorreo;
	protected HtmlInputText numeroTelefono;
	protected HtmlInputText respuesta1;
	protected HtmlInputText pregunta2;
	protected HtmlInputText respuesta2;
	protected HtmlInputText respuesta3;
	protected SelectBooleanCheckbox politicaDatos;
	protected HtmlForm registroUsuariosForm;
	protected CommandButton botonRegsitrar;
	protected HtmlInputHidden ipValue;
	protected OutputPanel panelPoliticaDatos;
	protected PanelGrid displayConfirmacion;
	protected CommandButton volver;
	protected Dialog dialogoPoliticas;
	protected HtmlSelectOneMenu listaPregunta1;
	protected HtmlSelectOneMenu listaPregunta2;
	protected HtmlSelectOneMenu listaPregunta3;
	protected CommandButton botonPoliticas;
	private int tipoDocumentoSelected;
	private String numeroDocumento;
	private String numeroDocumentoValidacion="/[0-9]+/i";
	private String numeroTelefonoValidacion="/[0-9]+/i";
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
	private boolean checkPoliticas;
	private boolean habilitarCheck;
	protected Panel panelCondicionesReg;
	private String key = "Bar12345Bar12345"; // 128 bit key
    private String initVector = "RandomInitVector"; // 16 bytes IV
    private String initVector2 = "12345678901234567890123456789012"; // 16 bytes IV
    private String captchaValue;
    private static Captcha captcha;
	
	private List<TipoDocumento> listaTipoDoc = new ArrayList<TipoDocumento>();
	private List<Preguntas> preguntas = new ArrayList<Preguntas>();
	private List<Preguntas> listaPreguntas1 = new ArrayList<Preguntas>();
	private List<Preguntas> listaPreguntas2= new ArrayList<Preguntas>();
	private List<Preguntas> listaPreguntas3= new ArrayList<Preguntas>();


	
	private Configuracion conf = null;
	private String publickey;


	private String urlLogin;


	protected CommandButton botonCancelar;


	private Principal usuarioLogin;


	protected HtmlSelectOneMenu listaTipodoc;
	
	private int longitudMax;
	private String valorCaptcha;


	protected HtmlPanelGrid panelCaptcha;


	protected GraphicImage idCaptcha;


	protected InputText idValorCaptcha;

	public RegistrarUsuarioBean() {
		this.setCaptchaValue("/customCaptchaImg?view=registrarUsuario");
		FacesContext fc = FacesContext.getCurrentInstance();
		usuarioLogin = fc.getExternalContext().getUserPrincipal();
		this.habilitarCheck=true;
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
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			setUrlLogin(conf.getUrlLogin());
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ConstantesWebPrepago.ASIGNAR_CONTRASENA_ME007,""));
			GestorAuditoria.getInstance().registrarAuditoria(SeverityEnum.ERROR, EventSuccessEnum.SUCCESS.name(), 
					EventTypeEnum.ERROR.value(),ConstantesWebPrepago.ASIGNAR_CONTRASENA_ME007,"RegistarUsuariosWP",this.ip,"WebPrepago");
		}
	}
	
	public void registrarUsuario (){
		com.rbm.web.prepago.dto.Usuario usuario = new com.rbm.web.prepago.dto.Usuario();
		if (validarCampos() && validarCaptcha()) {
			if(validarLongitudDocumento()){
				if (this.correoElectronico.equals(this.confirmarCorreo)) {
					try {
						if ( GestorBD.consultarUsuarioPorTipoUsuario(this.numeroDocumento, ConstantesWebPrepago.USUARIO_TEMPORAL,null) != null) {
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,ConstantesWebPrepago.REGISTRAR_USUARIO_ME004, null));
						}else{
							
							usuario.setId(GestorBD.secuencialUsuario());
							usuario.setTipoDocumento(this.tipoDocumentoSelected);
							usuario.setNumDocumento(this.numeroDocumento);
							usuario.setNombres(this.nombres.toUpperCase());
							usuario.setApellidos(this.apellidos.toUpperCase());
							usuario.setCorreo(this.correoElectronico);
							usuario.setTelefono(this.telefono);
							usuario.setPregunta1(this.pregunta1Selected);
	//						usuario.setRespuesta1(this.respuestaUno.toUpperCase());
							String claveResp1 = new String(Hex.encodeHexString(encryptPreguntas(key, initVector2, this.respuestaUno.toUpperCase())));
							usuario.setRespuesta1(claveResp1);
							usuario.setPregunta2(this.pregunta2Selected);
	//						usuario.setRespuesta2(this.respuestaDos.toUpperCase());
							String claveResp2 = new String(Hex.encodeHexString(encryptPreguntas(key, initVector2, this.respuestaDos.toUpperCase())));
							usuario.setRespuesta2(claveResp2);
							usuario.setPregunta3(this.pregunta3);
	//						usuario.setRespuesta3(this.respuestaTres.toUpperCase());
							String claveResp3 = new String(Hex.encodeHexString(encryptPreguntas(key, initVector2, this.respuestaTres.toUpperCase())));
							usuario.setRespuesta3(claveResp3);
							
							GestorBD.registrarUsuarioTemporal(usuario);
							enviarCorreo(usuario);
	
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Se ha enviado un mensaje a su cuenta de correo  "+this.correoElectronico+"  para confirmar el registro.",""));
							FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
							
						}
					} catch (Exception e) {
						GestorAuditoria.getInstance().registrarAuditoria(SeverityEnum.ERROR, EventSuccessEnum.SUCCESS.name(), 
								EventTypeEnum.ERROR.value(),ConstantesWebPrepago.MSG_ERR_GENERAL,"RegistarUsuariosWP",this.ip,"WebPrepago");
						logger.info(this.getClass().getName(), "registrarUsuario", "error registrando usuarios: "+e.getMessage());
					}
				}else{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ConstantesWebPrepago.REGISTRAR_USUARIO_ME005, ""));
					logger.info(this.getClass().getName(), "registrarUsuario", "error registrando usuarios, correo electronico no coincide ");
				}
			}
		}
	}
	
	public boolean validarCaptcha() {
		boolean captchaPassed = false;
		try {
			if(captcha != null) {
				captchaPassed = captcha.isCorrect(this.valorCaptcha);
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
	

	public void cancelar(){
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
	}
	
	public void onTipoDocChange(){
		
		if(tipoDocumentoSelected==2){
			//numeros y letras
			numeroDocumentoValidacion="/[a-z0-9_]/i";			
		}else{
			//numeros 
			numeroDocumentoValidacion="/[0-9]+/i";			
		}
		
		validarLongitudDocumento();
	}
	
	private boolean validarLongitudDocumento(){
		TipoDocumento tipoDoc=buscarTipoDocumentobyCodigo(tipoDocumentoSelected);
		if(!tipoDoc.equals(null) && tipoDoc.getLongitudMax()!=0){
			longitudMax=tipoDoc.getLongitudMax();	
			return true;
		}else{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(ConstantesWebPrepago.REGISTRAR_USUARIO_ME008, tipoDoc.getDescripcion()), ""));
			logger.info(this.getClass().getName(), "onTipoDocChange", String.format(ConstantesWebPrepago.REGISTRAR_USUARIO_ME008, tipoDoc.getDescripcion()));
			return false;
		}
	}
	
	private TipoDocumento buscarTipoDocumentobyCodigo(int codigo){
		for (TipoDocumento tipoDoc : listaTipoDoc) {
			if(tipoDoc.getId()==codigo){
				return tipoDoc;
			}
		}
		return null;
	}

	private void enviarCorreo(com.rbm.web.prepago.dto.Usuario usuario) throws IOException {	
		
		try {
		Map<String, Object> informacionCorreo = new HashMap<String, Object>();
		informacionCorreo.put("numeroCedula", usuario.getNumDocumento());
		informacionCorreo.put("urlAsignarContra",conf.getUrlAsignarConstrasena()+"?id="+usuario.getId());
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
			EmailSender sender = new EmailSender();
			sender.enviarCorreo(correo, ConstantesWebPrepago.PLANTILLA_CORREO_WEBPREPAGO);
			logger.info(this.getClass().getName(), "enviarCorreo", "Correo electrónico con número de radicado enviado exitosamente");
		} catch (Exception e) {
			logger.error(this.getClass().getName(), "enviarCorreo", "Error enviando correo", e);
		}
		
	}
	
	

	public boolean validarCampos() {
		boolean validar = true;
		String mensaje="";
		FacesContext fc = FacesContext.getCurrentInstance();
		Map<String,String> params = fc.getExternalContext().getRequestParameterMap();
		
		
		
//		if (this.numeroDocumento.length()<6) {
//			mensaje +=" El valor minimo para el número de documento debe ser de 6 digitos,";
//			validar = false;
//		}
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
		}else{
			
		}
		if (this.confirmarCorreo==null || confirmarCorreo.isEmpty()) {
			mensaje +=" Confirmar correo electronico,";
			validar = false;
		}
		if (this.telefono== null || this.telefono.isEmpty()){
			mensaje +=" Telefono,";
			validar = false;
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
		if (!this.checkPoliticas) {
			mensaje +=" Check Politicas de privacidad,";
			validar = false;
		}
		if(this.valorCaptcha == null || this.valorCaptcha.isEmpty()){
			mensaje +="Captcha";
			validar = false;
		}
		if(!mensaje.isEmpty()){
			mensaje=mensaje.substring(0, mensaje.length()-1);
		}
		if(!validar){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ConstantesWebPrepago.REGISTRAR_USUARIO_ME003+ mensaje,""));
		}
		
		return validar;
	}
	
	public void cerrarDialogo(){
		RequestContext context = RequestContext.getCurrentInstance();
		this.habilitarCheck=false;
		context.execute("dialogoPoliticasVar.hide();");
	}

	public static byte[] encrypt(String key, String initVector, String value) {
		try {
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

			byte[] encrypted = cipher.doFinal(value.getBytes("UTF-8"));
			return Base64.encodeBase64(encrypted);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
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
	
	public void actualizarCaptcha(){
		this.valorCaptcha="";
		RequestContext context = RequestContext.getCurrentInstance();
		context.update("panelCaptcha");
	}
	
	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	protected Messages getMensajes() {
		if (mensajes == null) {
			mensajes = (Messages) findComponentInRoot("mensajes");
		}
		return mensajes;
	}

	protected HtmlInputText getNumDocumento() {
		if (numDocumento == null) {
			numDocumento = (HtmlInputText) findComponentInRoot("numDocumento");
		}
		return numDocumento;
	}

	protected HtmlInputText getNombre() {
		if (nombre == null) {
			nombre = (HtmlInputText) findComponentInRoot("nombre");
		}
		return nombre;
	}

	protected HtmlInputText getApellido() {
		if (apellido == null) {
			apellido = (HtmlInputText) findComponentInRoot("apellido");
		}
		return apellido;
	}

	protected HtmlInputText getCorreo() {
		if (correo == null) {
			correo = (HtmlInputText) findComponentInRoot("correo");
		}
		return correo;
	}

	protected HtmlInputText getConfirmaciónCorreo() {
		if (confirmaciónCorreo == null) {
			confirmaciónCorreo = (HtmlInputText) findComponentInRoot("confirmaciónCorreo");
		}
		return confirmaciónCorreo;
	}

	protected HtmlInputText getNumeroTelefono() {
		if (numeroTelefono == null) {
			numeroTelefono = (HtmlInputText) findComponentInRoot("numeroTelefono");
		}
		return numeroTelefono;
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

	protected HtmlInputText getRespuesta3() {
		if (respuesta3 == null) {
			respuesta3 = (HtmlInputText) findComponentInRoot("respuesta3");
		}
		return respuesta3;
	}

	protected SelectBooleanCheckbox getPoliticaDatos() {
		if (politicaDatos == null) {
			politicaDatos = (SelectBooleanCheckbox) findComponentInRoot("politicaDatos");
		}
		return politicaDatos;
	}

	protected HtmlForm getRegistroUsuariosForm() {
		if (registroUsuariosForm == null) {
			registroUsuariosForm = (HtmlForm) findComponentInRoot("registroUsuariosForm");
		}
		return registroUsuariosForm;
	}

	protected CommandButton getBotonRegsitrar() {
		if (botonRegsitrar == null) {
			botonRegsitrar = (CommandButton) findComponentInRoot("botonRegsitrar");
		}
		return botonRegsitrar;
	}

	protected HtmlInputHidden getIpValue() {
		if (ipValue == null) {
			ipValue = (HtmlInputHidden) findComponentInRoot("ipValue");
		}
		return ipValue;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	protected OutputPanel getPanelPoliticaDatos() {
		if (panelPoliticaDatos == null) {
			panelPoliticaDatos = (OutputPanel) findComponentInRoot("panelPoliticaDatos");
		}
		return panelPoliticaDatos;
	}

	protected PanelGrid getDisplayConfirmacion() {
		if (displayConfirmacion == null) {
			displayConfirmacion = (PanelGrid) findComponentInRoot("displayConfirmacion");
		}
		return displayConfirmacion;
	}

	protected CommandButton getVolver() {
		if (volver == null) {
			volver = (CommandButton) findComponentInRoot("volver");
		}
		return volver;
	}

	protected Dialog getDialogoPoliticas() {
		if (dialogoPoliticas == null) {
			dialogoPoliticas = (Dialog) findComponentInRoot("dialogoPoliticas");
		}
		return dialogoPoliticas;
	}

	protected HtmlSelectOneMenu getListaPregunta1() {
		if (listaPregunta1 == null) {
			listaPregunta1 = (HtmlSelectOneMenu) findComponentInRoot("listaPregunta1");
		}
		return listaPregunta1;
	}

	protected HtmlSelectOneMenu getListaPregunta2() {
		if (listaPregunta2 == null) {
			listaPregunta2 = (HtmlSelectOneMenu) findComponentInRoot("listaPregunta2");
		}
		return listaPregunta2;
	}

	protected HtmlSelectOneMenu getListaPregunta3() {
		if (listaPregunta3 == null) {
			listaPregunta3 = (HtmlSelectOneMenu) findComponentInRoot("listaPregunta3");
		}
		return listaPregunta3;
	}

	protected CommandButton getBotonPoliticas() {
		if (botonPoliticas == null) {
			botonPoliticas = (CommandButton) findComponentInRoot("botonPoliticas");
		}
		return botonPoliticas;
	}

	public int getTipoDocumentoSelected() {
		return tipoDocumentoSelected;
	}

	public void setTipoDocumentoSelected(int tipoDocumentoSelected) {
		this.tipoDocumentoSelected = tipoDocumentoSelected;
	}

	public String getNumeroDocumento() {
		return numeroDocumento;
	}
	
	public String getNumeroDocumentoValidacion() {
		return numeroDocumentoValidacion;
	}

	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}
	
	public void setNumeroDocumentoValidacion(String numeroDocumentoValidacion) {
		this.numeroDocumentoValidacion = numeroDocumentoValidacion;
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

	public String getRespuestaUno() {
		return respuestaUno;
	}

	public void setRespuestaUno(String respuestaUno) {
		this.respuestaUno = respuestaUno;
	}

	public int getPregunta1Selected() {
		return pregunta1Selected;
	}

	public void setPregunta1Selected(int pregunta1Selected) {
		this.pregunta1Selected = pregunta1Selected;
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

	public boolean isCheckPoliticas() {
		return checkPoliticas;
	}

	public void setCheckPoliticas(boolean checkPoliticas) {
		this.checkPoliticas = checkPoliticas;
	}

	public boolean isHabilitarCheck() {
		return habilitarCheck;
	}

	public void setHabilitarCheck(boolean habilitarCheck) {
		this.habilitarCheck = habilitarCheck;
	}

	protected Panel getPanelCondicionesReg() {
		if (panelCondicionesReg == null) {
			panelCondicionesReg = (Panel) findComponentInRoot("panelCondicionesReg");
		}
		return panelCondicionesReg;
	}

	public List<TipoDocumento> getListaTipoDoc() {
		return listaTipoDoc;
	}

	public void setListaTipoDoc(List<TipoDocumento> listaTipoDoc) {
		this.listaTipoDoc = listaTipoDoc;
	}

	public List<Preguntas> getPreguntas() {
		return preguntas;
	}

	public void setPreguntas(List<Preguntas> preguntas) {
		this.preguntas = preguntas;
	}

	public List<Preguntas> getListaPreguntas1() {
		return listaPreguntas1;
	}

	public void setListaPreguntas1(List<Preguntas> listaPreguntas1) {
		this.listaPreguntas1 = listaPreguntas1;
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

	

	

	/**
	 * @return the urlLogin
	 */
	public String getUrlLogin() {
		return urlLogin;
	}

	/**
	 * @param urlLogin the urlLogin to set
	 */
	public void setUrlLogin(String urlLogin) {
		this.urlLogin = urlLogin;
	}

	protected CommandButton getBotonCancelar() {
		if (botonCancelar == null) {
			botonCancelar = (CommandButton) findComponentInRoot("botonCancelar");
		}
		return botonCancelar;
	}

	public Principal getUsuarioLogin() {
		return usuarioLogin;
	}

	public void setUsuarioLogin(Principal usuarioLogin) {
		this.usuarioLogin = usuarioLogin;
	}

	protected HtmlSelectOneMenu getListaTipodoc() {
		if (listaTipodoc == null) {
			listaTipodoc = (HtmlSelectOneMenu) findComponentInRoot("listaTipodoc");
		}
		return listaTipodoc;
	}

	public int getLongitudMax() {
		return longitudMax;
	}

	public void setLongitudMax(int longitudMax) {
		this.longitudMax = longitudMax;
	}

	public String getValorCaptcha() {
		return valorCaptcha;
	}

	public void setValorCaptcha(String valorCaptcha) {
		this.valorCaptcha = valorCaptcha;
	}

	protected HtmlPanelGrid getPanelCaptcha() {
		if (panelCaptcha == null) {
			panelCaptcha = (HtmlPanelGrid) findComponentInRoot("panelCaptcha");
		}
		return panelCaptcha;
	}

	protected GraphicImage getIdCaptcha() {
		if (idCaptcha == null) {
			idCaptcha = (GraphicImage) findComponentInRoot("idCaptcha");
		}
		return idCaptcha;
	}

	protected InputText getIdValorCaptcha() {
		if (idValorCaptcha == null) {
			idValorCaptcha = (InputText) findComponentInRoot("idValorCaptcha");
		}
		return idValorCaptcha;
	}

	public String getCaptchaValue() {
		return captchaValue;
	}

	public void setCaptchaValue(String captchaValue) {
		this.captchaValue = captchaValue;
	}
	
	public static void setCaptcha(Captcha valor) {
		captcha = valor;
	}

	public String getNumeroTelefonoValidacion() {
		return numeroTelefonoValidacion;
	}

	public void setNumeroTelefonoValidacion(String numeroTelefonoValidacion) {
		this.numeroTelefonoValidacion = numeroTelefonoValidacion;
	}

}