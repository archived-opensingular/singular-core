package br.net.mirante.singular.form;

import org.junit.Assert;

import br.net.mirante.singular.form.SCorePackageTest.TestPacoteA.TestTipoA;
import br.net.mirante.singular.form.SCorePackageTest.TestPacoteA.TestTipoB;
import br.net.mirante.singular.form.SCorePackageTest.TestPacoteA.TestTipoComCargaInterna;
import br.net.mirante.singular.form.SCorePackageTest.TestPacoteA.TestTipoX;
import br.net.mirante.singular.form.type.basic.SPackageBasic;
import br.net.mirante.singular.form.type.core.SIInteger;
import br.net.mirante.singular.form.type.core.SIString;
import br.net.mirante.singular.form.type.core.SPackageCore;
import br.net.mirante.singular.form.type.core.STypeBoolean;
import br.net.mirante.singular.form.type.core.STypeDate;
import br.net.mirante.singular.form.type.core.STypeInteger;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.type.country.brazil.STypeCEP;
import br.net.mirante.singular.form.type.util.STypeEMail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.function.Supplier;

@RunWith(Parameterized.class)
public class SCorePackageTest extends TestCaseForm {

    public SCorePackageTest(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test
    public void testRecuperarTipo() {
        createTestDictionary().getTypeOptional(STypeString.class);
    }

    @Test
    public void testHerancaValorEntreTipos() {
        SDictionary dictionary = createTestDictionary();

        STypeSimple<?, ?> tipoS = dictionary.getTypeOptional(STypeSimple.class);
        STypeBoolean      tipoB = dictionary.getTypeOptional(STypeBoolean.class);
        STypeInteger      tipoI = dictionary.getTypeOptional(STypeInteger.class);

        Assert.assertFalse(tipoS.getAttributeValue(SPackageBasic.ATR_REQUIRED));
        Assert.assertFalse(tipoB.getAttributeValue(SPackageBasic.ATR_REQUIRED));
        Assert.assertFalse(tipoI.getAttributeValue(SPackageBasic.ATR_REQUIRED));

        tipoB.withRequired(true);

        Assert.assertEquals(false, tipoS.isRequired());
        Assert.assertEquals(true, tipoB.isRequired());
        Assert.assertEquals(false, tipoI.isRequired());

        tipoB.withRequired(true);
        tipoS.withRequired(false);

        Assert.assertEquals(false, tipoS.isRequired());
        Assert.assertEquals(true, tipoB.isRequired());
        Assert.assertEquals(false, tipoI.isRequired());

        tipoB.withRequired(null);

        Assert.assertEquals(false, tipoS.isRequired());
        Assert.assertEquals(false, tipoB.isRequired());
        Assert.assertEquals(false, tipoI.isRequired());

        tipoS.withRequired(true);
        tipoB.withRequired(null);

        Assert.assertEquals(true, tipoS.isRequired());
        Assert.assertEquals(false, tipoB.isRequired());
        Assert.assertEquals(true, tipoI.isRequired());
    }

    @Test
    public void testValidacaoBasica() {
        SDictionary dictionary = createTestDictionary();
        STypeBoolean tipoB = dictionary.getTypeOptional(STypeBoolean.class);
        STypeInteger tipoI = dictionary.getTypeOptional(STypeInteger.class);
        STypeString tipoS = dictionary.getTypeOptional(STypeString.class);

        testarAtribuicao(tipoB, true, null, null);
        testarAtribuicao(tipoB, true, true, true);
        testarAtribuicao(tipoB, true, false, false);
        testarAtribuicao(tipoB, true, "true", true);
        testarAtribuicao(tipoB, true, " true ", true);
        testarAtribuicao(tipoB, true, "false", false);
        testarAtribuicao(tipoB, true, 1, true);
        testarAtribuicao(tipoB, true, 0, false);
        testarAtribuicao(tipoB, false, -1, null);
        testarAtribuicao(tipoB, false, 2, null);
        testarAtribuicao(tipoB, false, "RR", null);
        testarAtribuicao(tipoB, false, new Object(), null);

        testarAtribuicao(tipoI, true, null, null);
        testarAtribuicao(tipoI, true, 1, 1);
        testarAtribuicao(tipoI, true, "1", 1);
        testarAtribuicao(tipoI, true, " 1", 1);
        testarAtribuicao(tipoI, true, " 1 ", 1);
        testarAtribuicao(tipoI, true, 10.5, 10);
        testarAtribuicao(tipoI, true, new Long(100), 100);
        testarAtribuicao(tipoI, false, "RR", null);
        testarAtribuicao(tipoI, false, new Object(), null);
        testarAtribuicao(tipoI, false, false, null);

        assertNull(tipoS.getAttributeInstance(SPackageBasic.ATR_EMPTY_TO_NULL));
        assertEquals(tipoS.getAttributeValue(SPackageBasic.ATR_EMPTY_TO_NULL), Boolean.TRUE);
        assertTrue(tipoS.getValorAtributoEmptyToNull());
        assertTrue(tipoS.getValorAtributoTrim());

        testarAtribuicao(tipoS, true, null, null);
        testarAtribuicao(tipoS, true, true, "true");
        testarAtribuicao(tipoS, true, false, "false");
        testarAtribuicao(tipoS, true, "true", "true");
        testarAtribuicao(tipoS, true, "false", "false");
        testarAtribuicao(tipoS, true, "RR", "RR");
        testarAtribuicao(tipoS, true, 100, "100");
        testarAtribuicao(tipoS, true, "", null);
        testarAtribuicao(tipoS, true, "  ", null);
        testarAtribuicao(tipoS, true, " true ", "true");
    }

    private static void testarAtribuicao(STypeSimple<?, ?> tipo, boolean valorValido, Object valor, Object valorFinalEsperado) {
        SISimple<?> instancia = tipo.newInstance();
        if (valorValido) {
            instancia.setValue(valor);
            Object resultado = instancia.getValue();
            Assert.assertEquals(valorFinalEsperado, resultado);

            Object resultado2 = instancia.getType().convert(valor, instancia.getType().getValueClass());
            Assert.assertEquals(resultado, resultado2);
        } else {
            assertException(() -> instancia.setValue(valor), "não consegue converter", "Deveria dar erro de conversão");

            Assert.assertEquals(valorFinalEsperado, instancia.getValue());

            assertException(() -> instancia.getType().convert(valor, instancia.getType().getValueClass()),
                    "não consegue converter", "Deveria dar erro de conversão");
        }
    }

    @Test
    public void testSelfReference() {
        SDictionary dictionary = createTestDictionary();

        STypeSimple<?, ?> tipoS = dictionary.getTypeOptional(STypeSimple.class);
        STypeBoolean tipoB = dictionary.getTypeOptional(STypeBoolean.class);
        STypeInteger tipoI = dictionary.getTypeOptional(STypeInteger.class);

        Assert.assertNull(tipoS.getAttributeValue(SPackageBasic.ATR_DEFAULT_IF_NULL));
        Assert.assertNull(tipoB.getAttributeValue(SPackageBasic.ATR_DEFAULT_IF_NULL));
        Assert.assertNull(tipoI.getAttributeValue(SPackageBasic.ATR_DEFAULT_IF_NULL));

        assertException(() -> tipoS.withDefaultValueIfNull(new Integer(1)), "abstrato",
                "Não deveria ser possível atribuir valor em um isntancia abstrata");

        Assert.assertEquals(null, tipoS.getAttributeValueOrDefaultValueIfNull());
        Assert.assertEquals(null, tipoB.getAttributeValueOrDefaultValueIfNull());
        Assert.assertEquals(null, tipoI.getAttributeValueOrDefaultValueIfNull());

        tipoI.withDefaultValueIfNull(new Integer(2));

        Assert.assertEquals(null, tipoS.getAttributeValueOrDefaultValueIfNull());
        Assert.assertEquals(null, tipoB.getAttributeValueOrDefaultValueIfNull());
        Assert.assertEquals(2, tipoI.getAttributeValueOrDefaultValueIfNull());

        tipoB.withDefaultValueIfNull(true);

        Assert.assertEquals(null, tipoS.getAttributeValueOrDefaultValueIfNull());
        Assert.assertEquals(true, tipoB.getAttributeValueOrDefaultValueIfNull());
        Assert.assertEquals(2, tipoI.getAttributeValueOrDefaultValueIfNull());

        try {
            tipoB.withDefaultValueIfNull("RR");
            assertEquals(true, tipoB.getAttributeValueOrDefaultValueIfNull());
            fail("Deveria ocorrer Exception ao atribuir um valor incorreto");
        } catch (RuntimeException e) {
            if (!e.getMessage().contains("não consegue converter")) {
                throw e;
            }
        }
        assertEquals(true, tipoB.getAttributeValueOrDefaultValueIfNull());
    }

    @Test
    public void testCriacaoDuplicada() {
        PackageBuilder pb = createTestDictionary().createNewPackage("teste");

        pb.createType("CPF", STypeString.class);
        assertException(() -> pb.createType("CPF", STypeString.class), "já está criada");
    }

    @Test
    public void testCriacaoAtributoLocal() {
        PackageBuilder pb = createTestDictionary().createNewPackage("teste");

        // AtrRef<?, ?, ?> atr = new AtrRef(null, "atTeste", MTipoString.class,
        // MIString.class, String.class);

        STypeString tipoX = pb.createType("XXXXX", STypeString.class);
        STypeString atributo = pb.createAttributeIntoType(tipoX, "atTeste", STypeString.class);

        assertNull(pb.getPackage().getLocalTypeOptional("YYYYYYY").orElse(null));

        assertNull(pb.getPackage().getLocalTypeOptional("atTeste").orElse(null));
        assertNotNull(pb.getPackage().getLocalType("XXXXX"));
        assertNotNull(tipoX.getLocalType("atTeste"));

        assertEquals(null, tipoX.getAttributeValue("teste.XXXXX.atTeste"));
        assertEquals(null, tipoX.getAttributeValue("atTeste"));

        atributo.withDefaultValueIfNull("0");
        assertEquals("0", tipoX.getAttributeValue("atTeste"));
        assertEquals("0", tipoX.getAttributeValue("teste.XXXXX.atTeste"));

        tipoX.setAttributeValue(atributo, "A");
        assertEquals("A", tipoX.getAttributeValue("atTeste"));
        assertEquals("A", tipoX.getAttributeValue("teste.XXXXX.atTeste"));

        tipoX.setAttributeValue("atTeste", "B");
        assertEquals("B", tipoX.getAttributeValue("atTeste"));
        assertEquals("B", tipoX.getAttributeValue("teste.XXXXX.atTeste"));

        tipoX.setAttributeValue("teste.XXXXX.atTeste", "C");
        assertEquals("C", tipoX.getAttributeValue("atTeste"));
        assertEquals("C", tipoX.getAttributeValue("teste.XXXXX.atTeste"));
    }

    @Test
    public void testDefaultNameForType() {
        SDictionary dictionary = createTestDictionary();
        assertEquals("TestTipoA", dictionary.getType(TestTipoA.class).getNameSimple());
        assertEquals("TestTipoB", dictionary.getType(TestTipoB.class).getNameSimple());
        assertEquals("TX", dictionary.getType(TestTipoX.class).getNameSimple());
    }

    public static final class TestPacoteA extends SPackage {

        static final AtrRef<STypeInteger, SIInteger, Integer> ATR_XX = new AtrRef<>(TestPacoteA.class, "xx", STypeInteger.class,
                SIInteger.class, Integer.class);

        protected TestPacoteA() {
            super("teste.pacoteA");
        }

        @Override
        protected void carregarDefinicoes(PackageBuilder pb) {
            pb.createAttributeType(ATR_XX);
            pb.addAttribute(SType.class, ATR_XX);

            pb.createType(TestTipoA.class);
            pb.createType("TestTipoAA", TestTipoA.class);
            pb.createType(TestTipoComCargaInterna.class);
            pb.createType(TestTipoB.class);
            pb.createType(TestTipoX.class);
        }

        @SInfoType(name = "TestTipoA", spackage = TestPacoteA.class)
        public static final class TestTipoA extends STypeInteger {
        }

        @SInfoType(name = "TestTipoComCargaInterna", spackage = TestPacoteA.class)
        public static final class TestTipoComCargaInterna extends STypeInteger {
            @Override
            protected void onLoadType(TypeBuilder tb) {
                withRequired(true);
                withInitialValue(10);
                withDefaultValueIfNull(11);
                with(TestPacoteA.ATR_XX, 12);
            }
        }

        @SInfoType(spackage = TestPacoteA.class)
        public static final class TestTipoB extends STypeInteger {
        }

        @SInfoType(name = "TX", spackage = TestPacoteA.class)
        public static final class TestTipoX extends STypeInteger {
        }
    }

    @SInfoPackage(name = "teste.pacoteB")
    public static final class TestPacoteB extends SPackage {
        static final AtrRef<STypeInteger, SIInteger, Integer> ATR_LABEL_Y = new AtrRef<>(TestPacoteB.class, "yy", STypeInteger.class,
                SIInteger.class, Integer.class);

        @Override
        protected void carregarDefinicoes(PackageBuilder pb) {
            pb.createType("TestTipoB", TestTipoA.class);

            pb.createAttributeIntoType(SType.class, ATR_LABEL_Y);
        }

    }

    @Test
    public void testPackageName() {
        assertEquals("teste.pacoteB", SFormUtil.getInfoPackageName(TestPacoteB.class));
        assertNull(SFormUtil.getInfoPackageName(TestPacoteA.class));

        SDictionary dictionary = createTestDictionary();
        TestPacoteA pacoteA = dictionary.loadPackage(TestPacoteA.class);
        TestPacoteB pacoteB = dictionary.loadPackage(TestPacoteB.class);

        assertEquals("teste.pacoteA", pacoteA.getName());
        assertEquals("teste.pacoteB", pacoteB.getName());
    }

    @Test
    public void testCargaSimplesPacote() {
        SDictionary dictionary = createTestDictionary();
        dictionary.loadPackage(TestPacoteA.class);
        assertTrue(dictionary.getPackages().stream().anyMatch(p -> p.getName().equals("teste.pacoteA")));
        assertNotNull(dictionary.getTypeOptional(TestTipoA.class));
        assertNotNull(dictionary.getTypeOptional("teste.pacoteA.TestTipoA"));
        assertNotNull(dictionary.getTypeOptional("teste.pacoteA.TestTipoAA"));
    }

    @Test
    public void testCargaAutomaticaPacotePorUsoReferenciaDeClasseDeUmTipo() {
        SDictionary dictionary = createTestDictionary();
        dictionary.loadPackage(TestPacoteB.class);
        assertTrue(dictionary.getPackages().stream().anyMatch(p -> p.getName().equals("teste.pacoteA")));
        assertTrue(dictionary.getPackages().stream().anyMatch(p -> p.getName().equals("teste.pacoteB")));
        assertNotNull(dictionary.getTypeOptional(TestTipoA.class));
        assertNotNull(dictionary.getTypeOptional("teste.pacoteA.TestTipoA"));
        assertNotNull(dictionary.getTypeOptional("teste.pacoteA.TestTipoAA"));
        assertNotNull(dictionary.getTypeOptional("teste.pacoteB.TestTipoB"));
    }

    @Test
    public void testCargaAutomaticaPacotePorInstanciarUmTipo() {
        SDictionary dictionary = createTestDictionary();
        SIString    is         = dictionary.newInstance(STypeString.class);
        assertNotNull(is);

        SIInteger instancia = dictionary.newInstance(TestTipoA.class);
        instancia.setValue(10);
        assertEquals((Integer) 10, instancia.getValue());
        assertCargaPacoteA(dictionary, true);
    }

    @Test
    public void testCargaAutomaticaPacotePorUsarUmAtributo() {
        SDictionary dictionary = createTestDictionary();
        PackageBuilder pb = dictionary.createNewPackage("teste");

        assertCargaPacoteA(dictionary, false);

        SType<SIString> tipoEndereco = pb.createType("endereco", STypeString.class).with(TestPacoteA.ATR_XX, 10);

        assertCargaPacoteA(dictionary, true);

        assertEquals((Integer) 10, tipoEndereco.getAttributeValue(TestPacoteA.ATR_XX));
        assertEquals(null, dictionary.getTypeOptional(STypeString.class).getAttributeValue(TestPacoteA.ATR_XX));
        assertEquals(null, dictionary.getTypeOptional(STypeSimple.class).getAttributeValue(TestPacoteA.ATR_XX));
        assertEquals(null, dictionary.getTypeOptional(SType.class).getAttributeValue(TestPacoteA.ATR_XX));
    }

    @Test
    public void testCargaAutomaticaPacotePorDarAddEmUmAtributo() {
        SDictionary dictionary = createTestDictionary();
        PackageBuilder pb = dictionary.createNewPackage("teste");

        assertCargaPacoteA(dictionary, false);
        pb.addAttribute(STypeInteger.class, TestPacoteA.ATR_XX);
        assertCargaPacoteA(dictionary, true);
    }

    @Test
    public void testCargaAutomaticaPacotePorLerUmAtributo() {
        SDictionary dictionary = createTestDictionary();
        dictionary.createNewPackage("teste");

        assertCargaPacoteA(dictionary, false);
        assertEquals(null, dictionary.getType(STypeString.class).getAttributeValue(TestPacoteA.ATR_XX));
        assertCargaPacoteA(dictionary, true);
    }

    private static void assertCargaPacoteA(SDictionary dictionary, boolean carregado) {
        if (carregado) {
            assertTrue(dictionary.getPackages().stream().anyMatch(p -> p.getName().equals("teste.pacoteA")));
            assertNotNull(dictionary.getTypeOptional("teste.pacoteA.TestTipoAA"));
            assertNotNull(dictionary.getTypeOptional("teste.pacoteA.xx"));
        } else {
            assertFalse(dictionary.getPackages().stream().anyMatch(p -> p.getName().equals("teste.pacoteA")));
            assertNull(dictionary.getTypeOptional("teste.pacoteA.TestTipoAA"));
            assertNull(dictionary.getTypeOptional("teste.pacoteA.xx"));
        }
    }

    @Test
    public void testSeTipoBaseadoEmClasseCarregaConfiguracaoInternaDaClasse() {
        SDictionary dictionary = createTestDictionary();
        dictionary.loadPackage(TestPacoteA.class);
        TestTipoComCargaInterna tipo = dictionary.getType(TestTipoComCargaInterna.class);

        assertEquals((Boolean) true, tipo.isRequired());
        assertEquals((Integer) 10, tipo.getAttributeValueInitialValue());
        assertEquals((Integer) 11, tipo.getAttributeValueOrDefaultValueIfNull());
        assertEquals((Integer) 12, tipo.getAttributeValue(TestPacoteA.ATR_XX));
    }

    @Test
    public void testSeTipoBaseadoEmClasseCarregaConfiguracaoInternaDaClasseAoExtender() {
        SDictionary dictionary = createTestDictionary();
        PackageBuilder pb = dictionary.createNewPackage("teste");
        TestTipoComCargaInterna tipo = pb.createType("derivado", TestTipoComCargaInterna.class);

        assertEquals((Boolean) true, tipo.isRequired());
        assertEquals((Integer) 10, tipo.getAttributeValueInitialValue());
        assertEquals((Integer) 11, tipo.getAttributeValueOrDefaultValueIfNull());
        assertEquals((Integer) 12, tipo.getAttributeValue(TestPacoteA.ATR_XX));
    }

    @Test
    public void testCargaTipoNoPacoteTrocado() {
        PackageBuilder pb = createTestDictionary().createNewPackage("teste");
        assertException(() -> pb.createType(TestPacoteA.TestTipoA.class), "como sendo do pacote",
                "Deveria dar uma exception pois o tipo tem a anotação para entrar em outro pacote");
    }

    @Test
    public void testCargaAtributoNoPacoteTrocado() {
        PackageBuilder pb = createTestDictionary().createNewPackage("teste");

        assertException(() -> pb.createAttributeType(TestPacoteA.ATR_XX), "Tentativa de criar o atributo",
                "Deveria dar uma exception pois o atributo pertence a outro pacote");
    }

    @Test
    public void testAutomaticLoadOfSingularTypeByName() {
        assertEquals(SPackageCore.NAME + ".String", SFormUtil.getTypeName(STypeString.class));

        loadTypeByName((Class) STypeSimple.class);
        loadTypeByName(STypeString.class);
        loadTypeByName(STypeDate.class);
        loadTypeByName(STypeBehavior.class);
        loadTypeByName(STypeEMail.class);
        loadTypeByName(STypeCEP.class);
    }

    private void loadTypeByName(Class<? extends SType<?>> typeClass) {
        String typeName = SFormUtil.getTypeName(typeClass);
        createTestDictionary().getType(typeName);
    }
}
