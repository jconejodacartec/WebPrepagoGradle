package com.rbm.web.prepago.managedBeans;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;

import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.messages.Messages;
import org.primefaces.component.outputpanel.OutputPanel;
import org.primefaces.component.panel.Panel;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.ibm.ams.audit.dto.EventSuccessEnum;
import com.ibm.ams.audit.dto.EventTypeEnum;
import com.ibm.ams.audit.dto.SeverityEnum;
import com.ibm.ams.log.Logger;
import com.rbm.web.prepago.configuracion.ItemDescripcion;
import com.rbm.web.prepago.dto.BIN;
import com.rbm.web.prepago.dto.BinXBolsillo;
import com.rbm.web.prepago.dto.Bolsillo;
import com.rbm.web.prepago.dto.Entidad;
import com.rbm.web.prepago.dto.Parametro;
import com.rbm.web.prepago.dto.TipoTarjeta;
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
@ManagedBean
@SessionScoped
public class AdministrarParametrosWPBean extends PageCodeBase implements Serializable{

	private static Logger logger = Logger.getInstance(ConstantesWebPrepago.NOMBRE_APLICACION, ConstantesWebPrepago.DIRECTORIO_CONFIGURACION);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3185840703909536072L;
	private String ip;
	protected HtmlForm formularioAdminParametros;
	protected HtmlInputHidden ipValue;
	protected Panel panelGeneral;
	protected Messages mensaje;
	protected SelectOneMenu parametros;
	
	private int parametroSelected;
	private List<Entidad> listaEntidad = new ArrayList<Entidad>();
	private List<TipoTarjeta> listaTipoTarjeta = new ArrayList<TipoTarjeta>();
	private List<BIN> listaBIN = new ArrayList<BIN>();
	
	private boolean filtrosEntidad;
	private boolean filtrosTipoTarjeta;
	private boolean filtrosBIN;
	private boolean panelConsulta;
	private String headerFilstros;
	private boolean botonAgregar;
	private boolean botonModificar;
	
	protected OutputPanel panelFiltros;
	protected HtmlInputText campoFiid;
	protected HtmlInputText campoNomEntidad;
	protected HtmlInputText campoNombreTipoTar;
	protected HtmlInputText campoCodigoBin;
	protected HtmlInputText campoRangoIni;
	protected HtmlInputText campoRangoFin;
	protected HtmlInputText campoLongitudTar;
	
	private List<Parametro> listaParametro = new ArrayList<Parametro>();
	private Integer fiid;
	private String nombreEntidad;
	private int idEntModificar;
	private int codTransaccion;
	private String nombreTran;
	private int idCodModificar;
	private String nombreTranAS400;
	private String nombreTipoTar;
	private int idTipoTarModificar;
	private int codigoEstado;
	private String nombreEstadoTar;
	private String nombreEstadoAS400;
	private int idEstadoModificar;
	private long codigoBin;
	private int tipoTarjetaSelected;
	private int longitudTar;
	private int entidadSelected;
	protected SelectOneMenu tipoTarLista;
	protected DataTable tablaRegistrosEntidad;
	protected OutputPanel panelResultado;
	protected DataTable tablaRegistrosTipoTarjeta;
	protected DataTable tablaRegistrosBIN;
	private long rangoInicial;
	private long rangoFinal;
	private int idBinModificar;
	private FileItemStream imagenCarga = null;
	private String typeMsjCarga;
	private String msjCarga;
	protected HtmlPanelGrid imagenCargar;
	private String valorParam;
	private boolean botonAgregarBin;
	protected SelectOneMenu autocomEntidad;
	private boolean botonModificarBin;
	private String imgCargar;
	private StreamedContent imagenTarjeta;
	private DefaultStreamedContent imgdecode;
	static boolean errorSql;
	private byte[] file;
	private boolean mostrarPanelEliminarAuto;
	private String correosNotficar;
	private String diasNotificasSelected;
	private List<String> listaDiasNotificar = new ArrayList<String>();
	private String diasEliminarSelected;
	private List<String> listaDiasEliminar = new ArrayList<String>();
	private List<Parametro> listaParametros = new ArrayList<Parametro>();
	private boolean filtrosBolsillo;
	private String campoBolsillo;
	private int campoCodigoBolsillo;
	private List<Bolsillo> listaBolsillos = new ArrayList<Bolsillo>();
	private String idBolsilloModificar;
	private boolean filtrosBinxBolsillo;
	private ItemDescripcion itemBinEntidadSelected;
	private List<ItemDescripcion> listaBinEntidades = new ArrayList<ItemDescripcion>();
	private List<BinXBolsillo> listaBinBolsillos = new ArrayList<BinXBolsillo>();
	private String bolsilloSelected;
	private BinXBolsillo binXbolsilloModificar = new BinXBolsillo();
	private String nombreBolsilloModificar;
	

