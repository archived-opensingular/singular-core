package br.net.mirante.singular.form.mform;

import org.junit.Assert;

import br.net.mirante.singular.form.mform.TestMPacoteCore.TestPacoteA.TestTipoA;
import br.net.mirante.singular.form.mform.TestMPacoteCore.TestPacoteA.TestTipoComCargaInterna;
import br.net.mirante.singular.form.mform.core.SIInteger;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.SPackageCore;
import br.net.mirante.singular.form.mform.core.STypeBoolean;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;

public class TestMPacoteCore extends TestCaseForm {

    public void testBasicLoad() {
        SDictionary.create();
    }

    public void testRecuperarTipo() {
        SDictionary dicionario = SDictionary.create();

        dicionario.getTypeOptional(STypeString.class);
    }

    public void testHerancaValorEntreTipos() {
        SDictionary dicionario = SDictionary.create();

        STypeSimple<?, ?> tipoS = dicionario.getTypeOptional(STypeSimple.class);
        STypeBoolean tipoB = dicionario.getTypeOptional(STypeBoolean.class);
        STypeInteger tipoI = dicionario.getTypeOptional(STypeInteger.class);

        Assert.assertFalse(tipoS.getAttributeValue(SPackageCore.ATR_REQUIRED));
        Assert.assertFalse(tipoB.getAttributeValue(SPackageCore.ATR_REQUIRED));
        Assert.assertFalse(tipoI.getAttributeValue(SPackageCore.ATR_REQUIRED));

        tipoB.withRequired(true);

        Assert.assertEquals(false, tipoS.isRequired());
        Assert.assertEquals(true, tipoB.isRequired());
        Assert.assertEquals(false, tipoI.isRequired());

        tipoS.withRequired(false);

        Assert.assertEquals(false, tipoS.isRequired());
        Assert.assertEquals(true, tipoB.isRequired());
        Assert.assertEquals(false, tipoI.isRequired());

        tipoB.withRequired(null);

        Assert.assertEquals(false, tipoS.isRequired());
        Assert.assertEquals(null, tipoB.isRequired());
        Assert.assertEquals(false, tipoI.isRequired());
    }

