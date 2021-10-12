BEGIN
  DECLARE
    INTERVALO VARCHAR2(100 BYTE);
    job_doesnt_exist EXCEPTION;
   	PRAGMA EXCEPTION_INIT( job_doesnt_exist, -27475 );
  BEGIN
	begin
	   dbms_scheduler.drop_job(job_name => 'eliminar_usuarios_temporales');
	exception when job_doesnt_exist then
	   null;
	end;
    SELECT VALOR INTO INTERVALO FROM WPR_PARAMETROS WHERE NOMBRE='FRECUENCIA_ELIMINACION_USUARIO_TEMPORAL';
    DBMS_SCHEDULER.CREATE_JOB (
     job_name        => 'eliminar_usuarios_temporales',
     job_type        => 'PLSQL_BLOCK',
     job_action      => 'BEGIN 
                          DELETE
                            FROM WPR_USUARIO 
                              WHERE USR_TIPOUSUARIO=2 
                                AND sysdate+(SYSDATE-USR_FECHACREACION)>=(SELECT sysdate+TO_NUMBER(VALOR)/24
                                                                              FROM WPR_PARAMETROS 
                                                                              WHERE NOMBRE='||chr(39)||'HORAS_PERMANENCIA_REGISTRO_TEMPORAL'||chr(39)||'); 
                         END;',
     start_date      => sysdate,
     repeat_interval => 'FREQ=HOURLY;interval='||INTERVALO,
     enabled         => TRUE);
  END;
END;
/