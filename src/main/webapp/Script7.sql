SELECT TMHTAR FROM TDARCHV3P.TDMH;
SELECT * FROM TDARCHV3P.TDMH WHERE TMHTAR = '1234';

SELECT APBCDS + APBSBG - (ASPAUT + APBAUT + APBSMI) as saldo FROM AUARCHV4P.AUPBF WHERE apbpan='5332950000000012';

SELECT CSCPAN FROM AUARCHV4P.aucaf where asbpan='5332950000000012';

SELECT * FROM TDARCHV3P.TDNI;
SELECT * FROM TDARCHV3P.TDST;
SELECT * FROM TDARCHV3P.TDNS;
SELECT * FROM TDARCHV3P.TDBI   ;
SELECT * FROM AUARCHV4.AUTRA;
SELECT * FROM PEREDE.XTCD;
SELECT * FROM TDARCHV3P.TDES   ;
SELECT * FROM AUARCHV4P.AUCAF ;
SELECT * FROM AUARCHV4P.AUPBF  ;
SELECT * FROM TDARCHV3P.TDMH ; 
--historico tdarchv3p.tdnv;
SELECT * FROM AUARCHV4P.AUMV where AMV002='12';
SELECT * FROM TDARCHV3P.TDCO  ; 

--e.	Codigo y descripcion de estados de tarjetas.
SELECT TESDES AS NOMBRE_ESTADO FROM TDARCHV3P.TDES WHERE TESCOD = ?;
SELECT AETEST, AETDES FROM AUARCHV4.AUET;

--estado de una tarjeta especifica
SELECT TESCOA, TESDES FROM AUARCHV4P.AUCAF INNER JOIN TDARCHV3P.TDES ON (TESCOA=ASBEST) WHERE ASBPAN='5332950000000012';

SELECT AETDES AS ESTADO FROM AUARCHV4P.AUCAF INNER JOIN AUARCHV4.AUET ON AETEST=ASBEST WHERE ASBPAN = '5332950000000012';

--c.	Codigos y descripcion de las transacciones.
SELECT TRATRT, TRADES AS NOMBRE_TRANSACCION FROM AUARCHV4.AUTRA order by 1 asc WHERE TRATRT = ?;


-- Consulta de existencia de la tarjeta:
SELECT * FROM auarchv4p.aucaf where asbpan='5332950000000012';

--2. Consulta de saldo de la tarjeta:
SELECT apbpan,apbcds FROM auarchv4p.AUPBF WHERE apbpan='5332950000000012';

--3. Consulta de movimiento de la tarjeta:
SELECT
substr(AMVFTR,1,4) || '-' || substr(AMVFTR,5,2) || '-' concat substr(AMVFTR,7,2) FECHATRAN, yyyymmdd
AMV012 HORA, hhmmss en 24h
substr(AMV043, 1, 22) || ' ' || substr(AMV043, 30, 6) UBICACION,
AMVTTR TIPOTRAN,
AMVVLC VLRCOMISION,
AMVVIE CUATROXMIL,
AMVTAR TARJETA,
case AMV039 when '00' then 'APROBADA' else 'DECLINADA' end DESCRIPCION_RTA,
case AMV039 when '00' then AMV004/100 else 0 end VALORTX
FROM auarchv4p.AUMV
WHERE amvtar ='5116140000000061'
AND AMVTTR IN ('','0','1','3','4','6','8','10','11','12','20','61') realizar inner para buscar la descripcion de transaccion
AND amvinr in (' ','2')
AND AMVMSG ='0200' se elimina
AND (substr(AMVFTR,1,4) || '-' || substr(AMVFTR,5,2) || '-' concat substr(AMVFTR,7,2)) >= date(now()) - 90 days
order by 1 desc,2 desc,3 desc;

SELECT
substr(AMHFTR,1,4) || '-' || substr(AMHFTR,5,2) || '-' concat substr(AMHFTR,7,2) FECHATRAN,
AMH012 HORA,
substr(AMH043, 1, 22) || ' ' || substr(AMH043, 30, 6) UBICACION,
AMHTTR TIPOTRAN,
AMHVLC VLRCOMISION,
AMHVIE CUATROXMIL,
AMHTAR TARJETA,
AMH039 RTAAUT,
case AMH039 when '00' then 'APROBADA' else 'DECLINADA' end DESCRIPCION_RTA,
case AMH039 when '00' then AMH004/100 else 0 end VALORTX
FROM auarchv4p.aumvh
WHERE amhtar ='5116140000000061'
AND AMHTTR IN ('','0','1','3','4','6','8','10','11','12','20','61')
AND AMHINR in (' ','2')
AND AMHMSG ='0200'
AND (substr(AMHFTR,1,4) || '-' || substr(AMHFTR,5,2) || '-' || substr(AMHFTR,7,2)) >= date(now()) - 90 days order by 1 desc,2 desc,3 desc;




AUMV actual es igual que tdmh un dia anterior
aumv 5 y aumvh historico movimientos

SELECT
  AMVFSI FECHATRAN, 
  AMV012 HORA, 
  substr(AMV043, 1, 22) || ' ' || substr(AMV043, 30, 6) UBICACION, 
  TRADES TIPOTRAN, 
  case when AMV004>0 then AMV004/100 else 0 end VALORTX,
  case AMV039 when '00' then 'APROBADA' else 'DECLINADA' end RESPUESTA,
  AMVVLC VLRCOMISION, 
  AMVVIE VLRIMPUESTO
FROM AUARCHV4P.AUMV 
INNER JOIN AUARCHV4.AUTRA ON (TRATRT=AMVTTR)
WHERE amvtar ='5116140000000061' 
  AND amvinr in (' ','2','1') 
  AND AMVFSI >= '20170403'
  AND AMVFSI <= '20170403'
UNION ALL
SELECT 
  AMHFSI FECHATRAN,
  AMH012 HORA, 
  substr(AMH043, 1, 22) || ' ' || substr(AMH043, 30, 6) UBICACION, 
  TRADES TIPOTRAN, 
  case when AMH004>0 then AMH004/100 else 0 end VALORTX,
  case AMH039 when '00' then 'APROBADA' else 'DECLINADA' end RESPUESTA, 
  AMHVLC VLRCOMISION, 
  AMHVIE VLRIMPUESTO 
FROM AUARCHV4P.AUMVH 
INNER JOIN AUARCHV4.AUTRA ON (TRATRT=AMHTTR)
WHERE amhtar ='5116140000000061' 
  AND AMHINR in (' ','2','1') 
  AND AMHFSI >= '20170403'
  AND AMHFSI <= '20170403'
  order by 1 desc, 2 desc, 3 desc
  
  UPDATE CJARCHV71.SIMA SET SMABOD=24, SMAOUB=2, SMASSU=10 WHERE SMASMA=118518;
  
  
  SELECT TBICOD FROM TDARCHV3P.TDBI WHERE TBICOD LIKE '8%' AND TBINDT = 16
  
  
  SELECT TMHTAR FROM TDARCHV3P.TDMH;
  
  SELECT * FROM TDARCHV3P.TDMH WHERE TMHTAR = '6279500140000012867';
