package br.net.mirante.singular.form.mform;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.net.mirante.singular.form.mform.core.STypeData;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.options.MOptionsCompositeProvider;
import br.net.mirante.singular.form.mform.util.transformer.Value;

public class TestMoptionsConfigTipoComposto {


    private static final Date DT_INICIAL = new Date();
    private static final Date DT_FINAL = new Date();
    private static final Date DT_1 = new Date();
    private static final Date DT_2 = new Date();
    private static final String label1 = DT_1 + " - " + DT_2;
    private static final Date DT_3 = new Date();
    private static final Date DT_4 = new Date();
    private static final String label2 = DT_3 + " - " + DT_4;

    private SDictionary _dicionario;
    private STypeComposite<? extends SIComposite> _raiz;
    private STypeComposite<SIComposite> _periodo;
    private STypeData _dataInicial;
    private STypeData _dataFinal;
    private SIComposite evento;
    private SIComposite periodo;
    private SInstance opcaoPeriodo;


    @Before
    public void setup() {
        _dicionario = SDictionary.create();
        PackageBuilder pb = _dicionario.createNewPackage("teste");

        _raiz = pb.createTipoComposto("_raiz");
        _periodo = _raiz.addCampoComposto("periodo");
        STypeString descricao  = _periodo.addCampoString("descricao");
        _dataInicial = _periodo.addCampo("dataInicial", STypeData.class);
        _dataFinal = _periodo.addCampo("dataFinal", STypeData.class);


        _raiz.asAtrBasic().label("Evento");
        // *** período não possui label
        _dataInicial.asAtrBasic().label("Data inicial");
        _dataFinal.asAtrBasic().label("Data final");


        _periodo.withSelectionFromProvider(descricao, (MOptionsCompositeProvider)(instancia, lb) -> {
            lb
                    .add()
                    .set(descricao, label1)
                    .set(_dataInicial, DT_1)
                    .set(_dataFinal, DT_2)
                    .add()
                    .set(descricao, label2)
                    .set(_dataInicial, DT_3)
                    .set(_dataFinal, DT_4);
        });


        evento = _raiz.novaInstancia();

        // perido
        periodo = (SIComposite) evento.getCampo(_periodo.getSimpleName());
        opcaoPeriodo = _periodo.getProviderOpcoes().listAvailableOptions(periodo).get(0);
        Value.hydrate(periodo, Value.dehydrate(opcaoPeriodo));
    }


    @Test
    public void testValueFromKey() {
        String keyFromOption = periodo.getOptionsConfig().getKeyFromOptions(opcaoPeriodo);
        Assert.assertNotNull(keyFromOption);
        Assert.assertEquals(periodo, opcaoPeriodo.getOptionsConfig().getValueFromKey(keyFromOption));
        Assert.assertEquals(opcaoPeriodo.getValue(), periodo.getValue());
    }

    @Test
    public void testeLabelFromKey() {
        String keyFromOption = periodo.getOptionsConfig().getKeyFromOptions(opcaoPeriodo);
        String label = periodo.getOptionsConfig().getLabelFromKey(keyFromOption);
        Assert.assertEquals(opcaoPeriodo.getSelectLabel(), label);
    }

    @Test
    public void testMTipoOpcoes(){
        for(SInstance instancia : _periodo.getProviderOpcoes().listAvailableOptions(periodo)){
            Assert.assertEquals(_periodo, instancia.getType());
        }
    }

    @Test
    public void testKeyValueMapping(){
        for(SInstance instancia : _periodo.getProviderOpcoes().listAvailableOptions(periodo)){
            String key = periodo.getOptionsConfig().getKeyFromOptions(instancia);
            Assert.assertEquals(instancia, periodo.getOptionsConfig().getValueFromKey(key));
            Assert.assertEquals(periodo.getOptionsConfig().getLabelFromKey(key), instancia.getSelectLabel());
        }
    }

    @Test
    public void testSelectLabel() {
        SList lista = _periodo.getProviderOpcoes().listAvailableOptions(periodo);
        SInstance instancia1 = lista.get(0);
        Assert.assertEquals(label1, instancia1.getSelectLabel());
        SInstance instancia2 = lista.get(0);
        Assert.assertEquals(label2, instancia2.getSelectLabel());

        Assert.assertNotNull(_periodo.getSelectLabel());
    }
}
