-- Script de carga inicial de dados

INSERT INTO DBSINGULAR.TB_TIPO_TAREFA (CO_TIPO_TAREFA, DS_TIPO_TAREFA) VALUES (0, 'Automática'); -- JAVA
INSERT INTO DBSINGULAR.TB_TIPO_TAREFA (CO_TIPO_TAREFA, DS_TIPO_TAREFA) VALUES (1, 'Tarefa de Usuário'); -- HUMANA
INSERT INTO DBSINGULAR.TB_TIPO_TAREFA (CO_TIPO_TAREFA, DS_TIPO_TAREFA) VALUES (2, 'Espera'); -- ESPERA
INSERT INTO DBSINGULAR.TB_TIPO_TAREFA (CO_TIPO_TAREFA, DS_TIPO_TAREFA) VALUES (3, 'Fim'); -- FIM


INSERT INTO DBSINGULAR.TB_MODULO VALUES('SINGULARTEST', 'Singular Test','http://url');