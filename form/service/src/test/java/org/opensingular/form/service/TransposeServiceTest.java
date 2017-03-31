package org.opensingular.form.service;

import org.junit.Test;
import org.opensingular.form.persistence.dao.ReportDAO;
import org.opensingular.form.persistence.dto.PeticaoPrimariaDTO;
import org.opensingular.form.persistence.service.TransposeService;

import javax.inject.Inject;
import java.util.List;

public class TransposeServiceTest extends FormServiceTest {

    @Inject
    ReportDAO reportDAO;

    @Test
    public void getListaPeticaoPrimaria() {
        String sql = TransposeService.getInstance()
            .addColumn("requerente.nome", "TXT_VALOR", "nomeRequerente")
            .addColumn("requerente.cnpj", "TXT_VALOR", "cnpjRequerente")
            .addColumn("requerente.enderecoEletronico", "TXT_VALOR", "emailRequerente")
            .addFilter("nomeRequerente", "=", "Associação Hospitalar Beneficente de Bandeirantes")
            .generateSql();

        List<PeticaoPrimariaDTO> peticoes = reportDAO.listPeticoesPrimarias(sql);
        System.out.println(peticoes);
    }
}