    public void testValidacaoBasica() {
        SDictionary dicionario = SDictionary.create();
        STypeBoolean tipoB = dicionario.getTypeOptional(STypeBoolean.class);
        STypeInteger tipoI = dicionario.getTypeOptional(STypeInteger.class);
        STypeString tipoS = dicionario.getTypeOptional(STypeString.class);

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

        assertNull(tipoS.getAttributeInstance(SPackageCore.ATR_EMPTY_TO_NULL));
        assertEquals(tipoS.getAttributeValue(SPackageCore.ATR_EMPTY_TO_NULL), Boolean.TRUE);
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

    public void testSelfReference() {
        SDictionary dicionario = SDictionary.create();

        STypeSimple<?, ?> tipoS = dicionario.getTypeOptional(STypeSimple.class);
        STypeBoolean tipoB = dicionario.getTypeOptional(STypeBoolean.class);
        STypeInteger tipoI = dicionario.getTypeOptional(STypeInteger.class);

        Assert.assertNull(tipoS.getAttributeValue(SPackageCore.ATR_DEFAULT_IF_NULL));
        Assert.assertNull(tipoB.getAttributeValue(SPackageCore.ATR_DEFAULT_IF_NULL));
        Assert.assertNull(tipoI.getAttributeValue(SPackageCore.ATR_DEFAULT_IF_NULL));

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

    public void testCriacaoDuplicada() {
        SDictionary dicionario = SDictionary.create();
        PackageBuilder pb = dicionario.createNewPackage("teste");

        pb.createType("CPF", STypeString.class);
        assertException(() -> pb.createType("CPF", STypeString.class), "já está criada");
    }

    public void testCriacaoAtributoLocal() {
        SDictionary dicionario = SDictionary.create();
        PackageBuilder pb = dicionario.createNewPackage("teste");

        // AtrRef<?, ?, ?> atr = new AtrRef(null, "atTeste", MTipoString.class,
        // MIString.class, String.class);

        STypeString tipoX = pb.createType("XXXXX", STypeString.class);
        SAttribute atributo = pb.createAttributeIntoType(tipoX, "atTeste", STypeString.class);

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

    }

    public static final class TestPacoteB extends SPackage {
        static final AtrRef<STypeInteger, SIInteger, Integer> ATR_LABEL_Y = new AtrRef<>(TestPacoteB.class, "yy", STypeInteger.class,
                SIInteger.class, Integer.class);

        protected TestPacoteB() {
            super("teste.pacoteB");
        }

        @Override
        protected void carregarDefinicoes(PackageBuilder pb) {
            pb.createType("TestTipoB", TestTipoA.class);

            pb.createAttributeIntoType(SType.class, ATR_LABEL_Y);
        }

    }

    public void testCargaSimplesPacote() {
        SDictionary dicionario = SDictionary.create();
        dicionario.loadPackage(TestPacoteA.class);
        assertTrue(dicionario.getPackages().stream().anyMatch(p -> p.getName().equals("teste.pacoteA")));
        assertNotNull(dicionario.getTypeOptional(TestTipoA.class));
        assertNotNull(dicionario.getTypeOptional("teste.pacoteA.TestTipoA"));
        assertNotNull(dicionario.getTypeOptional("teste.pacoteA.TestTipoAA"));
    }

    public void testCargaAutomaticaPacotePorUsoReferenciaDeClasseDeUmTipo() {
        SDictionary dicionario = SDictionary.create();
        dicionario.loadPackage(TestPacoteB.class);
        assertTrue(dicionario.getPackages().stream().anyMatch(p -> p.getName().equals("teste.pacoteA")));
        assertTrue(dicionario.getPackages().stream().anyMatch(p -> p.getName().equals("teste.pacoteB")));
        assertNotNull(dicionario.getTypeOptional(TestTipoA.class));
        assertNotNull(dicionario.getTypeOptional("teste.pacoteA.TestTipoA"));
        assertNotNull(dicionario.getTypeOptional("teste.pacoteA.TestTipoAA"));
        assertNotNull(dicionario.getTypeOptional("teste.pacoteB.TestTipoB"));
    }

    public void testCargaAutomaticaPacotePorInstanciarUmTipo() {
        SDictionary dicionario = SDictionary.create();
        SIString is = dicionario.newInstance(STypeString.class);
        assertNotNull(is);

        SIInteger instancia = dicionario.newInstance(TestTipoA.class);
        instancia.setValue(10);
        assertEquals((Integer) 10, instancia.getValue());
        assertCargaPacoteA(dicionario, true);
    }

    public void testCargaAutomaticaPacotePorUsarUmAtributo() {
        SDictionary dicionario = SDictionary.create();
        PackageBuilder pb = dicionario.createNewPackage("teste");

        assertCargaPacoteA(dicionario, false);

        SType<SIString> tipoEndereco = pb.createType("endereco", STypeString.class).with(TestPacoteA.ATR_XX, 10);

        assertCargaPacoteA(dicionario, true);

        assertEquals((Integer) 10, tipoEndereco.getAttributeValue(TestPacoteA.ATR_XX));
        assertEquals(null, dicionario.getTypeOptional(STypeString.class).getAttributeValue(TestPacoteA.ATR_XX));
        assertEquals(null, dicionario.getTypeOptional(STypeSimple.class).getAttributeValue(TestPacoteA.ATR_XX));
        assertEquals(null, dicionario.getTypeOptional(SType.class).getAttributeValue(TestPacoteA.ATR_XX));
    }

    public void testCargaAutomaticaPacotePorDarAddEmUmAtributo() {
        SDictionary dicionario = SDictionary.create();
        PackageBuilder pb = dicionario.createNewPackage("teste");

        assertCargaPacoteA(dicionario, false);
        pb.addAttribute(STypeInteger.class, TestPacoteA.ATR_XX);
        assertCargaPacoteA(dicionario, true);
    }

    public void testCargaAutomaticaPacotePorLerUmAtributo() {
        SDictionary dicionario = SDictionary.create();
        dicionario.createNewPackage("teste");

        assertCargaPacoteA(dicionario, false);
        assertEquals(null, dicionario.getType(STypeString.class).getAttributeValue(TestPacoteA.ATR_XX));
        assertCargaPacoteA(dicionario, true);
    }

    private static void assertCargaPacoteA(SDictionary dicionario, boolean carregado) {
        if (carregado) {
            assertTrue(dicionario.getPackages().stream().anyMatch(p -> p.getName().equals("teste.pacoteA")));
            assertNotNull(dicionario.getTypeOptional("teste.pacoteA.TestTipoAA"));
            assertNotNull(dicionario.getTypeOptional("teste.pacoteA.xx"));
        } else {
            assertFalse(dicionario.getPackages().stream().anyMatch(p -> p.getName().equals("teste.pacoteA")));
            assertNull(dicionario.getTypeOptional("teste.pacoteA.TestTipoAA"));
            assertNull(dicionario.getTypeOptional("teste.pacoteA.xx"));
        }
    }

    public void testSeTipoBaseadoEmClasseCarregaConfiguracaoInternaDaClasse() {
        SDictionary dicionario = SDictionary.create();
        dicionario.loadPackage(TestPacoteA.class);
        TestTipoComCargaInterna tipo = dicionario.getType(TestTipoComCargaInterna.class);

        assertEquals((Boolean) true, tipo.isRequired());
        assertEquals((Integer) 10, tipo.getAttributeValueInitialValue());
        assertEquals((Integer) 11, tipo.getAttributeValueOrDefaultValueIfNull());
        assertEquals((Integer) 12, tipo.getAttributeValue(TestPacoteA.ATR_XX));
    }

    public void testSeTipoBaseadoEmClasseCarregaConfiguracaoInternaDaClasseAoExtender() {
        SDictionary dicionario = SDictionary.create();
        PackageBuilder pb = dicionario.createNewPackage("teste");
        TestTipoComCargaInterna tipo = pb.createType("derivado", TestTipoComCargaInterna.class);

        assertEquals((Boolean) true, tipo.isRequired());
        assertEquals((Integer) 10, tipo.getAttributeValueInitialValue());
        assertEquals((Integer) 11, tipo.getAttributeValueOrDefaultValueIfNull());
        assertEquals((Integer) 12, tipo.getAttributeValue(TestPacoteA.ATR_XX));
    }

    public void testCargaTipoNoPacoteTrocado() {
        SDictionary dicionario = SDictionary.create();
        PackageBuilder pb = dicionario.createNewPackage("teste");
        assertException(() -> pb.createType(TestPacoteA.TestTipoA.class), "como sendo do pacote",
                "Deveria dar uma exception pois o tipo tem a anotação para entrar em outro pacote");
    }

    public void testCargaAtributoNoPacoteTrocado() {
        SDictionary dicionario = SDictionary.create();
        PackageBuilder pb = dicionario.createNewPackage("teste");

        assertException(() -> pb.createAttributeType(TestPacoteA.ATR_XX), "Tentativa de criar o atributo",
                "Deveria dar uma exception pois o atributo pertence a outro pacote");
    }
}
