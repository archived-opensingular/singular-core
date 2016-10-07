package br.net.mirante.singular.form;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.function.Supplier;

@RunWith(Parameterized.class)
public class TestLeitorPath extends TestCaseForm {

    public TestLeitorPath(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test
    public void testGeral() {
        assertPath("a", "a");
        assertPath("a.b", "a", "b");
        assertPath("a.b.c", "a", "b", "c");
        assertPathException(".a.b", "inválido na posição 0");
        assertPathException("a..b", "inválido na posição 2");
        assertPathException("a.b..", "inválido na posição 4");

        assertPathException("a%%", "0 : Não é um nome de campo válido");
        assertPathException(" a", "0 : Não é um nome de campo válido");

        assertPath("[0]", 0);
        assertPath("[0100]", 100);
        assertPath("[0][1]", 0, 1);
        assertPath("a[0].b.c[1]", "a", 0, "b", "c", 1);

        assertPathException("[]", "inválido na posição 0");
        assertPathException("[a]", "inválido na posição 1");
        assertPathException("[ 0]", "inválido na posição 1");
        assertPathException("[1 0]", "inválido na posição 2");
        assertPathException("[1]b", "inválido na posição 3");
        assertPathException("a.[0]", "inválido na posição 2");
        assertPathException("a.[0", "inválido na posição 2");
    }

    private static void assertPathException(String path, String trechoMsgEsperada) {
        assertException(() -> {
            PathReader leitor = new PathReader(path);
            while (!leitor.isEmpty()) {
                leitor = leitor.next();
            }
        } , trechoMsgEsperada);

    }

    private static void assertPath(String path, Object... resultadoEsperado) {
        PathReader leitor = new PathReader(path);

        for (int i = 0; i < resultadoEsperado.length; i++) {
            Object esperado = resultadoEsperado[i];
            if (leitor.isEmpty()) {
                fail("O leitor terminou antes do esperado. Faltou o resultado de indice [" + i + "]=" + esperado);
            }
            if (esperado instanceof Integer) {
                assertTrue(leitor.isIndex());
                assertEquals(esperado, leitor.getIndex());
            } else {
                assertFalse(leitor.isIndex());
                assertEquals(esperado, leitor.getTrecho());
            }
            assertEquals((i + 1 == resultadoEsperado.length), leitor.isLast());
            leitor = leitor.next();
        }
        if (!leitor.isEmpty()) {
            fail("Ainda há item no leitor para ler: " + leitor.getTrecho());
        }
        final PathReader leitor2 = leitor;
        assertException(() -> leitor2.isIndex(), "Leitura já está no fim");
        assertException(() -> leitor2.getTrecho(), "Leitura já está no fim");
        assertException(() -> leitor2.next(), "Leitura já está no fim");

    }
}
