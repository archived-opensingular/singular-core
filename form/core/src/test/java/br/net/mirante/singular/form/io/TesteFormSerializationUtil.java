package br.net.mirante.singular.form.io;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;

import org.fest.assertions.api.Assertions;
import org.junit.Assert;
import org.junit.Test;

import br.net.mirante.singular.form.ICompositeInstance;
import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.RefService;
import br.net.mirante.singular.form.SDictionary;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SIList;
import br.net.mirante.singular.form.SInfoPackage;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.document.RefType;
import br.net.mirante.singular.form.document.SDocument;
import br.net.mirante.singular.form.document.SDocumentFactory;
import br.net.mirante.singular.form.document.ServiceRegistry.Pair;
import br.net.mirante.singular.form.type.basic.AtrBasic;
import br.net.mirante.singular.form.type.basic.SPackageBasic;
import br.net.mirante.singular.form.type.core.SIString;
import br.net.mirante.singular.form.type.core.STypeString;

public class TesteFormSerializationUtil {

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
        SInstanceTesteEndereco instancia = (SInstanceTesteEndereco) SDocumentFactory.empty().createInstance(new RefTypeSeria());

        instancia.setValue("bairro", "A2");
        instancia.setValue("numero", 10);
        instancia.getValue("rua");
        testSerializacao(instancia);

