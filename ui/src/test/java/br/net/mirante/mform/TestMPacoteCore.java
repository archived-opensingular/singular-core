package br.net.mirante.mform;

import java.util.Collection;

import junit.framework.TestCase;
import org.junit.Assert;

import br.net.mirante.mform.TestMPacoteCore.TestPacoteA.TestTipoA;
import br.net.mirante.mform.TestMPacoteCore.TestPacoteA.TestTipoComCargaInterna;
import br.net.mirante.mform.core.MIFormula;
import br.net.mirante.mform.core.MIInteger;
import br.net.mirante.mform.core.MIString;
import br.net.mirante.mform.core.MPacoteCore;
import br.net.mirante.mform.core.MTipoBoolean;
import br.net.mirante.mform.core.MTipoFormula;
import br.net.mirante.mform.core.MTipoInteger;
import br.net.mirante.mform.core.MTipoString;

public class TestMPacoteCore extends TestCase {

    public void testBasicLoad() {
        MDicionario.create();
    }

    public void testRecuperarTipo() {
        MDicionario dicionario = MDicionario.create();

        dicionario.getTipoOpcional(MTipoString.class);
    }

    public void testHerancaValorEntreTipos() {
        MDicionario dicionario = MDicionario.create();

        MTipoSimples<?, ?> tipoS = dicionario.getTipoOpcional(MTipoSimples.class);
        MTipoBoolean tipoB = dicionario.getTipoOpcional(MTipoBoolean.class);
        MTipoInteger tipoI = dicionario.getTipoOpcional(MTipoInteger.class);

        Assert.assertFalse(tipoS.getValorAtributo(MPacoteCore.ATR_OBRIGATORIO));
        Assert.assertFalse(tipoB.getValorAtributo(MPacoteCore.ATR_OBRIGATORIO));
        Assert.assertFalse(tipoI.getValorAtributo(MPacoteCore.ATR_OBRIGATORIO));

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
        MDicionario dicionario = MDicionario.create();
        MTipoBoolean tipoB = dicionario.getTipoOpcional(MTipoBoolean.class);
        MTipoInteger tipoI = dicionario.getTipoOpcional(MTipoInteger.class);
        MTipoString tipoS = dicionario.getTipoOpcional(MTipoString.class);

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

        assertNull(tipoS.getInstanciaAtributo(MPacoteCore.ATR_EMPTY_TO_NULL));
        assertEquals(tipoS.getValorAtributo(MPacoteCore.ATR_EMPTY_TO_NULL), Boolean.TRUE);
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

    private void testarAtribuicao(MTipoSimples<?, ?> tipo, boolean valorValido, Object valor, Object valorFinalEsperado) {
        MISimples<?> instancia = tipo.novaInstancia();
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
                    "não consegue converter",
                    "Deveria dar erro de conversão");
        }
    }

    public void testSelfReference() {
        MDicionario dicionario = MDicionario.create();

        MTipoSimples<?, ?> tipoS = dicionario.getTipoOpcional(MTipoSimples.class);
        MTipoBoolean tipoB = dicionario.getTipoOpcional(MTipoBoolean.class);
        MTipoInteger tipoI = dicionario.getTipoOpcional(MTipoInteger.class);

        Assert.assertNull(tipoS.getValorAtributo(MPacoteCore.ATR_DEFAULT_IF_NULL));
        Assert.assertNull(tipoB.getValorAtributo(MPacoteCore.ATR_DEFAULT_IF_NULL));
        Assert.assertNull(tipoI.getValorAtributo(MPacoteCore.ATR_DEFAULT_IF_NULL));

        assertException(() -> tipoS.withDefaultValueIfNull(new Integer(1)), "abstrato", "Não deveria ser possível atribuir valor em um isntancia abstrata");

        Assert.assertEquals(null, tipoS.getValorAtributoDefaultValueIfNull());
        Assert.assertEquals(null, tipoB.getValorAtributoDefaultValueIfNull());
        Assert.assertEquals(null, tipoI.getValorAtributoDefaultValueIfNull());

        tipoI.withDefaultValueIfNull(new Integer(2));

        Assert.assertEquals(null, tipoS.getValorAtributoDefaultValueIfNull());
        Assert.assertEquals(null, tipoB.getValorAtributoDefaultValueIfNull());
        Assert.assertEquals(2, tipoI.getValorAtributoDefaultValueIfNull());

        tipoB.withDefaultValueIfNull(true);

        Assert.assertEquals(null, tipoS.getValorAtributoDefaultValueIfNull());
        Assert.assertEquals(true, tipoB.getValorAtributoDefaultValueIfNull());
        Assert.assertEquals(2, tipoI.getValorAtributoDefaultValueIfNull());

        try {
            tipoB.withDefaultValueIfNull("RR");
            assertEquals(true, tipoB.getValorAtributoDefaultValueIfNull());
            fail("Deveria ocorrer Exception ao atribuir um valor incorreto");
        } catch (RuntimeException e) {
            if (!e.getMessage().contains("não consegue converter")) {
                throw e;
            }
        }
        assertEquals(true, tipoB.getValorAtributoDefaultValueIfNull());
    }

    public void testAtributoValorInicial() {
        MDicionario dicionario = MDicionario.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");

        MTipo<MIString> tipo = pb.createTipo("local", MTipoString.class).withValorInicial("aqui");
        MTipoString tipoString = dicionario.getTipoOpcional(MTipoString.class);

        assertEquals("aqui", tipo.getValorAtributoValorInicial());
        assertEquals(null, tipoString.getValorAtributoValorInicial());

        MIString i1 = tipo.novaInstancia();
        assertEquals("aqui", i1.getValor());

        tipo.withValorInicial("la");
        assertEquals("aqui", i1.getValor());
        assertEquals("la", tipo.getValorAtributoValorInicial());
        assertEquals("la", tipo.novaInstancia().getValor());

        tipo.withValorInicial("none");
        assertEquals("none", tipo.getValorAtributoValorInicial());
        assertEquals("none", tipo.novaInstancia().getValor());

        tipo.withValorInicial(null);
        assertEquals(null, tipo.getValorAtributoValorInicial());
        assertEquals(null, tipo.novaInstancia().getValor());

        tipoString.withValorInicial("X");
        assertEquals("X", tipoString.novaInstancia().getValor());
        assertEquals(null, tipo.novaInstancia().getValor());

        tipo.withValorInicial("Y");
        assertEquals("X", tipoString.novaInstancia().getValor());
        assertEquals("Y", tipo.novaInstancia().getValor());
    }

    public void testCriacaoDuplicada() {
        MDicionario dicionario = MDicionario.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");

        pb.createTipo("CPF", MTipoString.class);
        assertException(() -> pb.createTipo("CPF", MTipoString.class), "já está criada");
    }

    public void testCriacaoAtributoLocal() {
        MDicionario dicionario = MDicionario.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");

        // AtrRef<?, ?, ?> atr = new AtrRef(null, "atTeste", MTipoString.class,
        // MIString.class, String.class);

        MTipoString tipoX = pb.createTipo("XXXXX", MTipoString.class);
        MAtributo atributo = pb.createAtributo(tipoX, "atTeste", MTipoString.class);

        assertNull(pb.getPacote().getTipoLocalOpcional("YYYYYYY"));

        assertNull(pb.getPacote().getTipoLocalOpcional("atTeste"));
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

    public static final class TestPacoteA extends MPacote {

        static final AtrRef<MTipoInteger, MIInteger, Integer> ATR_XX = new AtrRef<>(TestPacoteA.class, "xx", MTipoInteger.class,
                MIInteger.class, Integer.class);

        protected TestPacoteA() {
            super("teste.pacoteA");
        }

        @Override
        protected void carregarDefinicoes(PacoteBuilder pb) {
            pb.createAtributo(ATR_XX);
            pb.addAtributo(MTipo.class, ATR_XX);

            pb.createTipo(TestTipoA.class);
            pb.createTipo("TestTipoAA", TestTipoA.class);
            pb.createTipo(TestTipoComCargaInterna.class);
        }

        @MFormTipo(nome = "TestTipoA", pacote = TestPacoteA.class)
        public static final class TestTipoA extends MTipoInteger {
        }

        @MFormTipo(nome = "TestTipoComCargaInterna", pacote = TestPacoteA.class)
        public static final class TestTipoComCargaInterna extends MTipoInteger {
            @Override
            protected void onCargaTipo(TipoBuilder tb) {
                super.onCargaTipo(tb);
                withObrigatorio(true);
                withValorInicial(10);
                withDefaultValueIfNull(11);
                with(TestPacoteA.ATR_XX, 12);
            }
        }

    }

    public static final class TestPacoteB extends MPacote {
        static final AtrRef<MTipoInteger, MIInteger, Integer> ATR_LABEL_Y = new AtrRef<>(TestPacoteB.class, "yy", MTipoInteger.class,
                MIInteger.class, Integer.class);

        protected TestPacoteB() {
            super("teste.pacoteB");
        }

        @Override
        protected void carregarDefinicoes(PacoteBuilder pb) {
            pb.createTipo("TestTipoB", TestTipoA.class);

            pb.createAtributo(MTipo.class, ATR_LABEL_Y);
        }

    }

    public void testCargaSimplesPacote() {
        MDicionario dicionario = MDicionario.create();
        dicionario.carregarPacote(TestPacoteA.class);
        assertTrue(dicionario.getPacotes().stream().anyMatch(p -> p.getNome().equals("teste.pacoteA")));
        assertNotNull(dicionario.getTipoOpcional(TestTipoA.class));
        assertNotNull(dicionario.getTipoOpcional("teste.pacoteA.TestTipoA"));
        assertNotNull(dicionario.getTipoOpcional("teste.pacoteA.TestTipoAA"));
    }

    public void testCargaAutomaticaPacotePorUsoReferenciaDeClasseDeUmTipo() {
        MDicionario dicionario = MDicionario.create();
        dicionario.carregarPacote(TestPacoteB.class);
        assertTrue(dicionario.getPacotes().stream().anyMatch(p -> p.getNome().equals("teste.pacoteA")));
        assertTrue(dicionario.getPacotes().stream().anyMatch(p -> p.getNome().equals("teste.pacoteB")));
        assertNotNull(dicionario.getTipoOpcional(TestTipoA.class));
        assertNotNull(dicionario.getTipoOpcional("teste.pacoteA.TestTipoA"));
        assertNotNull(dicionario.getTipoOpcional("teste.pacoteA.TestTipoAA"));
        assertNotNull(dicionario.getTipoOpcional("teste.pacoteB.TestTipoB"));
    }

    public void testCargaAutomaticaPacotePorInstanciarUmTipo() {
        MDicionario dicionario = MDicionario.create();
        MIString is = dicionario.novaInstancia(MTipoString.class);
        assertNotNull(is);

        MIInteger instancia = dicionario.novaInstancia(TestTipoA.class);
        instancia.setValor(10);
        assertEquals((Integer) 10, instancia.getValor());
        assertTrue(dicionario.getPacotes().stream().anyMatch(p -> p.getNome().equals("teste.pacoteA")));
        assertNotNull(dicionario.getTipoOpcional("teste.pacoteA.TestTipoAA"));
    }

    public void testCargaAutomaticaPacotePorUsarUmAtributo() {
        MDicionario dicionario = MDicionario.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");

        MTipo<MIString> tipoEndereco = pb.createTipo("endereco", MTipoString.class).with(TestPacoteA.ATR_XX, 10);

        assertEquals((Integer) 10, tipoEndereco.getValorAtributo(TestPacoteA.ATR_XX));
        assertEquals(null, dicionario.getTipoOpcional(MTipoString.class).getValorAtributo(TestPacoteA.ATR_XX));
        assertEquals(null, dicionario.getTipoOpcional(MTipoSimples.class).getValorAtributo(TestPacoteA.ATR_XX));
        assertEquals(null, dicionario.getTipoOpcional(MTipo.class).getValorAtributo(TestPacoteA.ATR_XX));

        assertTrue(dicionario.getPacotes().stream().anyMatch(p -> p.getNome().equals("teste.pacoteA")));
        assertNotNull(dicionario.getTipoOpcional("teste.pacoteA.TestTipoAA"));
        assertNotNull(dicionario.getTipoOpcional("teste.pacoteA.xx"));
    }

    public void testSeTipoBaseadoEmClasseCarregaConfiguracaoInternaDaClasse() {
        MDicionario dicionario = MDicionario.create();
        dicionario.carregarPacote(TestPacoteA.class);
        TestTipoComCargaInterna tipo = dicionario.getTipo(TestTipoComCargaInterna.class);

        assertEquals((Boolean) true, tipo.isObrigatorio());
        assertEquals((Integer) 10, tipo.getValorAtributoValorInicial());
        assertEquals((Integer) 11, tipo.getValorAtributoDefaultValueIfNull());
        assertEquals((Integer) 12, tipo.getValorAtributo(TestPacoteA.ATR_XX));
    }

    public void testRefenciaCircularDePacotes() {
        Assert.fail("implementar");
    }

    public void testCriarDoisAtributosComMesmoNome() {
        MDicionario dicionario = MDicionario.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");

        MTipoString tipo = pb.createTipo("X", MTipoString.class);
        pb.createAtributo(tipo, "a", MTipoInteger.class);
        assertException(() -> pb.createAtributo(tipo, "a", MTipoString.class), "já está criada",
                "Deveria ter ocorrido uma exception por ter dois atributo com mesmo nome criado pelo mesmo pacote");
    }

    public void testCriarDoisAtributosDePacotesDiferentesComMesmoNome() {
        MDicionario dicionario = MDicionario.create();
        PacoteBuilder pb1 = dicionario.criarNovoPacote("teste1");

        MTipoSimples<?, ?> tipo = pb1.createTipo("X", MTipoSimples.class);
        MAtributo at1 = pb1.createAtributo(tipo, "a", MTipoInteger.class);

        PacoteBuilder pb2 = dicionario.criarNovoPacote("teste2");
        MAtributo at2 = pb2.createAtributo(tipo, "a", MTipoInteger.class);

        assertException(() -> pb2.createAtributo(dicionario.getTipo(MTipoSimples.class), "a", MTipoInteger.class), "já está criada");

        assertEquals("teste1.X.a", at1.getNome());
        assertEquals("teste2.a", at2.getNome());

        tipo.setValorAtributo(at1.getNome(), 1);
        tipo.setValorAtributo(at2.getNome(), 2);
        assertEquals((Integer) 1, tipo.getValorAtributo(at1.getNome()));
        assertEquals((Integer) 2, tipo.getValorAtributo(at2.getNome()));

        tipo.setValorAtributo(at1, 10);
        tipo.setValorAtributo(at2, 20);
        assertEquals((Integer) 10, tipo.getValorAtributo(at1.getNome()));
        assertEquals((Integer) 20, tipo.getValorAtributo(at2.getNome()));
    }

    public void testCargaTipoNoPacoteTrocado() {
        MDicionario dicionario = MDicionario.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");
        assertException(() -> pb.createTipo(TestPacoteA.TestTipoA.class), "como sendo do pacote",
                "Deveria dar uma exception pois o tipo tem a anotação para entrar em outro pacote");
    }

    public void testCargaAtributoNoPacoteTrocado() {
        MDicionario dicionario = MDicionario.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");

        assertException(() -> pb.createAtributo(TestPacoteA.ATR_XX), "Tentativa de criar o atributo",
                "Deveria dar uma exception pois o atributo pertence a outro pacote");
    }

    public void testCriacaoNovosAtributosNosPacotesCerto() {
        MDicionario dicionario = MDicionario.create();
        dicionario.carregarPacote(TestPacoteB.class);

        assertEquals("teste.pacoteB.yy", TestPacoteB.ATR_LABEL_Y.getNomeCompleto());

        MTipo<?> tipoAtributo = dicionario.getTipo(TestPacoteB.ATR_LABEL_Y.getNomeCompleto());
        assertNotNull(tipoAtributo);
        assertEquals("teste.pacoteB.yy", tipoAtributo.getNome());
        assertEquals("teste.pacoteB", tipoAtributo.getPacote().getNome());
        assertEquals("teste.pacoteB", tipoAtributo.getEscopoPai().getNome());
    }

    public void testTipoCompostoCriacao() {
        MDicionario dicionario = MDicionario.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");

        MTipoComposto<?> tipoEndereco = pb.createTipoComposto("endereco");
        tipoEndereco.addCampo("rua", MTipoString.class);
        tipoEndereco.addCampoString("bairro", true);
        tipoEndereco.addCampoInteger("cep", true);

        MTipoComposto tipoClassificacao = tipoEndereco.addCampoComposto("classificacao");
        tipoClassificacao.addCampoInteger("prioridade");
        tipoClassificacao.addCampoString("descricao");

        assertTipo(tipoEndereco.getTipoLocal("rua"), "rua");
        assertTipo(tipoEndereco.getCampo("rua"), "rua");
        assertEquals((Object) false, tipoEndereco.getTipoLocal("rua").isObrigatorio());
        assertEquals((Object) true, tipoEndereco.getTipoLocal("cep").isObrigatorio());

        assertTipo(tipoEndereco.getTipoLocal("classificacao"), "classificacao");
        assertTipo(tipoEndereco.getTipoLocal("classificacao.prioridade"), "prioridade");
        assertEquals(MTipoInteger.class, tipoEndereco.getTipoLocal("classificacao.prioridade").getClass());

        assertNull(tipoEndereco.getTipoLocalOpcional("classificacao.prioridade.x.y"));
        assertException(() -> tipoEndereco.getTipoLocal("classificacao.prioridade.x.y"), "Não existe o tipo");

        MIComposto endereco = tipoEndereco.novaInstancia();

        assertNull(endereco.getValor("rua"));
        assertNull(endereco.getValor("bairro"));
        assertNull(endereco.getValor("cep"));
        assertNull(endereco.getValor("classificacao"));
        assertNull(endereco.getValor("classificacao.prioridade"));
        assertNull(endereco.getValor("classificacao.descricao"));

        assertException(() -> endereco.setValor(100), "Método não suportado");

        testAtribuicao(endereco, "rua", "Pontes");
        testAtribuicao(endereco, "bairro", "Norte");
        testAtribuicao(endereco, "classificacao.prioridade", 1);
        assertNotNull(endereco.getValor("classificacao"));
        assertTrue(endereco.getValor("classificacao") instanceof Collection);
        assertTrue(((Collection<?>) endereco.getValor("classificacao")).size() >= 1);
        testAtribuicao(endereco, "classificacao.prioridade", 1);

        testAtribuicao(endereco, "classificacao", null);
        assertNull(endereco.getValor("classificacao.prioridade"));
        testAtribuicao(endereco, "classificacao.prioridade", null);

        assertException(() -> endereco.setValor("classificacao", "X"), "Método não suportado");
    }

    private static void assertTipo(MTipo<?> tipo, String nomeEsperado) {
        assertNotNull(tipo);
        assertEquals(nomeEsperado, tipo.getNomeSimples());
    }

    private static void testAtribuicao(MInstancia registro, String path, Object valor) {
        registro.setValor(path, valor);
        assertEquals(valor, registro.getValor(path));
    }

    public void testTipoCompostoReusoTipo() {
        fail("Implementar");
    }

    public void testTipoCompostoTestarValorInicialEValorDefaultIfNull() {
        // Testar ser o campos de valor são criados com o valor inicial correto
        fail("Implementar");
    }

    private static void assertException(Runnable acao, String trechoMsgEsperada) {
        assertException(acao, RuntimeException.class, trechoMsgEsperada, null);
    }

    private static void assertException(Runnable acao, String trechoMsgEsperada, String msgFailException) {
        assertException(acao, RuntimeException.class, trechoMsgEsperada, msgFailException);
    }

    private static void assertException(Runnable acao, Class<? extends Exception> exceptionEsperada) {
        assertException(acao, exceptionEsperada, null, null);
    }

    private static void assertException(Runnable acao, Class<? extends Exception> exceptionEsperada, String trechoMsgEsperada,
            String msgFailException) {
        try {
            acao.run();
            String msg = "Não ocorreu nenhuma Exception. Era esperado " + exceptionEsperada.getSimpleName() + "'";
            if (trechoMsgEsperada != null) {
                msg += " com mensagem contendo '" + trechoMsgEsperada + "'";
            }
            if (msgFailException != null) {
                msg += ", pois " + msgFailException;
            }
            fail(msg);
        } catch (Exception e) {
            if (exceptionEsperada.isInstance(e)) {
                if (trechoMsgEsperada == null || e.getMessage().contains(trechoMsgEsperada)) {
                    return;
                }
            }
            throw e;
        }

    }

    private static void assertLista(MILista lista, Object[] valoresEsperados) {
        assertEquals(valoresEsperados.length, lista.size());
        for (int i = 0; i < valoresEsperados.length; i++) {
            assertEquals(valoresEsperados[i], lista.getValorAt(i));
        }
    }

    public void testTipoListaCriacaoOfTipoSimples() {
        MDicionario dicionario = MDicionario.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");

        MTipoLista<MTipoString> nomes = pb.createTipoListaOf("nomes", MTipoString.class);

        MILista<MIString> lista = nomes.novaInstancia();
        lista.addValor("Paulo");
        assertLista(lista, new String[] {"Paulo"});
        lista.addValor("Joao");
        assertLista(lista, new String[] {"Paulo", "Joao"});
        lista.addValor("Maria");
        assertLista(lista, new String[] {"Paulo", "Joao", "Maria"});

        lista.remove(1);
        assertLista(lista, new String[] {"Paulo", "Maria"});
        assertException(() -> lista.remove(10), IndexOutOfBoundsException.class);

        assertException(() -> lista.addValor(null), "Não é aceito null");
        assertException(() -> lista.addValor(""), "Não é permitido");
        assertException(() -> lista.addNovo(), "não é um tipo composto");

        MILista<MIInteger> listaInt = dicionario.getTipo(MTipoInteger.class).novaLista();
        listaInt.addValor(10);
        assertLista(listaInt, new Integer[] {10});
        listaInt.addValor("20");
        assertLista(listaInt, new Integer[] {10, 20});
        assertException(() -> listaInt.addValor("XX"), "não consegue converter");

        assertEquals(lista.getValor("[0]"), "Paulo");
        assertEquals(listaInt.getValor("[1]"), 20);
        assertException(() -> listaInt.getValor("[20]"), IndexOutOfBoundsException.class);

    }

    public void testTipoListaCriacaoOfTipoComposto() {
        MDicionario dicionario = MDicionario.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");

        MTipoLista<MTipoComposto<?>> tipoPedidos = pb.createTipoListaOfNovoTipoComposto("pedidos", "pedido");
        tipoPedidos.getTipoElementos().addCampoString("descricao");
        tipoPedidos.getTipoElementos().addCampoInteger("qtd");

        MILista<MIComposto> pedidos = tipoPedidos.novaInstancia();
        MIComposto pedido;
        assertException(() -> pedidos.addValor("Paulo"), "Método não suportado");
        pedido = pedidos.addNovo();
        assertNotNull(pedido);
        assertEquals(1, pedidos.size());
        assertTrue((pedidos.get(0)) instanceof MIComposto);
        assertTrue((pedidos.getValorAt(0)) instanceof Collection);

        assertException(() -> pedidos.get(10), IndexOutOfBoundsException.class);
        assertException(() -> pedidos.getValorAt(10), IndexOutOfBoundsException.class);

        pedido.setValor("descricao", "bola");
        pedido.setValor("qtd", 20);

        pedido = pedidos.addNovo();
        pedido.setValor("descricao", "rede");
        pedido.setValor("qtd", -10);

        assertException(() -> pedidos.getValorAt(10), IndexOutOfBoundsException.class);

        assertEquals(pedidos.getValor("[0].descricao"), "bola");
        assertEquals(pedidos.getValor("[0].qtd"), 20);

        testAtribuicao(pedidos, "[1].descricao", "rede2");
        testAtribuicao(pedidos, "[1].qtd", 20);
        assertException(() -> pedidos.setValor("[1].marca", 10), "Não é um campo definido");
    }

    public void testTipoCriacaoOfTipoCompostoTipado() {
        MDicionario dicionario = MDicionario.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");

        MTipoLista<MTipoFormula> tipoFormulas = pb.createTipoListaOf("formulas", MTipoFormula.class);

        MILista<MIFormula> formulas = tipoFormulas.novaInstancia();

        MIFormula formula = formulas.addNovo();
        formula.setSciptJS("XXX");
        assertEquals(MTipoFormula.TipoScript.JS, formula.getTipoScriptEnum());

        assertEquals("XXX", formulas.getValorString("[0].script"));
        assertEquals("JS", formulas.getValorString("[0].tipoScript"));
    }
}
