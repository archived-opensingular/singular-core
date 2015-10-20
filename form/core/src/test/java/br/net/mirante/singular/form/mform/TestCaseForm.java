package br.net.mirante.singular.form.mform;

import org.junit.Assert;

import junit.framework.TestCase;

public abstract class TestCaseForm extends TestCase {

    protected static void testarAtribuicao(MTipoSimples<?, ?> tipo, boolean valorValido, Object valor, Object valorFinalEsperado) {
        MISimples<?> instancia = tipo.novaInstancia();
        if (valorValido) {
            instancia.setValor(valor);
            Object resultado = instancia.getValor();
            Assert.assertEquals(valorFinalEsperado, resultado);

            Object resultado2 = instancia.getMTipo().converter(valor, instancia.getMTipo().getClasseTipoNativo());
            Assert.assertEquals(resultado, resultado2);
        } else {
            assertException(() -> instancia.setValor(valor), "não consegue converter", "Deveria dar erro de conversão");

            Assert.assertEquals(valorFinalEsperado, instancia.getValor());

            assertException(() -> instancia.getMTipo().converter(valor, instancia.getMTipo().getClasseTipoNativo()),
                    "não consegue converter", "Deveria dar erro de conversão");
        }
    }

    protected static void testAtribuicao(MInstancia registro, String path, Object valor) {
        registro.setValor(path, valor);
        assertEquals(valor, registro.getValor(path));
    }

    protected static void assertException(Runnable acao, String trechoMsgEsperada) {
        assertException(acao, RuntimeException.class, trechoMsgEsperada, null);
    }

    protected static void assertException(Runnable acao, String trechoMsgEsperada, String msgFailException) {
        assertException(acao, RuntimeException.class, trechoMsgEsperada, msgFailException);
    }

    protected static void assertException(Runnable acao, Class<? extends Exception> exceptionEsperada) {
        assertException(acao, exceptionEsperada, null, null);
    }

    protected static void assertException(Runnable acao, Class<? extends Exception> exceptionEsperada, String trechoMsgEsperada,
            String msgFailException) {
        try {
            acao.run();
            String msg = "Não ocorreu nenhuma Exception. Era esperado " + exceptionEsperada.getSimpleName() + "'";
            if (trechoMsgEsperada != null) {
                msg += " com mensagem contendo '" + trechoMsgEsperada + "'";
            }
            if (msgFailException != null) {
                msg += ", pois " + msgFailException;
            }
            fail(msg);
        } catch (Exception e) {
            if (exceptionEsperada.isInstance(e)) {
                if (trechoMsgEsperada == null || e.getMessage().contains(trechoMsgEsperada)) {
                    return;
                }
            }
            throw e;
        }

    }
}
