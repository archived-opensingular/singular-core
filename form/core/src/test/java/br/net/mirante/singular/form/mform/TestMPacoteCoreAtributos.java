package br.net.mirante.singular.form.mform;

import br.net.mirante.singular.form.mform.TestMPacoteCore.TestPacoteB;
import br.net.mirante.singular.form.mform.TestMPacoteCoreAtributos.TestPacoteCAI.TipoComAtributoInterno1;
import br.net.mirante.singular.form.mform.TestMPacoteCoreAtributos.TestPacoteCAI.TipoComAtributoInterno2;
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

    public void testCriacaoAtributoDentroDaClasseDoTipo() {
        // Também testa se dá problema um tipo extendendo outro e ambos com
        // onCargaTipo()

        MDicionario dicionario = MDicionario.create();
        dicionario.carregarPacote(TestPacoteCAI.class);

        TipoComAtributoInterno1 tipo1 = dicionario.getTipo(TipoComAtributoInterno1.class);
        TipoComAtributoInterno2 tipo2 = dicionario.getTipo(TipoComAtributoInterno2.class);

        assertNull(tipo1.getValorAtributo(TestPacoteCAI.ATR_REF_ID1));
        assertNull(tipo1.getValorAtributo(TestPacoteCAI.ATR_REF_ID3));
        assertNull(tipo2.getValorAtributo(TestPacoteCAI.ATR_REF_ID1));
        assertNull(tipo2.getValorAtributo(TestPacoteCAI.ATR_REF_ID2));
        assertNull(tipo2.getValorAtributo(TestPacoteCAI.ATR_REF_ID3));

        tipo1.setValorAtributo(TestPacoteCAI.ATR_REF_ID1, "A1");
        tipo1.setValorAtributo(TestPacoteCAI.ATR_REF_ID3, "A3");

        assertEquals("A1", tipo2.getValorAtributo(TestPacoteCAI.ATR_REF_ID1));
        assertEquals("A3", tipo2.getValorAtributo(TestPacoteCAI.ATR_REF_ID3));

        tipo2.setValorAtributo(TestPacoteCAI.ATR_REF_ID1, "B1");
        tipo2.setValorAtributo(TestPacoteCAI.ATR_REF_ID2, "B2");
        tipo2.setValorAtributo(TestPacoteCAI.ATR_REF_ID3, "B3");

        assertEquals("A1", tipo1.getValorAtributo(TestPacoteCAI.ATR_REF_ID1));
        assertEquals("A3", tipo1.getValorAtributo(TestPacoteCAI.ATR_REF_ID3));
        assertEquals("B1", tipo2.getValorAtributo(TestPacoteCAI.ATR_REF_ID1));
        assertEquals("B2", tipo2.getValorAtributo(TestPacoteCAI.ATR_REF_ID2));
        assertEquals("B3", tipo2.getValorAtributo(TestPacoteCAI.ATR_REF_ID3));

        MIComposto instancia1 = dicionario.novaInstancia(TipoComAtributoInterno1.class);
        MIComposto instancia2 = dicionario.novaInstancia(TipoComAtributoInterno2.class);

        assertEquals("A1", instancia1.getValorAtributo(TestPacoteCAI.ATR_REF_ID1));
        assertEquals("A3", instancia1.getValorAtributo(TestPacoteCAI.ATR_REF_ID3));
        assertEquals("B1", instancia2.getValorAtributo(TestPacoteCAI.ATR_REF_ID1));
        assertEquals("B2", instancia2.getValorAtributo(TestPacoteCAI.ATR_REF_ID2));
        assertEquals("B3", instancia2.getValorAtributo(TestPacoteCAI.ATR_REF_ID3));

        instancia1.setValorAtributo(TestPacoteCAI.ATR_REF_ID1, "AI1");
        instancia1.setValorAtributo(TestPacoteCAI.ATR_REF_ID3, "AI3");
        instancia2.setValorAtributo(TestPacoteCAI.ATR_REF_ID1, "BI1");
        instancia2.setValorAtributo(TestPacoteCAI.ATR_REF_ID2, "BI2");
        instancia2.setValorAtributo(TestPacoteCAI.ATR_REF_ID3, "BI3");

        assertEquals("AI1", instancia1.getValorAtributo(TestPacoteCAI.ATR_REF_ID1));
        assertEquals("AI3", instancia1.getValorAtributo(TestPacoteCAI.ATR_REF_ID3));
        assertEquals("BI1", instancia2.getValorAtributo(TestPacoteCAI.ATR_REF_ID1));
        assertEquals("BI2", instancia2.getValorAtributo(TestPacoteCAI.ATR_REF_ID2));
        assertEquals("BI3", instancia2.getValorAtributo(TestPacoteCAI.ATR_REF_ID3));

        assertEquals("A1", tipo1.getValorAtributo(TestPacoteCAI.ATR_REF_ID1));
        assertEquals("A3", tipo1.getValorAtributo(TestPacoteCAI.ATR_REF_ID3));
        assertEquals("B1", tipo2.getValorAtributo(TestPacoteCAI.ATR_REF_ID1));
        assertEquals("B2", tipo2.getValorAtributo(TestPacoteCAI.ATR_REF_ID2));
        assertEquals("B3", tipo2.getValorAtributo(TestPacoteCAI.ATR_REF_ID3));
    }

    public static final class TestPacoteCAI extends MPacote {

        static final AtrRef<MTipoString, MIString, String> ATR_REF_ID1 = new AtrRef<>(TestPacoteCAI.class, "refId1", MTipoString.class,
                MIString.class, String.class);

        static final AtrRef<MTipoString, MIString, String> ATR_REF_ID2 = new AtrRef<>(TestPacoteCAI.class, "refId2", MTipoString.class,
                MIString.class, String.class);

        static final AtrRef<MTipoString, MIString, String> ATR_REF_ID3 = new AtrRef<>(TestPacoteCAI.class, "refId3", MTipoString.class,
                MIString.class, String.class);

        @Override
        protected void carregarDefinicoes(PacoteBuilder pb) {
            pb.createTipo(TipoComAtributoInterno1.class);
            pb.createTipo(TipoComAtributoInterno2.class);
            pb.createTipoAtributo(TipoComAtributoInterno1.class, ATR_REF_ID1);
            pb.createTipoAtributo(TipoComAtributoInterno2.class, ATR_REF_ID2);

        }

        @MInfoTipo(nome = "TipoCAI1", pacote = TestPacoteCAI.class)
        public static class TipoComAtributoInterno1 extends MTipoComposto<MIComposto> {

            @Override
            protected void onCargaTipo(TipoBuilder tb) {
                tb.createTipoAtributo(ATR_REF_ID3);
                addCampoString("nome");
            }
        }

        @MInfoTipo(nome = "TipoCAI2", pacote = TestPacoteCAI.class)
        public static class TipoComAtributoInterno2 extends TipoComAtributoInterno1 {

            @Override
            protected void onCargaTipo(TipoBuilder tb) {
                addCampoString("endereco");
            }
        }
    }

    public void testIsAttributeETestAtributoComposto() {
        MDicionario dicionario = MDicionario.create();
        PacoteBuilder pb1 = dicionario.criarNovoPacote("teste1");

        MTipoComposto<?> tipoPosicao = pb1.createTipoComposto("posicao");
        tipoPosicao.addCampoString("cor");
        tipoPosicao.addCampoInteger("linha");

        MTipo<?> tipo = pb1.createTipo("X", MTipoString.class);
        MAtributo at1 = pb1.createTipoAtributo(tipo, "a", MTipoString.class);
        MAtributo at2 = pb1.createTipoAtributo(tipo, "b", tipoPosicao);

        tipo.setValorAtributo("a", "a1");
        assertIsAtributo(tipo.getInstanciaAtributoInterno(at1.getNome()));

        tipo.setValorAtributo("b", "cor", "b1");
        tipo.setValorAtributo("b", "linha", 1);
        assertIsAtributo(tipo.getInstanciaAtributoInterno(at2.getNome()));
        assertIsAtributo(((ICompositeInstance) tipo.getInstanciaAtributoInterno(at2.getNome())).getCampo("cor"));
        assertIsAtributo(((ICompositeInstance) tipo.getInstanciaAtributoInterno(at2.getNome())).getCampo("linha"));

        MIString instancia = (MIString) tipo.novaInstancia();
        assertEquals(false, instancia.isAttribute());
        assertEquals(0, instancia.getAtributos().size());

        instancia.setValorAtributo(at1.getNome(), "a2");
        instancia.setValorAtributo(at2.getNome(), "cor", "b2");
        instancia.setValorAtributo(at2.getNome(), "linha", 2);

        assertEquals(2, instancia.getAtributos().size());
        assertIsAtributo(tipo.getInstanciaAtributoInterno(at2.getNome()));
        assertIsAtributo(((ICompositeInstance) tipo.getInstanciaAtributoInterno(at2.getNome())).getCampo("cor"));
        assertIsAtributo(((ICompositeInstance) tipo.getInstanciaAtributoInterno(at2.getNome())).getCampo("linha"));
        instancia.getAtributos().values().stream().forEach(a -> assertIsAtributo(a));
    }

    private static void assertIsAtributo(MInstancia instancia) {
        assertTrue(instancia.isAttribute());
        if (instancia instanceof ICompositeInstance) {
            ((ICompositeInstance) instancia).stream().forEach(i -> assertIsAtributo(i));
        }
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
