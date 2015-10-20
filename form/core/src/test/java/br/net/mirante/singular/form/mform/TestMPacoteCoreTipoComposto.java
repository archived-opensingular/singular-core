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

        assertTipo(tipoEndereco.getTipoLocal("rua"), "rua", MTipoString.class);
        assertTipo(tipoEndereco.getCampo("rua"), "rua", MTipoString.class);
        assertEquals((Object) false, tipoEndereco.getTipoLocal("rua").isObrigatorio());
        assertEquals((Object) true, tipoEndereco.getTipoLocal("cep").isObrigatorio());

        assertTipo(tipoEndereco.getTipoLocal("classificacao"), "classificacao", MTipoComposto.class);
        assertTipo(tipoEndereco.getTipoLocal("classificacao.prioridade"), "prioridade", MTipoInteger.class);

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

    private static void assertTipo(MTipo<?> tipo, String nomeEsperado, Class<?> classeEsperadaDoTipo) {
        assertNotNull(tipo);
        assertEquals(nomeEsperado, tipo.getNomeSimples());
        assertEquals(classeEsperadaDoTipo, tipo.getClass());
    }

    public void testCriacaoDinamicaDeAtributos() {
        MDicionario dicionario = MDicionario.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");

        MTipoComposto<? extends MIComposto> tipoBloco = pb.createTipoComposto("bloco");
        tipoBloco.addCampoInteger("inicio");
        tipoBloco.addCampoInteger("fim");
        tipoBloco.addCampoListaOf("enderecos", MTipoString.class);
        tipoBloco.addCampoListaOfComposto("itens", "item").getTipoElementos().addCampoInteger("qtd");
        tipoBloco.addCampoComposto("subBloco").addCampoBoolean("teste");

        MIComposto bloco = tipoBloco.novaInstancia();

        assertTrue(bloco.isEmptyOfData());
        assertTrue(bloco.isCampoNull("inicio"));
        assertTrue(bloco.isCampoNull("enderecos"));
        assertTrue(bloco.isCampoNull("itens"));
        assertTrue(bloco.isCampoNull("itens[0].qtd"));
        assertTrue(bloco.isCampoNull("subBloco"));
        assertTrue(bloco.isCampoNull("subBloco.teste"));
        assertEquals(0, bloco.getCampos().size());

        assertCriacaoDinamicaSubCampo(bloco, "inicio", 0, 1);
        assertCriacaoDinamicaSubCampo(bloco, "enderecos", 1, 2);

        assertCriacaoDinamicaSubCampo(bloco, "itens", 2, 3);
        bloco.getFieldList("itens").addNovo();
        assertCriacaoDinamicaSubCampo(bloco.getFieldRecord("itens[0]"), "qtd", 0, 1);
        assertNotNull(bloco.getValor("itens[0]"));
        assertNull(bloco.getValor("itens[0].qtd"));
        bloco.setValor("itens[0].qtd", 10);
        assertEquals(bloco.getValor("itens[0].qtd"), 10);

        assertCriacaoDinamicaSubCampo(bloco, "subBloco", 3, 4);
        assertCriacaoDinamicaSubCampo(bloco.getFieldRecord("subBloco"), "teste", 0, 1);
        assertNull(bloco.getValor("subBloco.teste"));
        bloco.setValor("subBloco.teste", true);
        assertEquals(bloco.getValor("subBloco.teste"), true);

        // Testa criando em cadeia
        bloco = tipoBloco.novaInstancia();
        assertCriacaoDinamicaSubCampo(bloco, "subBloco.teste", 0, 1);
        assertEquals(1, bloco.getFieldRecord("subBloco").getCampos().size());
    }

    private static void assertCriacaoDinamicaSubCampo(MIComposto bloco, String path, int qtdCamposAntes, int qtdCamposDepois) {
        Object resultado2 = bloco.getValor(path); // Não provoca nova instancia
        assertNull(resultado2);
        assertTrue(bloco.isCampoNull(path));
        assertEquals(qtdCamposAntes, bloco.getCampos().size());

        MInstancia resultado = bloco.getCampo(path); // Provoca instancia
        assertNotNull(resultado);
        if (resultado instanceof MISimples) {
            assertNull(bloco.getValor(path));
        }
        assertTrue(resultado.isEmptyOfData());
        assertEquals(qtdCamposDepois, bloco.getCampos().size());
    }

    public void testTipoCompostoCriacaoComAtributoDoTipoListaDeTipoSimples() {
        MDicionario dicionario = MDicionario.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");

        MTipoComposto<? extends MIComposto> tipoBloco = pb.createTipoComposto("bloco");
        tipoBloco.addCampoListaOf("enderecos", MTipoString.class);

        assertTipo(tipoBloco.getTipoLocal("enderecos"), "enderecos", MTipoLista.class);
        assertTipo(tipoBloco.getCampo("enderecos"), "enderecos", MTipoLista.class);
        assertTipo(((MTipoLista<?>) tipoBloco.getCampo("enderecos")).getTipoElementos(), "String", MTipoString.class);

        MIComposto bloco = tipoBloco.novaInstancia();
        assertNull(bloco.getValor("enderecos"));
        assertNull(bloco.getValor("enderecos[0]"));
        assertEquals(0, bloco.getCampos().size());
        assertTrue(bloco.isEmptyOfData());

        bloco.getFieldList("enderecos");
        assertEquals(1, bloco.getCampos().size());
        assertTrue(bloco.isEmptyOfData());
        assertNotNull(bloco.getValor("enderecos"));
        assertNull(bloco.getValor("enderecos[0]"));
        assertNull(bloco.getFieldList("enderecos").getValor("[0]"));

        bloco.getFieldList("enderecos").addValor("E1");
        assertEquals(1, bloco.getCampos().size());
        assertFalse(bloco.isEmptyOfData());
        assertEquals("E1", bloco.getValor("enderecos[0]"));
        assertEquals("E1", bloco.getFieldList("enderecos").getValor("[0]"));
        assertEqualsList(bloco.getValor("enderecos"), "E1");

        testAtribuicao(bloco, "enderecos[0]", "E2");
    }

    public void testTipoCompostoCriacaoComAtributoDoTipoListaDeTipoComposto() {
        MDicionario dicionario = MDicionario.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");

        MTipoComposto<? extends MIComposto> tipoBloco = pb.createTipoComposto("bloco");
        MTipoLista<MTipoComposto<?>> tipoEnderecos = tipoBloco.addCampoListaOfComposto("enderecos", "endereco");
        MTipoComposto<?> tipoEndereco = tipoEnderecos.getTipoElementos();
        tipoEndereco.addCampoString("rua");
        tipoEndereco.addCampoString("cidade");

        MIComposto bloco = tipoBloco.novaInstancia();
        assertNull(bloco.getValor("enderecos"));
        assertNull(bloco.getValor("enderecos[0]"));
        assertNull(bloco.getValor("enderecos[0].rua"));
        assertEquals(0, bloco.getCampos().size());
        assertTrue(bloco.isEmptyOfData());

        bloco.getFieldList("enderecos");
        assertEquals(1, bloco.getCampos().size());
        assertTrue(bloco.isEmptyOfData());
        assertNotNull(bloco.getValor("enderecos"));
        assertNull(bloco.getValor("enderecos[0]"));
        assertNull(bloco.getValor("enderecos[0].rua"));
        assertNull(bloco.getFieldList("enderecos").getValor("[0]"));
        assertNull(bloco.getFieldList("enderecos").getValor("[0].rua"));

        MIComposto endereco = (MIComposto) bloco.getFieldList("enderecos").addNovo();
        assertEquals(1, bloco.getCampos().size());
        assertTrue(bloco.isEmptyOfData());
        assertTrue(endereco.isEmptyOfData());
        assertEquals(0, endereco.getCampos().size());
        assertNotNull(bloco.getValor("enderecos"));
        assertNotNull(bloco.getValor("enderecos[0]"));
        assertNull(bloco.getValor("enderecos[0].rua"));
        assertNotNull(bloco.getFieldList("enderecos").getValor("[0]"));
        assertNull(bloco.getFieldList("enderecos").getValor("[0].rua"));

        bloco.getCampo("enderecos[0].rua");
        assertEquals(1, endereco.getCampos().size());
        assertTrue(bloco.isEmptyOfData());
        assertTrue(endereco.isEmptyOfData());
        assertNotNull(bloco.getValor("enderecos[0]"));
        assertNull(bloco.getValor("enderecos[0].rua"));

        testAtribuicao(bloco, "enderecos[0].rua", "E2");
        assertFalse(bloco.isEmptyOfData());
        assertFalse(endereco.isEmptyOfData());

    }
}
