package com.rbm.web.prepago.configuracion;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;


	
	@AutoProperty
	@XmlRootElement(name = "webPrepago")
	@XmlType(propOrder = { 
			"urlAsignarConstrasena",
			"urlLogin",
			"impuesto",
			"dataSource",
			"bpodataSource",
			"abreviatura",
			"grupo",
			"queries",
			})
	public class Configuracion {

		private String urlAsignarConstrasena;
		
		private String urlLogin;
		
		
		private String impuesto;
		
		private String abreviatura;
		
		private String grupo;
		
		/** JNDI del datasource configurado en el servidor. */
		private String dataSource;
		
		/** JNDI del  BPO configurado en el servidor. */
		private String bpodataSource;

		public String getAbreviatura() {
			return abreviatura;
		}

		public void setAbreviatura(String abreviatura) {
			this.abreviatura = abreviatura;
		}

		public String getGrupo() {
			return grupo;
		}

		public void setGrupo(String grupo) {
			this.grupo = grupo;
		}

		/** Objeto queries. */
		private Queries queries;
		
		

		/**
		 * Gets the queries.
		 * 
		 * @return the queries
		 */
		public Queries getQueries() {
			return queries;
		}

		/**
		 * Sets the queries.
		 * 
		 * @param queries
		 *            the new queries
		 */
		public void setQueries(Queries queries) {
			this.queries = queries;
		}

		/**
		 * Gets the data source.
		 * 
		 * @return the data source
		 */
		public String getDataSource() {
			return dataSource;
		}

		/**
		 * Sets the data source.
		 * 
		 * @param dataSource
		 *            the new data source
		 */
		public void setDataSource(String dataSource) {
			this.dataSource = dataSource;
		}


		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return Pojomatic.toString(this);
		}

		public String getUrlAsignarConstrasena() {
			return urlAsignarConstrasena;
		}

		public void setUrlAsignarConstrasena(String urlAsignarConstrasena) {
			this.urlAsignarConstrasena = urlAsignarConstrasena;
		}

		public String getUrlLogin() {
			return urlLogin;
		}

		public void setUrlLogin(String urlLogin) {
			this.urlLogin = urlLogin;
		}

		public String getImpuesto() {
			return impuesto;
		}

		public void setImpuesto(String impuesto) {
			this.impuesto = impuesto;
		}

		public String getBpodataSource() {
			return bpodataSource;
		}

		public void setBpodataSource(String bpodataSource) {
			this.bpodataSource = bpodataSource;
		}


}
