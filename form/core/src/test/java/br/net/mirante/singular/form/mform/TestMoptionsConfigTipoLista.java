package br.net.mirante.singular.form.mform;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.net.mirante.singular.form.mform.core.STypeDate;
import br.net.mirante.singular.form.mform.options.SOptionsCompositeProvider;
import br.net.mirante.singular.form.mform.util.transformer.SListBuilder;
import br.net.mirante.singular.form.mform.util.transformer.Value;

public class TestMoptionsConfigTipoLista {


    private static final Date DT_1 = new Date();
    private static final Date DT_2 = new Date();
    private static final Date DT_3 = new Date();
    private static final Date DT_4 = new Date();
    private static final Date DT_5 = new Date();
    private static final Date DT_6 = new Date();
    private SDictionary _dicionario;
    private STypeComposite<? extends SIComposite> _raiz;
    private STypeList<STypeComposite<SIComposite>, SIComposite> _alertas;
    private STypeComposite<SIComposite> _alerta;
    private STypeDate _alerta_data;
    private SIComposite evento;
    private SIList<SIComposite> alertas;
    private SIList listaAlertas;
    private SInstance opcaoAlerta1;
    private SInstance opcaoAlerta2;
    private SInstance opcaoAlerta3;


    @Before
    public void setup() {
        _dicionario = SDictionary.create();
        PackageBuilder pb = _dicionario.createNewPackage("teste");

        _raiz = pb.createCompositeType("_raiz");
        

        _alertas = _raiz.addFieldListOfComposite("alertas", "alerta");
        _alerta = _alertas.getElementsType();
        _alerta_data = _alerta.addField("data", STypeDate.class);

        _raiz.asAtrBasic().label("Evento");
        _alertas.asAtrBasic().label("Alertas");
        _alerta.asAtrBasic().label("Alerta");
        _alerta_data.asAtrBasic().label("Data");

        evento = _raiz.newInstance();

        //alertas
        listaAlertas = (SIList) evento.getField(_alertas.getNameSimple());

        _alerta.withSelectionFromProvider(_alerta_data, new SOptionsCompositeProvider() {
            @Override
            public void listOptions(SInstance instancia, SListBuilder<STypeComposite> lb) {
                lb
                        .add()
                        .set(_alerta_data, DT_1)
                        .add()
                        .set(_alerta_data, DT_2)
                        .add()
                        .set(_alerta_data, DT_3)
                        .add()
                        .set(_alerta_data, DT_4)
                        .add()
                        .set(_alerta_data, DT_5)
                        .add()
                        .set(_alerta_data, DT_6);

            }
        });

        SIList listaOpcoes = _alerta.getOptionsProvider().listAvailableOptions(listaAlertas);
        opcaoAlerta1 = listaOpcoes.get(0);
        opcaoAlerta2 = listaOpcoes.get(1);
        opcaoAlerta3 = listaOpcoes.get(2);
        SInstance m1 = listaAlertas.addNew();
        SInstance m2 = listaAlertas.addNew();
        SInstance m3 = listaAlertas.addNew();
        Value.hydrate(m1, Value.dehydrate(opcaoAlerta1));
        Value.hydrate(m2, Value.dehydrate(opcaoAlerta2));
        Value.hydrate(m3, Value.dehydrate(opcaoAlerta3));


    }


    @Test
    public void testValueFromKey() {
        String keyFromOption1 = listaAlertas.getOptionsConfig().getKeyFromOption(opcaoAlerta1);
        String keyFromOption2 = listaAlertas.getOptionsConfig().getKeyFromOption(opcaoAlerta2);
        String keyFromOption3 = listaAlertas.getOptionsConfig().getKeyFromOption(opcaoAlerta3);
        Assert.assertNotNull(keyFromOption1);
        Assert.assertNotNull(keyFromOption2);
        Assert.assertNotNull(keyFromOption3);
        Assert.assertEquals(opcaoAlerta1, listaAlertas.getOptionsConfig().getValueFromKey(keyFromOption1));
        Assert.assertEquals(opcaoAlerta2, listaAlertas.getOptionsConfig().getValueFromKey(keyFromOption2));
        Assert.assertEquals(opcaoAlerta3, listaAlertas.getOptionsConfig().getValueFromKey(keyFromOption3));
        Assert.assertEquals(listaAlertas.size(), 3);
        Assert.assertEquals(listaAlertas.get(0), listaAlertas.getOptionsConfig().getValueFromKey(keyFromOption1));
        Assert.assertEquals(listaAlertas.get(1), listaAlertas.getOptionsConfig().getValueFromKey(keyFromOption2));
        Assert.assertEquals(listaAlertas.get(2), listaAlertas.getOptionsConfig().getValueFromKey(keyFromOption3));
    }

    @Test
    public void testeLabelFromKey() {
        String keyFromOption1 = listaAlertas.getOptionsConfig().getKeyFromOption(opcaoAlerta1);
        String keyFromOption2 = listaAlertas.getOptionsConfig().getKeyFromOption(opcaoAlerta2);
        String keyFromOption3 = listaAlertas.getOptionsConfig().getKeyFromOption(opcaoAlerta3);
        String label1 = listaAlertas.getOptionsConfig().getLabelFromKey(keyFromOption1);
        String label2 = listaAlertas.getOptionsConfig().getLabelFromKey(keyFromOption2);
        String label3 = listaAlertas.getOptionsConfig().getLabelFromKey(keyFromOption3);
        Assert.assertEquals(opcaoAlerta1.getSelectLabel(), label1);
        Assert.assertEquals(opcaoAlerta2.getSelectLabel(), label2);
        Assert.assertEquals(opcaoAlerta3.getSelectLabel(), label3);
    }

    @Test
    public void testMTipoOpcoes(){
        for(SInstance instancia : _alerta.getOptionsProvider().listAvailableOptions(listaAlertas)){
            Assert.assertEquals(_alerta, instancia.getType());
        }
    }

    @Test
    public void testKeyValueMapping(){
        for(SInstance instancia : _alerta.getOptionsProvider().listAvailableOptions(listaAlertas)){
            String key = listaAlertas.getOptionsConfig().getKeyFromOption(instancia);
            Assert.assertEquals(instancia, listaAlertas.getOptionsConfig().getValueFromKey(key));
            Assert.assertEquals(listaAlertas.getOptionsConfig().getLabelFromKey(key), instancia.getSelectLabel());
        }
    }

    @Test
    public void testSelectLabel() {
        for(SInstance instancia : _alerta.getOptionsProvider().listAvailableOptions(listaAlertas)){
            Assert.assertEquals(String.valueOf(Value.of(instancia, _alerta_data)), instancia.getSelectLabel());
        }
        Assert.assertEquals(_alerta_data.getNameSimple(), _alerta.getSelectLabel());
    }
}
