package org.opensingular.form;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.SPackageTest.TestPacoteA.TestTipoA;
import org.opensingular.form.SPackageTest.TestPacoteA.TestTipoB;
import org.opensingular.form.SPackageTest.TestPacoteA.TestTipoComCargaInterna;
import org.opensingular.form.SPackageTest.TestPacoteA.TestTipoX;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.type.core.SIInteger;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.SPackageCore;
import org.opensingular.form.type.core.STypeBoolean;
import org.opensingular.form.type.core.STypeDate;
import org.opensingular.form.type.core.STypeDecimal;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.country.brazil.STypeCEP;
import org.opensingular.form.type.util.STypeEMail;
import org.opensingular.internal.lib.commons.test.SingularTestUtil;

import java.math.BigDecimal;

@RunWith(Parameterized.class)
public class SPackageTest extends TestCaseForm {

    public SPackageTest(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test
    public void testRecuperarTipo() {
        createTestDictionary().getType(STypeString.class);
    }

    @Test
    public void testHerancaValorEntreTipos() {
        SDictionary dictionary = createTestDictionary();

        STypeSimple<?, ?> tipoS = dictionary.getType(STypeSimple.class);
        STypeBoolean      tipoB = dictionary.getType(STypeBoolean.class);
        STypeInteger      tipoI = dictionary.getType(STypeInteger.class);

        Assert.assertFalse(tipoS.getAttributeValue(SPackageBasic.ATR_REQUIRED));
        Assert.assertFalse(tipoB.getAttributeValue(SPackageBasic.ATR_REQUIRED));
        Assert.assertFalse(tipoI.getAttributeValue(SPackageBasic.ATR_REQUIRED));

        tipoB.asAtr().required(true);

        Assert.assertEquals(false, tipoS.isRequired());
        Assert.assertEquals(true, tipoB.isRequired());
        Assert.assertEquals(false, tipoI.isRequired());

        tipoB.asAtr().required(true);
        tipoS.asAtr().required(false);

        Assert.assertEquals(false, tipoS.isRequired());
        Assert.assertEquals(true, tipoB.isRequired());
        Assert.assertEquals(false, tipoI.isRequired());

        tipoB.asAtr().required(false);

        Assert.assertEquals(false, tipoS.isRequired());
        Assert.assertEquals(false, tipoB.isRequired());
        Assert.assertEquals(false, tipoI.isRequired());

        tipoS.asAtr().required(true);
        tipoB.asAtr().required(false);

        Assert.assertEquals(true, tipoS.isRequired());
        Assert.assertEquals(false, tipoB.isRequired());
        Assert.assertEquals(true, tipoI.isRequired());
    }

    @Test
    public void testValidacaoBasica() {
        SDictionary dictionary = createTestDictionary();
        STypeBoolean tipoB = dictionary.getType(STypeBoolean.class);
        STypeInteger tipoI = dictionary.getType(STypeInteger.class);
        STypeString tipoS = dictionary.getType(STypeString.class);

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

        assertNull(tipoS.findAttributeInstance(SPackageBasic.ATR_EMPTY_TO_NULL));
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

        STypeSimple<?, ?> tipoS = dictionary.getType(STypeSimple.class);
        STypeBoolean tipoB = dictionary.getType(STypeBoolean.class);
        STypeInteger tipoI = dictionary.getType(STypeInteger.class);

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
        PackageBuilder pb = createTestPackage();

        pb.createType("CPF", STypeString.class);
        assertException(() -> pb.createType("CPF", STypeString.class), "já está criada");
    }

    @Test
    public void testCriacaoAtributoLocal() {
        PackageBuilder pb = createTestPackage();

        // AtrRef<?, ?, ?> atr = new AtrRef(null, "atTeste", MTipoString.class,
        // MIString.class, String.class);

        STypeString tipoX = pb.createType("XXXXX", STypeString.class);
        STypeString atributo = pb.createAttributeIntoType(tipoX, "atTeste", STypeString.class);

        assertNull(pb.getPackage().getLocalTypeOptional("YYYYYYY").orElse(null));

        assertNull(pb.getPackage().getLocalTypeOptional("atTeste").orElse(null));
        assertNotNull(pb.getPackage().getLocalType("XXXXX"));
        assertNotNull(tipoX.getLocalType("atTeste"));

        assertEquals(null, tipoX.getAttributeValue("teste.XXXXX.atTeste"));

        atributo.withDefaultValueIfNull("0");
        assertEquals("0", tipoX.getAttributeValue("teste.XXXXX.atTeste"));

        tipoX.setAttributeValue(atributo, "A");
        assertEquals("A", tipoX.getAttributeValue("teste.XXXXX.atTeste"));

        tipoX.setAttributeValue("teste.XXXXX.atTeste", "C");
        assertEquals("C", tipoX.getAttributeValue("teste.XXXXX.atTeste"));
    }

    @Test
    public void testDefaultNameForType() {
        SDictionary dictionary = createTestDictionary();
        TestCase.assertEquals("TestTipoA", dictionary.getType(TestTipoA.class).getNameSimple());
        TestCase.assertEquals("TestTipoB", dictionary.getType(TestTipoB.class).getNameSimple());
        TestCase.assertEquals("TX", dictionary.getType(TestTipoX.class).getNameSimple());
    }

    @SInfoPackage(name = "teste.pacoteA")
    public static final class TestPacoteA extends SPackage {

        static final AtrRef<STypeInteger, SIInteger, Integer> ATR_XX = new AtrRef<>(TestPacoteA.class, "xx", STypeInteger.class,
                SIInteger.class, Integer.class);

        @Override
        protected void onLoadPackage(PackageBuilder pb) {
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
                asAtr().required(true);
                setInitialValue(10);
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
        protected void onLoadPackage(PackageBuilder pb) {
            pb.createType("TestTipoB", TestTipoA.class);

            pb.createAttributeIntoType(SType.class, ATR_LABEL_Y);
        }

    }

    public static final class TestPacoteC extends SPackage {

    }

    @Test
    public void testPackageName() {
        assertEquals("teste.pacoteA", SFormUtil.getInfoPackageName(TestPacoteA.class));
        assertEquals("teste.pacoteB", SFormUtil.getInfoPackageName(TestPacoteB.class));
        assertEquals(TestPacoteC.class.getName(), SFormUtil.getInfoPackageName(TestPacoteC.class));

        SDictionary dictionary = createTestDictionary();
        TestPacoteA pacoteA    = dictionary.loadPackage(TestPacoteA.class);
        TestPacoteB pacoteB    = dictionary.loadPackage(TestPacoteB.class);
        TestPacoteC pacoteC    = dictionary.loadPackage(TestPacoteC.class);

        assertEquals("teste.pacoteA", pacoteA.getName());
        assertEquals("teste.pacoteB", pacoteB.getName());
        assertEquals(TestPacoteC.class.getName(), pacoteC.getName());
    }

    @Test
    public void testCargaSimplesPacote() {
        SDictionary dictionary = createTestDictionary();
        dictionary.loadPackage(TestPacoteA.class);
        assertTrue(dictionary.getPackages().stream().anyMatch(p -> p.getName().equals("teste.pacoteA")));
        TestCase.assertNotNull(dictionary.getType(TestTipoA.class));
        assertNotNull(dictionary.getTypeOptional("teste.pacoteA.TestTipoA").get());
        assertNotNull(dictionary.getTypeOptional("teste.pacoteA.TestTipoAA").get());
    }

    @Test
    public void testCargaAutomaticaPacotePorUsoReferenciaDeClasseDeUmTipo() {
        SDictionary dictionary = createTestDictionary();
        dictionary.loadPackage(TestPacoteB.class);
        assertTrue(dictionary.getPackages().stream().anyMatch(p -> p.getName().equals("teste.pacoteA")));
        assertTrue(dictionary.getPackages().stream().anyMatch(p -> p.getName().equals("teste.pacoteB")));
        TestCase.assertNotNull(dictionary.getType(TestTipoA.class));
        assertNotNull(dictionary.getTypeOptional("teste.pacoteA.TestTipoA").get());
        assertNotNull(dictionary.getTypeOptional("teste.pacoteA.TestTipoAA").get());
        assertNotNull(dictionary.getTypeOptional("teste.pacoteB.TestTipoB").get());
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
        PackageBuilder pb         = createTestPackage();
        SDictionary    dictionary = pb.getDictionary();

        assertCargaPacoteA(dictionary, false);

        SType<SIString> tipoEndereco = pb.createType("endereco", STypeString.class).with(TestPacoteA.ATR_XX, 10);

        assertCargaPacoteA(dictionary, true);

        assertEquals((Integer) 10, tipoEndereco.getAttributeValue(TestPacoteA.ATR_XX));
        assertEquals(null, dictionary.getType(STypeString.class).getAttributeValue(TestPacoteA.ATR_XX));
        assertEquals(null, dictionary.getType(STypeSimple.class).getAttributeValue(TestPacoteA.ATR_XX));
        assertEquals(null, dictionary.getType(SType.class).getAttributeValue(TestPacoteA.ATR_XX));
    }

    @Test
    public void testCargaAutomaticaPacotePorDarAddEmUmAtributo() {
        PackageBuilder pb         = createTestPackage();

        assertCargaPacoteA(pb.getDictionary(), false);
        pb.addAttribute(STypeInteger.class, TestPacoteA.ATR_XX);
        assertCargaPacoteA(pb.getDictionary(), true);
    }

    @Test
    public void testCargaAutomaticaPacotePorLerUmAtributo() {
        SDictionary dictionary = createTestDictionary();

        assertCargaPacoteA(dictionary, false);
        assertEquals(null, dictionary.getType(STypeString.class).getAttributeValue(TestPacoteA.ATR_XX));
        assertCargaPacoteA(dictionary, true);
    }

    private static void assertCargaPacoteA(SDictionary dictionary, boolean carregado) {
        if (carregado) {
            assertTrue(dictionary.getPackages().stream().anyMatch(p -> p.getName().equals("teste.pacoteA")));
            assertNotNull(dictionary.getTypeOptional("teste.pacoteA.TestTipoAA").get());
            assertNotNull(dictionary.getTypeOptional("teste.pacoteA.xx").get());
        } else {
            assertFalse(dictionary.getPackages().stream().anyMatch(p -> p.getName().equals("teste.pacoteA")));
            assertNull(dictionary.getTypeOptional("teste.pacoteA.TestTipoAA").orElse(null));
            assertNull(dictionary.getTypeOptional("teste.pacoteA.xx").orElse(null));
        }
    }

    @Test
    public void testSeTipoBaseadoEmClasseCarregaConfiguracaoInternaDaClasse() {
        SDictionary dictionary = createTestDictionary();
        dictionary.loadPackage(TestPacoteA.class);
        TestTipoComCargaInterna tipo = dictionary.getType(TestTipoComCargaInterna.class);

        TestCase.assertEquals((Boolean) true, tipo.isRequired());
        TestCase.assertEquals((Integer) 10, tipo.newInstance().getValue());
        TestCase.assertEquals((Integer) 11, tipo.getAttributeValueOrDefaultValueIfNull());
        TestCase.assertEquals((Integer) 12, tipo.getAttributeValue(TestPacoteA.ATR_XX));
    }

    @Test
    public void testSeTipoBaseadoEmClasseCarregaConfiguracaoInternaDaClasseAoExtender() {
        SDictionary             dictionary = createTestDictionary();
        PackageBuilder          pb         = dictionary.createNewPackage("teste");
        TestTipoComCargaInterna tipo       = pb.createType("derivado", TestTipoComCargaInterna.class);

        TestCase.assertEquals((Boolean) true, tipo.isRequired());
        TestCase.assertEquals((Integer) 10, tipo.newInstance().getValue());
        TestCase.assertEquals((Integer) 11, tipo.getAttributeValueOrDefaultValueIfNull());
        TestCase.assertEquals((Integer) 12, tipo.getAttributeValue(TestPacoteA.ATR_XX));
    }

    @Test
    public void testCargaTipoNoPacoteTrocado() {
        PackageBuilder pb = createTestPackage();
        assertException(() -> pb.createType(TestPacoteA.TestTipoA.class), "como sendo do pacote",
                "Deveria dar uma exception pois o tipo tem a anotação para entrar em outro pacote");
    }

    @Test
    public void testCargaAtributoNoPacoteTrocado() {
        PackageBuilder pb = createTestPackage();

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

    /**
     * Testa o funcionamento de carga de apenas alguns tipo de um pacote quando o pacote não declara todos os seus
     * tipo.
     */
    @Test
    public void testLazyPackageLoad() {
        // Simple reference
        SDictionary dictionary = createTestDictionary();
        SPackageTestLazy.TypeLazyA typeA = dictionary.getType(SPackageTestLazy.TypeLazyA.class);
        assertType(typeA).isNotNull().isDirectExtensionOf(STypeDecimal.class);
        typeA.newInstance();

        // A reference with other reference
        dictionary = createTestDictionary();
        SPackageTestLazy.TypeLazyB typeB = dictionary.getType(SPackageTestLazy.TypeLazyB.class);
        assertType(typeB).isNotNull().isDirectExtensionOf(SPackageTestLazy.TypeLazyA.class);
        typeB.newInstance();

        // A lazy composite referencing a lazy simple type
        dictionary = createTestDictionary();
        SPackageTestLazy.TypeLazyC typeC = dictionary.getType(SPackageTestLazy.TypeLazyC.class);
        assertType(typeC).isNotNull();
        SIComposite iC = typeC.newInstance();
        iC.setValue("valueB", 10);
        iC.setValue("valueA", new BigDecimal(100.1));
        assertInstance(iC).isValueEquals("valueB" , new BigDecimal(10));
        assertInstance(iC).isValueEquals("valueA" , new BigDecimal(100.1));
    }

    @SInfoPackage(name="packageLazy")
    public static class SPackageTestLazy extends SPackage {

        @SInfoType(name = "LazyA", spackage = SPackageTestLazy.class)
        public static class TypeLazyA extends STypeDecimal {
        }

        @SInfoType(name = "LazyB", spackage = SPackageTestLazy.class)
        public static class TypeLazyB extends TypeLazyA {

        }
        @SInfoType(name = "LazyC", spackage = SPackageTestLazy.class)
        public static class TypeLazyC extends STypeComposite<SIComposite> {
            @Override
            protected void onLoadType(TypeBuilder tb) {
                addField("valueB", TypeLazyB.class);
                addField("valueA", TypeLazyA.class);
                addFieldString("name");
            }
        }
    }

    @Test
    public void testDerivatedSPackageShouldNotPassNameInConstructor() {
        SDictionary dictionary = createTestDictionary();
        dictionary.createNewPackage("ok.not.extenstion");

        SingularTestUtil.assertException(() -> createTestDictionary().loadPackage(PackageUsingWrongName.class),
                SingularFormException.class, "não deve ser usado o construtor SPackage(String)");
    }

    public static class PackageUsingWrongName extends SPackage {
        public PackageUsingWrongName() {
            super("no.name.expected.here");
        }
    }
}
