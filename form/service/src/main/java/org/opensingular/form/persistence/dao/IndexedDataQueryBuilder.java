package org.opensingular.form.persistence.dao;

public class IndexedDataQueryBuilder {

    private final String        schema;
    private       StringBuilder select;
    private       StringBuilder from;
    private       StringBuilder join;
    private int colCount = 0;

    public IndexedDataQueryBuilder(String schema) {
        this.schema = schema;

        select = new StringBuilder("select distinct tipoformulario.co_tipo_formulario as co_tipo_formulario \n");
        from = new StringBuilder("from ").append(this.schema).append(".tb_tipo_formulario tipoformulario\n  inner join ")
                .append(this.schema).append(".tb_formulario formulario on tipoformulario.co_tipo_formulario = formulario.co_tipo_formulario \n");
        join = new StringBuilder();
    }


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
     * @return
     */
    public String createQueryForIndexedData() {
        return select.toString() + from.toString() + join.toString();
    }

    private void addColumnToSelect(String column) {
        if (colCount == 0) {
            select.append("  , ").append(column).append(".co_versao_formulario as co_versao_formulario \n");
        }
        select.append("  , ").append(column).append(".ds_valor as ").append(column).append('\n');
    }

    private void addJoinClause(String columnAlias, String fieldsNames) {
        String joinAlias = "tb_cache_campo_" + ++colCount;

        join.append("  inner join ").append(schema).append(".tb_cache_campo ").append(joinAlias).append(" on ").append(joinAlias).append(".co_tipo_formulario = tipoformulario.co_tipo_formulario \n");
        join.append("  inner join ").append(schema).append(".tb_cache_valor ").append(columnAlias).append(" on ").append(columnAlias).append(".co_cache_campo = ").append(joinAlias).append(".co_cache_campo \n");
        join.append("          and ").append(columnAlias).append(".co_versao_formulario = formulario.co_versao_atual \n");
        join.append("          and ").append(joinAlias).append(".ds_caminho_campo in (").append(fieldsNames).append(") \n");
    }

    private String getFieldsNames(String[] fields) {
        StringBuilder fieldsNames = new StringBuilder();

        if (fields != null) {
            for (int i = 0; i < fields.length; i++) {
                fieldsNames.append('\'').append(fields[i]).append('\'');
                if (fields.length - i > 1) {
                    fieldsNames.append(", ");
                }
            }
        }

        return fieldsNames.toString();
    }

}
