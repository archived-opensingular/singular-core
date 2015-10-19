package br.net.mirante.singular.form.mform;

import org.junit.Assert;

import br.net.mirante.singular.form.mform.core.MTipoInteger;
import br.net.mirante.singular.form.mform.core.MTipoString;

public class TestMFormUtil extends TestCaseForm {

    public void testValidacaoNomeSimples() {
        testarNomeInvalido(" sss ");
        testarNomeInvalido("sss ");
        testarNomeInvalido(" ss");
        testarNomeInvalido("1ss");
        testarNomeInvalido("*ss");
        testarNomeInvalido("@ss");
        testarNomeInvalido("ss.xx");
        MFormUtil.checkNomeSimplesValido("long");
        MFormUtil.checkNomeSimplesValido("int");
        MFormUtil.checkNomeSimplesValido("ss");
        MFormUtil.checkNomeSimplesValido("s1");
        MFormUtil.checkNomeSimplesValido("sã1");
    }

    private static void testarNomeInvalido(String nome) {
        try {
            MFormUtil.checkNomeSimplesValido(nome);
            Assert.fail("O nome deveria ser invalido");
        } catch (RuntimeException e) {
            if (!e.getMessage().contains("válido") || !(e.getMessage().charAt(0) == '\'')) {
                throw e;

            }
        }
    }

