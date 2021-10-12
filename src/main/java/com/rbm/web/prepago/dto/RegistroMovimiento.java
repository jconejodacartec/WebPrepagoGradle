package com.rbm.web.prepago.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;



@XmlRootElement(name = "com.rbm.web.prepago.dto.ReporteMovimientos")
public class RegistroMovimiento implements Serializable {

	private static final long serialVersionUID = 7611933709598618759L;
	
	private String fechaTransaccion;
	private String hora;
	private String ubicacion;
	private String tipoTransaccion;
	private String valorTransaccion;
	private String respTransaccion;
	private String valorComision;
	private String respCobroComision;
	private String valorImpuesto;
	private String bolsillo;
	

	public String getFechaTransaccion() {
		return fechaTransaccion;
	}
	public void setFechaTransaccion(String fechaTransaccion) {
		this.fechaTransaccion = fechaTransaccion;
	}
	public String getHora() {
		return hora;
	}
	public void setHora(String hora) {
		this.hora = hora;
	}
	public String getUbicacion() {
		return ubicacion;
	}
	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}
	public String getTipoTransaccion() {
		return tipoTransaccion;
	}
	public void setTipoTransaccion(String tipoTransaccion) {
		this.tipoTransaccion = tipoTransaccion;
	}
	public String getValorTransaccion() {
		return valorTransaccion;
	}
	public void setValorTransaccion(String valorTransaccion) {
		this.valorTransaccion = valorTransaccion;
	}
	public String getRespTransaccion() {
		return respTransaccion;
	}
	public void setRespTransaccion(String respTransaccion) {
		this.respTransaccion = respTransaccion;
	}
	public String getValorComision() {
		return valorComision;
	}
	public void setValorComision(String valorComision) {
		this.valorComision = valorComision;
	}
	public String getRespCobroComision() {
		return respCobroComision;
	}
	public void setRespCobroComision(String respCobroComision) {
		this.respCobroComision = respCobroComision;
	}
	public String getValorImpuesto() {
		return valorImpuesto;
	}
	public void setValorImpuesto(String valorImpuesto) {
		this.valorImpuesto = valorImpuesto;
	}
	public String getBolsillo() {
		return bolsillo;
	}
	public void setBolsillo(String bolsillo) {
		this.bolsillo = bolsillo;
	}
	
	


}
