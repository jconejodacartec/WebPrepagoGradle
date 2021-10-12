package com.rbm.web.prepago.util;

import java.io.File;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

public interface ConstantesWebPrepago {
	
	/** Constante con el mensaje de error general */
	public static final String MSG_ERR_GENERAL = "Estamos presentando problemas técnicos, por favor consulte con el Administrador del Sistema.";
	
	/** Variable de entorno de configuracion */
	public static final String CONFIGURACION_DIR = "SWF_PORTAL_CONFIGPATH";

	/** Nombre de la aplicacion web prepago */
	public static final String NOMBRE_APLICACION = "webPrepago";

	/** Directorio de configuracion web prepago */
	public static final String DIRECTORIO_CONFIGURACION = "webPrepago";

	/** Directorio de configuracion del LDAP */
	public static final String CONFIGURACION_LDAP = "ldap";
	
	/** Constante con la ruta de configuracion de las imagenes */
	public static final String IMAGENES_DIR = System.getProperty(CONFIGURACION_DIR) + DIRECTORIO_CONFIGURACION + File.separatorChar + "imagenes" + File.separatorChar;


	/** Nombre del archivo de configuracion de WEBPREPAGO. */
	public static final String CONFIGURACION_ARCHIVO_WEB_PREPAGO = "WebPrepago_Conf.xml";


	public static final String PATH_NEW_USER_PERSONAS = "cn=usuarios,ou=personas,DC=REDEBAN,DC=COM";

	/** Respuesta BPO */
	public static final String RESPUESTA_BPO = "00000";
	
	public static final String RESPUESTA_BPO1 = "00001";
	
	public static final String RESPUESTA_BPO2 = "00002";

	/** Ruta del archivo de configuracion del LDAP */
	public static final String PATH_CONF_LDAP = System.getProperty(CONFIGURACION_DIR) + CONFIGURACION_LDAP +  File.separatorChar + "ldapConfig.xml";

	/** Ruta del archivo de acceso a usuarios del LDAP */
	public static final String PATH_MAPPING_LDAP = System.getProperty(CONFIGURACION_DIR) + CONFIGURACION_LDAP +  File.separatorChar + "ldapMapping.xml";


	/** Variables para Registrar Usuario */
	public static final String REGISTRAR_USUARIO_ME003 = "No se ha ingresado información en uno o más campos obligatorios.";
	public static final String REGISTRAR_USUARIO_ME003_1 = "Campo Nombres y/o Apellidos no puede ser menor a dos caracteres.";
	public static final String REGISTRAR_USUARIO_ME003_2 = "Formato de correo invalido ";
	public static final String REGISTRAR_USUARIO_ME003_3 = "Formato de teléfono movil invalido ";
	public static final String REGISTRAR_USUARIO_ME003_6 = "Formato de documento invalido ";

	public static final String REGISTRAR_USUARIO_ME004 = "El usuario ya existe en el sistema.";

	public static final String REGISTRAR_USUARIO_ME005 = "La confirmación del correo electrónico no coincide.";
	public static final String REGISTRAR_USUARIO_ME008 = "La longitud máxima del tipo documento %s no está parametrizada";
	public static final String MSG_ERROR_CAPTCHA = "La información del captcha no es correcta.";


	/** Variables para Asignar contrasena */


	public static final String ASIGNAR_CONTRASENA_ME002 = "La contraseña ingresada no cumple con la estructura requerida.";

	public static final String ASIGNAR_CONTRASENA_ME003 = "No se ha ingresado información en uno o más campos obligatorios.";

	public static final String ASIGNAR_CONTRASENA_ME004 = "El usuario ingresado no existe en el sistema.";

	public static final String USUARIO_EXISTE_EN_LDAP = "Usuario ya registrado. Para restaurar su contraseña ingrese a https://www.rbmcolombia.com opción  ¿Olvidó su contraseña?";

	public static final String ASIGNAR_CONTRASENA_ME007 = "Estamos presentando problemas técnicos, por favor contacte al Administrador del Sistema";

	public static final String ASIGNAR_CONTRASENA_ME001 = "Las contraseñas no coinciden.";

