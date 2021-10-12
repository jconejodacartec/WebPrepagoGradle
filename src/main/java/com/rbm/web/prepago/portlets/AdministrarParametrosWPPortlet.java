package com.rbm.web.prepago.portlets;

import static com.rbm.web.prepago.util.ConstantesWebPrepago.BEAN_ADMINISTRAR_PARAMETROS;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.portlet.PortletFileUpload;

import com.rbm.web.prepago.exception.ExceptionWebPrepago;
import com.rbm.web.prepago.managedBeans.AdministrarParametrosWPBean;




public class AdministrarParametrosWPPortlet extends com.ibm.faces20.portlet.FacesPortlet {
	
	
		

	
	
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
				AdministrarParametrosWPBean bean2 = null;
				PortletSession portletSession2 = request.getPortletSession(false);
				FileItemStream file = null;
				try {
					PortletFileUpload upload = new PortletFileUpload();
					FileItemIterator iterator = upload.getItemIterator((ActionRequest) request);

					while (iterator.hasNext()) {
						file = iterator.next();
						if (!file.isFormField()) {
							break;
						}
					}

					if (portletSession2 != null) {
						bean2 = (AdministrarParametrosWPBean) portletSession2
								.getAttribute(BEAN_ADMINISTRAR_PARAMETROS);
						if (bean2 == null) {
							HttpSession httpSession = (HttpSession) portletSession2;
							if (httpSession != null) {
								bean2 = (AdministrarParametrosWPBean) httpSession
										.getAttribute(BEAN_ADMINISTRAR_PARAMETROS);
							}
						}
						if (bean2 != null) {
//							bean2.setParametroSelected(5);
							if(bean2.getParametroSelected()==5){
								bean2.cargarArchivo(file);
							}
						}
					}
				} catch (FileUploadException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ExceptionWebPrepago e) {
					e.printStackTrace();
				}
			super.processAction(request, response);
	}
	
	
	
}