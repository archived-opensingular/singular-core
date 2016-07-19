CREATE SCHEMA if not exists DBSINGULAR;

CREATE SEQUENCE DBSINGULAR.SQ_CO_FORMULARIO  START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE DBSINGULAR.SQ_CO_COLECAO  START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE DBSINGULAR.SQ_CO_TIPO_FORMULARIO  START WITH 1 INCREMENT BY 1;

/*==============================================================*/
/* Table: TB_FORMULARIO                                         */
/*==============================================================*/
CREATE TABLE DBSINGULAR.TB_FORMULARIO (
   CO_FORMULARIO        BIGINT                  NOT NULL,
   CO_TIPO_FORMULARIO   BIGINT                  NULL, --NOT NULL, COMENTANDO ATÉ ALTERAR O CODIGO
   DS_XML               VARCHAR(MAX)         NOT NULL,
   DS_XML_ANOTACAO      VARCHAR(MAX)         NULL,
   CO_COLECAO           BIGINT                  NULL,
   CONSTRAINT PK_FORMULARIO PRIMARY KEY (CO_FORMULARIO)
);

/*==============================================================*/
/* Table: TB_COLECAO                                            */
/*==============================================================*/
CREATE TABLE DBSINGULAR.TB_COLECAO (
   CO_COLECAO           INT                  NOT NULL,
   CO_TIPO_FORMULARIO   INT                  NOT NULL,
   NO_COLECAO           VARCHAR(50)          NULL,
   CONSTRAINT PK_COLECAO PRIMARY KEY (CO_COLECAO)
);

/*==============================================================*/
/* Table: TB_TIPO_FORMULARIO                                    */
/*==============================================================*/
CREATE TABLE DBSINGULAR.TB_TIPO_FORMULARIO (
   CO_TIPO_FORMULARIO   INT                  NOT NULL,
   SG_TIPO_FORMULARIO   VARCHAR(200)         NOT NULL,
   NU_VERSAO_CACHE      INT                  NOT NULL,
   CONSTRAINT PK_TIPO_FORMULARIO PRIMARY KEY (CO_TIPO_FORMULARIO)
);

/*==============================================================*/
/* Table: TB_ARQUIVO_PETICAO                                    */
/*==============================================================*/
CREATE TABLE DBSINGULAR.TB_ARQUIVO_PETICAO (
   CO_ARQUIVO_PETICAO   VARCHAR(40)               NOT NULL,
   DS_SHA1                 VARCHAR(40) NOT NULL,
   BL_ARQUIVO_PETICAO     BLOB         NOT NULL,
   NU_TAMANHO           BIGINT        NOT NULL,
   CONSTRAINT PK_ARQUIVO_PETICAO PRIMARY KEY (CO_ARQUIVO_PETICAO)
);