package org.opensingular.form.persistence.service;

import org.opensingular.form.persistence.dao.ReportDAO;
import org.opensingular.form.persistence.dto.BaseDTO;
import org.opensingular.form.persistence.dto.PeticaoPrimariaDTO;

import javax.inject.Inject;
import java.util.List;

public class TransposeService {

    //TODO Leo Valeriano - Definir a melhor maneira de informar o form a ser pesquisado
    private String       form            = "mform.peticao.STypePeticaoPrimariaSimplificada";
    private StringBuffer casesForColumns = new StringBuffer();
    private StringBuffer where           = new StringBuffer();
    private TransposeService instance;

    @Inject
    private ReportDAO reportDAO;

    private TransposeService() {
        //hidden constructor, use getInstance
    }

    public List<? extends BaseDTO> list() {
        return reportDAO.listDtos(generateSql(), PeticaoPrimariaDTO.class);
    }

    public static TransposeService getInstance() {
        return new TransposeService();
    }

    public TransposeService addColumn(String field, String type, String alias) {
        casesForColumns.append(", MAX(CASE WHEN c.TXT_CAMINHO_CAMPO = '" + field +  "' THEN v.TXT_VALOR END) AS " + alias + "\n");
        return this;
    }

    public TransposeService addFilter(String column, String operator, String value) {
        where.append(" AND " + column + " " + operator + " '" + value + "' \n");
        return this;
    }

    public String generateSql() {
        StringBuffer sb = new StringBuffer("SELECT * FROM (\n");
        sb.append("SELECT 1 as codVersaoFormulario --v.CO_VERSAO_FORMULARIO as codVersaoFormulario\n");
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
