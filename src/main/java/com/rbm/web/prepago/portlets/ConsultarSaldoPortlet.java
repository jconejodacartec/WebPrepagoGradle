package com.rbm.web.prepago.portlets;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;


public class ConsultarSaldoPortlet extends com.ibm.faces20.portlet.FacesPortlet {
	
	
		

	
	
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
		
	@Override
	public void serveResource(ResourceRequest resRequest,
			ResourceResponse resResponse) throws PortletException, IOException {
		// TODO Auto-generated method stub
		super.serveResource(resRequest, resResponse);
		ConsultarMovimientosPortlet.serveResources(resRequest, resResponse);
		
		
	}
	
	
	
}