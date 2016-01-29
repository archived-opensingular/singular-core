package br.net.mirante.singular.form.mform;

import static org.fest.assertions.api.Assertions.assertThat;

import br.net.mirante.singular.form.mform.TestMPacoteCore.TestPacoteB;
import br.net.mirante.singular.form.mform.TestMPacoteCoreAtributos.TestPacoteCAI.TipoComAtributoInterno1;
import br.net.mirante.singular.form.mform.TestMPacoteCoreAtributos.TestPacoteCAI.TipoComAtributoInterno2;
import br.net.mirante.singular.form.mform.core.SIInteger;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeBoolean;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.util.comuns.STypeCEP;
import br.net.mirante.singular.form.mform.util.comuns.STypeCNPJ;
import br.net.mirante.singular.form.mform.util.comuns.STypeCPF;
import br.net.mirante.singular.form.mform.util.comuns.STypeEMail;

public class TestMPacoteCoreAtributos extends TestCaseForm {

    public static final class TestPacoteAA extends SPackage {

        static final AtrRef<STypeInteger, SIInteger, Integer> ATR_XX = new AtrRef<>(TestPacoteAA.class, "xx", STypeInteger.class,
                SIInteger.class, Integer.class);

        static final AtrRef<STypeInteger, SIInteger, Integer> ATR_YY = new AtrRef<>(TestPacoteAA.class, "yy", STypeInteger.class,
                SIInteger.class, Integer.class);

        protected TestPacoteAA() {
            super("teste.pacoteAA");
        }

        @Override
        protected void carregarDefinicoes(PacoteBuilder pb) {
            pb.createTipoAtributo(ATR_XX);
            pb.addAtributo(SType.class, ATR_XX);

            pb.createTipoAtributo(ATR_YY).withDefaultValueIfNull(15);
            pb.addAtributo(STypeString.class, ATR_YY, 17);
            pb.addAtributo(STypeCPF.class, ATR_YY, 19);
            pb.addAtributo(STypeCEP.class, ATR_YY, 21);
            pb.addAtributo(STypeCNPJ.class, ATR_YY, 23);

            pb.addAtributo(STypeInteger.class, ATR_YY);
        }
    }

    public void testValorDefaultEInicialDeAtributos() {
        SDictionary dicionario = SDictionary.create();
        dicionario.carregarPacote(TestPacoteAA.class);

        // Teste no tipo
        assertLeituraAtributo(dicionario.getTipo(STypeInteger.class), 15, 15);
        assertLeituraAtributo(dicionario.getTipo(STypeString.class), 17, 17);
        // Os tipos a seguir extends MTipoString
        assertLeituraAtributo(dicionario.getTipo(STypeEMail.class), 17, 17);
        assertLeituraAtributo(dicionario.getTipo(STypeCPF.class), 19, 19);
        assertLeituraAtributo(dicionario.getTipo(STypeCEP.class), 21, 21);
        assertLeituraAtributo(dicionario.getTipo(STypeCNPJ.class), 23, 23);
    }

    private static void assertLeituraAtributo(SType<?> alvo, Object esperadoGetValor, Object esperadoGetValorWithDefault) {

        assertEquals(esperadoGetValor, alvo.getValorAtributo(TestPacoteAA.ATR_YY));
        assertEquals(esperadoGetValor, alvo.getValorAtributo(TestPacoteAA.ATR_YY, Integer.class));

        SInstance instancia = alvo.novaInstancia();
        assertEquals(esperadoGetValor, instancia.getValorAtributo(TestPacoteAA.ATR_YY));
        assertEquals(esperadoGetValor, instancia.getValorAtributo(TestPacoteAA.ATR_YY, Integer.class));

        Integer novoValor = 1024;
        instancia.setValorAtributo(TestPacoteAA.ATR_YY, novoValor);
        assertEquals(novoValor, instancia.getValorAtributo(TestPacoteAA.ATR_YY));
        assertEquals(novoValor, instancia.getValorAtributo(TestPacoteAA.ATR_YY, Integer.class));
    }

    public void testAdicionarAtributoEmOutroPacote() {
        SDictionary dicionario = SDictionary.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");
        pb.addAtributo(STypeString.class, TestPacoteAA.ATR_XX, 17);

        STypeString tipo = dicionario.getTipo(STypeString.class);
        assertEquals(tipo.getValorAtributo(TestPacoteAA.ATR_XX), (Object) 17);
        SIString instancia = tipo.novaInstancia();
        assertEquals(instancia.getValorAtributo(TestPacoteAA.ATR_XX), (Object) 17);
    }

