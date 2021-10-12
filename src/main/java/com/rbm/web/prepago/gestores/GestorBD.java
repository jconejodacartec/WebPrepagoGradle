package com.rbm.web.prepago.gestores;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import com.ibm.ams.db.ManejadorDeDatosDB;
import com.ibm.ams.log.Logger;
import com.ibm.ws.webservices.xml.waswebservices.parameter;
import com.rbm.web.prepago.configuracion.Configuracion;
import com.rbm.web.prepago.configuracion.Sentencia;
import com.rbm.web.prepago.dto.BIN;
import com.rbm.web.prepago.dto.BinXBolsillo;
import com.rbm.web.prepago.dto.Bolsillo;
import com.rbm.web.prepago.dto.CodigosTransaccion;
import com.rbm.web.prepago.dto.Entidad;
import com.rbm.web.prepago.dto.Estado;
import com.rbm.web.prepago.dto.Historico;
import com.rbm.web.prepago.dto.ParamBines;
import com.rbm.web.prepago.dto.Parametro;
import com.rbm.web.prepago.dto.Preguntas;
import com.rbm.web.prepago.dto.RegistroMovimiento;
import com.rbm.web.prepago.dto.SaldoXBolsillo;
import com.rbm.web.prepago.dto.TarjetaPrepago;
import com.rbm.web.prepago.dto.TipoDocumento;
import com.rbm.web.prepago.dto.TipoTarjeta;
import com.rbm.web.prepago.dto.Usuario;
import com.rbm.web.prepago.dto.UsuarioInactivo;
import com.rbm.web.prepago.dto.UsuarioInfoErrada;
import com.rbm.web.prepago.exception.ExceptionWebPrepago;
import com.rbm.web.prepago.util.ConstantesWebPrepago;
//import org.apache.openjpa.persistence.criteria.Expressions.Substring;

public class GestorBD {

	/** Clase logger para gestionar los menajes de logs. */
	private static Logger logger = Logger.getInstance(
			ConstantesWebPrepago.NOMBRE_APLICACION,
			ConstantesWebPrepago.DIRECTORIO_CONFIGURACION);

	private static String key = "Bar12345Bar12345"; // 128 bit key
	private static String initVector = "RandomInitVector"; // 16 bytes IV
	private static String initVector2 = "12345678901234567890123456789012"; // 16 bytes IV

	public static List<TipoDocumento> getTipoDoc() throws ExceptionWebPrepago, SQLException {
		Configuracion conf = null;
		Connection conexion = null;
		TipoDocumento tipoDoc;
		ResultSet rst = null;
		List<TipoDocumento> tipoDocs = new ArrayList<TipoDocumento>();

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para getTipoDoc");
			}

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_OBTENER_TIPO_DOCUMENTOS);
			if(sentencia == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para getTipoDoc");
			}
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getDataSource());

			logger.info(GestorBD.class.getName(), "getTipoDoc",
					"Obteniendo tipo documentos");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), sentencia.getTimeout());
			// Recorre los resultados de las busquedas
			while (rst.next()) {
				tipoDoc = new TipoDocumento();
				tipoDoc.setId(rst.getInt("ID_TIPODOCUMENTO"));
				tipoDoc.setDescripcion(rst.getString("TDOC_DESCRIPCION"));
				tipoDoc.setLongitudMax(rst.getInt("TDOC_LONGITUD_MAX"));
				
				tipoDocs.add(tipoDoc);
			}
			logger.info(GestorBD.class.getName(), "getTipoDoc",
					"Agregando tipoDoc exitoso, cantidad: " + tipoDocs.size());
		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "getTipoDoc",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "getTipoDoc",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
			
				// si el resulset no es nullo cierra la conexion
				if (rst != null) {
					rst.close();
				}
		}
		return tipoDocs;
	}

	private static Sentencia obtenerSentencia(Configuracion conf,
			String nombreQuery) {
		// Validaciones de que lo necesario para buscar un query no sea nulo
		if (conf != null) {
			if (conf.getQueries() != null) {
				if (conf.getQueries().getSentencia() != null) {
					for (Sentencia sentencia : conf.getQueries().getSentencia()) {
						if (nombreQuery.equals(sentencia.getNombre())) {
							return sentencia;
						}
					}
				}
			}
		}
		return null;
	}

	public static List<Preguntas> getPreguntas() throws ExceptionWebPrepago, SQLException {
		Configuracion conf = null;
		Connection conexion = null;
		Preguntas pre = null;
		ResultSet rst = null;
		List<Preguntas> listaPreguntas = new ArrayList<Preguntas>();

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para getPreguntas");
			}

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_OBTENER_PREGUNTAS);
			if(sentencia == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para getPreguntas");
			}
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getDataSource());

			logger.info(GestorBD.class.getName(), "getPreguntas",
					"Obteniendo preguntas");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), sentencia.getTimeout());
			// Recorre los resultados de las busquedas
			while (rst.next()) {
				pre = new Preguntas();
				pre.setId(rst.getInt("ID_PREGUNTA"));
				pre.setPregunta(rst.getString("PRE_PREGUNTA"));
				pre.setLista(rst.getInt("PRE_LISTA"));

				listaPreguntas.add(pre);
			}
			logger.info(GestorBD.class.getName(), "getPreguntas",
					"Agregando pregunta exitoso, cantidad: " + listaPreguntas.size());
		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "getPreguntas",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "getPreguntas",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
				// si el resulset no es nullo cierra la conexion
			if (rst != null) {
				rst.close();
			}
		}
		return listaPreguntas;
	}

	public static Connection getConexion() throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion = null;
		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getDataSource());
		} catch (Exception e) {
			// TODO Bloque catch generado automaticamente
			logger.error(GestorBD.class.getName(), "getConexion",
					"Error obteniendo la conexion o la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		}
		return conexion;
	}

	public static Connection getConexionBpo() throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion = null;
		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getBpodataSource());
		} catch (Exception e) {
			// TODO Bloque catch generado automaticamente
			logger.error(GestorBD.class.getName(), "getConexion",
					"Error obteniendo la conexion o la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		}
		return conexion;
	}

	public static Usuario consultarUsuarioPorTipoUsuario(String id,
			int tipoUsuario, Connection conexion) throws ExceptionWebPrepago, SQLException {
		Configuracion conf = null;

		ResultSet rst = null;
		Usuario usr = null;
		boolean crearConexion = (conexion == null);

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para consultarUsuarioTemporal");
			}
			List<Object> params = new ArrayList<Object>();
			params.add(id);
			params.add(tipoUsuario);
			if (crearConexion)
				conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
						.getDataSource());

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_COSULTAR_USUARIO_POR_TIPO);
			if(sentencia == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para consultarUsuarioTemporal");
			}
			logger.info(GestorBD.class.getName(), "consultarUsuarioTipo",
					"Consultando existencia de usuario");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), params, sentencia.getTimeout());
			// Recorre los resultados de las busquedas
			while (rst.next()) {

				logger.info(GestorBD.class.getName(), "consultarUsuarioTipo",
						"consultando usuario: " + id);
				usr = new Usuario();
				usr.setId(rst.getInt("ID_USUARIO"));
				usr.setApellidos(rst.getString("USR_APELLIDO"));
				usr.setCorreo(rst.getString("USR_CORREO"));
				usr.setNombres(rst.getString("USR_NOMBRE"));
				usr.setNumDocumento(rst.getString("USR_DOCUMENTO"));
				usr.setPregunta1(rst.getInt("USR_ID_PREGUNTA1"));
				usr.setPregunta2(rst.getInt("USR_ID_PREGUNTA2"));
				usr.setPregunta3(rst.getInt("USR_ID_PREGUNTA3"));
//				usr.setRespuesta1(rst.getString("USR_RESPUESTA1"));
				usr.setRespuesta1(decryptPreguntas(key, initVector2,rst.getString("USR_RESPUESTA1")));
//				usr.setRespuesta2(rst.getString("USR_RESPUESTA2"));
				usr.setRespuesta2(decryptPreguntas(key, initVector2,rst.getString("USR_RESPUESTA2")));
//				usr.setRespuesta3(rst.getString("USR_RESPUESTA3"));
				usr.setRespuesta3(decryptPreguntas(key, initVector2,rst.getString("USR_RESPUESTA3")));
				usr.setTelefono(rst.getString("USR_TELEFONO"));
				usr.setTipoDocumento(rst.getInt("USR_ID_TIPODOCUMENTO"));

			}
			return usr;
		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "consultarUsuarioTipo",
					"Error al consultar la base de datos: ", e);
			throw new SQLException(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "consultarUsuarioTipo",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			// Solo cierra la conexion cuando el mismo la crea
			if (crearConexion) {
				ManejadorDeDatosDB.cerrarConexion(conexion);
			}
			// si el resulset no es nulo cierra la conexion
			if (rst != null) {
				rst.close();
			}
		}
	}

	public static Usuario consultarUsuario(String id, Connection conexion)
			throws ExceptionWebPrepago, SQLException {
		Configuracion conf = null;

		ResultSet rst = null;
		Usuario usr = null;
		boolean crearConexion = (conexion == null);
		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para consultarUsuario");
			}
			List<Object> params = new ArrayList<Object>();
			params.add(id);

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_COSULTAR_USUARIO);
			if(sentencia == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para consultarUsuario");
			}
			if (crearConexion)
				conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
						.getDataSource());
			logger.info(GestorBD.class.getName(), "consultarUsuario",
					"Consultando existencia de usuario");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), params, sentencia.getTimeout());
			// Recorre los resultados de las busquedas
			while (rst.next()) {

				logger.info(GestorBD.class.getName(), "consultarUsuario",
						"consultando usuario: " + id);
				usr = new Usuario();
				usr.setApellidos(rst.getString("USR_APELLIDO"));
				usr.setCorreo(rst.getString("USR_CORREO"));
				usr.setNombres(rst.getString("USR_NOMBRE"));
				usr.setNumDocumento(rst.getString("USR_DOCUMENTO"));
				usr.setPregunta1(rst.getInt("USR_ID_PREGUNTA1"));
				usr.setPregunta2(rst.getInt("USR_ID_PREGUNTA2"));
				usr.setPregunta3(rst.getInt("USR_ID_PREGUNTA3"));
				usr.setRespuesta1(rst.getString("USR_RESPUESTA1"));
				usr.setRespuesta2(rst.getString("USR_RESPUESTA2"));
				usr.setRespuesta3(rst.getString("USR_RESPUESTA3"));
				usr.setTelefono(rst.getString("USR_TELEFONO"));
				usr.setTipoDocumento(rst.getInt("USR_ID_TIPODOCUMENTO"));
				usr.setFechaCreacion(rst.getTimestamp("USR_FECHACREACION"));
			}
			return usr;
		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "consultarUsuario",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "consultarUsuario",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			// Solo cierra la conexion cuando el mismo la crea
			if (crearConexion) {
				ManejadorDeDatosDB.cerrarConexion(conexion);
			}
				// si el resulset no es nullo cierra la conexion
			if (rst != null) {
				rst.close();
			}
		}
	}

	public static void registrarUsuarioTemporal(Usuario usr)
			throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para consultarUsuarioTemporal");
			}
			List<Object> params = new ArrayList<Object>();
			params.add(usr.getId());
			params.add(usr.getTipoDocumento());
			params.add(usr.getNumDocumento());
			params.add(usr.getNombres());
			params.add(usr.getApellidos());
			params.add(usr.getTelefono());
			params.add(usr.getCorreo());
			params.add(usr.getPregunta1());
			params.add(usr.getRespuesta1());
			params.add(usr.getPregunta2());
			params.add(usr.getRespuesta2());
			params.add(usr.getPregunta3());
			params.add(usr.getRespuesta3());
			params.add(ConstantesWebPrepago.USUARIO_TEMPORAL);
			params.add(new java.sql.Timestamp(new java.util.Date().getTime()));

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_REGISTRAR_USUARIO);
			if(sentencia == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para consultarUsuarioTemporal");
			}
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getDataSource());
			logger.info(GestorBD.class.getName(), "registrarUsuarioTemporal",
					"Consultando existencia de usuario");
			ManejadorDeDatosDB.actualizarDB(conexion, sentencia.getQuery(),
					params, sentencia.getTimeout());

		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "registrarUsuarioTemporal",
					"Error al consultar la configuracion", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);

		}
	}

	/**
	 * Volver el usuario que llega por parametro un usuario permanente en la BD
	 * 
	 * @param login
	 * @throws ExceptionWebPrepago
	 */
	public static void cambiarUsuarioAPermanente(String login,
			int tipoPermanente) throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para cambiarUsuarioAPermanente");
			}
			List<Object> params = new ArrayList<Object>();
			params.add(tipoPermanente);
			params.add(login);

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_CAMBIAR_A_PERMANENTE);
			if(sentencia == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para cambiarUsuarioAPermanente");
			}
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getDataSource());
			logger.info(GestorBD.class.getName(), "cambiarUsuarioAPermanente",
					"Consultando existencia de usuario");
			ManejadorDeDatosDB.actualizarDB(conexion, sentencia.getQuery(),
					params, sentencia.getTimeout());

		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "cambiarUsuarioAPermanente",
					"Error al consultar la configuracion", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);

		}

	}

	public static int secuencialUsuario() throws ExceptionWebPrepago, SQLException {

		Configuracion conf = null;
		Connection conexion = null;
		int valor = 0;
		ResultSet rst = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para secuencialUsuario");
			}

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_OBTENER_SECUENCIA_USUARIO);
			if(sentencia == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para secuencialUsuario");
			}
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getDataSource());

			logger.info(GestorBD.class.getName(), "secuencialUsuario",
					"Obteniendo usuarios");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), sentencia.getTimeout());
			// Recorre los resultados de las busquedas
			if (rst.next()) {
				valor = rst.getInt("ID_USUARIO");
			}
		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "secuencialUsuario",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "secuencialUsuario",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
				// si el resulset no es nullo cierra la conexion
			if (rst != null) {
				rst.close();
			}
		}
		return valor;
	}

	/**
	 * Consulta las tarjetas prepagadas correspondientes a un usuario
	 * 
	 * @param id
	 *            - el id del usuario a consultar
	 * @return Lista de tarjetas prepagadas
	 * @throws ExceptionWebPrepago
	 * @throws SQLException 
	 */
	public static List<TarjetaPrepago> consultarTarjetasPorUsuario(int id,
			Connection conexion, Connection conexionBpo, String bean)
			throws ExceptionWebPrepago, SQLException {
		Configuracion conf = null;

		boolean crearConexion = (conexion == null);
		boolean crearConexionBpo = (conexionBpo == null);

		TarjetaPrepago tarjeta;
		ResultSet rst = null;
		List<TarjetaPrepago> listaTarjetas = new ArrayList<TarjetaPrepago>();
		Boolean bolsillo = false;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para getTipoDoc");
			}

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_CONSULTAR_TARJETAS_X_USUARIO);
			if(sentencia == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para getTipoDoc");
			}
			if (crearConexion)
				conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
						.getDataSource());
			if (crearConexionBpo)
				conexionBpo = ManejadorDeDatosDB
						.obtenerConexionPorDatasource(conf.getBpodataSource());
			List<Object> params = new ArrayList<Object>();
			params.add(id);

			logger.info(GestorBD.class.getName(), "getTarjetasPorUsuario",
					"Obteniendo las tarjetas de un usuario");
			logger.debug(GestorBD.class.getName(), "getTarjetasPorUsuario","se ejecutara el query: "+sentencia.getQuery());
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), params, sentencia.getTimeout());
			
			// Recorre los resultados de las busquedas
			while (rst.next()) {
				List<Bolsillo> bolsillosBd = new ArrayList<Bolsillo>();
				List<SaldoXBolsillo> bolsillosBpo = new ArrayList<SaldoXBolsillo>();
				tarjeta = new TarjetaPrepago();
				tarjeta.setNumTarjeta(decrypt(key, initVector,rst.getString("TAR_NUM_ENCRIPT")));
				bolsillosBd = getBolsillosbyTarjeta(tarjeta.getNumTarjeta());
				String estado = (consultarEstadoTarjetaBPO(tarjeta.getNumTarjeta(), conexionBpo));
				if(bolsillosBd!=null && !bolsillosBd.isEmpty() ) {
					bolsillo=true;
					bolsillosBpo = consultarSaldoTarjetaMultiBolsilloBPO(tarjeta.getNumTarjeta(), conexionBpo);
					if(bolsillosBpo!=null && !bolsillosBpo.isEmpty()) {
						for( SaldoXBolsillo bolsillos : bolsillosBpo) {
							tarjeta = new TarjetaPrepago();
							tarjeta.setNumeroTarjeta(rst.getString("TAR_NUMERO"));
							tarjeta.setSaldo(bolsillos.getSaldoBolsillo());
							tarjeta.setTipoTarjeta(rst.getString("TIP_DESCRIPCION"));
							tarjeta.setEntidad(rst.getString("ENT_DESCRIPCION"));
							tarjeta.setNombreTarjeta(rst.getString("TAR_NOMBRE"));
							tarjeta.setFechaRegistro(rst.getString("TAR_FECHA_REGISTRO"));
							tarjeta.setNumTarjeta(decrypt(key, initVector,
									rst.getString("TAR_NUM_ENCRIPT")));
							tarjeta.setRutaIconoTarjeta(getNombreImagen(
									tarjeta.getNumTarjeta(), conexion));
							tarjeta.setEstado(estado);
							//Buscar nombre bolsillo de caché
							for(Bolsillo bol : GestorCache.getBolsillos()) {
								if(bolsillos.getBolsillo().equals(bol.getId())){
									tarjeta.setBolsillo(bol.getNombre());
								}
							}
							logger.info(GestorBD.class.getName(), "getTarjetasPorUsuario",
									"Agregando tarjeta: " + tarjeta.getNumeroTarjeta()+" con saldo: "+tarjeta.getSaldo());
							  listaTarjetas.add(tarjeta);
						}
					}else {
						logger.debug(GestorBD.class.getName(), "getTarjetasPorUsuario",
								"Error al consultar los bolsillos de BPO");
						return null;
						}
				}else {
					tarjeta.setNumeroTarjeta(rst.getString("TAR_NUMERO"));
					tarjeta.setSaldo(consultarSaldoTarjetaBPO(tarjeta.getNumTarjeta(), conexionBpo));
					tarjeta.setTipoTarjeta(rst.getString("TIP_DESCRIPCION"));
					tarjeta.setEntidad(rst.getString("ENT_DESCRIPCION"));
					tarjeta.setNombreTarjeta(rst.getString("TAR_NOMBRE"));
					tarjeta.setFechaRegistro(rst.getString("TAR_FECHA_REGISTRO"));
					tarjeta.setNumTarjeta(decrypt(key, initVector,
							rst.getString("TAR_NUM_ENCRIPT")));
					tarjeta.setRutaIconoTarjeta(getNombreImagen(
							tarjeta.getNumTarjeta(), conexion));
					tarjeta.setEstado(estado);
					tarjeta.setBolsillo("");
					logger.info(GestorBD.class.getName(), "getTarjetasPorUsuario",
							"Agregando tarjeta: " + tarjeta.getNumeroTarjeta());
					listaTarjetas.add(tarjeta);
				}
			}
				
		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "getTarjetasPorUsuario",
					"Error al consultar la base de datos: ", e);
			throw new SQLException(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "getTarjetasPorUsuario",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			// Solo cierra la conexion si el mismo la crea
			if (crearConexion) {
				ManejadorDeDatosDB.cerrarConexion(conexion);
			}
			if (crearConexionBpo) {
				ManejadorDeDatosDB.cerrarConexion(conexionBpo);
			}
			// si el resulset no es nulo cierra la conexion
			if (rst != null) {
				rst.close();
			}

		}
		return listaTarjetas;

	}

	/**
	 * Consulta el saldo de una tarjeta
	 * 
	 * @param numTarjeta
	 *            - numero tarjeta a consultar
	 * @return Saldo de tarjeta
	 * @throws ExceptionWebPrepago
	 * @throws SQLException 
	 */
	public static Double consultarSaldoTarjetaBPO(String numTarjeta,
			Connection conexion) throws ExceptionWebPrepago, SQLException {
		Configuracion conf = null;

		ResultSet rst = null;
		CallableStatement callableStatement = null;
		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getBpodataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para consultarSaldoTarjetaBPO");
			}

			// Consulta
			// BPO**************************************************************
			
			String tarjetaFormatead = String.format("%-19s", numTarjeta); //espacios derecha
			String filler = "";
			filler = String.format("%1$-4044s", filler);

			String filler2 = "";
			filler2 = String.format("%1$-4064s", filler2);
			String param1 = "2" + tarjetaFormatead + filler;
			
			String query = "{? = CALL TDPGMSV5R.TDCWPB1('2" + tarjetaFormatead
					+ filler + "','" + filler2 + "') }";

			Sentencia sentencia = obtenerSentencia(conf, ConstantesWebPrepago.QUERY_LLAMADO_BPO_PCI);
			if(sentencia == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para consultarSaldoTarjetaBPO");
			}
			callableStatement = conexion.prepareCall(
					//"CALL TDPGMSV5R.TDCWPB1(?,?)", ResultSet.TYPE_FORWARD_ONLY,
					sentencia.getQuery(), ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			logger.info(GestorBD.class.getName(), "consultarSaldoTarjetaBPO","Obteniendo consultarSaldoTarjetaBPO");
			callableStatement.setString(1, param1);
			callableStatement.registerOutParameter(2, Types.VARCHAR);
			callableStatement.execute();
			logger.debug(GestorBD.class.getName(), "consultarSaldoTarjetaBPO",
					"Se ejecuta el programa : " + query);
			// Boolean existeBin = false;

			String resultado = "";
			String respuesta = "";
			 
			resultado = callableStatement.getString(2);
			logger.debug(GestorBD.class.getName(), "consultarSaldoTarjetaBPO",	"RESULTADO****"+resultado);
			respuesta = resultado.substring(32, 37);
			if (respuesta.equals(ConstantesWebPrepago.RESPUESTA_BPO)) {
				String entero = resultado.substring(20, 30);
				String decimal =resultado.substring(30,32);
				String completo= entero + "." + decimal;
				double saldo = Double.parseDouble(completo);
				logger.debug(GestorBD.class.getName(), "consultarSaldoTarjetaBPO",	"Exitosa");
				return saldo;
			} else if (respuesta.equals(ConstantesWebPrepago.RESPUESTA_BPO1)) {
				logger.debug(GestorBD.class.getName(), "consultarSaldoTarjetaBPO", "No existe registro coincidente");
				throw new ExceptionWebPrepago("No existe registro coincidente");
			} else if (respuesta.equals(ConstantesWebPrepago.RESPUESTA_BPO2)) {
				logger.debug(GestorBD.class.getName(), "consultarSaldoTarjetaBPO", "No hay conexion con base de datos BPO");
				throw new ExceptionWebPrepago("No hay conexión con base de datos BPO");
			}
			
			// ********************************
			// Sentencia sentencia = obtenerSentencia(conf,
			// ConstantesWebPrepago.QUERY_CONSULTA_SALDO_TARJETA_BPO);
			//
			// List<Object> params = new ArrayList<Object>();
			// params.add(numTarjeta);
			//
			//
			// logger.info(GestorBD.class.getName(), "consultarSaldoTarjetaBPO",
			// "Obteniendo consultarSaldoTarjetaBPO");
			// rst = ManejadorDeDatosDB.consultarBD(conexion,
			// sentencia.getQuery(), params, sentencia.getTimeout());

			// Recorre los resultados de las busquedas
//			if (rst.next()) {
				// return rst.getDouble("SALDO");
//				double saldo = Double.parseDouble(rst.getString(3));
//				return saldo;
//			}
		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "consultarSaldoTarjetaBPO",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "consultarSaldoTarjetaBPO",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			// la conexion la cierra el que llama el metodo
				// si el resulset no es nulo cierra la conexion
				if (callableStatement != null) {
					callableStatement.close();
				}
		}
		return null;

	}

	/**
	 * Consulta el estado de una tarjeta
	 * 
	 * @param numTarjeta
	 *            - numero tarjeta a consultar
	 * @return estado de tarjeta
	 * @throws ExceptionWebPrepago
	 * @throws SQLException 
	 */
	public static String consultarEstadoTarjetaBPO(String numTarjeta,
			Connection conexion) throws ExceptionWebPrepago, SQLException {
		Configuracion conf = null;
		// Connection conexion = null;
		ResultSet rst = null;
		CallableStatement callableStatement = null;
		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getBpodataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para consultarEstadoTarjetaBPO");
			}

			// Consulta
			// BPO**************************************************************
			String tarjetaFormatead = String.format("%-19s", numTarjeta); //espacios derecha
			String filler = "";
			filler = String.format("%1$-4044s", filler);

			String filler2 = "";
			filler2 = String.format("%1$-4064s", filler2);

			String param1 = "6" + tarjetaFormatead + filler; 
		
			String query = "{? = CALL TDPGMSV5R.TDCWPB1('6" + tarjetaFormatead + filler +"','"+ filler2 +"') }";
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf.getBpodataSource());
			Sentencia sentencia = obtenerSentencia(conf, ConstantesWebPrepago.QUERY_LLAMADO_BPO_PCI);
			logger.debug(GestorBD.class.getName(), "consultarEstadoTarjetaBPO", "Consulta a ejecutar: "+ConstantesWebPrepago.QUERY_LLAMADO_BPO_PCI);
			if(sentencia == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para consultarEstadoTarjetaBPO");
			}
			//callableStatement = conexion.prepareCall("CALL TDPGMSV5R.TDCWPB1(?,?)", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			callableStatement = conexion.prepareCall(sentencia.getQuery(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			callableStatement.setString (1, param1); 
			callableStatement.registerOutParameter (2, Types.VARCHAR);
			callableStatement.execute();
			logger.info(GestorBD.class.getName(), "consultarEstadoTarjetaBPO", "Obteniendo consultarEstadoTarjetaBPO");
			String resultado = "";
			String respuesta = "";
			resultado= callableStatement.getString(2);
			logger.info(GestorBD.class.getName(), "consultarEstadoTarjetaBPO", "Resultado de BPO :"+resultado);
			respuesta = resultado.substring(50,55);  
			if (respuesta.equals(ConstantesWebPrepago.RESPUESTA_BPO)){
				logger.debug(GestorBD.class.getName(), "consultarEstadoTarjetaBPO",	"Exitosa");
				return resultado.substring(20,50);
			} else if (respuesta.equals(ConstantesWebPrepago.RESPUESTA_BPO1)) {
				logger.debug(GestorBD.class.getName(), "consultarEstadoTarjetaBPO", "No existe registro coincidente");
				throw new ExceptionWebPrepago("No existe registro coincidente");
			} else if (respuesta.equals(ConstantesWebPrepago.RESPUESTA_BPO2)) {
				logger.debug(GestorBD.class.getName(), "consultarEstadoTarjetaBPO", "No hay conexion con base de datos BPO");
				throw new ExceptionWebPrepago("No hay conexión con base de datos BPO");
			}
			// ********************************

			// Sentencia sentencia = obtenerSentencia(conf,
			// ConstantesWebPrepago.QUERY_CONSULTA_ESTADO_TARJETA_BPO);
			// //conexion =
			// ManejadorDeDatosDB.obtenerConexionPorDatasource(conf.getBpodataSource());
			// List<Object> params = new ArrayList<Object>();
			// params.add(numTarjeta);
			//
			//
			// logger.info(GestorBD.class.getName(),
			// "consultarEstadoTarjetaBPO",
			// "Obteniendo consultarEstadoTarjetaBPO");
			// rst = ManejadorDeDatosDB.consultarBD(conexion,
			// sentencia.getQuery(), params, sentencia.getTimeout());

			// Recorre los resultados de las busquedas
