package br.net.mirante.singular.form.mform.util.transformer;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SISimple;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.mform.core.STypeData;
import br.net.mirante.singular.form.mform.core.STypeString;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TestVal {

    private static final String DESCRICAO = "Descrição Teste";
    private static final Date DT_INICIAL = new Date();
    private static final Date DT_FINAL = new Date();
    private static final Date DT_1 = new Date();
    private static final Date DT_2 = new Date();
    private static final Date DT_3 = new Date();
    private SDictionary _dicionario;
    private STypeComposite<? extends SIComposite> _raiz;
    private STypeString _descricao;
    private STypeComposite<SIComposite> _periodo;
    private STypeData _dataInicial;
    private STypeData _dataFinal;
    private STypeList<STypeComposite<SIComposite>, SIComposite> _alertas;
    private STypeComposite<SIComposite> _alerta;
    private STypeData _alerta_data;
    private SIComposite evento;
    private SIList<SIComposite> alertas;
    private Map<String, Object> valorEsperado = new LinkedHashMap<>();
    private SIList listaAlertas;
    private SIComposite alerta3;
    private SIComposite alertaVazio;
    private SIComposite alerta2;
    private SIComposite alerta1;
    private SISimple dataVazia;
    private SISimple data3;

    @Before
    public void setup() {
        _dicionario = SDictionary.create();
        PackageBuilder pb = _dicionario.createNewPackage("teste");

        _raiz = pb.createCompositeType("_raiz");
        _descricao = _raiz.addField("descricao", STypeString.class);
        _periodo = _raiz.addFieldComposite("periodo");
        _dataInicial = _periodo.addField("dataInicial", STypeData.class);
        _dataFinal = _periodo.addField("dataFinal", STypeData.class);
        _alertas = _periodo.addFieldListOfComposite("alertas", "alerta");
        _alerta = _alertas.getElementsType();
        _alerta_data = _alerta.addField("data", STypeData.class);

        _raiz.asAtrBasic().label("Evento");
        _descricao.asAtrBasic().label("Descrição");
        // *** período não possui label
        _dataInicial.asAtrBasic().label("Data inicial");
        _dataFinal.asAtrBasic().label("Data final");
        _alertas.asAtrBasic().label("Alertas");
        _alerta.asAtrBasic().label("Alerta");
        _alerta_data.asAtrBasic().label("Data");

        evento = _raiz.newInstance();


        // descricao
        evento.setValue(_descricao, DESCRICAO);
        valorEsperado.put(_descricao.getNameSimple(), DESCRICAO);
        // perido
        SIComposite periodo = (SIComposite) evento.getField(_periodo.getNameSimple());
        valorEsperado.put(_periodo.getNameSimple(), new LinkedHashMap<String, Object>());
        //dt inicial
        periodo.setValue(_dataInicial, DT_INICIAL);
        ((Map<String, Object>) valorEsperado.get(_periodo.getNameSimple())).put(_dataInicial.getNameSimple(), DT_INICIAL);
        //dt final
        periodo.setValue(_dataFinal, DT_FINAL);
        ((Map<String, Object>) valorEsperado.get(_periodo.getNameSimple())).put(_dataFinal.getNameSimple(), DT_FINAL);
        //alertas
        listaAlertas = (SIList) periodo.getField(_alertas.getNameSimple());
        ((Map<String, Object>) valorEsperado.get(_periodo.getNameSimple())).put(_alertas.getNameSimple(), new ArrayList<Map<String, Date>>());

        //Alerta Data 1
        alerta1 = _alerta.newInstance();
        alerta1.getField(_alerta_data.getNameSimple()).setValue(DT_1);
        listaAlertas.addElement(alerta1);
        Map<String, Date> alertaMap1 = new LinkedHashMap<>();
        alertaMap1.put(_alerta_data.getNameSimple(), DT_1);
        ((List<Map<String, Date>>) ((Map<String, Object>) valorEsperado.get(_periodo.getNameSimple())).get(_alertas.getNameSimple())).add(alertaMap1);

        //Alerta Data 2
        alerta2 = _alerta.newInstance();
        alerta2.getField(_alerta_data.getNameSimple()).setValue(DT_2);
        listaAlertas.addElement(alerta2);
        Map<String, Date> alertaMap2 = new LinkedHashMap<>();
        alertaMap2.put(_alerta_data.getNameSimple(), DT_2);
        ((List<Map<String, Date>>) ((Map<String, Object>) valorEsperado.get(_periodo.getNameSimple())).get(_alertas.getNameSimple())).add(alertaMap2);

        //Alerta Data 3
        alerta3 = _alerta.newInstance();
        data3 = (SISimple) alerta3.getField(_alerta_data.getNameSimple());
        data3.setValue(DT_3);
        listaAlertas.addElement(alerta3);
        Map<String, Date> alertaMap3 = new LinkedHashMap<>();
        alertaMap3.put(_alerta_data.getNameSimple(), DT_3);
        ((List<Map<String, Date>>) ((Map<String, Object>) valorEsperado.get(_periodo.getNameSimple())).get(_alertas.getNameSimple())).add(alertaMap3);

        //Alerta Vazio
        alertaVazio = _alerta.newInstance();
        dataVazia = (SISimple) alertaVazio.getField(_alerta_data.getNameSimple());
        dataVazia.setValue(null);

        listaAlertas.addElement(alertaVazio);
        Map<String, Date> alertaVazioMap = new LinkedHashMap<>();
        alertaVazioMap.put(_alerta_data.getNameSimple(), null);
        ((List<Map<String, Date>>) ((Map<String, Object>) valorEsperado.get(_periodo.getNameSimple())).get(_alertas.getNameSimple())).add(alertaVazioMap);

    }

    @Test
    public void testDehydrate() {
        Assert.assertEquals(valorEsperado, Value.dehydrate(evento));
    }

    @Test
    public void testHydrate() {
        SIComposite novaInstancia = _raiz.newInstance();
        Value.hydrate(novaInstancia, valorEsperado);
        Assert.assertEquals(evento, novaInstancia);
    }

    @Test
    public void testHydrateDehydrate() {
        SIComposite novaInstancia = _raiz.newInstance();
        Value.hydrate(novaInstancia, valorEsperado);
        Assert.assertEquals(valorEsperado, Value.dehydrate(novaInstancia));
    }


    @Test
    public void testDehydrateHydrate() {
        Object value = Value.dehydrate(evento);
        SInstance novaInstancia = _raiz.newInstance();
        Value.hydrate(novaInstancia, valorEsperado);
        Assert.assertEquals(evento, novaInstancia);
    }

    @Test
    public void testNoNull() {
        Assert.assertTrue(Value.notNull(evento));
        Assert.assertTrue(Value.notNull(evento, _alerta_data));
        Assert.assertTrue(Value.notNull(evento, _alerta));
        Assert.assertTrue(Value.notNull(listaAlertas));
    }

    @Test
    public void testValOf() {
        Assert.assertEquals(DT_1, Value.of(alerta1, _alerta_data));
        Assert.assertEquals(DT_2, Value.of(alerta2, _alerta_data));
        Assert.assertEquals(DT_3, Value.of(alerta3, _alerta_data));
        Assert.assertEquals(null, Value.of(alertaVazio, _alerta_data));
        Assert.assertEquals((Date) null, Value.of(dataVazia));
        Assert.assertEquals(DT_3, Value.of(data3));
    }

    @Test
    public void testNullSafe() {
        Value.of((SISimple) null);
        Value.of(null, (String) null);
        Value.of(null, (STypeSimple) null);
        Value.notNull((SIList) null);
        Value.notNull((SInstance) null, (STypeSimple) null);
        Value.notNull((SInstance) null, (STypeComposite) null);
        Value.notNull((SInstance) null, (STypeList) null);
        Value.dehydrate((SIComposite) null);
        Value.hydrate((SIComposite) null, null);
    }

}
