package br.net.mirante.singular.form.mform;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.net.mirante.singular.form.mform.core.STypeString;

public class TestMoptionsConfigTipoSimples {


    private SDictionary _dicionario;
    private STypeComposite<? extends SIComposite> _raiz;
    private STypeString _descricao;
    private SIComposite evento;
    private SISimple descricao;
    private SInstance opcaoDescricao;

    @Before
    public void setup() {
        _dicionario = SDictionary.create();
        PackageBuilder pb = _dicionario.createNewPackage("teste");

        _raiz = pb.createTipoComposto("_raiz");
        _descricao = _raiz.addCampo("descricao", STypeString.class);
        _descricao.withSelection()
                .add("super", "Super")
                .add("mega", "Mega")
                .add("master", "Master")
                .add("blaster", "Blaster");

        _raiz.asAtrBasic().label("Evento");
        _descricao.asAtrBasic().label("Descrição");


        evento = _raiz.novaInstancia();


        // descricao
        descricao = (SISimple) evento.getCampo(_descricao.getSimpleName());
        opcaoDescricao = _descricao.getProviderOpcoes().listAvailableOptions(descricao).get(0);
        descricao.setValue(opcaoDescricao.getValue());


    }


    @Test
    public void testValueFromKey() {
        String keyFromOption = descricao.getOptionsConfig().getKeyFromOptions(opcaoDescricao);
        Assert.assertNotNull(keyFromOption);
        Assert.assertEquals(descricao, descricao.getOptionsConfig().getValueFromKey(keyFromOption));
        Assert.assertEquals(opcaoDescricao.getValue(), descricao.getValue());
    }

    @Test
    public void testeLabelFromKey() {
        String keyFromOption = descricao.getOptionsConfig().getKeyFromOptions(opcaoDescricao);
        String label = descricao.getOptionsConfig().getLabelFromKey(keyFromOption);
        Assert.assertEquals(opcaoDescricao.getSelectLabel(), label);
    }

    @Test
    public void testMTipoOpcoes() {
        for (SInstance instancia : _descricao.getProviderOpcoes().listAvailableOptions(descricao)) {
            Assert.assertEquals(_descricao, instancia.getType());
        }
    }

    @Test
    public void testKeyValueMapping() {
        for (SInstance instancia : _descricao.getProviderOpcoes().listAvailableOptions(descricao)) {
            String key = descricao.getOptionsConfig().getKeyFromOptions(instancia);
            Assert.assertEquals(instancia, descricao.getOptionsConfig().getValueFromKey(key));
            Assert.assertEquals(descricao.getOptionsConfig().getLabelFromKey(key), instancia.getSelectLabel());
        }
    }

    @Test
    public void testSelectLabel() {
        for (SInstance instancia : _descricao.getProviderOpcoes().listAvailableOptions(descricao)) {
            Assert.assertEquals(StringUtils.capitalize((String) instancia.getValue()), instancia.getSelectLabel());
        }
        Assert.assertNull(_descricao.getSelectLabel());
    }
}