//			if (rst.next()) {
				// return rst.getString("ESTADO");
//			}
		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "consultarEstadoTarjetaBPO",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "consultarEstadoTarjetaBPO",
					"Error al consultar en BPO: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar en BPO", e);
		} finally {
			// //la conexion la cierra el que llama el metodo
				// si el resulset no es nulo cierra la conexion
				if (callableStatement != null) {
					callableStatement.close();
				}
		}
		return null;

	}

	/**
	 * Consulta los movimientos de una tarjeta
	 * 
	 * @param numTarjeta
	 *            - numero tarjeta a consultar
	 * @param fechaIni
	 *            - Fecha Inicio
	 * @param fechaFin
	 *            - Fecha Fin
	 * @return Lista de movimientos
	 * @throws ExceptionWebPrepago
	 * @throws SQLException 
	 */
	public static List<RegistroMovimiento> consultarMovimientosBPO(String numTarjeta, String fechaIni, String fechaFin, String bolsillo)
			throws ExceptionWebPrepago, SQLException {
		Configuracion conf = null;
		Connection conexion = null;
		ResultSet rst = null;
		List<RegistroMovimiento> movimientos = new ArrayList<RegistroMovimiento>();
		RegistroMovimiento movimiento;
		SimpleDateFormat sdfBPO = new SimpleDateFormat(ConstantesWebPrepago.FORMATO_HORA_BPO);
		SimpleDateFormat sdfWeb = new SimpleDateFormat(ConstantesWebPrepago.FORMATO_HORA_WEB);
		Date hora = null;
		CallableStatement callableStatement = null;

		String textoFormateado = String.format("%-19s", numTarjeta); //espacios derecha
 
		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getBpodataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para consultarMovimientosBPO");
			}
			
			// Consulta
			// BPO**************************************************************
			String filler = "";

			String filler2 = "";
			filler2 = String.format("%1$-4064s", filler2);
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf.getBpodataSource());
			String param1;
			String query;
			if(bolsillo!=null && !bolsillo.isEmpty()) {
				filler = String.format("%1$-4026s", filler);
				param1 = "3" + textoFormateado + bolsillo + fechaFin + fechaIni + filler; 
				query = "{? = CALL TDPGMSV5R.TDCWPB1('3" + textoFormateado + bolsillo + fechaIni + fechaFin + filler +"','"+filler2 +"') }";
				 
			}else {
				filler = String.format("%1$-4028s", filler);
				param1 = "3" + textoFormateado + fechaFin + fechaIni + filler; 
				query = "{? = CALL TDPGMSV5R.TDCWPB1('3" + textoFormateado + fechaIni + fechaFin + filler +"','"+filler2 +"') }";
			}
			Sentencia sentencia = obtenerSentencia(conf, ConstantesWebPrepago.QUERY_LLAMADO_BPO_PCI);
			if(sentencia == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para consultarMovimientosBPO");
			}
			logger.debug(GestorBD.class.getName(), "consultarMovimientosBPO","Se ejecuta el programa : " + query);
			//callableStatement = conexion.prepareCall("CALL TDPGMSV5R.TDCWPB1(?,?)", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			callableStatement = conexion.prepareCall(sentencia.getQuery(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			callableStatement.setString (1, param1); 
			callableStatement.registerOutParameter (2, Types.VARCHAR);
			callableStatement.execute();
			
		
			String resultado = "";
			String respuesta = "";
			resultado= callableStatement.getString(2);
			logger.debug(GestorBD.class.getName(), "consultarMovimientosBPO",	"RESULTADO**** "+resultado);
			respuesta = resultado.substring(4029,4034);
			//logger.debug(GestorBD.class.getName(), "consultarMovimientosTarjetaBPO",	"Respuesta**** "+respuesta);			
			if (respuesta.equals(ConstantesWebPrepago.RESPUESTA_BPO)){
				logger.debug(GestorBD.class.getName(), "consultarMovimientosBPO",	"Exitosa");
				String resultado2 ="";
				if(bolsillo!=null && !bolsillo.isEmpty()) {
					resultado2 = resultado.substring(38,3877); 
				}else {
					resultado2 = resultado.substring(36,4029); 
				}
				String[] partes = resultado2.split(";");

				for (int i = 0; i < partes.length; i++){
					String parte = partes[i]; 

//					se recorre el arreglo de respuestas movimientos mientras no sea blanco. 
					if(!parte.substring(0,1).contains(" ")){
						movimiento = new RegistroMovimiento();
						String fecha = parte.substring(0,8);
						movimiento.setFechaTransaccion(fecha.substring(6) + "/"
								+ fecha.substring(4, 6) + "/" + fecha.substring(0, 4));
//						if(!parte.substring(8,10).contains(" ")){}
							hora = sdfBPO.parse(parte.substring(8,14));
							movimiento.setHora(sdfWeb.format(hora));

						
						movimiento.setUbicacion(parte.substring(14,44));
						movimiento.setTipoTransaccion(parte.substring(44,74));
						String entero = parte.substring(74,84);
						String decimal =parte.substring(84,86);
						String completo= entero + "." + decimal;

						String formateado=ConstantesWebPrepago.FORMATO_VALORES.format(Double.parseDouble(completo));
						movimiento.setValorTransaccion(formateado);
						movimiento.setRespTransaccion(parte.substring(86,96));
						
						String Comientero = parte.substring(96,107);
						String Comidecimal =parte.substring(107,109);
						String Comicompleto= Comientero + "." + Comidecimal;
						String formateadoComi=ConstantesWebPrepago.FORMATO_VALORES.format(Double.parseDouble(Comicompleto));
						movimiento.setValorComision(formateadoComi);
						String Impentero = parte.substring(109,117);
						String Impdecimal = parte.substring(117,119);
						String Impcompleto= Impentero + "." + Impdecimal;
						String formateadoImp=ConstantesWebPrepago.FORMATO_VALORES.format(Double.parseDouble(Impcompleto));
						movimiento.setValorImpuesto(formateadoImp);
						if(bolsillo!=null && !bolsillo.isEmpty()) {
							for(Bolsillo bol : GestorCache.getBolsillos()) {
								if(bolsillo.equals(bol.getId())){
									movimiento.setBolsillo(bol.getNombre());
								}
							}
						}
						movimientos.add(movimiento);	
					}
				}
			} else if (respuesta.equals(ConstantesWebPrepago.RESPUESTA_BPO1)) {
				logger.debug(GestorBD.class.getName(), "consultarMovimientosBPO", "No existe registro coincidente");
				throw new ExceptionWebPrepago("No existe registro coincidente");
			} else if (respuesta.equals(ConstantesWebPrepago.RESPUESTA_BPO2)) {
				logger.debug(GestorBD.class.getName(), "consultarMovimientosBPO", "No hay conexion con base de datos BPO");
				throw new ExceptionWebPrepago("No hay conexión con base de datos BPO");
			}
			// ********************************			
			
			