    public void testAtributoValorInicial() {
        SDictionary dicionario = SDictionary.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");

        SType<SIString> tipo = pb.createTipo("local", STypeString.class).withValorInicial("aqui");
        STypeString tipoString = dicionario.getTipoOpcional(STypeString.class);

        assertEquals("aqui", tipo.getValorAtributoValorInicial());
        assertEquals(null, tipoString.getValorAtributoValorInicial());

        SIString i1 = tipo.novaInstancia();
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
        SDictionary dicionario = SDictionary.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");

        STypeString tipo = pb.createTipo("X", STypeString.class);
        pb.createTipoAtributo(tipo, "a", STypeInteger.class);
        assertException(() -> pb.createTipoAtributo(tipo, "a", STypeString.class), "já está criada",
                "Deveria ter ocorrido uma exception por ter dois atributo com mesmo nome criado pelo mesmo pacote");
    }

    public void testCriarDoisAtributosDePacotesDiferentesComMesmoNome() {
        SDictionary dicionario = SDictionary.create();
        PacoteBuilder pb1 = dicionario.criarNovoPacote("teste1");

        STypeSimples<?, ?> tipo = pb1.createTipo("X", STypeSimples.class);
        MAtributo at1 = pb1.createTipoAtributo(tipo, "a", STypeInteger.class);

        PacoteBuilder pb2 = dicionario.criarNovoPacote("teste2");
        MAtributo at2 = pb2.createTipoAtributo(tipo, "a", STypeInteger.class);

        assertException(() -> pb2.createTipoAtributo(dicionario.getTipo(STypeSimples.class), "a", STypeInteger.class), "já está criada");

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
        SDictionary dicionario = SDictionary.create();
        dicionario.carregarPacote(TestPacoteB.class);

        assertEquals("teste.pacoteB.yy", TestPacoteB.ATR_LABEL_Y.getNomeCompleto());

        SType<?> tipoAtributo = dicionario.getTipo(TestPacoteB.ATR_LABEL_Y.getNomeCompleto());
        assertNotNull(tipoAtributo);
        assertEquals("teste.pacoteB.yy", tipoAtributo.getNome());
        assertEquals("teste.pacoteB", tipoAtributo.getPacote().getNome());
        assertEquals("teste.pacoteB", tipoAtributo.getEscopoPai().getNome());
    }

    public void testCriacaoAtributoDentroDaClasseDoTipo() {
        // Também testa se dá problema um tipo extendendo outro e ambos com
        // onLoadType()

        SDictionary dicionario = SDictionary.create();
        TestPacoteCAI pkg = dicionario.carregarPacote(TestPacoteCAI.class);

        TipoComAtributoInterno1 tipo1 = dicionario.getTipo(TipoComAtributoInterno1.class);
        TipoComAtributoInterno2 tipo2 = dicionario.getTipo(TipoComAtributoInterno2.class);
        TipoComAtributoInterno1 fieldOfTipo1 = pkg.fieldOfTipoComAtributoInterno1;

        assertNull(tipo1.getValorAtributo(TestPacoteCAI.ATR_REF_ID1));
        assertNull(tipo1.getValorAtributo(TestPacoteCAI.ATR_REF_ID3));
        assertNull(tipo2.getValorAtributo(TestPacoteCAI.ATR_REF_ID1));
        assertNull(tipo2.getValorAtributo(TestPacoteCAI.ATR_REF_ID2));
        assertNull(tipo2.getValorAtributo(TestPacoteCAI.ATR_REF_ID3));
        assertNull(fieldOfTipo1.getValorAtributo(TestPacoteCAI.ATR_REF_ID3));

        tipo1.setValorAtributo(TestPacoteCAI.ATR_REF_ID1, "A1");
        tipo1.setValorAtributo(TestPacoteCAI.ATR_REF_ID3, "A3");

        assertEquals("A1", tipo2.getValorAtributo(TestPacoteCAI.ATR_REF_ID1));
        assertEquals("A3", tipo2.getValorAtributo(TestPacoteCAI.ATR_REF_ID3));
        
        fieldOfTipo1.setValorAtributo(TestPacoteCAI.ATR_REF_ID1, "A1");
        fieldOfTipo1.setValorAtributo(TestPacoteCAI.ATR_REF_ID3, "A3");

        assertEquals("A1", fieldOfTipo1.getValorAtributo(TestPacoteCAI.ATR_REF_ID1));
        assertEquals("A3", fieldOfTipo1.getValorAtributo(TestPacoteCAI.ATR_REF_ID3));

        tipo2.setValorAtributo(TestPacoteCAI.ATR_REF_ID1, "B1");
        tipo2.setValorAtributo(TestPacoteCAI.ATR_REF_ID2, "B2");
        tipo2.setValorAtributo(TestPacoteCAI.ATR_REF_ID3, "B3");

        assertEquals("A1", tipo1.getValorAtributo(TestPacoteCAI.ATR_REF_ID1));
        assertEquals("A3", tipo1.getValorAtributo(TestPacoteCAI.ATR_REF_ID3));
        assertEquals("B1", tipo2.getValorAtributo(TestPacoteCAI.ATR_REF_ID1));
        assertEquals("B2", tipo2.getValorAtributo(TestPacoteCAI.ATR_REF_ID2));
        assertEquals("B3", tipo2.getValorAtributo(TestPacoteCAI.ATR_REF_ID3));

        SIComposite instancia1 = dicionario.novaInstancia(TipoComAtributoInterno1.class);
        SIComposite instancia2 = dicionario.novaInstancia(TipoComAtributoInterno2.class);

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
        
        assertEquals("A1", fieldOfTipo1.getValorAtributo(TestPacoteCAI.ATR_REF_ID1));
        assertEquals("A3", fieldOfTipo1.getValorAtributo(TestPacoteCAI.ATR_REF_ID3));
        
        SInstance instanceOfFieldOfTipo1 = fieldOfTipo1.novaInstancia();
        
        assertEquals("A1", instanceOfFieldOfTipo1.getValorAtributo(TestPacoteCAI.ATR_REF_ID1));
        assertEquals("A3", instanceOfFieldOfTipo1.getValorAtributo(TestPacoteCAI.ATR_REF_ID3));
        
    }
    
