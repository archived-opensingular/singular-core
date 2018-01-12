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
/* SEQUENCES                                                    */
/*==============================================================*/

create sequence DBFORM.SQ_PARENT;

create sequence DBFORM.SQ_ITEM;

/*==============================================================*/
/* Table: TB_PARENT                                             */
/*==============================================================*/
create table DBFORM.TB_PARENT
(
  ID_PARENT   NUMBER               not null,
  FK_ITEM NUMBER  not null,
  DS_DESCRIPTION          CHAR(32),
  constraint PK_TB_PARENT primary key (ID_PARENT)
);

/*==============================================================*/
/* Table: TB_ITEM                                               */
/*==============================================================*/
create table DBFORM.TB_ITEM
(
  ID_ITEM   NUMBER               not null,
  DS_DESCRIPTION          CHAR(32),
  constraint PK_TB_ITEM primary key (ID_ITEM)
);

alter table DBFORM.TB_PARENT
  add constraint FK_PARENT_ITEM foreign key (FK_ITEM)
references DBFORM.TB_ITEM (ID_ITEM);
