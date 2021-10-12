package com.rbm.web.prepago.gestores;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.ibm.ams.audit.dto.Application;
import com.ibm.ams.audit.dto.AuditDto;
import com.ibm.ams.audit.dto.EventDto;
import com.ibm.ams.audit.dto.SeverityEnum;
import com.ibm.ams.audit.exception.AuditException;
import com.ibm.ams.audit.manager.AuditController;
import com.ibm.ams.log.Logger;
import com.ibm.ams.util.AuditConstList;
import com.rbm.web.prepago.util.ConstantesWebPrepago;



/**
 * 
 * @author cduartea
 * 
 */
public class GestorAuditoria {

	private static Logger logger = Logger.getInstance(ConstantesWebPrepago.NOMBRE_APLICACION, ConstantesWebPrepago.DIRECTORIO_CONFIGURACION);
	private static volatile GestorAuditoria gestorAuditoria = null;
	private AuditController controller;

	/**
	 * Constructor
	 */
	private GestorAuditoria() {
		
		controller = new AuditController();

	}
	
	/**
	 * Metodo encargado de realizar la auditoria de la aplicacion SIFRA
	 * 
	 */
	public void registrarAuditoria(SeverityEnum severidad, String eventoExitoso, int eventTypeId, String mensaje, String usuarioLogin, String IP, String originClass) {
		AuditDto dto = new AuditDto();
		EventDto eventType = new EventDto();
		Application application = new Application();
		
		eventType.setId(eventTypeId);
		application.setId(AuditConstList.APLICACION_HCWEB);
		
		try{
			String ipServidor = InetAddress.getLocalHost().getHostAddress();
			
			dto.setUser(usuarioLogin);
			dto.setEventDate(new java.util.Date());
			dto.setEventType(eventType);
			dto.setEventOriginApp(application);
			dto.setEventOriginClass(originClass);
			dto.setEventUserIp(IP);
			dto.setEventOriginIp(ipServidor);
			dto.setEventSuccess(eventoExitoso);
			dto.setEventSeverity(severidad);
			dto.setEventMessage(mensaje);
			dto.setEventSeverityName(eventoExitoso.toString());
		
			controller.audit(dto);
		} catch (AuditException e) {
			logger.error(this.getClass().getName(), "registrarAuditoria", "Error registrando evento de auditoria: ", e);
		} catch (UnknownHostException e) {
			logger.error(this.getClass().getName(), "registrarAuditoria", "Error registrando evento de auditoria: ", e);
		}
	}
	

	/**
	 * Metodo que obtiene una instancia de la clase GestorSIFRA
	 * 
	 * @return instancia de la clase
	 */
	public static synchronized GestorAuditoria getInstance() {
		if (gestorAuditoria == null) {
			gestorAuditoria = new GestorAuditoria();
		}
		return gestorAuditoria;
	}


}