    public void testAtribuicaoDeValoresDeAtributosPorString() {
        SDictionary dicionario = SDictionary.create();
        TestPacoteCAI pkg = dicionario.carregarPacote(TestPacoteCAI.class);

        TipoComAtributoInterno1 fieldOfTipo1 = pkg.fieldOfTipoComAtributoInterno1;
        
        SInstance instanceOfFieldOfTipo1 = fieldOfTipo1.novaInstancia();
        
        instanceOfFieldOfTipo1.setValorAtributo(TestPacoteCAI.ATR_REF_ID3,"what");
        
//        System.out.println(pkg.fieldOfTipoComAtributoInterno1.getNome());
//        System.out.println(pkg.fieldOfTipoComAtributoInterno1.getSuperTipo().getNome());
        
        
        String basePath = pkg.fieldOfTipoComAtributoInterno1.getSuperTipo().getNome()+".";
        instanceOfFieldOfTipo1.setValorAtributo(basePath+TestPacoteCAI.ATR_KEY_ID4,"what");
        assertThat(instanceOfFieldOfTipo1.getValorAtributo(basePath+TestPacoteCAI.ATR_KEY_ID4))
            .isEqualTo("what");
        
    }

    public static final class TestPacoteCAI extends SPackage {

        static final AtrRef<STypeString, SIString, String> ATR_REF_ID1 = new AtrRef<>(TestPacoteCAI.class, "refId1", STypeString.class,
                SIString.class, String.class);

        static final AtrRef<STypeString, SIString, String> ATR_REF_ID2 = new AtrRef<>(TestPacoteCAI.class, "refId2", STypeString.class,
                SIString.class, String.class);

        static final AtrRef<STypeString, SIString, String> ATR_REF_ID3 = new AtrRef<>(TestPacoteCAI.class, "refId3", STypeString.class,
                SIString.class, String.class);
        static final String ATR_KEY_ID4 = "refId4";

        TipoComAtributoInterno1 fieldOfTipoComAtributoInterno1;

        @Override
        protected void carregarDefinicoes(PacoteBuilder pb) {
            pb.createTipo(TipoComAtributoInterno1.class);
            pb.createTipo(TipoComAtributoInterno2.class);
            pb.createTipoAtributo(TipoComAtributoInterno1.class, ATR_REF_ID1);
            pb.createTipoAtributo(TipoComAtributoInterno2.class, ATR_REF_ID2);
            STypeComposto<? extends SIComposite> grouper = pb.createTipoComposto("Grouper");
            fieldOfTipoComAtributoInterno1 = grouper.addCampo("TipoComAtributoInterno1", TipoComAtributoInterno1.class);

            pb.createTipoAtributo(TipoComAtributoInterno1.class, ATR_REF_ID3);
            pb.createTipoAtributo(TipoComAtributoInterno1.class, ATR_KEY_ID4, STypeString.class);
        }

        @MInfoTipo(nome = "TipoCAI1", pacote = TestPacoteCAI.class)
        public static class TipoComAtributoInterno1 extends STypeComposto<SIComposite> {

            @Override
            protected void onLoadType(TipoBuilder tb) {
                addCampoString("nome");
            }
        }

