package org.opensingular.form;

import org.opensingular.form.TestMPacoteCoreTipoLista.TestPackageWithCircularReference.TypeTestPark;
import org.opensingular.form.TestMPacoteCoreTipoLista.TestPackageWithCircularReference.TypeTestTree;
import org.opensingular.form.TestMPacoteCoreTipoLista.TestPacoteListaA.Pedido;
import org.opensingular.form.TestMPacoteCoreTipoLista.TestPacoteListaA.TestTipoListaComCargaInterna;
import org.opensingular.form.TestMPacoteCoreTipoLista.TestPacoteListaA.TipoPedido;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.type.core.SIInteger;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeBoolean;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

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
        assertLista(listaInt, new Integer[]{10});
        listaInt.addValue("20");
        assertLista(listaInt, new Integer[]{10, 20});
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
        assertFilhos(pedidos, 0);
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

        STypeList<TipoPedido, Pedido> tipoPedidos = pb.createListTypeOf("formulas", TipoPedido.class);
        TipoPedido tipoPedidoLista = tipoPedidos.getElementsType();
        assertType(tipoPedidoLista).isDirectExtensionOf(TipoPedido.class);

        assertType(tipoPedidoLista.id).isNotNull();
        assertType(tipoPedidoLista.nome).isNotNull();
        assertType(tipoPedidoLista.embalagem).isNotNull().isComposite(2);
        assertType(tipoPedidoLista).isComposite(3);

        assertType(tipoPedidoLista.embalagem.descricao).isNotNull();
        assertType(tipoPedidoLista.embalagem.especial).isNotNull();

        assertType(tipoPedidoLista.id).isDirectExtensionOf(TipoPedido.class, "id");
        assertType(tipoPedidoLista.nome).isDirectExtensionOf(TipoPedido.class, "nome");
        assertType(tipoPedidoLista.embalagem).isExtensionOfParentCompositeFieldReference();
        assertType(tipoPedidoLista.embalagem.descricao).isExtensionOfParentCompositeFieldReference();
        assertType(tipoPedidoLista.embalagem.especial).isExtensionOfParentCompositeFieldReference();

        SIList<Pedido> pedidos = (SIList<Pedido>) tipoPedidos.newInstance();

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
        STypeList<?, ?> list = pb.createListTypeOf("list", original);
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
        assertType(list.getElementsType()).isComposite(original.getFields().size() + 1);
    }

    private void testChangeInAttributeOfTheListElementTypeMustNotChangeTheOriginalType(SType<?> original,
            STypeList<?, ?> list) {
        assertType(list.getElementsType()).isDirectExtensionOf(original);

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
    //@Ignore("Desativado devido a problema estrutural. Voltar quando tiver sido resolvido")
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

    private void verificarVariaveisPedidoCriada(STypeList<?, ?> list) {
        verificarVariaveisPedidoCriada((TipoPedido) list.getElementsType());
    }

    private void verificarVariaveisPedidoCriada(TipoPedido pedido) {
        assertNotNull(pedido.id);
        assertNotNull(pedido.embalagem);
        assertNotNull(pedido.embalagem.descricao);
    }

    @Test
    //@Ignore("Desativado devido a problema estrutural. Voltar quando tiver sido resolvido")
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

    private void testChangeInAttributeOfTheListElementTypeMustNotChangeTheOriginalTypeByClassPedido(TipoPedido pedido,
            STypeList<TipoPedido, Pedido> list1, STypeList<TipoPedido, Pedido> list2) {
        assertType(list1.getElementsType()).isDirectExtensionOf(pedido);
        assertType(list2.getElementsType()).isDirectExtensionOf(pedido).isNotSameAs(list1.getElementsType());

        assertType(pedido).isAttrLabel(null);
        assertType(list1.getElementsType()).isAttrLabel(null);
        assertType(list2.getElementsType()).isAttrLabel(null);
        list1.getElementsType().asAtr().label("xxx");
        assertType(pedido).isAttrLabel(null);
        assertType(list1.getElementsType()).isAttrLabel("xxx");
        assertType(list2.getElementsType()).isAttrLabel(null);

        assertType(pedido).isAttrSubTitle(null);
        assertType(list1.getElementsType()).isAttrSubTitle(null);
        assertType(list2.getElementsType()).isAttrSubTitle(null);
        pedido.asAtr().subtitle("yyy");
        assertType(pedido).isAttrSubTitle("yyy");
        assertType(list1.getElementsType()).isAttrSubTitle("yyy");
        assertType(list2.getElementsType()).isAttrSubTitle("yyy");
    }

    @Test
    public void testeOnCargaTipoDireto() {
        TestTipoListaComCargaInterna tipo = createTestDictionary().getType(TestTipoListaComCargaInterna.class);
        assertEquals("xxx", tipo.asAtr().getLabel());
        assertEquals((Boolean) true, tipo.isRequired());
    }

    @Test
    public void testeOnCargaTipoChamadaSubTipo() {
        PackageBuilder pb = createTestDictionary().createNewPackage("teste");
        TestTipoListaComCargaInterna tipo = pb.createType("arquivo", TestTipoListaComCargaInterna.class);

        assertEquals("xxx", tipo.asAtr().getLabel());
        assertEquals((Boolean) true, tipo.isRequired());
    }

    public static final class TestPacoteListaA extends SPackage {

        protected TestPacoteListaA() {
            super("teste.pacoteListaA");
        }

        @Override
        protected void onLoadPackage(PackageBuilder pb) {
            pb.createType(TestTipoListaComCargaInterna.class);
            pb.createType(TipoEmbalagem.class);
            pb.createType(TipoPedido.class);
        }

        @SInfoType(spackage = TestPacoteListaA.class)
        public static final class TestTipoListaComCargaInterna extends STypeList<STypeString, SIString> {
            @Override
            protected void onLoadType(TypeBuilder tb) {
                withRequired(true);
                asAtr().label("xxx");
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

            public STypeString   id;
            public STypeString   nome;
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
        for (Integer i = 0; i < size; i++) {
            assertNotNull(list.get(i));
            assertEquals(i, list.get(i).getValue());
            assertEquals(i, list.getField("[" + i + "]").getValue());
            assertEquals(i, list.getFieldOpt("[" + i + "]").get().getValue());
            assertEquals(i, list.getValueAt(i));
        }
        assertException(() -> list.getValueAt(size), INDICE_INVALIDO);
        assertException(() -> list.get(size), INDICE_INVALIDO);
        assertException(() -> list.getField("[" + size + "]"), INDICE_INVALIDO);
        assertFalse(list.getFieldOpt("[" + size + "]").isPresent());


        assertEquals((size == 0), list.isEmpty());
        assertEquals(size, list.size());
    }

    private SIList<SIInteger> createIntList(int size) {
        PackageBuilder    pb   = createTestDictionary().createNewPackage("teste");
        SIList<SIInteger> list = pb.createListTypeOf("numbers", STypeInteger.class).newInstance();

        for (int i = 0; i < size; i++) {
            list.addValue(i);
        }
        return list;
    }

    @Test
    public void realTestCircularReferenceWithListAndComposite() {
        TypeTestTree    tTree = createTestDictionary().getType(TypeTestTree.class);
        AssertionsSType aTree = assertType(tTree);
        aTree.isAttrLabel("Tree").isComposite(2);
        aTree.isString("name").isNotRecursiveReference();
        aTree.isList("childrens").isNotRecursiveReference();
        aTree.isNotRecursiveReference();
        aTree.listElementType("childrens").isDirectExtensionOf(tTree).isRecursiveReference();
        aTree.listElementType("childrens").isAttrLabel("SubTree").isComposite(2);
        assertType(tTree.name).isNotNull().isSameAs(tTree.getField("name"));
        assertType(tTree.childrens).isNotNull().isSameAs(tTree.getField("childrens"));
        assertType(((TypeTestTree) aTree.listElementType("childrens").getTarget()).name).isSameAs(tTree.name);
        assertType(((TypeTestTree) aTree.listElementType("childrens").getTarget()).childrens).isSameAs(tTree.childrens);

        testTreeRecursiveInstance(tTree, tTree.name, tTree.childrens);

        TypeTestPark tPark = tTree.getDictionary().getType(TypeTestPark.class);

        testCompositeWithTreeFields(tTree, tTree.name, tTree.childrens, tPark);

        AssertionsSType aPark = assertType(tPark);
        aPark.field("tree").isSameAs(tPark.tree);
        aPark.field("trees").listElementType().isNotSameAs(tPark.tree).isSameAs(tPark.trees.getElementsType());
        aPark.field("trees").listElementType().field("name").isSameAs(tPark.trees.getElementsType().name);
        aPark.field("trees").listElementType().field("childrens").isSameAs(tPark.trees.getElementsType().childrens);
        aPark.field("trees").listElementType().field("childrens").listElementType().isSameAs(
                tPark.trees.getElementsType().childrens.getElementsType());
        aPark.field("trees").listElementType().field("childrens").listElementType().isDirectExtensionOf(
                tTree.childrens.getElementsType());
    }

    @SInfoPackage(name = "circular")
    public static final class TestPackageWithCircularReference extends SPackage {

        @Override
        protected void onLoadPackage(PackageBuilder pb) {
            pb.createType(TypeTestTree.class);
            pb.createType(TypeTestPark.class);
        }

        @SInfoType(name = "tree", spackage = TestPackageWithCircularReference.class)
        public static final class TypeTestTree extends STypeComposite<SIComposite> {

            public STypeString                          name;
            public STypeList<TypeTestTree, SIComposite> childrens;

            @Override
            protected void onLoadType(TypeBuilder tb) {
                name = addFieldString("name");
                childrens = addFieldListOf("childrens", TypeTestTree.class);
                asAtr().label("Tree");
                childrens.getElementsType().asAtr().label("SubTree");
            }
        }

        @SInfoType(name = "park", spackage = TestPackageWithCircularReference.class)
        public static final class TypeTestPark extends STypeComposite<SIComposite> {

            public STypeString                          name;
            public TypeTestTree                         tree;
            public STypeList<TypeTestTree, SIComposite> trees;

            @Override
            protected void onLoadType(TypeBuilder tb) {
                name = addFieldString("name");
                tree = addField("tree", TypeTestTree.class);
                trees = addFieldListOf("trees", TypeTestTree.class);
            }
        }
    }

    @Test
    public void testCircularReferenceWithOutTypeDefinedByClass() {
        PackageBuilder                                      pb             = createTestDictionary().createNewPackage("test");
        STypeComposite<SIComposite>                         tTree          = pb.createCompositeType("tree");
        STypeString                                         tTreeName      = tTree.addFieldString("name");
        STypeList<STypeComposite<SIComposite>, SIComposite> tTreeChildrens = tTree.addFieldListOf("childrens", tTree);
        STypeComposite<SIComposite>                         tSubTree       = tTreeChildrens.getElementsType();
        tTree.asAtr().label("Tree");
        tSubTree.asAtr().label("SubTree");

        AssertionsSType aTree = assertType(tTree);
        aTree.isAttrLabel("Tree").isComposite(2);
        aTree.isString("name").isNotRecursiveReference();
        aTree.isList("childrens").isNotRecursiveReference();
        aTree.isNotRecursiveReference();
        aTree.listElementType("childrens").isDirectExtensionOf(tTree).isRecursiveReference();
        aTree.listElementType("childrens").isAttrLabel("SubTree").isComposite(2);
        aTree.listElementType("childrens").field("name").isSameAs(tTreeName);
        aTree.listElementType("childrens").listElementType("childrens").isSameAs(tSubTree);

        testTreeRecursiveInstance(tTree, tTreeName, tTreeChildrens);

        STypeComposite<SIComposite>                         tPark      = pb.createCompositeType("park");
        STypeString                                         tParkName  = tPark.addFieldString("name");
        STypeComposite<SIComposite>                         tParkTree  = tPark.addField("tree", tTree);
        STypeList<STypeComposite<SIComposite>, SIComposite> tParkTrees = tPark.addFieldListOf("trees", tTree);

        testCompositeWithTreeFields(tTree, tTreeName, tTreeChildrens, tPark);
    }

    private void testCompositeWithTreeFields(STypeComposite<SIComposite> tTree, SType<?> tTreeName,
                                             SType<?> tTreeChildrens2, STypeComposite<SIComposite> tPark) {
        STypeList<STypeComposite<SIComposite>, SIComposite> tTreeChildrens =
                (STypeList<STypeComposite<SIComposite>, SIComposite>) tTreeChildrens2;

        tPark.getField("tree").asAtr().label("parkTree");
        tPark.getLocalType("tree.childrens").asAtr().label("parkSubTree");
        tPark.getLocalType("trees.tree").asAtr().label("parkTree2");
        tPark.getLocalType("trees.tree.childrens").asAtr().label("parkSubTree2");

        AssertionsSType aPark = assertType(tPark);
        aPark.isComposite(3);
        aPark.field("tree").isDirectExtensionOf(tTree);
        aPark.field("trees").listElementType().isDirectExtensionOf(tTree).isNotSameAs(tPark.getField("tree"));
        aPark.field("trees").listElementType().field("name").isDirectExtensionOf(tTreeName);
        aPark.field("trees").listElementType().field("childrens").isDirectExtensionOf(tTreeChildrens);
        aPark.field("trees").listElementType().field("childrens").listElementType().isDirectExtensionOf(
                tTreeChildrens.getElementsType());

        SIComposite iPark = tPark.newInstance();
    }

    private void testTreeRecursiveInstance(STypeComposite<SIComposite> tTree, SType<?> tName, SType<?> tChildrens2) {
        STypeList<STypeComposite<SIComposite>, SIComposite> tChildrens =
                (STypeList<STypeComposite<SIComposite>, SIComposite>) tChildrens2;

        SIComposite iTree = tTree.newInstance();
        iTree.setValue(tName, "a");
        SIComposite f = iTree.getField(tChildrens).addNew();
        f.setValue(tName, "b");
        f = iTree.getField(tChildrens).addNew();
        f.setValue(tName, "c");
        f.getField(tChildrens).addNew().setValue(tName, "d");

        AssertionsSInstance aITree = assertInstance(iTree);
        aITree.isAttrLabel("Tree");
        aITree.field("childrens[0]").isComposite().isAttrLabel("SubTree");
        aITree.field("childrens[1].childrens[0]").isComposite().isAttrLabel("SubTree");
        aITree.isValueEquals("name", "a");
        aITree.isValueEquals("childrens[0].name", "b");
        aITree.isValueEquals("childrens[1].name", "c");
        aITree.isValueEquals("childrens[1].childrens[0].name", "d");
        aITree.isList("childrens", 2);
        aITree.isList("childrens[0].childrens", 0);
        aITree.isList("childrens[1].childrens", 1);
        aITree.isList("childrens[1].childrens[0].childrens", 0);
    }

    @Ignore
    public void testCircularReferenceWithIntermediaryClassAndWithOutTypeDefinedByClass() {

    }
}
