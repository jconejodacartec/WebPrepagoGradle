package com.rbm.web.prepago.gestores;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.ibm.ams.excepcion.XmlToDtoException;
import com.ibm.ams.log.Logger;
import com.ibm.ams.reportes.GestorReportes;
import com.rbm.web.prepago.dto.ReporteMovimientos;
import com.rbm.web.prepago.exception.ExceptionWebPrepago;
import com.rbm.web.prepago.util.ConstantesWebPrepago;


public class GestorReportesWebPrepago {
	private static Logger logger = Logger.getInstance(ConstantesWebPrepago.NOMBRE_APLICACION, ConstantesWebPrepago.DIRECTORIO_CONFIGURACION);

	private static volatile GestorReportesWebPrepago instance = null;
	
	private GestorReportesWebPrepago(){
		
	}
	
	public static synchronized GestorReportesWebPrepago getInstance() {
		if (instance == null) {
			instance = new GestorReportesWebPrepago();
		}
		
		return instance;
	}
	
	
	

	public void generarReporteExcelMovimientos(ReporteMovimientos reporteMovimientos, OutputStream outputStream, boolean multibolsillo) throws ExceptionWebPrepago {
	
		try {
			Map<String, Object> datosReporte = new HashMap<String, Object>();
			
			datosReporte.put("fechaReporte", reporteMovimientos.getFechaInicial() + "-" + reporteMovimientos.getFechaFinal());
			datosReporte.put("fechaGeneracion", new Date());
			datosReporte.put("usuario", reporteMovimientos.getUsuario());
			datosReporte.put("nombreArchivo", reporteMovimientos.getNombreArchivo());
			datosReporte.put("emisor", reporteMovimientos.getEmisor());
			datosReporte.put("datos", reporteMovimientos.getRegistros());
			datosReporte.put("impuesto", reporteMovimientos.getImpuesto());
			datosReporte.put("saldo", reporteMovimientos.getSaldo());
			datosReporte.put("numTarjeta", reporteMovimientos.getNumTarjeta());
			datosReporte.put("nombreTarjeta", reporteMovimientos.getNombreTarjeta());
			String plantilla=ConstantesWebPrepago.PLANTILLA_REPORTE_CONSULTAR_MOVIMIENTOS;
			if(multibolsillo) {
				plantilla=ConstantesWebPrepago.PLANTILLA_REPORTE_CONSULTAR_MOVIMIENTOS_BOLSILLO;
			}
			
			crearReporteExcel(outputStream, plantilla, datosReporte);
		} catch (FileNotFoundException e) {
			throw new ExceptionWebPrepago("Error por no encontrar la plantilla del reporte: ", e);
		} catch (XmlToDtoException e) {
			throw new ExceptionWebPrepago("Error por no poder parsear el archivo de configuracion de reportes: ", e);
		} catch (IOException e) {
			throw new ExceptionWebPrepago("Error por no poder abrir la plantilla del reporte", e);
		}
	}
	
	private void crearReporteExcel(OutputStream outputStream, String nombrePlantilla, Map<String, Object> datosReporte) 
			throws XmlToDtoException, FileNotFoundException, IOException {
		GestorReportes gestorReportes = new GestorReportes(ConstantesWebPrepago.REPORTES_CONFIG_PATH);
		gestorReportes.getReporte(outputStream, nombrePlantilla, datosReporte);
	}
	
	
}
