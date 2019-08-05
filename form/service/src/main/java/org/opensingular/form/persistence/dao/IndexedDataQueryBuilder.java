/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.persistence.dao;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.MySQLDialect;


/**
 * This class is used by Singular Studio for SEI!.
 * <p>
 * Note: The query have to has a join with TB_VERSAO_FORMULARIO with alias currentV
 * And join with TB_TIPO_FORMULARIO with alias tpForm
 */
public class IndexedDataQueryBuilder {

    private final String                   schema;
    private       StringBuilder            select;
    private       StringBuilder            from = new StringBuilder(" FROM ");
    private       StringBuilder            join = new StringBuilder();
    private       StringBuilder            where;
    private       StringBuilder            joinCache;
    private       StringBuilder            order;
    private       Class<? extends Dialect> dialect;

    public IndexedDataQueryBuilder(String schema) {
        this.schema = schema;

        select = new StringBuilder("SELECT DISTINCT 'EMPTY' \n");
        joinCache = new StringBuilder();
        where = new StringBuilder(" WHERE 1 = 1 ");
        order = new StringBuilder();
    }

    public void setDialect(Class<? extends Dialect> dialect) {
        this.dialect = dialect;
    }

    public void createSelect(String selectClause) {
        select = new StringBuilder(selectClause);
    }

    public void appendToSelect(String columnName) {
        select.append("  , ").append(columnName);
    }

    public void appendToJoin(String joinClause) {
        join.append(joinClause);
    }


    public void appendToWhere(String joinClause) {
        where.append(joinClause);
    }

    public void appendToOrder(String joinClause) {
        if (StringUtils.isEmpty(order)) {
            order.append(" ORDER BY ");
        }
        order.append(joinClause);
    }

    public void appendToFrom(String fromClause) {
        from.append(fromClause);
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

        if (!join.toString().contains("currentV")) {
            String joinFormTables = " LEFT JOIN DBSINGULAR.TB_TIPO_FORMULARIO tpForm "
                    + "ON cpAdd.CO_TIPO_FORMULARIO = tpForm.CO_TIPO_FORMULARIO"
                    + " LEFT JOIN " + schema + ".TB_VERSAO_FORMULARIO currentV "
                    + "ON cpAdd.CO_VERSAO_ATUAL = currentV.CO_VERSAO_FORMULARIO ";
            join.append(joinFormTables);
        }

        return new StringBuilder()
                .append(select)
                .append(from)
                .append(join)
                .append(joinCache)
                .append(where)
                .append(order).toString();
    }

    private void addColumnToSelect(String column) {

        select.append("  , ").append(column)
                .append(".DS_VALOR as ")
                .append(column)
                .append('\n');
    }

    private void addJoinClause(String columnAlias, String fieldsNames) {

        String coalaseSubQuerySelectValue;
        if (dialect != null && MySQLDialect.class.isAssignableFrom(dialect)) {
            coalaseSubQuerySelectValue = " COALESCE(cast(CACHE_VALOR.DS_VALOR as CHAR), cast(CACHE_VALOR.NU_VALOR as CHAR),  " +
                    "cast(CACHE_VALOR.DT_VALOR as CHAR)) as ds_valor ";
        } else {
            coalaseSubQuerySelectValue = " COALESCE(cast(CACHE_VALOR.DS_VALOR as varchar), cast(CACHE_VALOR.NU_VALOR as varchar), " +
                    " cast(CACHE_VALOR.DT_VALOR as varchar)) as ds_valor ";
        }

        String leftSubQuery = "  LEFT JOIN (SELECT " +
                " CACHE_VALOR.CO_VERSAO_FORMULARIO as co_versao_formulario, " +
                " CACHE_CAMPO.CO_TIPO_FORMULARIO              as co_tipo_formulario, " +
                coalaseSubQuerySelectValue +
                " FROM " + schema + ".TB_CACHE_CAMPO CACHE_CAMPO " +
                " INNER JOIN DBSINGULAR.TB_CACHE_VALOR CACHE_VALOR " +
                "                 on CACHE_VALOR.CO_CACHE_CAMPO = CACHE_CAMPO.CO_CACHE_CAMPO " +
                "                    and CACHE_CAMPO.DS_CAMINHO_CAMPO in (" + fieldsNames + ") " +
                " ) " + columnAlias + " on " + columnAlias + ".CO_VERSAO_FORMULARIO = currentV.CO_VERSAO_FORMULARIO " +
                " and " + columnAlias + ".CO_TIPO_FORMULARIO = tpForm.CO_TIPO_FORMULARIO ";

        joinCache.append(leftSubQuery);

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