	public static final String ASIGNAR_CONTRASENA_MI001 = "Se ha creado la contraseña con éxito.";

	public static final String ASIGNAR_CONTRASENA_MI002 = "El enlace para confirmar el registro ha caducado, por favor vuelva a realizar el proceso de registro en el sistema.";

	public static final String ASIGNAR_CONTRASENA_MI003 ="Se ha realizado con éxito el cambio de contraseña.";

	public static final String ASIGNAR_CONTRASENA_ME005 = "No se logró verificar su identidad, puede llamar a la Línea de Atención a clientes Nacional CAC a los números 01 8000 122 363, 01 8000 912 363 y 01 8000 912 912.";

	public static final String ASIGNAR_CONTRASENA_ME006 = "Valor no permitido para el campo.";


	/** Variables para Asignar contrasena */

	public static final String ADMINISTRAR_TARJETAS_ME007 = "Estamos presentando problemas técnicos, por favor contacte al Administrador del Sistema";

	public static final String ADMINISTRAR_TARJETAS_MI001 = "No hay tarjetas inscritas con este usuario en el sistema.";

	public static final String ADMINISTRAR_TARJETAS_MI003 = "Se eliminó la inscripción de la tarjeta con éxito.";
	
	public static final String ADMINISTRAR_TARJETAS_MI004 = "Número de tarjeta ingresado no existe.";

	public static final String ADMINISTRAR_TARJETAS_MI005 = "No puede inscribir esta tarjeta, se ha alcanzado el número máximo de tarjetas permitidas por usuario.";

	public static final String ADMINISTRAR_TARJETAS_ME001 = "El número de tarjeta ya se encuentra registrado.";

	public static final String ADMINISTRAR_TARJETAS_MI002 = "Se inscribió la tarjeta con éxito.";

	public static final String ADMINISTRAR_TARJETAS_MI006 = "Se actualizó la información con éxito.";

	public static final String ADMINISTRAR_TARJETAS_ME003 = "No se ha ingresado información en uno o más campos obligatorios.";
	
	
	/** Variables para Asignar contrasena */
	
	public static final String ADMINISTRAR_PARAMETROS_ME002 = "Estamos presentando problemas técnicos, por favor contacte al Administrador del Sistema";
	
	public static final String ADMINISTRAR_PARAMETROS_ME004 = "No se ha ingresado información en uno o más campos obligatorios.";
	
	public static final String ADMINISTRAR_PARAMETROS_ME001 = "Este parámetro ya se encuentra creado";
	
	public static final String ADMINISTRAR_PARAMETROS_MI001 = "Se guardaron los cambios exitosamente";
	
	public static final String ADMINISTRAR_PARAMETROS_MW002 = "No existe información a presentar para la sección de consulta.";
	
	
	/**Variables para Modificar información usuario*/
	
	public static final String MODIFICAR_INFO_USR_ME001 = "Error cargando información en el formulario";
	public static final String MODIFICAR_INFO_USR_ME002 = "Error modificando la información del usuario.";
	
	
	public static final String DEPURAR_USUARIOS_MI002 = "Se eliminó la tarjeta con éxito.";

	/*CONTANTES DE BD*/

	public static final String QUERY_INSERT_USUARIO = "";

	public static final String QUERY_USUARIO_PERMANENTE= "";

	public static final String QUERY_CONSULTAR_TARJETAS_X_USUARIO = "consultarTarjetasXUsuario";

	public static final String QUERY_CONSULTAR_TARJETAS_USUARIO ="consultarTarjetasUsuario";

	public static final String QUERY_ELIMINAR_TARJETA ="EliminarTarjeta";

	public static final String QUERY_EXISTE_TARJETA ="ExisteTarjeta";

	public static final String QUERY_ENTIDAD_TIPOTAR ="EntidadTipoTarjeta";

	public static final String QUERY_REGISTRAR_TARJETAUSUARIO ="RegistrarTarjetaUsuario";
	
	public static final String QUERY_MODIFICAR_TARJETAUSUARIO ="ModificarTarjetaUsuario";
	
	public static final String QUERY_LLAMADO_BPO_PCI ="llamadoBPOPCI";

