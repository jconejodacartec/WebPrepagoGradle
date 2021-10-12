package com.rbm.web.prepago.managedBeans;

import java.security.Principal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.faces.application.FacesMessage;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;

import org.apache.commons.codec.binary.Base64;
import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.component.confirmdialog.ConfirmDialog;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.messages.Messages;
import org.primefaces.component.outputpanel.OutputPanel;
import org.primefaces.component.panel.Panel;
import org.primefaces.component.panelgrid.PanelGrid;
import org.primefaces.context.RequestContext;

import com.ibm.ams.audit.dto.EventSuccessEnum;
import com.ibm.ams.audit.dto.EventTypeEnum;
import com.ibm.ams.audit.dto.SeverityEnum;
import com.ibm.ams.log.Logger;
import com.rbm.web.prepago.dto.Bolsillo;
import com.rbm.web.prepago.dto.ParamBines;
import com.rbm.web.prepago.dto.TarjetaPrepago;
import com.rbm.web.prepago.dto.TipoDocumento;
import com.rbm.web.prepago.dto.Usuario;
import com.rbm.web.prepago.exception.ExceptionWebPrepago;
import com.rbm.web.prepago.gestores.GestorAuditoria;
import com.rbm.web.prepago.gestores.GestorBD;
import com.rbm.web.prepago.gestores.GestorCache;
import com.rbm.web.prepago.pagecode.PageCodeBase;
import com.rbm.web.prepago.util.ConstantesWebPrepago;



/**
 * @author jprobayo
 *
 */




public class AdministrarTarjetaBean extends PageCodeBase {
	
	private static Logger logger = Logger.getInstance(ConstantesWebPrepago.NOMBRE_APLICACION, ConstantesWebPrepago.DIRECTORIO_CONFIGURACION);
	
	private List<TarjetaPrepago> resultados = new ArrayList<TarjetaPrepago>();
	protected HtmlForm formularioAdministrarTarjeta;
	protected Panel panelGeneral;
	protected HtmlInputText numeroTarjeta;
	protected HtmlInputText entidad;
	protected HtmlSelectOneMenu tipoDocumento;
	protected HtmlInputText numDocumento;
	protected HtmlInputText nombreTarjeta;
	protected CommandButton botonCancelar;
	protected OutputPanel panelResultadoConsulta;
	protected DataTable tablaValores;
	protected PanelGrid displayConfirmacion;
	protected CommandButton condirmarCambios;
	protected CommandButton deshacerCambios;
	private String ip;
	protected HtmlInputHidden ipValue;
	protected CommandButton botonGuardar;
	protected ConfirmDialog confirmacionEliminar;
	protected HtmlInputText tipoTarjeta;
	protected OutputPanel panelInscribirTarjetas;
	
	private boolean mostrarPanelTarjetas;
	private String numTarjeta;
	private String entidadRelacionada;
	private int tipoDocSelected;
	private List<TipoDocumento> listaTipoDocumento = new ArrayList<TipoDocumento>();
	private List<Bolsillo> listaBolsillos = new ArrayList<Bolsillo>();
	private String bolsilloSelected;
	private String campoDocumento;
	private String campoNombreTarjeta;
	private boolean deshabilitarBtnInscribir;
	private String campoTipoTarjeta;
	private List<Usuario> listaResultado = new ArrayList<Usuario>();
	private Principal usuarioLogin;	
	private boolean disableCampoNumTarjeta;
	private String numTarjetaEliminar;
	private String numTarjetaEliminarEnmascarada;
	private TarjetaPrepago tarjetaPrepago = new TarjetaPrepago();
	private Usuario usuario = new Usuario();
	private ParamBines paramBines = new ParamBines();
	private String typeMsjCarga;
	private String msjCarga;
	
