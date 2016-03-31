package br.net.mirante.singular.exemplos.notificacaosimplificada.dao;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.EmbalagemPrimariaBasica;
import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.EmbalagemSecundaria;
import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.generic.VocabularioControlado;
import br.net.mirante.singular.exemplos.notificacaosimplificada.spring.NotificaoSimplificadaSpringConfiguration;
import org.fest.assertions.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import java.util.List;

import static org.junit.Assert.*;

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
}