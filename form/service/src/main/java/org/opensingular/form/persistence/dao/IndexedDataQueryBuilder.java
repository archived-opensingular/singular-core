package org.opensingular.form.persistence.dao;

import org.opensingular.lib.support.persistence.util.Constants;

public class IndexedDataQueryBuilder {

    private StringBuilder select   = new StringBuilder("select distinct tipoformulario.co_tipo_formulario as co_tipo_formulario \n");
    private StringBuilder from     = new StringBuilder("from " + Constants.SCHEMA + ".tb_tipo_formulario tipoformulario\n  inner join " + Constants.SCHEMA + ".tb_formulario formulario on tipoformulario.co_tipo_formulario = formulario.co_tipo_formulario \n");
    private StringBuilder join     = new StringBuilder();
    private int          colCount = 0;

    public IndexedDataQueryBuilder addColumn(String columnAlias, String[] fieldName) {
        addColumnToSelect(columnAlias);

        String fieldsNames = getFieldsNames(fieldName);
        addJoinClause(columnAlias, fieldsNames);
        return this;
    }

    /**
     * Cria um código SQL que sempre terá as colunas CO_TIPO_FORMULARIO e CO_VERSAO_FORMULARIO.
     * A partir dessas colunas é possível fazer outros joins com as tabelas do singular.
     *
     * @return*/
    public String createQueryForIndexedData() {
        return select.toString() + from.toString() + join.toString();
    }

    private void addColumnToSelect(String column) {
        if (colCount == 0) {
            select.append("  , " + column + ".co_versao_formulario as co_versao_formulario \n");
        }
        select.append("  , " + column + ".ds_valor as " + column + "\n");
    }

    private void addJoinClause(String columnAlias, String fieldsNames) {
        String joinAlias = "tb_cache_campo_" + ++colCount;
        String fields = "";

        join.append("  inner join " + Constants.SCHEMA + ".tb_cache_campo " + joinAlias + " on " + joinAlias + ".co_tipo_formulario = tipoformulario.co_tipo_formulario \n");
        join.append("  inner join " + Constants.SCHEMA + ".tb_cache_valor " + columnAlias + " on " + columnAlias + ".co_cache_campo = " + joinAlias + ".co_cache_campo \n");
        join.append("          and " + columnAlias + ".co_versao_formulario = formulario.co_versao_atual \n");
        join.append("          and " + joinAlias + ".ds_caminho_campo in (" + fieldsNames + ") \n");
    }

    private String getFieldsNames(String[] fields) {
         StringBuilder fieldsNames = new StringBuilder();

         if (fields != null) {
             for (int i=0; i<fields.length; i++) {
                fieldsNames.append("'" + fields[i] + "'");
                if (fields.length - i > 1) {
                    fieldsNames.append(", ");
                }
             }
         }

         return fieldsNames.toString();
    }

}
