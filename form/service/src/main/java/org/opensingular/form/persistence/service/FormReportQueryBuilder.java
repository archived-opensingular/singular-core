package org.opensingular.form.persistence.service;

/**
 * Fornece meios de se obter uma consulta SQL baseada num Formulario indexado.
 * Recebe o SType que representa o form e os atributos que devem ser retornados na consulta.
 */
public class FormReportQueryBuilder {

    private String form;
    private StringBuffer casesForColumns = new StringBuffer();
    private StringBuffer where           = new StringBuffer();
    private String fromClause;

    private FormReportQueryBuilder() {
        //hidden constructor, use getInstance
    }

    public static FormReportQueryBuilder getInstance() {
        FormReportQueryBuilder instance = new FormReportQueryBuilder();

        StringBuffer sb = new StringBuffer("FROM DBSINGULAR.TB_CACHE_CAMPO c \n");
        sb.append("INNER JOIN DBSINGULAR.TB_CACHE_VALOR v ON v.CO_CACHE_CAMPO = c.CO_CACHE_CAMPO \n");
        sb.append("INNER JOIN DBSINGULAR.TB_TIPO_FORMULARIO tf ON c.CO_TIPO_FORMULARIO = tf.CO_TIPO_FORMULARIO \n");

        instance.fromClause = sb.toString();
        return instance;
    }

    public FormReportQueryBuilder withForm(String form) {
        this.form = form;
        return this;
    }

    public FormReportQueryBuilder addColumn(String field, String type, String alias) {
        casesForColumns.append(", MAX(CASE WHEN c.TXT_CAMINHO_CAMPO = '" + field + "' THEN v.TXT_VALOR END) AS " + alias + "\n");
        return this;
    }

    public FormReportQueryBuilder addFilter(String column, String operator, String value) {
        where.append(" AND " + column + " " + operator + " '" + value + "' \n");
        return this;
    }

    public String buildSql() {
        StringBuffer sb = new StringBuffer("SELECT * FROM (\n");
        sb.append("SELECT C.CO_TIPO_FORMULARIO AS CODTIPOFORMULARIO, V.CO_VERSAO_FORMULARIO AS CODVERSAOFORMULARIO \n");
        sb.append(casesForColumns);
        sb.append(fromClause);
        sb.append("WHERE TF.SG_TIPO_FORMULARIO = '" + form + "' \n");
        sb.append("GROUP BY CODVERSAOFORMULARIO \n");
        sb.append(")");
        sb.append("WHERE 1 = 1 \n");
        sb.append(where);
        return sb.toString();
    }


    public String buildSqlToChild() {
        StringBuffer sb = new StringBuffer("SELECT * FROM (\n");
        sb.append("SELECT DISTINCT C.CO_TIPO_FORMULARIO AS CODTIPOFORMULARIO, V.CO_VERSAO_FORMULARIO AS CODVERSAOFORMULARIO, CO_AGRUPADOR \n");
        sb.append(casesForColumns);
        sb.append(fromClause);
        sb.append("GROUP BY V.CO_VERSAO_FORMULARIO, C.CO_TIPO_FORMULARIO, CO_AGRUPADOR \n");
        sb.append(")");
        sb.append("WHERE CO_AGRUPADOR IS NOT NULL \n");
        sb.append(where);
        return sb.toString();
    }

}
