package com.rbm.web.prepago.dto;

import java.util.Date;

public class Historico {
	
	private int id;
	private String usuario;
	private Date fechaRegistro;
	private String numtarjeta;
	private String entidad;
	private String accion;
	
	
	
	
	public Historico() {
		super();
		// TODO Apendice de constructor generado automaticamente
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getFechaRegistro() {
		return fechaRegistro;
	}
	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}
	public String getNumtarjeta() {
		return numtarjeta;
	}
	public void setNumtarjeta(String numtarjeta) {
		this.numtarjeta = numtarjeta;
	}
	public String getEntidad() {
		return entidad;
	}
	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}
	public String getAccion() {
		return accion;
	}
	public void setAccion(String accion) {
		this.accion = accion;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

}