    public void testResolverTipoCampo() {
        MDicionario dicionario = MDicionario.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");

        MTipoComposto<? extends MIComposto> tipoBloco = pb.createTipoComposto("bloco");
        MTipoInteger integer1 = tipoBloco.addCampoInteger("integer1");
        MTipoString string1 = tipoBloco.addCampoString("string1");
        MTipoComposto<?> tipoSubBloco = tipoBloco.addCampoComposto("subBloco");
        MTipoInteger integer2 = tipoSubBloco.addCampoInteger("integer2");
        MTipoString tipoString2 = pb.createTipo("string2", MTipoString.class);
        MTipoLista<MTipoString> tipoListaString2 = tipoBloco.addCampoListaOf("enderecos", tipoString2);
        MTipoString tipoString3 = pb.createTipo("string3", MTipoString.class);
        MTipoLista<MTipoString> tipoListaString3 = tipoBloco.addCampoListaOf("nomes", tipoString3);
        MTipoLista<MTipoComposto<?>> listaSubBloco2 = tipoBloco.addCampoListaOfComposto("listaSubBloco2", "coisa");
        MTipoInteger tipoQtd = listaSubBloco2.getTipoElementos().addCampoInteger("qtd");

        tipoBloco.debug();

        assertTipoResultante(tipoBloco, "integer1", integer1);
        assertTipoResultante(tipoBloco, "integer1", dicionario.getTipo("teste.bloco.integer1"));
        assertTipoResultante(tipoBloco, "integer1", dicionario.getTipo(MTipoInteger.class));
        assertTipoResultante(tipoBloco, "integer1", string1, false);
        assertTipoResultante(tipoBloco, "integer1", dicionario.getTipo("teste.bloco.string1"), false);
        assertTipoResultante(tipoBloco, "integer1", integer2, false);
        assertTipoResultante(tipoBloco, "integer1", tipoQtd, false);
        assertTipoResultante(tipoBloco, "string1", string1);

        assertTipoResultanteException(tipoBloco, "integerX", "Não existe o campo 'integerX'");
        assertTipoResultanteException(tipoBloco, "integer1.a", "Não se aplica um path a um tipo simples");
        assertTipoResultanteException(tipoBloco, "integer1[0]", "Não se aplica um path a um tipo simples");
        assertTipoResultanteException(integer1, "a", "Não se aplica um path a um tipo simples");
        assertTipoResultanteException(integer1, "[0]", "Não se aplica um path a um tipo simples");

        assertTipoResultante(tipoBloco, "subBloco", tipoSubBloco);
        assertTipoResultante(tipoBloco, "subBloco", dicionario.getTipo(MTipoComposto.class));
        assertTipoResultante(tipoBloco, "subBloco.integer2", integer2);

        assertTipoResultanteException(tipoBloco, "integerX", "Não existe o campo 'integerX'");
        assertTipoResultanteException(tipoBloco, "[0]", "Índice de lista não se aplica a um tipo composto");

        assertTipoResultante(tipoBloco, "enderecos", tipoListaString2);
        assertTipoResultante(tipoBloco, "enderecos", dicionario.getTipo(MTipoLista.class));
        assertTipoResultante(tipoBloco, "enderecos[1]", tipoString2);
        assertTipoResultante(tipoBloco, "enderecos[4]", dicionario.getTipo(MTipoString.class));
        assertTipoResultante(tipoBloco, "nomes", tipoListaString3);
        assertTipoResultante(tipoBloco, "nomes", dicionario.getTipo(MTipoLista.class));
        assertTipoResultante(tipoBloco, "nomes[20]", dicionario.getTipo(MTipoString.class));
        assertTipoResultante(tipoBloco, "nomes[20]", dicionario.getTipo(MTipoInteger.class), false);
        assertTipoResultante(tipoBloco, "nomes[60]", tipoListaString3.getTipoElementos());

        assertTipoResultante(tipoBloco, "listaSubBloco2", listaSubBloco2);
        assertTipoResultante(tipoBloco, "listaSubBloco2", dicionario.getTipo(MTipoLista.class));
        assertTipoResultante(tipoBloco, "listaSubBloco2[1]", listaSubBloco2.getTipoElementos());
        assertTipoResultante(tipoBloco, "listaSubBloco2[1].qtd", tipoQtd);
        assertTipoResultante(tipoBloco, "listaSubBloco2[1].qtd", dicionario.getTipo(MTipoInteger.class));

        assertTipoResultante(listaSubBloco2, "[1].qtd", tipoQtd);
        assertTipoResultante(tipoListaString2, "[1]", dicionario.getTipo(MTipoString.class));

        assertTipoResultanteException(tipoBloco, "listaSubBloco2.a", "Não se aplica a um tipo lista");
        assertTipoResultanteException(listaSubBloco2, "a", "Não se aplica a um tipo lista");
        assertTipoResultanteException(listaSubBloco2, "[1][1]", "Índice de lista não se aplica a um tipo composto");
        assertTipoResultanteException(tipoListaString2, "a", "Não se aplica a um tipo lista");
        assertTipoResultanteException(tipoListaString2, "[1][1]", "Não se aplica um path a um tipo simples");
    }

    private static void assertTipoResultanteException(MTipo<?> pontoOrigem, String path, String msgExceptionEsperada) {
        assertException(() -> MFormUtil.resolverTipoCampo(pontoOrigem, new LeitorPath(path)), msgExceptionEsperada);

    }

    private static void assertTipoResultante(MTipo<?> pontoOrigem, String path, MTipo<?> tipoEsperado) {
        assertTipoResultante(pontoOrigem, path, tipoEsperado, true);
    }
    private static void assertTipoResultante(MTipo<?> pontoOrigem, String path, MTipo<?> tipoEsperado, boolean temQueSerCompativel) {
        MTipo<?> tipoResultado = MFormUtil.resolverTipoCampo(pontoOrigem, new LeitorPath(path));
        if (tipoResultado.isTypeOf(tipoEsperado)) {
            if (!temQueSerCompativel) {
                fail("No path '" + path + "' foi encontrado o resultado '" + tipoResultado.getNome() + "', o que não deveria ser o caso");
            }
        } else if (temQueSerCompativel) {
            fail("No path '" + path + "' foi encontrado o resultado '" + tipoResultado.getNome() + "', mas era esperado '"
                    + tipoEsperado.getNome() + "'");
        }
    }
}
