package br.net.mirante.singular.form.mform;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.net.mirante.singular.form.mform.core.STypeDate;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.options.SOptionsCompositeProvider;
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
    private STypeDate _dataInicial;
    private STypeDate _dataFinal;
    private SIComposite evento;
    private SIComposite periodo;
    private SInstance opcaoPeriodo;


    @Before
    public void setup() {
        _dicionario = SDictionary.create();
        PackageBuilder pb = _dicionario.createNewPackage("teste");

        _raiz = pb.createCompositeType("_raiz");
        _periodo = _raiz.addFieldComposite("periodo");
        STypeString descricao  = _periodo.addFieldString("descricao");
        _dataInicial = _periodo.addField("dataInicial", STypeDate.class);
        _dataFinal = _periodo.addField("dataFinal", STypeDate.class);


        _raiz.asAtrBasic().label("Evento");
        // *** período não possui label
        _dataInicial.asAtrBasic().label("Data inicial");
        _dataFinal.asAtrBasic().label("Data final");


        _periodo.withSelectionFromProvider(descricao, (SOptionsCompositeProvider)(instancia, lb) -> {
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


        evento = _raiz.newInstance();

        // perido
        periodo = (SIComposite) evento.getField(_periodo.getNameSimple());
        opcaoPeriodo = _periodo.getOptionsProvider().listAvailableOptions(periodo).get(0);
        Value.hydrate(periodo, Value.dehydrate(opcaoPeriodo));
    }


    @Test
    public void testValueFromKey() {
        String keyFromOption = periodo.getOptionsConfig().getKeyFromOption(opcaoPeriodo);
        Assert.assertNotNull(keyFromOption);
        Assert.assertEquals(periodo, opcaoPeriodo.getOptionsConfig().getValueFromKey(keyFromOption));
        Assert.assertEquals(opcaoPeriodo.getValue(), periodo.getValue());
    }

    @Test
    public void testeLabelFromKey() {
        String keyFromOption = periodo.getOptionsConfig().getKeyFromOption(opcaoPeriodo);
        String label = periodo.getOptionsConfig().getLabelFromKey(keyFromOption);
        Assert.assertEquals(opcaoPeriodo.getSelectLabel(), label);
    }

    @Test
    public void testMTipoOpcoes(){
        for(SInstance instancia : _periodo.getOptionsProvider().listAvailableOptions(periodo)){
            Assert.assertEquals(_periodo, instancia.getType());
        }
    }

    @Test
    public void testKeyValueMapping(){
        for(SInstance instancia : _periodo.getOptionsProvider().listAvailableOptions(periodo)){
            String key = periodo.getOptionsConfig().getKeyFromOption(instancia);
            Assert.assertEquals(instancia, periodo.getOptionsConfig().getValueFromKey(key));
            Assert.assertEquals(periodo.getOptionsConfig().getLabelFromKey(key), instancia.getSelectLabel());
        }
    }

    @Test
    public void testSelectLabel() {
        SIList lista = _periodo.getOptionsProvider().listAvailableOptions(periodo);
        SInstance instancia1 = lista.get(0);
        Assert.assertEquals(label1, instancia1.getSelectLabel());
        SInstance instancia2 = lista.get(0);
        Assert.assertEquals(label2, instancia2.getSelectLabel());

        Assert.assertNotNull(_periodo.getSelectLabel());
    }
}
