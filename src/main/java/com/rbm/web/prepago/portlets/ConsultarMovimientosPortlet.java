package com.rbm.web.prepago.portlets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import com.ibm.faces20.portlet.FacesPortlet;
import com.rbm.web.prepago.managedBeans.ConsultarMovimientosBean;
import com.rbm.web.prepago.util.ConstantesWebPrepago;


public class ConsultarMovimientosPortlet extends com.ibm.faces20.portlet.FacesPortlet {

	
	
	public void destroy() {
		super.destroy();
	}
	
	public void doConfigure(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		super.doConfigure(request, response);
	}
	
	
	public void doEdit(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		super.doEdit(request, response);
	}
	
	public void doHelp(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		super.doHelp(request, response);
	}
	
	public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		
		
		super.doView(request, response);
	}
	
	public void init() throws PortletException {
		super.init();
	}
	
		
	
	
	public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
		
		super.processAction(request, response);
	}
		
	
	/**
	 * @see FacesPortlet#serveResource(ResourceRequest, ResourceResponse)
	 */
	public static void serveResources(ResourceRequest resRequest, ResourceResponse resResponse) throws PortletException, IOException {
		String resourceID = resRequest.getResourceID();
		if (ConstantesWebPrepago.RESOURCE_ID_EXPORT_REPORTE_CONSULTAR_MOVIMIENTOS_EXCEL.equals(resourceID)) {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			OutputStream responseOutputStream = resResponse.getPortletOutputStream();
			
			ConsultarMovimientosBean bean = (ConsultarMovimientosBean) resRequest.getPortletSession().
					getAttribute(ConstantesWebPrepago.BEAN_CONSULTAR_MOVIMIENTOS);
			
			try { 
				if (bean != null) {
					bean.exportarExcel(outputStream);
					String patron = "ddMMyyyy";
					SimpleDateFormat formato = new SimpleDateFormat(patron);
					
					resResponse.setContentType(ConstantesWebPrepago.CONTENT_TYPE_EXCEL);
					resResponse.addProperty(ConstantesWebPrepago.CONTENT_TYPE_KEY, 
							ConstantesWebPrepago.ATTACHMENT_KEY + ConstantesWebPrepago.NOMBRE_REPORTE_MOVIMIENTOS+ bean.getTarjeta().getNumeroTarjetaEnmascarado()+ConstantesWebPrepago.FORMATO_ARCHIVO_EXCEL);
					
					responseOutputStream.write(outputStream.toByteArray());
					responseOutputStream.flush();
				}
			} finally {
				responseOutputStream.close();
				outputStream.close();
			}
		}
		 else if (ConstantesWebPrepago.RESOURCE_ID_EXPORT_REPORTE_NOVEDADES_PDF.equals(resourceID)) {
			ConsultarMovimientosBean bean = (ConsultarMovimientosBean) resRequest.getPortletSession().
					getAttribute(ConstantesWebPrepago.BEAN_CONSULTAR_MOVIMIENTOS);
			OutputStream outStream = resResponse.getPortletOutputStream();
			byte[]archivo = bean.exportarReportePDF();
			resResponse.setContentType("application/pdf");
			String patron = "ddMMyyyy";
			SimpleDateFormat formato = new SimpleDateFormat(patron);
			resResponse.addProperty("content-disposition", ConstantesWebPrepago.ATTACHMENT_KEY+ConstantesWebPrepago.NOMBRE_REPORTE_MOVIMIENTOS+ bean.getTarjeta().getNumeroTarjetaEnmascarado()+ConstantesWebPrepago.FORMATO_ARCHIVO_PDF);
			outStream.write(archivo);
			outStream.flush();
			outStream.close();
		}
		
	
	}
	
	
	public void serveResource(ResourceRequest resRequest, ResourceResponse resResponse) throws PortletException, IOException {
		String resourceID = resRequest.getResourceID();
		if (ConstantesWebPrepago.RESOURCE_ID_EXPORT_REPORTE_CONSULTAR_MOVIMIENTOS_EXCEL.equals(resourceID)) {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			OutputStream responseOutputStream = resResponse.getPortletOutputStream();
			
			ConsultarMovimientosBean bean = (ConsultarMovimientosBean) resRequest.getPortletSession().
					getAttribute(ConstantesWebPrepago.BEAN_CONSULTAR_MOVIMIENTOS);
			
			try { 
				if (bean != null) {
					bean.exportarExcel(outputStream);
					String patron = "ddMMyyyy";
					SimpleDateFormat formato = new SimpleDateFormat(patron);
					
					resResponse.setContentType(ConstantesWebPrepago.CONTENT_TYPE_EXCEL);
					resResponse.addProperty(ConstantesWebPrepago.CONTENT_TYPE_KEY, 
							ConstantesWebPrepago.ATTACHMENT_KEY + ConstantesWebPrepago.NOMBRE_REPORTE_MOVIMIENTOS+ bean.getTarjeta().getNumeroTarjetaEnmascarado()+ConstantesWebPrepago.FORMATO_ARCHIVO_EXCEL);
					
					responseOutputStream.write(outputStream.toByteArray());
					responseOutputStream.flush();
				}
			} finally {
				responseOutputStream.close();
				outputStream.close();
			}
		}
		 else if (ConstantesWebPrepago.RESOURCE_ID_EXPORT_REPORTE_NOVEDADES_PDF.equals(resourceID)) {
			ConsultarMovimientosBean bean = (ConsultarMovimientosBean) resRequest.getPortletSession().
					getAttribute(ConstantesWebPrepago.BEAN_CONSULTAR_MOVIMIENTOS);
			OutputStream outStream = resResponse.getPortletOutputStream();
			try { 
				byte[]archivo = bean.exportarReportePDF();
				resResponse.setContentType("application/pdf");
				resResponse.addProperty(ConstantesWebPrepago.CONTENT_TYPE_KEY, 
						ConstantesWebPrepago.ATTACHMENT_KEY+ConstantesWebPrepago.NOMBRE_REPORTE_MOVIMIENTOS+ bean.getTarjeta().getNumeroTarjetaEnmascarado()+ConstantesWebPrepago.FORMATO_ARCHIVO_PDF);
				outStream.write(archivo);
				outStream.flush();
			}finally{
				outStream.close();
			}
		 }
		super.serveResource(resRequest, resResponse);
	}
	
	
	
}