	/*CONTANTES DE CACHE*/

	/** Nombre del cache con que se guarda la inforacion de catalogos CACHE_TIPO_DOCUMENTO. */
	public static final String CACHE_TIPO_DOCUMENTO = "TipoDocumento";

	/** Nombre del cache con que se guarda la inforacion de catalogos CACHE_PREGUNTAS. */
	public static final String CACHE_PREGUNTAS = "Preguntas";
	
	/** Nombre del cache con que se guarda la inforacion de catalogos CACHE_PARAMETROS. */
	public static final String CACHE_PARAMETROS = "Parametros";
	
	/** Nombre del cache con que se guarda la inforacion de catalogos CACHE_ENTIDAD. */
	public static final String CACHE_ENTIDAD = "Entidades";
	
	/** Nombre del cache con que se guarda la inforacion de catalogos CACHE_COD_TRANS. */
	public static final String CACHE_COD_TRANS = "Codigos";
	
	/** Nombre del cache con que se guarda la inforacion de catalogos CACHE_COD_TRANS_BPO. */
	public static final String CACHE_COD_TRANS_BPO = "CodigosTransBPO";
	
	/** Nombre del cache con que se guarda la inforacion de catalogos CACHE_TIPO_TAR. */
	public static final String CACHE_TIPO_TAR = "TipoTarjeta";
	
	/** Nombre del cache con que se guarda la inforacion de catalogos CACHE_ESTADOS. */
	public static final String CACHE_ESTADOS = "Estados";
	
	/** Nombre del cache con que se guarda la inforacion de catalogos CACHE_ESTADOS_BPO. */
	public static final String CACHE_ESTADOS_BPO = "EstadosBPO";
	
	/** Nombre del cache con que se guarda la inforacion de catalogos CACHE_BINES. */
	public static final String CACHE_BINES = "Bines";
	
	/** Nombre del cache con que se guarda la inforacion de catalogos CACHE_BOLSILLO. */
	public static final String CACHE_BOLSILLO = "Bolsillo";
	
	/** Nombre del cache con que se guarda la inforacion de catalogos CACHE_BINES. */
	public static final String CACHE_PARAMETROS_BD = "ParametrosBD";
	
	/** Nombre del cache con que se guarda la inforacion de catalogos CACHE_BIN_X_BOLSILLO. */
	public static final String CACHE_BIN_X_BOLSILLO = "BinXBolsillo";


	/** Constantes para envio de correos */
	public static String PLANTILLA_CORREO_WEBPREPAGO = "correoWebPrepago.vm";
	public static final String PLANTILLA_CORREO_WEBPREPAGO_ASIGNACION_CONTRASENA = "correoWebPrepagoAsignarContrasena.vm";
	public static final String PLANTILLA_CORREO_WEBPREPAGO_NOTIFICAR_INACTIVIDAD = "correoWebPrepagoNotificarInactividad.vm";
	public static final String PLANTILLA_CORREO_WEBPREPAGO_NOTIFICAR_ELIMINACION = "correoWebPrepagoNotificarEliminacion.vm";
	public static final String PLANTILLA_CORREO_WEBPREPAGO_NOTIFICAR_ELIMINAR_USUARIO = "correoWebPrepagoEliminarInfoErr.vm";
	public static final String PLANTILLA_CORREO_MODIFICAR_INFO_PERSONAL = "correoModificarInfo.vm";
	public static final String PLANTILLA_CORREO_WEBPREPAGO_INACTIVIDAD ="correoWebPrepagoInactividad.vm";

