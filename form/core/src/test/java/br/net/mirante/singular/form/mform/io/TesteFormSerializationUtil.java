package br.net.mirante.singular.form.mform.io;

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

import org.junit.Test;

import br.net.mirante.singular.form.mform.ICompositeInstance;
import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.RefService;
import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SList;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.core.annotation.AtrAnnotation;
import br.net.mirante.singular.form.mform.document.RefType;
import br.net.mirante.singular.form.mform.document.SDocument;
import br.net.mirante.singular.form.mform.document.SDocumentFactory;
import br.net.mirante.singular.form.mform.document.ServiceRegistry.Pair;

public class TesteFormSerializationUtil {

    @Test
    public void testVerySimplesCase() {
        SInstance instancia = createSerializableTestInstance("teste.endereco", pacote -> pacote.createTipo("endereco", STypeString.class));
        testSerializacao(instancia);

    }

    @Test
    public void testTipoComposto() {
        SIComposite instancia = (SIComposite) createSerializableTestInstance("teste.endereco", pacote -> {
            STypeComposite<? extends SIComposite> endereco = pacote.createTipoComposto("endereco");
            endereco.addCampoString("rua");
            endereco.addCampoString("bairro");
            endereco.addCampoString("cidade");
        });
        instancia.setValor("rua", "A1");
        instancia.setValor("bairro", "A2");
        testSerializacao(instancia);

        // Testa um subPath
        testSerializacao(instancia.getCampo("bairro"));
    }

    @Test
    public void testSerialializeEmptyObject() {
        SIComposite instance = (SIComposite) createSerializableTestInstance("teste.pedido", pacote -> {
            STypeComposite<?> tipoPedido = pacote.createTipoComposto("pedido");
            tipoPedido.addCampoString("nome");
            tipoPedido.addCampoString("descr");
            tipoPedido.addCampoString("prioridade");
            tipoPedido.addCampoListaOf("clientes", STypeString.class);
        });

        FormSerializationUtil.toInstance(FormSerializationUtil.toSerializedObject(instance));
    }

