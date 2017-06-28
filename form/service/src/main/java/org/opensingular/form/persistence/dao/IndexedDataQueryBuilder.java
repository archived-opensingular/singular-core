package org.opensingular.form.persistence.dao;

public class IndexedDataQueryBuilder {

    private StringBuilder select   = new StringBuilder("select distinct tipoformulario.co_tipo_formulario as co_tipo_formulario \n");
    private StringBuilder from     = new StringBuilder("from dbsingular.tb_tipo_formulario tipoformulario\n  inner join dbsingular.tb_formulario formulario on tipoformulario.co_tipo_formulario = formulario.co_tipo_formulario \n");
    private StringBuilder join     = new StringBuilder();
    private int          colCount = 0;

    public IndexedDataQueryBuilder addColumn(String columnAlias, String fieldName) {
        addColumnToSelect(columnAlias);
        addJoinClause(columnAlias, fieldName);
        return this;
    }

    /**
     * Cria um código SQL que sempre terá as colunas CO_TIPO_FORMULARIO e CO_VERSAO_FORMULARIO.
     * A partir dessas colunas é possível fazer outros joins com as tabelas do singular.
     *
     * @return
     */
    public String createQueryForIndexedData() {
        return select.toString() + from.toString() + join.toString();
    }

    private void addColumnToSelect(String column) {
        if (colCount == 0) {
            select.append("  , " + column + ".co_versao_formulario as co_versao_formulario \n");
        }
        select.append("  , " + column + ".txt_valor as " + column + "\n");
    }

    private void addJoinClause(String columnAlias, String fieldName) {
        String joinAlias = "tb_cache_campo_" + ++colCount;

        join.append("  inner join dbsingular.tb_cache_campo " + joinAlias + " on " + joinAlias + ".co_tipo_formulario = tipoformulario.co_tipo_formulario \n");
        join.append("  inner join dbsingular.tb_cache_valor " + columnAlias + " on " + columnAlias + ".co_cache_campo = " + joinAlias + ".co_cache_campo \n");
        join.append("          and " + columnAlias + ".co_versao_formulario = formulario.co_versao_atual \n");
        join.append("          and " + joinAlias + ".txt_caminho_campo = '" + fieldName + "' \n");
    }

}