	public static final String QUERY_OBTENER_TIPO_DOCUMENTOS = "obtenerTipoDocumento";
	public static final String QUERY_OBTENER_PREGUNTAS = "obtenerPreguntas";
	public static final String QUERY_COSULTAR_USUARIO = "consultarUsuario";
	public static final String QUERY_REGISTRAR_USUARIO = "registrarUsuario";
	public static final String QUERY_COSULTAR_USUARIO_POR_TIPO = "consultarUsuarioPorTipo";
	public static final String QUERY_CAMBIAR_A_PERMANENTE = "AsiganrUsuarioPermanente";
	public static final String QUERY_OBTENER_SECUENCIA_USUARIO = "ObtenerSecuenciaUsuario";
	public static final String QUERY_OBTENER_ENTIDADES = "ObtenerEntidades";
	public static final String QUERY_OBTENER_SECUENCIA_ENTIDAD = "ObtenerSecuenciaEntidad";
	public static final String QUERY_REGISTRAR_ENTIDAD = "RegistrarEntidad";
	public static final String QUERY_MODIFICAR_ENTIDAD = "ModificarEntidad";
	public static final String QUERY_ELIMINAR_ENTIDAD = "EliminarEntidad";
	public static final String QUERY_OBTENER_COD_TRANS = "ObtenerCodTransaccion";
	public static final String QUERY_OBTENER_SECUENCIA_CODIGO_TRAN = "ObtenerSecuenciaCodigoTran";
	public static final String QUERY_REGISTRAR_COD_TRAN = "RegistrarCodigoTran";
	public static final String QUERY_MODIFICAR_CODIGO_TRAN = "ModificarCodigoTran";
	public static final String QUERY_OBTENER_TIPO_TAR = "ObtenerTipoTarjeta";
	public static final String QUERY_OBTENER_SECUENCIA_TIPO_TARJETA = "ObtenerSecuenciaTipoTar";
	public static final String QUERY_REGISTRAR_TIPO_TAR = "RegistrarTipoTarjeta";
	public static final String QUERY_MODIFICAR_TIPO_TARJETA = "ModificarTipoTarjeta";
	public static final String QUERY_OBTENER_PARAM_BINES = "consultarParamBin";
	public static final String QUERY_OBTENER_ESTADOS = "ObtenerEstados";
	public static final String QUERY_OBTENER_SECUENCIA_ESTADO = "ObtenerSecuenciaEstado";
	public static final String QUERY_REGISTRAR_ESTADO = "RegistrarEstado";
	public static final String QUERY_MODIFICAR_ESTADO = "ModificarEstado";
	public static final String QUERY_OBTENER_BINES = "ObtenerBines";
	public static final String QUERY_OBTENER_SECUENCIA_BIN = "ObtenerSecuenciaBin";
	public static final String QUERY_REGISTRAR_BIN = "RegistrarBin";
	public static final String QUERY_MODIFICAR_BIN = "ModificarBin";
	public static final String QUERY_EXISTE_TARJETA_BD= "existeTarjetaBD";
	public static final String QUERY_OBTENER_SECUENCIA_HISTORICO = "secuencialHistorico";
	public static final String QUERY_OBTENER_USR_INACTIVOS = "ObtenerUsuariosInactivos";
	public static final String QUERY_OBTENER_USR_INACTIVOS_DIAS = "ObtenerUsuariosInactivosXDias";
	public static final String QUERY_OBTENER_NOMBRE_IMG = "ObtenerNombreImagen";	
	public static final String QUERY_MODIFICAR_USUARIO_NOTIFICAR = "modificarUsuarioNotificar";
	public static final String QUERY_MODIFICAR_ULTIMA_CONEXION  = "modificarUltimaConexion";
	public static final String QUERY_ELIMINAR_USUARIO_INACTIVO  = "eliminarUsuarioInactivo";
	public static final String QUERY_REGISTRAR_HISTORICO  = "registrarHistorico";
	public static final String QUERY_ELIMINAR_TARJETAS_USUARIO_INACTIVO  = "eliminarUsuarioInactivoTarjetas";
	public static final String QUERY_OBTENER_BOLSILLOS = "ConsultarBolsillos";
	public static final String QUERY_OBTENER_BOLSILLOS_TARJETA = "ConsultarBolsillosbyTarjeta";
	public static final String QUERY_ACTUALIZAR_INFO_PERSONAL = "ActualizarDatosInfoPersonal";
	public static final String QUERY_CONSULTAR_PARAMETROS = "ConsultarParametros";
	public static final String QUERY_GUARDAR_PARAMETROS_TAREA = "GuardarParametrosTarea";
	public static final String QUERY_REGISTRAR_BOLSILLO = "RegistrarBolsillo";
	public static final String QUERY_MODIFICAR_BOLSILLO = "ModificarBolsillo";
	public static final String QUERY_OBTENER_BIN_BOLSILLO = "ConsultarBinBolsillos";
	public static final String QUERY_REGISTRAR_BIN_BOLSILLO = "RegistrarBinXBolsillo";
	public static final String QUERY_MODIFICAR_BIN_BOLSILLO = "ModificarBinXBolsillo";
	public static final String QUERY_ELIMINAR_BIN_BOLSILLO = "EliminarBinXBolsillo";
	public static final String QUERY_ELIMINAR_BOLSILLO = "EliminarBolsillo";
	public static final String QUERY_OBTENER_USR_INACTIVOS_DIAS_TAREA = "ObtenerUsuariosInactivosXDiasTarea";

