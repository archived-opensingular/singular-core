

create schema if not exists DBARRECAD;
create schema if not exists DBCORPORATIVO;
create schema if not exists DBSEGURANCA;

/*==============================================================*/
/* Table: TL_TRANSACAO                                          */
/*==============================================================*/
create table DBARRECAD.TL_TRANSACAO 
(
   CO_ENTIDADE          CHAR(32)             not null,
   NU_CPF_RESPONSAVEL_TRANSACAO VARCHAR2(11)         not null,
   DT_TRANSACAO         DATE                 not null,
   CO_TIPO_PRODUTO      NUMBER(3)            not null,
   ST_TRANSACAO         VARCHAR2(1)          not null,
   NU_APPTID            NUMBER,
   CO_ASSUNTO           NUMBER(5),
   NU_PROCESSO          VARCHAR2(17),
   NU_TRANSACAO_INTERNET VARCHAR2(30)         IDENTITY ,
   DS_REFERENCIA        VARCHAR2(200),
   DT_ATENDIMENTO       DATE,
   NU_PROTOCOLO         VARCHAR2(18),
   DS_XML_PETICAO       CLOB,
   ST_CONCLUIDA         VARCHAR2(1),
   ST_SQL               VARCHAR2(1),
   DT_RETIFICACAO       DATE,
   ST_PROCESSADO        CHAR,
   DT_INICIO_TRANSACAO  DATE                 default SYSDATE,
   TP_CANCELAMENTO      VARCHAR2(1)         
      
      constraint CKC_TPCANCELAMENTO_TLTRANS check (TP_CANCELAMENTO is null or ( TP_CANCELAMENTO in ('C','A') )),
   DT_CANCELAMENTO      DATE,
   DS_XML_INICIAL_TRANSACAO CLOB,
   DT_PROCESSAMENTO     DATE,
   TP_PROTOCOLO         CHAR,
   TP_FORMULARIO        CHAR,
   ST_PETICAO_MANUAL    CHAR                 default 'X' not null,
   constraint PK_TRANSACAO primary key (NU_TRANSACAO_INTERNET)
);


create sequence DBARRECAD.SQ_TRANSACAO;



/*==============================================================*/
/* Table: TB_PESSOA                                             */
/*==============================================================*/
create table DBCORPORATIVO.TB_PESSOA
(
   ID_PESSOA            CHAR(32)             not null,
   TP_PESSOA            VARCHAR2(1)          not null
      constraint CK_PESSOA_TPPESSOA check (TP_PESSOA in ('F','J')),
   DT_CADASTRO          DATE                 not null,
   DT_ATUALIZACAO       DATE,
   ST_VALIDADO          VARCHAR2(1)          not null
      constraint CK_PESSOA_STVALIDADO check (ST_VALIDADO in ('S','N')),
   CO_BANCO             VARCHAR2(3),
   CO_AGENCIA           VARCHAR2(20),
   NU_CONTA_CORRENTE    VARCHAR2(50),
   CO_CIDADE            NUMBER(6)            not null,
   DS_QUALIFICACAO_ENDERECO VARCHAR2(60),
   DS_ENDERECO          VARCHAR2(150)        not null,
   NO_BAIRRO            VARCHAR2(60),
   NU_CEP               VARCHAR2(10),
   DS_EMAIL             VARCHAR2(80),
   CO_UF                VARCHAR2(2),
   CO_PAIS              NUMBER(6)            not null,
   NU_NUVS              VARCHAR2(12),
   TP_ISENCAO_TAXA      VARCHAR2(1)
      constraint CK_PESSOA_TPISENCAOTAXA check (TP_ISENCAO_TAXA is null or (TP_ISENCAO_TAXA in ('S','T'))),
   TP_ORIGEM_CARGA      VARCHAR2(1)
      constraint CK_PESSOA_TPORIGEMCARGA check (TP_ORIGEM_CARGA is null or (TP_ORIGEM_CARGA in ('C','R','S','D','O','Z','L','P','G','T','M','H','X'))),
   NO_CIDADE_ESTRANGEIRA VARCHAR2(150),
   DS_AUDITORIA         VARCHAR2(10),
   NU_ENDERECO          VARCHAR2(5),
   TP_LOGRADOURO        CHAR
      constraint CKC_TP_LOGRADOURO_TB_PESSO check (TP_LOGRADOURO is null or (TP_LOGRADOURO in ('R','C','N') and TP_LOGRADOURO = upper(TP_LOGRADOURO))),
   DS_COMPLEMENTO_ENDERECO VARCHAR2(40),
   DS_LOGRADOURO        VARCHAR2(60),
   constraint PK_PESSOA primary key (ID_PESSOA)

);


