package br.net.mirante.singular.form.mform;

import br.net.mirante.singular.form.mform.TestMPacoteCore.TestPacoteB;
import br.net.mirante.singular.form.mform.core.MIInteger;
import br.net.mirante.singular.form.mform.core.MIString;
import br.net.mirante.singular.form.mform.core.MTipoBoolean;
import br.net.mirante.singular.form.mform.core.MTipoInteger;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.util.comuns.MTipoCEP;
import br.net.mirante.singular.form.mform.util.comuns.MTipoCNPJ;
import br.net.mirante.singular.form.mform.util.comuns.MTipoCPF;
import br.net.mirante.singular.form.mform.util.comuns.MTipoEMail;

public class TestMPacoteCoreAtributos extends TestCaseForm {

    public static final class TestPacoteAA extends MPacote {

        static final AtrRef<MTipoInteger, MIInteger, Integer> ATR_XX = new AtrRef<>(TestPacoteAA.class, "xx", MTipoInteger.class,
                MIInteger.class, Integer.class);

        static final AtrRef<MTipoInteger, MIInteger, Integer> ATR_YY = new AtrRef<>(TestPacoteAA.class, "yy", MTipoInteger.class,
                MIInteger.class, Integer.class);

        protected TestPacoteAA() {
            super("teste.pacoteAA");
        }

        @Override
        protected void carregarDefinicoes(PacoteBuilder pb) {
            pb.createTipoAtributo(ATR_XX);
            pb.addAtributo(MTipo.class, ATR_XX);

            pb.createTipoAtributo(ATR_YY).withDefaultValueIfNull(15);
            pb.addAtributo(MTipoString.class, ATR_YY, 17);
            pb.addAtributo(MTipoCPF.class, ATR_YY, 19);
            pb.addAtributo(MTipoCEP.class, ATR_YY, 21);
            pb.addAtributo(MTipoCNPJ.class, ATR_YY, 23);

            pb.addAtributo(MTipoInteger.class, ATR_YY);
        }
    }

    public void testValorDefaultEInicialDeAtributos() {
        MDicionario dicionario = MDicionario.create();
        dicionario.carregarPacote(TestPacoteAA.class);

//        dicionario.debug();
        // Teste no tipo
        assertLeituraAtributo(dicionario.getTipo(MTipoInteger.class), 15, 15);
        assertLeituraAtributo(dicionario.getTipo(MTipoString.class), 17, 17);
        // Os tipos a seguir extends MTipoString
        assertLeituraAtributo(dicionario.getTipo(MTipoEMail.class), 17, 17);
        assertLeituraAtributo(dicionario.getTipo(MTipoCPF.class), 19, 19);
        assertLeituraAtributo(dicionario.getTipo(MTipoCEP.class), 21, 21);
        assertLeituraAtributo(dicionario.getTipo(MTipoCNPJ.class), 23, 23);
    }

    private static void assertLeituraAtributo(MTipo<?> alvo, Object esperadoGetValor, Object esperadoGetValorWithDefault) {

        assertEquals(esperadoGetValor, alvo.getValorAtributo(TestPacoteAA.ATR_YY));
        assertEquals(esperadoGetValor, alvo.getValorAtributo(TestPacoteAA.ATR_YY, Integer.class));

        MInstancia instancia = alvo.novaInstancia();
        assertEquals(esperadoGetValor, instancia.getValorAtributo(TestPacoteAA.ATR_YY));
        assertEquals(esperadoGetValor, instancia.getValorAtributo(TestPacoteAA.ATR_YY, Integer.class));

        Integer novoValor = 1024;
        instancia.setValorAtributo(TestPacoteAA.ATR_YY, novoValor);
        assertEquals(novoValor, instancia.getValorAtributo(TestPacoteAA.ATR_YY));
        assertEquals(novoValor, instancia.getValorAtributo(TestPacoteAA.ATR_YY, Integer.class));
    }

    public void testAdicionarAtributoEmOutroPacote() {
        MDicionario dicionario = MDicionario.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");
        pb.addAtributo(MTipoString.class, TestPacoteAA.ATR_XX, 17);

        MTipoString tipo = dicionario.getTipo(MTipoString.class);
        assertEquals(tipo.getValorAtributo(TestPacoteAA.ATR_XX), (Object) 17);
        MIString instancia = tipo.novaInstancia();
        assertEquals(instancia.getValorAtributo(TestPacoteAA.ATR_XX), (Object) 17);
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

    public void testCriarDoisAtributosComMesmoNome() {
        MDicionario dicionario = MDicionario.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");

        MTipoString tipo = pb.createTipo("X", MTipoString.class);
        pb.createTipoAtributo(tipo, "a", MTipoInteger.class);
        assertException(() -> pb.createTipoAtributo(tipo, "a", MTipoString.class), "já está criada",
                "Deveria ter ocorrido uma exception por ter dois atributo com mesmo nome criado pelo mesmo pacote");
    }

    public void testCriarDoisAtributosDePacotesDiferentesComMesmoNome() {
        MDicionario dicionario = MDicionario.create();
        PacoteBuilder pb1 = dicionario.criarNovoPacote("teste1");

        MTipoSimples<?, ?> tipo = pb1.createTipo("X", MTipoSimples.class);
        MAtributo at1 = pb1.createTipoAtributo(tipo, "a", MTipoInteger.class);

        PacoteBuilder pb2 = dicionario.criarNovoPacote("teste2");
        MAtributo at2 = pb2.createTipoAtributo(tipo, "a", MTipoInteger.class);

        assertException(() -> pb2.createTipoAtributo(dicionario.getTipo(MTipoSimples.class), "a", MTipoInteger.class), "já está criada");

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

    public void testTipoCompostoTestarValorInicialEValorDefaultIfNull() {
        testInicialEDefault(MTipoInteger.class, 10, 11);
        testInicialEDefault(MTipoString.class, "A", "B");
        testInicialEDefault(MTipoBoolean.class, true, false);
    }

    private static <T extends MTipo<?>> void testInicialEDefault(Class<T> tipo, Object valorInicial, Object valorIfNull) {
        assertTrue(!valorInicial.equals(valorIfNull));
        MDicionario dicionario = MDicionario.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");
        MTipo<?> tx = pb.createTipo("x", tipo).withValorInicial(valorInicial);
        MTipo<?> ty = pb.createTipo("y", tipo).withDefaultValueIfNull(valorIfNull);
        MTipo<?> tz = pb.createTipo("z", tipo).withValorInicial(valorInicial).withDefaultValueIfNull(valorIfNull);

        MInstancia instX = tx.novaInstancia();
        assertEquals(valorInicial, instX.getValor());
        assertEquals(valorInicial, instX.getValorWithDefault());

        MInstancia instY = ty.novaInstancia();
        assertNull(instY.getValor());
        assertEquals(valorIfNull, instY.getValorWithDefault());
        instY.setValor(valorInicial);
        assertEquals(valorInicial, instY.getValorWithDefault());
        instY.setValor(null);
        assertEquals(valorIfNull, instY.getValorWithDefault());

        MInstancia instZ = tz.novaInstancia();
        assertEquals(valorInicial, instZ.getValor());
        assertEquals(valorInicial, instZ.getValorWithDefault());
        instZ.setValor(null);
        assertEquals(valorIfNull, instZ.getValorWithDefault());
    }
}
