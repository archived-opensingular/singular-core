package br.net.mirante.singular.form.mform.util.transformer;

import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MISimples;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoLista;
import br.net.mirante.singular.form.mform.MTipoSimples;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.core.MTipoData;
import br.net.mirante.singular.form.mform.core.MTipoString;
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
    private MDicionario _dicionario;
    private MTipoComposto<? extends MIComposto> _raiz;
    private MTipoString _descricao;
    private MTipoComposto<MIComposto> _periodo;
    private MTipoData _dataInicial;
    private MTipoData _dataFinal;
    private MTipoLista<MTipoComposto<MIComposto>, MIComposto> _alertas;
    private MTipoComposto<MIComposto> _alerta;
    private MTipoData _alerta_data;
    private MIComposto evento;
    private MILista<MIComposto> alertas;
    private Map<String, Object> valorEsperado = new LinkedHashMap<>();
    private MILista listaAlertas;
    private MIComposto alerta3;
    private MIComposto alertaVazio;
    private MIComposto alerta2;
    private MIComposto alerta1;
    private MISimples dataVazia;
    private MISimples data3;

    @Before
    public void setup() {
        _dicionario = MDicionario.create();
        PacoteBuilder pb = _dicionario.criarNovoPacote("teste");

        _raiz = pb.createTipoComposto("_raiz");
        _descricao = _raiz.addCampo("descricao", MTipoString.class);
        _periodo = _raiz.addCampoComposto("periodo");
        _dataInicial = _periodo.addCampo("dataInicial", MTipoData.class);
        _dataFinal = _periodo.addCampo("dataFinal", MTipoData.class);
        _alertas = _periodo.addCampoListaOfComposto("alertas", "alerta");
        _alerta = _alertas.getTipoElementos();
        _alerta_data = _alerta.addCampo("data", MTipoData.class);

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
        MIComposto periodo = (MIComposto) evento.getCampo(_periodo.getNomeSimples());
        valorEsperado.put(_periodo.getNomeSimples(), new LinkedHashMap<String, Object>());
        //dt inicial
        periodo.setValor(_dataInicial, DT_INICIAL);
        ((Map<String, Object>) valorEsperado.get(_periodo.getNomeSimples())).put(_dataInicial.getNomeSimples(), DT_INICIAL);
        //dt final
        periodo.setValor(_dataFinal, DT_FINAL);
        ((Map<String, Object>) valorEsperado.get(_periodo.getNomeSimples())).put(_dataFinal.getNomeSimples(), DT_FINAL);
        //alertas
        listaAlertas = (MILista) periodo.getCampo(_alertas.getNomeSimples());
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
        data3 = (MISimples) alerta3.getCampo(_alerta_data.getNomeSimples());
        data3.setValor(DT_3);
        listaAlertas.addElement(alerta3);
        Map<String, Date> alertaMap3 = new LinkedHashMap<>();
        alertaMap3.put(_alerta_data.getNomeSimples(), DT_3);
        ((List<Map<String, Date>>) ((Map<String, Object>) valorEsperado.get(_periodo.getNomeSimples())).get(_alertas.getNomeSimples())).add(alertaMap3);

        //Alerta Vazio
        alertaVazio = _alerta.novaInstancia();
        dataVazia = (MISimples) alertaVazio.getCampo(_alerta_data.getNomeSimples());
        dataVazia.setValor(null);

        listaAlertas.addElement(alertaVazio);
        Map<String, Date> alertaVazioMap = new LinkedHashMap<>();
        alertaVazioMap.put(_alerta_data.getNomeSimples(), null);
        ((List<Map<String, Date>>) ((Map<String, Object>) valorEsperado.get(_periodo.getNomeSimples())).get(_alertas.getNomeSimples())).add(alertaVazioMap);

    }

    @Test
    public void testDehydrate() {
        Assert.assertEquals(valorEsperado, Val.dehydrate(evento));
    }

    @Test
    public void testHydrate() {
        MIComposto novaInstancia = _raiz.novaInstancia();
        Val.hydrate(novaInstancia, valorEsperado);
        Assert.assertEquals(evento, novaInstancia);
    }

    @Test
    public void testHydrateDehydrate() {
        MIComposto novaInstancia = _raiz.novaInstancia();
        Val.hydrate(novaInstancia, valorEsperado);
        Assert.assertEquals(valorEsperado, Val.dehydrate(novaInstancia));
    }


    @Test
    public void testDehydrateHydrate() {
        Object value = Val.dehydrate(evento);
        MInstancia novaInstancia = _raiz.novaInstancia();
        Val.hydrate(novaInstancia, valorEsperado);
        Assert.assertEquals(evento, novaInstancia);
    }

    @Test
    public void testNoNull() {
        Assert.assertTrue(Val.notNull(evento));
        Assert.assertTrue(Val.notNull(evento, _alerta_data));
        Assert.assertTrue(Val.notNull(evento, _alerta));
        Assert.assertTrue(Val.notNull(listaAlertas));
    }

    @Test
    public void testValOf() {
        Assert.assertEquals(DT_1, Val.of(alerta1, _alerta_data));
        Assert.assertEquals(DT_2, Val.of(alerta2, _alerta_data));
        Assert.assertEquals(DT_3, Val.of(alerta3, _alerta_data));
        Assert.assertEquals(null, Val.of(alertaVazio, _alerta_data));
        Assert.assertEquals((Date) null, Val.of(dataVazia));
        Assert.assertEquals(DT_3, Val.of(data3));
    }

    @Test
    public void testNullSafe() {
        Val.of((MISimples)null);
        Val.of(null, null);
        Val.notNull((MILista) null);
        Val.notNull((MInstancia) null, (MTipoSimples) null);
        Val.notNull((MInstancia) null, (MTipoComposto) null);
        Val.notNull((MInstancia) null, (MTipoLista) null);
        Val.dehydrate((MIComposto)null);
        Val.hydrate((MIComposto)null, null);
    }

}
