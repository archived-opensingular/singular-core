package br.net.mirante.singular.bam.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

public class InstanceDAOTest {

    private static final String SQL =
            "SELECT %d AS POS, UPPER(SUBSTRING(DATENAME(MONTH, CAST('%04d-%02d-01T00:00:00.000' AS DATETIME)), 0, 4))%n"
            + "       + '/' + SUBSTRING(DATENAME(YEAR, CAST('%04d-%02d-01T00:00:00.000' AS DATETIME)), 3, 4) AS MES,%n"
            + "       ISNULL(AVG(DATEDIFF(DAY, INS.DT_INICIO, INS.DT_FIM)), 0) AS TEMPO%n"
            + "FROM TB_INSTANCIA_PROCESSO INS%n"
            + "  LEFT JOIN TB_VERSAO_PROCESSO PRO ON PRO.CO_VERSAO_PROCESSO = INS.CO_VERSAO_PROCESSO%n"
            + "  INNER JOIN TB_DEFINICAO_PROCESSO DEF ON DEF.CO_DEFINICAO_PROCESSO = PRO.CO_DEFINICAO_PROCESSO%n"
            + "WHERE DT_FIM > CAST('%04d-%02d-01T00:00:00.000' AS DATETIME) AND SG_PROCESSO = :processCode";

    @Test
    public void mountDateSQL() {
        List<String> sqls = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        for (int pos = 13; pos > 0; pos--) {
            int monthPlus1 = calendar.get(Calendar.MONTH) + 1;
            int yearPlus1 = calendar.get(Calendar.YEAR);
            calendar.add(Calendar.MONTH, -1);
            int month = calendar.get(Calendar.MONTH) + 1;
            int year = calendar.get(Calendar.YEAR);
            sqls.add(String.format(SQL, pos, year, month, year, month, yearPlus1, monthPlus1));
        }
        assertTrue(sqls.size() == 13);
        int pos = 13;
        for (String sql : sqls) {
            System.out.println(String.format("%s%n%s%n", sql, pos-- == 1 ? "ORDER BY POS" : "UNION"));
        }
    }
}