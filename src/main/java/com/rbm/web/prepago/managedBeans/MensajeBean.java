/**
 * 
 */
package com.rbm.web.prepago.managedBeans;


import javax.faces.application.FacesMessage;
import javax.faces.component.html.HtmlForm;
import javax.faces.context.FacesContext;

import org.primefaces.component.messages.Messages;
import com.ibm.faces20.portlet.component.PortletResourceURL;
import com.rbm.web.prepago.pagecode.PageCodeBase;

import org.primefaces.component.panel.Panel;
import org.primefaces.context.RequestContext;

/**
 * @author jprobayo
 *
 */
public class MensajeBean extends PageCodeBase {

	protected HtmlForm formularioConsultarCargueMasivo;
	protected Messages mensajes;
	protected PortletResourceURL ReporteConsultaCargueMasivoExcel;
	protected Panel panelGeneral;
	protected Panel panelFiltros;
	
	
	
	public MensajeBean() {
		FacesContext.getCurrentInstance().addMessage("mensajes", new FacesMessage(FacesMessage.SEVERITY_ERROR, "se elimino el texto",""));
		RequestContext.getCurrentInstance().update("panelGeneral");
	}
	
	

	
	protected HtmlForm getFormularioConsultarCargueMasivo() {
		if (formularioConsultarCargueMasivo == null) {
			formularioConsultarCargueMasivo = (HtmlForm) findComponentInRoot("formularioConsultarCargueMasivo");
		}
		return formularioConsultarCargueMasivo;
	}

	protected Messages getMensajes() {
		if (mensajes == null) {
			mensajes = (Messages) findComponentInRoot("mensajes");
		}
		return mensajes;
	}

	protected Panel getPanelGeneral() {
		if (panelGeneral == null) {
			panelGeneral = (Panel) findComponentInRoot("panelGeneral");
		}
		return panelGeneral;
	}

	protected Panel getPanelFiltros() {
		if (panelFiltros == null) {
			panelFiltros = (Panel) findComponentInRoot("panelFiltros");
		}
		return panelFiltros;
	}

}