/*==============================================================*/
/* Table: TB_PESSOA_FISICA                                      */
/*==============================================================*/
create table DBCORPORATIVO.TB_PESSOA_FISICA
(
   NU_CPF               VARCHAR2(11),
   NO_MAE               VARCHAR2(60),
   TP_SEXO              VARCHAR2(1),
   DT_NASCIMENTO        DATE,
   ID_PESSOA_FISICA     CHAR(32)             not null,
   NO_PESSOA_FISICA     VARCHAR2(100)        not null,
   CO_PAIS_NACIONALIDADE NUMBER(6),
   CO_CIDADE_NATURALIDADE NUMBER(6),
   NO_CIDADE_NATURALIDADE VARCHAR2(60),
   TP_ESTADO_CIVIL      VARCHAR2(1)
      constraint CK_PESSOAFISICA_TPESTADOCIVIL check (TP_ESTADO_CIVIL is null or (TP_ESTADO_CIVIL in ('1','2','3','4','5'))),
      constraint CK_TPESTCIVIL_TBPESSOAFIS check (TP_ESTADO_CIVIL is null or (TP_ESTADO_CIVIL in ('1','2','3','4','5') and TP_ESTADO_CIVIL = upper(TP_ESTADO_CIVIL))),
   NO_CONJUGUE          VARCHAR2(60),
   TP_SANGUE_RH         VARCHAR2(3)
      constraint CK_PESSOAFISICA_TPSANGUERH check (TP_SANGUE_RH is null or (TP_SANGUE_RH in ('A+','A-','AB+','AB-','O+','O-','B+','B-'))),
   NO_PAI               VARCHAR2(60),
   TP_NACIONALIDADE     VARCHAR2(1)
      constraint CK_PF_TPNACIONALIDADE check (TP_NACIONALIDADE is null or (TP_NACIONALIDADE in ('1','2','3','4'))),
   TP_ORIGEM_ETNICA     VARCHAR2(1)
      constraint CK_PESSOAFISICA_TPORIGEMETNICA check (TP_ORIGEM_ETNICA is null or (TP_ORIGEM_ETNICA in ('1','2','3','4','5','6'))),
   TP_CIDADANIA         VARCHAR2(1)
      constraint CK_PESSOAFISICA_TPCIDADANIA check (TP_CIDADANIA is null or (TP_CIDADANIA in ('B','E'))),
   DS_AUDITORIA         VARCHAR2(10),
   NU_CNVS              VARCHAR2(8),
   constraint PK_PESSOA_FISICA primary key (ID_PESSOA_FISICA)
);


/*==============================================================*/
/* Table: TB_PESSOA_JURIDICA                                    */
/*==============================================================*/
create table DBCORPORATIVO.TB_PESSOA_JURIDICA
(
   ID_PESSOA_JURIDICA   CHAR(32)             not null,
   NU_CNPJ              VARCHAR2(14),
   ID_PESSOA_JURIDICA_LOCALIZACAO CHAR(32),
   ID_PESSOA_JURIDICA_PRINCIPAL CHAR(32),
   TP_PESSOA_JURIDICA   VARCHAR2(1)          not null
      constraint CK_PJ_TPPESSOAJURIDICA check (TP_PESSOA_JURIDICA in ('N','I')),
   CO_CNAE_FISCAL       VARCHAR2(7),
   CO_PORTE             NUMBER(2),
   NO_FANTASIA          VARCHAR2(120),
   NO_RAZAO_SOCIAL      VARCHAR2(120)        not null,
   NU_SAC               VARCHAR2(15),
   DS_HOMEPAGE          VARCHAR2(100),
   NU_ANO_BASE_PORTE    NUMBER(4),
   DT_ATUALIZACAO_PORTE DATE,
   DS_OBSERVACAO        VARCHAR2(4000),
   TP_CLASSIFICACAO     VARCHAR2(1)          default NULL
      constraint CK_PJ_TPCLASSIFICACAO check (TP_CLASSIFICACAO is null or (TP_CLASSIFICACAO in ('E','A','H','O','I'))),
   CO_CNES              VARCHAR2(11),
   TP_COMPROVANTE       VARCHAR2(1)
      constraint CK_TPCOMPROVANTE check (TP_COMPROVANTE is null or (TP_COMPROVANTE in ('1','2','3','4','5','6','7','8'))),
   DS_AUDITORIA         VARCHAR2(10),
   DS_NATUREZA_JURIDICA VARCHAR2(100),
   NU_CNVS              VARCHAR2(8),
   DS_IDENTIFICACAO_CNVS VARCHAR2(120),
   ID_PESSOA_JURIDICAVIRTUAL CHAR(32),
   CO_CATEGORIA         VARCHAR2(3),
   constraint PK_PESSOA_JURIDICA primary key (ID_PESSOA_JURIDICA)
);


