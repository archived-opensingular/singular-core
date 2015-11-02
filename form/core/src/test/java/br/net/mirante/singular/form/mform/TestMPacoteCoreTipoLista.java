package br.net.mirante.singular.form.mform;

import java.util.Collection;

import br.net.mirante.singular.form.mform.TestMPacoteCoreTipoLista.TestPacoteListaA.TestTipoListaComCargaInterna;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
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

        MTipoLista<MTipoString, MIString> nomes = pb.createTipoListaOf("nomes", MTipoString.class);

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
        assertEqualsList(lista.getValor(), valoresEsperados);
    }

    @SuppressWarnings("unchecked")
    public void testTipoListaCriacaoOfTipoComposto() {
        MDicionario dicionario = MDicionario.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");

        MTipoLista<MTipoComposto<MIComposto>, MIComposto> tipoPedidos = pb.createTipoListaOfNovoTipoComposto("pedidos", "pedido");
        tipoPedidos.getTipoElementos().addCampoString("descricao");
        tipoPedidos.getTipoElementos().addCampoInteger("qtd");

        MILista<MIComposto> pedidos = (MILista<MIComposto>) tipoPedidos.novaInstancia();
        MIComposto pedido;
        assertException(() -> pedidos.addValor("Paulo"), "Método não suportado");
        pedido = pedidos.addNovo();
        assertFilhos(pedidos, 1);
        assertNotNull(pedido);
        assertEquals(1, pedidos.size());
        assertTrue((pedidos.get(0)) instanceof MIComposto);
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
    }

    @SuppressWarnings("unchecked")
    public void testTipoListaCriacaoOfTipoCompostoTipado() {
        MDicionario dicionario = MDicionario.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");

        MTipoLista<MTipoFormula, MIFormula> tipoFormulas = pb.createTipoListaOf("formulas", MTipoFormula.class);

        MILista<MIFormula> formulas = (MILista<MIFormula>) tipoFormulas.novaInstancia();

        MIFormula formula = formulas.addNovo();
        formula.setSciptJS("XXX");
        assertEquals(MTipoFormula.TipoScript.JS, formula.getTipoScriptEnum());

        assertEquals("XXX", formulas.getValorString("[0].script"));
        assertEquals("JS", formulas.getValorString("[0].tipoScript"));
    }

    public void testeOnCargaTipoDireto() {
        MDicionario dicionario = MDicionario.create();
        TestTipoListaComCargaInterna tipo = dicionario.getTipo(TestTipoListaComCargaInterna.class);
        assertEquals("xxx", tipo.as(AtrBasic.class).getLabel());
        assertEquals((Boolean) true, tipo.isObrigatorio());
    }

    public void testeOnCargaTipoChamadaSubTipo() {
        MDicionario dicionario = MDicionario.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");
        TestTipoListaComCargaInterna tipo = pb.createTipo("arquivo", TestTipoListaComCargaInterna.class);

        assertEquals("xxx", tipo.as(AtrBasic.class).getLabel());
        assertEquals((Boolean) true, tipo.isObrigatorio());
    }

    public static final class TestPacoteListaA extends MPacote {

        protected TestPacoteListaA() {
            super("teste.pacoteListaA");
        }

        @Override
        protected void carregarDefinicoes(PacoteBuilder pb) {
            pb.createTipo(TestTipoListaComCargaInterna.class);
        }

        @MInfoTipo(nome = "TestTipoListaComCargaInterna", pacote = TestPacoteListaA.class)
        public static final class TestTipoListaComCargaInterna extends MTipoLista<MTipoString, MIString> {
            @Override
            protected void onCargaTipo(TipoBuilder tb) {
                super.onCargaTipo(tb);
                withObrigatorio(true);
                as(AtrBasic.class).label("xxx");
            }
        }

    }
}
