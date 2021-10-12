package com.rbm.web.prepago.configuracion;

import javax.xml.bind.annotation.XmlType;


@XmlType(propOrder = {"nombre",
		"query",
		"timeout"})

public class Sentencia {
	
	/** Nmbre de la sentencia. */
	private String nombre;

	/** Query. */
	private String query;

	/** Timeout para ejecutar el query. */
	private int timeout;

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

}