	public AdministrarParametrosWPBean() {
		
		try {

			
//			this.imagenTarjeta= new DefaultStreamedContent(new FileInputStream(path),"image/png");
			this.listaParametro.addAll(GestorCache.getParametros());
			this.listaEntidad.addAll(GestorCache.getEntidades());
			this.listaTipoTarjeta.addAll(GestorCache.getTipoTarjetas());
			this.listaBIN.addAll(GestorCache.getBin());
//			this.imagenTarjeta = new DefaultStreamedContent(new ByteArrayInputStream(listaBIN.get(0).getBuffer()));
//			this.imgCargar = ConstantesWebPrepago.IMAGENES_DIR+listaBIN.get(0).getImagen();
			this.filtrosBIN=false;
			this.filtrosEntidad=false;
			this.filtrosTipoTarjeta=false;
			this.panelConsulta=false;
			
			this.botonAgregarBin=false;
			this.botonModificarBin=false;
			this.mostrarPanelEliminarAuto=false;
			this.setFiltrosBolsillo(false);
//			this.imgdecode = new DefaultStreamedContent(new FileInputStream(ConstantesWebPrepago.IMAGENES_DIR+"tarjetaColsubsidio.png"),"");
//			contexto.update("imagendecode imagendecodePanel");
			this.listaParametros.addAll(GestorBD.getParametros());
			this.filtrosBinxBolsillo = false;
			llenarDiasEvaluacion();
		} catch (Exception e) {
			logger.error(this.getClass().getName(), "AdministrarParametrosWPBean", ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_ME002, e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_ME002, ""));
			GestorAuditoria.getInstance().registrarAuditoria(SeverityEnum.ERROR, EventSuccessEnum.SUCCESS.name(), 
					EventTypeEnum.ERROR.value(),ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_ME002,"AdministrarParametrosWPBean",this.ip,"WebPrepago");
		}
	}


	public void onParametroChange(){
		
		switch (this.parametroSelected) {
		case 1:
			try {
				this.listaEntidad = new ArrayList<Entidad>();
				this.listaEntidad.addAll(GestorCache.getEntidades());
				if (this.listaEntidad.isEmpty()) {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_MW002, ""));
				}
			} catch (Exception e) {
				logger.error(this.getClass().getName(), "onParametroChange", "Error al cargar registros de Entidades", e);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al cargar registros de Entidades", ""));
				GestorAuditoria.getInstance().registrarAuditoria(SeverityEnum.ERROR, EventSuccessEnum.SUCCESS.name(), 
						EventTypeEnum.ERROR.value(),"Error al cargar registros de Entidades","onParametroChange",this.ip,"WebPrepago");
			}
			this.panelConsulta=true;
			this.headerFilstros="Agregar Entidad";
			this.filtrosBIN=false;
			this.filtrosEntidad=true;
			this.filtrosTipoTarjeta=false;
			this.botonAgregar=true;
			this.botonModificar=false;
			this.botonAgregarBin=false;
			this.mostrarPanelEliminarAuto = false;
			this.setFiltrosBolsillo(false);
			this.filtrosBinxBolsillo = false;
			
			break;
		case 3:
			
			try {
				this.listaTipoTarjeta = new ArrayList<TipoTarjeta>();
				this.listaTipoTarjeta.addAll(GestorCache.getTipoTarjetas());
				if (this.listaTipoTarjeta.isEmpty()) {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_MW002, ""));
				}
			} catch (Exception e) {
				logger.error(this.getClass().getName(), "onParametroChange", "Error al cargar registros de Códigos de Transacción", e);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al cargar registros de Códigos de Transacción", ""));
				GestorAuditoria.getInstance().registrarAuditoria(SeverityEnum.ERROR, EventSuccessEnum.SUCCESS.name(), 
						EventTypeEnum.ERROR.value(),"Error al cargar registros de Códigos de Transacción","onParametroChange",this.ip,"WebPrepago");
			}
			this.panelConsulta=true;
			this.headerFilstros="Agregar Tipo Tarjeta";
			this.filtrosBIN=false;
			this.filtrosEntidad=false;
			this.filtrosTipoTarjeta=true;
			this.botonAgregar=true;
			this.botonModificar=false;
			this.botonAgregarBin=false;
			this.mostrarPanelEliminarAuto= false;
			this.setFiltrosBolsillo(false);
			this.filtrosBinxBolsillo = false;
			break;
		case 5:
			try {
				this.valorParam=String.valueOf(this.parametroSelected);
				this.listaBIN = new ArrayList<BIN>();
				this.listaBIN.addAll(GestorCache.getBin());
				if (this.listaBIN.isEmpty()) {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_MW002, ""));
				}
				this.listaTipoTarjeta = new ArrayList<TipoTarjeta>();
				this.listaTipoTarjeta.addAll(GestorCache.getTipoTarjetas());
				this.listaEntidad = new ArrayList<Entidad>();
				this.listaEntidad.addAll(GestorCache.getEntidades());
				
			} catch (Exception e) {
				logger.error(this.getClass().getName(), "onParametroChange", "Error al cargar registros de BINES", e);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al cargar registros de BINES", ""));
				GestorAuditoria.getInstance().registrarAuditoria(SeverityEnum.ERROR, EventSuccessEnum.SUCCESS.name(), 
						EventTypeEnum.ERROR.value(),"Error al cargar registros de BINES","onParametroChange",this.ip,"WebPrepago");
			}
			this.panelConsulta=true;
			this.headerFilstros="Agregar BIN";
			this.filtrosBIN=true;
			this.filtrosEntidad=false;
			this.filtrosTipoTarjeta=false;
			this.botonAgregar=false;
			this.botonAgregarBin=true;
			this.botonModificar=false;
			this.mostrarPanelEliminarAuto = false;
			this.setFiltrosBolsillo(false);
			this.filtrosBinxBolsillo = false;
			break;
		case 6:
			this.mostrarPanelEliminarAuto=true;
			this.panelConsulta=true;
			this.headerFilstros="Eliminación automática usuarios inactivos";
			this.filtrosBIN=false;
			this.filtrosEntidad=false;
			this.filtrosTipoTarjeta=false;
			this.botonAgregar=false;
			this.botonModificar=false;
			this.botonAgregarBin=false;
			this.filtrosBolsillo=false;
			llenarCamposPantallaTarea();
			this.filtrosBinxBolsillo = false;
			break;
		case 7:
			this.listaBolsillos = new ArrayList<Bolsillo>();
			this.mostrarPanelEliminarAuto=false;
			this.panelConsulta=true;
			this.headerFilstros="Agregar Bolsillo";
			this.filtrosBIN=false;
			this.filtrosEntidad=false;
			this.filtrosTipoTarjeta=false;
			this.botonAgregar=true;
			this.botonModificar=false;
			this.botonAgregarBin=false;
			this.setFiltrosBolsillo(true);
			this.filtrosBinxBolsillo = false;
			try {
				this.listaBolsillos.addAll(GestorCache.getBolsillos());
			} catch (Exception e) {
				logger.error(this.getClass().getName(), "onParametroChange", "Error al cargar registros de bolsillos", e);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al cargar registros de bolsillos", ""));
				GestorAuditoria.getInstance().registrarAuditoria(SeverityEnum.ERROR, EventSuccessEnum.SUCCESS.name(), 
						EventTypeEnum.ERROR.value(),"Error al cargar registros de bolsillos","onParametroChange",this.ip,"WebPrepago");
			}
			break;
		case 8:
			this.listaBolsillos = new ArrayList<Bolsillo>();
			this.listaBinBolsillos = new ArrayList<BinXBolsillo>();
			this.mostrarPanelEliminarAuto=false;
			this.panelConsulta=true;
			this.headerFilstros="Agregar Asociar Bin Bolsillo";
			this.filtrosBIN=false;
			this.filtrosEntidad=false;
			this.filtrosTipoTarjeta=false;
			this.botonAgregar=true;
			this.botonModificar=false;
			this.botonAgregarBin=false;
			this.filtrosBolsillo= false;
			this.filtrosBinxBolsillo = true;
			try {
				this.listaBolsillos.addAll(GestorCache.getBolsillos());
				this.listaBinBolsillos.addAll(GestorCache.getBinXBolsillos());
				llenarBinEntidades();
			} catch (Exception e) {
				logger.error(this.getClass().getName(), "onParametroChange", "Error al cargar registros de bin x bolsillo", e);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al cargar registros de bin por bolsillo", ""));
				GestorAuditoria.getInstance().registrarAuditoria(SeverityEnum.ERROR, EventSuccessEnum.SUCCESS.name(), 
						EventTypeEnum.ERROR.value(),"Error al cargar registros de bolsillos por bin","onParametroChange",this.ip,"WebPrepago");
			}
			break;
		case 0:
			this.mostrarPanelEliminarAuto=false;
			this.panelConsulta=false;
			this.filtrosBIN=false;
			this.filtrosEntidad=false;
			this.filtrosTipoTarjeta=false;
			this.botonAgregar=false;
			this.botonModificar=false;
			this.botonAgregarBin=false;
			this.setFiltrosBolsillo(false);
			this.filtrosBinxBolsillo = false;
			break;
		}
	}
	

	
	public void modificar(Entidad enti){
			this.headerFilstros="Modificar Entidad";
			this.botonAgregar=false;
			this.botonModificar=true;
			this.fiid=enti.getCodigo();
			this.nombreEntidad=enti.getNombre();
			this.idEntModificar=enti.getId();
	}
	
	public void modificarTipoTar(TipoTarjeta tipoTarjeta){
		this.headerFilstros="Modificar Tipo Tarjeta";
		this.botonAgregar=false;
		this.botonModificar=true;
		this.nombreTipoTar= tipoTarjeta.getNombre();
		this.idTipoTarModificar= tipoTarjeta.getId();
	}
	
	public void modificarBin(BIN bin){
		this.headerFilstros="Modificar BIN";
		this.botonAgregar=false;
		this.botonModificar=false;
		this.codigoBin= bin.getNumero();
		this.rangoInicial= bin.getRangoIni();
		this.rangoFinal= bin.getRangoFin();
		this.longitudTar= bin.getLongitud();
		this.entidadSelected= bin.getEntidad().getId();
		this.tipoTarjetaSelected= bin.getTipoTarjeta().getId();
		this.idBinModificar=bin.getId();
		this.botonAgregarBin=false;
		this.botonModificarBin=true;
		
	}
	
	public void agregarParametro(){
//		RequestContext contexto = RequestContext.getCurrentInstance();
		try {
			
			logger.debug(this.getClass().getName(), "agregarParametro", "entra a agregarParametro()");
			boolean existe= false;
			
			if (validarCampos()) {
				switch (this.parametroSelected) {
				//TODO cambiar los numeros del switch por constantes
				case 1:
					for (Entidad entidad : this.listaEntidad) {
						if (entidad.getCodigo() == this.fiid.intValue() || entidad.getNombre().equals(this.nombreEntidad)) {
							existe=true;
							break;
						}
					}
					if (!existe) {
						Entidad entidad = new Entidad();
						this.listaEntidad= new ArrayList<Entidad>();
						entidad.setCodigo(this.fiid);
						entidad.setNombre(this.nombreEntidad);
						GestorBD.registrarEntidad(entidad);
						listaEntidad.addAll(GestorBD.getEntidades());
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_MI001, ""));
						logger.info(this.getClass().getName(), "agregarParametro", ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_MI001);
						limpiarCampos();
					}else{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_ME001, ""));
						logger.info(this.getClass().getName(), "agregarParametro", ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_ME001);
					}
					break;

				case 3:
					for (TipoTarjeta tarjeta : this.listaTipoTarjeta) {
						if (tarjeta.getNombre().equals(this.nombreTipoTar)) { //TODO jb:  equalsIgnoreCase?
							existe=true;
							break;
						}
					}
					if (!existe) {
						TipoTarjeta tipoTar = new TipoTarjeta();
						this.listaTipoTarjeta = new ArrayList<TipoTarjeta>();
						tipoTar.setNombre(this.nombreTipoTar);
						GestorBD.registrarTipoTarjeta(tipoTar);
						this.listaTipoTarjeta.addAll(GestorBD.getTipoTar());
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_MI001, ""));
						limpiarCampos();
					}else{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_ME001, ""));
						logger.info(this.getClass().getName(), "agregarParametro", ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_ME001);
					}
					break;
				case 5:
					if(!validaRango(String.valueOf(this.rangoInicial)) && !validaRango(String.valueOf(this.rangoFinal))){
						for (BIN bin : this.listaBIN) {
							if ((this.codigoBin==bin.getNumero() && this.entidadSelected==bin.getEntidad().getId()) || (this.codigoBin==bin.getNumero())) {
								existe=true;
								break;
							}
						}
						if (!GestorBD.consultarExisteBinBPO(this.codigoBin, this.longitudTar)) {
							this.setTypeMsjCarga("error");
							this.setMsjCarga("El codigo BIN no existe en el sistema BPO");
//							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "El codigo BIN no existe en el sistema BPO", ""));
						} else {
							if (!existe) {
								BIN bin = new BIN();
								this.listaBIN = new ArrayList<BIN>();
								bin.setNumero((this.codigoBin));
								bin.setRangoIni(this.rangoInicial);
								bin.setRangoFin(this.rangoFinal);
								bin.setLongitud(this.longitudTar);
								Entidad entidad = new Entidad();
								entidad.setCodigo(this.entidadSelected);
								bin.setEntidad(entidad);
								TipoTarjeta tipoTarjeta = new TipoTarjeta();
								tipoTarjeta.setId(this.tipoTarjetaSelected);
								bin.setTipoTarjeta(tipoTarjeta);
								bin.setImagen(this.imagenCarga.getName());
								GestorBD.registrarBin(bin);
								this.listaBIN.addAll(GestorBD.getBines());
								this.setTypeMsjCarga("info");
								this.setMsjCarga(ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_MI001);
								limpiarCampos();
							}else{
								this.setTypeMsjCarga("error");
								this.setMsjCarga(ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_ME001);
							}
						}

					}else{
						this.setTypeMsjCarga("error");
						this.setMsjCarga("Rango Inicial o Rango Final se encuentran entre Rangos ya existentes");
//						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Rango Inicial o Rango Final se encuentran entre Rangos ya existentes", ""));
					}
					break;
				case 7:
					for(Bolsillo bol: this.listaBolsillos) {
						if(bol.getId().equals(Integer.toString(this.campoCodigoBolsillo)) || bol.getNombre().equals(this.campoBolsillo)) {
							existe=true;
							break;
						}
					}
					if(!existe) {
						Bolsillo bolsillo = new Bolsillo();
						this.listaBolsillos = new ArrayList<Bolsillo>();
						bolsillo.setId(Integer.toString(this.campoCodigoBolsillo));
						bolsillo.setNombre(this.campoBolsillo);
						GestorBD.registrarBolsillo(bolsillo);
						this.listaBolsillos.addAll(GestorBD.getBolsillos());
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_MI001, ""));
						limpiarCampos();
					}else {
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_ME001, ""));
						logger.info(this.getClass().getName(), "agregarParametro", ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_ME001);
					}
					
					break;
				case 8:
					for(BinXBolsillo item : this.listaBinBolsillos) {
						if(this.itemBinEntidadSelected.getCodigo().equals(item.getBin()) && this.bolsilloSelected.equals(String.valueOf(item.getCodigoBolsillo()))) {
							existe = true;
							break;
						}
					}
					if(!existe) {
						BinXBolsillo xBolsillo = new BinXBolsillo();
						this.listaBinBolsillos = new ArrayList<BinXBolsillo>();
						xBolsillo.setBinID(obtenerIdBin(this.itemBinEntidadSelected.getCodigo()));
						xBolsillo.setCodigoBolsillo(Integer.parseInt(this.bolsilloSelected));
						GestorBD.registrarBinXBolsillo(xBolsillo);
						this.listaBinBolsillos.addAll(GestorBD.getBinXBolsillos());
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_MI001, ""));
						limpiarCampos();
					}else {
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_ME001, ""));
						logger.info(this.getClass().getName(), "agregarParametro", ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_ME001);
					}
					break;
				}
			}
		} catch (Exception e) {
			logger.error(this.getClass().getName(), "agregarParametro", "error registrando parametro", e);
			this.setTypeMsjCarga("error");
			this.setMsjCarga("Error al consultar en la base de datos BPO");
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al consultar en la base de datos", ""));
			GestorAuditoria.getInstance().registrarAuditoria(SeverityEnum.ERROR, EventSuccessEnum.SUCCESS.name(), 
					EventTypeEnum.ERROR.value(),"Error al consultar en la base de datos","onParametroChange",this.ip,"WebPrepago");
		}
		GestorCache.limpiarCache();
	}



	private boolean validarCampos() {
		String mensaje="";
		boolean valida = true;
		
		switch (this.parametroSelected) {
		case 1:
			if ( this.fiid==null || this.fiid.equals(0) ) {
				mensaje+="Código Compensación (FIID),";
				valida = false;
			}
			if (this.nombreEntidad.isEmpty() || this.nombreEntidad==null) {
				mensaje+=" Nombre Entidad";
				valida = false;
			}
			break;
		case 3:
			if (this.nombreTipoTar.isEmpty() || this.nombreTipoTar==null) {
				mensaje+="Nombre del Tipo de Tarjeta";
				valida = false;
			}
			
			break;
		case 5:
			if (this.codigoBin==0) {
				mensaje+="Código BIN, ";
				valida = false;
			}
			if (this.rangoInicial==0) {
				mensaje+="Rango Inicial, ";
				valida = false;
			}
			if (this.rangoFinal==0) {
				mensaje+="Rango Final, ";
				valida = false;
			}
			if (this.rangoInicial==0) {
				mensaje+="Rango Inicial, ";
				valida = false;
			}
			if (this.longitudTar==0) {
				mensaje+="Longitud Tarjeta, ";
				valida = false;
			}
			if (this.entidadSelected==0) {
				mensaje+="Entidad, ";
				valida = false;
			}
			if (this.tipoTarjetaSelected==0) {
				mensaje+="Tipo Tarjeta, ";
				valida = false;
			}
			/*if (this.imagen==0) {
				mensaje+="Imagen";
				valida = false;
			}*/
			break;
		case 6:
			if(this.diasNotificasSelected.isEmpty() || this.diasNotificasSelected == null) {
				mensaje+= "días evaluación notificación, ";
				valida = false;
			}
			if(this.diasEliminarSelected.isEmpty() || this.diasEliminarSelected == null) {
				mensaje+= "días evaluación eliminación, ";
				valida = false;
			}
			if(this.correosNotficar.isEmpty() || this.correosNotficar == null) {
				mensaje+= "Correo electrónico, ";
				valida = false;
			}else {
				 String [] items = this.correosNotficar.split(";");
				  List<String> container = Arrays.asList(items);
				  for(String email : container ) {
				  	Pattern patronEmail = Pattern.compile("^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@" +
				  	      "[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4})$");
			        Matcher mEmail = patronEmail.matcher(email.toLowerCase());
			        if (!mEmail.matches()){
			        	mensaje+= "Estructura invalida del correo";
			        	valida =  false;
			        }
				  }
			}
			break;
		case 7:
			if(this.campoBolsillo.isEmpty() || this.campoBolsillo == null) {
				mensaje +="Bolsillo, ";
				valida= false;
			}
			if(this.campoCodigoBolsillo == 0) {
				mensaje +="Código Bolsillo, ";
				valida= false;
			}
			break;
		case 8:
			if(this.itemBinEntidadSelected == null || this.itemBinEntidadSelected.getCodigo() == null) {
				mensaje+="BIN, ";
				valida = false;
			}
			if(this.bolsilloSelected.isEmpty() || this.bolsilloSelected==null) {
				mensaje+="Bolsillo, ";
				valida = false;
			}
			break;
		}
		if (!valida) {
			this.setTypeMsjCarga("error");
			mensaje = mensaje.substring(0, mensaje.length()-2);
			this.setMsjCarga(ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_ME004 + mensaje);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_ME004 +" "+ mensaje + ".", ""));
		}
		return valida;
	}

	public void modificarParametro(){
		boolean existe= false;
		
		if (validarCampos()) {
			switch (this.parametroSelected) {
			case 1:
				for (Entidad entidad : this.listaEntidad) {
					if ((entidad.getCodigo()==Integer.valueOf(this.fiid) || entidad.getNombre().equals(this.nombreEntidad)) && entidad.getId()!= this.idEntModificar) {
						existe=true;
						break;
					}
				}
				if (!existe) {
					Entidad entidad = new Entidad();
					this.listaEntidad= new ArrayList<Entidad>();
					try {
						entidad.setCodigo(this.fiid);
						entidad.setNombre(this.nombreEntidad);
						entidad.setId(this.idEntModificar);
						GestorBD.modificarEntidad(entidad);
						listaEntidad.addAll(GestorBD.getEntidades());
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_MI001, ""));
						limpiarCampos();
						this.headerFilstros="Agregar Entidad";
					} catch (Exception e) {
						logger.error(this.getClass().getName(), "modificarParametro", "Error al modificar Entidad", e);
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al modificar Entidad", ""));
						GestorAuditoria.getInstance().registrarAuditoria(SeverityEnum.ERROR, EventSuccessEnum.SUCCESS.name(), 
								EventTypeEnum.ERROR.value(),"Error al modificar Entidad","modificarParametro",this.ip,"WebPrepago");
					}
				}else{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_ME001, ""));
				}
				break;

			case 3:
				for (TipoTarjeta tarjeta : this.listaTipoTarjeta) {
					if (tarjeta.getNombre().equals(this.nombreTipoTar) && tarjeta.getId()==this.idTipoTarModificar) {
						existe=true;
						break;
					}
				}
				if (!existe) {
					TipoTarjeta tipoTar = new TipoTarjeta();
					this.listaTipoTarjeta= new ArrayList<TipoTarjeta>();
					try {
						tipoTar.setId(this.idTipoTarModificar); 
						tipoTar.setNombre(this.nombreTipoTar);
						GestorBD.modificarTipoTarjeta(tipoTar);
						listaTipoTarjeta.addAll(GestorBD.getTipoTar());
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_MI001, ""));
						limpiarCampos();
						this.headerFilstros="Agregar Tipo Tarjeta";
					} catch (Exception e) {
						logger.error(this.getClass().getName(), "modificarParametro", "Error al modificar tipo Tarjeta", e);
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al modificar tipo Tarjeta", ""));
						GestorAuditoria.getInstance().registrarAuditoria(SeverityEnum.ERROR, EventSuccessEnum.SUCCESS.name(), 
								EventTypeEnum.ERROR.value(),"Error al cargar registros de Entidades","modificarParametro",this.ip,"WebPrepago");
					}
				}else{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_ME001, ""));
				}
				break;
			case 5:
				if((!validaRango(String.valueOf(this.rangoInicial)) || !validaRango(String.valueOf(this.rangoFinal)))){
					for (BIN bin : this.listaBIN) {
						if (((this.codigoBin==bin.getNumero() && this.entidadSelected==bin.getEntidad().getId()) || (this.codigoBin==bin.getNumero()))&& this.idBinModificar!=bin.getId()) {
							existe=true;
							break;
						}
					}
					if (!existe) {
						BIN bin = new BIN();
						this.listaBIN= new ArrayList<BIN>();
						try {
							bin.setId(this.idBinModificar);
							bin.setNumero((this.codigoBin));
							bin.setRangoIni(this.rangoInicial);
							bin.setRangoFin(this.rangoFinal);
							bin.setLongitud(this.longitudTar);
							Entidad entidad = new Entidad();
							entidad.setId(this.entidadSelected);
							bin.setEntidad(entidad);
							TipoTarjeta tipoTarjeta = new TipoTarjeta();
							tipoTarjeta.setId(this.tipoTarjetaSelected);
							bin.setTipoTarjeta(tipoTarjeta);
							bin.setImagen(this.imagenCarga.getName());
							GestorBD.modificarBin(bin);
							listaBIN.addAll(GestorBD.getBines());

							this.setTypeMsjCarga("info");
							this.setMsjCarga(ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_MI001);
							//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_MI001, ""));
							limpiarCampos();
							this.headerFilstros="Agregar BIN";
						} catch (Exception e) {
							this.setTypeMsjCarga("error"); 
							this.setMsjCarga("Error al modificar BIN");
							logger.error(this.getClass().getName(), "modificarParametro", "Error al modificar BIN", e);
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al modificar BIN", ""));
							GestorAuditoria.getInstance().registrarAuditoria(SeverityEnum.ERROR, EventSuccessEnum.SUCCESS.name(), 
									EventTypeEnum.ERROR.value(),"Error al modificar BIN","modificarParametro",this.ip,"WebPrepago");
						}
					}else{

						this.setTypeMsjCarga("error");
						this.setMsjCarga(ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_ME001);
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_ME001, ""));
					}
				}else{
					this.setTypeMsjCarga("error");
					this.setMsjCarga("Rango Inicial o Rango Final se encuentran entre Rangos ya existentes");
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Rango Inicial o Rango Final se encuentran entre Rangos ya existentes", ""));
				}
				break;
			case 7:
				for(Bolsillo bol: this.listaBolsillos) {
					if((bol.getId().equals(Integer.toString(this.campoCodigoBolsillo))&& !Integer.toString(this.campoCodigoBolsillo).equals(idBolsilloModificar))
								|| bol.getNombre().equals(this.campoBolsillo) && !this.campoBolsillo.equals(nombreBolsilloModificar)) {
						existe=true;
						break;
					}
				}
				if(!existe) {
					try {
						this.listaBolsillos = new ArrayList<Bolsillo>();
						Bolsillo bolsillo = new Bolsillo();
						bolsillo.setId(Integer.toString(this.campoCodigoBolsillo));
						bolsillo.setNombre(this.campoBolsillo);
						GestorBD.modificarBolsillo(bolsillo,this.idBolsilloModificar);
						this.listaBolsillos.addAll(GestorBD.getBolsillos());
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_MI001, ""));
						limpiarCampos();
					} catch (Exception e) {
						logger.error(this.getClass().getName(), "modificarParametro", "Error al modificar bolsillo", e);
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al modificar tipo bolsillo", ""));
						GestorAuditoria.getInstance().registrarAuditoria(SeverityEnum.ERROR, EventSuccessEnum.SUCCESS.name(), 
								EventTypeEnum.ERROR.value(),"Error al cargar registros de bolsillo","modificarParametro",this.ip,"WebPrepago");
					}
				}else {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_ME001, ""));
					logger.info(this.getClass().getName(), "agregarParametro", ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_ME001);
				}
				break;
			case 8:
	
				for(BinXBolsillo item : this.listaBinBolsillos) {
					if(this.itemBinEntidadSelected.getCodigo().equals(item.getBin()) && this.bolsilloSelected.equals(String.valueOf(item.getCodigoBolsillo()))) {
						//if(!this.binXbolsilloModificar.getBin().equals(item.getBin()) && this.binXbolsilloModificar.getCodigoBolsillo()!=item.getCodigoBolsillo()) {
							existe = true;
							break;
						//}
					}
				}
				if(!existe) {
					try {
						this.listaBinBolsillos = new ArrayList<BinXBolsillo>();
						BinXBolsillo binXBolsillo = new BinXBolsillo();
						binXBolsillo.setBinID(obtenerIdBin(this.itemBinEntidadSelected.getCodigo()));
						binXBolsillo.setCodigoBolsillo(Integer.parseInt(this.bolsilloSelected));
						GestorBD.modificarBinXBolsillo(binXBolsillo, this.binXbolsilloModificar.getBinID(),this.binXbolsilloModificar.getCodigoBolsillo() );
						this.listaBinBolsillos.addAll(GestorBD.getBinXBolsillos());
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_MI001, ""));
						limpiarCampos();
					} catch (Exception e) {
						logger.error(this.getClass().getName(), "modificarParametro", "Error al modificar bin bolsillo", e);
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al modificar bin bolsillo", ""));
						GestorAuditoria.getInstance().registrarAuditoria(SeverityEnum.ERROR, EventSuccessEnum.SUCCESS.name(), 
								EventTypeEnum.ERROR.value(),"Error al cargar registros de bin bolsillo","modificarParametro",this.ip,"WebPrepago");
					}
				}else {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_ME001, ""));
					logger.info(this.getClass().getName(), "agregarParametro", ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_ME001);
				}
				break;
			}
			GestorCache.limpiarCache();
		}
		
	}
	
	public void limpiarCampos(){
		this.fiid=0;
		this.nombreEntidad=null;
		this.botonModificar=false;
		this.codTransaccion=0;
		this.nombreTran=null;
		this.nombreTipoTar=null;
		this.nombreTranAS400=null;
		this.nombreEstadoTar=null;
		this.nombreEstadoAS400=null;
		this.codigoEstado=0;
		this.codigoBin=0;
		this.rangoInicial=0;
		this.rangoFinal=0;
		this.longitudTar=0;
		this.entidadSelected=0;
		this.tipoTarjetaSelected=0;
		this.campoBolsillo=null;
		this.campoCodigoBolsillo=0;
		
		if(this.parametroSelected==5){
			this.botonAgregarBin=true;
			this.botonAgregar=false;
		}else{
			this.botonAgregarBin=false;
			this.botonAgregar=true;
		}
		this.botonModificarBin=false;
		this.itemBinEntidadSelected = null;
		this.bolsilloSelected = null;
	}
	
	public void eliminarEntidad(Entidad enti){
		try {
			GestorBD.eliminarEntidad(enti.getCodigo());
			this.listaEntidad=new ArrayList<Entidad>();
			this.listaEntidad.addAll(GestorBD.getEntidades());
			GestorCache.limpiarCache();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Se eliminó el registro exitosamente", ""));
		} catch (Exception e) {
			logger.error(this.getClass().getName(), "eliminarEntidad", "Error al eliminar el registro", e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al eliminar el registro", ""));
			GestorAuditoria.getInstance().registrarAuditoria(SeverityEnum.ERROR, EventSuccessEnum.SUCCESS.name(), 
					EventTypeEnum.ERROR.value(),"Error al eliminar el registro","eliminarEntidad",this.ip,"WebPrepago");
		}
	}
	
	
	/*public List<ItemDescripcion> completeItemEntidad(String query){
		List<ItemDescripcion> results = new ArrayList<ItemDescripcion>();
		List<Entidad> enti = new ArrayList<Entidad>();
			
		try {
			enti.addAll(GestorCache.getEntidades());
			List<ItemDescripcion> entidadesReloaded = new ArrayList<ItemDescripcion>();
			
			for(Entidad entidad: enti){
				ItemDescripcion item = new ItemDescripcion();
				
				item.setCodigo(entidad.getCodigo()+"");
				item.setNombre(entidad.getNombre());
				
				entidadesReloaded.add(item);
			}      
			
			for(ItemDescripcion item : entidadesReloaded){
				if(item.getNombre().toLowerCase().contains(query.toLowerCase())){
					results.add(item);
					//break;
				}
			}
			return results;
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
					"Error al cargar la ENTIDAD, por favor consulte con el Administrador del Sistema", 
					""));
		}	
		
		return null;
	}*/
	
	
	public void validarLongitud(){
		if (this.rangoFinal!=0 && this.rangoInicial!=0) {
			String x = String.valueOf(this.rangoInicial);
			String y = String.valueOf(this.rangoFinal);
			if (x.length()<13 || y.length()<13) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, 
						"La longitud minima para Rango Inicial y Final debe ser de 13 digitos", 
						""));
			} else {
				if (x.length()==y.length()) {
					this.longitudTar = x.length();
				} else {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, 
							"La longitud de Rango Inicial y Rango final no coinciden", 
							""));
				}
			}
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
					"No ha ingresado el campo Rango Inicial o Rango Final", 
					""));
		}
	}
	
	public void x(){
		
	}
	
	
	public boolean validaRango(String rangoNum){
		boolean existe= false;
		
		if (!this.botonModificarBin) {
			for (BIN bin : this.listaBIN) {
				if(Long.parseLong(rangoNum)>=bin.getRangoIni() && Long.parseLong(rangoNum)<=bin.getRangoFin()){
					existe = true;
					break;
				}
			}
		} else {
			for (BIN bin : this.listaBIN) {
				if(Long.parseLong(rangoNum)>=bin.getRangoIni() && Long.parseLong(rangoNum)<=bin.getRangoFin() && bin.getId()!=this.idBinModificar){
					existe = true;
					break;
				}
			}
		}
		
		return existe;
	}
	
	public void botonCancelar(){
		limpiarCampos();
		this.filtrosBIN=false;
		this.filtrosEntidad=false;
		this.filtrosTipoTarjeta=false;
		this.panelConsulta=false;
		this.filtrosBolsillo = false;
		this.parametroSelected=0;
		this.filtrosBinxBolsillo = false;
	}
	
	
	public String cargarArchivo(FileItemStream inputStream) throws IOException, ExceptionWebPrepago {
		
		String mensaje = null;
		this.setImagenCarga(null);
		if (inputStream.getName()!=null && !inputStream.getName().isEmpty()) {
			String[] nombre=inputStream.getName().split("\\.");
			if(nombre[nombre.length-1].equalsIgnoreCase("png") || nombre[nombre.length-1].equalsIgnoreCase("jpg")){
				this.setImagenCarga(inputStream);
				guardar();
//				GestorBD.registrarBin(this.imagenCarga.getName());
				if (!this.botonModificarBin) {
					agregarParametro();
				}else{
					modificarParametro();
				}
				
			}else{
				mensaje="Tipo archivo no válido";
				this.setTypeMsjCarga("error");
				this.setMsjCarga(mensaje);
			}
		}else {
			mensaje="No ha seleccionado una imagen";
			this.setTypeMsjCarga("error");
			this.setMsjCarga(mensaje);
		}
		return mensaje;
	}
	
	
	
	
	public void guardar(){
		if (this.imagenCarga!=null) {
			try {
				this.file = IOUtils.toByteArray(this.imagenCarga.openStream());
				FileUtils.writeByteArrayToFile(new File(ConstantesWebPrepago.IMAGENES_DIR+ this.imagenCarga.getName()), file);
			} catch (IOException e) {	
				logger.error(this.getClass().getName(), "guardar", ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_ME002, e);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
						ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_ME002, ""));
				GestorAuditoria.getInstance().registrarAuditoria(SeverityEnum.ERROR, EventSuccessEnum.SUCCESS.name(), 
						EventTypeEnum.ERROR.value(),ConstantesWebPrepago.ADMINISTRAR_PARAMETROS_ME002,"guardar",this.ip,"WebPrepago");
			} 
		}
	}
	
	public void clean() {
		this.typeMsjCarga = "";
		this.msjCarga = "";
	}
	
	public void llenarDiasEvaluacion() {
		for(Parametro param: this.listaParametros) {
			if(param.getParametroNombre().equals(ConstantesWebPrepago.PARAMETRO_DIAS_NOTIFICAR)) {
				this.listaDiasNotificar = Arrays.asList(param.getParametroValor().split(","));
			}else if(param.getParametroNombre().equals(ConstantesWebPrepago.PARAMETRO_DIAS_ELIMINAR)) {
				this.listaDiasEliminar = Arrays.asList(param.getParametroValor().split(","));
			}
		}
	}
	
	public void llenarCamposPantallaTarea() {
		for(Parametro param: this.listaParametros) {
			if(param.getParametroNombre().equals(ConstantesWebPrepago.PARAMETRO_DIAS_NOTIFICAR_TAREA)) {
				this.diasNotificasSelected = param.getParametroValor();
			}else if(param.getParametroNombre().equals(ConstantesWebPrepago.PARAMETRO_DIAS_ELIMINAR_TAREA)) {
				this.diasEliminarSelected = param.getParametroValor();
			}else if(param.getParametroNombre().equals(ConstantesWebPrepago.PARAMETRO_CORREOS_TAREA)) {
				this.correosNotficar = (param.getParametroValor().trim().equals(""))?"":param.getParametroValor();
			}
		}
	}
	
	public void confirmarParametrosTarea() {
		try {
			if (validarCampos()) {
				GestorBD.guardarParametrosTarea(this.diasNotificasSelected, this.diasEliminarSelected, this.correosNotficar);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Parámetros registrados exitosamente", ""));
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al registrar parametros", ""));
			logger.error(this.getClass().getName(), "agregarParametro", "error registrando parametro", e);
			GestorAuditoria.getInstance().registrarAuditoria(SeverityEnum.ERROR, EventSuccessEnum.SUCCESS.name(), 
					EventTypeEnum.ERROR.value(),"Error al cargar registros de Entidades","onParametroChange",this.ip,"WebPrepago");
		}
	}
	
	public void modificarBolsillo(Bolsillo bolsillo) {
		this.idBolsilloModificar = bolsillo.getId();
		this.nombreBolsilloModificar = bolsillo.getNombre();
		this.headerFilstros="Modificar Bolsillo";
		this.botonAgregar=false;
		this.botonModificar=true;
		this.campoCodigoBolsillo = Integer.parseInt(bolsillo.getId());
		this.campoBolsillo = bolsillo.getNombre();
	}
	
	public List<ItemDescripcion> completeItem(String query) {
		List<ItemDescripcion> results = new ArrayList<ItemDescripcion>(); 
		for(ItemDescripcion item: this.listaBinEntidades) {
			if(item.getCodigo().toLowerCase().contains(query.toLowerCase())){
				item.setNombre(item.getNombre().contains("-")?item.getNombre():"-"+item.getNombre());
				results.add(item);
			}
		}
		return results;
	}
	
	public void llenarBinEntidades() {
		this.listaBinEntidades = new ArrayList<ItemDescripcion>();
		for(BIN itembin: this.listaBIN) {
			ItemDescripcion item = new ItemDescripcion(Long.toString(itembin.getNumero()),itembin.getEntidad().getNombre());
			this.listaBinEntidades.add(item);
		}
		
	}
	
	public void modificarBinBolsillo(BinXBolsillo binXbol) {
		this.binXbolsilloModificar = new BinXBolsillo();
		this.binXbolsilloModificar = binXbol;
		this.headerFilstros="Modificar Asociar Bin Bolsillo";
		this.botonAgregar=false;
		this.botonModificar=true;
		this.itemBinEntidadSelected = new ItemDescripcion(binXbol.getBin(), "-"+binXbol.getEntidad());
		this.bolsilloSelected = Integer.toString(binXbol.getCodigoBolsillo());
	}
	
	public void eliminarBinXBolsillo(BinXBolsillo binXbol) {
		try {
			GestorBD.eliminarBinXBolsillo(binXbol);
			this.listaBinBolsillos = new ArrayList<BinXBolsillo>();
			this.listaBinBolsillos.addAll(GestorBD.getBinXBolsillos());
			GestorCache.limpiarCache();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Se eliminó el registro exitosamente", ""));
		} catch (Exception e) {
			logger.error(this.getClass().getName(), "eliminarBinXBolsillo", "Error al eliminar el registro", e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al eliminar el registro", ""));
			GestorAuditoria.getInstance().registrarAuditoria(SeverityEnum.ERROR, EventSuccessEnum.SUCCESS.name(), 
					EventTypeEnum.ERROR.value(),"Error al eliminar el registro","eliminarBinXBolsillo",this.ip,"WebPrepago");
		}
	}
	
	public int obtenerIdBin(String bin) {
		int idBin = 0;
		for(BIN itemBin : this.listaBIN) {
			if(bin.equals(Long.toString(itemBin.getNumero()))) {
				idBin = itemBin.getId();
				break;
			}
		}
		return idBin;
	}
	
	
	public void eliminarBolsillo(Bolsillo bol) {
		try {
			GestorBD.eliminarBolsillo(bol);
			this.listaBolsillos = new ArrayList<Bolsillo>();
			this.listaBolsillos.addAll(GestorBD.getBolsillos());
			GestorCache.limpiarCache();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Se eliminó el registro exitosamente", ""));
		} catch (Exception e) {
			logger.error(this.getClass().getName(), "eliminarBolsillo", "Error al eliminar el registro", e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al eliminar el registro", ""));
			GestorAuditoria.getInstance().registrarAuditoria(SeverityEnum.ERROR, EventSuccessEnum.SUCCESS.name(), 
					EventTypeEnum.ERROR.value(),"Error al eliminar el registro","eliminarBolsillo",this.ip,"WebPrepago");
		}
	}
	
	public void clicBin() {
		
	}
	
	protected HtmlForm getFormularioAdminParametros() {
		if (formularioAdminParametros == null) {
			formularioAdminParametros = (HtmlForm) findComponentInRoot("formularioAdminParametros");
		}
		return formularioAdminParametros;
	}

	protected HtmlInputHidden getIpValue() {
		if (ipValue == null) {
			ipValue = (HtmlInputHidden) findComponentInRoot("ipValue");
		}
		return ipValue;
	}

	protected Panel getPanelGeneral() {
		if (panelGeneral == null) {
			panelGeneral = (Panel) findComponentInRoot("panelGeneral");
		}
		return panelGeneral;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	protected Messages getMensaje() {
		if (mensaje == null) {
			mensaje = (Messages) findComponentInRoot("mensaje");
		}
		return mensaje;
	}

	protected SelectOneMenu getParametros() {
		if (parametros == null) {
			parametros = (SelectOneMenu) findComponentInRoot("parametros");
		}
		return parametros;
	}


	public int getParametroSelected() {
		return parametroSelected;
	}





	public void setParametroSelected(int parametroSelected) {
		this.parametroSelected = parametroSelected;
	}





	public String getHeaderFilstros() {
		return headerFilstros;
	}





	public void setHeaderFilstros(String headerFilstros) {
		this.headerFilstros = headerFilstros;
	}


	protected OutputPanel getPanelFiltros() {
		if (panelFiltros == null) {
			panelFiltros = (OutputPanel) findComponentInRoot("panelFiltros");
		}
		return panelFiltros;
	}


	public List<Entidad> getListaEntidad() {
		return listaEntidad;
	}


	public void setListaEntidad(List<Entidad> listaEntidad) {
		this.listaEntidad = listaEntidad;
	}

	public List<TipoTarjeta> getListaTipoTarjeta() {
		return listaTipoTarjeta;
	}


	public void setListaTipoTarjeta(List<TipoTarjeta> listaTipoTarjeta) {
		this.listaTipoTarjeta = listaTipoTarjeta;
	}


	public List<BIN> getListaBIN() {
		return listaBIN;
	}


	public void setListaBIN(List<BIN> listaBIN) {
		this.listaBIN = listaBIN;
	}


	public boolean isFiltrosEntidad() {
		return filtrosEntidad;
	}


	public void setFiltrosEntidad(boolean filtrosEntidad) {
		this.filtrosEntidad = filtrosEntidad;
	}

	public boolean isFiltrosTipoTarjeta() {
		return filtrosTipoTarjeta;
	}


	public void setFiltrosTipoTarjeta(boolean filtrosTipoTarjeta) {
		this.filtrosTipoTarjeta = filtrosTipoTarjeta;
	}

	public boolean isFiltrosBIN() {
		return filtrosBIN;
	}


	public void setFiltrosBIN(boolean filtrosBIN) {
		this.filtrosBIN = filtrosBIN;
	}


	public boolean isPanelConsulta() {
		return panelConsulta;
	}


	public void setPanelConsulta(boolean panelConsulta) {
		this.panelConsulta = panelConsulta;
	}


	public List<Parametro> getListaParametro() {
		return listaParametro;
	}


	public void setListaParametro(List<Parametro> listaParametro) {
		this.listaParametro = listaParametro;
	}


	public Integer getFiid() {
		//Para que el valo por defecto que se muestra en la intefaz sea vacio y no un 0
		if(this.fiid!=null && this.fiid.equals(0)) return null;
		else return fiid;
	}


	public void setFiid(Integer fiid) {
	
		this.fiid = fiid;
	}


	protected HtmlInputText getCampoFiid() {
		if (campoFiid == null) {
			campoFiid = (HtmlInputText) findComponentInRoot("campoFiid");
		}
		return campoFiid;
	}


	public String getNombreEntidad() {
		return nombreEntidad;
	}


	public void setNombreEntidad(String nombreEntidad) {
		this.nombreEntidad = nombreEntidad;
	}


	protected HtmlInputText getCampoNomEntidad() {
		if (campoNomEntidad == null) {
			campoNomEntidad = (HtmlInputText) findComponentInRoot("campoNomEntidad");
		}
		return campoNomEntidad;
	}


	public boolean isBotonAgregar() {
		return botonAgregar;
	}


	public void setBotonAgregar(boolean botonAgregar) {
		this.botonAgregar = botonAgregar;
	}


	public boolean isBotonModificar() {
		return botonModificar;
	}


	public void setBotonModificar(boolean botonModificar) {
		this.botonModificar = botonModificar;
	}


	public int getIdEntModificar() {
		return idEntModificar;
	}


	public void setIdEntModificar(int idEntModificar) {
		this.idEntModificar = idEntModificar;
	}


	public int getCodTransaccion() {
		return codTransaccion;
	}


	public void setCodTransaccion(int codTransaccio) {
		this.codTransaccion = codTransaccio;
	}


	public String getNombreTran() {
		return nombreTran;
	}


	public void setNombreTran(String nombreTran) {
		this.nombreTran = nombreTran;
	}


	public int getIdCodModificar() {
		return idCodModificar;
	}


	public void setIdCodModificar(int idCodModificar) {
		this.idCodModificar = idCodModificar;
	}


	public String getNombreTranAS400() {
		return nombreTranAS400;
	}


	public void setNombreTranAS400(String nombreTranAS400) {
		this.nombreTranAS400 = nombreTranAS400;
	}


	protected HtmlInputText getCampoNombreTipoTar() {
		if (campoNombreTipoTar == null) {
			campoNombreTipoTar = (HtmlInputText) findComponentInRoot("campoNombreTipoTar");
		}
		return campoNombreTipoTar;
	}


	public String getNombreTipoTar() {
		return nombreTipoTar;
	}


	public void setNombreTipoTar(String nombreTipoTar) {
		this.nombreTipoTar = nombreTipoTar;
	}


	public int getIdTipoTarModificar() {
		return idTipoTarModificar;
	}


	public void setIdTipoTarModificar(int idTipoTarModificar) {
		this.idTipoTarModificar = idTipoTarModificar;
	}


	public int getCodigoEstado() {
		return codigoEstado;
	}


	public void setCodigoEstado(int codigoEstado) {
		this.codigoEstado = codigoEstado;
	}


	public String getNombreEstadoTar() {
		return nombreEstadoTar;
	}


	public void setNombreEstadoTar(String nombreEstadoTar) {
		this.nombreEstadoTar = nombreEstadoTar;
	}


	public String getNombreEstadoAS400() {
		return nombreEstadoAS400;
	}


	public void setNombreEstadoAS400(String nombreEstadoAS400) {
		this.nombreEstadoAS400 = nombreEstadoAS400;
	}


	public int getIdEstadoModificar() {
		return idEstadoModificar;
	}


	public void setIdEstadoModificar(int idEstadoModificar) {
		this.idEstadoModificar = idEstadoModificar;
	}


	public HtmlInputText getCampoCodigoBin() {
		if (campoCodigoBin == null) {
			campoCodigoBin = (HtmlInputText) findComponentInRoot("campoCodigoBin");
		}
		return campoCodigoBin;
	}


	public long getCodigoBin() {
		return codigoBin;
	}


	public void setCodigoBin(long codigoBin) {
		this.codigoBin = codigoBin;
	}


	public HtmlInputText getCampoRangoIni() {
		if (campoRangoIni == null) {
			campoRangoIni = (HtmlInputText) findComponentInRoot("campoRangoIni");
		}
		return campoRangoIni;
	}

	public HtmlInputText getCampoRangoFin() {
		if (campoRangoFin == null) {
			campoRangoFin = (HtmlInputText) findComponentInRoot("campoRangoFin");
		}
		return campoRangoFin;
	}

	protected HtmlInputText getCampoLongitudTar() {
		if (campoLongitudTar == null) {
			campoLongitudTar = (HtmlInputText) findComponentInRoot("campoLongitudTar");
		}
		return campoLongitudTar;
	}


	public int getTipoTarjetaSelected() {
		return tipoTarjetaSelected;
	}


	public void setTipoTarjetaSelected(int tipoTarjetaSelected) {
		this.tipoTarjetaSelected = tipoTarjetaSelected;
	}


	public int getLongitudTar() {
		return longitudTar;
	}


	public void setLongitudTar(int longitudTar) {
		this.longitudTar = longitudTar;
	}

	protected SelectOneMenu getTipoTarLista() {
		if (tipoTarLista == null) {
			tipoTarLista = (SelectOneMenu) findComponentInRoot("tipoTarLista");
		}
		return tipoTarLista;
	}


	protected DataTable getTablaRegistrosEntidad() {
		if (tablaRegistrosEntidad == null) {
			tablaRegistrosEntidad = (DataTable) findComponentInRoot("tablaRegistrosEntidad");
		}
		return tablaRegistrosEntidad;
	}


	protected OutputPanel getPanelResultado() {
		if (panelResultado == null) {
			panelResultado = (OutputPanel) findComponentInRoot("panelResultado");
		}
		return panelResultado;
	}


	protected DataTable getTablaRegistrosTipoTarjeta() {
		if (tablaRegistrosTipoTarjeta == null) {
			tablaRegistrosTipoTarjeta = (DataTable) findComponentInRoot("tablaRegistrosTipoTarjeta");
		}
		return tablaRegistrosTipoTarjeta;
	}


	protected DataTable getTablaRegistrosBIN() {
		if (tablaRegistrosBIN == null) {
			tablaRegistrosBIN = (DataTable) findComponentInRoot("tablaRegistrosBIN");
		}
		return tablaRegistrosBIN;
	}


	public long getRangoInicial() {
		return rangoInicial;
	}


	public void setRangoInicial(long rangoInicial) {
		this.rangoInicial = rangoInicial;
	}


	public long getRangoFinal() {
		return rangoFinal;
	}


	public void setRangoFinal(long rangoFinal) {
		this.rangoFinal = rangoFinal;
	}


	public int getIdBinModificar() {
		return idBinModificar;
	}


	public void setIdBinModificar(int idBinModificar) {
		this.idBinModificar = idBinModificar;
	}


	public FileItemStream getImagenCarga() {
		return imagenCarga;
	}


	public void setImagenCarga(FileItemStream imagenCarga) {
		this.imagenCarga = imagenCarga;
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


	protected HtmlPanelGrid getImagenCargar() {
		if (imagenCargar == null) {
			imagenCargar = (HtmlPanelGrid) findComponentInRoot("imagenCargar");
		}
		return imagenCargar;
	}


	public String getValorParam() {
		return valorParam;
	}


	public void setValorParam(String valorParam) {
		this.valorParam = valorParam;
	}


	public boolean isBotonAgregarBin() {
		return botonAgregarBin;
	}


	public void setBotonAgregarBin(boolean botonAgregarBin) {
		this.botonAgregarBin = botonAgregarBin;
	}


	protected SelectOneMenu getAutocomEntidad() {
		if (autocomEntidad == null) {
			autocomEntidad = (SelectOneMenu) findComponentInRoot("autocomEntidad");
		}
		return autocomEntidad;
	}


	public int getEntidadSelected() {
		return entidadSelected;
	}


	public void setEntidadSelected(int entidadSelected) {
		this.entidadSelected = entidadSelected;
	}


	public boolean isBotonModificarBin() {
		return botonModificarBin;
	}


	public void setBotonModificarBin(boolean botonModificarBin) {
		this.botonModificarBin = botonModificarBin;
	}


	public StreamedContent getImagenTarjeta() throws FileNotFoundException, IOException {
//		String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
//		for (BIN bin : listaBIN) {
//			
//				return bin.getImagenTarjeta();
//			
//		}
		FacesContext context = FacesContext.getCurrentInstance();
		String nombre=context.getExternalContext().getRequestParameterMap().get("nombreImg");
		String path = ConstantesWebPrepago.IMAGENES_DIR + nombre;
		if(nombre!=null){
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			BufferedImage img = ImageIO.read(new FileInputStream(path.replace("\\\\", "\\")));
	
			ImageIO.write(img, "png", bos);
	
			this.imagenTarjeta= new DefaultStreamedContent(new ByteArrayInputStream(
					bos.toByteArray()), "image/jpg");
			RequestContext contexto = RequestContext.getCurrentInstance();
			contexto.update("formularioAdminParametros:img");
		}
		return imagenTarjeta;
	}


	public void setImagenTarjeta(StreamedContent imagenTarjeta) {
		this.imagenTarjeta = imagenTarjeta;
	}


	public DefaultStreamedContent getImgdecode() {
		return imgdecode;
	}


	public void setImgdecode(DefaultStreamedContent imgdecode) {
		this.imgdecode = imgdecode;
	}


	public byte[] getFile() {
		return file;
	}


	public void setFile(byte[] file) {
		this.file = file;
	}


	public String getImgCargar() {
		return imgCargar;
	}


	public void setImgCargar(String imgCargar) {
		this.imgCargar = imgCargar;
	}

	public boolean isErrorSql() {
		return errorSql;
	}


	public static void setErrorSql(boolean errorSql) {
		AdministrarParametrosWPBean.errorSql = errorSql;
	}


	public boolean isMostrarPanelEliminarAuto() {
		return mostrarPanelEliminarAuto;
	}


	public void setMostrarPanelEliminarAuto(boolean mostrarPanelEliminarAuto) {
		this.mostrarPanelEliminarAuto = mostrarPanelEliminarAuto;
	}


	public String getCorreosNotficar() {
		return correosNotficar;
	}


	public void setCorreosNotficar(String correosNotficar) {
		this.correosNotficar = correosNotficar;
	}


	public String getDiasNotificasSelected() {
		return diasNotificasSelected;
	}


	public void setDiasNotificasSelected(String diasNotificasSelected) {
		this.diasNotificasSelected = diasNotificasSelected;
	}


	public List<String> getListaDiasNotificar() {
		return listaDiasNotificar;
	}


	public void setListaDiasNotificar(List<String> listaDiasNotificar) {
		this.listaDiasNotificar = listaDiasNotificar;
	}


	public String getDiasEliminarSelected() {
		return diasEliminarSelected;
	}


	public void setDiasEliminarSelected(String diasEliminarSelected) {
		this.diasEliminarSelected = diasEliminarSelected;
	}


	public List<String> getListaDiasEliminar() {
		return listaDiasEliminar;
	}


	public void setListaDiasEliminar(List<String> listaDiasEliminar) {
		this.listaDiasEliminar = listaDiasEliminar;
	}


	public List<Parametro> getListaParametros() {
		return listaParametros;
	}


	public void setListaParametros(List<Parametro> listaParametros) {
		this.listaParametros = listaParametros;
	}


	public boolean isFiltrosBolsillo() {
		return filtrosBolsillo;
	}


	public void setFiltrosBolsillo(boolean filtrosBolsillo) {
		this.filtrosBolsillo = filtrosBolsillo;
	}


	public String getCampoBolsillo() {
		return campoBolsillo;
	}


	public void setCampoBolsillo(String campoBolsillo) {
		this.campoBolsillo = campoBolsillo;
	}


	public List<Bolsillo> getListaBolsillos() {
		return listaBolsillos;
	}


	public void setListaBolsillos(List<Bolsillo> listaBolsillos) {
		this.listaBolsillos = listaBolsillos;
	}


	public int getCampoCodigoBolsillo() {
		return campoCodigoBolsillo;
	}


	public void setCampoCodigoBolsillo(int campoCodigoBolsillo) {
		this.campoCodigoBolsillo = campoCodigoBolsillo;
	}


	public String getIdBolsilloModificar() {
		return idBolsilloModificar;
	}


	public void setIdBolsilloModificar(String idBolsilloModificar) {
		this.idBolsilloModificar = idBolsilloModificar;
	}


	public boolean isFiltrosBinxBolsillo() {
		return filtrosBinxBolsillo;
	}


	public void setFiltrosBinxBolsillo(boolean filtrosBinxBolsillo) {
		this.filtrosBinxBolsillo = filtrosBinxBolsillo;
	}


	public ItemDescripcion getItemBinEntidadSelected() {
		return itemBinEntidadSelected;
	}


	public void setItemBinEntidadSelected(ItemDescripcion itemBinEntidadSelected) {
		this.itemBinEntidadSelected = itemBinEntidadSelected;
	}


	public List<ItemDescripcion> getListaBinEntidades() {
		return listaBinEntidades;
	}


	public void setListaBinEntidades(List<ItemDescripcion> listaBinEntidades) {
		this.listaBinEntidades = listaBinEntidades;
	}


	public List<BinXBolsillo> getListaBinBolsillos() {
		return listaBinBolsillos;
	}


	public void setListaBinBolsillos(List<BinXBolsillo> listaBinBolsillos) {
		this.listaBinBolsillos = listaBinBolsillos;
	}


	public String getBolsilloSelected() {
		return bolsilloSelected;
	}


	public void setBolsilloSelected(String bolsilloSelected) {
		this.bolsilloSelected = bolsilloSelected;
	}


	public BinXBolsillo getBinXbolsilloModificar() {
		return binXbolsilloModificar;
	}


	public void setBinXbolsilloModificar(BinXBolsillo binXbolsilloModificar) {
		this.binXbolsilloModificar = binXbolsilloModificar;
	}


	public String getNombreBolsilloModificar() {
		return nombreBolsilloModificar;
	}


	public void setNombreBolsilloModificar(String nombreBolsilloModificar) {
		this.nombreBolsilloModificar = nombreBolsilloModificar;
	}
	
}