package com.rbm.web.prepago.converter;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import com.rbm.web.prepago.configuracion.ItemDescripcion;



/**
*   <b>IBM. Global Bussiness Services AMS Colombia .</b>
*   
*   <p><b>Descripcion: </b>
*   
*   <p> Permite realizar la convercion de un String a objeto de tipo ItemDescripcion
*   	y de ItemDescripcion a String. 	  	   	
*   
* 	<p><b>Notas: </b>
*     
* 	<p><b>Proyecto base: </b> Compensacion
* 
*   @author dprojas@co.ibm.com IBM - Diana Rojas
*   <p><b>Fecha de creacion(13/sep/2013): </b> 
* 
*   @version [1.0, 13/sep/2013]
*   <p><b>ChangeLog: </b>
*   
*	<p> Version: 1.0
*	<p>- Implementacion de conversores
*	
*	<p> Version: 1.1 
*	<p> Implementacion APIDoc.
*
**/
@FacesConverter("ItemDescripcionConverter")
public class ItemDescripcionConverter implements Converter {

	public ItemDescripcionConverter() {
	}

	/**
	* Metodo que permite la conversion de un String a Objeto de tipo ItemDescripcion.
	**/
	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2)throws ConverterException {
		if (arg2.trim().equals("")) {  
            return null;  
        } else { 
        	ItemDescripcion item = new ItemDescripcion(arg2, null);
        	return item;
        }
	}

	/**
	* Metodo que permite la conversion de un Objeto de tipo ItemDescripcion a String.
	**/
	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2)throws ConverterException {
		if(arg2 == null || arg2.equals("")){
			return "";
		}
		if(arg2 instanceof ItemDescripcion){
			return ((ItemDescripcion) arg2).getCodigo();
		}else{
			FacesMessage msg = new FacesMessage("Error de Conversion ItemDescripcion.",
											     "El objeto a comvertir no es del tipo Item Descripción");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ConverterException(msg);
		}
	}

}
