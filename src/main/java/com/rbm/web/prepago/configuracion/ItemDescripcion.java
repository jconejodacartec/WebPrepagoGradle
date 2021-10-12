package com.rbm.web.prepago.configuracion;

import java.io.Serializable;

public class ItemDescripcion implements Serializable,Comparable<ItemDescripcion>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Identificador del catalogo
	 */
	private String codigo;
	/**
	 * Descripcion del catalogo
	 */
	private String nombre;
	
	/**
	 * Constructor por defecto de la clase
	 */
	public ItemDescripcion() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructor con parametros de ItemDescripcion
	 * @param codigo identificador a darle al catalogo
	 * @param nombre Descripcion del catalogo
	 */
	public ItemDescripcion(String codigo, String nombre) {
		super();
		this.codigo = codigo;
		this.nombre = nombre;
	}

	/**
	 * Get del identificador del catalogo
	 * @return Codifo del catalogo
	 */
	public String getCodigo() {
		return codigo;
	}

	/**
	 * Set del identificador del catalogo
	 * @param codigo Identificador a asociar a la instancia del catalogo
	 */
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	/**
	 * Get de la Descripcion del catalogo
	 * @return Descripcion del catalogo
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * Set de la descripcion del catalogo
	 * @param nombre Descripcion del catalogo a dar  a la instancia del cataogo
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * Metodo encargado de comparar un objeto ItemDescripcion con otro
	 * @param ItemDescripcion a comparar con la instancia actual
	 */
	@Override
	public int compareTo(ItemDescripcion o) {
		return this.codigo.compareTo(o.codigo);
	}

	
	@Override
	public String toString() {
		return codigo;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ItemDescripcion other = (ItemDescripcion) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}
	
	

}
