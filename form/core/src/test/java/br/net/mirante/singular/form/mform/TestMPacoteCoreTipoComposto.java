package br.net.mirante.singular.form.mform;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import br.net.mirante.singular.form.mform.TestMPacoteCoreTipoComposto.TestPacoteCompostoA.TestTipoCompositeComCargaInterna;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;

public class TestMPacoteCoreTipoComposto extends TestCaseForm {

    public void testTipoCompostoCriacao() {
        SDictionary dicionario = SDictionary.create();
        PackageBuilder pb = dicionario.createNewPackage("teste");

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
        assertException(() -> tipoEndereco.getLocalType("classificacao.prioridade.x.y"), "Não existe o tipo");

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

    public void testeComposicaoCamposQuandoUmCompostoExtendeOutroComposto() {
        SDictionary dicionario = SDictionary.create();
        PackageBuilder pb = dicionario.createNewPackage("teste");

        STypeComposite<?> tipoBloco = pb.createCompositeType("bloco");
        tipoBloco.addFieldString("nome");
        tipoBloco.addFieldString("endereco");

        assertOrdemCampos(tipoBloco.getFields(), "nome", "endereco");
        assertOrdemCampos(tipoBloco.getFieldsLocal(), "nome", "endereco");

        STypeComposite<?> tipoSubBloco = pb.creatType("subBloco", tipoBloco);
        tipoSubBloco.addFieldInteger("idade");
        tipoSubBloco.addFieldString("area");

        assertOrdemCampos(tipoSubBloco.getFields(), "nome", "endereco", "idade", "area");
        assertOrdemCampos(tipoSubBloco.getFieldsLocal(), "idade", "area");

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

    public void testCriacaoDinamicaDeCamposNaInstancia() {
        SDictionary dicionario = SDictionary.create();
        PackageBuilder pb = dicionario.createNewPackage("teste");

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
        assertCriacaoDinamicaSubCampo(bloco.getFieldRecord("itens[0]"), "qtd", 0, 1);
        assertNotNull(bloco.getValue("itens[0]"));
        assertNull(bloco.getValue("itens[0].qtd"));
        bloco.setValue("itens[0].qtd", 10);
        assertEquals(bloco.getValue("itens[0].qtd"), 10);

        assertCriacaoDinamicaSubCampo(bloco, "subBloco", 3, 4);
        assertCriacaoDinamicaSubCampo(bloco.getFieldRecord("subBloco"), "teste", 0, 1);
        assertNull(bloco.getValue("subBloco.teste"));
        bloco.setValue("subBloco.teste", true);
        assertEquals(bloco.getValue("subBloco.teste"), true);

        // Testa criando em cadeia
        bloco = tipoBloco.newInstance();
        assertCriacaoDinamicaSubCampo(bloco, "subBloco.teste", 0, 1);
        assertEquals(1, bloco.getFieldRecord("subBloco").getFields().size());
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

    public void testTipoCompostoCriacaoComAtributoDoTipoListaDeTipoSimples() {
        SDictionary dicionario = SDictionary.create();
        PackageBuilder pb = dicionario.createNewPackage("teste");

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
        assertNull(bloco.getValue("enderecos[0]"));
        assertNull(bloco.getFieldList("enderecos").getValue("[0]"));

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

    public void testTipoCompostoCriacaoComAtributoDoTipoListaDeTipoComposto() {
        SDictionary dicionario = SDictionary.create();
        PackageBuilder pb = dicionario.createNewPackage("teste");

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
        assertNull(bloco.getValue("enderecos[0]"));
        assertNull(bloco.getValue("enderecos[0].rua"));
        assertNull(bloco.getFieldList("enderecos").getValue("[0]"));
        assertNull(bloco.getFieldList("enderecos").getValue("[0].rua"));

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

    public void testeOnCargaTipoDireto() {
        SDictionary dicionario = SDictionary.create();
        TestTipoCompositeComCargaInterna tipo = dicionario.getType(TestTipoCompositeComCargaInterna.class);
        assertEquals("xxx", tipo.as(AtrBasic.class).getLabel());
        assertNotNull(tipo.getField("nome"));
        assertEquals((Boolean) true, tipo.isRequired());
    }

    public void testeOnCargaTipoChamadaSubTipo() {
        SDictionary dicionario = SDictionary.create();
        PackageBuilder pb = dicionario.createNewPackage("teste");
        TestTipoCompositeComCargaInterna tipo = pb.createType("derivado", TestTipoCompositeComCargaInterna.class);

        TestTipoCompositeComCargaInterna tipoPai = dicionario.getType(TestTipoCompositeComCargaInterna.class);
        assertEquals("xxx", tipoPai.as(AtrBasic.class).getLabel());
        assertNotNull(tipoPai.getField("nome"));
        assertEquals((Boolean) true, tipoPai.isRequired());

        assertEquals("xxx", tipo.as(AtrBasic.class).getLabel());
        assertNotNull(tipo.getField("nome"));
        assertEquals((Boolean) true, tipo.isRequired());
    }

    public static final class TestPacoteCompostoA extends SPackage {

        protected TestPacoteCompostoA() {
            super("teste.pacoteCompostoA");
        }

        @Override
        protected void carregarDefinicoes(PackageBuilder pb) {
            pb.createType(TestTipoCompositeComCargaInterna.class);
        }

        @SInfoType(name = "TestTipoCompostoComCargaInterna", spackage = TestPacoteCompostoA.class)
        public static final class TestTipoCompositeComCargaInterna extends STypeComposite<SIComposite> {
            @Override
            protected void onLoadType(TypeBuilder tb) {
                withRequired(true);
                as(AtrBasic.class).label("xxx");
                addFieldString("nome");
            }
        }

    }

    public void testGetFieldOpt() {
        SDictionary dicionario = SDictionary.create();
        PackageBuilder pb = dicionario.createNewPackage("teste");

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
            assertException(() -> instance.getField(path), IndexOutOfBoundsException.class);
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
}
