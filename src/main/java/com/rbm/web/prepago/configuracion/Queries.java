package com.rbm.web.prepago.configuracion;

import java.util.ArrayList;
import java.util.List;


public class Queries {
	
	
	/** Lista de sentencias . */
	private List<Sentencia> sentencia;

	/**
	 * Gets the sentencia.
	 *
	 * @return the sentencia
	 */
	public List<Sentencia> getSentencia() {
		if(sentencia == null){
			sentencia = new ArrayList<Sentencia>();
		}
		return sentencia;
	}

	/**
	 * Sets the sentencia.
	 *
	 * @param sentencia the new sentencia
	 */
	public void setSentencia(List<Sentencia> sentencia) {
		this.sentencia = sentencia;
	}

}