        testSerializacao(instancia.getField("bairro"));
    }

    @Test
    public void testTipoCompostoByClassWithNullValue() {
        SInstanceTesteEndereco instancia = (SInstanceTesteEndereco) SDocumentFactory.empty().createInstance(new RefTypeSeria());

        instancia.setValue("bairro", "A2");
        instancia.setValue("rua", null);
        testSerializacao(instancia);
    }

    public static class RefTypeSeria extends RefType {

        @Override
        protected SType<?> retrieve() {
            SDictionary novo = SDictionary.create();
            return novo.getType(STypeTesteEndereco.class);
        }

    }

    @SInfoPackage(name = "p.teste.seria")
    public static class SPackageTesteSeria extends SPackage {

        @Override
        protected void carregarDefinicoes(PackageBuilder pb) {
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
        SIList<SIComposite> instancia = (SIList<SIComposite>) createSerializableTestInstance("teste.enderecos", pacote -> {
            STypeComposite<SIComposite> endereco = pacote.createListOfNewCompositeType("enderecos", "endereco").getElementsType();
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
        SInstance instancia = createSerializableTestInstance("teste.endereco", pacote -> pacote.createType("endereco", STypeString.class));

        instancia.getDocument().bindLocalService("A", String.class, RefService.of("AA"));
        SInstance instancia2 = testSerializacao(instancia);
        Assert.assertEquals("AA", instancia2.getDocument().lookupService("A", String.class));

        // Testa itens não mantido entre serializações
        instancia.getDocument().bindLocalService("B", String.class, RefService.ofToBeDescartedIfSerialized("BB"));
        instancia2 = serializarEDeserializar(instancia);
        assertNull(instancia2.getDocument().lookupService("B", String.class));

    }

    @Test
    public void testSerializacaoAtributos() {
        SIComposite instancia = (SIComposite) createSerializableTestInstance("teste.endereco", pacote -> {
            pacote.getDictionary().loadPackage(SPackageBasic.class);
            STypeComposite<?> tipoEndereco = pacote.createCompositeType("endereco");
            tipoEndereco.addFieldString("rua");
            tipoEndereco.addFieldString("cidade");
        });
        instancia.setValue("rua", "A");
        instancia.as(AtrBasic.class).label("Address");
        instancia.getField("rua").as(AtrBasic.class).label("Street");
        instancia.getField("cidade").as(AtrBasic.class).label("City");

        SIComposite instancia2 = (SIComposite) testSerializacao(instancia);

        assertEquals("Address", instancia2.as(AtrBasic.class).getLabel());
        assertEquals("Street", instancia2.getField("rua").as(AtrBasic.class).getLabel());
        assertEquals("City", instancia2.getField("cidade").as(AtrBasic.class).getLabel());

    }

    @Test
    public void testRefSerialization() {
        SIString endereco = (SIString) createSerializableTestInstance("teste.endereco",
                pacote -> pacote.createType("endereco", STypeString.class));

        endereco.setValue("aqui");

        InstanceSerializableRef<?> ref = new InstanceSerializableRef<>(endereco);
        testSerializacao(ref);
    }

    @Test
    public void testSerializationOfTwoIndependnteReferenceAtSameTime() {
        SInstance instancia1 = createSerializableTestInstance("teste.endereco", pacote -> pacote.createType("endereco", STypeString.class));

        SInstance instancia2 = SDocumentFactory.empty().createInstance(instancia1.getDocument().getRootRefType().get());

        TwoReferences tr1 = new TwoReferences();
        tr1.ref1 = new InstanceSerializableRef<>(instancia1);
        tr1.ref1.get().setValue("Rua 1");
        tr1.ref2 = new InstanceSerializableRef<>(instancia2);
        tr1.ref2.get().setValue("Rua 2");

        assertSame(tr1.ref1.get().getDictionary(), tr1.ref2.get().getDictionary());

        TwoReferences tr2 = toAndFromByteArray(tr1);

        assertEquivalent(tr1.ref1.get().getDocument(), tr2.ref1.get().getDocument());
        assertEquivalent(tr1.ref1.get(), tr2.ref1.get());
        assertSame(tr2.ref1.get().getDictionary(), tr2.ref2.get().getDictionary());
    }

    private static class TwoReferences implements Serializable {
        public InstanceSerializableRef<?> ref1;
        public InstanceSerializableRef<?> ref2;
    }

    private static void testSerializacaoComResolverSerializado(SInstance original) {
        testSerializacao(original, i -> FormSerializationUtil.toSerializedObject(i), FormSerializationUtil::toInstance);
    }

    public static void testSerializacao(InstanceSerializableRef<?> ref) {
        SInstance instancia2 = toAndFromByteArray(ref).get();
        assertEquivalent(ref.get().getDocument(), instancia2.getDocument());
        assertEquivalent(ref.get(), instancia2);
    }

    public static SInstance testSerializacao(SInstance original) {
        return testSerializacao(original, FormSerializationUtil::toSerializedObject, fs -> FormSerializationUtil.toInstance(fs));
    }

    private static SInstance testSerializacao(SInstance original, Function<SInstance, FormSerialized> toSerial,
            Function<FormSerialized, SInstance> fromSerial) {
        // Testa sem transformar em array de bytes
        FormSerialized fs = toSerial.apply(original);
        SInstance instancia2 = fromSerial.apply(fs);
        assertEquivalent(original.getDocument(), instancia2.getDocument());
        assertEquivalent(original, instancia2);

        fs = toAndFromByteArray(fs);
        instancia2 = fromSerial.apply(fs);
        assertEquivalent(original.getDocument(), instancia2.getDocument());
        assertEquivalent(original, instancia2);

        return instancia2;
    }

    private static <T> T toAndFromByteArray(T obj) {
        try {
            ByteArrayOutputStream out1 = new ByteArrayOutputStream();
            ObjectOutputStream out2 = new ObjectOutputStream(out1);
            out2.writeObject(obj);
            out2.close();

            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(out1.toByteArray()));
            return (T) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static SInstance serializarEDeserializar(SInstance original) {
        return serializarEDeserializar(original, FormSerializationUtil::toSerializedObject, fs -> FormSerializationUtil.toInstance(fs));
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

    public static SInstance createSerializableTestInstance(String typeName, ConfiguradorDicionarioTeste setupCode) {
        RefType refType = new RefType() {

            @Override
            protected SType<?> retrieve() {
                SDictionary novo = SDictionary.create();
                setupCode.setup(novo.createNewPackage("teste"));
                return novo.getType(typeName);
            }
        };
        return SDocumentFactory.empty().createInstance(refType);
    }

    public interface ConfiguradorDicionarioTeste extends Serializable {
        public void setup(PackageBuilder pacote);
    }

    public static void assertEquivalent(SDocument original, SDocument novo) {
        assertNotSame(original, novo);
        assertEquals(original.getLastId(), novo.getLastId());

        for (Entry<String, Pair> service : original.getLocalServices().entrySet()) {
            Object originalService = original.lookupService(service.getKey(), Object.class);
            Object novoService = novo.lookupService(service.getKey(), Object.class);
            if (originalService == null) {
                assertNull(novoService);
            } else if (novoService == null && !(service.getValue().provider instanceof ServiceRefTransientValue)) {
                fail("O documento deserializado para o serviço '" + service.getKey() + "' em vez de retorna uma instancia de "
                        + originalService.getClass().getName() + " retornou null");
            }

        }

        assertEquivalent(original.getRoot(), novo.getRoot());
    }

    private static void assertEquivalent(SInstance original, SInstance novo) {
        try {
            assertNotSame(original, novo);
            assertEquals(original.getClass(), novo.getClass());
            assertEquals(original.getType().getName(), novo.getType().getName());
            assertEquals(original.getType().getClass(), novo.getType().getClass());
            assertEquals(original.getName(), novo.getName());
            assertEquals(original.getId(), novo.getId());
            assertEquals(original.getPathFull(), novo.getPathFull());
            if (original.getParent() != null) {
                assertNotNull(novo.getParent());
                assertEquals(original.getParent().getPathFull(), novo.getParent().getPathFull());
            } else {
                assertNull(novo.getParent());
            }
            // if (false && original instanceof SIComposite) {
            // SIComposite originalC = (SIComposite) original;
            // SIComposite novoC = (SIComposite) novo;
            // for (SInstance field : originalC.getFields()) {
            // assertEquivalent(field, novoC.getField(field.getName()));
            // }
            // for (SInstance field : novoC.getFields()) {
            // assertEquivalent(originalC.getField(field.getName()), field);
            // }
            // }
            if (original instanceof ICompositeInstance) {
                List<SInstance> filhosOriginal = new ArrayList<>(((ICompositeInstance) original).getChildren());
                List<SInstance> filhosNovo = new ArrayList<>(((ICompositeInstance) novo).getChildren());
                assertEquals(filhosOriginal.size(), filhosNovo.size());
                for (int i = 0; i < filhosOriginal.size(); i++) {
                    assertEquivalent(filhosOriginal.get(0), filhosNovo.get(0));
                }
            } else {
                assertEquals(original.getValue(), novo.getValue());
            }

            assertEquals(original.getAttributes().size(), novo.getAttributes().size());
            for (Entry<String, SInstance> atrOriginal : original.getAttributes().entrySet()) {
                SInstance atrNovo = novo.getAttributes().get(atrOriginal.getKey());
                assertNotNull(atrNovo);
                assertEquals(atrOriginal.getValue(), atrNovo);
            }
        } catch (AssertionError e) {
            if (e.getMessage().startsWith("Erro comparando")) {
                throw e;
            }
            throw new AssertionError("Erro comparando '" + original.getPathFull() + "'", e);
        }
    }
}
