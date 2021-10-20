package com.rbm.web.prepago.managedBeans;

import java.io.ByteArrayOutputStream;
import java.security.Principal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.application.FacesMessage;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.component.calendar.Calendar;
import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.outputpanel.OutputPanel;
import org.primefaces.component.panel.Panel;
import org.primefaces.context.RequestContext;

import com.ibm.ams.excepcion.XmlToDtoException;
import com.ibm.ams.log.Logger;
import com.ibm.ams.reportes.GestorReportes;
import com.ibm.faces20.portlet.component.PortletResourceURL;
import com.rbm.web.prepago.configuracion.Configuracion;
import com.rbm.web.prepago.dto.Bolsillo;
import com.rbm.web.prepago.dto.RegistroMovimiento;
import com.rbm.web.prepago.dto.TarjetaPrepago;
import com.rbm.web.prepago.dto.Usuario;
import com.rbm.web.prepago.exception.ExceptionWebPrepago;
import com.rbm.web.prepago.gestores.GestorBD;
import com.rbm.web.prepago.gestores.GestorCache;
import com.rbm.web.prepago.gestores.GestorConfiguracionLDAP;
import com.rbm.web.prepago.gestores.GestorReportesWebPrepago;
import com.rbm.web.prepago.pagecode.PageCodeBase;
import com.rbm.web.prepago.util.ConstantesWebPrepago;

/**
 * @author ltautiva
 * 
 */
public class ConsultarMovimientosBean extends PageCodeBase {

	private String ip;
	private boolean mostarSaldo = true;

	private List<RegistroMovimiento> listaMovimientos = new ArrayList<RegistroMovimiento>();
	protected OutputPanel tablaResultados;
	protected DataTable tablaConsulta;
	protected HtmlInputText idEntidad;
	protected HtmlInputText tipoTarjetaId;
	protected CommandButton btnVolver;
	private Principal usuarioLogin;
	private Usuario usuarioDB;
	private List<TarjetaPrepago> listaTarjetas = new ArrayList<TarjetaPrepago>();
	private List<Bolsillo> bolsillos = new ArrayList<Bolsillo>();
	private String bolsilloSelected;
	private String tarjetaSelected;
	private String tipoTarjeta;
	private String entidad;
	private String saldo;
	private String impuesto;
	protected HtmlSelectOneMenu selectProducto;

	private static String nombreBean = "nombreBean";
	private String navegacion;
	private TarjetaPrepago tarjeta;
	private boolean mostrarVolver;
	private boolean mostarResultados;

	private Date fechaInicial;
	private Date fechaFinal;
	private Date minDate;
	private Date maxDate;
	private Date minDateCalFin;
	private Date maxDateCalFin;
	private com.rbm.web.prepago.dto.ReporteMovimientos reporteMovimientos;
	private GestorReportes reportes;
	private Configuracion conf;
	private String typeMsjCarga;
	private String msjCarga;
	private static Logger logger = Logger.getInstance(
			ConstantesWebPrepago.NOMBRE_APLICACION,
			ConstantesWebPrepago.DIRECTORIO_CONFIGURACION);
	protected PortletResourceURL ReporteExcelMovimientos;
	protected PortletResourceURL ReportePDFMovimientos;
	protected HtmlForm formularioConsulta;
	protected HtmlInputHidden ipValue;
	protected Panel panelGeneral;
	protected Calendar idFechaInicial;
	protected Calendar idFechaFinal;
	protected HtmlGraphicImage divimagenTarjeta;
	protected CommandButton btnConsultar;
	protected HtmlOutputText linkExcel;
	protected HtmlOutputText linkPDF;
	private String usuarioFinal;
	private List<TarjetaPrepago> ListaCompletatarjetasPrepago = new ArrayList<TarjetaPrepago>();

