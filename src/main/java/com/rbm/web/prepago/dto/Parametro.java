package com.rbm.web.prepago.dto;

public class Parametro {
	
	private int idParametro;
	private String parametroNombre;
	private String parametroValor;
	
	
	
	public Parametro(int idParametro, String parametro) {
		super();
		this.setIdParametro(idParametro);
		this.setParametroNombre(parametro);
	}
	
	public Parametro() {
		super();
	}

	public int getIdParametro() {
		return idParametro;
	}

	public void setIdParametro(int idParametro) {
		this.idParametro = idParametro;
	}

	public String getParametroNombre() {
		return parametroNombre;
	}

	public void setParametroNombre(String parametroNombre) {
		this.parametroNombre = parametroNombre;
	}

	public String getParametroValor() {
		return parametroValor;
	}

	public void setParametroValor(String parametroValor) {
		this.parametroValor = parametroValor;
	}
	

}
