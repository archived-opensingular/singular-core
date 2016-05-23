package br.net.mirante.singular.form;

import br.net.mirante.singular.form.TestMPacoteCoreTipoLista.TestPackageWithCircularReference.TypeTestTree;
import br.net.mirante.singular.form.TestMPacoteCoreTipoLista.TestPacoteListaA.Pedido;
import br.net.mirante.singular.form.TestMPacoteCoreTipoLista.TestPacoteListaA.TestTipoListaComCargaInterna;
import br.net.mirante.singular.form.TestMPacoteCoreTipoLista.TestPacoteListaA.TipoPedido;
import br.net.mirante.singular.form.type.basic.AtrBasic;
import br.net.mirante.singular.form.type.basic.SPackageBasic;
import br.net.mirante.singular.form.type.core.*;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static br.net.mirante.singular.form.AssertionsSForm.assertType;
import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class TestMPacoteCoreTipoLista extends TestCaseForm {

    public static final String INDICE_INVALIDO = "índice inválido";

    public TestMPacoteCoreTipoLista(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTipoListaCriacaoOfTipoSimples() {
        PackageBuilder pb = createTestDictionary().createNewPackage("teste");

        STypeList<STypeString, SIString> nomes = pb.createListTypeOf("nomes", STypeString.class);

        SIList<SIString> lista = (SIList<SIString>) nomes.newInstance();
        lista.addValue("Paulo");
        assertLista(lista, new String[]{"Paulo"});
        lista.addValue("Joao");
        assertLista(lista, new String[]{"Paulo", "Joao"});
        lista.addValue("Maria");
        assertLista(lista, new String[]{"Paulo", "Joao", "Maria"});

        testCaminho(lista, null, null);
        assertEquals(lista.getValue("[1]"), "Joao");
        assertEquals(lista.indexOf(lista.get(1)), 1);
        testCaminho(lista, "[1]", "[1]");
        testCaminho(lista.getField("[1]"), null, "[1]");

        lista.remove(1);
        assertLista(lista, new String[]{"Paulo", "Maria"});
        assertException(() -> lista.remove(10), INDICE_INVALIDO);

        SIList<SIInteger> listaInt = (SIList<SIInteger>) pb.getDictionary().getType(STypeInteger.class).newList();
        listaInt.addValue(10);
        assertLista(listaInt, new Integer[] { 10 });
        listaInt.addValue("20");
        assertLista(listaInt, new Integer[] { 10, 20 });
        assertException(() -> listaInt.addValue("XX"), "não consegue converter");

        assertEquals(lista.getValue("[0]"), "Paulo");
        assertEquals(listaInt.getValue("[1]"), 20);
        assertException(() -> listaInt.getValue("[20]"), INDICE_INVALIDO);

    }

    private static void assertLista(SIList<?> lista, Object[] valoresEsperados) {
        assertEqualsList(lista.getValue(), valoresEsperados);
    }

    @Test
    public void testTipoListaCriacaoOfTipoComposto() {
        PackageBuilder pb = createTestDictionary().createNewPackage("teste");

        STypeList<STypeComposite<SIComposite>, SIComposite> tipoPedidos = pb.createListOfNewCompositeType("pedidos",
                "pedido");
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

        assertException(() -> pedidos.get(10), INDICE_INVALIDO);
        assertException(() -> pedidos.getValueAt(10), INDICE_INVALIDO);

        pedido.setValue("descricao", "bola");
        pedido.setValue("qtd", 20);
        assertFilhos(pedidos, 3);

        pedido = pedidos.addNew();
        pedido.setValue("descricao", "rede");
        pedido.setValue("qtd", -10);

        assertException(() -> pedidos.getValueAt(10), INDICE_INVALIDO);

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

    @Test
    public void testTipoListaCriacaoOfTipoCompostoTipado() {
        PackageBuilder pb = createTestDictionary().createNewPackage("teste");

        STypeList<TipoPedido, Pedido> tipoPedido = pb.createListTypeOf("formulas", TipoPedido.class);
        assertType(tipoPedido.getElementsType().id).isNotNull();
        assertType(tipoPedido.getElementsType().nome).isNotNull();
        assertType(tipoPedido.getElementsType().embalagem).isNotNull().isComposite(2);
        assertType(tipoPedido.getElementsType()).isComposite(3);

        assertType(tipoPedido.getElementsType().embalagem.descricao).isNotNull();
        assertType(tipoPedido.getElementsType().embalagem.especial).isNotNull();

        SIList<Pedido> pedidos = (SIList<Pedido>) tipoPedido.newInstance();

        Pedido pedido = pedidos.addNew();
        pedido.setValue("id", "1");
        pedido.setValue("nome", "arroz");

        assertEquals("1", pedido.getValue("id"));
        assertEquals("1", pedidos.getValueString("[0].id"));
        assertEquals("arroz", pedidos.getValueString("[0].nome"));
    }

    @Test
    public void testChangeInAttributeOfTheListElementTypeMustNotChangeTheOriginalCompositeType() {
        //-----------------------------------------------------------------
        //Teste 1 - Criando a lista direto no package e usando um composite criado
        PackageBuilder pb = createTestDictionary().createNewPackage("teste");
        STypeComposite<SIComposite> original = pb.createCompositeType("original");
        original.addFieldString("s1");
        STypeList<?,?> list = pb.createListTypeOf("list", original);
        testChangeInAttributeOfTheListElementTypeMustNotChangeTheOriginalCompositeType(original, list);

        //-----------------------------------------------------------------
        //Teste 2 - Criando a lista direto no package e usando SType
        pb = createTestDictionary().createNewPackage("teste");
        original = pb.getType(STypeComposite.class);
        list = pb.createListOfNewCompositeType("list", "item");
        testChangeInAttributeOfTheListElementTypeMustNotChangeTheOriginalCompositeType(original, list);

        //-----------------------------------------------------------------
        //Teste 3 - Criando a lista direto no package e usando SType pela referencia de classe
        pb = createTestDictionary().createNewPackage("teste");
        original = pb.getType(STypeComposite.class);
        list = pb.createListTypeOf("list", STypeComposite.class);
        testChangeInAttributeOfTheListElementTypeMustNotChangeTheOriginalCompositeType(original, list);

        //-----------------------------------------------------------------
        //Teste 4 - Criando a lista de um tipo e usando um composite criado
        pb = createTestDictionary().createNewPackage("teste");
        original = pb.createCompositeType("original");
        original.addFieldString("s1");
        STypeComposite<SIComposite> parent = pb.createCompositeType("parent");
        list = parent.addFieldListOf("list", original);
        testChangeInAttributeOfTheListElementTypeMustNotChangeTheOriginalCompositeType(original, list);

        //-----------------------------------------------------------------
        //Teste 5 - Criando a lista de um tipo e usando SType
        pb = createTestDictionary().createNewPackage("teste");
        original = pb.getType(STypeComposite.class);
        parent = pb.createCompositeType("parent");
        list = parent.addFieldListOfComposite("list", "item");
        testChangeInAttributeOfTheListElementTypeMustNotChangeTheOriginalCompositeType(original, list);

        //-----------------------------------------------------------------
        //Teste 6 - Criando a lista de um tipo e usando SType pela referencia de classe
        pb = createTestDictionary().createNewPackage("teste");
        original = pb.getType(STypeComposite.class);
        parent = pb.createCompositeType("parent");
        list = parent.addFieldListOf("list", STypeComposite.class);
        testChangeInAttributeOfTheListElementTypeMustNotChangeTheOriginalCompositeType(original, list);
    }

    public void testChangeInAttributeOfTheListElementTypeMustNotChangeTheOriginalCompositeType(
            STypeComposite<?> original, STypeList<?, ?> list) {
        ((STypeComposite) list.getElementsType()).addFieldString("s2");

        testChangeInAttributeOfTheListElementTypeMustNotChangeTheOriginalType(original, list);
        assertType(list.getElementsType()).isComposite(original.getFields().size()+1);
    }

    private void testChangeInAttributeOfTheListElementTypeMustNotChangeTheOriginalType(SType<?> original,
            STypeList<?, ?> list) {
        assertNotSame(original, list.getElementsType());
        assertSame(original, list.getElementsType().getSuperType());

        assertType(original).isAttribute(SPackageBasic.ATR_LABEL, null);
        assertType(list.getElementsType()).isAttribute(SPackageBasic.ATR_LABEL, null);
        list.getElementsType().asAtr().label("xxx");
        assertType(original).isAttribute(SPackageBasic.ATR_LABEL, null);
        assertType(list.getElementsType()).isAttribute(SPackageBasic.ATR_LABEL, "xxx");

        assertType(original).isAttribute(SPackageBasic.ATR_SUBTITLE, null);
        assertType(list.getElementsType()).isAttribute(SPackageBasic.ATR_SUBTITLE, null);
        original.asAtr().subtitle("yyy");
        assertType(original).isAttribute(SPackageBasic.ATR_SUBTITLE, "yyy");
        assertType(list.getElementsType()).isAttribute(SPackageBasic.ATR_SUBTITLE, "yyy");
   }

    @Test
    @Ignore("Desativado devido a problema estrutural. Voltar quando tiver sido resolvido")
    public void testChangeInAttributeOfTheListElementTypeMustNotChangeTheOriginalTypeByClassPedido() {
        PackageBuilder pb = createTestDictionary().createNewPackage("teste");
        TipoPedido pedido = pb.getType(TipoPedido.class);
        STypeList<TipoPedido, Pedido> list = pb.createListTypeOf("list", pedido);
        testChangeInAttributeOfTheListElementTypeMustNotChangeTheOriginalType(pedido, list);
        verificarVariaveisPedidoCriada(pedido);
        verificarVariaveisPedidoCriada(list.getElementsType());

        pb = createTestDictionary().createNewPackage("teste");
        pedido = pb.getType(TipoPedido.class);
        list = pb.createListTypeOf("list", TipoPedido.class);
        testChangeInAttributeOfTheListElementTypeMustNotChangeTheOriginalType(pedido, list);
        verificarVariaveisPedidoCriada(pedido);
        verificarVariaveisPedidoCriada(list.getElementsType());
    }

    private void verificarVariaveisPedidoCriada(STypeList<?,?> list) {
        verificarVariaveisPedidoCriada((TipoPedido) list.getElementsType());
    }

    private void verificarVariaveisPedidoCriada(TipoPedido pedido) {
        assertNotNull(pedido.id);
        assertNotNull(pedido.embalagem);
        assertNotNull(pedido.embalagem.descricao);
    }

    @Test
    @Ignore("Desativado devido a problema estrutural. Voltar quando tiver sido resolvido")
    public void testChangeInAttributeOfTheListElementTypeMustNotChangeTheOriginalTypeByClassPedidoTwoTimes() {
        PackageBuilder pb = createTestDictionary().createNewPackage("teste");
        TipoPedido pedido = pb.getType(TipoPedido.class);
        STypeComposite<SIComposite> composite = pb.createCompositeType("block");
        STypeList<TipoPedido, Pedido> list1 = composite.addFieldListOf("list1", pedido);
        STypeList<TipoPedido, Pedido> list2 = composite.addFieldListOf("list2", pedido);
        testChangeInAttributeOfTheListElementTypeMustNotChangeTheOriginalTypeByClassPedido(pedido, list1, list2);
        verificarVariaveisPedidoCriada(pedido);
        verificarVariaveisPedidoCriada(list1.getElementsType());
        verificarVariaveisPedidoCriada(list2.getElementsType());

        pb = createTestDictionary().createNewPackage("teste");
        pedido = pb.getType(TipoPedido.class);
        composite = pb.createCompositeType("block");
        list1 = composite.addFieldListOf("list1", TipoPedido.class);
        list2 = composite.addFieldListOf("list2", TipoPedido.class);
        testChangeInAttributeOfTheListElementTypeMustNotChangeTheOriginalTypeByClassPedido(pedido, list1, list2);
        verificarVariaveisPedidoCriada(pedido);
        verificarVariaveisPedidoCriada(list1.getElementsType());
        verificarVariaveisPedidoCriada(list2.getElementsType());
    }

    private void testChangeInAttributeOfTheListElementTypeMustNotChangeTheOriginalTypeByClassPedido(TipoPedido pedido, STypeList<TipoPedido, Pedido> list1, STypeList<TipoPedido, Pedido> list2) {
        assertNotSame(list1.getElementsType(), list1.getElementsType());
        assertNotSame(pedido, list1.getElementsType());
        assertSame(pedido, list1.getElementsType().getSuperType());
        assertSame(pedido, list2.getElementsType().getSuperType());

        assertType(pedido).isAttribute(SPackageBasic.ATR_LABEL, null);
        assertType(list1.getElementsType()).isAttribute(SPackageBasic.ATR_LABEL, null);
        assertType(list2.getElementsType()).isAttribute(SPackageBasic.ATR_LABEL, null);
        list1.getElementsType().asAtr().label("xxx");
        assertType(pedido).isAttribute(SPackageBasic.ATR_LABEL, null);
        assertType(list1.getElementsType()).isAttribute(SPackageBasic.ATR_LABEL, "xxx");
        assertType(list2.getElementsType()).isAttribute(SPackageBasic.ATR_LABEL, null);

        assertType(pedido).isAttribute(SPackageBasic.ATR_SUBTITLE, null);
        assertType(list1.getElementsType()).isAttribute(SPackageBasic.ATR_SUBTITLE, null);
        assertType(list2.getElementsType()).isAttribute(SPackageBasic.ATR_SUBTITLE, null);
        pedido.asAtr().subtitle("yyy");
        assertType(pedido).isAttribute(SPackageBasic.ATR_SUBTITLE, "yyy");
        assertType(list1.getElementsType()).isAttribute(SPackageBasic.ATR_SUBTITLE, "yyy");
        assertType(list2.getElementsType()).isAttribute(SPackageBasic.ATR_SUBTITLE, "yyy");
    }

    @Test
    public void testeOnCargaTipoDireto() {
        TestTipoListaComCargaInterna tipo = createTestDictionary().getType(TestTipoListaComCargaInterna.class);
        assertEquals("xxx", tipo.as(AtrBasic.class).getLabel());
        assertEquals((Boolean) true, tipo.isRequired());
    }

    @Test
    public void testeOnCargaTipoChamadaSubTipo() {
        PackageBuilder pb = createTestDictionary().createNewPackage("teste");
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
            pb.createType(TipoEmbalagem.class);
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
        public static final class TipoEmbalagem extends STypeComposite<SIComposite> {

            public STypeString descricao;
            public STypeBoolean especial;

            @Override
            protected void onLoadType(TypeBuilder tb) {
                descricao = addFieldString("descricao");
                especial = addFieldBoolean("especial");
            }
        }

        @SInfoType(spackage = TestPacoteListaA.class)
        public static final class TipoPedido extends STypeComposite<Pedido> {

            public STypeString id;
            public STypeString nome;
            public TipoEmbalagem embalagem;

            public TipoPedido() {
                super(Pedido.class);
            }
            @Override
            protected void onLoadType(TypeBuilder tb) {
                id = addFieldString("id");
                nome = addFieldString("nome");
                embalagem = addField("embalagem", TipoEmbalagem.class);
            }
        }

        public static final class Pedido extends SIComposite {
        }

    }

    @Test
    public void testWrongIndexHandling() {
        testWrongIndexHandling(0);
        testWrongIndexHandling(1);
        testWrongIndexHandling(2);
    }

    private void testWrongIndexHandling(int size) {
        SIList<SIInteger> list = createIntList(size);
        assertEquals((size == 0), list.isEmpty());
        assertEquals(size, list.size());

        assertException(() -> list.getValueAt(-1), INDICE_INVALIDO);
        assertException(() -> list.get(-1), INDICE_INVALIDO);
        assertException(() -> list.getField("[-1]"), " inválido");
        assertException(() -> list.getFieldOpt("[-1]"), " inválido");
        for(int i = 0; i < size; i++) {
            assertNotNull(list.get(i));
            assertEquals(i, list.get(i).getValue());
            assertEquals(i, list.getField("[" + i+ "]").getValue());
            assertEquals(i, list.getFieldOpt("[" + i+ "]").get().getValue());
            assertEquals(i, list.getValueAt(i));
        }
        assertException(() -> list.getValueAt(size), INDICE_INVALIDO);
        assertException(() -> list.get(size), INDICE_INVALIDO);
        assertException(() -> list.getField("[" + size+ "]"), INDICE_INVALIDO);
        assertFalse(list.getFieldOpt("[" + size+ "]").isPresent());


        assertEquals((size == 0), list.isEmpty());
        assertEquals(size, list.size());
    }

    private SIList<SIInteger> createIntList(int size) {
        PackageBuilder pb = createTestDictionary().createNewPackage("teste");
        SIList<SIInteger> list = pb.createListTypeOf("numbers", STypeInteger.class).newInstance();

        for(int i = 0; i < size; i++) {
            list.addValue(i);
        }
        return list;
    }

    @Test
    public void testCircularReferenceWithListAndCompositePartial() {
        //TODO apagar esse teste e ativar o debaixo quando tudo estiver funcionado
        SDictionary dictionary = createTestDictionary();
        if (!dictionary.getDictionaryConfig().isExtendListElementType()) {
            realTestCircularReferenceWithListAndComposite();
        }
    }

    @Test
    @Ignore("Desativado até todos os cenários estiverem funcionando")
    public void testCircularReferenceWithListAndCompositeFull() {
        realTestCircularReferenceWithListAndComposite();
    }

    private void realTestCircularReferenceWithListAndComposite() {
        TypeTestTree stype = createTestDictionary().getType(TypeTestTree.class);
        AssertionsSType atype = assertType(stype);
        atype.isComposite(2);
        atype.isString("name");
        atype.isList("childrens");
        atype.listElementType("childrens").isComposite(2);
        assertThat((Object) stype.name).isNotNull().isSameAs(stype.getField("name"));
        assertThat((Object) stype.childrens).isNotNull().isSameAs(stype.getField("childrens"));
    }

    @SInfoPackage(name = "circular")
    public static final class TestPackageWithCircularReference extends SPackage {

        @Override
        protected void carregarDefinicoes(PackageBuilder pb) {
            pb.createType(TypeTestTree.class);
        }

        @SInfoType(name="item", spackage = TestPackageWithCircularReference.class)
        public static final class TypeTestTree extends STypeComposite<SIComposite> {

            public STypeString name;
            public STypeList<TypeTestTree, SIComposite> childrens;

            @Override
            protected void onLoadType(TypeBuilder tb) {
                name = addFieldString("name");
                childrens = addFieldListOf("childrens", TypeTestTree.class);
            }
        }
    }
}
