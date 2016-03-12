package br.net.mirante.singular.form.mform;

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
    private STypeList<STypeComposite<SIComposite>, SIComposite> _alertas;
    private STypeComposite<SIComposite> _alerta;
    private STypeData _alerta_data;

    private SIComposite evento;

    private SIList<SIComposite> alertas;

    @Before
    public void setUp() {
        _dicionario = SDictionary.create();
        PackageBuilder pb = _dicionario.createNewPackage("teste");

        _evento = pb.createCompositeType("evento");
        _descricao = _evento.addField("descricao", STypeString.class);
        _periodo = _evento.addFieldComposite("periodo");
        _dataInicial = _periodo.addField("dataInicia", STypeData.class);
        _dataFinal = _periodo.addField("dataFinal", STypeData.class);
        _alertas = _periodo.addFieldListOfComposite("alertas", "alerta");
        _alerta = _alertas.getElementsType();
        _alerta_data = _alerta.addField("data", STypeData.class);

        _evento.asAtrBasic().label("Evento");
        _descricao.asAtrBasic().label("Descrição");
        // *** período não possui label
        _dataInicial.asAtrBasic().label("Data inicial");
        _dataFinal.asAtrBasic().label("Data final");
        _alertas.asAtrBasic().label("Alertas");
        _alerta.asAtrBasic().label("Alerta");
        _alerta_data.asAtrBasic().label("Data");

        evento = _evento.newInstance();
        alertas = evento.findNearest(_alertas).get();
        alertas.addNew();
    }

    @Test
    public void testFindNearestFromSimpleTypeInstanceToSameInstance(){
        SInstance descricao = evento.findNearest(_descricao).get();
        Assert.assertEquals(descricao.findNearest(_descricao).get(), descricao);
    }

    @Test
    public void generateUserFriendlyPath_no_parentContext() {
        //@formatter:off
        Assert.assertEquals("Evento"                                , SFormUtil.generateUserFriendlyPath(evento));
        Assert.assertEquals("Evento > Descrição"                    , SFormUtil.generateUserFriendlyPath(evento.findNearest(_descricao  ).get()));
        Assert.assertEquals("Evento"                                , SFormUtil.generateUserFriendlyPath(evento.findNearest(_periodo    ).get()));
        Assert.assertEquals("Evento > Data inicial"                 , SFormUtil.generateUserFriendlyPath(evento.findNearest(_dataInicial).get()));
        Assert.assertEquals("Evento > Data final"                   , SFormUtil.generateUserFriendlyPath(evento.findNearest(_dataFinal  ).get()));
        Assert.assertEquals("Evento > Alertas"                      , SFormUtil.generateUserFriendlyPath(evento.findNearest(_alertas    ).get()));
        Assert.assertEquals("Evento > Alertas [1] > Alerta"         , SFormUtil.generateUserFriendlyPath(evento.findNearest(_alerta     ).get()));
        Assert.assertEquals("Evento > Alertas [1] > Alerta > Data"  , SFormUtil.generateUserFriendlyPath(evento.findNearest(_alerta_data).get()));
        //@formatter:on
    }

    @Test
    public void generateUserFriendlyPath_with_parentContext() {
        //@formatter:off
        Assert.assertNull  (                                      SFormUtil.generateUserFriendlyPath(evento, evento));
        Assert.assertEquals("Descrição"                         , SFormUtil.generateUserFriendlyPath(evento.findNearest(_descricao  ).get(), evento));
        Assert.assertNull  (                                      SFormUtil.generateUserFriendlyPath(evento.findNearest(_periodo    ).get(), evento));
        Assert.assertEquals("Data inicial"                      , SFormUtil.generateUserFriendlyPath(evento.findNearest(_dataInicial).get(), evento));
        Assert.assertEquals("Data final"                        , SFormUtil.generateUserFriendlyPath(evento.findNearest(_dataFinal  ).get(), evento));
        Assert.assertEquals("Alertas"                           , SFormUtil.generateUserFriendlyPath(evento.findNearest(_alertas    ).get(), evento));
        Assert.assertEquals("Alertas [1] > Alerta"              , SFormUtil.generateUserFriendlyPath(evento.findNearest(_alerta     ).get(), evento));
        Assert.assertEquals("Alertas [1] > Alerta > Data"       , SFormUtil.generateUserFriendlyPath(evento.findNearest(_alerta_data).get(), evento));
        
        Assert.assertEquals("Alerta"                            , SFormUtil.generateUserFriendlyPath(evento.findNearest(_alerta     ).get(), alertas));
        Assert.assertEquals("Alerta > Data"                     , SFormUtil.generateUserFriendlyPath(evento.findNearest(_alerta_data).get(), alertas));
        //@formatter:on
    }
}
