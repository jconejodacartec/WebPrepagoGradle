package com.rbm.web.prepago.listener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.ibm.ams.log.Logger;
import com.rbm.web.prepago.dto.Parametro;
import com.rbm.web.prepago.gestores.GestorBD;
import com.rbm.web.prepago.gestores.GestorCache;
import com.rbm.web.prepago.gestores.GestorTarea;
import com.rbm.web.prepago.util.ConstantesWebPrepago;
import org.quartz.SchedulerException;


public class WebPrepagoAplicationListener implements ServletContextListener{
	
	private static Logger logger = Logger.getInstance(ConstantesWebPrepago.NOMBRE_APLICACION, ConstantesWebPrepago.DIRECTORIO_CONFIGURACION);
	private List<Parametro> listaParametros = new ArrayList<Parametro>();
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		logger.debug(this.getClass().getName(), "contextDestroyed", "Deteniendo Aplicación WebPrepago", "");	
		try{
			logger.debug(this.getClass().getName(), "contextDestroyed", "Eliminando todas las tareas de WebPrepago", "");	
			GestorTarea.getInstance().eliminarTareas();
			
		}catch (SchedulerException se) {
			logger.error(this.getClass().getName(), "Error al eliminar todas las Tareas de WebPrepago","", se);
		}
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		try {
			String cron = null;
			this.listaParametros = new ArrayList<Parametro>();
			this.listaParametros = GestorBD.getParametros();
			for(Parametro param: this.listaParametros) {
				if(param.getParametroNombre().equals(ConstantesWebPrepago.PARAMETRO_HORA_EJECUCION_TAREA)) {
					cron = param.getParametroValor();
					break;
				}
			}
			logger.debug(this.getClass().getName(), "contextInitialized", "Iniciando Aplicacion WebPrepago"+cron);
			GestorTarea.getInstance().programarTarea(cron);			
		} catch (SchedulerException e) {
			logger.error(this.getClass().getName(), "Error iniciando todas las Tareas de WebPrepago","", e);
		} catch (Exception e) {
			logger.error(this.getClass().getName(), "Error al iniciar todas las Tareas de WebPrepago","", e);
		} 
	}
	
}