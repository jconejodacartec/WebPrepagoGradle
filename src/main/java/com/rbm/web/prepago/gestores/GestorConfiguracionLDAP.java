package com.rbm.web.prepago.gestores;

import java.io.File;
import java.io.FileNotFoundException;

import com.ibm.ams.excepcion.XmlToDtoException;
import com.ibm.ams.ldap.config.ConfiguracionMapeoLdap;
import com.ibm.ams.ldap.config.ConfiguracionParametrosLdap;
import com.ibm.ams.utils.XmlToDto;
import com.rbm.web.prepago.configuracion.Configuracion;
import com.rbm.web.prepago.util.ConstantesWebPrepago;


/**
*   <b>IBM. Global Bussiness Services AMS Colombia .</b>
*
*   <p><b>Descripcion: </b>
*   <p>Gestor encargado de cargar la configuracion del aplicativo
*
* 	<p><b>Notas: </b>
*   <p>Mantener el Archivo de configuracion PagoElectronicoWeb_Conf.xml en la carpeta de pagoelectronicoweb en config
*
* 	<p><b>Proyecto base: </b> PagoElectronicoWeb
*
*   @author <A HREF="mailto:damejia@co.ibm.com">Daniel Mejoa Rojas</A>
*   <p><b>Fecha de creacion(dd/mmm/aaaa):</b> 05/sep/2013
*
*   @version 1.0, 05/sep/2013
*   <p><b>ChangeLog:</b>
*	<p>Version: 1.0
*	<p>- Creacion de la clase.
*	<p>- Creacion de documentacion.
**/
public class GestorConfiguracionLDAP {
	/** El path del archivo de configuracion del aplicativo. */
	private static String pathArchivoConf = System.getProperty(ConstantesWebPrepago.CONFIGURACION_DIR) + ConstantesWebPrepago.DIRECTORIO_CONFIGURACION +File.separatorChar + ConstantesWebPrepago.CONFIGURACION_ARCHIVO_WEB_PREPAGO;

	/** El archivo. */
	private static File archivo = new File(pathArchivoConf);

	/** Objeto configuracion. */
	private static Configuracion configuracion = new Configuracion();

	/**
	 * Obtener configuracion. obtiene la configuracion apartir del archivo y carga la informacion en el objeto
	 *
	 * @return the configuracion
	 * @throws XmlToDtoException the xml to dto exception
	 */
	public static Configuracion obtenerConfiguracion() throws Exception{
		if(archivo.exists()){
			XmlToDto<Configuracion> converter = new XmlToDto<Configuracion>();
			configuracion = converter.getDto(configuracion, archivo);
			return configuracion;
		}
		else{
			throw new FileNotFoundException("el Archivo de configuracion no se encontron en: " + pathArchivoConf);
		}

	}
	/**
	 * obtenerConfiguracionLdap. obtiene la configuracion del LDAP apartir del archivo y carga la informacion en el objeto
	 *
	 * @return the configuracion
	 * @throws XmlToDtoException the xml to dto exception
	 */
	public static ConfiguracionParametrosLdap obtenerConfiguracionLdap(ConfiguracionParametrosLdap configP) throws Exception{
		XmlToDto<ConfiguracionMapeoLdap> parser = new XmlToDto<ConfiguracionMapeoLdap>();
		File archivoconfig = new File(ConstantesWebPrepago.PATH_CONF_LDAP);
		if(archivoconfig.exists()){
				return  parser.getDto(configP, archivoconfig);
		}else{
				throw new FileNotFoundException("el Archivo de configuracion no se encontron en: " + ConstantesWebPrepago.PATH_CONF_LDAP);
		}

	}
	/**
	 * obtenerConfiguracionLdap. obtiene la configuracion del mapeo LDAP apartir del archivo y carga la informacion en el objeto
	 *
	 * @return the configuracion
	 * @throws XmlToDtoException the xml to dto exception
	 */
	public static ConfiguracionMapeoLdap obtenerConfiguracionLdapMapeo(ConfiguracionMapeoLdap config) throws Exception{
		XmlToDto<ConfiguracionMapeoLdap> parser = new XmlToDto<ConfiguracionMapeoLdap>();
		File archivoMapping = new File(ConstantesWebPrepago.PATH_MAPPING_LDAP);
		if(archivoMapping.exists()){
				return parser.getDto(config, archivoMapping);
		}else{
				throw new FileNotFoundException("el Archivo de configuracion no se encontron en: " + ConstantesWebPrepago.PATH_MAPPING_LDAP);
		}

	}
}

