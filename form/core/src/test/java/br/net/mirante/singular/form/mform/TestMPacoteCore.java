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

        dicionario.getTipoOpcional(STypeString.class);
    }

    public void testHerancaValorEntreTipos() {
        SDictionary dicionario = SDictionary.create();

        STypeSimples<?, ?> tipoS = dicionario.getTipoOpcional(STypeSimples.class);
        STypeBoolean tipoB = dicionario.getTipoOpcional(STypeBoolean.class);
        STypeInteger tipoI = dicionario.getTipoOpcional(STypeInteger.class);

        Assert.assertFalse(tipoS.getValorAtributo(SPackageCore.ATR_OBRIGATORIO));
        Assert.assertFalse(tipoB.getValorAtributo(SPackageCore.ATR_OBRIGATORIO));
        Assert.assertFalse(tipoI.getValorAtributo(SPackageCore.ATR_OBRIGATORIO));

        tipoB.withObrigatorio(true);

        Assert.assertEquals(false, tipoS.isObrigatorio());
        Assert.assertEquals(true, tipoB.isObrigatorio());
        Assert.assertEquals(false, tipoI.isObrigatorio());

        tipoS.withObrigatorio(false);

        Assert.assertEquals(false, tipoS.isObrigatorio());
        Assert.assertEquals(true, tipoB.isObrigatorio());
        Assert.assertEquals(false, tipoI.isObrigatorio());

        tipoB.withObrigatorio(null);

        Assert.assertEquals(false, tipoS.isObrigatorio());
        Assert.assertEquals(null, tipoB.isObrigatorio());
        Assert.assertEquals(false, tipoI.isObrigatorio());
    }

    public void testValidacaoBasica() {
        SDictionary dicionario = SDictionary.create();
        STypeBoolean tipoB = dicionario.getTipoOpcional(STypeBoolean.class);
        STypeInteger tipoI = dicionario.getTipoOpcional(STypeInteger.class);
        STypeString tipoS = dicionario.getTipoOpcional(STypeString.class);

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

        assertNull(tipoS.getInstanciaAtributo(SPackageCore.ATR_EMPTY_TO_NULL));
        assertEquals(tipoS.getValorAtributo(SPackageCore.ATR_EMPTY_TO_NULL), Boolean.TRUE);
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

    private static void testarAtribuicao(STypeSimples<?, ?> tipo, boolean valorValido, Object valor, Object valorFinalEsperado) {
        SISimple<?> instancia = tipo.novaInstancia();
        if (valorValido) {
            instancia.setValor(valor);
            Object resultado = instancia.getValor();
            Assert.assertEquals(valorFinalEsperado, resultado);

            Object resultado2 = instancia.getMTipo().converter(valor, instancia.getMTipo().getClasseTipoNativo());
            Assert.assertEquals(resultado, resultado2);
        } else {
            assertException(() -> instancia.setValor(valor), "não consegue converter", "Deveria dar erro de conversão");

            Assert.assertEquals(valorFinalEsperado, instancia.getValor());

            assertException(() -> instancia.getMTipo().converter(valor, instancia.getMTipo().getClasseTipoNativo()),
                    "não consegue converter", "Deveria dar erro de conversão");
        }
    }

    public void testSelfReference() {
        SDictionary dicionario = SDictionary.create();

        STypeSimples<?, ?> tipoS = dicionario.getTipoOpcional(STypeSimples.class);
        STypeBoolean tipoB = dicionario.getTipoOpcional(STypeBoolean.class);
        STypeInteger tipoI = dicionario.getTipoOpcional(STypeInteger.class);

        Assert.assertNull(tipoS.getValorAtributo(SPackageCore.ATR_DEFAULT_IF_NULL));
        Assert.assertNull(tipoB.getValorAtributo(SPackageCore.ATR_DEFAULT_IF_NULL));
        Assert.assertNull(tipoI.getValorAtributo(SPackageCore.ATR_DEFAULT_IF_NULL));

        assertException(() -> tipoS.withDefaultValueIfNull(new Integer(1)), "abstrato",
                "Não deveria ser possível atribuir valor em um isntancia abstrata");

        Assert.assertEquals(null, tipoS.getValorAtributoOrDefaultValueIfNull());
        Assert.assertEquals(null, tipoB.getValorAtributoOrDefaultValueIfNull());
        Assert.assertEquals(null, tipoI.getValorAtributoOrDefaultValueIfNull());

        tipoI.withDefaultValueIfNull(new Integer(2));

        Assert.assertEquals(null, tipoS.getValorAtributoOrDefaultValueIfNull());
        Assert.assertEquals(null, tipoB.getValorAtributoOrDefaultValueIfNull());
        Assert.assertEquals(2, tipoI.getValorAtributoOrDefaultValueIfNull());

        tipoB.withDefaultValueIfNull(true);

        Assert.assertEquals(null, tipoS.getValorAtributoOrDefaultValueIfNull());
        Assert.assertEquals(true, tipoB.getValorAtributoOrDefaultValueIfNull());
        Assert.assertEquals(2, tipoI.getValorAtributoOrDefaultValueIfNull());

        try {
            tipoB.withDefaultValueIfNull("RR");
            assertEquals(true, tipoB.getValorAtributoOrDefaultValueIfNull());
            fail("Deveria ocorrer Exception ao atribuir um valor incorreto");
        } catch (RuntimeException e) {
            if (!e.getMessage().contains("não consegue converter")) {
                throw e;
            }
        }
        assertEquals(true, tipoB.getValorAtributoOrDefaultValueIfNull());
    }

    public void testCriacaoDuplicada() {
        SDictionary dicionario = SDictionary.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");

        pb.createTipo("CPF", STypeString.class);
        assertException(() -> pb.createTipo("CPF", STypeString.class), "já está criada");
    }

    public void testCriacaoAtributoLocal() {
        SDictionary dicionario = SDictionary.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");

        // AtrRef<?, ?, ?> atr = new AtrRef(null, "atTeste", MTipoString.class,
        // MIString.class, String.class);

        STypeString tipoX = pb.createTipo("XXXXX", STypeString.class);
        MAtributo atributo = pb.createTipoAtributo(tipoX, "atTeste", STypeString.class);

        assertNull(pb.getPacote().getTipoLocalOpcional("YYYYYYY").orElse(null));

        assertNull(pb.getPacote().getTipoLocalOpcional("atTeste").orElse(null));
        assertNotNull(pb.getPacote().getTipoLocal("XXXXX"));
        assertNotNull(tipoX.getTipoLocal("atTeste"));

        assertEquals(null, tipoX.getValorAtributo("teste.XXXXX.atTeste"));
        assertEquals(null, tipoX.getValorAtributo("atTeste"));

        atributo.withDefaultValueIfNull("0");
        assertEquals("0", tipoX.getValorAtributo("atTeste"));
        assertEquals("0", tipoX.getValorAtributo("teste.XXXXX.atTeste"));

        tipoX.setValorAtributo(atributo, "A");
        assertEquals("A", tipoX.getValorAtributo("atTeste"));
        assertEquals("A", tipoX.getValorAtributo("teste.XXXXX.atTeste"));

        tipoX.setValorAtributo("atTeste", "B");
        assertEquals("B", tipoX.getValorAtributo("atTeste"));
        assertEquals("B", tipoX.getValorAtributo("teste.XXXXX.atTeste"));

        tipoX.setValorAtributo("teste.XXXXX.atTeste", "C");
        assertEquals("C", tipoX.getValorAtributo("atTeste"));
        assertEquals("C", tipoX.getValorAtributo("teste.XXXXX.atTeste"));
    }

    public static final class TestPacoteA extends SPackage {

        static final AtrRef<STypeInteger, SIInteger, Integer> ATR_XX = new AtrRef<>(TestPacoteA.class, "xx", STypeInteger.class,
                SIInteger.class, Integer.class);

        protected TestPacoteA() {
            super("teste.pacoteA");
        }

        @Override
        protected void carregarDefinicoes(PacoteBuilder pb) {
            pb.createTipoAtributo(ATR_XX);
            pb.addAtributo(SType.class, ATR_XX);

            pb.createTipo(TestTipoA.class);
            pb.createTipo("TestTipoAA", TestTipoA.class);
            pb.createTipo(TestTipoComCargaInterna.class);
        }

        @MInfoTipo(nome = "TestTipoA", pacote = TestPacoteA.class)
        public static final class TestTipoA extends STypeInteger {
        }

        @MInfoTipo(nome = "TestTipoComCargaInterna", pacote = TestPacoteA.class)
        public static final class TestTipoComCargaInterna extends STypeInteger {
            @Override
            protected void onLoadType(TipoBuilder tb) {
                withObrigatorio(true);
                withValorInicial(10);
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
        protected void carregarDefinicoes(PacoteBuilder pb) {
            pb.createTipo("TestTipoB", TestTipoA.class);

            pb.createTipoAtributo(SType.class, ATR_LABEL_Y);
        }

    }

    public void testCargaSimplesPacote() {
        SDictionary dicionario = SDictionary.create();
        dicionario.carregarPacote(TestPacoteA.class);
        assertTrue(dicionario.getPacotes().stream().anyMatch(p -> p.getNome().equals("teste.pacoteA")));
        assertNotNull(dicionario.getTipoOpcional(TestTipoA.class));
        assertNotNull(dicionario.getTipoOpcional("teste.pacoteA.TestTipoA"));
        assertNotNull(dicionario.getTipoOpcional("teste.pacoteA.TestTipoAA"));
    }

    public void testCargaAutomaticaPacotePorUsoReferenciaDeClasseDeUmTipo() {
        SDictionary dicionario = SDictionary.create();
        dicionario.carregarPacote(TestPacoteB.class);
        assertTrue(dicionario.getPacotes().stream().anyMatch(p -> p.getNome().equals("teste.pacoteA")));
        assertTrue(dicionario.getPacotes().stream().anyMatch(p -> p.getNome().equals("teste.pacoteB")));
        assertNotNull(dicionario.getTipoOpcional(TestTipoA.class));
        assertNotNull(dicionario.getTipoOpcional("teste.pacoteA.TestTipoA"));
        assertNotNull(dicionario.getTipoOpcional("teste.pacoteA.TestTipoAA"));
        assertNotNull(dicionario.getTipoOpcional("teste.pacoteB.TestTipoB"));
    }

    public void testCargaAutomaticaPacotePorInstanciarUmTipo() {
        SDictionary dicionario = SDictionary.create();
        SIString is = dicionario.novaInstancia(STypeString.class);
        assertNotNull(is);

        SIInteger instancia = dicionario.novaInstancia(TestTipoA.class);
        instancia.setValor(10);
        assertEquals((Integer) 10, instancia.getValor());
        assertCargaPacoteA(dicionario, true);
    }

    public void testCargaAutomaticaPacotePorUsarUmAtributo() {
        SDictionary dicionario = SDictionary.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");

        assertCargaPacoteA(dicionario, false);

        SType<SIString> tipoEndereco = pb.createTipo("endereco", STypeString.class).with(TestPacoteA.ATR_XX, 10);

        assertCargaPacoteA(dicionario, true);

        assertEquals((Integer) 10, tipoEndereco.getValorAtributo(TestPacoteA.ATR_XX));
        assertEquals(null, dicionario.getTipoOpcional(STypeString.class).getValorAtributo(TestPacoteA.ATR_XX));
        assertEquals(null, dicionario.getTipoOpcional(STypeSimples.class).getValorAtributo(TestPacoteA.ATR_XX));
        assertEquals(null, dicionario.getTipoOpcional(SType.class).getValorAtributo(TestPacoteA.ATR_XX));
    }

    public void testCargaAutomaticaPacotePorDarAddEmUmAtributo() {
        SDictionary dicionario = SDictionary.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");

        assertCargaPacoteA(dicionario, false);
        pb.addAtributo(STypeInteger.class, TestPacoteA.ATR_XX);
        assertCargaPacoteA(dicionario, true);
    }

    public void testCargaAutomaticaPacotePorLerUmAtributo() {
        SDictionary dicionario = SDictionary.create();
        dicionario.criarNovoPacote("teste");

        assertCargaPacoteA(dicionario, false);
        assertEquals(null, dicionario.getTipo(STypeString.class).getValorAtributo(TestPacoteA.ATR_XX));
        assertCargaPacoteA(dicionario, true);
    }

    private static void assertCargaPacoteA(SDictionary dicionario, boolean carregado) {
        if (carregado) {
            assertTrue(dicionario.getPacotes().stream().anyMatch(p -> p.getNome().equals("teste.pacoteA")));
            assertNotNull(dicionario.getTipoOpcional("teste.pacoteA.TestTipoAA"));
            assertNotNull(dicionario.getTipoOpcional("teste.pacoteA.xx"));
        } else {
            assertFalse(dicionario.getPacotes().stream().anyMatch(p -> p.getNome().equals("teste.pacoteA")));
            assertNull(dicionario.getTipoOpcional("teste.pacoteA.TestTipoAA"));
            assertNull(dicionario.getTipoOpcional("teste.pacoteA.xx"));
        }
    }

    public void testSeTipoBaseadoEmClasseCarregaConfiguracaoInternaDaClasse() {
        SDictionary dicionario = SDictionary.create();
        dicionario.carregarPacote(TestPacoteA.class);
        TestTipoComCargaInterna tipo = dicionario.getTipo(TestTipoComCargaInterna.class);

        assertEquals((Boolean) true, tipo.isObrigatorio());
        assertEquals((Integer) 10, tipo.getValorAtributoValorInicial());
        assertEquals((Integer) 11, tipo.getValorAtributoOrDefaultValueIfNull());
        assertEquals((Integer) 12, tipo.getValorAtributo(TestPacoteA.ATR_XX));
    }

    public void testSeTipoBaseadoEmClasseCarregaConfiguracaoInternaDaClasseAoExtender() {
        SDictionary dicionario = SDictionary.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");
        TestTipoComCargaInterna tipo = pb.createTipo("derivado", TestTipoComCargaInterna.class);

        assertEquals((Boolean) true, tipo.isObrigatorio());
        assertEquals((Integer) 10, tipo.getValorAtributoValorInicial());
        assertEquals((Integer) 11, tipo.getValorAtributoOrDefaultValueIfNull());
        assertEquals((Integer) 12, tipo.getValorAtributo(TestPacoteA.ATR_XX));
    }

    public void testCargaTipoNoPacoteTrocado() {
        SDictionary dicionario = SDictionary.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");
        assertException(() -> pb.createTipo(TestPacoteA.TestTipoA.class), "como sendo do pacote",
                "Deveria dar uma exception pois o tipo tem a anotação para entrar em outro pacote");
    }

    public void testCargaAtributoNoPacoteTrocado() {
        SDictionary dicionario = SDictionary.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");

        assertException(() -> pb.createTipoAtributo(TestPacoteA.ATR_XX), "Tentativa de criar o atributo",
                "Deveria dar uma exception pois o atributo pertence a outro pacote");
    }
}