	/*CONTANTES TIPOS DE USUARIOS*/

	public static final int USUARIO_TEMPORAL = 2;

	public static final int USUARIO_PREMANENTE = 1;

	public static final String RECAPTCHA_RESPONSE = "g-recaptcha-response";

	public static final String OBJETO_TARJETA = "Tarjeta";

	public static final String CONSULTAR_MOVIEMIENTOS = "ConsultarMovimientosView";



	public static final String CONSULTAR_SALDO_MI001 = "No hay tarjetas inscritas en el sistema.";
	
	public static final String CONSULTAR_SALDO_ME003 = "No se ha ingresado información en uno o más campos obligatorios.";
	
	public static final String CONSULTAR_SALDO_ME004 = "El rango de busqueda maximo es de 90 dias.";

	public static final String CONSULTAR_SALDO_ME001 = "Estamos presentando problemas técnicos, por favor contacte al Administrador del Sistema";
	
	public static final String CONSULTAR_SALDO_ME002 = "Error en consulta BPO";
	
	public static final String CONSULTAR_SALDO_ME005 = "No es posible realizar la consulta por falla en Base De Datos";

	public static final String DIRECTORIO_IMAGENES = "imagenes";



	public static final String BEAN_CONSULTAR_MOVIMIENTOS = "pc_ConsultarMovimientosBean";

	public static final String BEAN_CONSULTA_NOVEDAD = "pc_ConsultarSaldoBean";
	
	public static final String BEAN_ADMINISTRAR_PARAMETROS = "pc_AdministrarParametrosWPView";
	
	public static final String BEAN_USUARIOS_INACTIVOS = "pc_EliminarUsuariosInactivos";



	public static final String NAVEGACION = "navegacion";

	public static final String ATTRIBUTO_NOMBRE_BEAN = "nombreBean";

	public static final String DETALLE_NOVEDAD = "DetalleNovedadView";

	public static final String CONSULTA_NOVEDADES = "ConsultarSaldoView";


	// Generacion de reportes
	/** Constante con la ruta de configuracion de los archivos de reportes */
	public static final String REPORTES_CONFIG_PATH = System.getProperty(CONFIGURACION_DIR) + DIRECTORIO_CONFIGURACION + File.separatorChar + "plantillas" + File.separatorChar + "ReportesConfig.xml";

	/** Nombre plantilla reporte respuesta consulta cargue masivo */
	public static final String PLANTILLA_REPORTE_CONSULTAR_MOVIMIENTOS = "plantillaReporteConsultarMovimientos";
	
	public static final String PLANTILLA_REPORTE_CONSULTAR_MOVIMIENTOS_BOLSILLO = "plantillaReporteConsultarMovimientosBolsillo";
	
	public static final String PLANTILLA__REPORTE_USUARIOS_INACTIVOS="plantillaUsuariosInactivos";

	public static final String RESOURCE_ID_EXPORT_REPORTE_CONSULTAR_MOVIMIENTOS_EXCEL = "ReporteExcelMovimientos";
	
	/** Constante con el formato de archivos PDF */
	public static final String FORMATO_ARCHIVO_PDF = ".pdf";
	
	/** Constante con el formato de archivos de excel */
	public static final String FORMATO_ARCHIVO_EXCEL = ".xls";

