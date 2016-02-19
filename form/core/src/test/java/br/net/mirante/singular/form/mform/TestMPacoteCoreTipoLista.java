package br.net.mirante.singular.form.mform;

import java.util.Collection;

import br.net.mirante.singular.form.mform.TestMPacoteCoreTipoLista.TestPacoteListaA.TestTipoListaComCargaInterna;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.SIFormula;
import br.net.mirante.singular.form.mform.core.SIInteger;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeFormula;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;

public class TestMPacoteCoreTipoLista extends TestCaseForm {

    @SuppressWarnings("unchecked")
    public void testTipoListaCriacaoOfTipoSimples() {
        SDictionary dicionario = SDictionary.create();
        PackageBuilder pb = dicionario.createNewPackage("teste");

        STypeLista<STypeString, SIString> nomes = pb.createTipoListaOf("nomes", STypeString.class);

        SList<SIString> lista = (SList<SIString>) nomes.novaInstancia();
        lista.addValor("Paulo");
        assertLista(lista, new String[] { "Paulo" });
        lista.addValor("Joao");
        assertLista(lista, new String[] { "Paulo", "Joao" });
        lista.addValor("Maria");
        assertLista(lista, new String[] { "Paulo", "Joao", "Maria" });

        testCaminho(lista, null, null);
        assertEquals(lista.getValor("[1]"), "Joao");
        assertEquals(lista.indexOf(lista.get(1)), 1);
        testCaminho(lista, "[1]", "[1]");
        testCaminho(lista.getCampo("[1]"), null, "[1]");

        lista.remove(1);
        assertLista(lista, new String[] { "Paulo", "Maria" });
        assertException(() -> lista.remove(10), IndexOutOfBoundsException.class);

        SList<SIInteger> listaInt = (SList<SIInteger>) dicionario.getType(STypeInteger.class).novaLista();
        listaInt.addValor(10);
        assertLista(listaInt, new Integer[] { 10 });
        listaInt.addValor("20");
        assertLista(listaInt, new Integer[] { 10, 20 });
        assertException(() -> listaInt.addValor("XX"), "não consegue converter");

        assertEquals(lista.getValor("[0]"), "Paulo");
        assertEquals(listaInt.getValor("[1]"), 20);
        assertException(() -> listaInt.getValor("[20]"), IndexOutOfBoundsException.class);

    }

    private static void assertLista(SList<?> lista, Object[] valoresEsperados) {
        assertEqualsList(lista.getValue(), valoresEsperados);
    }

    public void testTipoListaCriacaoOfTipoComposto() {
        SDictionary dicionario = SDictionary.create();
        PackageBuilder pb = dicionario.createNewPackage("teste");

        STypeLista<STypeComposite<SIComposite>, SIComposite> tipoPedidos = pb.createTipoListaOfNovoTipoComposto("pedidos", "pedido");
        tipoPedidos.getTipoElementos().addCampoString("descricao");
        tipoPedidos.getTipoElementos().addCampoInteger("qtd");

        SList<SIComposite> pedidos = (SList<SIComposite>) tipoPedidos.novaInstancia();
        SIComposite pedido;
        assertException(() -> pedidos.addValor("Paulo"), "SIComposite só suporta valores de mesmo tipo");
        pedido = pedidos.addNovo();
        assertFilhos(pedidos, 1);
        assertNotNull(pedido);
        assertEquals(1, pedidos.size());
        assertTrue((pedidos.get(0)) instanceof SIComposite);
        assertTrue((pedidos.getValorAt(0)) instanceof Collection);

        assertException(() -> pedidos.get(10), IndexOutOfBoundsException.class);
        assertException(() -> pedidos.getValorAt(10), IndexOutOfBoundsException.class);

        pedido.setValor("descricao", "bola");
        pedido.setValor("qtd", 20);
        assertFilhos(pedidos, 3);

        pedido = pedidos.addNovo();
        pedido.setValor("descricao", "rede");
        pedido.setValor("qtd", -10);

        assertException(() -> pedidos.getValorAt(10), IndexOutOfBoundsException.class);

        assertEquals(pedidos.getValor("[0].descricao"), "bola");
        assertEquals(pedidos.getValor("[0].qtd"), 20);

        testAtribuicao(pedidos, "[1].descricao", "rede2", 6);
        testAtribuicao(pedidos, "[1].qtd", 20, 6);
        assertException(() -> pedidos.setValor("[1].marca", 10), "Não é um campo definido");

        testCaminho(pedidos, null, null);
        testCaminho(pedidos, "[0]", "[0]");
        testCaminho(pedidos, "[0].descricao", "[0].descricao");
        testCaminho(pedidos.getCampo("[0]"), null, "[0]");
        testCaminho(pedidos.getCampo("[0]"), "qtd", "[0].qtd");
        testCaminho(pedidos.getCampo("[0].qtd"), null, "[0].qtd");
    }

    public void testTipoListaCriacaoOfTipoCompostoTipado() {
        SDictionary dicionario = SDictionary.create();
        PackageBuilder pb = dicionario.createNewPackage("teste");

        STypeLista<STypeFormula, SIFormula> tipoFormulas = pb.createTipoListaOf("formulas", STypeFormula.class);

        SList<SIFormula> formulas = (SList<SIFormula>) tipoFormulas.novaInstancia();

        SIFormula formula = formulas.addNovo();
        formula.setSciptJS("XXX");
        assertEquals(STypeFormula.TipoScript.JS, formula.getTipoScriptEnum());

        assertEquals("XXX", formulas.getValorString("[0].script"));
        assertEquals("JS", formulas.getValorString("[0].tipoScript"));
    }

    public void testeOnCargaTipoDireto() {
        SDictionary dicionario = SDictionary.create();
        TestTipoListaComCargaInterna tipo = dicionario.getType(TestTipoListaComCargaInterna.class);
        assertEquals("xxx", tipo.as(AtrBasic.class).getLabel());
        assertEquals((Boolean) true, tipo.isObrigatorio());
    }

    public void testeOnCargaTipoChamadaSubTipo() {
        SDictionary dicionario = SDictionary.create();
        PackageBuilder pb = dicionario.createNewPackage("teste");
        TestTipoListaComCargaInterna tipo = pb.createTipo("arquivo", TestTipoListaComCargaInterna.class);

        assertEquals("xxx", tipo.as(AtrBasic.class).getLabel());
        assertEquals((Boolean) true, tipo.isObrigatorio());
    }

    public static final class TestPacoteListaA extends SPackage {

        protected TestPacoteListaA() {
            super("teste.pacoteListaA");
        }

        @Override
        protected void carregarDefinicoes(PackageBuilder pb) {
            pb.createTipo(TestTipoListaComCargaInterna.class);
        }

        @MInfoTipo(nome = "TestTipoListaComCargaInterna", pacote = TestPacoteListaA.class)
        public static final class TestTipoListaComCargaInterna extends STypeLista<STypeString, SIString> {
            @Override
            protected void onLoadType(TypeBuilder tb) {
                withObrigatorio(true);
                as(AtrBasic.class).label("xxx");
            }
        }

    }
}
