package br.net.mirante.singular.form.mform;

import java.util.List;
import java.util.Objects;

import junit.framework.TestCase;

public abstract class TestCaseForm extends TestCase {

    protected static <R extends MInstancia & IPathEnabledInstance> void testAtribuicao(R registro, String path, Object valor) {
        registro.setValor(path, valor);
        assertEquals(valor, registro.getValor(path));
    }

    protected static void assertEqualsList(Object valor, Object... valoresEsperados) {
        if (!(valor instanceof List)) {
            throw new RuntimeException("Não é uma lista");
        }
        List<?> valores = (List<?>) valor;
        assertEquals(valores.size(), valoresEsperados.length);
        for (int i = 0; i < valoresEsperados.length; i++) {
            if (!Objects.equals(valoresEsperados[i], valores.get(i))) {
                throw new RuntimeException(
                        "Valores diferentes na posição " + i + ": era esparado " + valoresEsperados[i] + " e veio " + valores.get(i));
            }
        }
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
