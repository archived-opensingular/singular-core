DROP ALL OBJECTS;

/*==============================================================*/
/* User: DBFORM                                             */
/*==============================================================*/
create user DBFORM
identified by "";

/*==============================================================*/
/* SCHEMA: DBFORM                                           */
/*==============================================================*/
CREATE SCHEMA if not exists DBFORM;

/*==============================================================*/
/* Table: TB_POSTO_ATENDIMENTO                                  */
/*==============================================================*/
create table DBFORM.TB_POSTO_ATENDIMENTO
(
   CO_SEQ_POSTO_ATENDIMENTO NUMBER(6)            not null,
   CO_SEQ_ENDERECO      NUMBER,
   DS_EMAIL             VARCHAR2(100),
   constraint PK_TB_POSTO_ATENDIMENTO primary key (CO_SEQ_POSTO_ATENDIMENTO)
);

/*==============================================================*/
/* Table: TB_POSTO_EXTERNO                                      */
/*==============================================================*/
create table DBFORM.TB_POSTO_EXTERNO
(
  CO_POSTO_ATENDIMENTO NUMBER(6)            not null,
  ID_PESSOA_JURIDICA   CHAR(32)             not null,
  constraint PK_TB_POSTO_EXTERNO primary key (CO_POSTO_ATENDIMENTO, ID_PESSOA_JURIDICA)
);

/*==============================================================*/
/* Table: TB_PESSOA_JURIDICA                                    */
/*==============================================================*/
create table DBFORM.TB_PESSOA_JURIDICA
(
   ID_PESSOA_JURIDICA   CHAR(32)             not null,
   NU_CNPJ              VARCHAR2(14),
   constraint PK_TB_PESSOA_JURIDICA primary key (ID_PESSOA_JURIDICA)
);

/*==============================================================*/
/* Table: TB_ENDERECO                                           */
/*==============================================================*/
create table DBFORM.TB_ENDERECO
(
  CO_SEQ_ENDERECO      NUMBER               not null,
  NO_CEP               VARCHAR2(8),
  constraint PK_TB_ENDERECO primary key (CO_SEQ_ENDERECO)
);

alter table DBFORM.TB_POSTO_ATENDIMENTO
   add constraint FK_TB_POSTO_REFERENCE_TB_ENDER foreign key (CO_SEQ_ENDERECO)
   references DBFORM.TB_ENDERECO (CO_SEQ_ENDERECO);

alter table DBFORM.TB_POSTO_EXTERNO
  add constraint FK_POSTOEXTERNO_PJ foreign key (ID_PESSOA_JURIDICA)
  references DBFORM.TB_PESSOA_JURIDICA (ID_PESSOA_JURIDICA);

alter table DBFORM.TB_POSTO_EXTERNO
  add constraint FK_PTEXTERNO_PTATENDIMENTO foreign key (CO_POSTO_ATENDIMENTO)
  references DBFORM.TB_POSTO_ATENDIMENTO (CO_SEQ_POSTO_ATENDIMENTO);