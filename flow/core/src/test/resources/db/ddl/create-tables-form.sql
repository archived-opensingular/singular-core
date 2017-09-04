CREATE SCHEMA if not exists DBSINGULAR;


/*==============================================================*/
/* Table: TB_FORMULARIO                                         */
/*==============================================================*/
CREATE TABLE DBSINGULAR.TB_FORMULARIO (
   CO_FORMULARIO        INT                  NOT NULL,
   CO_TIPO_FORMULARIO   INT                  NOT NULL,
   CO_COLECAO           INT                  NULL,
   CO_VERSAO_ATUAL      INT                  NULL,
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
   NO_LABEL_FORMULARIO  VARCHAR(200)         NULL,
   NU_VERSAO_CACHE      INT                  NOT NULL,
   CONSTRAINT PK_TIPO_FORMULARIO PRIMARY KEY (CO_TIPO_FORMULARIO)
);

/*==============================================================*/
/* Table: TB_VERSAO_FORMULARIO                                  */
/*==============================================================*/
CREATE TABLE DBSINGULAR.TB_VERSAO_FORMULARIO (
   CO_VERSAO_FORMULARIO INT                  NOT NULL,
   CO_FORMULARIO        INT                  NOT NULL,
   DT_INCLUSAO          SMALLDATETIME        NOT NULL,
   XML_CONTEUDO         VARCHAR(MAX)         NOT NULL,
   CO_AUTOR_INCLUSAO    INT                  NULL,
   NU_VERSAO_CACHE      INT                  NULL, -- deve ser no null apos implementação
  ST_INDEXADO          CHAR(1)              NULL,
   CONSTRAINT PK_TB_VERSAO_FORMULARIO PRIMARY KEY (CO_VERSAO_FORMULARIO)
);

/*==============================================================*/
/* Table: TB_VERSAO_ANOTACAO_FORMULARIO                         */
/*==============================================================*/
CREATE TABLE DBSINGULAR.TB_VERSAO_ANOTACAO_FORMULARIO
(
   CO_VERSAO_ANOTACAO   INTEGER              NOT NULL,
   DT_INCLUSAO          DATE                 NOT NULL,
   XML_ANOTACAO         CLOB                 NOT NULL,
   CO_AUTOR_INCLUSAO    INTEGER,
   CO_VERSAO_FORMULARIO INTEGER              NOT NULL,
   CO_CHAVE_ANOTACAO    VARCHAR2(200)        NOT NULL,
   CONSTRAINT PK_TB_VERSAO_ANOTACAO_FORMULAR PRIMARY KEY (CO_VERSAO_ANOTACAO)
);

/*==============================================================*/
/* Table: TB_CONTEUDO_ARQUIVO                                   */
/*==============================================================*/
CREATE TABLE DBSINGULAR.TB_CONTEUDO_ARQUIVO (
   CO_CONTEUDO_ARQUIVO  INT          		 NOT NULL,
   TX_SHA1              CHAR(40)             NOT NULL,
   NU_BYTES             INT			         NOT NULL,
   DT_INCLUSAO          DATETIME             NOT NULL,
   BL_CONTEUDO          IMAGE                NOT NULL,
   CONSTRAINT PK_TB_CONTEUDO_ARQUIVO PRIMARY KEY (CO_CONTEUDO_ARQUIVO)
);

/*==============================================================*/
/* Table: TB_ARQUIVO                                            */
/*==============================================================*/
CREATE TABLE DBSINGULAR.TB_ARQUIVO (
   CO_ARQUIVO           INT                  NOT NULL,
   NO_ARQUIVO           VARCHAR(200)         NOT NULL,
   CO_CONTEUDO_ARQUIVO	INT			         NOT NULL,
   TX_SHA1              CHAR(40)             NOT NULL,
   NU_BYTES             INT                  NOT NULL,
   DT_CRIACAO           SMALLDATETIME        NOT NULL,
   CONSTRAINT PK_TB_ARQUIVO PRIMARY KEY (CO_ARQUIVO)
);

CREATE TABLE DBSINGULAR.TB_ANOTACAO_FORMULARIO
(
   CO_VERSAO_FORMULARIO INTEGER              NOT NULL,
   CO_CHAVE_ANOTACAO    VARCHAR2(200)        NOT NULL,
   CO_VERSAO_ANOTACAO_ATUAL INTEGER              NULL,
   CONSTRAINT PK_TB_ANOTACAO PRIMARY KEY (CO_VERSAO_FORMULARIO, CO_CHAVE_ANOTACAO)
);

/*==============================================================*/
/* Table: TB_ANEXO_FORMULARIO                                   */
/*==============================================================*/
CREATE TABLE DBSINGULAR.TB_ANEXO_FORMULARIO
(
   CO_VERSAO_FORMULARIO NUMBER               NOT NULL,
   CO_ARQUIVO           NUMBER               NOT NULL,
   CONSTRAINT PK_TB_ANEXO_FORMULARIO PRIMARY KEY (CO_VERSAO_FORMULARIO, CO_ARQUIVO)
);
