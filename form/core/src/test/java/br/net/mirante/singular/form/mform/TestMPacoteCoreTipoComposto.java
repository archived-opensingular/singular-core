package br.net.mirante.singular.form.mform;

import java.util.Collection;

import br.net.mirante.singular.form.mform.core.MTipoInteger;
import br.net.mirante.singular.form.mform.core.MTipoString;

public class TestMPacoteCoreTipoComposto extends TestCaseForm {

    public void testTipoCompostoCriacao() {
        MDicionario dicionario = MDicionario.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");

        MTipoComposto<?> tipoEndereco = pb.createTipoComposto("endereco");
        tipoEndereco.addCampo("rua", MTipoString.class);
        tipoEndereco.addCampoString("bairro", true);
        tipoEndereco.addCampoInteger("cep", true);

        MTipoComposto<?> tipoClassificacao = tipoEndereco.addCampoComposto("classificacao");
        tipoClassificacao.addCampoInteger("prioridade");
        tipoClassificacao.addCampoString("descricao");

        assertTipo(tipoEndereco.getTipoLocal("rua"), "rua");
        assertTipo(tipoEndereco.getCampo("rua"), "rua");
        assertEquals((Object) false, tipoEndereco.getTipoLocal("rua").isObrigatorio());
        assertEquals((Object) true, tipoEndereco.getTipoLocal("cep").isObrigatorio());

        assertTipo(tipoEndereco.getTipoLocal("classificacao"), "classificacao");
        assertTipo(tipoEndereco.getTipoLocal("classificacao.prioridade"), "prioridade");
        assertEquals(MTipoInteger.class, tipoEndereco.getTipoLocal("classificacao.prioridade").getClass());

        assertNull(tipoEndereco.getTipoLocalOpcional("classificacao.prioridade.x.y"));
        assertException(() -> tipoEndereco.getTipoLocal("classificacao.prioridade.x.y"), "Não existe o tipo");

        MIComposto endereco = tipoEndereco.novaInstancia();

        assertNull(endereco.getValor("rua"));
        assertNull(endereco.getValor("bairro"));
        assertNull(endereco.getValor("cep"));
        assertNull(endereco.getValor("classificacao"));
        assertNull(endereco.getValor("classificacao.prioridade"));
        assertNull(endereco.getValor("classificacao.descricao"));

        assertException(() -> endereco.setValor(100), "Método não suportado");

        testAtribuicao(endereco, "rua", "Pontes");
        testAtribuicao(endereco, "bairro", "Norte");
        testAtribuicao(endereco, "classificacao.prioridade", 1);
        assertNotNull(endereco.getValor("classificacao"));
        assertTrue(endereco.getValor("classificacao") instanceof Collection);
        assertTrue(((Collection<?>) endereco.getValor("classificacao")).size() >= 1);
        testAtribuicao(endereco, "classificacao.prioridade", 1);

        testAtribuicao(endereco, "classificacao", null);
        assertNull(endereco.getValor("classificacao.prioridade"));
        testAtribuicao(endereco, "classificacao.prioridade", null);

        assertException(() -> endereco.setValor("classificacao", "X"), "Método não suportado");
    }

    private static void assertTipo(MTipo<?> tipo, String nomeEsperado) {
        assertNotNull(tipo);
        assertEquals(nomeEsperado, tipo.getNomeSimples());
    }

}
