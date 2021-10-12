package com.rbm.web.prepago.dto;

import java.util.Date;

public class Usuario {

	private int tipoDocumento;
	private String numDocumento;
	private String nombres;
	private String apellidos;
	private String correo;
	private String telefono;
	
	private int pregunta1;
	private String  respuesta1;
	private int pregunta2;
	private String respuesta2;
	private int pregunta3;
	private String respuesta3;
	private Date fechaCreacion;
	private int id;
	private TipoDocumento tipoDocum = new TipoDocumento();
	private TarjetaPrepago tarjetaPrepago = new TarjetaPrepago();
	
	
	public Usuario() {

	}
	public int getTipoDocumento() {
		return tipoDocumento;
	}
	public void setTipoDocumento(int tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}
	public String getNumDocumento() {
		return numDocumento;
	}
	public void setNumDocumento(String numDocumento) {
		this.numDocumento = numDocumento;
	}
	public String getNombres() {
		return nombres;
	}
	public void setNombres(String nombres) {
		this.nombres = nombres;
	}
	public String getApellidos() {
		return apellidos;
	}
	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}
	public String getCorreo() {
		return correo;
	}
	public void setCorreo(String correo) {
		this.correo = correo;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public int getPregunta1() {
		return pregunta1;
	}
	public void setPregunta1(int pregunta1) {
		this.pregunta1 = pregunta1;
	}
	public String getRespuesta1() {
		return respuesta1;
	}
	public void setRespuesta1(String respuesta1) {
		this.respuesta1 = respuesta1;
	}
	public int getPregunta2() {
		return pregunta2;
	}
	public void setPregunta2(int pregunta2) {
		this.pregunta2 = pregunta2;
	}
	public String getRespuesta2() {
		return respuesta2;
	}
	public void setRespuesta2(String respuesta2) {
		this.respuesta2 = respuesta2;
	}
	public int getPregunta3() {
		return pregunta3;
	}
	public void setPregunta3(int pregunta3) {
		this.pregunta3 = pregunta3;
	}
	public String getRespuesta3() {
		return respuesta3;
	}
	public void setRespuesta3(String respuesta3) {
		this.respuesta3 = respuesta3;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getFechaCreacion() {
		return fechaCreacion;
	}
	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}
	public TarjetaPrepago getTarjetaPrepago() {
		return tarjetaPrepago;
	}
	public void setTarjetaPrepago(TarjetaPrepago tarjetaPrepago) {
		this.tarjetaPrepago = tarjetaPrepago;
	}
	public TipoDocumento getTipoDocum() {
		return tipoDocum;
	}
	public void setTipoDocum(TipoDocumento tipoDocum) {
		this.tipoDocum = tipoDocum;
	}
}
