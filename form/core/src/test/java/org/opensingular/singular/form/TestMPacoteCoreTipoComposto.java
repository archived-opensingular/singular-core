package org.opensingular.singular.form;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SISimple;
import org.opensingular.form.SInfoPackage;
import org.opensingular.form.SInfoType;
import org.opensingular.form.SInstance;
import org.opensingular.form.SPackage;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.TypeBuilder;
import org.opensingular.singular.form.TestMPacoteCoreTipoComposto.TestPacoteCompostoA.TestTipoCompositeComCargaInterna;
import org.opensingular.singular.form.TestMPacoteCoreTipoComposto.TestPacoteCompostoA.TestTipoCompositeComCargaInternaB;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.opensingular.singular.form.TestMPacoteCoreTipoLista.INDICE_INVALIDO;


@RunWith(Parameterized.class)
public class TestMPacoteCoreTipoComposto extends TestCaseForm {

    public TestMPacoteCoreTipoComposto(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test
    public void testTipoCompostoCriacao() {
        PackageBuilder pb = createTestDictionary().createNewPackage("teste");

        STypeComposite<?> tipoEndereco = pb.createCompositeType("endereco");
        tipoEndereco.addField("rua", STypeString.class);
        tipoEndereco.addFieldString("bairro", true);
        tipoEndereco.addFieldInteger("cep", true);

        STypeComposite<?> tipoClassificacao = tipoEndereco.addFieldComposite("classificacao");
        tipoClassificacao.addFieldInteger("prioridade");
        tipoClassificacao.addFieldString("descricao");

        assertTipo(tipoEndereco.getLocalType("rua"), "rua", STypeString.class);
        assertTipo(tipoEndereco.getField("rua"), "rua", STypeString.class);
        assertEquals((Object) false, tipoEndereco.getLocalType("rua").isRequired());
        assertEquals((Object) true, tipoEndereco.getLocalType("cep").isRequired());

        assertTipo(tipoEndereco.getLocalType("classificacao"), "classificacao", STypeComposite.class);
        assertTipo(tipoEndereco.getLocalType("classificacao.prioridade"), "prioridade", STypeInteger.class);

        assertNull(tipoEndereco.getLocalTypeOptional("classificacao.prioridade.x.y").orElse(null));
        assertException(() -> tipoEndereco.getLocalType("classificacao.prioridade.x.y"), "Não foi encontrado o tipo");

        SIComposite endereco = tipoEndereco.newInstance();
        assertFilhos(endereco, 0);

        assertNull(endereco.getValue("rua"));
        assertNull(endereco.getValue("bairro"));
        assertNull(endereco.getValue("cep"));
        assertNull(endereco.getValue("classificacao"));
        assertNull(endereco.getValue("classificacao.prioridade"));
        assertNull(endereco.getValue("classificacao.descricao"));
        assertFilhos(endereco, 0);

        assertException(() -> endereco.setValue(100), "SIComposite só suporta valores de mesmo tipo");

        testAtribuicao(endereco, "rua", "Pontes", 1);
        testAtribuicao(endereco, "bairro", "Norte", 2);
        testAtribuicao(endereco, "classificacao.prioridade", 1, 4);

        testCaminho(endereco, null, null);
        testCaminho(endereco, "rua", "rua");
        testCaminho(endereco, "classificacao.prioridade", "classificacao.prioridade");
        testCaminho(endereco.getField("classificacao"), null, "classificacao");
        testCaminho(endereco.getField("classificacao.prioridade"), null, "classificacao.prioridade");

        assertNotNull(endereco.getValue("classificacao"));
        assertTrue(endereco.getValue("classificacao") instanceof Collection);
        assertTrue(((Collection<?>) endereco.getValue("classificacao")).size() >= 1);
        testAtribuicao(endereco, "classificacao.prioridade", 1, 4);

        testAtribuicao(endereco, "classificacao", null, 2);
        assertNull(endereco.getValue("classificacao.prioridade"));
        testAtribuicao(endereco, "classificacao.prioridade", null, 2);

        assertException(() -> endereco.setValue("classificacao", "X"), "SIComposite só suporta valores de mesmo tipo");
    }

    private static void assertTipo(SType<?> tipo, String nomeEsperado, Class<?> classeEsperadaDoTipo) {
        assertNotNull(tipo);
        assertEquals(nomeEsperado, tipo.getNameSimple());
        assertEquals(classeEsperadaDoTipo, tipo.getClass());
    }

    @Test
    public void testeComposicaoCamposQuandoUmCompostoExtendeOutroComposto() {
        PackageBuilder pb = createTestDictionary().createNewPackage("teste");

        STypeComposite<?> tipoBloco = pb.createCompositeType("bloco");
        tipoBloco.addFieldString("nome");
        tipoBloco.addFieldString("endereco");

        assertOrdemCampos(tipoBloco.getFields(), "nome", "endereco");
        assertOrdemCampos(tipoBloco.getFieldsLocal(), "nome", "endereco");

        STypeComposite<?> tipoSubBloco = pb.createType("subBloco", tipoBloco);
        tipoSubBloco.addFieldInteger("idade");
        tipoSubBloco.addFieldString("area");

        assertOrdemCampos(tipoSubBloco.getFields(), "nome", "endereco", "idade", "area");
        //TODO O resultado abaixo talvez tenha que ser repensando
        assertOrdemCampos(tipoSubBloco.getFieldsLocal(), "nome", "endereco", "idade", "area");

        SIComposite subBloco = tipoSubBloco.newInstance();
        testAtribuicao(subBloco, "area", "sul", 1);
        testAtribuicao(subBloco, "idade", 10, 2);
        assertNull(subBloco.getValue("endereco"));
        testAtribuicao(subBloco, "endereco", "Rua 1", 3);
        testAtribuicao(subBloco, "nome", "Paulo", 4);

        assertEqualsList(subBloco.getFields().stream().map(c -> c.getValue()).collect(Collectors.toList()), "Paulo", "Rua 1", 10, "sul");
    }

    private static void assertOrdemCampos(Collection<SType<?>> fields, String... nomesEsperados) {
        assertEqualsList(fields.stream().map(f -> f.getNameSimple()).collect(Collectors.toList()), (Object[]) nomesEsperados);
    }

    @Test
    public void testCriacaoDinamicaDeCamposNaInstancia() {
        PackageBuilder pb = createTestDictionary().createNewPackage("teste");

        STypeComposite<SIComposite> tipoBloco = pb.createCompositeType("bloco");
        tipoBloco.addFieldInteger("inicio");
        tipoBloco.addFieldInteger("fim");
        tipoBloco.addFieldListOf("enderecos", STypeString.class);
        tipoBloco.addFieldListOfComposite("itens", "item").getElementsType().addFieldInteger("qtd");
        tipoBloco.addFieldComposite("subBloco").addFieldBoolean("teste");

        SIComposite bloco = tipoBloco.newInstance();

        assertTrue(bloco.isEmptyOfData());
        assertTrue(bloco.isFieldNull("inicio"));
        assertTrue(bloco.isFieldNull("enderecos"));
        assertTrue(bloco.isFieldNull("itens"));
        assertTrue(bloco.isFieldNull("itens[0].qtd"));
        assertTrue(bloco.isFieldNull("subBloco"));
        assertTrue(bloco.isFieldNull("subBloco.teste"));
        assertEquals(0, bloco.getFields().size());

        assertCriacaoDinamicaSubCampo(bloco, "inicio", 0, 1);
        assertCriacaoDinamicaSubCampo(bloco, "enderecos", 1, 2);

        assertCriacaoDinamicaSubCampo(bloco, "itens", 2, 3);
        bloco.getFieldList("itens").addNew();
        assertCriacaoDinamicaSubCampo(bloco.getFieldComposite("itens[0]"), "qtd", 0, 1);
        assertNotNull(bloco.getValue("itens[0]"));
        assertNull(bloco.getValue("itens[0].qtd"));
        bloco.setValue("itens[0].qtd", 10);
        assertEquals(bloco.getValue("itens[0].qtd"), 10);

        assertCriacaoDinamicaSubCampo(bloco, "subBloco", 3, 4);
        assertCriacaoDinamicaSubCampo(bloco.getFieldComposite("subBloco"), "teste", 0, 1);
        assertNull(bloco.getValue("subBloco.teste"));
        bloco.setValue("subBloco.teste", true);
        assertEquals(bloco.getValue("subBloco.teste"), true);

        // Testa criando em cadeia
        bloco = tipoBloco.newInstance();
        assertCriacaoDinamicaSubCampo(bloco, "subBloco.teste", 0, 1);
        assertEquals(1, bloco.getFieldComposite("subBloco").getFields().size());
    }

    private static void assertCriacaoDinamicaSubCampo(SIComposite bloco, String path, int qtdCamposAntes, int qtdCamposDepois) {
        Object resultado2 = bloco.getValue(path); // Não provoca nova instancia
        assertNull(resultado2);
        assertTrue(bloco.isFieldNull(path));
        assertEquals(qtdCamposAntes, bloco.getFields().size());

        SInstance resultado = bloco.getField(path); // Provoca instancia
        assertNotNull(resultado);
        if (resultado instanceof SISimple) {
            assertNull(bloco.getValue(path));
        }
        assertTrue(resultado.isEmptyOfData());
        assertEquals(qtdCamposDepois, bloco.getFields().size());
    }

    @Test
    public void testTipoCompostoCriacaoComAtributoDoTipoListaDeTipoSimples() {
        PackageBuilder pb = createTestDictionary().createNewPackage("teste");

        STypeComposite<? extends SIComposite> tipoBloco = pb.createCompositeType("bloco");
        tipoBloco.addFieldListOf("enderecos", STypeString.class);

        assertTipo(tipoBloco.getLocalType("enderecos"), "enderecos", STypeList.class);
        assertTipo(tipoBloco.getField("enderecos"), "enderecos", STypeList.class);
        assertTipo(((STypeList<?, ?>) tipoBloco.getField("enderecos")).getElementsType(), "String", STypeString.class);

        SIComposite bloco = tipoBloco.newInstance();
        assertNull(bloco.getValue("enderecos"));
        assertNull(bloco.getValue("enderecos[0]"));
        assertEquals(0, bloco.getFields().size());
        assertTrue(bloco.isEmptyOfData());

        bloco.getFieldList("enderecos");
        assertEquals(1, bloco.getFields().size());
        assertTrue(bloco.isEmptyOfData());
        assertNotNull(bloco.getValue("enderecos"));
        assertException(() -> bloco.getValue("enderecos[0]"), INDICE_INVALIDO);
        assertException(() -> bloco.getFieldList("enderecos").getValue("[0]"), INDICE_INVALIDO);

        bloco.getFieldList("enderecos").addValue("E1");
        assertEquals(1, bloco.getFields().size());
        assertFalse(bloco.isEmptyOfData());
        assertEquals("E1", bloco.getValue("enderecos[0]"));
        assertEquals("E1", bloco.getFieldList("enderecos").getValue("[0]"));
        assertEqualsList(bloco.getValue("enderecos"), "E1");

        testAtribuicao(bloco, "enderecos[0]", "E2", 2);

        testCaminho(bloco, "enderecos", "enderecos");
        testCaminho(bloco, "enderecos[0]", "enderecos[0]");
        testCaminho(bloco.getField("enderecos[0]"), null, "enderecos[0]");
    }

    @Test
    public void testTipoCompostoCriacaoComAtributoDoTipoListaDeTipoComposto() {
        PackageBuilder pb = createTestDictionary().createNewPackage("teste");

        STypeComposite<? extends SIComposite> tipoBloco = pb.createCompositeType("bloco");
        STypeList<STypeComposite<SIComposite>, SIComposite> tipoEnderecos = tipoBloco.addFieldListOfComposite("enderecos", "endereco");
        STypeComposite<?> tipoEndereco = tipoEnderecos.getElementsType();
        tipoEndereco.addFieldString("rua");
        tipoEndereco.addFieldString("cidade");

        SIComposite bloco = tipoBloco.newInstance();
        assertNull(bloco.getValue("enderecos"));
        assertNull(bloco.getValue("enderecos[0]"));
        assertNull(bloco.getValue("enderecos[0].rua"));
        assertEquals(0, bloco.getFields().size());
        assertTrue(bloco.isEmptyOfData());

        bloco.getFieldList("enderecos");
        assertEquals(1, bloco.getFields().size());
        assertTrue(bloco.isEmptyOfData());
        assertNotNull(bloco.getValue("enderecos"));
        assertException(() -> bloco.getValue("enderecos[0]"), INDICE_INVALIDO);
        assertException(() -> bloco.getValue("enderecos[0].rua"), INDICE_INVALIDO);
        assertException(() -> bloco.getFieldList("enderecos").getValue("[0]"), INDICE_INVALIDO);
        assertException(() -> bloco.getFieldList("enderecos").getValue("[0].rua"), INDICE_INVALIDO);

        SIComposite endereco = (SIComposite) bloco.getFieldList("enderecos").addNew();
        assertEquals(1, bloco.getFields().size());
        assertTrue(bloco.isEmptyOfData());
        assertTrue(endereco.isEmptyOfData());
        assertEquals(0, endereco.getFields().size());
        assertNotNull(bloco.getValue("enderecos"));
        assertNotNull(bloco.getValue("enderecos[0]"));
        assertNull(bloco.getValue("enderecos[0].rua"));
        assertNotNull(bloco.getFieldList("enderecos").getValue("[0]"));
        assertNull(bloco.getFieldList("enderecos").getValue("[0].rua"));

        bloco.getField("enderecos[0].rua");
        assertEquals(1, endereco.getFields().size());
        assertTrue(bloco.isEmptyOfData());
        assertTrue(endereco.isEmptyOfData());
        assertNotNull(bloco.getValue("enderecos[0]"));
        assertNull(bloco.getValue("enderecos[0].rua"));

        testAtribuicao(bloco, "enderecos[0].rua", "E2", 3);
        assertFalse(bloco.isEmptyOfData());
        assertFalse(endereco.isEmptyOfData());

        testCaminho(bloco, "enderecos", "enderecos");
        testCaminho(bloco, "enderecos[0]", "enderecos[0]");
        testCaminho(bloco, "enderecos[0].rua", "enderecos[0].rua");
        testCaminho(bloco.getField("enderecos[0]"), null, "enderecos[0]");
        testCaminho(bloco.getField("enderecos[0]"), "rua", "enderecos[0].rua");
        testCaminho(bloco.getField("enderecos[0].rua"), null, "enderecos[0].rua");
    }

    @Test
    public void testeOnCargaTipoDireto() {
        TestTipoCompositeComCargaInterna tipo = createTestDictionary().getType(TestTipoCompositeComCargaInterna.class);
        assertEquals("xxx", tipo.asAtr().getLabel());
        assertNotNull(tipo.getField("nome"));
        assertEquals((Boolean) true, tipo.isRequired());
    }

    @Test
    public void testeOnCargaTipoChamadaSubTipo() {
        PackageBuilder pb = createTestDictionary().createNewPackage("teste");
        TestTipoCompositeComCargaInterna tipo = pb.createType("derivado", TestTipoCompositeComCargaInterna.class);

        TestTipoCompositeComCargaInterna tipoPai = pb.getDictionary().getType(TestTipoCompositeComCargaInterna.class);
        assertEquals("xxx", tipoPai.asAtr().getLabel());
        assertNotNull(tipoPai.getField("nome"));
        assertEquals((Boolean) true, tipoPai.isRequired());

        assertEquals("xxx", tipo.asAtr().getLabel());
        assertNotNull(tipo.getField("nome"));
        assertEquals((Boolean) true, tipo.isRequired());
    }

    @Test
    public void testCargaCamposDeSubTipoCompostoRepetidoDuasVezesByClass() {
        SDictionary dictionary = createTestDictionary();
        TestTipoCompositeComCargaInterna a = dictionary.getType(TestTipoCompositeComCargaInterna.class);
        TestTipoCompositeComCargaInternaB b = dictionary.getType(TestTipoCompositeComCargaInternaB.class);
        AssertionsSType assertA = assertType(a).isComposite(1);
        AssertionsSType assertB = assertType(b).isComposite(3);
        assertB.isString("descricao");
        assertB.isComposite("bloco1", 1);
        assertB.isString("bloco1.nome");
        assertB.isComposite("bloco2", 1);
        assertB.isString("bloco2.nome");
        assertNotNull(b.descricao);
        assertNotNull(b.bloco1);
        assertNotNull(b.bloco1.nome);
        assertNotNull(b.bloco2);
        assertNotNull(b.bloco2.nome);

        assertNotSame(b.bloco1, b.bloco2);
        assertNotSame(b.bloco1, a);
        assertSame(b.bloco1.getSuperType(), a);
        assertSame(b.bloco2.getSuperType(), a);

        //Mudança em um campo não deve alterar o outro - 1
        assertA.isAttribute(SPackageBasic.ATR_LABEL, "xxx");
        assertB.field("bloco1").isAttribute(SPackageBasic.ATR_LABEL, "xxx");
        assertB.field("bloco2").isAttribute(SPackageBasic.ATR_LABEL, "xxx");

        b.bloco1.asAtr().label("yyy");
        assertA.isAttribute(SPackageBasic.ATR_LABEL, "xxx");
        assertB.field("bloco1").isAttribute(SPackageBasic.ATR_LABEL, "yyy");
        assertB.field("bloco2").isAttribute(SPackageBasic.ATR_LABEL, "xxx");

        //Mudança em um campo não deve alterar o outro - 2
        assertA.isAttribute(SPackageBasic.ATR_SUBTITLE, null);
        assertB.field("bloco1").isAttribute(SPackageBasic.ATR_SUBTITLE, null);
        assertB.field("bloco2").isAttribute(SPackageBasic.ATR_SUBTITLE, null);
        b.bloco1.asAtr().subtitle("aaa");
        assertA.isAttribute(SPackageBasic.ATR_SUBTITLE, null);
        assertB.field("bloco1").isAttribute(SPackageBasic.ATR_SUBTITLE, "aaa");
        assertB.field("bloco2").isAttribute(SPackageBasic.ATR_SUBTITLE, null);
        a.asAtr().subtitle("bbb");
        assertA.isAttribute(SPackageBasic.ATR_SUBTITLE, "bbb");
        assertB.field("bloco1").isAttribute(SPackageBasic.ATR_SUBTITLE, "aaa");
        assertB.field("bloco2").isAttribute(SPackageBasic.ATR_SUBTITLE, "bbb");


        //Mudança em um campo não deve alterar o outro - 3 - segundo nível
        assertNotSame(b.bloco1.nome, b.bloco2.nome);
        assertNotSame(a.nome, b.bloco2.nome);
        assertSame(a.nome, b.bloco1.nome.getSuperType() );
        assertSame(a.nome, b.bloco2.nome.getSuperType());

        assertA.field("nome").isAttribute(SPackageBasic.ATR_SUBTITLE, null);
        assertB.field("bloco1.nome").isAttribute(SPackageBasic.ATR_SUBTITLE, null);
        assertB.field("bloco2.nome").isAttribute(SPackageBasic.ATR_SUBTITLE, null);
        b.bloco1.nome.asAtr().subtitle("rrr");
        assertA.field("nome").isAttribute(SPackageBasic.ATR_SUBTITLE, null);
        assertB.field("bloco1.nome").isAttribute(SPackageBasic.ATR_SUBTITLE, "rrr");
        assertB.field("bloco2.nome").isAttribute(SPackageBasic.ATR_SUBTITLE, null);
        a.nome.asAtr().subtitle("sss");
        assertA.field("nome").isAttribute(SPackageBasic.ATR_SUBTITLE, "sss");
        assertB.field("bloco1.nome").isAttribute(SPackageBasic.ATR_SUBTITLE, "rrr");
        assertB.field("bloco2.nome").isAttribute(SPackageBasic.ATR_SUBTITLE, "sss");
    }

    @Test
    public void testCargaCamposDeSubTipoCompostoRepetidoDuasVezesByReference() {
        SDictionary dictionary = createTestDictionary();
        TestTipoCompositeComCargaInterna a = dictionary.getType(TestTipoCompositeComCargaInterna.class);
        TestTipoCompositeComCargaInternaB b = dictionary.getType(TestTipoCompositeComCargaInternaB.class);

        STypeComposite<SIComposite> c = dictionary.createNewPackage("test").createCompositeType("blockC");
        c.addFieldString("descricao");
        TestTipoCompositeComCargaInterna blocoA1 = c.addField("blocoA1", a);
        TestTipoCompositeComCargaInterna blocoA2 = c.addField("blocoA2", a);
        TestTipoCompositeComCargaInternaB blocoB1 = c.addField("blocoB1", b);
        TestTipoCompositeComCargaInternaB blocoB2 = c.addField("blocoB2", b);

        AssertionsSType assertA = assertType(a).isComposite(1);
        AssertionsSType assertB = assertType(b).isComposite(3);
        AssertionsSType assertC = assertType(c).isComposite(5);
        assertC.isString("descricao");
        assertC.isComposite("blocoA1", 1).isString("nome");
        assertC.isComposite("blocoA2", 1).isString("nome");
        assertC.isComposite("blocoB1", 3).isString("bloco2.nome");
        assertC.isComposite("blocoB1.bloco1", 1).isString("nome");
        assertC.isComposite("blocoB2.bloco2", 1).isString("nome");


        assertNotNull(blocoA1.nome);
        assertNotNull(b.descricao);
        assertNotNull(blocoB1.descricao);
        assertNotNull(blocoB1.bloco1.nome);

        assertNotSame(blocoB1, blocoB2);
        assertNotSame(b, blocoB1);
        assertSame(blocoB1.getSuperType(), b);

        assertNotSame(blocoA1, blocoA2);
        assertNotSame(a, blocoA2);
        assertSame(a, blocoA1.getSuperType());

        assertNotSame(blocoA1.nome, blocoA2.nome);
        assertNotSame(a.nome, blocoA2.nome);
        assertSame(a.nome, blocoA2.nome.getSuperType());

        assertNotSame(blocoB1.bloco1 , blocoB2.bloco1);
        assertNotSame(b.bloco1, blocoB1.bloco1);
        assertSame(b.bloco1, blocoB1.bloco1.getSuperType());

        assertNotSame(blocoB1.bloco1.nome , blocoB2.bloco1.nome);
        assertNotSame(b.bloco1.nome, blocoB1.bloco1.nome);
        assertSame(b.bloco1.nome, blocoB1.bloco1.nome.getSuperType());


        //Mudança em um campo não pai, não deve alterar o outro - 1
        assertA.isAttribute(SPackageBasic.ATR_LABEL, "xxx");
        assertB.field("bloco1").isAttribute(SPackageBasic.ATR_LABEL, "xxx");
        assertB.field("bloco2").isAttribute(SPackageBasic.ATR_LABEL, "xxx");
        assertC.field("blocoB1.bloco1").isAttribute(SPackageBasic.ATR_LABEL, "xxx");
        assertC.field("blocoB1.bloco2").isAttribute(SPackageBasic.ATR_LABEL, "xxx");
        assertC.field("blocoB2.bloco1").isAttribute(SPackageBasic.ATR_LABEL, "xxx");
        assertC.field("blocoB2.bloco2").isAttribute(SPackageBasic.ATR_LABEL, "xxx");

        blocoB1.bloco1.asAtr().label("yyy");
        assertA.isAttribute(SPackageBasic.ATR_LABEL, "xxx");
        assertB.field("bloco1").isAttribute(SPackageBasic.ATR_LABEL, "xxx");
        assertB.field("bloco2").isAttribute(SPackageBasic.ATR_LABEL, "xxx");
        assertC.field("blocoB1.bloco1").isAttribute(SPackageBasic.ATR_LABEL, "yyy");
        assertC.field("blocoB1.bloco2").isAttribute(SPackageBasic.ATR_LABEL, "xxx");
        assertC.field("blocoB2.bloco1").isAttribute(SPackageBasic.ATR_LABEL, "xxx");
        assertC.field("blocoB2.bloco2").isAttribute(SPackageBasic.ATR_LABEL, "xxx");

        b.bloco2.asAtr().label("zzz");
        assertA.isAttribute(SPackageBasic.ATR_LABEL, "xxx");
        assertB.field("bloco1").isAttribute(SPackageBasic.ATR_LABEL, "xxx");
        assertB.field("bloco2").isAttribute(SPackageBasic.ATR_LABEL, "zzz");
        assertC.field("blocoB1.bloco1").isAttribute(SPackageBasic.ATR_LABEL, "yyy");
        assertC.field("blocoB1.bloco2").isAttribute(SPackageBasic.ATR_LABEL, "zzz");
        assertC.field("blocoB2.bloco1").isAttribute(SPackageBasic.ATR_LABEL, "xxx");
        assertC.field("blocoB2.bloco2").isAttribute(SPackageBasic.ATR_LABEL, "zzz");

        a.asAtr().label("www");
        assertA.isAttribute(SPackageBasic.ATR_LABEL, "www");
        assertB.field("bloco1").isAttribute(SPackageBasic.ATR_LABEL, "www");
        assertB.field("bloco2").isAttribute(SPackageBasic.ATR_LABEL, "zzz");
        assertC.field("blocoB1.bloco1").isAttribute(SPackageBasic.ATR_LABEL, "yyy");
        assertC.field("blocoB1.bloco2").isAttribute(SPackageBasic.ATR_LABEL, "zzz");
        assertC.field("blocoB2.bloco1").isAttribute(SPackageBasic.ATR_LABEL, "www");
        assertC.field("blocoB2.bloco2").isAttribute(SPackageBasic.ATR_LABEL, "zzz");
    }

    @SInfoPackage(name = "teste.pacoteCompostoA")
    public static final class TestPacoteCompostoA extends SPackage {

        @Override
        protected void onLoadPackage(PackageBuilder pb) {
            pb.createType(TestTipoCompositeComCargaInterna.class);
            pb.createType(TestTipoCompositeComCargaInternaB.class);
        }

        @SInfoType(name = "TestTipoCompostoComCargaInterna", spackage = TestPacoteCompostoA.class)
        public static final class TestTipoCompositeComCargaInterna extends STypeComposite<SIComposite> {

            public STypeString nome;

            @Override
            protected void onLoadType(TypeBuilder tb) {
                withRequired(true);
                asAtr().label("xxx");
                nome = addFieldString("nome");
            }
        }

        @SInfoType(name = "TestTipoCompostoComCargaInternaB", spackage = TestPacoteCompostoA.class)
        public static final class TestTipoCompositeComCargaInternaB extends STypeComposite<SIComposite> {

            public STypeString descricao;
            public TestTipoCompositeComCargaInterna bloco1;
            public TestTipoCompositeComCargaInterna bloco2;

            @Override
            protected void onLoadType(TypeBuilder tb) {
                withRequired(true);
                descricao = addFieldString("descricao");
                bloco1 = addField("bloco1", TestTipoCompositeComCargaInterna.class);
                bloco2 = addField("bloco2", TestTipoCompositeComCargaInterna.class);
            }
        }

    }

    @Test
    public void testGetFieldOpt() {
        PackageBuilder pb = createTestDictionary().createNewPackage("teste");

        STypeComposite<? extends SIComposite> tipoBloco = pb.createCompositeType("bloco");
        tipoBloco.addFieldString("ba");
        tipoBloco.addFieldString("bc");
        STypeComposite<SIComposite> tipoSubBloco = tipoBloco.addFieldComposite("subbloco");
        tipoSubBloco.addFieldString("sa");
        tipoSubBloco.addFieldString("sb");
        STypeList<STypeComposite<SIComposite>, SIComposite> tipoLista = tipoBloco.addFieldListOfComposite("itens", "item");
        tipoLista.getElementsType().addFieldString("la");

        SIComposite instance = tipoBloco.newInstance();
        instance.setValue("ba", "1");
        instance.setValue("subbloco.sa", "2");
        instance.getFieldList("itens").addNew();
        instance.setValue("itens[0].la", "3");

        assertThatFound(instance, "ba", "1");
        assertThatFound(instance, "bc", null);
        assertThatFound(instance, "subbloco.sa", "2");
        assertThatFound(instance, "subbloco.sb", null);

        assertThatNotFound(instance, "bx", false);
        assertThatNotFound(instance, "subbloco.sx", false);
        assertThatNotFound(instance, "bx.bx.bx", false);

        assertThatFound(instance, "itens[0].la", "3");
        assertThatNotFound(instance, "itens[0].lb", false);
        assertThatNotFound(instance, "itens[1].la", true);
        assertThatNotFound(instance, "itens[1].lb", true);

    }

    private static void assertThatNotFound(SIComposite instance, String path, boolean indexException) {
        Optional<SInstance> field = instance.getFieldOpt(path);
        assertNotNull(field);
        assertFalse(field.isPresent());
        if (indexException) {
            assertException(() -> instance.getField(path), INDICE_INVALIDO);
        } else {
            assertException(() -> instance.getField(path), "Não é um campo definido");
        }
    }

    private static void assertThatFound(SIComposite instance, String path, String value) {
        Optional<SInstance> field = instance.getFieldOpt(path);
        assertNotNull(field);
        assertNotNull(field.get());
        assertEquals(field.get().getValue(), value);

        SInstance field2 = instance.getField(path);
        assertNotNull(field2);
        assertEquals(field2.getValue(), value);
    }

    @Test
    public void testTentativaDeCampoComNomeDuplicado() {
        PackageBuilder pb = createTestDictionary().createNewPackage("teste");
        STypeComposite<SIComposite> block = pb.createCompositeType("block");
        block.addFieldString("field1");
        assertException(() -> block.addFieldString("field1"), "Já existe um campo criado com o nome");
        assertException(() -> block.addFieldComposite("field1"), "Já existe um campo criado com o nome");
        assertException(() -> block.addFieldListOfComposite("field1","f"), "Já existe um campo criado com o nome");
        assertType(block).isComposite(1);
    }
}
