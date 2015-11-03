package br.net.mirante.singular.form.mform.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Test;

import br.net.mirante.singular.form.mform.ICompositeInstance;
import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.MDicionarioLoader;
import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.SDocument;
import br.net.mirante.singular.form.mform.core.MIString;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.io.FormSerializationUtil.FormSerialized;

public class TesteFormSerializationUtil {

    @Test
    public void testVerySimplesCase() {
        MDicionarioLoader loader = createLoaderPacoteTeste((pacote) -> {
            pacote.createTipo("endereco", MTipoString.class);
        });
        MInstancia instancia = loader.loadType("teste.endereco").novaInstancia();
        testSerializacao(loader, instancia);

    }

    @Test
    public void testTipoComposto() {
        MDicionarioLoader loader = createLoaderPacoteTeste((pacote) -> {
            MTipoComposto<? extends MIComposto> endereco = pacote.createTipoComposto("endereco");
            endereco.addCampoString("rua");
            endereco.addCampoString("bairro");
            endereco.addCampoString("cidade");
        });
        MIComposto instancia = (MIComposto) loader.loadType("teste.endereco").novaInstancia();
        instancia.setValor("rua", "A1");
        instancia.setValor("bairro", "A2");
        testSerializacao(loader, instancia);

        // Testa um subPath
        testSerializacao(loader, instancia.getCampo("bairro"));
    }

    @Test
    public void testTipoListSimples() {
        MDicionarioLoader loader = createLoaderPacoteTeste((pacote) -> {
            pacote.createTipoListaOf("enderecos", MTipoString.class);
        });
        MILista<MIString> instancia = (MILista<MIString>) loader.loadType("teste.enderecos").novaInstancia();
        instancia.addValor("A1");
        instancia.addValor("A2");
        instancia.addValor("A3");
        instancia.addValor("A4");
        testSerializacao(loader, instancia);

        // Testa um subPath
        testSerializacao(loader, instancia.getCampo("[1]"));
    }

    @Test
    public void testTipoListComposto() {
        MDicionarioLoader loader = createLoaderPacoteTeste((pacote) -> {
            MTipoComposto<MIComposto> endereco = pacote.createTipoListaOfNovoTipoComposto("enderecos", "endereco").getTipoElementos();
            endereco.addCampoString("rua");
            endereco.addCampoString("bairro");
            endereco.addCampoString("cidade");
        });
        MILista<MIComposto> instancia = (MILista<MIComposto>) loader.loadType("teste.enderecos").novaInstancia();
        instancia.addNovo(e -> e.setValor("rua", "A1"));
        instancia.addNovo(e -> e.setValor("bairro", "A2"));
        instancia.addNovo(e -> {
            e.setValor("rua", "A31");
            e.setValor("bairro", "A32");
        });
        testSerializacao(loader, instancia);

        // Testa um subPath
        testSerializacao(loader, instancia.getCampo("[0].rua"));
        testSerializacao(loader, instancia.getCampo("[2].bairro"));
    }

    @Test
    public void testTipoCompostoListCompostoList() {
        MDicionarioLoader loader = createLoaderPacoteTeste((pacote) -> {
            MTipoComposto<? extends MIComposto> tipoCurriculo = pacote.createTipoComposto("curriculo");
            tipoCurriculo.addCampoString("nome");
            MTipoComposto<MIComposto> tipoContato = tipoCurriculo.addCampoListaOfComposto("contatos", "contato").getTipoElementos();
            tipoContato.addCampoInteger("prioridade");
            MTipoComposto<MIComposto> endereco = tipoContato.addCampoListaOfComposto("enderecos", "endereco").getTipoElementos();
            endereco.addCampoString("rua");
            endereco.addCampoString("cidade");
        });

        MIComposto instancia = (MIComposto) loader.loadType("teste.curriculo").novaInstancia();
        instancia.setValor("nome", "Joao");
        MIComposto contato = (MIComposto) instancia.getFieldList("contatos").addNovo();
        contato.setValor("prioridade", -1);
        contato.getFieldList("enderecos").addNovo();
        contato.setValor("enderecos[0].rua", "A31");
        contato.setValor("enderecos[0].cidade", "A32");
        testSerializacao(loader, instancia);

        // Testa um subPath
        testSerializacao(loader, instancia.getCampo("nome"));
        testSerializacao(loader, instancia.getCampo("contatos"));
    }

