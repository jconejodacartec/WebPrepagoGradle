package com.rbm.web.prepago.dto;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "ReporteMovimientos")
public class ReporteMovimientos implements Serializable {

	private static final long serialVersionUID = -7079666525605357722L;

	private String nombreArchivo;
	private String fechaInicial;
	private String fechaFinal;
	private String usuario;
	private String emisor;
	private String impuesto;
	private String saldo;
	private String numTarjeta;
	private String nombreTarjeta;
	private String fechaGeneracion;
	private String usuarioEnmascarado;

	

	private List<RegistroMovimiento> registros;


	@XmlElementWrapper(name = "movimientos")
	@XmlElement(name = "movimiento")
	public List<RegistroMovimiento> getRegistros() {
		return registros;
	}

	public void setRegistros(List<RegistroMovimiento> registros) {
		this.registros = registros;
	}




	public String getNombreArchivo() {
		return nombreArchivo;
	}

	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}



	/**
	 * @return the fechaInicial
	 */
	@XmlElement(name="fechaInicial")
	public String getFechaInicial() {
		return fechaInicial;
	}

	/**
	 * @param fechaInicial the fechaInicial to set
	 */

	public void setFechaInicial(String fechaInicial) {
		this.fechaInicial = fechaInicial;
	}

	/**
	 * @return the fechaFinal
	 */
	public String getFechaFinal() {
		return fechaFinal;
	}

	/**
	 * @param fechaFinal the fechaFinal to set
	 */
	@XmlElement(name="fechaFinal")
	public void setFechaFinal(String fechaFinal) {
		this.fechaFinal = fechaFinal;
	}

	/**
	 * @return the usuario
	 */
	@XmlElement(name="usuario")
	public String getUsuario() {
		return usuario;
	}

	/**
	 * @param usuario the usuario to set
	 */
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	/**
	 * @return the emisor
	 */
	@XmlElement(name="emisor")
	public String getEmisor() {
		return emisor;
	}

	/**
	 * @param emisor the emisor to set
	 */
	public void setEmisor(String emisor) {
		this.emisor = emisor;
	}

	public String getImpuesto() {
		return impuesto;
	}

	@XmlElement(name="impuesto")
	public void setImpuesto(String impuesto) {
		this.impuesto = impuesto;
	}

	@XmlElement(name="saldo")
	public String getSaldo() {
		return saldo;
	}

	public void setSaldo(String saldo) {
		this.saldo = saldo;
	}

	@XmlElement(name="numTarjeta")
	public String getNumTarjeta() {
		return numTarjeta;
	}

	public void setNumTarjeta(String numTarjeta) {
		this.numTarjeta = numTarjeta;
	}

	@XmlElement(name="nombreTarjeta")
	public String getNombreTarjeta() {
		return nombreTarjeta;
	}

	public void setNombreTarjeta(String nombreTarjeta) {
		this.nombreTarjeta = nombreTarjeta;
	}

	@XmlElement(name="fechaGeneracion")
	public String getFechaGeneracion() {
		return fechaGeneracion;
	}

	public void setFechaGeneracion(String fechaGeneracion) {
		this.fechaGeneracion = fechaGeneracion;
	}

	public String getUsuarioEnmascarado() {
		return usuarioEnmascarado;
	}

	public void setUsuarioEnmascarado(String usuarioEnmascarado) {
		this.usuarioEnmascarado = usuarioEnmascarado;
	}
}