        @MInfoTipo(nome = "TipoCAI2", pacote = TestPacoteCAI.class)
        public static class TipoComAtributoInterno2 extends TipoComAtributoInterno1 {

            @Override
            protected void onLoadType(TipoBuilder tb) {
                addCampoString("endereco");
            }
        }
    }

    public void testIsAttributeETestAtributoComposto() {
        SDictionary dicionario = SDictionary.create();
        PacoteBuilder pb1 = dicionario.criarNovoPacote("teste1");

        STypeComposto<?> tipoPosicao = pb1.createTipoComposto("posicao");
        tipoPosicao.addCampoString("cor");
        tipoPosicao.addCampoInteger("linha");

        SType<?> tipo = pb1.createTipo("X", STypeString.class);
        MAtributo at1 = pb1.createTipoAtributo(tipo, "a", STypeString.class);
        MAtributo at2 = pb1.createTipoAtributo(tipo, "b", tipoPosicao);

        tipo.setValorAtributo("a", "a1");
        assertIsAtributo(tipo.getInstanciaAtributoInterno(at1.getNome()), null);

        tipo.setValorAtributo("b", "cor", "b1");
        tipo.setValorAtributo("b", "linha", 1);
        assertIsAtributo(tipo.getInstanciaAtributoInterno(at2.getNome()), null);
        assertIsAtributo(((ICompositeInstance) tipo.getInstanciaAtributoInterno(at2.getNome())).getCampo("cor"), null);
        assertIsAtributo(((ICompositeInstance) tipo.getInstanciaAtributoInterno(at2.getNome())).getCampo("linha"), null);

        SIString instancia = (SIString) tipo.novaInstancia();
        assertEquals(false, instancia.isAttribute());
        assertEquals(0, instancia.getAtributos().size());

        instancia.setValorAtributo(at1.getNome(), "a2");
        instancia.setValorAtributo(at2.getNome(), "cor", "b2");
        instancia.setValorAtributo(at2.getNome(), "linha", 2);

        assertEquals(2, instancia.getAtributos().size());
        assertIsAtributo(instancia.getAtributos().get(at1.getNome()), instancia);
        assertIsAtributo(instancia.getAtributos().get(at2.getNome()), instancia);
        assertIsAtributo(((ICompositeInstance) instancia.getAtributos().get(at2.getNome())).getCampo("cor"), instancia);
        assertIsAtributo(((ICompositeInstance) instancia.getAtributos().get(at2.getNome())).getCampo("linha"), instancia);
        instancia.getAtributos().values().stream().forEach(a -> assertIsAtributo(a, instancia));
    }

    private static void assertIsAtributo(SInstance instancia, SInstance expectedOwner) {
        assertTrue(instancia.isAttribute());
        assertTrue(expectedOwner == instancia.getAttributeOwner());
        if (instancia instanceof ICompositeInstance) {
            ((ICompositeInstance) instancia).stream().forEach(i -> assertIsAtributo(i, expectedOwner));
        }
    }

    public void testTipoCompostoTestarValorInicialEValorDefaultIfNull() {
        testInicialEDefault(STypeInteger.class, 10, 11);
        testInicialEDefault(STypeString.class, "A", "B");
        testInicialEDefault(STypeBoolean.class, true, false);
    }

    private static <T extends SType<?>> void testInicialEDefault(Class<T> tipo, Object valorInicial, Object valorIfNull) {
        assertTrue(!valorInicial.equals(valorIfNull));
        SDictionary dicionario = SDictionary.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");
        SType<?> tx = pb.createTipo("x", tipo).withValorInicial(valorInicial);
        SType<?> ty = pb.createTipo("y", tipo).withDefaultValueIfNull(valorIfNull);
        SType<?> tz = pb.createTipo("z", tipo).withValorInicial(valorInicial).withDefaultValueIfNull(valorIfNull);

        SInstance instX = tx.novaInstancia();
        assertEquals(valorInicial, instX.getValor());
        assertEquals(valorInicial, instX.getValorWithDefault());

        SInstance instY = ty.novaInstancia();
        assertNull(instY.getValor());
        assertEquals(valorIfNull, instY.getValorWithDefault());
        instY.setValor(valorInicial);
        assertEquals(valorInicial, instY.getValorWithDefault());
        instY.setValor(null);
        assertEquals(valorIfNull, instY.getValorWithDefault());

        SInstance instZ = tz.novaInstancia();
        assertEquals(valorInicial, instZ.getValor());
        assertEquals(valorInicial, instZ.getValorWithDefault());
        instZ.setValor(null);
        assertEquals(valorIfNull, instZ.getValorWithDefault());
    }
}
