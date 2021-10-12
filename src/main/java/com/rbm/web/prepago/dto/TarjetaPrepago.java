package com.rbm.web.prepago.dto;

import com.rbm.web.prepago.util.ConstantesWebPrepago;

public class TarjetaPrepago {
	
	private String numeroTarjeta="";
	private String numeroTarjetaEnmascarado="";
	private String entidad;
	private String nombreTarjeta="";
	private Double saldo = 0.0;
	private String saldoFormatoDecimal;
	private String estado;
	private String tipoDocumento;
	private String numDocumento;
	private String fechaRegistro;
	private String tipoTarjeta;
	private TipoTarjeta tipTarjeta = new TipoTarjeta();
	private Entidad enti = new Entidad();
	private String rutaIconoTarjeta;
	private String numTarjeta="";
	private String bolsillo="";
	
	
	public String getNumeroTarjeta() {
		return numeroTarjeta;
	}
	public void setNumeroTarjeta(String numeroTarjeta) {
		this.numeroTarjeta = numeroTarjeta;
		enmascarar();
	}
	
	private void enmascarar() {
		if(this.numeroTarjeta.length() == 13){
			this.numeroTarjetaEnmascarado = this.numeroTarjeta.substring(0,6) + "****" + this.numeroTarjeta.substring(9,13);
		}
		else if(this.numeroTarjeta.length() == 14){
			this.numeroTarjetaEnmascarado = this.numeroTarjeta.substring(0,6) + "****" + this.numeroTarjeta.substring(9,14);
		}
		else if(this.numeroTarjeta.length() == 16){
			this.numeroTarjetaEnmascarado = this.numeroTarjeta.substring(0,6) + "******" + this.numeroTarjeta.substring(11,16);
		}
		else if(this.numeroTarjeta.length() == 19){
			this.numeroTarjetaEnmascarado = this.numeroTarjeta.substring(0,9) + "******" + this.numeroTarjeta.substring(15,19);
		}
		else{
			this.numeroTarjetaEnmascarado = this.numeroTarjeta;
		}
		
	}
	public String getEntidad() {
		return entidad;
	}
	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}
	public String getNombreTarjeta() {
		return nombreTarjeta;
	}
	public void setNombreTarjeta(String nombreTarjeta) {
		this.nombreTarjeta = nombreTarjeta;
	}
	public Double getSaldo() {
		return saldo;
	}
	public void setSaldo(Double saldo) {
		this.saldo = saldo;
		if(saldo!=null){
			this.setSaldoFormatoDecimal(ConstantesWebPrepago.FORMATO_VALORES.format(saldo));
		}
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	/**
	 * @return the tipoDocumento
	 */
	public String getTipoDocumento() {
		return tipoDocumento;
	}
	/**
	 * @param tipoDocumento the tipoDocumento to set
	 */
	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}
	/**
	 * @return the numDocumento
	 */
	public String getNumDocumento() {
		return numDocumento;
	}
	/**
	 * @param numDocumento the numDocumento to set
	 */
	public void setNumDocumento(String numDocumento) {
		this.numDocumento = numDocumento;
	}
	/**
	 * @return the fechaRegistro
	 */
	public String getFechaRegistro() {
		return fechaRegistro;
	}
	/**
	 * @param fechaRegistro the fechaRegistro to set
	 */
	public void setFechaRegistro(String fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}
	public String getTipoTarjeta() {
		return tipoTarjeta;
	}
	public void setTipoTarjeta(String tipoTarjeta) {
		this.tipoTarjeta = tipoTarjeta;
	}
	public String getNumeroTarjetaEnmascarado() {
		return numeroTarjetaEnmascarado;
	}
	public void setNumeroTarjetaEnmascarado(String numeroTarjetaEnmascarado) {
		this.numeroTarjetaEnmascarado = numeroTarjetaEnmascarado;
	}
	public String getSaldoFormatoDecimal() {
		return saldoFormatoDecimal;
	}
	public void setSaldoFormatoDecimal(String saldoFormatoDecimal) {
		this.saldoFormatoDecimal = saldoFormatoDecimal;
	}
	public Entidad getEnti() {
		return enti;
	}
	public void setEnti(Entidad enti) {
		this.enti = enti;
	}
	public TipoTarjeta getTipTarjeta() {
		return tipTarjeta;
	}
	public void setTipTarjeta(TipoTarjeta tipTarjeta) {
		this.tipTarjeta = tipTarjeta;
	}
	public String getRutaIconoTarjeta() {
		return rutaIconoTarjeta;
	}
	public void setRutaIconoTarjeta(String rutaIconoTarjeta) {
		this.rutaIconoTarjeta = rutaIconoTarjeta;
	}
	public String getNumTarjeta() {
		return numTarjeta;
	}
	public void setNumTarjeta(String numTarjeta) {
		this.numTarjeta = numTarjeta;
	}
	public String getBolsillo() {
		return bolsillo;
	}
	public void setBolsillo(String bolsillo) {
		this.bolsillo = bolsillo;
	}	
	
	

}