	/** Constante con el formato de la fecha del archivo */
	public static final String FORMATO_FECHA_ARCHIVO = "ddMMyyyyhhmm";

	/** Tipo de contenido para la generacion de reportes en excel */
	public static final String CONTENT_TYPE_EXCEL = "application/vnd.ms-excel";
	
	/** Tipo de contenido para la descarga de imagen */
	public static final String CONTENT_TYPE_IMAGE ="image/jpg";

	/** Identificador del tipo de contenido para la generacion de reporte */
	public static final String CONTENT_TYPE_KEY = "content-disposition";

	/** Constante con los caracteres para adicionar un adjunto el response junto con el nombre */
	public static final String ATTACHMENT_KEY = "attachment; filename=";
	
	public static final String FORMATO_FECHA_DDMMYYYY = "ddMMyyyy";

	public static final String NOMBRE_REPORTE_MOVIMIENTOS = "MOV_";

	public static final String RESOURCE_ID_EXPORT_REPORTE_NOVEDADES_PDF = "ReportePDFMovimientos";

	/** Constante con el formato de las fechas en excel */
	public static final String FORMATO_FECHA_XLS = "dd/MM/yy";
	
	/** Constante con el formato de las fechas en AS400 */
	public static final String FORMATO_FECHA_BPO = "yyyyMMdd";
	
	/** Constante con el formato de la hora en AS400 */
	public static final String FORMATO_HORA_BPO = "HHmmss";
	
	/** Constante con el formato de la hora en WEB PREPAGO */
	public static final String FORMATO_HORA_WEB = "hh:mm aa";
	
	public static final DecimalFormat FORMATO_VALORES = new DecimalFormat("#,###,###.00");

	public static final String EMISOR = "REDEBAN";
	
	public static final String EXP_VALIDAR_CLAVE="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{8,16}";

	
	public static final String PARAMETRO_UNO="1-Entidad";
	
	
	public static final String PARAMETRO_DOS="2-Código de Transacción";
	
	public static final String PARAMETRO_TRES="3-Tipo de Tarjeta";
	
	public static final String PARAMETRO_CUATRO="4-Estado de Tarjeta";
	
	public static final String PARAMETRO_CINCO="5-Código BIN";
	
	public static final String PARAMETRO_SEIS="6-Eliminación Automática";
	
	public static final String PARAMETRO_SIETE="7-Bolsillo";
	
	public static final String PARAMETRO_OCHO="8-Crear Asociación Bin";


	
	public static final String PATH_SEARCH_GROUP = "cn=Tarjeta Habiente,cn=grupos,ou=personas,DC=REDEBAN,DC=COM";
	
	public static final String PARAM_GESTOR_USUARIOS = "GestorUsuarios";

	public static final String ACCION_HISTORICO_1 = "Eliminar usuario inactivo";

	public static final String FORMATO_EMAIL = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
	
	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = 
		    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
	
	public static final String PARAMETRO_DIAS_NOTIFICAR = "LISTA_DIAS_INACTIVIDAD";
	
	public static final String PARAMETRO_DIAS_ELIMINAR = "LISTA_DIAS_ELIMINACION";
	public static final String PARAMETRO_DIAS_NOTIFICAR_TAREA ="DIAS_INACTIVIDAD_TAREA";
	public static final String PARAMETRO_DIAS_INACTIVIDAD ="DIAS_INACTIVIDAD";

	public static final String PARAMETRO_DIAS_ELIMINAR_TAREA ="DIAS_ELIMINACION_TAREA";
	public static final String PARAMETRO_CORREOS_TAREA ="CORREOS_NOTIFICAR_DEPURACION";
	public static final String PARAMETRO_HORA_EJECUCION_TAREA ="HORA_EJECUCION_TAREA_DEPURAR";

	public static final String JOB_NAME_PROCESO_DEPURAR_USUARIOS = "IniciarProcesoDepurarUsariosJob";

	public static final String GROUP_NAME_PROCESO_DEPURAR_USUARIOS = "GrupoProcesoDepurarUsuarios";

	public static final String TRIGGER_NAME_PROCESO_DEPURAR_USUARIOS = "IniciarProcesoDepurarUsarios";
}
