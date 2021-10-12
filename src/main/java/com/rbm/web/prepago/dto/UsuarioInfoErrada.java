package com.rbm.web.prepago.dto;

public class UsuarioInfoErrada {
	
	private int id;
	private String usuario;
	private String numTarjetaEnmascarado;
	private String numTarjetaEncriptado;
	private String entidad;
	private String fechaRegistro;
	private String correo;
	private boolean eliminar;
	private boolean eliminarYnotificar;
	
	
	
	public UsuarioInfoErrada() {
		super();
		// TODO Apendice de constructor generado automaticamente
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public String getNumTarjetaEnmascarado() {
		return numTarjetaEnmascarado;
	}
	public void setNumTarjetaEnmascarado(String numTarjetaEnmascarado) {
		this.numTarjetaEnmascarado = numTarjetaEnmascarado;
	}
	public String getNumTarjetaEncriptado() {
		return numTarjetaEncriptado;
	}
	public void setNumTarjetaEncriptado(String numTarjetaEncriptado) {
		this.numTarjetaEncriptado = numTarjetaEncriptado;
	}
	public String getEntidad() {
		return entidad;
	}
	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}
	public String getFechaRegistro() {
		return fechaRegistro;
	}
	public void setFechaRegistro(String fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public boolean isEliminar() {
		return eliminar;
	}

	public void setEliminar(boolean eliminar) {
		this.eliminar = eliminar;
	}

	public boolean isEliminarYnotificar() {
		return eliminarYnotificar;
	}

	public void setEliminarYnotificar(boolean eliminarYnotificar) {
		this.eliminarYnotificar = eliminarYnotificar;
	}
	

}
