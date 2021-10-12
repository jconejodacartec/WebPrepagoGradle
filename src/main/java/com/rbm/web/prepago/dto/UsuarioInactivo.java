package com.rbm.web.prepago.dto;

import java.io.Serializable;

public class UsuarioInactivo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String usuario;
	private String fechaCreacion;
	private String tiempoInactividad;
	private String fechaNotificacion;
	private String fechaUltimaConexion;
	private String numTarjetaEnmascarada;
	private boolean eliminar;
	private boolean notificar;
	private boolean deshabilitarEliminar;
	private boolean deshabilitarNotificar;
	private String entidad;
	private String correo;
	private String nombres;
	
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public String getFechaCreacion() {
		return fechaCreacion;
	}
	public void setFechaCreacion(String fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}
	public String getTiempoInactividad() {
		return tiempoInactividad;
	}
	public void setTiempoInactividad(String tiempoInactividad) {
		this.tiempoInactividad = tiempoInactividad;
	}
	public String getFechaNotificacion() {
		return fechaNotificacion;
	}
	public void setFechaNotificacion(String fechaNotificacion) {
		this.fechaNotificacion = fechaNotificacion;
	}
	
	public String getFechaUltimaConexion() {
		return fechaUltimaConexion;
	}
	public void setFechaUltimaConexion(String fechaUltimaConexion) {
		this.fechaUltimaConexion = fechaUltimaConexion;
	}
	public boolean isEliminar() {
		return eliminar;
	}
	public void setEliminar(boolean eliminar) {
		this.eliminar = eliminar;
	}
	public boolean isNotificar() {
		return notificar;
	}
	public void setNotificar(boolean notificar) {
		this.notificar = notificar;
	}
	public boolean isDeshabilitarEliminar() {
		return deshabilitarEliminar;
	}
	public void setDeshabilitarEliminar(boolean deshabilitarEliminar) {
		this.deshabilitarEliminar = deshabilitarEliminar;
	}
	public boolean isDeshabilitarNotificar() {
		return deshabilitarNotificar;
	}
	public void setDeshabilitarNotificar(boolean deshabilitarNotificar) {
		this.deshabilitarNotificar = deshabilitarNotificar;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNumTarjetaEnmascarada() {
		return numTarjetaEnmascarada;
	}
	public void setNumTarjetaEnmascarada(String numTarjetaEnmascarada) {
		this.numTarjetaEnmascarada = numTarjetaEnmascarada;
	}
	public String getEntidad() {
		return entidad;
	}
	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}
	public String getCorreo() {
		return correo;
	}
	public void setCorreo(String correo) {
		this.correo = correo;
	}
	public String getNombres() {
		return nombres;
	}
	public void setNombres(String nombres) {
		this.nombres = nombres;
	}
	
	
}
