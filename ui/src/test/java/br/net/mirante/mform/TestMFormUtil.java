package br.net.mirante.mform;

import junit.framework.TestCase;
import org.junit.Assert;

public class TestMFormUtil extends TestCase {

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
}