//			Sentencia sentencia = obtenerSentencia(conf,
//					ConstantesWebPrepago.QUERY_CONSULTA_MOVIMIENTOS_BPO);
//			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
//					.getBpodataSource());
//			List<Object> params = new ArrayList<Object>();
//			params.add(numTarjeta);
//			params.add(fechaIni);
//			params.add(fechaFin);
//			params.add(numTarjeta);
//			params.add(fechaIni);
//			params.add(fechaFin);
//
//			logger.info(GestorBD.class.getName(), "consultarMovimientosBPO",
//					"Obteniendo consultarMovimientosBPO");
//			rst = ManejadorDeDatosDB.consultarBD(conexion,
//					sentencia.getQuery(), params, sentencia.getTimeout());
//
//			// Recorre los resultados de las busquedas
//			while (rst.next()) {
//				movimiento = new RegistroMovimiento();
//				String fecha = rst.getString("FECHATRAN");
//				movimiento.setFechaTransaccion(fecha.substring(6) + "/"
//						+ fecha.substring(4, 6) + "/" + fecha.substring(0, 4));
//				hora = sdfBPO.parse(rst.getString("HORA"));
//				movimiento.setHora(sdfWeb.format(hora));
//				movimiento.setUbicacion(rst.getString("UBICACION"));
//				movimiento.setTipoTransaccion(rst.getString("TIPOTRAN"));
//				movimiento
//						.setValorTransaccion(ConstantesWebPrepago.FORMATO_VALORES
//								.format(rst.getDouble("VALORTX")));
//				movimiento.setRespTransaccion(rst.getString("RESPUESTA"));
//				movimiento
//						.setValorComision(ConstantesWebPrepago.FORMATO_VALORES
//								.format(rst.getDouble("VLRCOMISION")));
//				movimiento
//						.setValorImpuesto(ConstantesWebPrepago.FORMATO_VALORES
//								.format(rst.getDouble("VLRIMPUESTO")));
//				movimientos.add(movimiento);
//			}
			return movimientos;
		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "consultarMovimientosBPO",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "consultarMovimientosBPO",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
			
				// si el resulset no es nulo cierra la conexion
				if (callableStatement != null) {
					callableStatement.close();
				}

		}

	}
	
	public static List<Usuario> consutarTarjetasUsuario(String idUsuario)
			throws ExceptionWebPrepago, SQLException {
		Configuracion conf = null;
		Connection conexion = null;
		ResultSet rst = null;
		List<Usuario> listaUsuarios = new ArrayList<Usuario>();

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para consutarTarjetasUsuario");
			}

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_CONSULTAR_TARJETAS_USUARIO);
			if(sentencia == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para consutarTarjetasUsuario");
			}
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getDataSource());
			List<Object> params = new ArrayList<Object>();
			params.add(idUsuario);

			logger.info(GestorBD.class.getName(), "consutarTarjetasUsuario",
					"Obteniendo las tarjetas de un usuario");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), params, sentencia.getTimeout());

			// Recorre los resultados de las busquedas
			while (rst.next()) {
				Usuario usuario = new Usuario();

				usuario.getTarjetaPrepago().setNumeroTarjeta(
						rst.getString("NUM_ENMAS"));
				usuario.getTarjetaPrepago().getEnti()
						.setId(rst.getInt("IDENTI"));
				usuario.getTarjetaPrepago().getEnti()
						.setNombre(rst.getString("ENTIDAD"));
				usuario.getTipoDocum().setId(rst.getInt("IDTIPODOC"));
				usuario.getTipoDocum().setDescripcion(rst.getString("TIPODOC"));
				usuario.getTarjetaPrepago().setNumDocumento(
						rst.getString("NUMDOCASO"));
				usuario.getTarjetaPrepago().setNombreTarjeta(
						rst.getString("NOMBRE_TAR"));
				SimpleDateFormat sfor = new SimpleDateFormat("dd/MM/yyyy");
				usuario.getTarjetaPrepago().setFechaRegistro(
						sfor.format(rst.getDate("FECHA_TAR")));
				usuario.getTarjetaPrepago().getTipTarjeta()
						.setId(rst.getInt("IDTIPOTAR"));
				usuario.getTarjetaPrepago().getTipTarjeta()
						.setNombre(rst.getString("TIPOTAR"));
				usuario.getTarjetaPrepago().setNumTarjeta(
						decrypt(key, initVector,
								rst.getString("TAR_NUM_ENCRIPT")));

				listaUsuarios.add(usuario);
			}
		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "consutarTarjetasUsuario",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "consutarTarjetasUsuario",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
				// si el resulset no es nulo cierra la conexion
				if (rst != null) {
					rst.close();
				}
		}
		return listaUsuarios;

	}

	public static String decrypt(String key, String initVector, String encrypted) {
		try {
			IvParameterSpec iv = new IvParameterSpec(
					initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"),
					"AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

			byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted
					.getBytes("UTF8")));

			return new String(original);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}
	
	public static String decryptPreguntas(String key, String initVector, String encrypted) {
		try {
			IvParameterSpec iv = new IvParameterSpec(Hex.decodeHex(initVector.toCharArray()));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"),
					"AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

			byte[] original = cipher.doFinal(Hex.decodeHex(encrypted.toCharArray()));

			return new String(original);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	public static void EliminarTarjeta(String numTarjeta, Connection conexion)
			throws ExceptionWebPrepago, SQLException {
		Configuracion conf = null;
		boolean crearConexion = (conexion == null);
		ResultSet rst = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para EliminarTarjeta");
			}

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_ELIMINAR_TARJETA);
			if(sentencia == null) {
				throw new ExceptionWebPrepago("Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para EliminarTarjeta");
			}
			if (crearConexion)
				conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
						.getDataSource());
			List<Object> params = new ArrayList<Object>();
			params.add(numTarjeta);

			logger.info(GestorBD.class.getName(), "EliminarTarjeta",
					"Obteniendo las tarjetas de un usuario");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), params, sentencia.getTimeout());

		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "EliminarTarjeta",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "EliminarTarjeta",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			// Solo cierra la conexion cuando el mismo la crea
			if (crearConexion) {
				ManejadorDeDatosDB.cerrarConexion(conexion);
			}
				// si el resulset no es nulo cierra la conexion
			if (rst != null) {
				rst.close();
			}
		}

	}

	public static boolean existeTarjeta(String numTarjeta)
			throws ExceptionWebPrepago, SQLException {
		Configuracion conf = null;
		Connection conexion = null;
		ResultSet rst = null;
		boolean existe = false;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para existeTarjeta");
			}

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_EXISTE_TARJETA);
			if(sentencia == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para existeTarjeta");
			}
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getDataSource());
			List<Object> params = new ArrayList<Object>();
			params.add(numTarjeta);

			logger.info(GestorBD.class.getName(), "existeTarjeta",
					"Obteniendo las tarjetas de un usuario");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), params, sentencia.getTimeout());

			if (rst.next()) {
				existe = true;
			}

		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "existeTarjeta",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "existeTarjeta",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
				// si el resulset no es nulo cierra la conexion
				if (rst != null) {
					rst.close();
				}
		}
		return existe;
	}

	public static TarjetaPrepago tarjetaPrepago(String numTarjeta)
			throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion = null;
		ResultSet rst = null;
		TarjetaPrepago prepago = new TarjetaPrepago();

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para tarjetaPrepago");
			}

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_ENTIDAD_TIPOTAR);
			if(sentencia == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para tarjetaPrepago");
			}
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getDataSource());
			List<Object> params = new ArrayList<Object>();
			params.add(numTarjeta);

			logger.info(GestorBD.class.getName(), "tarjetaPrepago",
					"Obteniendo las tarjetas de un usuario");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), params, sentencia.getTimeout());
			while (rst.next()) {
				Entidad entidad = new Entidad();
				entidad.setCodigo(rst.getInt("ID_ENTIDAD"));
				entidad.setNombre(rst.getString("ENTIDAD"));
				prepago.setEnti(entidad);
				TipoTarjeta tipoTarjeta = new TipoTarjeta();
				tipoTarjeta.setId(rst.getInt("ID_TIPO_TAR"));
				tipoTarjeta.setNombre(rst.getString("TIPO_TARJETA"));
				prepago.setTipTarjeta(tipoTarjeta);
			}

		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "tarjetaPrepago",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "tarjetaPrepago",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
			try {
				// si el resulset no es nulo cierra la conexion
				if (rst != null) {
					rst.close();
				}
			} catch (SQLException e) {
				logger.error(GestorBD.class.getName(), "tarjetaPrepago",
						"Error al cerrar recursos de BD", e);
				throw new ExceptionWebPrepago("Error al cerrar recursos de BD",
						e);
			}

		}
		return prepago;
	}

	public static void asignarTarjeta(Usuario usuario, String clave)
			throws ExceptionWebPrepago, SQLException {
		Configuracion conf = null;
		Connection conexion = null;
		ResultSet rst = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para asignarTarjeta");
			}

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_REGISTRAR_TARJETAUSUARIO);
			if(sentencia == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para asignarTarjeta");
			}
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getDataSource());
			List<Object> params = new ArrayList<Object>();
			params.add(usuario.getTarjetaPrepago()
					.getNumeroTarjetaEnmascarado());
			params.add(usuario.getTarjetaPrepago().getTipTarjeta().getId());
			params.add(usuario.getTarjetaPrepago().getEnti().getId());
			params.add(usuario.getTarjetaPrepago().getNombreTarjeta());
			params.add(usuario.getId());
			params.add(new java.sql.Date(new Date().getTime()));
			params.add(clave);
			params.add(usuario.getNumDocumento());
			params.add(usuario.getTipoDocum().getId());

			logger.info(GestorBD.class.getName(), "asignarTarjeta",
					"Obteniendo las tarjetas de un usuario");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), params, sentencia.getTimeout());

		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "asignarTarjeta",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "asignarTarjeta",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
				// si el resulset no es nulo cierra la conexion
				if (rst != null) {
					rst.close();
				}
		}

	}

	public static void modificarTarjeta(int idusuario, String numDoc,
			String nombreTar, int idTipoDoc, String clave)
			throws ExceptionWebPrepago, SQLException {
		Configuracion conf = null;
		Connection conexion = null;
		ResultSet rst = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para asignarTarjeta");
			}

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_MODIFICAR_TARJETAUSUARIO);
			if(sentencia == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para asignarTarjeta");
			}
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getDataSource());
			List<Object> params = new ArrayList<Object>();
			params.add(nombreTar);
			params.add(numDoc);
			params.add(idTipoDoc);
			params.add(clave);
			params.add(idusuario);

			// int t=ManejadorDeDatosDB.actualizarDB(conexion,
			// sentencia.getQuery(), params, sentencia.getTimeout());
			ManejadorDeDatosDB.actualizarDB(conexion, sentencia.getQuery(),
					params, sentencia.getTimeout());
			logger.info(GestorBD.class.getName(), "asignarTarjeta",
					"Modificando una tarjeta de un usuario");

		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "asignarTarjeta",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "asignarTarjeta",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
				// si el resulset no es nulo cierra la conexion
				if (rst != null) {
					rst.close();
				}

		}

	}

	public static List<Entidad> getEntidades() throws ExceptionWebPrepago, SQLException {
		Configuracion conf = null;
		Connection conexion = null;
		Entidad entidad;
		ResultSet rst = null;
		List<Entidad> entidades = new ArrayList<Entidad>();

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para getEntidades");
			}

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_OBTENER_ENTIDADES);
			if(sentencia == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para getEntidades");
			}
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getDataSource());

			logger.info(GestorBD.class.getName(), "getEntidades",
					"Obteniendo entidades");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), sentencia.getTimeout());
			// Recorre los resultados de las busquedas
			while (rst.next()) {

				entidad = new Entidad();
				entidad.setCodigo(rst.getInt("FIID_ENTIDAD"));
				entidad.setNombre(rst.getString("ENT_DESCRIPCION"));
				entidad.setId(rst.getInt("ID_ENTIDAD"));
				
				entidades.add(entidad);
			}
			logger.info(GestorBD.class.getName(), "getEntidades",
					"Agregando Entidad exitosa. cantidad: " + entidades.size());
		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "getEntidades",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "getEntidades",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
				// si el resulset no es nullo cierra la conexion
				if (rst != null) {
					rst.close();
				}
		}
		return entidades;
	}

	public static int secuencialEntidad() throws ExceptionWebPrepago, SQLException {

		Configuracion conf = null;
		Connection conexion = null;
		int valor = 0;
		ResultSet rst = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para secuencialEntidad");
			}

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_OBTENER_SECUENCIA_ENTIDAD);
			if(sentencia == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para secuencialEntidad");
			}
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getDataSource());

			logger.info(GestorBD.class.getName(), "secuencialEntidad",
					"Obteniendo entidades");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), sentencia.getTimeout());
			// Recorre los resultados de las busquedas
			if (rst.next()) {
				valor = rst.getInt("ID_ENTIDAD");
			}
		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "secuencialEntidad",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "secuencialEntidad",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
				// si el resulset no es nullo cierra la conexion
				if (rst != null) {
					rst.close();
				}
		}
		return valor;
	}

	public static void registrarEntidad(Entidad enti)
			throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para registrarEntidad");
			}
			List<Object> params = new ArrayList<Object>();

			params.add(secuencialEntidad());
			params.add(enti.getNombre());
			params.add(enti.getCodigo());

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_REGISTRAR_ENTIDAD);
			if(sentencia == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para registrarEntidad");
			}
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getDataSource());
			logger.info(GestorBD.class.getName(), "registrarEntidad",
					"Consultando existencia de usuario");
			ManejadorDeDatosDB.actualizarDB(conexion, sentencia.getQuery(),
					params, sentencia.getTimeout());

		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "registrarEntidad",
					"Error al consultar la configuracion", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);

		}
	}

	public static void modificarEntidad(Entidad entidad)
			throws ExceptionWebPrepago, SQLException {
		Configuracion conf = null;
		Connection conexion = null;
		ResultSet rst = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para modificarEntidad");
			}

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_MODIFICAR_ENTIDAD);
			if(sentencia == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para modificarEntidad");
			}
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getDataSource());
			List<Object> params = new ArrayList<Object>();
			params.add(entidad.getNombre());
			params.add(entidad.getCodigo());
			params.add(entidad.getId());

			logger.info(GestorBD.class.getName(), "modificarEntidad",
					"Obteniendo las entidades");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), params, sentencia.getTimeout());

		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "modificarEntidad",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "modificarEntidad",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
				// si el resulset no es nulo cierra la conexion
				if (rst != null) {
					rst.close();
				}
		}

	}

	public static void eliminarEntidad(int fiid) throws ExceptionWebPrepago, SQLException {
		Configuracion conf = null;
		Connection conexion = null;
		ResultSet rst = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para eliminarEntidad");
			}

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_ELIMINAR_ENTIDAD);
			if(sentencia == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para eliminarEntidad");
			}
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getDataSource());
			List<Object> params = new ArrayList<Object>();
			params.add(fiid);

			logger.info(GestorBD.class.getName(), "eliminarEntidad",
					"Obteniendo las entidades");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), params, sentencia.getTimeout());

		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "eliminarEntidad",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "eliminarEntidad",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
				// si el resulset no es nulo cierra la conexion
				if (rst != null) {
					rst.close();
				}
		}

	}

	public static int secuencialCodigoTran() throws ExceptionWebPrepago {

		Configuracion conf = null;
		Connection conexion = null;
		int valor = 0;
		ResultSet rst = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para secuencialCodigoTran");
			}

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_OBTENER_SECUENCIA_CODIGO_TRAN);
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getDataSource());

			logger.info(GestorBD.class.getName(), "secuencialCodigoTran",
					"Obteniendo entidades");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), sentencia.getTimeout());
			// Recorre los resultados de las busquedas
			if (rst.next()) {
				valor = rst.getInt("ID_COD_TRAN");
			}
		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "secuencialCodigoTran",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "secuencialCodigoTran",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
			try {
				// si el resulset no es nullo cierra la conexion
				if (rst != null) {
					rst.close();
				}
			} catch (SQLException e) {
				logger.error(GestorBD.class.getName(), "secuencialCodigoTran",
						"Error al cerrar recursos de BD", e);
				throw new ExceptionWebPrepago("Error al cerrar recursos de BD",
						e);
			}

		}
		return valor;
	}

	public static void registrarCodigo(CodigosTransaccion cod)
			throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para registrarCodigo");
			}
			List<Object> params = new ArrayList<Object>();

			params.add(secuencialCodigoTran());
			params.add(cod.getCodigo());
			params.add(cod.getNombre());

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_REGISTRAR_COD_TRAN);
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getDataSource());
			logger.info(GestorBD.class.getName(), "registrarCodigo",
					"Consultando existencia de usuario");
			ManejadorDeDatosDB.actualizarDB(conexion, sentencia.getQuery(),
					params, sentencia.getTimeout());

		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "registrarCodigo",
					"Error al consultar la configuracion", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);

		}
	}

	public static void modificarCodigoTran(CodigosTransaccion codigo)
			throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion = null;
		ResultSet rst = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para modificarCodigoTran");
			}

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_MODIFICAR_CODIGO_TRAN);
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getDataSource());
			List<Object> params = new ArrayList<Object>();
			params.add(codigo.getCodigo());
			params.add(codigo.getNombre());
			params.add(codigo.getId());

			logger.info(GestorBD.class.getName(), "modificarCodigoTran",
					"Obteniendo las entidades");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), params, sentencia.getTimeout());

		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "modificarCodigoTran",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "modificarCodigoTran",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
			try {
				// si el resulset no es nulo cierra la conexion
				if (rst != null) {
					rst.close();
				}
			} catch (SQLException e) {
				logger.error(GestorBD.class.getName(), "modificarCodigoTran",
						"Error al cerrar recursos de BD", e);
				throw new ExceptionWebPrepago("Error al cerrar recursos de BD",
						e);
			}

		}

	}

	public static List<TipoTarjeta> getTipoTar() throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion = null;
		TipoTarjeta tarjeta;
		ResultSet rst = null;
		List<TipoTarjeta> tarjetas = new ArrayList<TipoTarjeta>();

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para getTipoTar");
			}

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_OBTENER_TIPO_TAR);
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getDataSource());

			logger.info(GestorBD.class.getName(), "getTipoTar",
					"Obteniendo entidades");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), sentencia.getTimeout());
			// Recorre los resultados de las busquedas
			while (rst.next()) {
				tarjeta = new TipoTarjeta();
				tarjeta.setId(rst.getInt("ID_TIPO_TARJETA"));
				tarjeta.setNombre(rst.getString("TIP_DESCRIPCION"));
				
				tarjetas.add(tarjeta);
			}
			logger.info(GestorBD.class.getName(), "getTipoTar",
					"Agregando TipoTar exitoso, total: " + tarjetas.size());
		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "getTipoTar",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "getTipoTar",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
			try {
				// si el resulset no es nullo cierra la conexion
				if (rst != null) {
					rst.close();
				}
			} catch (SQLException e) {
				logger.error(GestorBD.class.getName(), "getTipoTar",
						"Error al cerrar recursos de BD", e);
				throw new ExceptionWebPrepago("Error al cerrar recursos de BD",
						e);
			}

		}
		return tarjetas;
	}

	public static int secuencialTipoTarjeta() throws ExceptionWebPrepago {

		Configuracion conf = null;
		Connection conexion = null;
		int valor = 0;
		ResultSet rst = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para secuencialTipoTarjeta");
			}

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_OBTENER_SECUENCIA_TIPO_TARJETA);
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getDataSource());

			logger.info(GestorBD.class.getName(), "secuencialTipoTarjeta",
					"Obteniendo entidades");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), sentencia.getTimeout());
			// Recorre los resultados de las busquedas
			if (rst.next()) {
				valor = rst.getInt("ID_TIPO_TARJETA");
			}
		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "secuencialTipoTarjeta",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "secuencialTipoTarjeta",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
			try {
				// si el resulset no es nullo cierra la conexion
				if (rst != null) {
					rst.close();
				}
			} catch (SQLException e) {
				logger.error(GestorBD.class.getName(), "secuencialCodigoTran",
						"Error al cerrar recursos de BD", e);
				throw new ExceptionWebPrepago("Error al cerrar recursos de BD",
						e);
			}

		}
		return valor;
	}

	public static void registrarTipoTarjeta(TipoTarjeta tipoTar)
			throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para registrarTipoTarjeta");
			}
			List<Object> params = new ArrayList<Object>();

			params.add(secuencialTipoTarjeta());
			params.add(tipoTar.getNombre());

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_REGISTRAR_TIPO_TAR);
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getDataSource());
			logger.info(GestorBD.class.getName(), "registrarTipoTarjeta",
					"Consultando existencia de usuario");
			ManejadorDeDatosDB.actualizarDB(conexion, sentencia.getQuery(),
					params, sentencia.getTimeout());

		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "registrarTipoTarjeta",
					"Error al consultar la configuracion", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);

		}
	}

	public static void modificarTipoTarjeta(TipoTarjeta tarjeta)
			throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion = null;
		ResultSet rst = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para modificarTipoTarjeta");
			}

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_MODIFICAR_TIPO_TARJETA);
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getDataSource());
			List<Object> params = new ArrayList<Object>();
			params.add(tarjeta.getNombre());
			params.add(tarjeta.getId());

			logger.info(GestorBD.class.getName(), "modificarTipoTarjeta",
					"Obteniendo las entidades");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), params, sentencia.getTimeout());

		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "modificarTipoTarjeta",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "modificarTipoTarjeta",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
			try {
				// si el resulset no es nulo cierra la conexion
				if (rst != null) {
					rst.close();
				}
			} catch (SQLException e) {
				logger.error(GestorBD.class.getName(), "modificarTipoTarjeta",
						"Error al cerrar recursos de BD", e);
				throw new ExceptionWebPrepago("Error al cerrar recursos de BD",
						e);
			}

		}

	}

	public static ParamBines getParamBines(String numeroTarjeta)
			throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion = null;
		ParamBines paramBines = null;
		ResultSet rst = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para getParamBines");
			}

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_OBTENER_PARAM_BINES);
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getDataSource());

			List<Object> params = new ArrayList<Object>();
			params.add(numeroTarjeta);

			logger.info(GestorBD.class.getName(), "getParamBines",
					"Obteniendo entidades");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), params, sentencia.getTimeout());
			// Recorre los resultados de las busquedas
			while (rst.next()) {

				paramBines = new ParamBines();
				paramBines.setBin(rst.getInt("BIN"));
				paramBines.setRangoIni(rst.getLong("RANGOINI"));
				paramBines.setRangoFin(rst.getLong("RANGOFIN"));
				paramBines.setImagen(rst.getString("IMAGEN"));
				paramBines.setLongitudTarjeta(rst.getInt("LONGITUD"));
				Entidad entidad = new Entidad();
				entidad.setId(rst.getInt("IDENTIDAD"));
				entidad.setNombre(rst.getString("ENTIDAD"));
				TipoTarjeta tipoTarjeta = new TipoTarjeta();
				tipoTarjeta.setId(rst.getInt("IDTARJETA"));
				tipoTarjeta.setNombre(rst.getString("TIPOTARJETA"));
				paramBines.setEntidad(entidad);
				paramBines.setTipoTarjeta(tipoTarjeta);

				logger.info(GestorBD.class.getName(), "getParamBines",
						"Agregando param Bin ");

			}
		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "getParamBines",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "getParamBines",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
			try {
				// si el resulset no es nullo cierra la conexion
				if (rst != null) {
					rst.close();
				}
			} catch (SQLException e) {
				logger.error(GestorBD.class.getName(), "getParamBines",
						"Error al cerrar recursos de BD", e);
				throw new ExceptionWebPrepago("Error al cerrar recursos de BD",
						e);
			}

		}
		return paramBines;
	}

	/**public static String getNombreEstadoBPObyCodigo(int codigo)
			throws Exception {
		for (Estado estado : GestorCache.getEstadosBPO()) {
			if (estado.getCodigo() == codigo) {
				return estado.getAs400();
			}
		}
		return "";
	}**/

	/**public static String getNombreTransaccionBPObyCodigo(int codigo)
			throws Exception {
		for (CodigosTransaccion c : GestorCache.getCodigosTransBPO()) {
			if (c.getCodigo() == codigo) {
				return c.getAs400();
			}
		}
		return "";
	}**/

	public static int secuencialEstado() throws ExceptionWebPrepago {

		Configuracion conf = null;
		Connection conexion = null;
		int valor = 0;
		ResultSet rst = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para secuencialEstado");
			}

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_OBTENER_SECUENCIA_ESTADO);
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getDataSource());

			logger.info(GestorBD.class.getName(), "secuencialEstado",
					"Obteniendo entidades");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), sentencia.getTimeout());
			// Recorre los resultados de las busquedas
			if (rst.next()) {
				valor = rst.getInt("ID_ESTADO_TARJETA");
			}
		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "secuencialEstado",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "secuencialEstado",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
			try {
				// si el resulset no es nullo cierra la conexion
				if (rst != null) {
					rst.close();
				}
			} catch (SQLException e) {
				logger.error(GestorBD.class.getName(), "secuencialEstado",
						"Error al cerrar recursos de BD", e);
				throw new ExceptionWebPrepago("Error al cerrar recursos de BD",
						e);
			}

		}
		return valor;
	}

	public static void registrarEstado(Estado estado)
			throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para registrarEstado");
			}
			List<Object> params = new ArrayList<Object>();
			params.add(secuencialEstado());
			params.add(estado.getCodigo());
			params.add(estado.getNombre());

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_REGISTRAR_ESTADO);
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getDataSource());
			logger.info(GestorBD.class.getName(), "registrarEstado",
					"Consultando existencia de usuario");
			ManejadorDeDatosDB.actualizarDB(conexion, sentencia.getQuery(),
					params, sentencia.getTimeout());

		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "registrarEstado",
					"Error al consultar la configuracion", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);

		}
	}

	public static void modificarEstado(Estado estado)
			throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion = null;
		ResultSet rst = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para modificarTipoTarjeta");
			}

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_MODIFICAR_ESTADO);
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getDataSource());
			List<Object> params = new ArrayList<Object>();
			params.add(estado.getCodigo());
			params.add(estado.getNombre());
			params.add(estado.getId());

			logger.info(GestorBD.class.getName(), "modificarTipoTarjeta",
					"Obteniendo las entidades");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), params, sentencia.getTimeout());

		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "modificarTipoTarjeta",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "modificarTipoTarjeta",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
			try {
				// si el resulset no es nulo cierra la conexion
				if (rst != null) {
					rst.close();
				}
			} catch (SQLException e) {
				logger.error(GestorBD.class.getName(), "modificarTipoTarjeta",
						"Error al cerrar recursos de BD", e);
				throw new ExceptionWebPrepago("Error al cerrar recursos de BD",
						e);
			}

		}

	}

	public static List<BIN> getBines() throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion = null;
		BIN bin;
		ResultSet rst = null;
		List<BIN> bines = new ArrayList<BIN>();

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para getBines");
			}

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_OBTENER_BINES);
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getDataSource());

			logger.info(GestorBD.class.getName(), "getBines",
					"Obteniendo entidades");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), sentencia.getTimeout());
			// Recorre los resultados de las busquedas
			while (rst.next()) {
				bin = new BIN();
				bin.setId(rst.getInt("IDBIN"));
				bin.setNumero(rst.getLong("BIN"));
				bin.setRangoIni(rst.getLong("RANGOINI"));
				bin.setRangoFin(rst.getLong("RANGOFIN"));
				bin.setLongitud(rst.getInt("LONGITUD"));
				bin.setImagen(rst.getString("NOMBRE_IMAGEN"));
				Entidad entidad = new Entidad();
				entidad.setId(rst.getInt("IDNETIDAD"));
				entidad.setNombre(rst.getString("ENTIDAD"));
				bin.setEntidad(entidad);
				TipoTarjeta tipoTarjeta = new TipoTarjeta();
				tipoTarjeta.setId(rst.getInt("IDTIPOTAR"));
				tipoTarjeta.setNombre(rst.getString("TIPOTAR"));
				bin.setTipoTarjeta(tipoTarjeta);
				
				bines.add(bin);
			}
			logger.info(GestorBD.class.getName(), "getBines",
					"Agregando EntidadExitoso total bines: " + bines.size());
		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "getBines",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "getBines",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
			try {
				// si el resulset no es nullo cierra la conexion
				if (rst != null) {
					rst.close();
				}
			} catch (SQLException e) {
				logger.error(GestorBD.class.getName(), "getBines",
						"Error al cerrar recursos de BD", e);
				throw new ExceptionWebPrepago("Error al cerrar recursos de BD",
						e);
			}

		}
		return bines;
	}

	public static int secuencialBin() throws ExceptionWebPrepago {

		Configuracion conf = null;
		Connection conexion = null;
		int valor = 0;
		ResultSet rst = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para secuencialBin");
			}

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_OBTENER_SECUENCIA_BIN);
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getDataSource());

			logger.info(GestorBD.class.getName(), "secuencialBin",
					"Obteniendo entidades");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), sentencia.getTimeout());
			// Recorre los resultados de las busquedas
			if (rst.next()) {
				valor = rst.getInt("PARAM_BIN_ID");
			}
		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "secuencialBin",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "secuencialBin",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
			try {
				// si el resulset no es nullo cierra la conexion
				if (rst != null) {
					rst.close();
				}
			} catch (SQLException e) {
				logger.error(GestorBD.class.getName(), "secuencialBin",
						"Error al cerrar recursos de BD", e);
				throw new ExceptionWebPrepago("Error al cerrar recursos de BD",
						e);
			}

		}
		return valor;
	}

	public static void registrarBin(BIN bin) throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para registrarBin");
			}

			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getDataSource());

			List<Object> params = new ArrayList<Object>();
			params.add(bin.getNumero());
			params.add(bin.getRangoIni());
			params.add(bin.getRangoFin());
			params.add(bin.getEntidad().getCodigo());
			params.add(bin.getLongitud());
			params.add(bin.getImagen());
			params.add(bin.getTipoTarjeta().getId());
			params.add(secuencialBin());

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_REGISTRAR_BIN);
			logger.info(GestorBD.class.getName(), "registrarBin",
					"Consultando existencia de usuario");
			ManejadorDeDatosDB.actualizarDB(conexion, sentencia.getQuery(),
					params, sentencia.getTimeout());

		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "registrarBin",
					"Error al consultar la configuracion", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);

		}
	}

	public static void modificarBin(BIN bin) throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion = null;
		ResultSet rst = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para modificarBin");
			}

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_MODIFICAR_BIN);
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getDataSource());
			List<Object> params = new ArrayList<Object>();
			params.add(bin.getNumero());
			params.add(bin.getRangoIni());
			params.add(bin.getRangoFin());
			params.add(bin.getEntidad().getId());
			params.add(bin.getLongitud());
			params.add(bin.getImagen());
			params.add(bin.getTipoTarjeta().getId());
			params.add(bin.getId());

			logger.info(GestorBD.class.getName(), "modificarBin",
					"Obteniendo las entidades");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), params, sentencia.getTimeout());

		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "modificarBin",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "modificarBin",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
			try {
				// si el resulset no es nulo cierra la conexion
				if (rst != null) {
					rst.close();
				}
			} catch (SQLException e) {
				logger.error(GestorBD.class.getName(), "modificarBin",
						"Error al cerrar recursos de BD", e);
				throw new ExceptionWebPrepago("Error al cerrar recursos de BD",
						e);
			}

		}

	}

	@SuppressWarnings("resource")
	public static boolean existeTarjetaBPO(String numTar)
			throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion = null;
		ResultSet rst = null;
		boolean existe = false;
		// consulta BPO
		// ***********************************************************************************
		String tarjetaFormatead = String.format("%-19s", numTar); //espacios derecha
		
		String filler = "";
		filler = String.format("%1$-4044s", filler);

		String filler2 = "";
		filler2 = String.format("%1$-4064s", filler2);

		String param1 = "1" + tarjetaFormatead + filler;
		CallableStatement callableStatement = null;
		String query = "{? = CALL TDPGMSV5R.TDCWPB1('1" + tarjetaFormatead + filler
				+ "','" + filler2 + "') }";

		
		
		try {

			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getBpodataSource());
			Sentencia sentencia = obtenerSentencia(conf, ConstantesWebPrepago.QUERY_LLAMADO_BPO_PCI);
			callableStatement = conexion.prepareCall(
					//"CALL TDPGMSV5R.TDCWPB1(?,?)", ResultSet.TYPE_FORWARD_ONLY,
					sentencia.getQuery(), ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			callableStatement.setString(1, param1);
			callableStatement.registerOutParameter(2, Types.VARCHAR);
			callableStatement.execute();
			logger.debug(GestorBD.class.getName(), "existeTarjetaBPO",
					"Se ejecuta el programa : " + query);

			String resultado = "";
			String respuesta = "";
			resultado = callableStatement.getString(2);
			logger.debug(GestorBD.class.getName(), "existeTarjetaBPO","Respuesta BPO : " + resultado);
			respuesta = resultado.substring(39, 44);
			if (respuesta.equals(ConstantesWebPrepago.RESPUESTA_BPO)) {
				existe = true;
				logger.debug(GestorBD.class.getName(), "existeTarjetaBPO",
						"Exitosa");
			} else if (respuesta.equals(ConstantesWebPrepago.RESPUESTA_BPO1)) {
				existe = false;
				logger.debug(GestorBD.class.getName(), "existeTarjetaBPO",
						"No existe registro coincidente");
			} else if (respuesta.equals(ConstantesWebPrepago.RESPUESTA_BPO2)) {
				logger.debug(GestorBD.class.getName(), "existeTarjetaBPO",
						"No hay conexion con base de datos");
				throw new ExceptionWebPrepago(
						"No hay conexión con base de datos");
			}
			// ************************************************************************************************
			// try {
			// conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// //Valida que no sea nulos para poder ejecutar el query
			// if(conf.getQueries() == null || conf.getDataSource()== null){
			// throw new
			// ExceptionWebPrepago("Error al leer la configuracion del archivo campos conf, queries y/o dataSource en null, para modificarBin");
			// }
			//
			// Sentencia sentencia = obtenerSentencia(conf,
			// ConstantesWebPrepago.QUERY_EXISTE_TARJETA_BPO);
			// conexion =
			// ManejadorDeDatosDB.obtenerConexionPorDatasource(conf.getBpodataSource());
			// List<Object> params = new ArrayList<Object>();
			// params.add(numTar);
			//
			// logger.info(GestorBD.class.getName(), "modificarBin",
			// "Obteniendo las entidades");
			// rst = ManejadorDeDatosDB.consultarBD(conexion,
			// sentencia.getQuery(), params, sentencia.getTimeout());
			// if (rst.next()) {
			// existe = true;
			// return existe;
			// }
			return existe;

		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "modificarBin",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "modificarBin",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
			try {
				// si el resulset no es nulo cierra la conexion
				if (callableStatement != null) {
					callableStatement.close();
				}
			} catch (SQLException e) {
				logger.error(GestorBD.class.getName(), "modificarBin",
						"Error al cerrar recursos de BD", e);
				throw new ExceptionWebPrepago("Error al cerrar recursos de BD",
						e);
			}

		}

	}

	public static boolean existeTartejaBD(String numTar)
			throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion = null;
		ResultSet rst = null;
		boolean existe = false;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para modificarBin");
			}

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_EXISTE_TARJETA_BD);
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getDataSource());
			List<Object> params = new ArrayList<Object>();
			params.add(numTar);

			logger.info(GestorBD.class.getName(), "modificarBin",
					"Obteniendo las entidades");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), params, sentencia.getTimeout());
			if (rst.next()) {
				existe = true;
				return existe;
			}
			return existe;

		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "modificarBin",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "modificarBin",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
			try {
				// si el resulset no es nulo cierra la conexion
				if (rst != null) {
					rst.close();
				}
			} catch (SQLException e) {
				logger.error(GestorBD.class.getName(), "modificarBin",
						"Error al cerrar recursos de BD", e);
				throw new ExceptionWebPrepago("Error al cerrar recursos de BD",
						e);
			}

		}

	}

	public static List<UsuarioInactivo> getUsuariosInactivos(Integer inactividad)
			throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion = null;
		UsuarioInactivo usuarioInactivo;
		ResultSet rst = null;
		Sentencia sentencia;
		List<Object> params = new ArrayList<Object>();
		List<UsuarioInactivo> usuariosInactivos = new ArrayList<UsuarioInactivo>();

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para getBines");
			}
			if(inactividad==null || inactividad==0) {
				sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_OBTENER_USR_INACTIVOS);
				conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
						.getDataSource());
				
				logger.info(GestorBD.class.getName(), "getBines",
						"Obteniendo usuarios inactivos");
				rst = ManejadorDeDatosDB.consultarBD(conexion,
						sentencia.getQuery(), sentencia.getTimeout());
			}else {
				params.add(inactividad);
				sentencia = obtenerSentencia(conf,
						ConstantesWebPrepago.QUERY_OBTENER_USR_INACTIVOS_DIAS);
				conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
						.getDataSource());

				logger.info(GestorBD.class.getName(), "getBines",
						"Obteniendo usuarios inactivos");
				rst = ManejadorDeDatosDB.consultarBD(conexion,
						sentencia.getQuery(),params, sentencia.getTimeout());
			}
			
			// Recorre los resultados de las busquedas
			while (rst.next()) {
				usuarioInactivo = new UsuarioInactivo();
				usuarioInactivo.setUsuario(rst.getString("USR_DOCUMENTO"));
				String fecha = new SimpleDateFormat("dd/MM/yyyy").format(rst
						.getTimestamp("USR_FECHACREACION"));
				usuarioInactivo.setFechaCreacion(fecha);
				String fechaUlt = new SimpleDateFormat("dd/MM/yyyy")
						.format(new Date(rst.getDate("USR_ULTIMACONEXION")
								.getTime()));
				usuarioInactivo.setFechaUltimaConexion(fechaUlt);
				usuarioInactivo.setTiempoInactividad(rst
						.getString("DIAS_INACTIVIDAD"));
				usuarioInactivo.setFechaNotificacion(rst
						.getString("USR_FECHANOTIFICACION"));
				//logger.info(GestorBD.class.getName(), "getBines",
				//		"Agregando usuario: " + usuarioInactivo.getUsuario());
				usuarioInactivo.setId(rst.getInt("ID_USUARIO"));
				usuarioInactivo.setCorreo(rst.getString("USR_CORREO"));
				usuarioInactivo.setNombres(rst.getString("USR_NOMBRE")+" "+rst.getString("USR_APELLIDO"));

				usuariosInactivos.add(usuarioInactivo);

			}
			logger.info(GestorBD.class.getName(), "getBines", "Se encontraron "+usuariosInactivos.size()+" usuarios");
		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "getBines",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "getBines",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
			try {
				// si el resulset no es nullo cierra la conexion
				if (rst != null) {
					rst.close();
				}
			} catch (SQLException e) {
				logger.error(GestorBD.class.getName(), "getBines",
						"Error al cerrar recursos de BD", e);
				throw new ExceptionWebPrepago("Error al cerrar recursos de BD",
						e);
			}

		}
		return usuariosInactivos;
	}

	public static String getNombreImagen(String tarjeta, Connection conexion)
			throws ExceptionWebPrepago {
		Configuracion conf = null;
		// Connection conexion = null;
		String nombreImagen = null;
		ResultSet rst = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para getBines");
			}

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_OBTENER_NOMBRE_IMG);
			// conexion =
			// ManejadorDeDatosDB.obtenerConexionPorDatasource(conf.getDataSource());
			List<Object> params = new ArrayList<Object>();
			params.add(tarjeta);

			logger.info(GestorBD.class.getName(), "getNombreImagen",
					"Obteniendo usuarios inactivos");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), params, sentencia.getTimeout());
			// Recorre los resultados de las busquedas
			if (rst.next()) {
				nombreImagen = rst.getString("NOMBRE_IMAGEN");
			}
		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "getNombreImagen",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "getNombreImagen",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			// la conexion la cierra el que llama el metodo
			try {
				// si el resulset no es nullo cierra la conexion
				if (rst != null) {
					rst.close();
				}
			} catch (SQLException e) {
				logger.error(GestorBD.class.getName(), "getNombreImagen",
						"Error al cerrar recursos de BD", e);
				throw new ExceptionWebPrepago("Error al cerrar recursos de BD",
						e);
			}

		}
		return nombreImagen;
	}

	public static void ActualizarUsuarioNotificacion(String usuario) throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion = null;
		ResultSet rst = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para ActualizarUsuarioNotificacion");
			}

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_MODIFICAR_USUARIO_NOTIFICAR);
			conexion =ManejadorDeDatosDB.obtenerConexionPorDatasource(conf.getDataSource());
			List<Object> params = new ArrayList<Object>();
			params.add(new java.sql.Date(new Date().getTime()));
			params.add(usuario);

			logger.info(GestorBD.class.getName(),
					"ActualizarUsuarioNotificacion", "Obteniendo las entidades");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), params, sentencia.getTimeout());

		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(),
					"ActualizarUsuarioNotificacion",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(),
					"ActualizarUsuarioNotificacion",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			try {
				 ManejadorDeDatosDB.cerrarConexion(conexion);
				// si el resulset no es nulo cierra la conexion
				if (rst != null) {
					rst.close();
				}
			} catch (SQLException e) {
				logger.error(GestorBD.class.getName(),
						"ActualizarUsuarioNotificacion",
						"Error al cerrar recursos de BD", e);
				throw new ExceptionWebPrepago("Error al cerrar recursos de BD",
						e);
			}

		}

	}

	public static void ActualizarUltimaConexion(String id)
			throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion = null;
		ResultSet rst = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para ActualizarUltimaConexion");
			}

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_MODIFICAR_ULTIMA_CONEXION);
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getDataSource());
			List<Object> params = new ArrayList<Object>();
			params.add(new java.sql.Date(new Date().getTime()));
			params.add(id);
			logger.info(GestorBD.class.getName(), "ActualizarUltimaConexion",
					"Obteniendo las entidades");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), params, sentencia.getTimeout());

		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "ActualizarUltimaConexion",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "ActualizarUltimaConexion",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
			try {
				// si el resulset no es nulo cierra la conexion
				if (rst != null) {
					rst.close();
				}
			} catch (SQLException e) {
				logger.error(GestorBD.class.getName(),
						"ActualizarUltimaConexion",
						"Error al cerrar recursos de BD", e);
				throw new ExceptionWebPrepago("Error al cerrar recursos de BD",
						e);
			}

		}

	}

	public static void EliminarUsuarioInactivo(int id/*, Connection conexion*/)
			throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion  = null;
		ResultSet rst = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para EliminarUsuarioInactivo");
			}

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_ELIMINAR_USUARIO_INACTIVO);
			 conexion =
			 ManejadorDeDatosDB.obtenerConexionPorDatasource(conf.getDataSource());
			List<Object> params = new ArrayList<Object>();
			params.add(id);
			logger.info(GestorBD.class.getName(), "EliminarUsuarioInactivo",
					"Obteniendo las entidades");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), params, sentencia.getTimeout());

		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "EliminarUsuarioInactivo",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "EliminarUsuarioInactivo",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			// la conexion la cierra el que llama el metodo
			ManejadorDeDatosDB.cerrarConexion(conexion);
			try {
				// si el resulset no es nulo cierra la conexion
				if (rst != null) {
					rst.close();
				}
			} catch (SQLException e) {
				logger.error(GestorBD.class.getName(),
						"EliminarUsuarioInactivo",
						"Error al cerrar recursos de BD", e);
				throw new ExceptionWebPrepago("Error al cerrar recursos de BD",
						e);
			}

		}

	}
	
	/**Metodo duplicado para eliminar usuarios desde el job, se envia la conexion*/
	public static void EliminarUsuarioInactivoJob(int id, Connection conexion)
			throws ExceptionWebPrepago {
		Configuracion conf = null;
		ResultSet rst = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para EliminarUsuarioInactivoJob");
			}

			Sentencia sentencia = obtenerSentencia(conf,ConstantesWebPrepago.QUERY_ELIMINAR_USUARIO_INACTIVO);
			List<Object> params = new ArrayList<Object>();
			params.add(id);
			logger.info(GestorBD.class.getName(), "EliminarUsuarioInactivoJob","Obteniendo las entidades");
			rst = ManejadorDeDatosDB.consultarBD(conexion,sentencia.getQuery(), params, sentencia.getTimeout());

		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "EliminarUsuarioInactivoJob","Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago("Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "EliminarUsuarioInactivoJob","Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago("Error al consultar la configuracion", e);
		} finally {
			// la conexion la cierra el que llama el metodo
			//ManejadorDeDatosDB.cerrarConexion(conexion);
			try {
				// si el resulset no es nulo cierra la conexion
				if (rst != null) {
					rst.close();
				}
			} catch (SQLException e) {
				logger.error(GestorBD.class.getName(),"EliminarUsuarioInactivoJob","Error al cerrar recursos de BD", e);
				throw new ExceptionWebPrepago("Error al cerrar recursos de BD",e);
			}

		}

	}
	
	

	public static void registrarHistorico(Historico historico/*,
			Connection conexion*/) throws ExceptionWebPrepago {

		Configuracion conf = null;
		Connection conexion = null;
		//boolean crearConexion = (conexion == null);
		ResultSet rst = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para registrarHistorico");
			}

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_REGISTRAR_HISTORICO);
			//if (crearConexion){
				conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
						.getDataSource());
				//crearConexion = true;
			//}
			List<Object> params = new ArrayList<Object>();
			params.add(secuencialHistorico());
			params.add(historico.getUsuario());
			params.add(new java.sql.Timestamp(new java.util.Date().getTime()));
			if (historico.getNumtarjeta() == null
					|| historico.getNumtarjeta().isEmpty()) {
				params.add(null);
			} else {
				params.add(historico.getNumtarjeta());
			}
			params.add(historico.getEntidad());
			params.add(historico.getAccion());

			logger.info(GestorBD.class.getName(), "registrarHistorico",
					"Obteniendo parametros");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), params, sentencia.getTimeout());

		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "registrarHistorico",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "registrarHistorico",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			// Solo cierra la conexion si el mismo la cra
			//if (crearConexion)
				ManejadorDeDatosDB.cerrarConexion(conexion);
			try {
				// si el resulset no es nulo cierra la conexion
				if (rst != null) {
					rst.close();
				}
			} catch (SQLException e) {
				logger.error(GestorBD.class.getName(), "registrarHistorico",
						"Error al cerrar recursos de BD", e);
				throw new ExceptionWebPrepago("Error al cerrar recursos de BD",
						e);
			}

		}

	}
	
	
	/**Metodo duplicado para registrar hitorial desde el Job, se envia la conexion*/
	public static void registrarHistoricoJob(Historico historico,Connection conexion) throws ExceptionWebPrepago {

		Configuracion conf = null;
		ResultSet rst = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago("Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para registrarHistoricoJob");
			}

			Sentencia sentencia = obtenerSentencia(conf,ConstantesWebPrepago.QUERY_REGISTRAR_HISTORICO);
			
			List<Object> params = new ArrayList<Object>();
			params.add(secuencialHistorico());
			params.add(historico.getUsuario());
			params.add(new java.sql.Timestamp(new java.util.Date().getTime()));
			if (historico.getNumtarjeta() == null || historico.getNumtarjeta().isEmpty()) {
				params.add(null);
			} else {
				params.add(historico.getNumtarjeta());
			}
			params.add(historico.getEntidad());
			params.add(historico.getAccion());

			logger.info(GestorBD.class.getName(), "registrarHistoricoJob","Obteniendo parametros");
			rst = ManejadorDeDatosDB.consultarBD(conexion,sentencia.getQuery(), params, sentencia.getTimeout());

		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "registrarHistoricoJob","Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago("Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "registrarHistoricoJob","Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago("Error al consultar la configuracion", e);
		} finally {
			// Solo cierra la conexion si el mismo la cra
			//if (crearConexion)
				//ManejadorDeDatosDB.cerrarConexion(conexion);
			try {
				// si el resulset no es nulo cierra la conexion
				if (rst != null) {
					rst.close();
				}
			} catch (SQLException e) {
				logger.error(GestorBD.class.getName(), "registrarHistorico","Error al cerrar recursos de BD", e);
				throw new ExceptionWebPrepago("Error al cerrar recursos de BD",e);
			}

		}

	}
	
	

	public static int secuencialHistorico() throws ExceptionWebPrepago {

		Configuracion conf = null;
		Connection conexion = null;
		int valor = 0;
		ResultSet rst = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para secuencialHistorico");
			}

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_OBTENER_SECUENCIA_HISTORICO);
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getDataSource());

			logger.info(GestorBD.class.getName(), "secuencialHistorico",
					"Obteniendo entidades");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), sentencia.getTimeout());
			// Recorre los resultados de las busquedas
			if (rst.next()) {
				valor = rst.getInt("HIS_ID");
			}
		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "secuencialHistorico",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "secuencialHistorico",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
			try {
				// si el resulset no es nullo cierra la conexion
				if (rst != null) {
					rst.close();
				}
			} catch (SQLException e) {
				logger.error(GestorBD.class.getName(), "secuencialHistorico",
						"Error al cerrar recursos de BD", e);
				throw new ExceptionWebPrepago("Error al cerrar recursos de BD",
						e);
			}

		}
		return valor;
	}

	public static void EliminarTarjetasUsuarioInacitvo(int id/*,
			Connection conexion*/) throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion = null;
		ResultSet rst = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para EliminarTarjetasUsuarioInacitvo");
			}

			Sentencia sentencia = obtenerSentencia(
					conf,
					ConstantesWebPrepago.QUERY_ELIMINAR_TARJETAS_USUARIO_INACTIVO);
			 conexion =
			 ManejadorDeDatosDB.obtenerConexionPorDatasource(conf.getDataSource());
			List<Object> params = new ArrayList<Object>();
			params.add(id);
			logger.info(GestorBD.class.getName(),
					"EliminarTarjetasUsuarioInacitvo",
					"Obteniendo las entidades");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), params, sentencia.getTimeout());

		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(),
					"EliminarTarjetasUsuarioInacitvo",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(),
					"EliminarTarjetasUsuarioInacitvo",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			 ManejadorDeDatosDB.cerrarConexion(conexion);
			try {
				// si el resulset no es nulo cierra la conexion
				if (rst != null) {
					rst.close();
				}
			} catch (SQLException e) {
				logger.error(GestorBD.class.getName(),
						"EliminarTarjetasUsuarioInacitvo",
						"Error al cerrar recursos de BD", e);
				throw new ExceptionWebPrepago("Error al cerrar recursos de BD",
						e);
			}

		}

	}
	
	/**Metodo duplicado para la tarea programada, se le pasa la conexion*/
	public static void EliminarTarjetasUsuarioInacitvoJob(int id ,Connection conexion) throws ExceptionWebPrepago {
		Configuracion conf = null;
		ResultSet rst = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para EliminarTarjetasUsuarioInacitvoJob");
			}

			Sentencia sentencia = obtenerSentencia(conf,ConstantesWebPrepago.QUERY_ELIMINAR_TARJETAS_USUARIO_INACTIVO);

			List<Object> params = new ArrayList<Object>();
			params.add(id);
			logger.info(GestorBD.class.getName(),"EliminarTarjetasUsuarioInacitvoJob","Obteniendo las entidades");
			rst = ManejadorDeDatosDB.consultarBD(conexion,sentencia.getQuery(), params, sentencia.getTimeout());

		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(),"EliminarTarjetasUsuarioInacitvoJob","Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago("Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(),"EliminarTarjetasUsuarioInacitvoJob","Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago("Error al consultar la configuracion", e);
		} finally {
			 //ManejadorDeDatosDB.cerrarConexion(conexion);
			try {
				// si el resulset no es nulo cierra la conexion
				if (rst != null) {
					rst.close();
				}
			} catch (SQLException e) {
				logger.error(GestorBD.class.getName(),"EliminarTarjetasUsuarioInacitvoJob","Error al cerrar recursos de BD", e);
				throw new ExceptionWebPrepago("Error al cerrar recursos de BD",e);
			}

		}

	}
	

	public static List<UsuarioInfoErrada> getUsuariosInfoErrada(String usuario,
			String numTar, Date fecha, String clave) throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion = null;
		ResultSet rst = null;
		UsuarioInfoErrada usuarioInfoErrada;
		List<UsuarioInfoErrada> usuarios = new ArrayList<UsuarioInfoErrada>();		
		
		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para getBines");
			}

			String valorUsuario = (!usuario.isEmpty()) ? usuario.contains("*") ? " AND U.USR_DOCUMENTO LIKE '"
					+ usuario.replaceAll("\\*", "%") + "'"
					: " AND U.USR_DOCUMENTO = '"+ usuario + "'":"";
			String valorNumTarjeta = (!numTar.isEmpty()) ? " AND T.TAR_NUMERO LIKE '"
					+ numTar.replaceAll("\\*", "%") + "'"
					: "";
			
			 int intIndex = valorNumTarjeta.indexOf("%");
	          if(intIndex == - 1){
	        	  valorNumTarjeta = (!numTar.isEmpty()) ? " AND T.TAR_NUM_ENCRIPT = '"
	  					+ clave + "'"
	  					: "";
			}
			
			
			String nuevaFecha = "";
			if (fecha != null) {
				SimpleDateFormat sp = new SimpleDateFormat("dd/MM/yyyy");
				nuevaFecha = sp.format(new java.sql.Date(fecha.getTime()));
			}
			String valorFecha = (fecha != null) ? " AND to_date(to_char(u.usr_fechacreacion,'dd/MM/yyyy'),'dd/MM/yyyy') = to_date('"
					+ nuevaFecha + "','dd/MM/yyyy')"
					: "";

			String query = "SELECT U.USR_DOCUMENTO, U.ID_USUARIO,T.TAR_NUM_ENCRIPT, T.TAR_NUMERO, T.TAR_ID_ENTIDAD, E.ENT_DESCRIPCION, U.USR_FECHACREACION, U.USR_CORREO"
					+ " FROM WPR_USUARIO U LEFT JOIN WPR_TARJETAS T ON (T.TAR_ID_USUARIO = U.ID_USUARIO) "
					+ "LEFT JOIN WPR_ENTIDAD E ON (E.ID_ENTIDAD = T.TAR_ID_ENTIDAD) WHERE U.ID_USUARIO IS NOT NULL "
					+ valorUsuario + valorNumTarjeta + valorFecha
					+ " ORDER BY LENGTH (U.USR_DOCUMENTO), U.USR_DOCUMENTO  ASC";

			Sentencia sentencia = obtenerSentencia(
					conf,
					ConstantesWebPrepago.QUERY_ELIMINAR_TARJETAS_USUARIO_INACTIVO);
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getDataSource());

			logger.info(GestorBD.class.getName(), "getNombreImagen",
					"Obteniendo usuarios inactivos");
			rst = ManejadorDeDatosDB.consultarBD(conexion, query,
					sentencia.getTimeout());
			Boolean encontroReg = false;
			// Recorre los resultados de las busquedas
			while (rst.next()) {
				usuarioInfoErrada = new UsuarioInfoErrada();
				usuarioInfoErrada.setId(rst.getInt("ID_USUARIO"));
				usuarioInfoErrada.setUsuario(rst.getString("USR_DOCUMENTO"));
				usuarioInfoErrada.setNumTarjetaEnmascarado(rst
						.getString("TAR_NUMERO"));
				usuarioInfoErrada.setNumTarjetaEncriptado(rst
						.getNString("TAR_NUM_ENCRIPT"));
				usuarioInfoErrada.setEntidad(rst.getString("ENT_DESCRIPCION"));
				String fechaReg = new SimpleDateFormat("dd/MM/yyyy").format(rst
						.getTimestamp("USR_FECHACREACION"));
				usuarioInfoErrada.setFechaRegistro(fechaReg);
				usuarioInfoErrada.setCorreo(rst.getString("USR_CORREO"));

				usuarios.add(usuarioInfoErrada);
				encontroReg = true;
			}
			if(!encontroReg && intIndex != - 1){
				String query2 = "SELECT DISTINCT U.USR_DOCUMENTO, U.ID_USUARIO, U.USR_FECHACREACION, U.USR_CORREO"
						+ " FROM WPR_USUARIO U WHERE U.ID_USUARIO NOT IN (SELECT DISTINCT U.ID_USUARIO FROM WPR_USUARIO U INNER JOIN WPR_TARJETAS T ON (U.ID_USUARIO=T.TAR_ID_USUARIO))"
						//+ "WHERE U.ID_USUARIO IS NOT NULL "
						+ valorUsuario;
				sentencia = obtenerSentencia(
						conf,
						ConstantesWebPrepago.QUERY_ELIMINAR_TARJETAS_USUARIO_INACTIVO);
				conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
						.getDataSource());

				logger.info(GestorBD.class.getName(), "getNombreImagen",
						"Obteniendo usuarios inactivos");
				rst = ManejadorDeDatosDB.consultarBD(conexion, query2,
						sentencia.getTimeout());
				while (rst.next()) {
					usuarioInfoErrada = new UsuarioInfoErrada();
					usuarioInfoErrada.setId(rst.getInt("ID_USUARIO"));
					usuarioInfoErrada.setUsuario(rst.getString("USR_DOCUMENTO"));
					String fechaReg = new SimpleDateFormat("dd/MM/yyyy").format(rst
							.getTimestamp("USR_FECHACREACION"));
					usuarioInfoErrada.setFechaRegistro(fechaReg);
					usuarioInfoErrada.setCorreo(rst.getString("USR_CORREO"));

					usuarios.add(usuarioInfoErrada);
				}
			}
		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "getNombreImagen",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "getNombreImagen",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
			try {
				// si el resulset no es nullo cierra la conexion
				if (rst != null) {
					rst.close();
				}
			} catch (SQLException e) {
				logger.error(GestorBD.class.getName(), "getNombreImagen",
						"Error al cerrar recursos de BD", e);
				throw new ExceptionWebPrepago("Error al cerrar recursos de BD",
						e);
			}

		}
		return usuarios;
	}

	@SuppressWarnings("resource")
	public static boolean consultarExisteBinBPO(long bin, int longitud)
			throws ExceptionWebPrepago, SQLException {
		Configuracion conf = null;
		Connection conexion = null;
		ResultSet rst = null;
		// consulta BPO ********************
		String formatString = String.format("%%0%dd", 9);
		String formattedBin = String.format(formatString, bin);
		String filler = "";
		filler = String.format("%1$-4052s", filler);

		String filler2 = "";
		filler2 = String.format("%1$-4064s", filler2);

		String filler3 = "";
		filler3 = String.format("%1$-4008s", filler3);

		String param1 = "7" + formattedBin + longitud + filler;
		CallableStatement callableStatement = null;
		String query = "{? = CALL TDPGMSV5R.TDCWPB1('7" + formattedBin
				+ longitud + filler + "','" + filler2 + "') }";
		
		
		try {

			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getBpodataSource());
			Sentencia sentencia = obtenerSentencia(conf, ConstantesWebPrepago.QUERY_LLAMADO_BPO_PCI);
			callableStatement = conexion.prepareCall(
					//"CALL TDPGMSV5R.TDCWPB1(?,?)", ResultSet.TYPE_FORWARD_ONLY,
					sentencia.getQuery(), ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			callableStatement.setString(1, param1);
			callableStatement.registerOutParameter(2, Types.VARCHAR);
			callableStatement.execute();
			logger.debug(GestorBD.class.getName(), "consultarExisteBinBPO",
					"Se ejecuta el programa : " + query);
			Boolean existeBin = false;

			String resultado = "";
			String respuesta = "";
			resultado = callableStatement.getString(2);
			logger.debug(GestorBD.class.getName(), "consultarExisteBinBPO",
					"Respuesta BPO : " + resultado);
			respuesta = resultado.substring(21, 26);
			if (respuesta.equals(ConstantesWebPrepago.RESPUESTA_BPO)) {
				existeBin = true;
				logger.debug(GestorBD.class.getName(), "consultarExisteBinBPO",
						"Exitosa");
			} else if (respuesta.equals(ConstantesWebPrepago.RESPUESTA_BPO1)) {
				existeBin = false;
				logger.debug(GestorBD.class.getName(), "consultarExisteBinBPO",
						"No existe registro coincidente");
			} else if (respuesta.equals(ConstantesWebPrepago.RESPUESTA_BPO2)) {
				logger.debug(GestorBD.class.getName(), "consultarExisteBinBPO",
						"No hay conexion con base de datos");
				throw new ExceptionWebPrepago("No hay conexión con base de datos BPO");
			}

			return existeBin;

			// **************************
			// conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// //Valida que no sea nulos para poder ejecutar el query
			// if(conf.getQueries() == null || conf.getBpodataSource()== null){
			// throw new
			// ExceptionWebPrepago("Error al leer la configuracion del archivo campos conf, queries y/o dataSource en null, para consultarExisteBinBPO");
			// }
			//
			// Sentencia sentencia = obtenerSentencia(conf,
			// ConstantesWebPrepago.QUERY_CONSULTA_BIN_BPO);
			// conexion =
			// ManejadorDeDatosDB.obtenerConexionPorDatasource(conf.getBpodataSource());
			//
			// List<Object> params = new ArrayList<Object>();
			// params.add(bin+"%");
			// params.add(longitud);
			//
			// logger.info(GestorBD.class.getName(), "consultarExisteBinBPO",
			// "consultarExisteBinBPO");
			// rst = ManejadorDeDatosDB.consultarBD(conexion,
			// sentencia.getQuery(), params, sentencia.getTimeout());
			//
			//
			// // Recorre los resultados de las busquedas
			// if (rst.next()) {
			// logger.info(GestorBD.class.getName(), "consultarExisteBinBPO",
			// "El numero bin existe");
			// return true;
			// } else {
			// logger.info(GestorBD.class.getName(), "consultarExisteBinBPO",
			// "El numero no bin existe");
			// return false;
			// }
		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "consultarExisteBinBPO","Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago("Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "consultarExisteBinBPO","Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago("Error al consultar la configuracion", e);
		} finally {
			if (callableStatement != null)
				callableStatement.close();
			GestorBD.cerrarRecursos(null, callableStatement);
			ManejadorDeDatosDB.cerrarConexion(conexion);

			try {
				// si el resulset no es nulo cierra la conexion
				if (callableStatement != null) {
					callableStatement.close();
				}
			} catch (SQLException e) {
				logger.error(GestorBD.class.getName(), "consultarExisteBinBPO",
						"Error al cerrar recursos de BD", e);
				throw new ExceptionWebPrepago("Error al cerrar recursos de BD",
						e);
			}

		}

	}

	/**
	 * Metodo para cerrar los recursos de la base de datos
	 * 
	 * @param resultSet
	 * @param callableStatement
	 */
	private static void cerrarRecursos(ResultSet resultSet,
			CallableStatement callableStatement) {
		try {
			if (resultSet != null) {
				resultSet.close();
			}
			if (callableStatement != null) {
				callableStatement.close();
			}
		} catch (SQLException e) {
			logger.error("GestorBD", "cerrarRecursos",
					"Error al cerrar los recursos de la BD", e);
		}
	}
	
	public static void actualizarInfoPersonal(Usuario usr)
			throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para actualizarInfoPersonal");
			}
			List<Object> params = new ArrayList<Object>();
			params.add(usr.getNombres());
			params.add(usr.getApellidos());
			params.add(usr.getTelefono());
			params.add(usr.getCorreo());
			params.add(usr.getPregunta1());
			params.add(usr.getRespuesta1());
			params.add(usr.getPregunta2());
			params.add(usr.getRespuesta2());
			params.add(usr.getPregunta3());
			params.add(usr.getRespuesta3());
			
			params.add(usr.getNumDocumento());

			Sentencia sentencia = obtenerSentencia(conf,ConstantesWebPrepago.QUERY_ACTUALIZAR_INFO_PERSONAL);
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf.getDataSource());
			logger.info(GestorBD.class.getName(), "actualizarInfoPersonal","Consultando existencia de usuario");
			ManejadorDeDatosDB.actualizarDB(conexion, sentencia.getQuery(),params, sentencia.getTimeout());
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "actualizarInfoPersonal", "Error al consultar la configuracion", e);
			throw new ExceptionWebPrepago("Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);

		}
	}
	
	
	public static List<Parametro> getParametros() throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion = null;
		ResultSet rst = null;
		List<Parametro> listaParametros = new ArrayList<Parametro>();
		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para getParametros");
			}

			Sentencia sentencia = obtenerSentencia(conf,ConstantesWebPrepago.QUERY_CONSULTAR_PARAMETROS);
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf.getDataSource());

			logger.info(GestorBD.class.getName(), "getParametros","Obteniendo parametros");
			rst = ManejadorDeDatosDB.consultarBD(conexion,sentencia.getQuery(), sentencia.getTimeout());
			// Recorre los resultados de las busquedas
			while (rst.next()) {
				Parametro parametro = new Parametro();
				parametro.setParametroNombre(rst.getString("NOMBRE"));
				parametro.setParametroValor(rst.getString("VALOR"));
				
				listaParametros.add(parametro);
			}
			logger.info(GestorBD.class.getName(), "getParametros",
					"Agregando parametros exitoso, cantidad: " + listaParametros.size());
		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "getParametros",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "getParametros",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
			try {
				// si el resulset no es nullo cierra la conexion
				if (rst != null) {
					rst.close();
				}
			} catch (SQLException e) {
				logger.error(GestorBD.class.getName(), "getParametros",
						"Error al cerrar recursos de BD", e);
				throw new ExceptionWebPrepago("Error al cerrar recursos de BD",
						e);
			}

		}
		return listaParametros;
	}
	
  public static List<Bolsillo> getBolsillos() throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion = null;
		Bolsillo bolsillo;
		ResultSet rst = null;
		List<Bolsillo> bolsillos = new ArrayList<Bolsillo>();

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para getBolsillos");
			}

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_OBTENER_BOLSILLOS);
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getDataSource());

			logger.info(GestorBD.class.getName(), "getBolsillos",
					"Obteniendo bolsillos");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), sentencia.getTimeout());
			// Recorre los resultados de las busquedas
			while (rst.next()) {
				bolsillo = new Bolsillo();
				bolsillo.setId(rst.getString("ID_TIPO_BOLSILLO"));
				bolsillo.setNombre(rst.getString("TIP_DESCRIPCION"));
				logger.info(GestorBD.class.getName(), "getbolsillos",
						"Agregando bolsillo: " + bolsillo.getNombre());
				bolsillos.add(bolsillo);
			}
		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "getBolsillos",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "getBolsillos",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
			try {
				// si el resulset no es nullo cierra la conexion
				if (rst != null) {
					rst.close();
				}
			} catch (SQLException e) {
				logger.error(GestorBD.class.getName(), "getBolsillos",
						"Error al cerrar recursos de BD", e);
				throw new ExceptionWebPrepago("Error al cerrar recursos de BD",
						e);
			}

		}
		return bolsillos;
	}
  
  public static List<Bolsillo> getBolsillosbyTarjeta(String tarjeta)
			throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion = null;
		Bolsillo bolsillo;
		ResultSet rst = null;
		List<Bolsillo> bolsillos = new ArrayList<Bolsillo>();
		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para getBolsillosbyTarjeta");
			}

			Sentencia sentencia = obtenerSentencia(conf,
					ConstantesWebPrepago.QUERY_OBTENER_BOLSILLOS_TARJETA);
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf
					.getDataSource());
			List<Object> params = new ArrayList<Object>();
			params.add(tarjeta);

			logger.info(GestorBD.class.getName(), "getBolsillosbyTarjeta",
					"Obteniendo getBolsillosbyTarjeta");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), params, sentencia.getTimeout());
			// Recorre los resultados de las busquedas
			while (rst.next()) {
				bolsillo = new Bolsillo();
				bolsillo.setId(rst.getString("ID_TIPO_BOLSILLO"));
				bolsillo.setNombre(rst.getString("TIP_DESCRIPCION"));
				bolsillos.add(bolsillo);
			}
		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "getBolsillosbyTarjeta",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "getBolsillosbyTarjeta",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			// la conexion la cierra el que llama el metodo
			ManejadorDeDatosDB.cerrarConexion(conexion);
			try {
				// si el resulset no es nullo cierra la conexion
				if (rst != null) {
					rst.close();
				}
			} catch (SQLException e) {
				logger.error(GestorBD.class.getName(), "getBolsillosbyTarjeta",
						"Error al cerrar recursos de BD", e);
				throw new ExceptionWebPrepago("Error al cerrar recursos de BD",
						e);
			}

		}
		return bolsillos;
	}
  
  public static void guardarParametrosTarea(String diaN, String diaE, String correos)
			throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para guardarParametrosTarea");
			}
			List<Object> params = new ArrayList<Object>();
			params.add(diaN);
			params.add(diaE);
			params.add(correos);
			
			Sentencia sentencia = obtenerSentencia(conf,ConstantesWebPrepago.QUERY_GUARDAR_PARAMETROS_TAREA);
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf.getDataSource());
			logger.info(GestorBD.class.getName(), "guardarParametrosTarea","Conexion exitosa a base de datos");
			ManejadorDeDatosDB.actualizarDB(conexion, sentencia.getQuery(),params, sentencia.getTimeout());
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "guardarParametrosTarea", "Error al consultar la configuracion", e);
			throw new ExceptionWebPrepago("Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);

		}
	}
  
  public static void registrarBolsillo(Bolsillo bolsi)throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para registrarBolsillo");
			}
			List<Object> params = new ArrayList<Object>();

			params.add(bolsi.getId());
			params.add(bolsi.getNombre());

			Sentencia sentencia = obtenerSentencia(conf,ConstantesWebPrepago.QUERY_REGISTRAR_BOLSILLO);
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf.getDataSource());
			logger.info(GestorBD.class.getName(), "registrarBolsillo","Consultando existencia de usuario");
			ManejadorDeDatosDB.actualizarDB(conexion, sentencia.getQuery(),params, sentencia.getTimeout());

		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "registrarBolsillo", "Error al consultar la configuracion", e);
			throw new ExceptionWebPrepago("Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);

		}
	}
  
  public static void modificarBolsillo(Bolsillo bol, String id)
			throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion = null;
		ResultSet rst = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago("Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para modificarBolsillo");
			}

			Sentencia sentencia = obtenerSentencia(conf,ConstantesWebPrepago.QUERY_MODIFICAR_BOLSILLO);
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf.getDataSource());
			List<Object> params = new ArrayList<Object>();
			params.add(bol.getId());
			params.add(bol.getNombre());
			params.add(id);

			logger.info(GestorBD.class.getName(), "modificarBolsillo","Obteniendo los bolsillos");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), params, sentencia.getTimeout());

		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "modificarBolsillo","Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "modificarBolsillo","Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago("Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
			try {
				// si el resulset no es nulo cierra la conexion
				if (rst != null) {
					rst.close();
				}
			} catch (SQLException e) {
				logger.error(GestorBD.class.getName(), "modificarBolsillo","Error al cerrar recursos de BD", e);
				throw new ExceptionWebPrepago("Error al cerrar recursos de BD",e);
			}

		}

	}
  
  
  	public static List<BinXBolsillo> getBinXBolsillos() throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion = null;
		BinXBolsillo binXBolsillo;
		ResultSet rst = null;
		List<BinXBolsillo> binXbolsillos = new ArrayList<BinXBolsillo>();

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago("Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para getBinXBolsillos");
			}

			Sentencia sentencia = obtenerSentencia(conf,ConstantesWebPrepago.QUERY_OBTENER_BIN_BOLSILLO);
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf.getDataSource());

			logger.info(GestorBD.class.getName(), "getBinXBolsillos","Obteniendo bin-bolsillos");
			rst = ManejadorDeDatosDB.consultarBD(conexion,sentencia.getQuery(), sentencia.getTimeout());
			// Recorre los resultados de las busquedas
			while (rst.next()) {
				binXBolsillo = new BinXBolsillo();
				binXBolsillo.setBin(Long.toString(rst.getLong("PARAM_BIN_NUMERO")));
				binXBolsillo.setCodigoBolsillo(Integer.parseInt(rst.getString("ID_TIPO_BOLSILLO")));
				binXBolsillo.setEntidad(rst.getString("ENT_DESCRIPCION"));
				binXBolsillo.setNombreBolsillo(rst.getString("TIP_DESCRIPCION"));
				binXBolsillo.setBinID(rst.getInt("PARAM_BIN_ID"));
				
				binXbolsillos.add(binXBolsillo);
				logger.info(GestorBD.class.getName(), "getBinXBolsillos","Agregando bin-bolsillo, total: " + binXbolsillos.size());
			}
		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "getBinXBolsillos","Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago("Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "getBinXBolsillos","Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago("Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
			try {
				// si el resulset no es nullo cierra la conexion
				if (rst != null) {
					rst.close();
				}
			} catch (SQLException e) {
				logger.error(GestorBD.class.getName(), "getBinXBolsillos","Error al cerrar recursos de BD", e);
				throw new ExceptionWebPrepago("Error al cerrar recursos de BD",e);
			}

		}
		return binXbolsillos;
	}
  	
  	public static void registrarBinXBolsillo(BinXBolsillo xbolsillo)throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para registrarBinXBolsillo");
			}
			List<Object> params = new ArrayList<Object>();

			params.add(xbolsillo.getBinID());
			params.add(xbolsillo.getCodigoBolsillo());

			Sentencia sentencia = obtenerSentencia(conf,ConstantesWebPrepago.QUERY_REGISTRAR_BIN_BOLSILLO);
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf.getDataSource());
			logger.info(GestorBD.class.getName(), "registrarBinXBolsillo","Consultando existencia de usuario");
			ManejadorDeDatosDB.actualizarDB(conexion, sentencia.getQuery(),params, sentencia.getTimeout());

		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "registrarBinXBolsillo", "Error al consultar la configuracion", e);
			throw new ExceptionWebPrepago("Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);

		}
	}
  	
  	public static void modificarBinXBolsillo(BinXBolsillo bol, int id, int idBolsillo)
			throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion = null;
		ResultSet rst = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago("Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para modificarBinXBolsillo");
			}

			Sentencia sentencia = obtenerSentencia(conf,ConstantesWebPrepago.QUERY_MODIFICAR_BIN_BOLSILLO);
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf.getDataSource());
			List<Object> params = new ArrayList<Object>();
			params.add(bol.getBinID());
			params.add(bol.getCodigoBolsillo());
			params.add(id);
			params.add(idBolsillo);

			logger.info(GestorBD.class.getName(), "modificarBinXBolsillo","Obteniendo los bin X bolsillos");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), params, sentencia.getTimeout());

		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "modificarBinXBolsillo","Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "modificarBinXBolsillo","Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago("Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
			try {
				// si el resulset no es nulo cierra la conexion
				if (rst != null) {
					rst.close();
				}
			} catch (SQLException e) {
				logger.error(GestorBD.class.getName(), "modificarBinXBolsillo","Error al cerrar recursos de BD", e);
				throw new ExceptionWebPrepago("Error al cerrar recursos de BD",e);
			}

		}

	}
  	
  	public static void eliminarBinXBolsillo(BinXBolsillo bol)
			throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion = null;
		ResultSet rst = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago("Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para eliminarBinXBolsillo");
			}

			Sentencia sentencia = obtenerSentencia(conf,ConstantesWebPrepago.QUERY_ELIMINAR_BIN_BOLSILLO);
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf.getDataSource());
			List<Object> params = new ArrayList<Object>();
			params.add(bol.getBinID());
			

			logger.info(GestorBD.class.getName(), "eliminarBinXBolsillo","Obteniendo los bin X bolsillos");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), params, sentencia.getTimeout());

		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "eliminarBinXBolsillo","Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "eliminarBinXBolsillo","Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago("Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
			try {
				// si el resulset no es nulo cierra la conexion
				if (rst != null) {
					rst.close();
				}
			} catch (SQLException e) {
				logger.error(GestorBD.class.getName(), "eliminarBinXBolsillo","Error al cerrar recursos de BD", e);
				throw new ExceptionWebPrepago("Error al cerrar recursos de BD",e);
			}

		}

	}
  	
  	public static void eliminarBolsillo(Bolsillo bol)
			throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion = null;
		ResultSet rst = null;

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago("Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para eliminarBolsillo");
			}

			Sentencia sentencia = obtenerSentencia(conf,ConstantesWebPrepago.QUERY_ELIMINAR_BOLSILLO);
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf.getDataSource());
			List<Object> params = new ArrayList<Object>();
			params.add(bol.getId());
			

			logger.info(GestorBD.class.getName(), "eliminarBolsillo","Obteniendo los bolsillos");
			rst = ManejadorDeDatosDB.consultarBD(conexion,
					sentencia.getQuery(), params, sentencia.getTimeout());

		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "eliminarBolsillo","Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "eliminarBolsillo","Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago("Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
			try {
				// si el resulset no es nulo cierra la conexion
				if (rst != null) {
					rst.close();
				}
			} catch (SQLException e) {
				logger.error(GestorBD.class.getName(), "eliminarBolsillo","Error al cerrar recursos de BD", e);
				throw new ExceptionWebPrepago("Error al cerrar recursos de BD",e);
			}

		}

	}

  	public static List<UsuarioInactivo> getUsuariosInactivosTarea(Integer inactividad)
			throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion = null;
		UsuarioInactivo usuarioInactivo;
		ResultSet rst = null;
		Sentencia sentencia;
		List<Object> params = new ArrayList<Object>();
		List<UsuarioInactivo> usuariosInactivos = new ArrayList<UsuarioInactivo>();

		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getDataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para getUsuariosInactivosTarea");
			}
				params.add(inactividad);
				sentencia = obtenerSentencia(conf,ConstantesWebPrepago.QUERY_OBTENER_USR_INACTIVOS_DIAS_TAREA);
				conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf.getDataSource());

				logger.info(GestorBD.class.getName(), "getUsuariosInactivosTarea","Obteniendo usuarios inactivos");
				rst = ManejadorDeDatosDB.consultarBD(conexion,sentencia.getQuery(),params, sentencia.getTimeout());
			
			// Recorre los resultados de las busquedas
			while (rst.next()) {
				usuarioInactivo = new UsuarioInactivo();
				usuarioInactivo.setUsuario(rst.getString("USR_DOCUMENTO"));
				String fecha = new SimpleDateFormat("dd/MM/yyyy").format(rst.getTimestamp("USR_FECHACREACION"));
				usuarioInactivo.setFechaCreacion(fecha);
				String fechaUlt = new SimpleDateFormat("dd/MM/yyyy").format(new Date(rst.getDate("USR_ULTIMACONEXION").getTime()));
				usuarioInactivo.setFechaUltimaConexion(fechaUlt);
				usuarioInactivo.setTiempoInactividad(rst.getString("DIAS_INACTIVIDAD"));
				usuarioInactivo.setFechaNotificacion(rst.getString("USR_FECHANOTIFICACION"));
				//logger.info(GestorBD.class.getName(), "getBines",
				//		"Agregando usuario: " + usuarioInactivo.getUsuario());
				usuarioInactivo.setId(rst.getInt("ID_USUARIO"));
				usuarioInactivo.setCorreo(rst.getString("USR_CORREO"));
				usuarioInactivo.setNombres(rst.getString("USR_NOMBRE")+" "+rst.getString("USR_APELLIDO"));

				usuariosInactivos.add(usuarioInactivo);

			}
			logger.info(GestorBD.class.getName(), "getUsuariosInactivosTarea", "Se encontraron "+usuariosInactivos.size()+" usuarios");
		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "getUsuariosInactivosTarea","Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago("Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "getUsuariosInactivosTarea","Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago("Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
			try {
				// si el resulset no es nullo cierra la conexion
				if (rst != null) {
					rst.close();
				}
			} catch (SQLException e) {
				logger.error(GestorBD.class.getName(), "getUsuariosInactivosTarea","Error al cerrar recursos de BD", e);
				throw new ExceptionWebPrepago("Error al cerrar recursos de BD",e);
			}

		}
		return usuariosInactivos;
	}
  	
  	
  	/**
	 * Consulta el saldo de una tarjeta
	 * 
	 * @param numTarjeta
	 *            - numero tarjeta a consultar
	 * @return Saldo de tarjeta
	 * @throws ExceptionWebPrepago
  	 * @throws SQLException 
	 */
	public static List<SaldoXBolsillo> consultarSaldoTarjetaMultiBolsilloBPO(String numTarjeta,
			Connection conexion) throws ExceptionWebPrepago, SQLException {
		Configuracion conf = null;
		List<SaldoXBolsillo> listaBolsillos = new ArrayList<SaldoXBolsillo>();
		ResultSet rst = null;
		CallableStatement callableStatement = null;
		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getBpodataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para consultarSaldoTarjetaBPO");
			}

			// Consulta
			// BPO**************************************************************
			
			String tarjetaFormatead = String.format("%-19s", numTarjeta); //espacios derecha
			String filler = "";
			filler = String.format("%1$-4044s", filler);

			String filler2 = "";
			filler2 = String.format("%1$-4064s", filler2);
			String param1 = "8" + tarjetaFormatead + filler;
			
			String query = "{? = CALL TDPGMSV5R.TDCWPB1('8" + tarjetaFormatead
					+ filler + "','" + filler2 + "') }";

			Sentencia sentencia = obtenerSentencia(conf, ConstantesWebPrepago.QUERY_LLAMADO_BPO_PCI);
			callableStatement = conexion.prepareCall(
					//"CALL TDPGMSV5R.TDCWPB1(?,?)", ResultSet.TYPE_FORWARD_ONLY,
					sentencia.getQuery(), ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			logger.info(GestorBD.class.getName(), "consultarSaldoTarjetaBPO","Obteniendo consultarSaldoTarjetaBPO");
			logger.debug(GestorBD.class.getName(), "consultarExisteBinBPO",
					"Se ejecuta el programa : " + sentencia.getQuery());
			callableStatement.setString(1, param1);
			callableStatement.registerOutParameter(2, Types.VARCHAR);
			callableStatement.execute();
			
			// Boolean existeBin = false;

			String resultado = "";
			String respuesta = "";
			 
			resultado = callableStatement.getString(2);
			logger.debug(GestorBD.class.getName(), "consultarExisteBinBPO",
					"Respuesta BPO : " + resultado);
			respuesta = resultado.substring(92, 97);
			if (respuesta.equals(ConstantesWebPrepago.RESPUESTA_BPO)) {
				//String entero = resultado.substring(20, 30);
				//String decimal =resultado.substring(30,32);
				//String completo= entero + "." + decimal;
				Integer nBolsillos = Integer.parseInt(resultado.substring(20, 22));
				Integer inicial = 22;
				Integer fin = 34;
				for(int i=0; i<nBolsillos; i++) {
										
					SaldoXBolsillo Bolsillo = new SaldoXBolsillo();
					Bolsillo.setBolsillo(resultado.substring(inicial, inicial+2));
					String entero = resultado.substring(inicial+2, fin);
					String decimal =resultado.substring(fin,fin+2);
					String completo= entero + "." + decimal;
					Bolsillo.setSaldoBolsillo(Double.parseDouble(completo));
					listaBolsillos.add(i, Bolsillo);
					inicial=fin+2;
					fin=fin+14;
				}
				logger.debug(GestorBD.class.getName(), "consultarSaldoTarjetaBPO",	"Exitosa");
				return listaBolsillos;
			} else if (respuesta.equals(ConstantesWebPrepago.RESPUESTA_BPO1)) {
				logger.debug(GestorBD.class.getName(), "consultarSaldoTarjetaBPO", "No existe registro coincidente");
				throw new ExceptionWebPrepago("No existe registro coincidente");
			} else if (respuesta.equals(ConstantesWebPrepago.RESPUESTA_BPO2)) {
				logger.debug(GestorBD.class.getName(), "consultarSaldoTarjetaBPO", "No hay conexion con base de datos BPO");
				throw new ExceptionWebPrepago("No hay conexión con base de datos BPO");
			}	

		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "consultarSaldoTarjetaBPO",
					"Error al consultar la base de datos: ", e);
			throw new SQLException(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "consultarSaldoTarjetaBPO",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			// la conexion la cierra el que llama el metodo
			try {
				// si el resulset no es nulo cierra la conexion
				if (callableStatement != null) {
					callableStatement.close();
				}
			} catch (SQLException e) {
				logger.error(GestorBD.class.getName(),
						"consultarSaldoTarjetaBPO",
						"Error al cerrar recursos de BD", e);
				throw new ExceptionWebPrepago("Error al cerrar recursos de BD",
						e);
			}

		}
		return null;

	}
	/**
	 * Consulta los movimientos de una tarjeta
	 * 
	 * @param numTarjeta
	 *            - numero tarjeta a consultar
	 * @param fechaIni
	 *            - Fecha Inicio
	 * @param fechaFin
	 *            - Fecha Fin
	 * @return Lista de movimientos
	 * @throws ExceptionWebPrepago
	 */
	public static List<RegistroMovimiento> consultarMovimientosMultibolsilloBPO(String numTarjeta, String fechaIni, String fechaFin, String bolsillo)
			throws ExceptionWebPrepago {
		Configuracion conf = null;
		Connection conexion = null;
		ResultSet rst = null;
		List<RegistroMovimiento> movimientos = new ArrayList<RegistroMovimiento>();
		RegistroMovimiento movimiento;
		SimpleDateFormat sdfBPO = new SimpleDateFormat(ConstantesWebPrepago.FORMATO_HORA_BPO);
		SimpleDateFormat sdfWeb = new SimpleDateFormat(ConstantesWebPrepago.FORMATO_HORA_WEB);
		Date hora = null;
		CallableStatement callableStatement = null;

		String textoFormateado = String.format("%-19s", numTarjeta); //espacios derecha
 
		try {
			conf = GestorConfiguracionLDAP.obtenerConfiguracion();
			// Valida que no sea nulos para poder ejecutar el query
			if (conf.getQueries() == null || conf.getBpodataSource() == null) {
				throw new ExceptionWebPrepago(
						"Error al leer la configuración del archivo campos conf, queries y/o dataSource en null, para consultarMovimientosBPO");
			}
			
			// Consulta
			// BPO**************************************************************
			String filler = "";
			filler = String.format("%1$-4026s", filler);

			String filler2 = "";
			filler2 = String.format("%1$-4064s", filler2);
			conexion = ManejadorDeDatosDB.obtenerConexionPorDatasource(conf.getBpodataSource());
			String param1 = "3" + textoFormateado + fechaFin + fechaIni + filler; 
			String query = "{? = CALL TDPGMSV5R.TDCWPB1('3" + textoFormateado + "10"+ fechaIni + fechaFin + filler +"','"+filler2 +"') }";
			Sentencia sentencia = obtenerSentencia(conf, ConstantesWebPrepago.QUERY_LLAMADO_BPO_PCI);
			logger.debug(GestorBD.class.getName(), "consultarMovimientosBPO","Se ejecuta el programa : " + query);
			//callableStatement = conexion.prepareCall("CALL TDPGMSV5R.TDCWPB1(?,?)", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			callableStatement = conexion.prepareCall(sentencia.getQuery(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			callableStatement.setString (1, param1); 
			callableStatement.registerOutParameter (2, Types.VARCHAR);
			callableStatement.execute();
			
		
			String resultado = "";
			String respuesta = "";
			resultado= callableStatement.getString(2);
			logger.debug(GestorBD.class.getName(), "consultarMovimientosTarjetaBPO",	"RESULTADO****1"+resultado);

			respuesta = resultado.substring(4029,4034);
			
			if (respuesta.equals(ConstantesWebPrepago.RESPUESTA_BPO)){
				logger.debug(GestorBD.class.getName(), "consultarMovimientosBPO",	"Exitosa");
				String resultado2 = resultado.substring(36,4029); 
				String[] partes = resultado2.split(";");
				for (int i = 0; i < partes.length; i++){
					String parte = partes[i]; 
//					se recorre el arreglo de respuestas movimientos mientras no sea blanco. 
					if(!parte.substring(0,1).contains(" ")){
						movimiento = new RegistroMovimiento();
						String fecha = parte.substring(0,8);
						movimiento.setFechaTransaccion(fecha.substring(6) + "/"
								+ fecha.substring(4, 6) + "/" + fecha.substring(0, 4));
//						if(!parte.substring(8,10).contains(" ")){}
							hora = sdfBPO.parse(parte.substring(8,14));
							movimiento.setHora(sdfWeb.format(hora));

						
						movimiento.setUbicacion(parte.substring(14,44));
						movimiento.setTipoTransaccion(parte.substring(44,74));
						String entero = parte.substring(74,84);
						String decimal =parte.substring(84,86);
						String completo= entero + "." + decimal;

						String formateado=ConstantesWebPrepago.FORMATO_VALORES.format(Double.parseDouble(completo));
						movimiento.setValorTransaccion(formateado);
						movimiento.setRespTransaccion(parte.substring(86,96));
						
						String Comientero = parte.substring(96,107);
						String Comidecimal =parte.substring(107,109);
						String Comicompleto= Comientero + "." + Comidecimal;
						String formateadoComi=ConstantesWebPrepago.FORMATO_VALORES.format(Double.parseDouble(Comicompleto));
						movimiento.setValorComision(formateadoComi);
						String Impentero = parte.substring(109,117);
						String Impdecimal =parte.substring(117,119);
						String Impcompleto= Impentero + "." + Impdecimal;
						String formateadoImp=ConstantesWebPrepago.FORMATO_VALORES.format(Double.parseDouble(Impcompleto));
						movimiento.setValorImpuesto(formateadoImp);
						movimientos.add(movimiento);	
					}
				}
			} else if (respuesta.equals(ConstantesWebPrepago.RESPUESTA_BPO1)) {
				logger.debug(GestorBD.class.getName(), "consultarMovimientosBPO", "No existe registro coincidente");
				throw new ExceptionWebPrepago("No existe registro coincidente");
			} else if (respuesta.equals(ConstantesWebPrepago.RESPUESTA_BPO2)) {
				logger.debug(GestorBD.class.getName(), "consultarMovimientosBPO", "No hay conexion con base de datos BPO");
				throw new ExceptionWebPrepago("No hay conexión con base de datos BPO");
			}
			// ********************************	
			return movimientos;
		} catch (SQLException e) {
			logger.error(GestorBD.class.getName(), "consultarMovimientosBPO",
					"Error al consultar la base de datos: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la base de datos: ", e);
		} catch (Exception e) {
			logger.error(GestorBD.class.getName(), "consultarMovimientosBPO",
					"Error al consultar la configuracion: ", e);
			throw new ExceptionWebPrepago(
					"Error al consultar la configuracion", e);
		} finally {
			ManejadorDeDatosDB.cerrarConexion(conexion);
			try {
				// si el resulset no es nulo cierra la conexion
				if (callableStatement != null) {
					callableStatement.close();
				}
			} catch (SQLException e) {
				logger.error(GestorBD.class.getName(),
						"consultarMovimientosBPO",
						"Error al cerrar recursos de BD", e);
				throw new ExceptionWebPrepago("Error al cerrar recursos de BD",
						e);
			}

		}

	}

  	
}
