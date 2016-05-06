package br.net.mirante.singular.form;

import br.net.mirante.singular.form.TestMPacoteCoreTipoLista.TestPacoteListaA.Pedido;
import br.net.mirante.singular.form.TestMPacoteCoreTipoLista.TestPacoteListaA.TestTipoListaComCargaInterna;
import br.net.mirante.singular.form.TestMPacoteCoreTipoLista.TestPacoteListaA.TipoPedido;
import br.net.mirante.singular.form.type.basic.AtrBasic;
import br.net.mirante.singular.form.type.core.SIInteger;
import br.net.mirante.singular.form.type.core.SIString;
import br.net.mirante.singular.form.type.core.STypeInteger;
import br.net.mirante.singular.form.type.core.STypeString;

import java.util.Collection;

public class TestMPacoteCoreTipoLista extends TestCaseForm {

    @SuppressWarnings("unchecked")
    public void testTipoListaCriacaoOfTipoSimples() {
        SDictionary dicionario = SDictionary.create();
        PackageBuilder pb = dicionario.createNewPackage("teste");

        STypeList<STypeString, SIString> nomes = pb.createListTypeOf("nomes", STypeString.class);

        SIList<SIString> lista = (SIList<SIString>) nomes.newInstance();
        lista.addValue("Paulo");
        assertLista(lista, new String[] { "Paulo" });
        lista.addValue("Joao");
        assertLista(lista, new String[] { "Paulo", "Joao" });
        lista.addValue("Maria");
        assertLista(lista, new String[] { "Paulo", "Joao", "Maria" });

        testCaminho(lista, null, null);
        assertEquals(lista.getValue("[1]"), "Joao");
        assertEquals(lista.indexOf(lista.get(1)), 1);
        testCaminho(lista, "[1]", "[1]");
        testCaminho(lista.getField("[1]"), null, "[1]");

        lista.remove(1);
        assertLista(lista, new String[] { "Paulo", "Maria" });
        assertException(() -> lista.remove(10), IndexOutOfBoundsException.class);

        SIList<SIInteger> listaInt = (SIList<SIInteger>) dicionario.getType(STypeInteger.class).newList();
        listaInt.addValue(10);
        assertLista(listaInt, new Integer[] { 10 });
        listaInt.addValue("20");
        assertLista(listaInt, new Integer[] { 10, 20 });
        assertException(() -> listaInt.addValue("XX"), "não consegue converter");

        assertEquals(lista.getValue("[0]"), "Paulo");
        assertEquals(listaInt.getValue("[1]"), 20);
        assertException(() -> listaInt.getValue("[20]"), IndexOutOfBoundsException.class);

    }

    private static void assertLista(SIList<?> lista, Object[] valoresEsperados) {
        assertEqualsList(lista.getValue(), valoresEsperados);
    }

