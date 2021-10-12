package com.rbm.web.prepago.dto;

public class Preguntas {

	private int id;
	private String pregunta;
	private int lista;
	
	public Preguntas() {
		super();
		// TODO Apendice de constructor generado automaticamente
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPregunta() {
		return pregunta;
	}
	public void setPregunta(String pregunta) {
		this.pregunta = pregunta;
	}
	public int getLista() {
		return lista;
	}
	public void setLista(int lista) {
		this.lista = lista;
	}
}
