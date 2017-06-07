package org.opensingular.form.io;

import org.fest.assertions.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.AtrRef;
import org.opensingular.form.InstanceSerializableRef;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.RefService;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInfoPackage;
import org.opensingular.form.SInfoType;
import org.opensingular.form.SInstance;
import org.opensingular.form.SPackage;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TestCaseForm;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.context.ServiceRegistry;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.helpers.AssertionsSInstance;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.type.core.SIInteger;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.internal.lib.commons.util.SingularIOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map.Entry;
import java.util.function.Function;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class TestFormSerializationUtil extends TestCaseForm {

    public TestFormSerializationUtil(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    public static void testSerializacao(InstanceSerializableRef<?> ref) {
        SInstance instancia2 = SingularIOUtils.serializeAndDeserialize(ref).get();
        assertEquivalent(ref.get().getDocument(), instancia2.getDocument(), true);
        assertEquivalent(ref.get(), instancia2);
    }

    /**
     * Serializa e deserializa a instância e testa se a versão original e a deserializada possuem o mesmo conteúdo.
     */
    public static SInstance testSerializacao(SInstance original) {
        return testSerializacao(original, FormSerializationUtil::toSerializedObject, FormSerializationUtil::toInstance);
    }

    private static SInstance testSerializacao(SInstance original, Function<SInstance, FormSerialized> toSerial,
                                              Function<FormSerialized, SInstance> fromSerial) {
        // Testa sem transformar em array de bytes
        FormSerialized fs         = toSerial.apply(original);
        SInstance      instancia2 = fromSerial.apply(fs);
        assertEquivalent(original.getDocument(), instancia2.getDocument(), fs.getXml() != null);
        assertEquivalent(original, instancia2);

        fs = SingularIOUtils.serializeAndDeserialize(fs);
        instancia2 = fromSerial.apply(fs);
        assertEquivalent(original.getDocument(), instancia2.getDocument(), fs.getXml() != null);
        assertEquivalent(original, instancia2);

        return instancia2;
    }

    public static SInstance serializarEDeserializar(SInstance original) {
        return serializarEDeserializar(original, FormSerializationUtil::toSerializedObject,
                fs -> FormSerializationUtil.toInstance(fs));
    }

    private static SInstance serializarEDeserializar(SInstance original, Function<SInstance, FormSerialized> toSerial,
                                                     Function<FormSerialized, SInstance> fromSerial) {
        try {
            ByteArrayOutputStream out1 = new ByteArrayOutputStream();
            ObjectOutputStream out2 = new ObjectOutputStream(out1);
            out2.writeObject(toSerial.apply(original));
            out2.close();

            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(out1.toByteArray()));
            return fromSerial.apply((FormSerialized) in.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void assertEquivalent(SDocument original, SDocument copy, boolean verificarLastId) {
        assertNotSame(original, copy);
        if (verificarLastId) {
            assertEquals(original.getLastId(), copy.getLastId());
        }

        for (Entry<String, ServiceRegistry.ServiceEntry> service : original.getLocalRegistry().services().entrySet()) {
            Object originalService = original.lookupLocalService(service.getKey(), Object.class).orElse(null);
            Object copyService = copy.lookupLocalService(service.getKey(), Object.class).orElse(null);
            if (originalService == null) {
                assertNull(copyService);
            } else if (copyService == null && !(service.getValue().provider instanceof ServiceRefTransientValue)) {
                fail("O documento deserializado para o serviço '" + service.getKey() + "' em vez de retorna uma instancia de "
                        + originalService.getClass().getName() + " retornou null");
            }

        }

        assertEquivalent(original.getRoot(), copy.getRoot());
    }

    private static void assertEquivalent(SInstance original, SInstance copy) {
        AssertionsSInstance.assertEquivalentInstance(original, copy);
    }

    @Test
    public void testVerySimplesCase() {
        SInstance instancia = createSerializableTestInstance("teste.endereco", pacote -> pacote.createType("endereco", STypeString.class));
        testSerializacao(instancia);

    }

    @Test
    public void testTipoComposto() {
        SIComposite instancia = (SIComposite) createSerializableTestInstance("teste.endereco", pacote -> {
            STypeComposite<? extends SIComposite> endereco = pacote.createCompositeType("endereco");
            endereco.addFieldString("rua");
            endereco.addFieldString("bairro");
            endereco.addFieldInteger("numero");
            endereco.addFieldString("cidade");
        });
        instancia.setValue("rua", "A1");
        instancia.setValue("bairro", "A2");
        instancia.setValue("numero", 10);
        testSerializacao(instancia);

        // Testa um subPath
        testSerializacao(instancia.getField("bairro"));

        instancia.setValue("numero", null);
        testSerializacao(instancia);
    }

    @Test
    public void testTipoCompostoByClass() {
        SInstanceTesteEndereco instancia = (SInstanceTesteEndereco) SDocumentFactory.empty().createInstance(RefType.of(STypeTesteEndereco.class));

        instancia.setValue("bairro", "A2");
        instancia.setValue("numero", 10);
        instancia.getValue("rua");
        testSerializacao(instancia);

        testSerializacao(instancia.getField("bairro"));
    }

    @Test
    public void testTipoCompostoByClassWithNullValue() {
        SInstanceTesteEndereco instancia = (SInstanceTesteEndereco) SDocumentFactory.empty().createInstance(RefType.of(STypeTesteEndereco.class));

        instancia.setValue("bairro", "A2");
        instancia.setValue("rua", null);
        testSerializacao(instancia);
    }

    @Test
    public void testTipoCompostoWithSubTipoCompostoWithOneFieldSetToNull() {
        SIComposite bloco = (SIComposite) createSerializableTestInstance("teste.bloco", pacote -> {
            STypeComposite<SIComposite> tipoBloco = pacote.createCompositeType("bloco");
            STypeComposite<SIComposite> sub = tipoBloco.addFieldComposite("ref");
            sub.addFieldString("bairro");
            sub.addFieldString("rua");
        });
        bloco.setValue("ref.rua", null);
        testSerializacao(bloco);
    }

    @Test
    public void testTipoCompostoWithSubTipoCompostoByClassWithOneFieldSetToNull() {
        SIComposite bloco = (SIComposite) createSerializableTestInstance("teste.bloco", pacote -> {
            STypeComposite<SIComposite> tipoBloco = pacote.createCompositeType("bloco");
            tipoBloco.addField("ref", STypeTesteEndereco.class);
        });
        bloco.setValue("ref.rua", null);
        testSerializacao(bloco);
    }

    @Test
    public void testSerialializeEmptyObject() {
        SIComposite instance = (SIComposite) createSerializableTestInstance("teste.pedido", pacote -> {
            STypeComposite<?> tipoPedido = pacote.createCompositeType("pedido");
            tipoPedido.addFieldString("nome");
            tipoPedido.addFieldString("descr");
            tipoPedido.addFieldString("prioridade");
            tipoPedido.addFieldListOf("clientes", STypeString.class);
        });

        FormSerializationUtil.toInstance(FormSerializationUtil.toSerializedObject(instance));
    }

    @Test
    public void testTipoCompostoComAnotacoes() {
        SIComposite instancia = (SIComposite) createSerializableTestInstance("teste.endereco", pacote -> {
            STypeComposite<? extends SIComposite> endereco = pacote.createCompositeType("endereco");
            endereco.addFieldString("rua");
            endereco.asAtrAnnotation().setAnnotated();
        });
        instancia.setValue("rua", "rua dos bobos");
        instancia.asAtrAnnotation().text("numero zero ?");

        Assertions.assertThat(instancia.asAtrAnnotation().text()).isEqualTo("numero zero ?");
        SIComposite r = (SIComposite) testSerializacao(instancia);
        assertThat(r.getField("rua").getValue()).isEqualTo("rua dos bobos");
        Assertions.assertThat(r.asAtrAnnotation().text()).isEqualTo("numero zero ?");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTipoListSimples() {
        SIList<SIString> instancia = (SIList<SIString>) createSerializableTestInstance("teste.enderecos",
                pacote -> pacote.createListTypeOf("enderecos", STypeString.class));
        instancia.addValue("A1");
        instancia.addValue("A2");
        instancia.addValue("A3");
        instancia.addValue("A4");
        testSerializacao(instancia);

        // Testa um subPath
        testSerializacao(instancia.getField("[1]"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTipoListComposto() {
        SIList<SIComposite> instancia = (SIList<SIComposite>) createSerializableTestInstance("teste.enderecos",
                pacote -> {
                    STypeComposite<SIComposite> endereco =
                            pacote.createListOfNewCompositeType("enderecos", "endereco").getElementsType();
                    endereco.addFieldString("rua");
                    endereco.addFieldString("bairro");
                    endereco.addFieldString("cidade");
                });
        instancia.addNew(e -> e.setValue("rua", "A1"));
        instancia.addNew(e -> e.setValue("bairro", "A2"));
        instancia.addNew(e -> {
            e.setValue("rua", "A31");
            e.setValue("bairro", "A32");
        });
        testSerializacao(instancia);

        // Testa um subPath
        testSerializacao(instancia.getField("[0].rua"));
        testSerializacao(instancia.getField("[2].bairro"));
    }

    @Test
    public void testTipoCompostoListCompostoList() {
        SIComposite instancia = (SIComposite) createSerializableTestInstance("teste.curriculo", pacote -> {
            STypeComposite<? extends SIComposite> tipoCurriculo = pacote.createCompositeType("curriculo");
            tipoCurriculo.addFieldString("nome");
            STypeComposite<SIComposite> tipoContato = tipoCurriculo.addFieldListOfComposite("contatos", "contato").getElementsType();
            tipoContato.addFieldInteger("prioridade");
            STypeComposite<SIComposite> endereco = tipoContato.addFieldListOfComposite("enderecos", "endereco").getElementsType();
            endereco.addFieldString("rua");
            endereco.addFieldString("cidade");
        });

        instancia.setValue("nome", "Joao");
        SIComposite contato = (SIComposite) instancia.getFieldList("contatos").addNew();
        contato.setValue("prioridade", -1);
        contato.getFieldList("enderecos").addNew();
        contato.setValue("enderecos[0].rua", "A31");
        contato.setValue("enderecos[0].cidade", "A32");
        testSerializacao(instancia);

        // Testa um subPath
        testSerializacao(instancia.getField("nome"));
        testSerializacao(instancia.getField("contatos"));
    }

    @Test
    public void testSerializacaoReferenciaServico() {
        SInstance instancia = createSerializableTestInstance("teste.endereco", pacote -> pacote.createType("endereco" +
                "", STypeString.class));

        instancia.getDocument().bindLocalService("A", String.class, RefService.of("AA"));
        SInstance instancia2 = testSerializacao(instancia);
        Assert.assertEquals("AA", instancia2.getDocument().lookupLocalService("A", String.class).orElse(null));

        // Testa itens não mantido entre serializações
        instancia.getDocument().bindLocalService("B", String.class, RefService.ofToBeDescartedIfSerialized("BB"));
        instancia2 = serializarEDeserializar(instancia);
        assertNull(instancia2.getDocument().lookupLocalService("B", String.class).orElse(null));

    }

    @Test
    public void testSerializacaoAtributos() {
        SIComposite instancia = (SIComposite) createSerializableTestInstance("teste.endereco", pacote -> {
            pacote.loadPackage(SPackageBasic.class);
            STypeComposite<?> tipoEndereco = pacote.createCompositeType("endereco");
            tipoEndereco.addFieldString("rua");
            tipoEndereco.addFieldString("cidade");
        });
        instancia.setValue("rua", "A");
        instancia.asAtr().label("Address");
        instancia.getField("rua").asAtr().label("Street");
        instancia.getField("cidade").asAtr().label("City");

        SIComposite instancia2 = (SIComposite) testSerializacao(instancia);

        assertEquals("Address", instancia2.asAtr().getLabel());
        assertEquals("Street", instancia2.getField("rua").asAtr().getLabel());
        assertEquals("City", instancia2.getField("cidade").asAtr().getLabel());

    }

    /**
     * Testa se consegue serializar/deserializar uma instancia com um atributo que não é carregado na carga do tipo,
     * mas dinamicamente mais adiante após a criação.
     */
    @Test
    public void testSerializationAttributes_withDinamicLoadedAttribute() {
        SInstance instance  = createDinamicAttributeInstance();
        SInstance instance2 = serializarEDeserializar(instance);
        assertCorrectAttributeRead(assertInstance(instance2));
    }

    /**
     * Testa se consegue serializar/deserializar uma instancia com um atributo que não é carregado na carga do tipo,
     * mas dinamicamente mais adiante após a criação.
     */
    @Test
    public void testSerializationAttributes_withDinamicLoadedAttribute_twice() {
        SInstance instance  = createDinamicAttributeInstance();
        SInstance instance2 = serializarEDeserializar(instance);
        SInstance instance3 = serializarEDeserializar(instance2);
        assertCorrectAttributeRead(assertInstance(instance3));
    }

    private void assertCorrectAttributeRead(AssertionsSInstance instance) {
        //Verifica se a deserialização trouxe os dados de atributos corretos
        AtrRef<STypeString, SIString, String>    atr1 = PackageDinamicAttr.ATR_TEXT1;
        AtrRef<STypeInteger, SIInteger, Integer> atr2 = PackageDinamicAttr.ATR_INT1;
        assertFalse(instance.getTarget().getDictionary().getTypeOptional(atr1.getNameFull()).isPresent());
        assertFalse(instance.getTarget().getDictionary().getTypeOptional(atr2.getNameFull()).isPresent());

        instance.isAttribute(atr1.getNameFull(), "V1");
        instance.isAttribute(atr2.getNameFull(), "20");

        assertFalse(instance.getTarget().getDictionary().getTypeOptional(atr1.getNameFull()).isPresent());
        assertFalse(instance.getTarget().getDictionary().getTypeOptional(atr2.getNameFull()).isPresent());

        instance.isAttribute(atr1, "V1");
        instance.isAttribute(atr2, 20);

        //Depois das linhas a cima, então têm que ter convertido os valores
        instance.isAttribute(atr2.getNameFull(), 20);
        assertEquals(SIInteger.class, instance.getTarget().getAttributeDirectly(atr2.getNameFull()).orElse(null).getClass());

        assertTrue(instance.getTarget().getDictionary().getTypeOptional(atr1.getNameFull()).isPresent());
        assertTrue(instance.getTarget().getDictionary().getTypeOptional(atr2.getNameFull()).isPresent());
    }

    @NotNull
    private SIComposite createDinamicAttributeInstance() {
        SIComposite instance = (SIComposite) createSerializableTestInstance("teste.endereco", pacote -> {
            pacote.loadPackage(SPackageBasic.class);
            STypeComposite<?> tipoEndereco = pacote.createCompositeType("endereco");
            tipoEndereco.addFieldString("rua");
        });
        instance.setValue("rua", "A");
        instance.asAtr().label("Address");

        assertFalse(instance.getDictionary().getTypeOptional(PackageDinamicAttr.ATR_TEXT1.getNameFull()).isPresent());
        assertFalse(instance.getDictionary().getTypeOptional(PackageDinamicAttr.ATR_INT1.getNameFull()).isPresent());

        //Agora força a adição dinâmica do atributo ao type
        instance.setAttributeValue(PackageDinamicAttr.ATR_TEXT1, "V1");
        instance.setAttributeValue(PackageDinamicAttr.ATR_INT1, 20);

        assertTrue(instance.getDictionary().getTypeOptional(PackageDinamicAttr.ATR_TEXT1.getNameFull()).isPresent());
        assertTrue(instance.getDictionary().getTypeOptional(PackageDinamicAttr.ATR_INT1.getNameFull()).isPresent());

        return instance;
    }

    @Test
    public void testRefSerialization() {
        SIString endereco = (SIString) createSerializableTestInstance("teste.endereco",
                pacote -> pacote.createType("endereco", STypeString.class));

        endereco.setValue("aqui");

        InstanceSerializableRef<?> ref = endereco.getSerializableRef();
        testSerializacao(ref);
    }

    @Test
    public void testSerializationOfTwoIndependnteReferenceAtSameTime() {
        SInstance instancia1 = createSerializableTestInstance("teste.endereco", pacote -> pacote.createType("endereco", STypeString.class));

        SInstance instancia2 = SDocumentFactory.empty().createInstance(instancia1.getDocument().getRootRefType().get());

        TwoReferences tr1 = new TwoReferences();
        tr1.ref1 = instancia1.getSerializableRef();
        tr1.ref1.get().setValue("Rua 1");
        tr1.ref2 = instancia2.getSerializableRef();
        tr1.ref2.get().setValue("Rua 2");

        assertSame(tr1.ref1.get().getDictionary(), tr1.ref2.get().getDictionary());

        TwoReferences tr2 = SingularIOUtils.serializeAndDeserialize(tr1);

        assertEquivalent(tr1.ref1.get().getDocument(), tr2.ref1.get().getDocument(), true);
        assertEquivalent(tr1.ref1.get(), tr2.ref1.get());
        assertSame(tr2.ref1.get().getDictionary(), tr2.ref2.get().getDictionary());
    }

    @SInfoPackage(name = "p.teste.seria")
    public static class SPackageTesteSeria extends SPackage {

        @Override
        protected void onLoadPackage(PackageBuilder pb) {
            pb.createType(STypeTesteEndereco.class);
        }

    }

    @SInfoType(name = "TesteEndereco", spackage = SPackageTesteSeria.class)
    public static class STypeTesteEndereco extends STypeComposite<SInstanceTesteEndereco> {

        public STypeTesteEndereco() {
            super(SInstanceTesteEndereco.class);
        }

        @Override
        protected void onLoadType(TypeBuilder tb) {
            addFieldString("rua");
            addFieldString("bairro");
            addFieldInteger("numero");
            addFieldString("cidade");
        }
    }

    public static class SInstanceTesteEndereco extends SIComposite {

    }

    @SInfoPackage(name = "dinamic")
    public static class PackageDinamicAttr extends SPackage {

        public static final AtrRef<STypeString, SIString, String> ATR_TEXT1 = new AtrRef<>(
                PackageDinamicAttr.class, "text1", STypeString.class,
                SIString.class, String.class);

        public static final AtrRef<STypeInteger, SIInteger, Integer> ATR_INT1 = new AtrRef<>(
                PackageDinamicAttr.class, "int1", STypeInteger.class,
                SIInteger.class, Integer.class);

        protected void onLoadPackage(PackageBuilder pb) {
            pb.createAttributeIntoType(SType.class, ATR_TEXT1);
            pb.createAttributeIntoType(SType.class, ATR_INT1);
        }
    }

    private static class TwoReferences implements Serializable {
        public InstanceSerializableRef<?> ref1;
        public InstanceSerializableRef<?> ref2;
    }
}