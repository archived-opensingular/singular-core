package org.opensingular.form.persistence.service;

public class FormReportQueryBuilder {

    private String       form;
    private StringBuffer casesForColumns = new StringBuffer();
    private StringBuffer where           = new StringBuffer();

    private FormReportQueryBuilder() {
        //hidden constructor, use getInstance
    }

    public static FormReportQueryBuilder getInstance() {
        return new FormReportQueryBuilder();
    }

    public FormReportQueryBuilder withForm(String form) {
        this.form = form;
        return this;
    }

    public FormReportQueryBuilder addColumn(String field, String type, String alias) {
        casesForColumns.append(", MAX(CASE WHEN c.TXT_CAMINHO_CAMPO = '" + field +  "' THEN v.TXT_VALOR END) AS " + alias + "\n");
        return this;
    }

    public FormReportQueryBuilder addFilter(String column, String operator, String value) {
        where.append(" AND " + column + " " + operator + " '" + value + "' \n");
        return this;
    }

    public String buildSql() {
        StringBuffer sb = new StringBuffer("SELECT * FROM (\n");
        sb.append("SELECT v.CO_VERSAO_FORMULARIO as codVersaoFormulario \n");
        sb.append(casesForColumns);
        sb.append("FROM DBSINGULAR.TB_CACHE_CAMPO c \n");
        sb.append("    INNER JOIN DBSINGULAR.TB_CACHE_VALOR v ON v.CO_CACHE_CAMPO = c.CO_CACHE_CAMPO \n");
        sb.append("    INNER JOIN DBSINGULAR.TB_TIPO_FORMULARIO tf ON c.CO_TIPO_FORMULARIO = tf.CO_TIPO_FORMULARIO \n");
        sb.append("WHERE tf.SG_TIPO_FORMULARIO = '" + form + "' \n");
        sb.append("GROUP BY codVersaoFormulario \n");
        sb.append(")");
        sb.append("WHERE 1 = 1 \n");
        sb.append(where);
        return  sb.toString();
    }

}
