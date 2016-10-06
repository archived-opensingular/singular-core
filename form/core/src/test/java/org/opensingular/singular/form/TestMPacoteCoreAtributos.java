package org.opensingular.singular.form;

import org.opensingular.form.AtrRef;
import org.opensingular.form.ICompositeInstance;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SISimple;
import org.opensingular.form.SInfoType;
import org.opensingular.form.SInstance;
import org.opensingular.form.SPackage;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.TypeBuilder;
import org.opensingular.singular.form.TestMPacoteCoreAtributos.TestPacoteCAI.TipoComAtributoInterno1;
import org.opensingular.singular.form.TestMPacoteCoreAtributos.TestPacoteCAI.TipoComAtributoInterno2;
import org.opensingular.form.type.core.SIInteger;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeBoolean;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.country.brazil.STypeCEP;
import org.opensingular.form.type.country.brazil.STypeCNPJ;
import org.opensingular.form.type.country.brazil.STypeCPF;
import org.opensingular.form.type.util.STypeEMail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.Serializable;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class TestMPacoteCoreAtributos extends TestCaseForm {

    public TestMPacoteCoreAtributos(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    public static final class TestPacoteAA extends SPackage {

        static final AtrRef<STypeInteger, SIInteger, Integer> ATR_XX = new AtrRef<>(TestPacoteAA.class, "xx", STypeInteger.class,
                SIInteger.class, Integer.class);

        static final AtrRef<STypeInteger, SIInteger, Integer> ATR_YY = new AtrRef<>(TestPacoteAA.class, "yy", STypeInteger.class,
                SIInteger.class, Integer.class);

        protected TestPacoteAA() {
            super("teste.pacoteAA");
        }

        @Override
        protected void onLoadPackage(PackageBuilder pb) {
            pb.createAttributeType(ATR_XX);
            pb.addAttribute(SType.class, ATR_XX);

            pb.createAttributeType(ATR_YY).withDefaultValueIfNull(15);
            pb.addAttribute(STypeString.class, ATR_YY, 17);
            pb.addAttribute(STypeCPF.class, ATR_YY, 19);
            pb.addAttribute(STypeCEP.class, ATR_YY, 21);
            pb.addAttribute(STypeCNPJ.class, ATR_YY, 23);

            pb.addAttribute(STypeInteger.class, ATR_YY);
        }
    }

    public void testValorDefaultEInicialDeAtributos() {
        SDictionary dicionario = SDictionary.create();
        dicionario.loadPackage(TestPacoteAA.class);

        // Teste no tipo
        assertLeituraAtributo(dicionario.getType(STypeInteger.class), 15, 15);
        assertLeituraAtributo(dicionario.getType(STypeString.class), 17, 17);
        // Os tipos a seguir extends MTipoString
        assertLeituraAtributo(dicionario.getType(STypeEMail.class), 17, 17);
        assertLeituraAtributo(dicionario.getType(STypeCPF.class), 19, 19);
        assertLeituraAtributo(dicionario.getType(STypeCEP.class), 21, 21);
        assertLeituraAtributo(dicionario.getType(STypeCNPJ.class), 23, 23);
    }

    private static void assertLeituraAtributo(SType<?> alvo, Object esperadoGetValor, Object esperadoGetValorWithDefault) {

        assertEquals(esperadoGetValor, alvo.getAttributeValue(TestPacoteAA.ATR_YY));
        assertEquals(esperadoGetValor, alvo.getAttributeValue(TestPacoteAA.ATR_YY, Integer.class));

        SInstance instancia = alvo.newInstance();
        assertEquals(esperadoGetValor, instancia.getAttributeValue(TestPacoteAA.ATR_YY));
        assertEquals(esperadoGetValor, instancia.getAttributeValue(TestPacoteAA.ATR_YY, Integer.class));

        Integer novoValor = 1024;
        instancia.setAttributeValue(TestPacoteAA.ATR_YY, novoValor);
        assertEquals(novoValor, instancia.getAttributeValue(TestPacoteAA.ATR_YY));
        assertEquals(novoValor, instancia.getAttributeValue(TestPacoteAA.ATR_YY, Integer.class));
    }

    @Test
    public void testAdicionarAtributoEmOutroPacote() {
        SDictionary dicionario = SDictionary.create();
        PackageBuilder pb = dicionario.createNewPackage("teste");
        pb.addAttribute(STypeString.class, TestPacoteAA.ATR_XX, 17);

        STypeString tipo = dicionario.getType(STypeString.class);
        assertEquals(tipo.getAttributeValue(TestPacoteAA.ATR_XX), (Object) 17);
        SIString instancia = tipo.newInstance();
        assertEquals(instancia.getAttributeValue(TestPacoteAA.ATR_XX), (Object) 17);
    }

    @Test
    public void testAtributoValorInicial() {
        SDictionary dicionario = SDictionary.create();
        PackageBuilder pb = dicionario.createNewPackage("teste");

        SType<SIString> tipo = pb.createType("local", STypeString.class).withInitialValue("aqui");
        STypeString tipoString = dicionario.getTypeOptional(STypeString.class);

        assertEquals("aqui", tipo.getAttributeValueInitialValue());
        assertEquals(null, tipoString.getAttributeValueInitialValue());

        SIString i1 = tipo.newInstance();
        assertEquals("aqui", i1.getValue());

        tipo.withInitialValue("la");
        assertEquals("aqui", i1.getValue());
        assertEquals("la", tipo.getAttributeValueInitialValue());
        assertEquals("la", tipo.newInstance().getValue());

        tipo.withInitialValue("none");
        assertEquals("none", tipo.getAttributeValueInitialValue());
        assertEquals("none", tipo.newInstance().getValue());

        tipo.withInitialValue(null);
        assertEquals(null, tipo.getAttributeValueInitialValue());
        assertEquals(null, tipo.newInstance().getValue());

        tipoString.withInitialValue("X");
        assertEquals("X", tipoString.newInstance().getValue());
        assertEquals(null, tipo.newInstance().getValue());

        tipo.withInitialValue("Y");
        assertEquals("X", tipoString.newInstance().getValue());
        assertEquals("Y", tipo.newInstance().getValue());
    }

    @Test
    public void testCriarDoisAtributosComMesmoNome() {
        SDictionary dicionario = SDictionary.create();
        PackageBuilder pb = dicionario.createNewPackage("teste");

        STypeString tipo = pb.createType("X", STypeString.class);
        pb.createAttributeIntoType(tipo, "a", STypeInteger.class);
        assertException(() -> pb.createAttributeIntoType(tipo, "a", STypeString.class), "já está criada",
                "Deveria ter ocorrido uma exception por ter dois atributo com mesmo nome criado pelo mesmo pacote");
    }

    @Test
    public void testCriarDoisAtributosDePacotesDiferentesComMesmoNome() {
        SDictionary dicionario = SDictionary.create();
        PackageBuilder pb1 = dicionario.createNewPackage("teste1");

        STypeSimple<?, ?> tipo = pb1.createType("X", STypeSimple.class);
        STypeInteger at1 = pb1.createAttributeIntoType(tipo, "a", STypeInteger.class);

        PackageBuilder pb2 = dicionario.createNewPackage("teste2");
        STypeInteger at2 = pb2.createAttributeIntoType(tipo, "a", STypeInteger.class);

        assertException(() -> pb2.createAttributeIntoType(dicionario.getType(STypeSimple.class), "a", STypeInteger.class), "já está criada");

        assertEquals("teste1.X.a", at1.getName());
        assertEquals("teste2.a", at2.getName());

        tipo.setAttributeValue(at1.getName(), 1);
        tipo.setAttributeValue(at2.getName(), 2);
        assertEquals((Integer) 1, tipo.getAttributeValue(at1.getName()));
        assertEquals((Integer) 2, tipo.getAttributeValue(at2.getName()));

        tipo.setAttributeValue(at1, 10);
        tipo.setAttributeValue(at2, 20);
        assertEquals((Integer) 10, tipo.getAttributeValue(at1.getName()));
        assertEquals((Integer) 20, tipo.getAttributeValue(at2.getName()));
    }

    @Test
    public void testCriacaoNovosAtributosNosPacotesCerto() {
        SDictionary dicionario = SDictionary.create();
        dicionario.loadPackage(SCorePackageTest.TestPacoteB.class);

        assertEquals("teste.pacoteB.yy", SCorePackageTest.TestPacoteB.ATR_LABEL_Y.getNameFull());

        SType<?> tipoAtributo = dicionario.getType(SCorePackageTest.TestPacoteB.ATR_LABEL_Y.getNameFull());
        assertNotNull(tipoAtributo);
        assertEquals("teste.pacoteB.yy", tipoAtributo.getName());
        assertEquals("teste.pacoteB", tipoAtributo.getPackage().getName());
        assertEquals("teste.pacoteB", tipoAtributo.getParentScope().getName());
    }

    @Test
    public void testCriacaoAtributoDentroDaClasseDoTipo() {
        // Também testa se dá problema um tipo extendendo outro e ambos com
        // onLoadType()

        SDictionary dicionario = SDictionary.create();
        TestPacoteCAI pkg = dicionario.loadPackage(TestPacoteCAI.class);

        TipoComAtributoInterno1 tipo1 = dicionario.getType(TipoComAtributoInterno1.class);
        TipoComAtributoInterno2 tipo2 = dicionario.getType(TipoComAtributoInterno2.class);
        TipoComAtributoInterno1 fieldOfTipo1 = pkg.fieldOfTipoComAtributoInterno1;

        assertNull(tipo1.getAttributeValue(TestPacoteCAI.ATR_REF_ID1));
        assertNull(tipo1.getAttributeValue(TestPacoteCAI.ATR_REF_ID3));
        assertNull(tipo2.getAttributeValue(TestPacoteCAI.ATR_REF_ID1));
        assertNull(tipo2.getAttributeValue(TestPacoteCAI.ATR_REF_ID2));
        assertNull(tipo2.getAttributeValue(TestPacoteCAI.ATR_REF_ID3));
        assertNull(fieldOfTipo1.getAttributeValue(TestPacoteCAI.ATR_REF_ID3));

        tipo1.setAttributeValue(TestPacoteCAI.ATR_REF_ID1, "A1");
        tipo1.setAttributeValue(TestPacoteCAI.ATR_REF_ID3, "A3");

        assertEquals("A1", tipo2.getAttributeValue(TestPacoteCAI.ATR_REF_ID1));
        assertEquals("A3", tipo2.getAttributeValue(TestPacoteCAI.ATR_REF_ID3));
        
        fieldOfTipo1.setAttributeValue(TestPacoteCAI.ATR_REF_ID1, "A1");
        fieldOfTipo1.setAttributeValue(TestPacoteCAI.ATR_REF_ID3, "A3");

        assertEquals("A1", fieldOfTipo1.getAttributeValue(TestPacoteCAI.ATR_REF_ID1));
        assertEquals("A3", fieldOfTipo1.getAttributeValue(TestPacoteCAI.ATR_REF_ID3));

        tipo2.setAttributeValue(TestPacoteCAI.ATR_REF_ID1, "B1");
        tipo2.setAttributeValue(TestPacoteCAI.ATR_REF_ID2, "B2");
        tipo2.setAttributeValue(TestPacoteCAI.ATR_REF_ID3, "B3");

        assertEquals("A1", tipo1.getAttributeValue(TestPacoteCAI.ATR_REF_ID1));
        assertEquals("A3", tipo1.getAttributeValue(TestPacoteCAI.ATR_REF_ID3));
        assertEquals("B1", tipo2.getAttributeValue(TestPacoteCAI.ATR_REF_ID1));
        assertEquals("B2", tipo2.getAttributeValue(TestPacoteCAI.ATR_REF_ID2));
        assertEquals("B3", tipo2.getAttributeValue(TestPacoteCAI.ATR_REF_ID3));

        SIComposite instancia1 = dicionario.newInstance(TipoComAtributoInterno1.class);
        SIComposite instancia2 = dicionario.newInstance(TipoComAtributoInterno2.class);

        assertEquals("A1", instancia1.getAttributeValue(TestPacoteCAI.ATR_REF_ID1));
        assertEquals("A3", instancia1.getAttributeValue(TestPacoteCAI.ATR_REF_ID3));
        assertEquals("B1", instancia2.getAttributeValue(TestPacoteCAI.ATR_REF_ID1));
        assertEquals("B2", instancia2.getAttributeValue(TestPacoteCAI.ATR_REF_ID2));
        assertEquals("B3", instancia2.getAttributeValue(TestPacoteCAI.ATR_REF_ID3));

        instancia1.setAttributeValue(TestPacoteCAI.ATR_REF_ID1, "AI1");
        instancia1.setAttributeValue(TestPacoteCAI.ATR_REF_ID3, "AI3");
        instancia2.setAttributeValue(TestPacoteCAI.ATR_REF_ID1, "BI1");
        instancia2.setAttributeValue(TestPacoteCAI.ATR_REF_ID2, "BI2");
        instancia2.setAttributeValue(TestPacoteCAI.ATR_REF_ID3, "BI3");

        assertEquals("AI1", instancia1.getAttributeValue(TestPacoteCAI.ATR_REF_ID1));
        assertEquals("AI3", instancia1.getAttributeValue(TestPacoteCAI.ATR_REF_ID3));
        assertEquals("BI1", instancia2.getAttributeValue(TestPacoteCAI.ATR_REF_ID1));
        assertEquals("BI2", instancia2.getAttributeValue(TestPacoteCAI.ATR_REF_ID2));
        assertEquals("BI3", instancia2.getAttributeValue(TestPacoteCAI.ATR_REF_ID3));

        assertEquals("A1", tipo1.getAttributeValue(TestPacoteCAI.ATR_REF_ID1));
        assertEquals("A3", tipo1.getAttributeValue(TestPacoteCAI.ATR_REF_ID3));
        assertEquals("B1", tipo2.getAttributeValue(TestPacoteCAI.ATR_REF_ID1));
        assertEquals("B2", tipo2.getAttributeValue(TestPacoteCAI.ATR_REF_ID2));
        assertEquals("B3", tipo2.getAttributeValue(TestPacoteCAI.ATR_REF_ID3));
        
        assertEquals("A1", fieldOfTipo1.getAttributeValue(TestPacoteCAI.ATR_REF_ID1));
        assertEquals("A3", fieldOfTipo1.getAttributeValue(TestPacoteCAI.ATR_REF_ID3));
        
        SInstance instanceOfFieldOfTipo1 = fieldOfTipo1.newInstance();
        
        assertEquals("A1", instanceOfFieldOfTipo1.getAttributeValue(TestPacoteCAI.ATR_REF_ID1));
        assertEquals("A3", instanceOfFieldOfTipo1.getAttributeValue(TestPacoteCAI.ATR_REF_ID3));
        
    }

    @Test
    public void testAtribuicaoDeValoresDeAtributosPorString() {
        SDictionary dicionario = SDictionary.create();
        TestPacoteCAI pkg = dicionario.loadPackage(TestPacoteCAI.class);

        TipoComAtributoInterno1 fieldOfTipo1 = pkg.fieldOfTipoComAtributoInterno1;
        
        SInstance instanceOfFieldOfTipo1 = fieldOfTipo1.newInstance();
        
        instanceOfFieldOfTipo1.setAttributeValue(TestPacoteCAI.ATR_REF_ID3,"what");
        
//        System.out.println(pkg.fieldOfTipoComAtributoInterno1.getNome());
//        System.out.println(pkg.fieldOfTipoComAtributoInterno1.getSuperTipo().getNome());
        
        
        String basePath = pkg.fieldOfTipoComAtributoInterno1.getSuperType().getName()+".";
        instanceOfFieldOfTipo1.setAttributeValue(basePath+TestPacoteCAI.ATR_KEY_ID4,"what");
        assertThat(instanceOfFieldOfTipo1.getAttributeValue(basePath+TestPacoteCAI.ATR_KEY_ID4))
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
        protected void onLoadPackage(PackageBuilder pb) {
            pb.createType(TipoComAtributoInterno1.class);
            pb.createType(TipoComAtributoInterno2.class);
            pb.createAttributeIntoType(TipoComAtributoInterno1.class, ATR_REF_ID1);
            pb.createAttributeIntoType(TipoComAtributoInterno2.class, ATR_REF_ID2);
            STypeComposite<? extends SIComposite> grouper = pb.createCompositeType("Grouper");
            fieldOfTipoComAtributoInterno1 = grouper.addField("TipoComAtributoInterno1", TipoComAtributoInterno1.class);

            pb.createAttributeIntoType(TipoComAtributoInterno1.class, ATR_REF_ID3);
            pb.createAttributeIntoType(TipoComAtributoInterno1.class, ATR_KEY_ID4, STypeString.class);
        }

        @SInfoType(name = "TipoCAI1", spackage = TestPacoteCAI.class)
        public static class TipoComAtributoInterno1 extends STypeComposite<SIComposite> {

            @Override
            protected void onLoadType(TypeBuilder tb) {
                addFieldString("nome");
            }
        }

        @SInfoType(name = "TipoCAI2", spackage = TestPacoteCAI.class)
        public static class TipoComAtributoInterno2 extends TipoComAtributoInterno1 {

            @Override
            protected void onLoadType(TypeBuilder tb) {
                addFieldString("endereco");
            }
        }
    }

    @Test
    public void testIsAttributeETestAtributoComposto() {
        SDictionary dicionario = SDictionary.create();
        PackageBuilder pb1 = dicionario.createNewPackage("teste1");

        STypeComposite<?> tipoPosicao = pb1.createCompositeType("posicao");
        tipoPosicao.addFieldString("cor");
        tipoPosicao.addFieldInteger("linha");

        SType<?> tipo = pb1.createType("X", STypeString.class);
        STypeString at1 = pb1.createAttributeIntoType(tipo, "a", STypeString.class);
        STypeComposite<?> at2 = pb1.createAttributeIntoType(tipo, "b", tipoPosicao);

        tipo.setAttributeValue("a", "a1");
        assertAttribute(tipo.getAttributeInstanceInternal(at1.getName()), null);

        tipo.setAttributeValue("b", "cor", "b1");
        tipo.setAttributeValue("b", "linha", 1);
        assertAttribute(tipo.getAttributeInstanceInternal(at2.getName()), null);
        assertAttribute(((ICompositeInstance) tipo.getAttributeInstanceInternal(at2.getName())).getField("cor"), null);
        assertAttribute(((ICompositeInstance) tipo.getAttributeInstanceInternal(at2.getName())).getField("linha"), null);

        SIString instancia = (SIString) tipo.newInstance();
        assertEquals(false, instancia.isAttribute());
        assertEquals(0, instancia.getAttributes().size());

        instancia.setAttributeValue(at1.getName(), "a2");
        instancia.setAttributeValue(at2.getName(), "cor", "b2");
        instancia.setAttributeValue(at2.getName(), "linha", 2);

        assertEquals(2, instancia.getAttributes().size());
        assertAttribute(instancia.getAttribute(at1.getName()).get(), instancia);
        assertAttribute(instancia.getAttribute(at2.getName()).get(), instancia);
        assertAttribute(((ICompositeInstance) instancia.getAttribute(at2.getName()).get()).getField("cor"), instancia);
        assertAttribute(((ICompositeInstance) instancia.getAttribute(at2.getName()).get()).getField("linha"), instancia);
        instancia.getAttributes().stream().forEach(a -> assertAttribute(a, instancia));
    }

    private static void assertAttribute(SInstance instance, SInstance expectedOwner) {
        assertTrue(instance.isAttribute());
        assertTrue(expectedOwner == instance.getAttributeOwner());
        if (instance instanceof ICompositeInstance) {
            ((ICompositeInstance) instance).stream().forEach(i -> assertAttribute(i, expectedOwner));
        }
    }

    @Test
    public void testTipoCompostoTestarValorInicialEValorDefaultIfNull() {
        testInicialEDefault(STypeInteger.class, 10, 11);
        testInicialEDefault(STypeString.class, "A", "B");
        testInicialEDefault(STypeBoolean.class, true, false);
    }

    private static <T extends STypeSimple<X, V>, X extends SISimple<V>, V extends Serializable>  void testInicialEDefault(Class<T> tipo, Object valorInicial, Object valorIfNull) {
        assertTrue(!valorInicial.equals(valorIfNull));
        SDictionary dicionario = SDictionary.create();
        PackageBuilder pb = dicionario.createNewPackage("teste");
        T tx = (T) pb.createType("x", tipo).withInitialValue(valorInicial);
        T ty = (T) pb.createType("y", tipo).withDefaultValueIfNull(valorIfNull);
        T tz = (T) pb.createType("z", tipo).withInitialValue(valorInicial).withDefaultValueIfNull(valorIfNull);

        SInstance instX = tx.newInstance();
        assertEquals(valorInicial, instX.getValue());
        assertEquals(valorInicial, instX.getValueWithDefault());

        SInstance instY = ty.newInstance();
        assertNull(instY.getValue());
        assertEquals(valorIfNull, instY.getValueWithDefault());
        instY.setValue(valorInicial);
        assertEquals(valorInicial, instY.getValueWithDefault());
        instY.setValue(null);
        assertEquals(valorIfNull, instY.getValueWithDefault());

        SInstance instZ = tz.newInstance();
        assertEquals(valorInicial, instZ.getValue());
        assertEquals(valorInicial, instZ.getValueWithDefault());
        instZ.setValue(null);
        assertEquals(valorIfNull, instZ.getValueWithDefault());
    }
}
