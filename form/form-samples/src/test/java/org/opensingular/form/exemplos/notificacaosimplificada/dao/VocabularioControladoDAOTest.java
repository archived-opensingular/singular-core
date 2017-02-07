package org.opensingular.form.exemplos.notificacaosimplificada.dao;

import org.opensingular.form.exemplos.notificacaosimplificada.domain.CategoriaRegulatoriaMedicamento;
import org.opensingular.form.exemplos.notificacaosimplificada.domain.EmbalagemPrimariaBasica;
import org.opensingular.form.exemplos.notificacaosimplificada.domain.EmbalagemSecundaria;
import org.opensingular.form.exemplos.notificacaosimplificada.domain.LinhaCbpf;
import org.opensingular.form.exemplos.notificacaosimplificada.domain.Substancia;
import org.opensingular.form.exemplos.notificacaosimplificada.spring.NotificaoSimplificadaSpringConfiguration;
import org.fest.assertions.api.Assertions;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = NotificaoSimplificadaSpringConfiguration.class)
public class VocabularioControladoDAOTest {

    @Inject
    private VocabularioControladoDAO vocabularioControladoDAO;

    @Test
    @Transactional
    public void findByDescricaoForEmbalagemSecundaria() throws Exception {
        final List<EmbalagemSecundaria> result = vocabularioControladoDAO.findByDescricao(EmbalagemSecundaria.class, null);
        Assertions.assertThat(result).isNotEmpty();
    }

    @Test
    @Transactional
    public void findByDescricaoForEmbalagemPrimaria() throws Exception {
        final List<EmbalagemPrimariaBasica> result = vocabularioControladoDAO.findByDescricao(EmbalagemPrimariaBasica.class, null);
        Assertions.assertThat(result).isNotEmpty();
    }

    @Test
    @Transactional
    public void findByDescricaoForSubstancia() throws Exception {
        final List<Substancia> result = vocabularioControladoDAO.findByDescricao(Substancia.class, null);
        Assertions.assertThat(result).isNotEmpty();
    }

    @Test
    @Transactional
    public void listCategoriasRegulatoriasMedicamentoDinamizado() throws Exception {
        final List<CategoriaRegulatoriaMedicamento> result = vocabularioControladoDAO.listCategoriasRegulatoriasMedicamentoDinamizado(null);
        Assert.assertThat("Não foi possivel encontrar os tipos de medicamento dinamizado", result, IsCollectionWithSize.hasSize(3));
    }

    @Test
    @Transactional
    public void listarLinhasProducaoDinamizado() throws Exception {
        final List<LinhaCbpf> result = vocabularioControladoDAO.listarLinhasProducaoDinamizado(null);
        Assert.assertThat("Não foi possivel encontrar os linhas de producao para dinamizado", result, IsCollectionWithSize.hasSize(4));
    }
}