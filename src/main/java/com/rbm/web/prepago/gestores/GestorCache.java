package com.rbm.web.prepago.gestores;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import com.ibm.ams.cache.Cache;
import com.ibm.ams.cache.ManejadorCache;
import com.ibm.ams.log.Logger;
import com.rbm.web.prepago.dto.BIN;
import com.rbm.web.prepago.dto.BinXBolsillo;
import com.rbm.web.prepago.dto.Bolsillo;
import com.rbm.web.prepago.dto.Entidad;
import com.rbm.web.prepago.dto.Parametro;
import com.rbm.web.prepago.dto.Preguntas;
import com.rbm.web.prepago.dto.TipoDocumento;
import com.rbm.web.prepago.dto.TipoTarjeta;
import com.rbm.web.prepago.util.ConstantesWebPrepago;


@ManagedBean(name="gestorCache", eager = true)
@ApplicationScoped
public class GestorCache {
	
	/** Clase logger para gestionar los menajes de logs. */
	private static Logger logger = Logger.getInstance(ConstantesWebPrepago.NOMBRE_APLICACION, ConstantesWebPrepago.DIRECTORIO_CONFIGURACION);
	
	/** Instancia del Manejador de Cache. */
	private static ManejadorCache instance;
	

	public GestorCache() {
		try {
			ManejadorCache.init(System.getProperty(ConstantesWebPrepago.CONFIGURACION_DIR) + "webPrepago"+ File.separatorChar + "ehcache.xml", "webPrepago","webPrepago");
			instance = ManejadorCache.getInstance();
		} catch (Exception e) {
			
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<TipoDocumento> getTipoDocs() throws Exception{

		Cache cacheTipoDocs = instance.getCache(ConstantesWebPrepago.CACHE_TIPO_DOCUMENTO);
		List<TipoDocumento> tipoDocs = new ArrayList<TipoDocumento>();
		// Valida si el cache se vencio para volverlo a cargar
		if(cacheTipoDocs.getSize() == 0 || cacheTipoDocs.get(ConstantesWebPrepago.CACHE_TIPO_DOCUMENTO) == null){
			tipoDocs = GestorBD.getTipoDoc();
			cacheTipoDocs.put(ConstantesWebPrepago.CACHE_TIPO_DOCUMENTO, tipoDocs);
		}else{
			tipoDocs = (ArrayList<TipoDocumento>)cacheTipoDocs.get(ConstantesWebPrepago.CACHE_TIPO_DOCUMENTO);
		}

		return tipoDocs;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Preguntas> getPreguntas() throws Exception{

		Cache cachePreguntas = instance.getCache(ConstantesWebPrepago.CACHE_PREGUNTAS);
		List<Preguntas> preguntas = new ArrayList<Preguntas>();
		// Valida si el cache se vencio para volverlo a cargar
		if(cachePreguntas.getSize() == 0 || cachePreguntas.get(ConstantesWebPrepago.CACHE_PREGUNTAS) == null){
			logger.debug(GestorCache.class.getName(), "getPreguntas", "Cache preguntas expiro, subiendo las preguntas nuevamente");
			preguntas = GestorBD.getPreguntas();
			cachePreguntas.put(ConstantesWebPrepago.CACHE_TIPO_DOCUMENTO, preguntas);
		}else{
			preguntas = (ArrayList<Preguntas>)cachePreguntas.get(ConstantesWebPrepago.CACHE_PREGUNTAS);
		}

		return preguntas;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Parametro> getParametros() throws Exception{

		Cache cacheParametros = instance.getCache(ConstantesWebPrepago.CACHE_PARAMETROS);
		List<Parametro> parametros = new ArrayList<Parametro>();
		// Valida si el cache se vencio para volverlo a cargar
		if(cacheParametros.getSize() == 0 || cacheParametros.get(ConstantesWebPrepago.CACHE_PARAMETROS) == null){
			logger.debug(GestorCache.class.getName(), "getParametros", "Cache parametros expiro, subiendo los parametros nuevamente");
			parametros = cargarParametros();
			cacheParametros.put(ConstantesWebPrepago.CACHE_PARAMETROS, parametros);
		}else{
			parametros = (ArrayList<Parametro>)cacheParametros.get(ConstantesWebPrepago.CACHE_PARAMETROS);
		}

		return parametros;
	}

	private static List<Parametro> cargarParametros (){
		List<Parametro> listaParametros = new ArrayList<Parametro>();
	
		listaParametros.add(new Parametro(Integer.parseInt(ConstantesWebPrepago.PARAMETRO_UNO.split("-")[0]), ConstantesWebPrepago.PARAMETRO_UNO.split("-")[1]));
		//listaParametros.add(new Parametro(Integer.parseInt(ConstantesWebPrepago.PARAMETRO_DOS.split("-")[0]), ConstantesWebPrepago.PARAMETRO_DOS.split("-")[1]));
		listaParametros.add(new Parametro(Integer.parseInt(ConstantesWebPrepago.PARAMETRO_TRES.split("-")[0]), ConstantesWebPrepago.PARAMETRO_TRES.split("-")[1]));
		//listaParametros.add(new Parametro(Integer.parseInt(ConstantesWebPrepago.PARAMETRO_CUATRO.split("-")[0]), ConstantesWebPrepago.PARAMETRO_CUATRO.split("-")[1]));
		listaParametros.add(new Parametro(Integer.parseInt(ConstantesWebPrepago.PARAMETRO_CINCO.split("-")[0]), ConstantesWebPrepago.PARAMETRO_CINCO.split("-")[1]));
		listaParametros.add(new Parametro(Integer.parseInt(ConstantesWebPrepago.PARAMETRO_SEIS.split("-")[0]), ConstantesWebPrepago.PARAMETRO_SEIS.split("-")[1]));
		listaParametros.add(new Parametro(Integer.parseInt(ConstantesWebPrepago.PARAMETRO_SIETE.split("-")[0]), ConstantesWebPrepago.PARAMETRO_SIETE.split("-")[1]));
		listaParametros.add(new Parametro(Integer.parseInt(ConstantesWebPrepago.PARAMETRO_OCHO.split("-")[0]), ConstantesWebPrepago.PARAMETRO_OCHO.split("-")[1]));
		
		
		return listaParametros;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Entidad> getEntidades() throws Exception{

		Cache cacheEntidad = instance.getCache(ConstantesWebPrepago.CACHE_ENTIDAD);
		List<Entidad> entidades = new ArrayList<Entidad>();
		// Valida si el cache se vencio para volverlo a cargar
		if(cacheEntidad.getSize() == 0 || cacheEntidad.get(ConstantesWebPrepago.CACHE_ENTIDAD) == null){
			logger.debug(GestorCache.class.getName(), "getEntidades", "Cache entidades expiro, subiendo las entidades nuevamente");
			entidades = GestorBD.getEntidades();
			cacheEntidad.put(ConstantesWebPrepago.CACHE_ENTIDAD, entidades);
		}else{
			entidades = (ArrayList<Entidad>)cacheEntidad.get(ConstantesWebPrepago.CACHE_ENTIDAD);
		}

		return entidades;
	}
	
	
	@SuppressWarnings("unchecked")
	public static List<TipoTarjeta> getTipoTarjetas() throws Exception{

		Cache cachetipoTar = instance.getCache(ConstantesWebPrepago.CACHE_TIPO_TAR);
		List<TipoTarjeta> tarjetas = new ArrayList<TipoTarjeta>();
		// Valida si el cache se vencio para volverlo a cargar
		if(cachetipoTar.getSize() == 0 || cachetipoTar.get(ConstantesWebPrepago.CACHE_TIPO_TAR) == null){
			logger.debug(GestorCache.class.getName(), "getTipoTarjetas", "Cache tipo tarjeta expiro, subiendo las tarjetas nuevamente");
			tarjetas = GestorBD.getTipoTar();
			cachetipoTar.put(ConstantesWebPrepago.CACHE_TIPO_TAR, tarjetas);
		}else{
			tarjetas = (ArrayList<TipoTarjeta>)cachetipoTar.get(ConstantesWebPrepago.CACHE_TIPO_TAR);
		}

		return tarjetas;
	}
	
	
	@SuppressWarnings("unchecked")
	public static List<BIN> getBin() throws Exception{

		Cache cacheBin = instance.getCache(ConstantesWebPrepago.CACHE_BINES);
		List<BIN> bines = new ArrayList<BIN>();
		// Valida si el cache se vencio para volverlo a cargar
		if(cacheBin.getSize() == 0 || cacheBin.get(ConstantesWebPrepago.CACHE_BINES) == null){
			logger.debug(GestorCache.class.getName(), "getBin", "Cache Bines expiro, subiendo los Bines nuevamente");
			bines = GestorBD.getBines();
			cacheBin.put(ConstantesWebPrepago.CACHE_BINES, bines);
		}else{
			bines = (ArrayList<BIN>)cacheBin.get(ConstantesWebPrepago.CACHE_BINES);
		}

		return bines;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Bolsillo> getBolsillos() throws Exception{
		Cache cacheBolsillo = instance.getCache(ConstantesWebPrepago.CACHE_BOLSILLO);
		List<Bolsillo> bolsillos = new ArrayList<Bolsillo>();
		// Valida si el cache se vencio para volverlo a cargar
		if(cacheBolsillo.getSize() == 0 || cacheBolsillo.get(ConstantesWebPrepago.CACHE_BOLSILLO) == null){
			bolsillos = GestorBD.getBolsillos();
			cacheBolsillo.put(ConstantesWebPrepago.CACHE_BOLSILLO, bolsillos);
		}else{
			bolsillos = (ArrayList<Bolsillo>)cacheBolsillo.get(ConstantesWebPrepago.CACHE_BOLSILLO);
		}
		return bolsillos;
	}
	
	/*public static List<Parametro> getParametrosBD() throws Exception{

		Cache cacheParametros = instance.getCache(ConstantesWebPrepago.CACHE_PARAMETROS_BD);
		List<Parametro> listaParametros = new ArrayList<Parametro>();
		// Valida si el cache se vencio para volverlo a cargar
		if(cacheParametros.getSize() == 0 || cacheParametros.get(ConstantesWebPrepago.CACHE_PARAMETROS_BD) == null){
			logger.debug(GestorCache.class.getName(), "getBin", "Cache días notificar expiro, subiendo los días nuevamente");
			listaParametros = GestorBD.getParametros();
			cacheParametros.put(ConstantesWebPrepago.CACHE_PARAMETROS_BD, listaParametros);
		}else{
			listaParametros = (ArrayList<Parametro>)cacheParametros.get(ConstantesWebPrepago.CACHE_PARAMETROS_BD);
		}

		return listaParametros;
	}*/
	
	@SuppressWarnings("unchecked")
	public static List<BinXBolsillo> getBinXBolsillos() throws Exception{
		Cache cacheBinXBolsillo = instance.getCache(ConstantesWebPrepago.CACHE_BIN_X_BOLSILLO);
		List<BinXBolsillo> BinXbolsillos = new ArrayList<BinXBolsillo>();
		// Valida si el cache se vencio para volverlo a cargar
		if(cacheBinXBolsillo.getSize() == 0 || cacheBinXBolsillo.get(ConstantesWebPrepago.CACHE_BIN_X_BOLSILLO) == null){
			BinXbolsillos = GestorBD.getBinXBolsillos();
			cacheBinXBolsillo.put(ConstantesWebPrepago.CACHE_BIN_X_BOLSILLO, BinXbolsillos);
		}else{
			BinXbolsillos = (ArrayList<BinXBolsillo>)cacheBinXBolsillo.get(ConstantesWebPrepago.CACHE_BIN_X_BOLSILLO);
		}
		return BinXbolsillos;
	}
	
	public static void limpiarCache() {	
		instance.clearAll();
	}

}
