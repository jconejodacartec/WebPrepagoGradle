package com.rbm.web.prepago.dto;

public class TipoDocumento {

	private int id;
	private String descripcion;
	private int longitudMax;

	public TipoDocumento() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public int getLongitudMax() {
		return longitudMax;
	}

	public void setLongitudMax(int longitudMax) {
		this.longitudMax = longitudMax;
	}
}