	private String key = "Bar12345Bar12345"; // 128 bit key
    private String initVector = "RandomInitVector"; // 16 bytes IV
	protected Messages mensaje;
	protected CommandButton botonInscribir;
	protected HtmlOutputLabel botonInscribirl;
	private String usuarioFinal;
	public void buscarTarjetas(){
		RequestContext contexto = RequestContext.getCurrentInstance();
		try {
			FacesContext fc = FacesContext.getCurrentInstance();
			usuarioLogin = fc.getExternalContext().getUserPrincipal();
			String usuarioF = usuarioLogin.getName();
			String [] primero = usuarioF.split(",");
			String [] segundo = primero[0].toString().split("=");
			if(segundo.length>1){
				usuarioFinal = segundo[1];
			}else{
				usuarioFinal = segundo[0];
			}
			this.listaResultado= new ArrayList<Usuario>();
			this.listaResultado.addAll(GestorBD.consutarTarjetasUsuario(usuarioFinal));
			if (this.listaResultado.isEmpty()) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, ConstantesWebPrepago.ADMINISTRAR_TARJETAS_MI001, ""));
			}
			logger.info(this.getClass().getName(), "buscarTarjetas", "---------IP: "+this.ip);
		} catch (ExceptionWebPrepago e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Consultando Tarjetas para el usuario: "+ usuarioFinal, ""));
			e.printStackTrace();
		} catch (SQLException e) {
			logger.error(this.getClass().getName(), "buscarTarjetas","Error en BD", e);
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR,ConstantesWebPrepago.CONSULTAR_SALDO_ME001, ""));
		}
		contexto.update("panelResultadoConsulta");
		
	}
	
	public AdministrarTarjetaBean() {
		
		try {
			FacesContext fc = FacesContext.getCurrentInstance();
			usuarioLogin = fc.getExternalContext().getUserPrincipal();
			String usuarioF = usuarioLogin.getName();
			String [] primero = usuarioF.split(",");
			String [] segundo = primero[0].toString().split("=");
			if(segundo.length>1){
				usuarioFinal = segundo[1];
			}else{
				usuarioFinal = segundo[0];
			}
			GestorBD.ActualizarUltimaConexion(usuarioFinal);
			this.listaTipoDocumento.addAll(GestorCache.getTipoDocs());
			this.disableCampoNumTarjeta=false;
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ConstantesWebPrepago.ADMINISTRAR_TARJETAS_ME007, ""));
			e.printStackTrace();
		}

	}
	
	
	public void InscribirTarjeta(){
		this.deshabilitarBtnInscribir=true;
		this.mostrarPanelTarjetas=true;
		this.disableCampoNumTarjeta=false;
		this.bolsilloSelected = "";
	}
	
	public void cancelar(){
		limpiarCampos();
		this.deshabilitarBtnInscribir=false;
		this.mostrarPanelTarjetas=false;
	}
	
	
	public void limpiarCampos() {
		this.numTarjeta="";
		this.entidadRelacionada="";
		this.tipoDocSelected=0;
		this.campoDocumento="";
		this.campoNombreTarjeta="";
		this.campoTipoTarjeta="";
	}

	public void guardarTarjeta() throws ExceptionWebPrepago{
		RequestContext contexto = RequestContext.getCurrentInstance();
		try {
			if (!this.disableCampoNumTarjeta) {
				if (validarCampos()) {
					Usuario usuarioid = GestorBD.consultarUsuarioPorTipoUsuario(usuarioFinal, ConstantesWebPrepago.USUARIO_PREMANENTE,null);
					usuario.setId(usuarioid.getId());
					String clave = new String(encrypt(key, initVector, this.numTarjeta));
					usuario.getTarjetaPrepago().setNumeroTarjeta(this.numTarjeta);
					usuario.getTarjetaPrepago().setEnti(this.paramBines.getEntidad());
					usuario.getTipoDocum().setId(tipoDocSelected);
					usuario.setNumDocumento(campoDocumento);
					usuario.getTarjetaPrepago().setNombreTarjeta(this.paramBines.getTipoTarjeta().getNombre());
					usuario.getTarjetaPrepago().setTipTarjeta(this.paramBines.getTipoTarjeta());
					usuario.getTarjetaPrepago().setNombreTarjeta(this.campoNombreTarjeta);
					GestorBD.asignarTarjeta(usuario, clave);
					this.listaResultado = new ArrayList<Usuario>();
					this.listaResultado.addAll(GestorBD.consutarTarjetasUsuario(usuarioFinal));

					this.deshabilitarBtnInscribir=false;
					this.mostrarPanelTarjetas= false;
					limpiarCampos();
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ConstantesWebPrepago.ADMINISTRAR_TARJETAS_MI002,""));
					GestorAuditoria.getInstance().registrarAuditoria(SeverityEnum.INFORMATIONAL, EventSuccessEnum.SUCCESS.name(), 
							EventTypeEnum.CREAR.value(), construirMensaje("Creo la tarjeta numero" + this.numTarjeta ),usuarioFinal,this.ip,"WebPrepago");			
					
				} 
			} else {
				if (validarCampos()) {
					Usuario usuarioid = GestorBD.consultarUsuarioPorTipoUsuario(usuarioFinal, ConstantesWebPrepago.USUARIO_PREMANENTE,null);
					String clave = new String(encrypt(key, initVector, this.numTarjeta));
					GestorBD.modificarTarjeta(usuarioid.getId(), this.campoDocumento, this.campoNombreTarjeta, this.tipoDocSelected, clave);
					this.listaResultado = new ArrayList<Usuario>();
					this.listaResultado.addAll(GestorBD.consutarTarjetasUsuario(usuarioFinal));
					this.deshabilitarBtnInscribir=false;
					this.mostrarPanelTarjetas= false;
					limpiarCampos();
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ConstantesWebPrepago.ADMINISTRAR_TARJETAS_MI006,""));
					GestorAuditoria.getInstance().registrarAuditoria(SeverityEnum.INFORMATIONAL, EventSuccessEnum.SUCCESS.name(), 
							EventTypeEnum.CREAR.value(), construirMensaje("Creo la tarjeta numero" + this.numTarjeta ),usuarioFinal,this.ip,"WebPrepago");			
					
				} 
			}
			
			contexto.update("panelResultadoConsulta");
		} catch (ExceptionWebPrepago e) {
			logger.error(this.getClass().getName(), "guardarTarjeta","Error al procesar la tarjeta", e);
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR,ConstantesWebPrepago.CONSULTAR_SALDO_ME001, ""));
		} catch (SQLException e) {
			logger.error(this.getClass().getName(), "guardarTarjeta","Error en BD", e);
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR,ConstantesWebPrepago.CONSULTAR_SALDO_ME001, ""));
		}
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
	
	private boolean validarCampos() {
		boolean validar = true;
		
		if (this.numTarjeta == null || this.numTarjeta.isEmpty()) {
			validar = false;
		}
		if (this.tipoDocSelected==0) {
			validar=false;
		}
		if (this.campoDocumento == null || this.campoDocumento.isEmpty()) {
			validar=false;
		}
		if (this.campoNombreTarjeta== null || this.campoNombreTarjeta.isEmpty()) {
			validar=false;
		}
		if (!validar) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ConstantesWebPrepago.ADMINISTRAR_TARJETAS_ME003,""));
		}
		return validar;
	}

	public void consultarTipoYEntidad(){

		try {
			if (!GestorBD.existeTarjetaBPO(this.numTarjeta)) {
				this.setTypeMsjCarga("error");
				this.setMsjCarga(ConstantesWebPrepago.ADMINISTRAR_TARJETAS_MI004);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ConstantesWebPrepago.ADMINISTRAR_TARJETAS_MI004,""));
			} else {
				String clave = new String(encrypt(key, initVector, this.numTarjeta));
				if (GestorBD.existeTartejaBD(clave)) {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ConstantesWebPrepago.ADMINISTRAR_TARJETAS_ME001,""));
				} else {
					if (this.numTarjeta.length() <= 12 || this.numTarjeta.length()==15 || this.numTarjeta.length()==17 || this.numTarjeta.length()==18 || this.numTarjeta.length() >19) {
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "El número de tarjeta no es valido",""));
					} else {
						if (this.listaResultado.size()>=10) {
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ConstantesWebPrepago.ADMINISTRAR_TARJETAS_MI005,""));
							return;
						} else {
							try {
								//validarExistenciaTarjetaSEI
								this.paramBines = GestorBD.getParamBines(this.numTarjeta);
								if (this.paramBines != null ) {
									this.entidadRelacionada=this.paramBines.getEntidad().getNombre();
									this.campoTipoTarjeta=this.paramBines.getTipoTarjeta().getNombre();
									listaBolsillos=GestorBD.getBolsillosbyTarjeta(this.numTarjeta);
								} else {
									listaBolsillos=new ArrayList<>();
									this.entidadRelacionada="";
									this.campoTipoTarjeta="";
									FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "El número de tarjeta no existe.",""));
								}
							} catch (Exception e) {
								FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ConstantesWebPrepago.ADMINISTRAR_TARJETAS_ME007,""));
							}
						}
					}
				}
			}
		} catch (ExceptionWebPrepago e) {
			e.printStackTrace();
		}
	}
	
	public void editarTarjeta(Usuario usuarioEdit){
		
		try {
			this.mostrarPanelTarjetas=true;
			this.disableCampoNumTarjeta=true;
			this.numTarjeta = usuarioEdit.getTarjetaPrepago().getNumTarjeta();
			this.entidadRelacionada = usuarioEdit.getTarjetaPrepago().getEnti().getNombre();
			for (int i =0; i < this.listaTipoDocumento.size(); i++) { 
				if (this.listaTipoDocumento.get(i).getDescripcion().equals(usuarioEdit.getTipoDocum().getDescripcion())) {
					this.tipoDocSelected = this.listaTipoDocumento.get(i).getId();
					break;
				}
			}
			this.campoDocumento = usuarioEdit.getTarjetaPrepago().getNumDocumento();
			this.campoNombreTarjeta = usuarioEdit.getTarjetaPrepago().getNombreTarjeta();
			this.campoTipoTarjeta = usuarioEdit.getTarjetaPrepago().getTipTarjeta().getNombre();
			this.listaBolsillos=GestorBD.getBolsillosbyTarjeta(this.numTarjeta);
			 
			GestorAuditoria.getInstance().registrarAuditoria(SeverityEnum.INFORMATIONAL, EventSuccessEnum.SUCCESS.name(), 
					EventTypeEnum.MODIFICAR.value(), construirMensaje("Modificó la informacion de la tarjeta " + this.numTarjeta ),usuarioFinal,this.ip,"WebPrepago");			
			
		} catch (Exception e) {
			logger.error(this.getClass().getName(), "editarTarjeta", "Error cargando parametros para editar Tarjeta", e);
			GestorAuditoria.getInstance().registrarAuditoria(SeverityEnum.ERROR, EventSuccessEnum.SUCCESS.name(), 
					EventTypeEnum.ERROR.value(),ConstantesWebPrepago.MSG_ERR_GENERAL,usuarioFinal,this.ip,"WebPrepago");
		}
	}
	
	public void eliminarTarjeta(Usuario usuario){
		this.numTarjetaEliminar= usuario.getTarjetaPrepago().getNumTarjeta();
		this.numTarjetaEliminarEnmascarada = usuario.getTarjetaPrepago().getNumeroTarjetaEnmascarado();
	}
	
	public void btnConfirmarEliminar(){
		try {
			String clave = new String(encrypt(key, initVector, this.numTarjetaEliminar));
			GestorBD.EliminarTarjeta(clave,null);	
			this.listaResultado = new ArrayList<Usuario>();
			this.listaResultado.addAll(GestorBD.consutarTarjetasUsuario(usuarioFinal));
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ConstantesWebPrepago.ADMINISTRAR_TARJETAS_MI003,""));
			GestorAuditoria.getInstance().registrarAuditoria(SeverityEnum.INFORMATIONAL, EventSuccessEnum.SUCCESS.name(), 
					EventTypeEnum.MODIFICAR.value(), construirMensaje("Eliminó la informacion de la tarjeta " + this.numTarjeta ),usuarioFinal,this.ip,"WebPrepago");			
			
		} catch (ExceptionWebPrepago e) {
			logger.error(this.getClass().getName(), "btnConfirmarEliminar","Error al  confirmar la eliminacion", e);
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR,ConstantesWebPrepago.CONSULTAR_SALDO_ME001, ""));
			GestorAuditoria.getInstance().registrarAuditoria(SeverityEnum.ERROR, EventSuccessEnum.SUCCESS.name(), 
					EventTypeEnum.ERROR.value(),ConstantesWebPrepago.MSG_ERR_GENERAL,usuarioFinal,this.ip,"WebPrepago");			
			
		} catch (SQLException e) {
			logger.error(this.getClass().getName(), "btnConfirmarEliminar","Error en la BD", e);
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR,ConstantesWebPrepago.CONSULTAR_SALDO_ME001, ""));
		}
	}
	
	/**
	 * Metodo que permite construir un mensaje
	 */
	private String construirMensaje(String msg) {
		StringBuilder mensaje = new StringBuilder();
		mensaje.append("El usuario: ");
		mensaje.append(usuarioFinal).append(" ").append(msg);

		return mensaje.toString();
	}
	
	
	/**
	 * @return the resultados
	 */
	public List<TarjetaPrepago> getResultados() {
		return resultados;
	}
	/**
	 * @param resultados the resultados to set
	 */
	public void setResultados(List<TarjetaPrepago> resultados) {
		this.resultados = resultados;
	}

	protected HtmlForm getFormularioAdministrarTarjeta() {
		if (formularioAdministrarTarjeta == null) {
			formularioAdministrarTarjeta = (HtmlForm) findComponentInRoot("formularioAdministrarTarjeta");
		}
		return formularioAdministrarTarjeta;
	}

	protected Panel getPanelGeneral() {
		if (panelGeneral == null) {
			panelGeneral = (Panel) findComponentInRoot("panelGeneral");
		}
		return panelGeneral;
	}

	protected HtmlInputText getNumeroTarjeta() {
		if (numeroTarjeta == null) {
			numeroTarjeta = (HtmlInputText) findComponentInRoot("numeroTarjeta");
		}
		return numeroTarjeta;
	}

	protected HtmlInputText getEntidad() {
		if (entidad == null) {
			entidad = (HtmlInputText) findComponentInRoot("entidad");
		}
		return entidad;
	}

	protected HtmlSelectOneMenu getTipoDocumento() {
		if (tipoDocumento == null) {
			tipoDocumento = (HtmlSelectOneMenu) findComponentInRoot("tipoDocumento");
		}
		return tipoDocumento;
	}

	protected HtmlInputText getNumDocumento() {
		if (numDocumento == null) {
			numDocumento = (HtmlInputText) findComponentInRoot("numDocumento");
		}
		return numDocumento;
	}

	protected HtmlInputText getNombreTarjeta() {
		if (nombreTarjeta == null) {
			nombreTarjeta = (HtmlInputText) findComponentInRoot("nombreTarjeta");
		}
		return nombreTarjeta;
	}

	protected CommandButton getBotonCancelar() {
		if (botonCancelar == null) {
			botonCancelar = (CommandButton) findComponentInRoot("botonCancelar");
		}
		return botonCancelar;
	}

	protected OutputPanel getPanelResultadoConsulta() {
		if (panelResultadoConsulta == null) {
			panelResultadoConsulta = (OutputPanel) findComponentInRoot("panelResultadoConsulta");
		}
		return panelResultadoConsulta;
	}

	protected DataTable getTablaValores() {
		if (tablaValores == null) {
			tablaValores = (DataTable) findComponentInRoot("tablaValores");
		}
		return tablaValores;
	}

	protected PanelGrid getDisplayConfirmacion() {
		if (displayConfirmacion == null) {
			displayConfirmacion = (PanelGrid) findComponentInRoot("displayConfirmacion");
		}
		return displayConfirmacion;
	}

	protected CommandButton getCondirmarCambios() {
		if (condirmarCambios == null) {
			condirmarCambios = (CommandButton) findComponentInRoot("condirmarCambios");
		}
		return condirmarCambios;
	}

	protected CommandButton getDeshacerCambios() {
		if (deshacerCambios == null) {
			deshacerCambios = (CommandButton) findComponentInRoot("deshacerCambios");
		}
		return deshacerCambios;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	protected HtmlInputHidden getIpValue() {
		if (ipValue == null) {
			ipValue = (HtmlInputHidden) findComponentInRoot("ipValue");
		}
		return ipValue;
	}

	protected CommandButton getBotonGuardar() {
		if (botonGuardar == null) {
			botonGuardar = (CommandButton) findComponentInRoot("botonGuardar");
		}
		return botonGuardar;
	}

	protected ConfirmDialog getConfirmacionEliminar() {
		if (confirmacionEliminar == null) {
			confirmacionEliminar = (ConfirmDialog) findComponentInRoot("confirmacionEliminar");
		}
		return confirmacionEliminar;
	}

	protected HtmlInputText getTipoTarjeta() {
		if (tipoTarjeta == null) {
			tipoTarjeta = (HtmlInputText) findComponentInRoot("tipoTarjeta");
		}
		return tipoTarjeta;
	}

	protected OutputPanel getPanelInscribirTarjetas() {
		if (panelInscribirTarjetas == null) {
			panelInscribirTarjetas = (OutputPanel) findComponentInRoot("panelInscribirTarjetas");
		}
		return panelInscribirTarjetas;
	}

	public boolean isMostrarPanelTarjetas() {
		return mostrarPanelTarjetas;
	}

	public void setMostrarPanelTarjetas(boolean mostrarPanelTarjetas) {
		this.mostrarPanelTarjetas = mostrarPanelTarjetas;
	}

	public String getNumTarjeta() {
		return numTarjeta;
	}

	public void setNumTarjeta(String numTarjeta) {
		this.numTarjeta = numTarjeta;
	}

	public String getEntidadRelacionada() {
		return entidadRelacionada;
	}

	public void setEntidadRelacionada(String entidadRelacionada) {
		this.entidadRelacionada = entidadRelacionada;
	}

	public int getTipoDocSelected() {
		return tipoDocSelected;
	}

	public void setTipoDocSelected(int tipoDocSelected) {
		this.tipoDocSelected = tipoDocSelected;
	}

	public List<TipoDocumento> getListaTipoDocumento() {
		return listaTipoDocumento;
	}

	public void setListaTipoDocumento(List<TipoDocumento> listaTipoDocumento) {
		this.listaTipoDocumento = listaTipoDocumento;
	}

	public String getCampoDocumento() {
		return campoDocumento;
	}

	public void setCampoDocumento(String campoDocumento) {
		this.campoDocumento = campoDocumento;
	}

	public String getCampoNombreTarjeta() {
		return campoNombreTarjeta;
	}

	public void setCampoNombreTarjeta(String campoNombreTarjeta) {
		this.campoNombreTarjeta = campoNombreTarjeta;
	}

	public boolean isDeshabilitarBtnInscribir() {
		return deshabilitarBtnInscribir;
	}

	public void setDeshabilitarBtnInscribir(boolean deshabilitarBtnInscribir) {
		this.deshabilitarBtnInscribir = deshabilitarBtnInscribir;
	}

	public String getCampoTipoTarjeta() {
		return campoTipoTarjeta;
	}

	public void setCampoTipoTarjeta(String campoTipoTarjeta) {
		this.campoTipoTarjeta = campoTipoTarjeta;
	}

	public List<Usuario> getListaResultado() {
		return listaResultado;
	}

	public void setListaResultado(List<Usuario> listaResultado) {
		this.listaResultado = listaResultado;
	}

	public Principal getUsuarioLogin() {
		return usuarioLogin;
	}

	public void setUsuarioLogin(Principal usuarioLogin) {
		this.usuarioLogin = usuarioLogin;
	}

	public boolean isDisableCampoNumTarjeta() {
		return disableCampoNumTarjeta;
	}

	public void setDisableCampoNumTarjeta(boolean disableCampoNumTarjeta) {
		this.disableCampoNumTarjeta = disableCampoNumTarjeta;
	}

	public String getNumTarjetaEliminar() {
		return numTarjetaEliminar;
	}

	public void setNumTarjetaEliminar(String numTarjetaEliminar) {
		this.numTarjetaEliminar = numTarjetaEliminar;
	}

	public TarjetaPrepago getTarjetaPrepago() {
		return tarjetaPrepago;
	}

	public void setTarjetaPrepago(TarjetaPrepago tarjetaPrepago) {
		this.tarjetaPrepago = tarjetaPrepago;
	}

	public ParamBines getParamBines() {
		return paramBines;
	}

	public void setParamBines(ParamBines paramBines) {
		this.paramBines = paramBines;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getInitVector() {
		return initVector;
	}

	public void setInitVector(String initVector) {
		this.initVector = initVector;
	}

	public String getNumTarjetaEliminarEnmascarada() {
		return numTarjetaEliminarEnmascarada;
	}

	public void setNumTarjetaEliminarEnmascarada(
			String numTarjetaEliminarEnmascarada) {
		this.numTarjetaEliminarEnmascarada = numTarjetaEliminarEnmascarada;
	}

	protected Messages getMensaje() {
		if (mensaje == null) {
			mensaje = (Messages) findComponentInRoot("mensaje");
		}
		return mensaje;
	}

	protected CommandButton getBotonInscribir() {
		if (botonInscribir == null) {
			botonInscribir = (CommandButton) findComponentInRoot("botonInscribir");
		}
		return botonInscribir;
	}

	protected HtmlOutputLabel getBotonInscribirl() {
		if (botonInscribirl == null) {
			botonInscribirl = (HtmlOutputLabel) findComponentInRoot("botonInscribirl");
		}
		return botonInscribirl;
	}

	public String getTypeMsjCarga() {
		return typeMsjCarga;
	}

	public void setTypeMsjCarga(String typeMsjCarga) {
		this.typeMsjCarga = typeMsjCarga;
	}

	public String getMsjCarga() {
		return msjCarga;
	}

	public void setMsjCarga(String msjCarga) {
		this.msjCarga = msjCarga;
	}

	public List<Bolsillo> getListaBolsillos() {
		return listaBolsillos;
	}

	public void setListaBolsillos(List<Bolsillo> listaBolsillos) {
		this.listaBolsillos = listaBolsillos;
	}

	public String getBolsilloSelected() {
		return bolsilloSelected;
	}

	public void setBolsilloSelected(String bolsilloSelected) {
		this.bolsilloSelected = bolsilloSelected;
	}


}