/*==============================================================*/
/* Table: TB_USUARIO                                            */
/*==============================================================*/
create table DBCORPORATIVO.TB_USUARIO
(
   DS_SENHA             VARCHAR2(32)         not null,
   ST_BLOQUEADO         VARCHAR2(1)          default 'N' not null
      constraint CK_USUARIO_STBLOQUEADO check (ST_BLOQUEADO in ('S','N')),
   NU_TENTATIVA         NUMBER(1)            default 0 not null,
   DS_EMAIL             VARCHAR2(70)         not null,
   DS_PERGUNTA_SECRETA  VARCHAR2(255),
   DS_RESPOSTA_SECRETA  VARCHAR2(32),
   NU_REDEFINIR_SENHA   NUMBER(19),
   ID_PESSOA            CHAR(32)             not null,
   ID_USUARIO           CHAR(32)             not null,
   CO_PERGUNTA_SECRETA  NUMBER(2),
   constraint PK_USUARIO primary key (ID_USUARIO),
   constraint UC_USUARIO_PESSOA unique (ID_PESSOA)
);

/*==============================================================*/
/* Table: TB_USUARIO_REPRESENTANTE                              */
/*==============================================================*/
create table DBCORPORATIVO.TB_USUARIO_REPRESENTANTE
(
   ID_USUARIO_REPRESENTANTE CHAR(32)             not null,
   ID_USUARIO_REPRESENTANTE_PAI CHAR(32),
   ID_USUARIO           CHAR(32)             not null,
   ID_PESSOA_REPRESENTADA CHAR(32)             not null,
   DT_EXPIRACAO         DATE,
   ST_BLOQUEADO         VARCHAR2(1)          default 'N' not null
      constraint CK_USUARIOPESSOA_STBLOQUEADO check (ST_BLOQUEADO in ('S','N')),
   DS_JUSTIFICATIVA_BLOQUEIO VARCHAR2(150),
   constraint PK_USUARIO_REPRESENTANTE primary key (ID_USUARIO_REPRESENTANTE),
   constraint UC_USUARIOPESSOA_COUSUIDPES unique (ID_USUARIO, ID_PESSOA_REPRESENTADA)
);


/*==============================================================*/
/* Table: TB_USUARIO                                            */
/*==============================================================*/
create table DBSEGURANCA.TB_USUARIO
(
   CO_USERNAME          VARCHAR2(30)         not null,
   NO_USUARIO           VARCHAR2(60),
   CO_INTERNO_UORG      NUMBER(6),
   DT_EXPIRACAO_SENHA   DATE,
   DT_VALIDADE_SENHA    DATE,
   NU_TELEFONE          VARCHAR2(60),
   ST_BLOQUEADO         VARCHAR2(1)
      constraint CK_USUARIOSTBLOQUEADO check (ST_BLOQUEADO is null or (ST_BLOQUEADO in ('S','N'))),
   DS_CRIADOR           VARCHAR2(30)         not null,
   DS_SENHA             VARCHAR2(60),
   ST_GESTOR            VARCHAR2(1),
   DS_EMAIL             VARCHAR2(80),
   CO_COLABORADOR       NUMBER(6),
   ST_CRIA_USUARIO      VARCHAR2(1)
      constraint CK_USUARIOSTCRIAUSUARIO check (ST_CRIA_USUARIO is null or (ST_CRIA_USUARIO in ('S','N'))),
   NU_CPF               VARCHAR2(11)         not null,
   NU_MATRICULA_SIAPE   NUMBER(7),
   CO_SERVIDOR          NUMBER(6),
   NU_CONSELHO_REGIONAL VARCHAR2(30),
   DS_PROFISSAO         VARCHAR2(50),
   DS_SENHA_MD5         CHAR(32),
   DT_BLOQUEIO          DATE,
   DS_MOTIVO_BLOQUEIO   VARCHAR2(100),
   TP_USUARIO           VARCHAR2(1)
      constraint CK_USUARIO_TPUSUARIO check (TP_USUARIO is null or (TP_USUARIO in ('S','P','E'))),
   DT_CADASTRO          DATE,
   DT_ALTERACAO         DATE,
   DS_CARGO             VARCHAR2(60),
   CO_CIDADE            NUMBER(6),
   CO_PAIS              NUMBER(6),
   constraint PK_USUARIO primary key (CO_USERNAME)
);
