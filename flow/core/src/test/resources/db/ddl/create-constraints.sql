-- Scripts de inserção de Index

/*==============================================================*/
/* Index: IX_INSTANCIA_PROCESSO                                 */
/*==============================================================*/
CREATE INDEX IX_INSTANCIA_PROCESSO ON DBSINGULAR.TB_INSTANCIA_PROCESSO (
  CO_VERSAO_PROCESSO ASC,
  DT_INICIO ASC
);

/*==============================================================*/
/* Index: IX_HISTORICO_INSTANCIA_TAREFA                         */
/*==============================================================*/
CREATE INDEX IX_HISTORICO_INSTANCIA_TAREFA ON DBSINGULAR.TB_HISTORICO_INSTANCIA_TAREFA (
  CO_INSTANCIA_TAREFA ASC,
  DT_INICIO_ALOCACAO ASC
);

/*==============================================================*/
/* Index: IX_INSTANCIA_TAREFA                                   */
/*==============================================================*/
CREATE INDEX IX_INSTANCIA_TAREFA ON DBSINGULAR.TB_INSTANCIA_TAREFA (
  CO_INSTANCIA_PROCESSO ASC,
  DT_INICIO ASC
);

/*==============================================================*/
/* Index: IX_PROCESSO                                           */
/*==============================================================*/
CREATE INDEX IX_PROCESSO ON DBSINGULAR.TB_VERSAO_PROCESSO (
  CO_DEFINICAO_PROCESSO ASC,
  DT_VERSAO ASC
);

/*==============================================================*/
/* Index: IX_CLASSE_DEFINICAO                                   */
/*==============================================================*/
CREATE UNIQUE INDEX IX_CLASSE_DEFINICAO ON DBSINGULAR.TB_DEFINICAO_PROCESSO (
	NO_CLASSE_JAVA ASC
);

/*==============================================================*/
/* Index: IX_GRUPO_NOME                                         */
/*==============================================================*/
CREATE UNIQUE INDEX IX_GRUPO_NOME ON DBSINGULAR.TB_MODULO (NO_MODULO ASC);

/*==============================================================*/
/* Index: IX_GRUPO_CONEXAO                                      */
/*==============================================================*/
CREATE UNIQUE INDEX IX_GRUPO_CONEXAO ON DBSINGULAR.TB_MODULO (URL_CONEXAO ASC);

