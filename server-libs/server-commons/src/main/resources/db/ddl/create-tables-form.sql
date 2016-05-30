CREATE SEQUENCE DBSINGULAR.SQ_CO_FORMULARIO  START WITH 1 INCREMENT BY 1;

/*==============================================================*/
/* Table: TB_FORMULARIO                                            */
/*==============================================================*/
CREATE TABLE DBSINGULAR.TB_FORMULARIO (
   CO_FORMULARIO        BIGINT       NOT NULL,
   DS_XML               CLOB         NOT NULL,
   DS_XML_ANOTACAO      CLOB         NULL,
   CONSTRAINT PK_FORMULARIO PRIMARY KEY (CO_FORMULARIO)
);