	/**
	 * Obtiene las tarjetas prepagadas asociadas al usuario en sesion
	 */
	public void obtenerTarjetas() {
		try {
			RequestContext context = RequestContext.getCurrentInstance();
			// Consulta el id del usuario de la base de datos y trae todas las
			// tarjetas asociadas a este id
			// usuarioDB =
			// GestorBD.consultarUsuarioPorTipoUsuario(this.usuarioLogin.getName(),
			// ConstantesWebPrepago.USUARIO_PREMANENTE);
			
			
			usuarioDB = GestorBD.consultarUsuarioPorTipoUsuario(
					usuarioFinal,
					ConstantesWebPrepago.USUARIO_PREMANENTE,null);

			ListaCompletatarjetasPrepago = new ArrayList<TarjetaPrepago>();
			List<TarjetaPrepago> tarjetasPrepago = new ArrayList<TarjetaPrepago>();
			tarjetasPrepago = GestorBD.consultarTarjetasPorUsuario(usuarioDB.getId(),null,null, ConstantesWebPrepago.BEAN_CONSULTAR_MOVIMIENTOS);

			//limpiar la lista de tarjetas repetidas
			limpiarLista(tarjetasPrepago);
			ListaCompletatarjetasPrepago =tarjetasPrepago;
			
			// Si la lista esta vacia, entonces se muestra el mensaje
			// correspondiente
			if (listaTarjetas.size() == 0) {
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_WARN,
										ConstantesWebPrepago.CONSULTAR_SALDO_MI001,
										""));
			} else {
				onTarjetaChange();
				context.update("idEntidad");
				context.update("tipoTarjetaId");
				context.update("divimagenTarjeta");
			}
		}catch(ExceptionWebPrepago e){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ConstantesWebPrepago.CONSULTAR_SALDO_ME002 , ""));
		}
		catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							ConstantesWebPrepago.CONSULTAR_SALDO_ME001, ""));
		}
	}

	private void limpiarLista(List<TarjetaPrepago> tarjetasPrepago) {
		Map<String, TarjetaPrepago> listaN = new HashMap<String,TarjetaPrepago>(tarjetasPrepago.size());
		listaTarjetas = new ArrayList<TarjetaPrepago>();
		for(TarjetaPrepago tar: tarjetasPrepago) {
			listaN.put(tar.getNumTarjeta(), tar);
		}
		for(Entry<String, TarjetaPrepago> tp: listaN.entrySet()) {
			logger.debug(this.getClass().getName(), "qweqwe", "agregando tarjeta ***"+tp.getValue().getNumeroTarjeta());
			listaTarjetas.add(tp.getValue());
		}
	}

	public ConsultarMovimientosBean() {

		super();
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(false);
		FacesContext fc = FacesContext.getCurrentInstance();
		usuarioLogin = fc.getExternalContext().getUserPrincipal();
		String usuarioF = this.usuarioLogin.getName();
		String [] primero = usuarioF.split(",");
		String [] segundo = primero[0].toString().split("=");
		if(segundo.length>1){
			usuarioFinal = segundo[1];
		}else{
			usuarioFinal = segundo[0];
		}
		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			impuesto = conf.getImpuesto();
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							ConstantesWebPrepago.CONSULTAR_SALDO_ME001, ""));
		}
		this.mostrarVolver = false;
		this.mostarResultados = false;

		try {
			reportes = new GestorReportes(
					ConstantesWebPrepago.REPORTES_CONFIG_PATH);
			GestorBD.ActualizarUltimaConexion(usuarioFinal);

		} catch (XmlToDtoException e) {
			logger.error(this.getClass().getName(), "ConsultaPagosBean",
					"Error en el contructor", e);
			throw new RuntimeException(
					"Error en la creacion del gestor de reportes: ", e);
		} catch (ExceptionWebPrepago e) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Error al actualizar ultima conexion", ""));
			e.printStackTrace();
		}

		if (session.getAttribute(ConstantesWebPrepago.ATTRIBUTO_NOMBRE_BEAN) != null) {
			nombreBean = (String) session
					.getAttribute(ConstantesWebPrepago.ATTRIBUTO_NOMBRE_BEAN);
			logger.debug(this.getClass().getName(), "CONSTRUCTOR",
					"BEAN QUE LLAMA: " + nombreBean);

			if (nombreBean != null
					&& nombreBean
							.compareTo(ConstantesWebPrepago.BEAN_CONSULTA_NOVEDAD) == 0) {
				this.fechaFinal= new Date();
				this.fechaInicial= agregarDias(this.fechaFinal,-5);
				setTarjeta((TarjetaPrepago) session
						.getAttribute(ConstantesWebPrepago.OBJETO_TARJETA));
				setNavegacion((String) session
						.getAttribute(ConstantesWebPrepago.NAVEGACION));
				
				
				
				

				this.mostrarVolver = true;
				this.tarjetaSelected = this.tarjeta.getNumeroTarjeta();
			}
		}
		this.mostarSaldo = true;
		this.maxDate= new  Date();
		
		this.minDate= agregarDias(maxDate, -365);		 
		
	}

	/**
	 * Metodo que permite redirigir a la pagina anterior
	 * 
	 * @return ruta de regreso
	 */
	public String regresar() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(false);
		session.removeAttribute(ConstantesWebPrepago.OBJETO_TARJETA);
		session.removeAttribute(ConstantesWebPrepago.NAVEGACION);
		session.removeAttribute(ConstantesWebPrepago.ATTRIBUTO_NOMBRE_BEAN);
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
				.put(nombreBean, null);
		return navegacion;
	}

	public void onTarjetaChange() {

		TarjetaPrepago seleccionada = null;
		for (TarjetaPrepago tarjeta : listaTarjetas) {
			if (tarjeta.getNumeroTarjeta().equals(tarjetaSelected)) {
				seleccionada = tarjeta;
				break;
			}
		}

		if (seleccionada != null) {
			this.tarjeta = seleccionada;
			this.entidad = seleccionada.getEntidad();
			this.tipoTarjeta = seleccionada.getTipoTarjeta();
			//this.saldo = seleccionada.getSaldoFormatoDecimal();
			try {
				bolsillos=GestorBD.getBolsillosbyTarjeta(tarjeta.getNumTarjeta());
			} catch (ExceptionWebPrepago e) {
				logger.error(this.getClass().getName(), "onTarjetaChange",
						"Error en el onTarjetaChange bolsillos", e);
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								ConstantesWebPrepago.CONSULTAR_SALDO_ME001, ""));
			}
		}
	}

	public void consultarMov() {
		String nombreBolsillo = "";
		RequestContext context = RequestContext.getCurrentInstance();
		if (validarCampos()) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(ConstantesWebPrepago.FORMATO_FECHA_BPO);
				listaMovimientos = GestorBD.consultarMovimientosBPO(tarjeta.getNumTarjeta(), sdf.format(fechaInicial),sdf.format(fechaFinal), bolsilloSelected);
				if(!listaMovimientos.isEmpty()){
					this.mostarSaldo = true;
					this.mostarResultados = true;
				}else{
					this.mostarSaldo = false;
					this.mostarResultados = true;

				}
				for(Bolsillo bol : this.bolsillos) {
					logger.debug(this.getClass().getName(), "qweqwe", "id del bolsillo: "+bol.getId());
					if(bol.getId().equals(this.bolsilloSelected)) {
						nombreBolsillo = bol.getNombre();
						break;
					}
				}
				for(TarjetaPrepago tarjeta : ListaCompletatarjetasPrepago) {
					if(tarjeta.getBolsillo().equals(nombreBolsillo) && tarjeta.getNumeroTarjeta().equals(this.tarjetaSelected)) {
						logger.debug(this.getClass().getName(), "qweqwe", "saldo a mostar :"+tarjeta.getSaldoFormatoDecimal());
						this.saldo = tarjeta.getSaldoFormatoDecimal();
						break;
					}
				}
				context.update(":formularioConsulta:campoSaldo");
			} catch (ExceptionWebPrepago e) {
				this.setTypeMsjCarga("error");
				this.setMsjCarga("NO EXISTE REGISTRO COINCIDENTE");
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("NO EXISTE REGISTRO COINCIDENTE",""));
			} catch (SQLException e) {
				logger.error(this.getClass().getName(), "consultarMov","Error en BD", e);
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR,ConstantesWebPrepago.CONSULTAR_SALDO_ME001, ""));
			}
		} else {
			this.mostarSaldo = false;
			this.mostarResultados = false;
			
		}
	}

	private boolean validarCampos() {
		String mensaje = "";
		boolean valida = true;
		if (this.tarjetaSelected == null || this.tarjetaSelected.isEmpty()) {
			mensaje += " Producto,";
			valida = false;
		}
		if (this.fechaInicial == null) {
			mensaje += " Fecha Inicial,";
			valida = false;
		}
		
		if (this.fechaFinal == null) {
			mensaje += " Fecha Final,";
			valida = false;
		}
		if (this.bolsilloSelected == null) {
			mensaje += " Bolsillo seleccionado,";
			valida = false;
		}
		
						
		if (!valida) {
			mensaje=mensaje.substring(0, mensaje.length()-1);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ConstantesWebPrepago.CONSULTAR_SALDO_ME003+" "+mensaje, ""));
			
		}
		
		/* Descomentar si se necesitana implementar mensaje de error cuando las fechas son mayores a 90 dias
		else{ // si  los campos estan completos, valida el rango de las fechas
			long diferencia=compararFechas(this.fechaInicial, this.fechaFinal);
			if(diferencia>90){
				valida=false;
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								ConstantesWebPrepago.CONSULTAR_SALDO_ME004 + " "
										, ""));
			}
		}
		*/
		return valida;
	}
	private Date agregarDias(Date fecha,int dias){
		Date fechaNueva=new Date();
		fechaNueva.setTime(fecha.getTime()+ (long)dias*1000*60*60*24 );
		return fechaNueva;
	}
	
	public void x(){
		Date dia = new Date();
		setMinDateCalFin(this.fechaInicial);
		logger.debug(this.getClass().getName(), "x",
				"Generando nuevas fechas");
		
		this.maxDateCalFin = new Date(this.fechaInicial.getTime() + (long)90*1000*60*60*24 );
		if (this.maxDateCalFin.after(dia) ){
			this.maxDateCalFin = dia;
		}
	}
	
	private long compararFechas(Date fechaInicial,Date fechaFinal){
		 long diff=-1;
		  try {

		     diff = Math.round((fechaFinal.getTime() - fechaInicial.getTime()) / (double) 86400000);
		    
		  } catch (Exception e) {
				logger.debug(this.getClass().getName(), "validarCampos",
						"Error comparando las fechas");
				if(e!=null) e.printStackTrace();
		  }
		  return diff;
	}
	
	public List<RegistroMovimiento> getLista() {
		return listaMovimientos;
	}

	public void setLista(List<RegistroMovimiento> lista) {
		this.listaMovimientos = lista;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public boolean isMostarSaldo() {
		return mostarSaldo;
	}

	public void setMostarSaldo(boolean mostarSaldo) {
		this.mostarSaldo = mostarSaldo;
	}

	protected OutputPanel getTablaResultados() {
		if (tablaResultados == null) {
			tablaResultados = (OutputPanel) findComponentInRoot("tablaResultados");
		}
		return tablaResultados;
	}

	protected DataTable getTablaConsulta() {
		if (tablaConsulta == null) {
			tablaConsulta = (DataTable) findComponentInRoot("tablaConsulta");
		}
		return tablaConsulta;
	}

	protected HtmlInputText getIdEntidad() {
		if (idEntidad == null) {
			idEntidad = (HtmlInputText) findComponentInRoot("idEntidad");
		}
		return idEntidad;
	}

	protected HtmlInputText getTipoTarjetaId() {
		if (tipoTarjetaId == null) {
			tipoTarjetaId = (HtmlInputText) findComponentInRoot("tipoTarjetaId");
		}
		return tipoTarjetaId;
	}

	protected CommandButton getBtnVolver() {
		if (btnVolver == null) {
			btnVolver = (CommandButton) findComponentInRoot("btnVolver");
		}
		return btnVolver;
	}

	public Principal getUsuarioLogin() {
		return usuarioLogin;
	}

	public void setUsuarioLogin(Principal usuarioLogin) {
		this.usuarioLogin = usuarioLogin;
	}

	public Usuario getUsuarioDB() {
		return usuarioDB;
	}

	public void setUsuarioDB(Usuario usuarioDB) {
		this.usuarioDB = usuarioDB;
	}

	public List<TarjetaPrepago> getListaTarjetas() {
		return listaTarjetas;
	}

	public void setListaTarjetas(List<TarjetaPrepago> listaTarjetas) {
		this.listaTarjetas = listaTarjetas;
	}

	public String getTarjetaSelected() {
		return tarjetaSelected;
	}

	public void setTarjetaSelected(String tarjetaSelected) {
		this.tarjetaSelected = tarjetaSelected;
	}

	public String getTipoTarjeta() {
		return tipoTarjeta;
	}

	public void setTipoTarjeta(String tipoTarjeta) {
		this.tipoTarjeta = tipoTarjeta;
	}

	public String getEntidad() {
		return entidad;
	}

	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

	public String getSaldo() {
		return saldo;
	}

	public void setSaldo(String saldo) {
		this.saldo = saldo;
	}

	protected HtmlSelectOneMenu getSelectProducto() {
		if (selectProducto == null) {
			selectProducto = (HtmlSelectOneMenu) findComponentInRoot("selectProducto");
		}
		return selectProducto;
	}

	public static String getNombreBean() {
		return nombreBean;
	}

	public static void setNombreBean(String nombreBean) {
		ConsultarMovimientosBean.nombreBean = nombreBean;
	}

	public String getNavegacion() {
		return navegacion;
	}

	public void setNavegacion(String navegacion) {
		this.navegacion = navegacion;
	}

	public TarjetaPrepago getTarjeta() {
		return tarjeta;
	}

	public void setTarjeta(TarjetaPrepago tarjeta) {
		this.tarjeta = tarjeta;
	}

	public boolean isMostrarVolver() {
		return mostrarVolver;
	}

	public void setMostrarVolver(boolean mostrarVolver) {
		this.mostrarVolver = mostrarVolver;
	}

	public void exportarExcel(ByteArrayOutputStream outputStream) {
		try {
			boolean bolsillo = false;
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
			String fechaInicial = formatter.format(this.fechaInicial);
			String fechaFinal = formatter.format(this.fechaFinal);

			this.reporteMovimientos = new com.rbm.web.prepago.dto.ReporteMovimientos();
			logger.debug(this.getClass().getName(), "exportarExcel",
					"Inicia generacion Excel");
			reporteMovimientos.setFechaInicial(fechaInicial);
			reporteMovimientos.setFechaFinal(fechaFinal);
			String usuarioEnmascarado= enmascararUsuario(usuarioFinal);
			reporteMovimientos.setUsuario(usuarioEnmascarado);				
			reporteMovimientos.setUsuarioEnmascarado(usuarioEnmascarado);
			reporteMovimientos
				.setNombreArchivo(ConstantesWebPrepago.NOMBRE_REPORTE_MOVIMIENTOS+this.tarjeta.getNumeroTarjetaEnmascarado()+ConstantesWebPrepago.FORMATO_ARCHIVO_EXCEL);
			reporteMovimientos.setRegistros(this.listaMovimientos);
			reporteMovimientos.setEmisor(ConstantesWebPrepago.EMISOR);
			reporteMovimientos.setImpuesto(this.impuesto);
			String saldo = formatCurrency(this.tarjeta.getSaldo());
			reporteMovimientos.setSaldo(saldo);
			reporteMovimientos.setNumTarjeta(this.tarjeta.getNumeroTarjeta());
			reporteMovimientos
					.setNombreTarjeta(this.tarjeta.getNombreTarjeta());
			if(this.bolsilloSelected != null) {
				bolsillo = true;
			}
			if (reporteMovimientos.getRegistros().isEmpty()) {

				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_ERROR,
										"No existen registros para la consulta realizada",
										null));
			} else {
				GestorReportesWebPrepago.getInstance()
						.generarReporteExcelMovimientos(reporteMovimientos,
								outputStream, bolsillo);
			}
		} catch (ExceptionWebPrepago e) {
			logger.error(this.getClass().getName(), "exportarExcel",
					"Error al generar el reporte", e);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							ConstantesWebPrepago.MSG_ERR_GENERAL, null));
		}

	}

	/**
	 * Exportar reporte pdf.
	 * 
	 * @return el byte[]
	 */
	public byte[] exportarReportePDF() {

		boolean multibolsillo = false;
		byte[] reporte = null;
		SimpleDateFormat formatter = new SimpleDateFormat(
				ConstantesWebPrepago.FORMATO_FECHA_XLS);
		String fechaInicial = formatter.format(this.fechaInicial);
		String fechaFinal = formatter.format(this.fechaFinal);
		String fechaGeneracion = formatter.format(new Date());
		logger.debug(this.getClass().getName(), "exportarPDF",
				"Inicia generacion PDF");

		this.reporteMovimientos = new com.rbm.web.prepago.dto.ReporteMovimientos();
		reporteMovimientos.setFechaInicial(fechaInicial);
		reporteMovimientos.setFechaFinal(fechaFinal);
		reporteMovimientos.setUsuario(usuarioFinal);
		String usuarioEnmascarado= enmascararUsuario(usuarioFinal);	
		reporteMovimientos.setUsuarioEnmascarado(usuarioEnmascarado);
		reporteMovimientos
			.setNombreArchivo(ConstantesWebPrepago.NOMBRE_REPORTE_MOVIMIENTOS+this.tarjeta.getNumeroTarjetaEnmascarado()+ConstantesWebPrepago.FORMATO_ARCHIVO_PDF);
		reporteMovimientos.setEmisor(ConstantesWebPrepago.EMISOR);
		reporteMovimientos.setRegistros(this.listaMovimientos);
		reporteMovimientos.setImpuesto(this.impuesto);
		String saldo = formatCurrency(this.tarjeta.getSaldo());
		reporteMovimientos.setSaldo(saldo);
		reporteMovimientos.setNumTarjeta(this.tarjeta.getNumeroTarjeta());
		reporteMovimientos.setNombreTarjeta(this.tarjeta.getNombreTarjeta());
		reporteMovimientos.setFechaGeneracion(fechaGeneracion);
		String plantilla="ReporteConsultarMovimientos";
		if(this.bolsilloSelected != null) {
			multibolsillo = true;
		}
		if(multibolsillo) {
			plantilla="ReporteConsultarMovimientosBolsillo";
		}
		try {
			logger.debug(this.getClass().getName(), "exportarPDF",
					"antes de getReportePDF");
			reporte = reportes.getReportePDF(plantilla,
					reporteMovimientos);
			logger.debug(this.getClass().getName(), "exportarPDF",
					"Termino generacion PDF");
		} catch (Exception e) {
			logger.error(this.getClass().getName(), "exportarExcel",
					"Error al generar el PDF", e);
		}
		return reporte;
	}
	
	public String enmascararUsuario(String usuario){
		String masked="";
		int length=usuario.length();
		if(length<=4) return usuario;
		for(int i=0;i<usuario.length()-4;i++){
			masked+="*";
		}
		masked+=usuario.substring(usuario.length()-4);
		return masked;
	} 

	public String formatCurrency(Double amount) {
		Locale locale = new Locale("en", "US");
		NumberFormat currencyFormatter = NumberFormat
				.getCurrencyInstance(locale);
		return currencyFormatter.format(amount);
	}

	public Date getFechaInicial() {
		return fechaInicial;
	}

	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}

	public Date getFechaFinal() {
		return fechaFinal;
	}

	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}

	public List<RegistroMovimiento> getListaMovimientos() {
		return listaMovimientos;
	}

	public void setListaMovimientos(List<RegistroMovimiento> listaMovimientos) {
		this.listaMovimientos = listaMovimientos;
	}

	public boolean isMostarResultados() {
		return mostarResultados;
	}

	public void setMostarResultados(boolean mostarResultados) {
		this.mostarResultados = mostarResultados;
	}

	public com.rbm.web.prepago.dto.ReporteMovimientos getReporteMovimientos() {
		return reporteMovimientos;
	}

	public void setReporteMovimientos(
			com.rbm.web.prepago.dto.ReporteMovimientos reporteMovimientos) {
		this.reporteMovimientos = reporteMovimientos;
	}

	public GestorReportes getReportes() {
		return reportes;
	}

	public void setReportes(GestorReportes reportes) {
		this.reportes = reportes;
	}

	public Configuracion getConf() {
		return conf;
	}

	public void setConf(Configuracion conf) {
		this.conf = conf;
	}

	public String getImpuesto() {
		return impuesto;
	}

	public void setImpuesto(String impuesto) {
		this.impuesto = impuesto;
	}

	protected HtmlForm getFormularioConsulta() {
		if (formularioConsulta == null) {
			formularioConsulta = (HtmlForm) findComponentInRoot("formularioConsulta");
		}
		return formularioConsulta;
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

	protected Calendar getIdFechaInicial() {
		if (idFechaInicial == null) {
			idFechaInicial = (Calendar) findComponentInRoot("idFechaInicial");
		}
		return idFechaInicial;
	}

	protected Calendar getIdFechaFinal() {
		if (idFechaFinal == null) {
			idFechaFinal = (Calendar) findComponentInRoot("idFechaFinal");
		}
		return idFechaFinal;
	}

	protected HtmlGraphicImage getDivimagenTarjeta() {
		if (divimagenTarjeta == null) {
			divimagenTarjeta = (HtmlGraphicImage) findComponentInRoot("divimagenTarjeta");
		}
		return divimagenTarjeta;
	}

	public Date getMinDate() {
		return minDate;
	}

	public void setMinDate(Date minDate) {
		this.minDate = minDate;
	}

	public Date getMaxDate() {
		return maxDate;
	}

	public void setMaxDate(Date maxDate) {
		this.maxDate = maxDate;
	}

	protected CommandButton getBtnConsultar() {
		if (btnConsultar == null) {
			btnConsultar = (CommandButton) findComponentInRoot("btnConsultar");
		}
		return btnConsultar;
	}

	protected HtmlOutputText getLinkExcel() {
		if (linkExcel == null) {
			linkExcel = (HtmlOutputText) findComponentInRoot("linkExcel");
		}
		return linkExcel;
	}

	protected HtmlOutputText getLinkPDF() {
		if (linkPDF == null) {
			linkPDF = (HtmlOutputText) findComponentInRoot("linkPDF");
		}
		return linkPDF;
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

	public Date getMinDateCalFin() {
		return minDateCalFin;
	}

	public void setMinDateCalFin(Date minDateCalFin) {
		this.minDateCalFin = minDateCalFin;
	}

	public Date getMaxDateCalFin() {
		return maxDateCalFin;
	}

	public void setMaxDateCalFin(Date maxDateCalFin) {
		this.maxDateCalFin = maxDateCalFin;
	}

	public String getUsuarioFinal() {
		return usuarioFinal;
	}

	public void setUsuarioFinal(String usuarioFinal) {
		this.usuarioFinal = usuarioFinal;
	}

	public String getBolsilloSelected() {
		return bolsilloSelected;
	}

	public void setBolsilloSelected(String bolsilloSelected) {
		this.bolsilloSelected = bolsilloSelected;
	}

	public List<Bolsillo> getBolsillos() {
		return bolsillos;
	}

	public void setBolsillos(List<Bolsillo> bolsillos) {
		this.bolsillos = bolsillos;
	}

	public List<TarjetaPrepago> getListaCompletatarjetasPrepago() {
		return ListaCompletatarjetasPrepago;
	}

	public void setListaCompletatarjetasPrepago(List<TarjetaPrepago> listaCompletatarjetasPrepago) {
		ListaCompletatarjetasPrepago = listaCompletatarjetasPrepago;
	}

}