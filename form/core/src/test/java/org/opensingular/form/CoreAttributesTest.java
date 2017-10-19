package org.opensingular.form;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.CoreAttributesTest.TestPacoteCAI.TipoComAtributoInterno1;
import org.opensingular.form.CoreAttributesTest.TestPacoteCAI.TipoComAtributoInterno2;
import org.opensingular.form.type.core.SIBoolean;
import org.opensingular.form.type.core.SIInteger;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeBoolean;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.country.brazil.STypeCEP;
import org.opensingular.form.type.country.brazil.STypeCNPJ;
import org.opensingular.form.type.country.brazil.STypeCPF;
import org.opensingular.form.type.util.STypeEMail;

import javax.annotation.Nonnull;
import java.io.Serializable;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class CoreAttributesTest extends TestCaseForm {

    public CoreAttributesTest(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    public static final class TestPacoteAA extends SPackage {

        static final AtrRef<STypeInteger, SIInteger, Integer> ATR_XX = new AtrRef<>(TestPacoteAA.class, "xx", STypeInteger.class,
                SIInteger.class, Integer.class);

        static final AtrRef<STypeInteger, SIInteger, Integer> ATR_YY = new AtrRef<>(TestPacoteAA.class, "yy", STypeInteger.class,
                SIInteger.class, Integer.class);

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
        SDictionary dictionary = SDictionary.create();
        dictionary.loadPackage(TestPacoteAA.class);

        // Teste no tipo
        assertLeituraAtributo(dictionary.getType(STypeInteger.class), 15, 15);
        assertLeituraAtributo(dictionary.getType(STypeString.class), 17, 17);
        // Os tipos a seguir extends MTipoString
        assertLeituraAtributo(dictionary.getType(STypeEMail.class), 17, 17);
        assertLeituraAtributo(dictionary.getType(STypeCPF.class), 19, 19);
        assertLeituraAtributo(dictionary.getType(STypeCEP.class), 21, 21);
        assertLeituraAtributo(dictionary.getType(STypeCNPJ.class), 23, 23);
    }

    private static void assertLeituraAtributo(SType<?> alvo, Object esperadoGetValor, Object esperadoGetValorWithDefault) {

        assertEquals(esperadoGetValor, alvo.getAttributeValue(TestPacoteAA.ATR_YY));
        assertEquals(esperadoGetValor, alvo.getAttributeValue(TestPacoteAA.ATR_YY, Integer.class));

        SInstance instance = alvo.newInstance();
        assertEquals(esperadoGetValor, instance.getAttributeValue(TestPacoteAA.ATR_YY));
        assertEquals(esperadoGetValor, instance.getAttributeValue(TestPacoteAA.ATR_YY, Integer.class));

        Integer newValue = 1024;
        instance.setAttributeValue(TestPacoteAA.ATR_YY, newValue);
        assertEquals(newValue, instance.getAttributeValue(TestPacoteAA.ATR_YY));
        assertEquals(newValue, instance.getAttributeValue(TestPacoteAA.ATR_YY, Integer.class));
    }

    @Test
    public void testAdicionarAtributoEmOutroPacote() {
        SDictionary dictionary = SDictionary.create();
        PackageBuilder pb = dictionary.createNewPackage("teste");
        pb.addAttribute(STypeString.class, TestPacoteAA.ATR_XX, 17);

        STypeString tipo = dictionary.getType(STypeString.class);
        assertEquals(tipo.getAttributeValue(TestPacoteAA.ATR_XX), (Object) 17);
        SIString instance = tipo.newInstance();
        assertEquals(instance.getAttributeValue(TestPacoteAA.ATR_XX), (Object) 17);
    }

    private static boolean exceptionAtCorrentPoint = false;

    @Test
    public void testReadicionarAtributoExclusivo() {
        exceptionAtCorrentPoint = false;
        assertException(() -> createTestDictionary().loadPackage(PackageWrongAttribute1.class),
                SingularFormException.class, "pertence excelusivamente ao tipo");
        assertTrue(exceptionAtCorrentPoint);

        exceptionAtCorrentPoint = false;
        assertException(() -> createTestDictionary().loadPackage(PackageWrongAttribute2.class),
                SingularFormException.class, "já está criada");
        assertTrue(exceptionAtCorrentPoint);
    }

    @SInfoPackage()
    public static class PackageWrongAttribute1 extends SPackage {

        private static final AtrRef<STypeBoolean, SIBoolean, Boolean> ATR_X = new AtrRef(PackageWrongAttribute1.class,
                "aa", STypeBoolean.class, SIBoolean.class, Boolean.class);
        @Override
        protected void onLoadPackage(@Nonnull PackageBuilder pb) {
            pb.createAttributeIntoType(STypeList.class, ATR_X);
            exceptionAtCorrentPoint = true;
            pb.addAttribute(STypeComposite.class, ATR_X);
            exceptionAtCorrentPoint = false;
        }
    }

    @SInfoPackage()
    public static class PackageWrongAttribute2 extends SPackage {

        private static final AtrRef<STypeBoolean, SIBoolean, Boolean> ATR_Y = new AtrRef(PackageWrongAttribute2.class,
                "aa", STypeBoolean.class, SIBoolean.class, Boolean.class);
        @Override
        protected void onLoadPackage(@Nonnull PackageBuilder pb) {
            pb.createAttributeIntoType(STypeList.class, ATR_Y);
            exceptionAtCorrentPoint = true;
            pb.createAttributeIntoType(STypeComposite.class, ATR_Y);
            exceptionAtCorrentPoint = false;
        }
    }

    @Test
    public void testAtributoValorInicial() {
        SDictionary dictionary = SDictionary.create();
        PackageBuilder pb = dictionary.createNewPackage("teste");

        STypeSimple<SIString, String> tipo = (STypeSimple<SIString, String>) pb.createType("local", STypeString.class).setInitialValue("aqui");
        STypeString tipoString = dictionary.getType(STypeString.class);


        SIString i1 = tipo.newInstance();
        assertEquals("aqui", i1.getValue());

        tipo.setInitialValue("la");
        assertEquals("aqui", i1.getValue());
        assertEquals("la", tipo.newInstance().getValue());

        tipo.setInitialValue("none");
        assertEquals("none", tipo.newInstance().getValue());

        tipo.setInitialValue(null);
        assertEquals(null, tipo.newInstance().getValue());

        tipoString.setInitialValue("X");
        assertEquals("X", tipoString.newInstance().getValue());
        assertEquals(null, tipo.newInstance().getValue());

        tipo.setInitialValue("Y");
        assertEquals("X", tipoString.newInstance().getValue());
        assertEquals("Y", tipo.newInstance().getValue());
    }

    @Test
    public void testCriarDoisAtributosComMesmoNome() {
        SDictionary dictionary = SDictionary.create();
        PackageBuilder pb = dictionary.createNewPackage("teste");

        STypeString tipo = pb.createType("X", STypeString.class);
        pb.createAttributeIntoType(tipo, "a", STypeInteger.class);
        assertException(() -> pb.createAttributeIntoType(tipo, "a", STypeString.class), "já está criada",
                "Deveria ter ocorrido uma exception por ter dois atributo com mesmo nome criado pelo mesmo pacote");
    }

    @Test
    public void testCriarDoisAtributosDePacotesDiferentesComMesmoNome() {
        SDictionary dictionary = SDictionary.create();
        PackageBuilder pb1 = dictionary.createNewPackage("teste1");

        STypeSimple<?, ?> tipo = pb1.createType("X", STypeSimple.class);
        STypeInteger at1 = pb1.createAttributeIntoType(tipo, "a", STypeInteger.class);

        PackageBuilder pb2 = dictionary.createNewPackage("teste2");
        STypeInteger at2 = pb2.createAttributeIntoType(tipo, "a", STypeInteger.class);

        assertException(() -> pb2.createAttributeIntoType(dictionary.getType(STypeSimple.class), "a", STypeInteger.class), "já está criada");

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
        SDictionary dictionary = SDictionary.create();
        dictionary.loadPackage(SPackageTest.TestPacoteB.class);

        assertEquals("teste.pacoteB.yy", SPackageTest.TestPacoteB.ATR_LABEL_Y.getNameFull());

        SType<?> tipoAtributo = dictionary.getType(SPackageTest.TestPacoteB.ATR_LABEL_Y.getNameFull());
        assertNotNull(tipoAtributo);
        assertEquals("teste.pacoteB.yy", tipoAtributo.getName());
        assertEquals("teste.pacoteB", tipoAtributo.getPackage().getName());
        assertEquals("teste.pacoteB", tipoAtributo.getParentScope().getName());
    }

    @Test
    public void testCriacaoAtributoDentroDaClasseDoTipo() {
        // Também testa se dá problema um tipo extendendo outro e ambos com
        // onLoadType()

        SDictionary dictionary = SDictionary.create();
        TestPacoteCAI pkg = dictionary.loadPackage(TestPacoteCAI.class);

        TipoComAtributoInterno1 tipo1 = dictionary.getType(TipoComAtributoInterno1.class);
        TipoComAtributoInterno2 tipo2 = dictionary.getType(TipoComAtributoInterno2.class);
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

        SIComposite instance1 = dictionary.newInstance(TipoComAtributoInterno1.class);
        SIComposite instance2 = dictionary.newInstance(TipoComAtributoInterno2.class);

        assertEquals("A1", instance1.getAttributeValue(TestPacoteCAI.ATR_REF_ID1));
        assertEquals("A3", instance1.getAttributeValue(TestPacoteCAI.ATR_REF_ID3));
        assertEquals("B1", instance2.getAttributeValue(TestPacoteCAI.ATR_REF_ID1));
        assertEquals("B2", instance2.getAttributeValue(TestPacoteCAI.ATR_REF_ID2));
        assertEquals("B3", instance2.getAttributeValue(TestPacoteCAI.ATR_REF_ID3));

        instance1.setAttributeValue(TestPacoteCAI.ATR_REF_ID1, "AI1");
        instance1.setAttributeValue(TestPacoteCAI.ATR_REF_ID3, "AI3");
        instance2.setAttributeValue(TestPacoteCAI.ATR_REF_ID1, "BI1");
        instance2.setAttributeValue(TestPacoteCAI.ATR_REF_ID2, "BI2");
        instance2.setAttributeValue(TestPacoteCAI.ATR_REF_ID3, "BI3");

        assertEquals("AI1", instance1.getAttributeValue(TestPacoteCAI.ATR_REF_ID1));
        assertEquals("AI3", instance1.getAttributeValue(TestPacoteCAI.ATR_REF_ID3));
        assertEquals("BI1", instance2.getAttributeValue(TestPacoteCAI.ATR_REF_ID1));
        assertEquals("BI2", instance2.getAttributeValue(TestPacoteCAI.ATR_REF_ID2));
        assertEquals("BI3", instance2.getAttributeValue(TestPacoteCAI.ATR_REF_ID3));

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
        SDictionary dictionary = SDictionary.create();
        TestPacoteCAI pkg = dictionary.loadPackage(TestPacoteCAI.class);

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

        static final AtrRef<STypeString, SIString, String> ATR_REF_ID1 = new AtrRef<>(TipoComAtributoInterno1.class, "refId1", STypeString.class,
                SIString.class, String.class);

        static final AtrRef<STypeString, SIString, String> ATR_REF_ID2 = new AtrRef<>(TipoComAtributoInterno2.class, "refId2", STypeString.class,
                SIString.class, String.class);

        static final AtrRef<STypeString, SIString, String> ATR_REF_ID3 = new AtrRef<>(TipoComAtributoInterno1.class, "refId3", STypeString.class,
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
        SDictionary dictionary = SDictionary.create();
        PackageBuilder pb1 = dictionary.createNewPackage("teste1");

        STypeComposite<?> tipoPosicao = pb1.createCompositeType("posicao");
        tipoPosicao.addFieldString("cor");
        tipoPosicao.addFieldInteger("linha");

        SType<?> tipo = pb1.createType("X", STypeString.class);
        STypeString at1 = pb1.createAttributeIntoType(tipo, "a", STypeString.class);
        STypeComposite<?> at2 = pb1.createAttributeIntoType(tipo, "b", tipoPosicao);

        tipo.setAttributeValue("teste1.X.a", "a1");
        assertAttribute(tipo.findAttributeInstance(at1.getName()), null);

        tipo.setAttributeValue("teste1.X.b", "cor", "b1");
        tipo.setAttributeValue("teste1.X.b", "linha", 1);
        assertAttribute(tipo.findAttributeInstance(at2.getName()), null);
        assertAttribute(((ICompositeInstance) tipo.findAttributeInstance(at2.getName())).getField("cor"), null);
        assertAttribute(((ICompositeInstance) tipo.findAttributeInstance(at2.getName())).getField("linha"), null);

        SIString instance = (SIString) tipo.newInstance();
        assertEquals(false, instance.isAttribute());
        assertEquals(0, instance.getAttributes().size());

        instance.setAttributeValue(at1.getName(), "a2");
        instance.setAttributeValue(at2.getName(), "cor", "b2");
        instance.setAttributeValue(at2.getName(), "linha", 2);

        assertEquals(2, instance.getAttributes().size());
        assertAttribute(instance.getAttributeDirectly(at1.getName()).get(), instance);
        assertAttribute(instance.getAttributeDirectly(at2.getName()).get(), instance);
        assertAttribute(((ICompositeInstance) instance.getAttributeDirectly(at2.getName()).get()).getField("cor"), instance);
        assertAttribute(((ICompositeInstance) instance.getAttributeDirectly(at2.getName()).get()).getField("linha"), instance);
        instance.getAttributes().stream().forEach(a -> assertAttribute(a, instance));
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
        SDictionary dictionary = SDictionary.create();
        PackageBuilder pb = dictionary.createNewPackage("teste");
        T tx = (T) pb.createType("x", tipo).setInitialValue(valorInicial);
        T ty = (T) pb.createType("y", tipo).withDefaultValueIfNull(valorIfNull);
        T tz = (T) pb.createType("z", tipo).setInitialValue(valorInicial).withDefaultValueIfNull(valorIfNull);

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
