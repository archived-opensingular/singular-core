CREATE SCHEMA if not exists DBPETTOX;

CREATE SEQUENCE DBPETTOX.SQ_CULTURA_COSEQCULTURA;
CREATE SEQUENCE DBPETTOX.SQ_MODEMPREGO_COSEQMODEMPREGO;
CREATE SEQUENCE DBPETTOX.SQ_NORMA_COSEQNORMA;
CREATE SEQUENCE DBPETTOX.SQ_TIPODOSE_COSEQTIPODOSE;
CREATE SEQUENCE DBPETTOX.SQ_TIPOFORMUL_COSEQTIPOFORMUL;

/*==============================================================*/
/* Table: TD_CULTURA                                            */
/*==============================================================*/
CREATE TABLE DBPETTOX.TD_CULTURA
(
   CO_SEQ_CULTURA       NUMBER               NOT NULL,
   NO_CULTURA           VARCHAR2(150)        NOT NULL,
   CONSTRAINT PK_CULTURA PRIMARY KEY (CO_SEQ_CULTURA)
);

/*==============================================================*/
/* Table: TD_MODALIDADE_EMPREGO                                 */
/*==============================================================*/
CREATE TABLE DBPETTOX.TD_MODALIDADE_EMPREGO
(
   CO_SEQ_MODALIDADE_EMPREGO NUMBER               NOT NULL,
   NO_MODALIDADE_EMPREGO VARCHAR2(100)        NOT NULL,
   CONSTRAINT PK_MODALIDADE_EMPREGO PRIMARY KEY (CO_SEQ_MODALIDADE_EMPREGO)
);

/*==============================================================*/
/* Table: TD_NORMA                                              */
/*==============================================================*/
CREATE TABLE DBPETTOX.TD_NORMA
(
   CO_SEQ_NORMA         NUMBER               NOT NULL,
   NO_NORMA             VARCHAR2(50)         NOT NULL,
   CONSTRAINT PK_NORMA PRIMARY KEY (CO_SEQ_NORMA)
);

/*==============================================================*/
/* Table: TD_TIPO_DOSE                                          */
/*==============================================================*/
CREATE TABLE DBPETTOX.TD_TIPO_DOSE
(
   CO_SEQ_TIPO_DOSE     NUMBER               NOT NULL,
   NO_TIPO_DOSE         VARCHAR2(15)         NOT NULL,
   CONSTRAINT PK_TIPO_DOSE PRIMARY KEY (CO_SEQ_TIPO_DOSE)
);

/*==============================================================*/
/* Table: TD_TIPO_FORMULACAO                                    */
/*==============================================================*/
CREATE TABLE DBPETTOX.TD_TIPO_FORMULACAO
(
   CO_SEQ_TIPO_FORMULACAO NUMBER               NOT NULL,
   SG_TIPO_FORMULACAO   VARCHAR2(3)          NOT NULL,
   NO_TIPO_FORMULACAO   VARCHAR2(255)        NOT NULL,
   DS_TIPO_FORMULACAO   VARCHAR2(500)        NOT NULL,
   CONSTRAINT PK_TIPO_FORMULACAO PRIMARY KEY (CO_SEQ_TIPO_FORMULACAO)
);

CREATE TABLE DBPETTOX.TB_TRANSACAO_ENVIADA (
   CO_REQUISICAO           INTEGER              NOT NULL,
   NU_TRANSACAO_INTERNET VARCHAR2(30)         NOT NULL,
   CO_VERSAO_FORMULARIO INTEGER              NOT NULL,
   NU_PROCESSO          VARCHAR2(17),
   TP_TRANSACAO         CHAR(1)              not null,
   CONSTRAINT CK_TRANSACAO_TPTRANSACAO CHECK (TP_TRANSACAO IN ('P','E','R')),
   CONSTRAINT PK_TB_TRANSACAO_ENVIADA PRIMARY KEY (CO_REQUISICAO, NU_TRANSACAO_INTERNET, CO_VERSAO_FORMULARIO)
);