package br.net.mirante.singular.form.mform;

import br.net.mirante.singular.form.mform.core.SIString;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.net.mirante.singular.form.mform.core.STypeData;
import br.net.mirante.singular.form.mform.core.STypeString;

public class TestMFormUtilUserFriendlyPath {

    private SDictionary _dicionario;

    private STypeComposite<? extends SIComposite> _evento;
    private STypeString _descricao;
    private STypeComposite<SIComposite> _periodo;
    private STypeData _dataInicial;
    private STypeData _dataFinal;
    private STypeLista<STypeComposite<SIComposite>, SIComposite> _alertas;
    private STypeComposite<SIComposite> _alerta;
    private STypeData _alerta_data;

    private SIComposite evento;

    private SList<SIComposite> alertas;

    @Before
    public void setUp() {
        _dicionario = SDictionary.create();
        PackageBuilder pb = _dicionario.createNewPackage("teste");

        _evento = pb.createTipoComposto("evento");
        _descricao = _evento.addCampo("descricao", STypeString.class);
        _periodo = _evento.addCampoComposto("periodo");
        _dataInicial = _periodo.addCampo("dataInicia", STypeData.class);
        _dataFinal = _periodo.addCampo("dataFinal", STypeData.class);
        _alertas = _periodo.addCampoListaOfComposto("alertas", "alerta");
        _alerta = _alertas.getTipoElementos();
        _alerta_data = _alerta.addCampo("data", STypeData.class);

        _evento.asAtrBasic().label("Evento");
        _descricao.asAtrBasic().label("Descrição");
        // *** período não possui label
        _dataInicial.asAtrBasic().label("Data inicial");
        _dataFinal.asAtrBasic().label("Data final");
        _alertas.asAtrBasic().label("Alertas");
        _alerta.asAtrBasic().label("Alerta");
        _alerta_data.asAtrBasic().label("Data");

        evento = _evento.novaInstancia();
        alertas = evento.findNearest(_alertas).get();
        alertas.addNovo();
    }

    @Test
    public void testFindNearestFromSimpleTypeInstanceToSameInstance(){
        SInstance descricao = evento.findNearest(_descricao).get();
        Assert.assertEquals(descricao.findNearest(_descricao).get(), descricao);
    }

    @Test
    public void generateUserFriendlyPath_no_parentContext() {
        //@formatter:off
        Assert.assertEquals("Evento"                                , MFormUtil.generateUserFriendlyPath(evento));
        Assert.assertEquals("Evento > Descrição"                    , MFormUtil.generateUserFriendlyPath(evento.findNearest(_descricao  ).get()));
        Assert.assertEquals("Evento"                                , MFormUtil.generateUserFriendlyPath(evento.findNearest(_periodo    ).get()));
        Assert.assertEquals("Evento > Data inicial"                 , MFormUtil.generateUserFriendlyPath(evento.findNearest(_dataInicial).get()));
        Assert.assertEquals("Evento > Data final"                   , MFormUtil.generateUserFriendlyPath(evento.findNearest(_dataFinal  ).get()));
        Assert.assertEquals("Evento > Alertas"                      , MFormUtil.generateUserFriendlyPath(evento.findNearest(_alertas    ).get()));
        Assert.assertEquals("Evento > Alertas [1] > Alerta"         , MFormUtil.generateUserFriendlyPath(evento.findNearest(_alerta     ).get()));
        Assert.assertEquals("Evento > Alertas [1] > Alerta > Data"  , MFormUtil.generateUserFriendlyPath(evento.findNearest(_alerta_data).get()));
        //@formatter:on
    }

    @Test
    public void generateUserFriendlyPath_with_parentContext() {
        //@formatter:off
        Assert.assertNull  (                                      MFormUtil.generateUserFriendlyPath(evento, evento));
        Assert.assertEquals("Descrição"                         , MFormUtil.generateUserFriendlyPath(evento.findNearest(_descricao  ).get(), evento));
        Assert.assertNull  (                                      MFormUtil.generateUserFriendlyPath(evento.findNearest(_periodo    ).get(), evento));
        Assert.assertEquals("Data inicial"                      , MFormUtil.generateUserFriendlyPath(evento.findNearest(_dataInicial).get(), evento));
        Assert.assertEquals("Data final"                        , MFormUtil.generateUserFriendlyPath(evento.findNearest(_dataFinal  ).get(), evento));
        Assert.assertEquals("Alertas"                           , MFormUtil.generateUserFriendlyPath(evento.findNearest(_alertas    ).get(), evento));
        Assert.assertEquals("Alertas [1] > Alerta"              , MFormUtil.generateUserFriendlyPath(evento.findNearest(_alerta     ).get(), evento));
        Assert.assertEquals("Alertas [1] > Alerta > Data"       , MFormUtil.generateUserFriendlyPath(evento.findNearest(_alerta_data).get(), evento));
        
        Assert.assertEquals("Alerta"                            , MFormUtil.generateUserFriendlyPath(evento.findNearest(_alerta     ).get(), alertas));
        Assert.assertEquals("Alerta > Data"                     , MFormUtil.generateUserFriendlyPath(evento.findNearest(_alerta_data).get(), alertas));
        //@formatter:on
    }
}
