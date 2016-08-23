CREATE SCHEMA if not exists DBPPSTOX;

CREATE SEQUENCE DBPPSTOX.SQ_CULTURA_COSEQCULTURA;
CREATE SEQUENCE DBPPSTOX.SQ_MODEMPREGO_COSEQMODEMPREGO;
CREATE SEQUENCE DBPPSTOX.SQ_NORMA_COSEQNORMA;
CREATE SEQUENCE DBPPSTOX.SQ_SUBGRUPO_COSEQSUBGRUPO;
CREATE SEQUENCE DBPPSTOX.SQ_TIPODOSE_COSEQTIPODOSE;
CREATE SEQUENCE DBPPSTOX.SQ_TIPOFORMUL_COSEQTIPOFORMUL;

/*==============================================================*/
/* Table: TD_CULTURA                                            */
/*==============================================================*/
CREATE TABLE DBPPSTOX.TD_CULTURA
(
   CO_SEQ_CULTURA       NUMBER               NOT NULL,
   CO_SUBGRUPO          NUMBER,
   NO_CULTURA           VARCHAR2(150)        NOT NULL,
   CONSTRAINT PK_CULTURA PRIMARY KEY (CO_SEQ_CULTURA)
);

/*==============================================================*/
/* Table: TD_MODALIDADE_EMPREGO                                 */
/*==============================================================*/
CREATE TABLE DBPPSTOX.TD_MODALIDADE_EMPREGO
(
   CO_SEQ_MODALIDADE_EMPREGO NUMBER               NOT NULL,
   NO_MODALIDADE_EMPREGO VARCHAR2(100)        NOT NULL,
   CONSTRAINT PK_MODALIDADE_EMPREGO PRIMARY KEY (CO_SEQ_MODALIDADE_EMPREGO)
);

/*==============================================================*/
/* Table: TD_NORMA                                              */
/*==============================================================*/
CREATE TABLE DBPPSTOX.TD_NORMA
(
   CO_SEQ_NORMA         NUMBER               NOT NULL,
   NO_NORMA             VARCHAR2(50)         NOT NULL,
   CONSTRAINT PK_NORMA PRIMARY KEY (CO_SEQ_NORMA)
);

/*==============================================================*/
/* Table: TD_SUBGRUPO                                           */
/*==============================================================*/
CREATE TABLE DBPPSTOX.TD_SUBGRUPO
(
   CO_SEQ_SUBGRUPO      NUMBER               NOT NULL,
   SG_SUBGRUPO          CHAR(3)              NOT NULL,
   DS_CULTURA_REPRESENTATIVA VARCHAR(255)         NOT NULL,
   DS_CULTURA_MENOR     VARCHAR(500)         NOT NULL,
   CONSTRAINT PK_SUBGRUPO PRIMARY KEY (CO_SEQ_SUBGRUPO)
);

/*==============================================================*/
/* Table: TD_TIPO_DOSE                                          */
/*==============================================================*/
CREATE TABLE DBPPSTOX.TD_TIPO_DOSE
(
   CO_SEQ_TIPO_DOSE     NUMBER               NOT NULL,
   NO_TIPO_DOSE         VARCHAR2(15)         NOT NULL,
   CONSTRAINT PK_TIPO_DOSE PRIMARY KEY (CO_SEQ_TIPO_DOSE)
);

/*==============================================================*/
/* Table: TD_TIPO_FORMULACAO                                    */
/*==============================================================*/
CREATE TABLE DBPPSTOX.TD_TIPO_FORMULACAO
(
   CO_SEQ_TIPO_FORMULACAO NUMBER               NOT NULL,
   SG_TIPO_FORMULACAO   VARCHAR2(3)          NOT NULL,
   NO_TIPO_FORMULACAO   VARCHAR2(255)        NOT NULL,
   DS_TIPO_FORMULACAO   VARCHAR2(500)        NOT NULL,
   CONSTRAINT PK_TIPO_FORMULACAO PRIMARY KEY (CO_SEQ_TIPO_FORMULACAO)
);
