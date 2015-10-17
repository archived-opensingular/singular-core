package br.net.mirante.singular.form.mform;

import java.util.Collection;

import br.net.mirante.singular.form.mform.core.MIFormula;
import br.net.mirante.singular.form.mform.core.MIInteger;
import br.net.mirante.singular.form.mform.core.MIString;
import br.net.mirante.singular.form.mform.core.MTipoFormula;
import br.net.mirante.singular.form.mform.core.MTipoInteger;
import br.net.mirante.singular.form.mform.core.MTipoString;

public class TestMPacoteCoreTipoLista extends TestCaseForm {

    @SuppressWarnings("unchecked")
    public void testTipoListaCriacaoOfTipoSimples() {
        MDicionario dicionario = MDicionario.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");

        MTipoLista<MTipoString> nomes = pb.createTipoListaOf("nomes", MTipoString.class);

        MILista<MIString> lista = (MILista<MIString>) nomes.novaInstancia();
        lista.addValor("Paulo");
        assertLista(lista, new String[] { "Paulo" });
        lista.addValor("Joao");
        assertLista(lista, new String[] { "Paulo", "Joao" });
        lista.addValor("Maria");
        assertLista(lista, new String[] { "Paulo", "Joao", "Maria" });

        lista.remove(1);
        assertLista(lista, new String[] { "Paulo", "Maria" });
        assertException(() -> lista.remove(10), IndexOutOfBoundsException.class);

        assertException(() -> lista.addValor(null), "Não é aceito null");
        assertException(() -> lista.addValor(""), "Não é permitido");
        assertException(() -> lista.addNovo(), "não é um tipo composto");

        MILista<MIInteger> listaInt = (MILista<MIInteger>) dicionario.getTipo(MTipoInteger.class).novaLista();
        listaInt.addValor(10);
        assertLista(listaInt, new Integer[] { 10 });
        listaInt.addValor("20");
        assertLista(listaInt, new Integer[] { 10, 20 });
        assertException(() -> listaInt.addValor("XX"), "não consegue converter");

        assertEquals(lista.getValor("[0]"), "Paulo");
        assertEquals(listaInt.getValor("[1]"), 20);
        assertException(() -> listaInt.getValor("[20]"), IndexOutOfBoundsException.class);

    }

    private static void assertLista(MILista<?> lista, Object[] valoresEsperados) {
        assertEquals(valoresEsperados.length, lista.size());
        for (int i = 0; i < valoresEsperados.length; i++) {
            assertEquals(valoresEsperados[i], lista.getValorAt(i));
        }
    }

    @SuppressWarnings("unchecked")
    public void testTipoListaCriacaoOfTipoComposto() {
        MDicionario dicionario = MDicionario.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");

        MTipoLista<MTipoComposto<?>> tipoPedidos = pb.createTipoListaOfNovoTipoComposto("pedidos", "pedido");
        tipoPedidos.getTipoElementos().addCampoString("descricao");
        tipoPedidos.getTipoElementos().addCampoInteger("qtd");

        MILista<MIComposto> pedidos = (MILista<MIComposto>) tipoPedidos.novaInstancia();
        MIComposto pedido;
        assertException(() -> pedidos.addValor("Paulo"), "Método não suportado");
        pedido = pedidos.addNovo();
        assertNotNull(pedido);
        assertEquals(1, pedidos.size());
        assertTrue((pedidos.get(0)) instanceof MIComposto);
        assertTrue((pedidos.getValorAt(0)) instanceof Collection);

        assertException(() -> pedidos.get(10), IndexOutOfBoundsException.class);
        assertException(() -> pedidos.getValorAt(10), IndexOutOfBoundsException.class);

        pedido.setValor("descricao", "bola");
        pedido.setValor("qtd", 20);

        pedido = pedidos.addNovo();
        pedido.setValor("descricao", "rede");
        pedido.setValor("qtd", -10);

        assertException(() -> pedidos.getValorAt(10), IndexOutOfBoundsException.class);

        assertEquals(pedidos.getValor("[0].descricao"), "bola");
        assertEquals(pedidos.getValor("[0].qtd"), 20);

        testAtribuicao(pedidos, "[1].descricao", "rede2");
        testAtribuicao(pedidos, "[1].qtd", 20);
        assertException(() -> pedidos.setValor("[1].marca", 10), "Não é um campo definido");
    }

    @SuppressWarnings("unchecked")
    public void testTipoListaCriacaoOfTipoCompostoTipado() {
        MDicionario dicionario = MDicionario.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");

        MTipoLista<MTipoFormula> tipoFormulas = pb.createTipoListaOf("formulas", MTipoFormula.class);

        MILista<MIFormula> formulas = (MILista<MIFormula>) tipoFormulas.novaInstancia();

        MIFormula formula = formulas.addNovo();
        formula.setSciptJS("XXX");
        assertEquals(MTipoFormula.TipoScript.JS, formula.getTipoScriptEnum());

        assertEquals("XXX", formulas.getValorString("[0].script"));
        assertEquals("JS", formulas.getValorString("[0].tipoScript"));
    }
}
