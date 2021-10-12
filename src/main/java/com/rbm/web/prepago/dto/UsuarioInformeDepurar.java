package com.rbm.web.prepago.dto;

public class UsuarioInformeDepurar {
	
	private String id;
	private String nombres;
	private String correo;
	private String fechaUltimaConexion;
	
	
	public UsuarioInformeDepurar() {
		
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getNombres() {
		return nombres;
	}


	public void setNombres(String nombres) {
		this.nombres = nombres;
	}


	public String getFechaUltimaConexion() {
		return fechaUltimaConexion;
	}


	public void setFechaUltimaConexion(String fechaUltimaConexion) {
		this.fechaUltimaConexion = fechaUltimaConexion;
	}


	public String getCorreo() {
		return correo;
	}


	public void setCorreo(String correo) {
		this.correo = correo;
	}

}
