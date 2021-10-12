package com.rbm.web.prepago.managedBeans;



import java.io.Serializable;
import java.security.Principal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.component.autocomplete.AutoComplete;
import org.primefaces.component.calendar.Calendar;
import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.component.outputpanel.OutputPanel;
import org.primefaces.component.panel.Panel;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;

import com.ibm.faces20.portlet.component.PortletResourceURL;
import com.rbm.web.prepago.dto.TarjetaPrepago;
import com.rbm.web.prepago.dto.Usuario;
import com.rbm.web.prepago.exception.ExceptionWebPrepago;
import com.rbm.web.prepago.gestores.GestorBD;
import com.rbm.web.prepago.pagecode.PageCodeBase;
import com.rbm.web.prepago.util.ConstantesWebPrepago;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlInputHidden;
import org.primefaces.component.messages.Messages;
import org.primefaces.component.datatable.DataTable;



public class ConsultarSaldoBean extends PageCodeBase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6088805391892227019L;
	/**
	 * 
	 */
	private List<TarjetaPrepago> listaTarjetas = new ArrayList<TarjetaPrepago>();
	private String ip;
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
	protected Panel PanelRegistro;
	protected AutoComplete autoComTipoDoc;
	protected AutoComplete autoComPregunta;
	protected AutoComplete autoComPregunta2;
	protected AutoComplete autoComPregunta3;
	protected Calendar CalfechaRegistro;
	protected OutputPanel panelPoliticaDatos;
	protected SelectBooleanCheckbox politicaDatos;
	protected CommandButton botonRegsitrar;
	protected CommandButton botonCancelar;
	private Principal usuarioLogin;
	protected PortletResourceURL ConsultarMovimientos;
	private Usuario usuarioDB;
	private boolean mostrarTabla;
	protected HtmlForm consultaTarjetaForm;
	protected HtmlInputHidden ipValue;
	protected Messages mensajes;
	protected DataTable tablaTarjetasPrepago;
	protected Panel panelParametrizacion;
	
	/**
	 * Obtiene las tarjetas prepagadas asociadas al usuario en sesion
	 */
	
	public void init() {
		try{
			FacesContext fc = FacesContext.getCurrentInstance();
			usuarioLogin = fc.getExternalContext().getUserPrincipal();
			String usuarioF = this.usuarioLogin.getName();
			String [] primero = usuarioF.split(",");
			String [] segundo = primero[0].toString().split("=");
			String usuarioFinal;
			if(segundo.length>1){
				usuarioFinal = segundo[1];
			}else{
				usuarioFinal = segundo[0];
			}
			this.listaTarjetas= new ArrayList<TarjetaPrepago>();
			//Consulta el id del usuario de la base de datos y trae todas las tarjetas asociadas a este id
			usuarioDB = GestorBD.consultarUsuarioPorTipoUsuario(usuarioFinal, ConstantesWebPrepago.USUARIO_PREMANENTE,null);
			List<TarjetaPrepago> tarjetasPrepago = GestorBD.consultarTarjetasPorUsuario(usuarioDB.getId(),null,null, ConstantesWebPrepago.BEAN_CONSULTA_NOVEDAD);
			listaTarjetas.addAll(tarjetasPrepago);
			this.mostrarTabla=true;
			//Si la lista esta vacia, entonces se muestra el mensaje correspondiente
			if(listaTarjetas.size() == 0){
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,ConstantesWebPrepago.CONSULTAR_SALDO_MI001, ""));
				this.mostrarTabla=false;
			}
		}catch(SQLException e){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ConstantesWebPrepago.CONSULTAR_SALDO_ME005 , ""));
			e.printStackTrace();
		}catch(Exception e){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ConstantesWebPrepago.CONSULTAR_SALDO_ME001 , ""));
			e.printStackTrace();
			
		}


	}
	
	public ConsultarSaldoBean() {
		super();
		FacesContext fc = FacesContext.getCurrentInstance();
		usuarioLogin = fc.getExternalContext().getUserPrincipal();
		String usuarioF = this.usuarioLogin.getName();
		String [] primero = usuarioF.split(",");
		String [] segundo = primero[0].toString().split("=");
		String usuarioFinal;
		if(segundo.length>1){
			usuarioFinal = segundo[1];
		}else{
			usuarioFinal = segundo[0];
		}
		
		try {
			GestorBD.ActualizarUltimaConexion(usuarioFinal);
		} catch (ExceptionWebPrepago e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al actualizar ultima conexion" , ""));
			e.printStackTrace();
		}

	}



	/**
	 * Metodo que permite redirigir la pagina para consultar el saldo
	 * @param tarjeta
	 * @return ruta regresar
	 */
	public String consultaSaldo(TarjetaPrepago tarjeta) {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ConstantesWebPrepago.BEAN_CONSULTAR_MOVIMIENTOS,null);
		session.setAttribute(ConstantesWebPrepago.OBJETO_TARJETA,tarjeta);
		session.setAttribute(ConstantesWebPrepago.NAVEGACION,ConstantesWebPrepago.CONSULTA_NOVEDADES);
		session.setAttribute(ConstantesWebPrepago.ATTRIBUTO_NOMBRE_BEAN,ConstantesWebPrepago.BEAN_CONSULTA_NOVEDAD);
		return ConstantesWebPrepago.CONSULTAR_MOVIEMIENTOS;
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

	protected AutoComplete getAutoComTipoDoc() {
		if (autoComTipoDoc == null) {
			autoComTipoDoc = (AutoComplete) findComponentInRoot("autoComTipoDoc");
		}
		return autoComTipoDoc;
	}

	protected AutoComplete getAutoComPregunta() {
		if (autoComPregunta == null) {
			autoComPregunta = (AutoComplete) findComponentInRoot("autoComPregunta");
		}
		return autoComPregunta;
	}

	protected AutoComplete getAutoComPregunta2() {
		if (autoComPregunta2 == null) {
			autoComPregunta2 = (AutoComplete) findComponentInRoot("autoComPregunta2");
		}
		return autoComPregunta2;
	}

	protected AutoComplete getAutoComPregunta3() {
		if (autoComPregunta3 == null) {
			autoComPregunta3 = (AutoComplete) findComponentInRoot("autoComPregunta3");
		}
		return autoComPregunta3;
	}

	protected Calendar getCalfechaRegistro() {
		if (CalfechaRegistro == null) {
			CalfechaRegistro = (Calendar) findComponentInRoot("CalfechaRegistro");
		}
		return CalfechaRegistro;
	}

	protected OutputPanel getPanelPoliticaDatos() {
		if (panelPoliticaDatos == null) {
			panelPoliticaDatos = (OutputPanel) findComponentInRoot("panelPoliticaDatos");
		}
		return panelPoliticaDatos;
	}

	protected SelectBooleanCheckbox getPoliticaDatos() {
		if (politicaDatos == null) {
			politicaDatos = (SelectBooleanCheckbox) findComponentInRoot("politicaDatos");
		}
		return politicaDatos;
	}

	protected CommandButton getBotonRegsitrar() {
		if (botonRegsitrar == null) {
			botonRegsitrar = (CommandButton) findComponentInRoot("botonRegsitrar");
		}
		return botonRegsitrar;
	}

	protected CommandButton getBotonCancelar() {
		if (botonCancelar == null) {
			botonCancelar = (CommandButton) findComponentInRoot("botonCancelar");
		}
		return botonCancelar;
	}

	public List<TarjetaPrepago> getListaTN() {
		return listaTarjetas;
	}

	public void setListaTN(List<TarjetaPrepago> listaTN) {
		this.listaTarjetas = listaTN;
	}

	public List<TarjetaPrepago> getListaTarjetas() {
		return listaTarjetas;
	}

	public void setListaTarjetas(List<TarjetaPrepago> listaTarjetas) {
		this.listaTarjetas = listaTarjetas;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Principal getUsuarioLogin() {
		return usuarioLogin;
	}

	public void setUsuarioLogin(Principal usuarioLogin) {
		this.usuarioLogin = usuarioLogin;
	}


	public boolean isMostrarTabla() {
		return mostrarTabla;
	}


	public void setMostrarTabla(boolean mostrarTabla) {
		this.mostrarTabla = mostrarTabla;
	}

	protected HtmlForm getConsultaTarjetaForm() {
		if (consultaTarjetaForm == null) {
			consultaTarjetaForm = (HtmlForm) findComponentInRoot("consultaTarjetaForm");
		}
		return consultaTarjetaForm;
	}

	protected HtmlInputHidden getIpValue() {
		if (ipValue == null) {
			ipValue = (HtmlInputHidden) findComponentInRoot("ipValue");
		}
		return ipValue;
	}

	protected Messages getMensajes() {
		if (mensajes == null) {
			mensajes = (Messages) findComponentInRoot("mensajes");
		}
		return mensajes;
	}

	protected DataTable getTablaTarjetasPrepago() {
		if (tablaTarjetasPrepago == null) {
			tablaTarjetasPrepago = (DataTable) findComponentInRoot("tablaTarjetasPrepago");
		}
		return tablaTarjetasPrepago;
	}

	protected Panel getPanelParametrizacion() {
		if (panelParametrizacion == null) {
			panelParametrizacion = (Panel) findComponentInRoot("panelParametrizacion");
		}
		return panelParametrizacion;
	}


}