    @Test
    public void testTipoCompostoComAnotacoes() {
        SIComposite instancia = (SIComposite) createSerializableTestInstance("teste.endereco", pacote -> {
            STypeComposite<? extends SIComposite> endereco = pacote.createTipoComposto("endereco");
            endereco.addCampoString("rua");
            endereco.as(AtrAnnotation::new).setAnnotated();
        });
        instancia.setValor("rua", "rua dos bobos");
        instancia.as(AtrAnnotation::new).text("numero zero ?");

        assertThat(instancia.as(AtrAnnotation::new).text()).isEqualTo("numero zero ?");
        SIComposite r = (SIComposite) testSerializacao(instancia);
        assertThat(r.getCampo("rua").getValue()).isEqualTo("rua dos bobos");
        assertThat(r.as(AtrAnnotation::new).text()).isEqualTo("numero zero ?");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTipoListSimples() {
        SList<SIString> instancia = (SList<SIString>) createSerializableTestInstance("teste.enderecos",
                pacote -> pacote.createTipoListaOf("enderecos", STypeString.class));
        instancia.addValor("A1");
        instancia.addValor("A2");
        instancia.addValor("A3");
        instancia.addValor("A4");
        testSerializacao(instancia);

        // Testa um subPath
        testSerializacao(instancia.getCampo("[1]"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTipoListComposto() {
        SList<SIComposite> instancia = (SList<SIComposite>) createSerializableTestInstance("teste.enderecos", pacote -> {
            STypeComposite<SIComposite> endereco = pacote.createTipoListaOfNovoTipoComposto("enderecos", "endereco").getTipoElementos();
            endereco.addCampoString("rua");
            endereco.addCampoString("bairro");
            endereco.addCampoString("cidade");
        });
        instancia.addNovo(e -> e.setValor("rua", "A1"));
        instancia.addNovo(e -> e.setValor("bairro", "A2"));
        instancia.addNovo(e -> {
            e.setValor("rua", "A31");
            e.setValor("bairro", "A32");
        });
        testSerializacao(instancia);

        // Testa um subPath
        testSerializacao(instancia.getCampo("[0].rua"));
        testSerializacao(instancia.getCampo("[2].bairro"));
    }

    @Test
    public void testTipoCompostoListCompostoList() {
        SIComposite instancia = (SIComposite) createSerializableTestInstance("teste.curriculo", pacote -> {
            STypeComposite<? extends SIComposite> tipoCurriculo = pacote.createTipoComposto("curriculo");
            tipoCurriculo.addCampoString("nome");
            STypeComposite<SIComposite> tipoContato = tipoCurriculo.addCampoListaOfComposto("contatos", "contato").getTipoElementos();
            tipoContato.addCampoInteger("prioridade");
            STypeComposite<SIComposite> endereco = tipoContato.addCampoListaOfComposto("enderecos", "endereco").getTipoElementos();
            endereco.addCampoString("rua");
            endereco.addCampoString("cidade");
        });

        instancia.setValor("nome", "Joao");
        SIComposite contato = (SIComposite) instancia.getFieldList("contatos").addNovo();
        contato.setValor("prioridade", -1);
        contato.getFieldList("enderecos").addNovo();
        contato.setValor("enderecos[0].rua", "A31");
        contato.setValor("enderecos[0].cidade", "A32");
        testSerializacao(instancia);

        // Testa um subPath
        testSerializacao(instancia.getCampo("nome"));
        testSerializacao(instancia.getCampo("contatos"));
    }

    @Test
    public void testSerializacaoReferenciaServico() {
        SInstance instancia = createSerializableTestInstance("teste.endereco", pacote -> pacote.createTipo("endereco", STypeString.class));

        instancia.getDocument().bindLocalService("A", String.class, RefService.of("AA"));
        SInstance instancia2 = testSerializacao(instancia);
        assertEquals("AA", instancia2.getDocument().lookupService("A", String.class));

        // Testa itens não mantido entre serializações
        instancia.getDocument().bindLocalService("B", String.class, RefService.ofToBeDescartedIfSerialized("BB"));
        instancia2 = serializarEDeserializar(instancia);
        assertNull(instancia2.getDocument().lookupService("B", String.class));

    }

    @Test
    public void testSerializacaoAtributos() {
        SIComposite instancia = (SIComposite) createSerializableTestInstance("teste.endereco", pacote -> {
            pacote.getDicionario().loadPackage(SPackageBasic.class);
            STypeComposite<?> tipoEndereco = pacote.createTipoComposto("endereco");
            tipoEndereco.addCampoString("rua");
            tipoEndereco.addCampoString("cidade");
        });
        instancia.setValor("rua", "A");
        instancia.as(AtrBasic.class).label("Address");
        instancia.getCampo("rua").as(AtrBasic.class).label("Street");
        instancia.getCampo("cidade").as(AtrBasic.class).label("City");

        SIComposite instancia2 = (SIComposite) testSerializacao(instancia);

        assertEquals("Address", instancia2.as(AtrBasic.class).getLabel());
        assertEquals("Street", instancia2.getCampo("rua").as(AtrBasic.class).getLabel());
        assertEquals("City", instancia2.getCampo("cidade").as(AtrBasic.class).getLabel());

    }

    @Test
    public void testRefSerialization() {
        SIString endereco = (SIString) createSerializableTestInstance("teste.endereco",
                pacote -> pacote.createTipo("endereco", STypeString.class));

        endereco.setValue("aqui");

        InstanceSerializableRef<?> ref = new InstanceSerializableRef<>(endereco);
        testSerializacao(ref);
    }

    @Test
    public void testSerializationOfTwoIndependnteReferenceAtSameTime() {
        SInstance instancia1 = createSerializableTestInstance("teste.endereco", pacote -> pacote.createTipo("endereco", STypeString.class));

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
        assertNotSame(original, novo);
        assertEquals(original.getClass(), novo.getClass());
        assertEquals(original.getType().getName(), novo.getType().getName());
        assertEquals(original.getType().getClass(), novo.getType().getClass());
        assertEquals(original.getNome(), novo.getNome());
        assertEquals(original.getId(), novo.getId());
        assertEquals(original.getPathFull(), novo.getPathFull());
        if (original.getParent() != null) {
            assertNotNull(novo.getParent());
            assertEquals(original.getParent().getPathFull(), novo.getParent().getPathFull());
        } else {
            assertNull(novo.getParent());
        }
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

        assertEquals(original.getAtributos().size(), novo.getAtributos().size());
        for (Entry<String, SInstance> atrOriginal : original.getAtributos().entrySet()) {
            SInstance atrNovo = novo.getAtributos().get(atrOriginal.getKey());
            assertNotNull(atrNovo);
            assertEquals(atrOriginal.getValue(), atrNovo);
        }
    }
}