    public void testTipoListaCriacaoOfTipoComposto() {
        SDictionary dicionario = SDictionary.create();
        PackageBuilder pb = dicionario.createNewPackage("teste");

        STypeList<STypeComposite<SIComposite>, SIComposite> tipoPedidos = pb.createListOfNewCompositeType("pedidos", "pedido");
        tipoPedidos.getElementsType().addFieldString("descricao");
        tipoPedidos.getElementsType().addFieldInteger("qtd");

        SIList<SIComposite> pedidos = (SIList<SIComposite>) tipoPedidos.newInstance();
        SIComposite pedido;
        assertException(() -> pedidos.addValue("Paulo"), "SIComposite só suporta valores de mesmo tipo");
        pedido = pedidos.addNew();
        assertFilhos(pedidos, 1);
        assertNotNull(pedido);
        assertEquals(1, pedidos.size());
        assertTrue((pedidos.get(0)) instanceof SIComposite);
        assertTrue((pedidos.getValueAt(0)) instanceof Collection);

        assertException(() -> pedidos.get(10), IndexOutOfBoundsException.class);
        assertException(() -> pedidos.getValueAt(10), IndexOutOfBoundsException.class);

        pedido.setValue("descricao", "bola");
        pedido.setValue("qtd", 20);
        assertFilhos(pedidos, 3);

        pedido = pedidos.addNew();
        pedido.setValue("descricao", "rede");
        pedido.setValue("qtd", -10);

        assertException(() -> pedidos.getValueAt(10), IndexOutOfBoundsException.class);

        assertEquals(pedidos.getValue("[0].descricao"), "bola");
        assertEquals(pedidos.getValue("[0].qtd"), 20);

        testAtribuicao(pedidos, "[1].descricao", "rede2", 6);
        testAtribuicao(pedidos, "[1].qtd", 20, 6);
        assertException(() -> pedidos.setValue("[1].marca", 10), "Não é um campo definido");

        testCaminho(pedidos, null, null);
        testCaminho(pedidos, "[0]", "[0]");
        testCaminho(pedidos, "[0].descricao", "[0].descricao");
        testCaminho(pedidos.getField("[0]"), null, "[0]");
        testCaminho(pedidos.getField("[0]"), "qtd", "[0].qtd");
        testCaminho(pedidos.getField("[0].qtd"), null, "[0].qtd");
    }

    public void testTipoListaCriacaoOfTipoCompostoTipado() {
        SDictionary dicionario = SDictionary.create();
        PackageBuilder pb = dicionario.createNewPackage("teste");

        STypeList<TipoPedido, Pedido> tipoPedido = pb.createListTypeOf("formulas", TipoPedido.class);

        SIList<Pedido> pedidos = (SIList<Pedido>) tipoPedido.newInstance();

        Pedido pedido = pedidos.addNew();
        pedido.setValue("id", "1");
        pedido.setValue("nome", "arroz");

        assertEquals("1", pedido.getValue("id"));
        assertEquals("1", pedidos.getValueString("[0].id"));
        assertEquals("arroz", pedidos.getValueString("[0].nome"));
    }

    public void testeOnCargaTipoDireto() {
        SDictionary dicionario = SDictionary.create();
        TestTipoListaComCargaInterna tipo = dicionario.getType(TestTipoListaComCargaInterna.class);
        assertEquals("xxx", tipo.as(AtrBasic.class).getLabel());
        assertEquals((Boolean) true, tipo.isRequired());
    }

    public void testeOnCargaTipoChamadaSubTipo() {
        SDictionary dicionario = SDictionary.create();
        PackageBuilder pb = dicionario.createNewPackage("teste");
        TestTipoListaComCargaInterna tipo = pb.createType("arquivo", TestTipoListaComCargaInterna.class);

        assertEquals("xxx", tipo.as(AtrBasic.class).getLabel());
        assertEquals((Boolean) true, tipo.isRequired());
    }

    public static final class TestPacoteListaA extends SPackage {

        protected TestPacoteListaA() {
            super("teste.pacoteListaA");
        }

        @Override
        protected void carregarDefinicoes(PackageBuilder pb) {
            pb.createType(TestTipoListaComCargaInterna.class);
            pb.createType(TipoPedido.class);
        }

        @SInfoType(spackage = TestPacoteListaA.class)
        public static final class TestTipoListaComCargaInterna extends STypeList<STypeString, SIString> {
            @Override
            protected void onLoadType(TypeBuilder tb) {
                withRequired(true);
                as(AtrBasic.class).label("xxx");
            }
        }

        @SInfoType(spackage = TestPacoteListaA.class)
        public static final class TipoPedido extends STypeComposite<Pedido> {
            public TipoPedido() {
                super(Pedido.class);
            }
            @Override
            protected void onLoadType(TypeBuilder tb) {
                addFieldString("id");
                addFieldString("nome");
            }
        }

        public static final class Pedido extends SIComposite {
        }

    }
}
