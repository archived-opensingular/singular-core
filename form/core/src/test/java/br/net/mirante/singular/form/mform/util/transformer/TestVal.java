package br.net.mirante.singular.form.mform.util.transformer;

import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SList;
import br.net.mirante.singular.form.mform.SISimple;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeLista;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.mform.PacoteBuilder;
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
    private STypeLista<STypeComposite<SIComposite>, SIComposite> _alertas;
    private STypeComposite<SIComposite> _alerta;
    private STypeData _alerta_data;
    private SIComposite evento;
    private SList<SIComposite> alertas;
    private Map<String, Object> valorEsperado = new LinkedHashMap<>();
    private SList listaAlertas;
    private SIComposite alerta3;
    private SIComposite alertaVazio;
    private SIComposite alerta2;
    private SIComposite alerta1;
    private SISimple dataVazia;
    private SISimple data3;

    @Before
    public void setup() {
        _dicionario = SDictionary.create();
        PacoteBuilder pb = _dicionario.criarNovoPacote("teste");

        _raiz = pb.createTipoComposto("_raiz");
        _descricao = _raiz.addCampo("descricao", STypeString.class);
        _periodo = _raiz.addCampoComposto("periodo");
        _dataInicial = _periodo.addCampo("dataInicial", STypeData.class);
        _dataFinal = _periodo.addCampo("dataFinal", STypeData.class);
        _alertas = _periodo.addCampoListaOfComposto("alertas", "alerta");
        _alerta = _alertas.getTipoElementos();
        _alerta_data = _alerta.addCampo("data", STypeData.class);

        _raiz.asAtrBasic().label("Evento");
        _descricao.asAtrBasic().label("Descrição");
        // *** período não possui label
        _dataInicial.asAtrBasic().label("Data inicial");
        _dataFinal.asAtrBasic().label("Data final");
        _alertas.asAtrBasic().label("Alertas");
        _alerta.asAtrBasic().label("Alerta");
        _alerta_data.asAtrBasic().label("Data");

        evento = _raiz.novaInstancia();


        // descricao
        evento.setValor(_descricao, DESCRICAO);
        valorEsperado.put(_descricao.getNomeSimples(), DESCRICAO);
        // perido
        SIComposite periodo = (SIComposite) evento.getCampo(_periodo.getNomeSimples());
        valorEsperado.put(_periodo.getNomeSimples(), new LinkedHashMap<String, Object>());
        //dt inicial
        periodo.setValor(_dataInicial, DT_INICIAL);
        ((Map<String, Object>) valorEsperado.get(_periodo.getNomeSimples())).put(_dataInicial.getNomeSimples(), DT_INICIAL);
        //dt final
        periodo.setValor(_dataFinal, DT_FINAL);
        ((Map<String, Object>) valorEsperado.get(_periodo.getNomeSimples())).put(_dataFinal.getNomeSimples(), DT_FINAL);
        //alertas
        listaAlertas = (SList) periodo.getCampo(_alertas.getNomeSimples());
        ((Map<String, Object>) valorEsperado.get(_periodo.getNomeSimples())).put(_alertas.getNomeSimples(), new ArrayList<Map<String, Date>>());

        //Alerta Data 1
        alerta1 = _alerta.novaInstancia();
        alerta1.getCampo(_alerta_data.getNomeSimples()).setValor(DT_1);
        listaAlertas.addElement(alerta1);
        Map<String, Date> alertaMap1 = new LinkedHashMap<>();
        alertaMap1.put(_alerta_data.getNomeSimples(), DT_1);
        ((List<Map<String, Date>>) ((Map<String, Object>) valorEsperado.get(_periodo.getNomeSimples())).get(_alertas.getNomeSimples())).add(alertaMap1);

        //Alerta Data 2
        alerta2 = _alerta.novaInstancia();
        alerta2.getCampo(_alerta_data.getNomeSimples()).setValor(DT_2);
        listaAlertas.addElement(alerta2);
        Map<String, Date> alertaMap2 = new LinkedHashMap<>();
        alertaMap2.put(_alerta_data.getNomeSimples(), DT_2);
        ((List<Map<String, Date>>) ((Map<String, Object>) valorEsperado.get(_periodo.getNomeSimples())).get(_alertas.getNomeSimples())).add(alertaMap2);

        //Alerta Data 3
        alerta3 = _alerta.novaInstancia();
        data3 = (SISimple) alerta3.getCampo(_alerta_data.getNomeSimples());
        data3.setValor(DT_3);
        listaAlertas.addElement(alerta3);
        Map<String, Date> alertaMap3 = new LinkedHashMap<>();
        alertaMap3.put(_alerta_data.getNomeSimples(), DT_3);
        ((List<Map<String, Date>>) ((Map<String, Object>) valorEsperado.get(_periodo.getNomeSimples())).get(_alertas.getNomeSimples())).add(alertaMap3);

        //Alerta Vazio
        alertaVazio = _alerta.novaInstancia();
        dataVazia = (SISimple) alertaVazio.getCampo(_alerta_data.getNomeSimples());
        dataVazia.setValor(null);

        listaAlertas.addElement(alertaVazio);
        Map<String, Date> alertaVazioMap = new LinkedHashMap<>();
        alertaVazioMap.put(_alerta_data.getNomeSimples(), null);
        ((List<Map<String, Date>>) ((Map<String, Object>) valorEsperado.get(_periodo.getNomeSimples())).get(_alertas.getNomeSimples())).add(alertaVazioMap);

    }

    @Test
    public void testDehydrate() {
        Assert.assertEquals(valorEsperado, Value.dehydrate(evento));
    }

    @Test
    public void testHydrate() {
        SIComposite novaInstancia = _raiz.novaInstancia();
        Value.hydrate(novaInstancia, valorEsperado);
        Assert.assertEquals(evento, novaInstancia);
    }

    @Test
    public void testHydrateDehydrate() {
        SIComposite novaInstancia = _raiz.novaInstancia();
        Value.hydrate(novaInstancia, valorEsperado);
        Assert.assertEquals(valorEsperado, Value.dehydrate(novaInstancia));
    }


    @Test
    public void testDehydrateHydrate() {
        Object value = Value.dehydrate(evento);
        SInstance novaInstancia = _raiz.novaInstancia();
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
        Value.of((SISimple)null);
        Value.of(null, null);
        Value.notNull((SList) null);
        Value.notNull((SInstance) null, (STypeSimple) null);
        Value.notNull((SInstance) null, (STypeComposite) null);
        Value.notNull((SInstance) null, (STypeLista) null);
        Value.dehydrate((SIComposite)null);
        Value.hydrate((SIComposite)null, null);
    }

}
