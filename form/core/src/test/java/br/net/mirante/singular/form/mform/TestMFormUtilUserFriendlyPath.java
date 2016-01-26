package br.net.mirante.singular.form.mform;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.net.mirante.singular.form.mform.core.MTipoData;
import br.net.mirante.singular.form.mform.core.MTipoString;

public class TestMFormUtilUserFriendlyPath {

    private MDicionario _dicionario;

    private MTipoComposto<? extends MIComposto>               _evento;
    private MTipoString                                       _descricao;
    private MTipoComposto<MIComposto>                         _periodo;
    private MTipoData                                         _dataInicial;
    private MTipoData                                         _dataFinal;
    private MTipoLista<MTipoComposto<MIComposto>, MIComposto> _alertas;
    private MTipoComposto<MIComposto>                         _alerta;
    private MTipoData                                         _alerta_data;

    private MIComposto evento;

    private MILista<MIComposto> alertas;

    @Before
    public void setUp() {
        _dicionario = MDicionario.create();
        PacoteBuilder pb = _dicionario.criarNovoPacote("teste");

        _evento = pb.createTipoComposto("evento");
        _descricao = _evento.addCampo("descricao", MTipoString.class);
        _periodo = _evento.addCampoComposto("periodo");
        _dataInicial = _periodo.addCampo("dataInicia", MTipoData.class);
        _dataFinal = _periodo.addCampo("dataFinal", MTipoData.class);
        _alertas = _periodo.addCampoListaOfComposto("alertas", "alerta");
        _alerta = _alertas.getTipoElementos();
        _alerta_data = _alerta.addCampo("data", MTipoData.class);

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