    private static void testSerializacao(MDicionarioLoader loader, MInstancia original) {
        // Testa sem transformar em array de bytes
        FormSerialized fs = FormSerializationUtil.toSerializedObject(original);
        MInstancia instancia2 = FormSerializationUtil.toInstance(fs, loader);
        assertEquivalent(original.getDocument(), instancia2.getDocument());
        assertEquivalent(original, instancia2);

        // Testa transformando em um array de bytes
        try {
            ByteArrayOutputStream out1 = new ByteArrayOutputStream();
            ObjectOutputStream out2 = new ObjectOutputStream(out1);
            out2.writeObject(fs);
            out2.close();

            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(out1.toByteArray()));
            fs = (FormSerialized) in.readObject();
            instancia2 = FormSerializationUtil.toInstance(fs, loader);
            assertEquivalent(original.getDocument(), instancia2.getDocument());
            assertEquivalent(original, instancia2);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static MDicionarioLoader createLoaderPacoteTeste(Consumer<PacoteBuilder> setupCode) {
        return new MDicionarioLoader() {
            @Override
            protected void configDicionary(MDicionario newDicionary, String taregetTypeName) {
                setupCode.accept(newDicionary.criarNovoPacote("teste"));
            }
        };
    }

    private static MDicionarioLoader createLoader(Consumer<MDicionario> setupCode) {
        return new MDicionarioLoader() {
            @Override
            protected void configDicionary(MDicionario newDicionary, String taregetTypeName) {
                setupCode.accept(newDicionary);
            }
        };
    }

    private static void assertEquivalent(SDocument original, SDocument novo) {
        assertNotSame(original, novo);
        for (String serviceName : original.getLocalServices().keySet()) {
            Object originalService = original.lookupLocalService(serviceName, Object.class);
            Object novoService = novo.lookupLocalService(serviceName, Object.class);
            if (originalService == null) {
                assertNull(novoService);
            } else if (novoService == null) {
                fail("O documento deseriazado para o serviço '" + serviceName + "' em vez de retorna uma instancia de "
                        + originalService.getClass().getName() + " retornou null");
            }

        }

        try {
            assertEquivalent(original.getRoot(), novo.getRoot());
        } catch (AssertionError e) {
            original.getRoot().debug();
            novo.getRoot().debug();
            throw e;
        }
    }

    private static void assertEquivalent(MInstancia original, MInstancia novo) {
        assertNotSame(original, novo);
        assertEquals(original.getClass(), novo.getClass());
        assertEquals(original.getMTipo().getNome(), novo.getMTipo().getNome());
        assertEquals(original.getMTipo().getClass(), novo.getMTipo().getClass());
        assertEquals(original.getNome(), novo.getNome());
        assertEquals(original.getPathFull(), novo.getPathFull());
        if (original.getPai() != null) {
            assertNotNull(novo.getPai());
            assertEquals(original.getPai().getPathFull(), novo.getPai().getPathFull());
        } else {
            assertNull(novo.getPai());
        }
        if (original instanceof ICompositeInstance) {
            List<MInstancia> filhosOriginal = new ArrayList<>(((ICompositeInstance) original).getChildren());
            List<MInstancia> filhosNovo = new ArrayList<>(((ICompositeInstance) novo).getChildren());
            assertEquals(filhosOriginal.size(), filhosNovo.size());
            for (int i = 0; i < filhosOriginal.size(); i++) {
                assertEquivalent(filhosOriginal.get(0), filhosNovo.get(0));
            }
        } else {
            assertEquals(original.getValor(), novo.getValor());
        }
    }
}
