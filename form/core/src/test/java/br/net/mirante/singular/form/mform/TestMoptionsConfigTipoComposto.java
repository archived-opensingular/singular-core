package br.net.mirante.singular.form.mform;

import br.net.mirante.singular.form.mform.core.MTipoData;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.options.MOptionsCompositeProvider;
import br.net.mirante.singular.form.mform.util.transformer.Val;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

public class TestMoptionsConfigTipoComposto {


    private static final Date DT_INICIAL = new Date();
    private static final Date DT_FINAL = new Date();
    private static final Date DT_1 = new Date();
    private static final Date DT_2 = new Date();
    private static final String label1 = DT_1 + " - " + DT_2;
    private static final Date DT_3 = new Date();
    private static final Date DT_4 = new Date();
    private static final String label2 = DT_3 + " - " + DT_4;

    private MDicionario _dicionario;
    private MTipoComposto<? extends MIComposto> _raiz;
    private MTipoComposto<MIComposto> _periodo;
    private MTipoData _dataInicial;
    private MTipoData _dataFinal;
    private MIComposto evento;
    private MIComposto periodo;
    private MInstancia opcaoPeriodo;


    @Before
    public void setup() {
        _dicionario = MDicionario.create();
        PacoteBuilder pb = _dicionario.criarNovoPacote("teste");

        _raiz = pb.createTipoComposto("_raiz");
        _periodo = _raiz.addCampoComposto("periodo");
        MTipoString descricao  = _periodo.addCampoString("descricao");
        _dataInicial = _periodo.addCampo("dataInicial", MTipoData.class);
        _dataFinal = _periodo.addCampo("dataFinal", MTipoData.class);


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
        periodo = (MIComposto) evento.getCampo(_periodo.getNomeSimples());
        opcaoPeriodo = _periodo.getProviderOpcoes().listAvailableOptions(periodo).get(0);
        Val.hydrate(periodo, Val.dehydrate(opcaoPeriodo));
    }


    @Test
    public void testValueFromKey() {
        String keyFromOption = periodo.getOptionsConfig().getKeyFromOptions(opcaoPeriodo);
        Assert.assertNotNull(keyFromOption);
        Assert.assertEquals(periodo, opcaoPeriodo.getOptionsConfig().getValueFromKey(keyFromOption));
        Assert.assertEquals(opcaoPeriodo.getValor(), periodo.getValor());
    }

    @Test
    public void testeLabelFromKey() {
        String keyFromOption = periodo.getOptionsConfig().getKeyFromOptions(opcaoPeriodo);
        String label = periodo.getOptionsConfig().getLabelFromKey(keyFromOption);
        Assert.assertEquals(opcaoPeriodo.getSelectLabel(), label);
    }

    @Test
    public void testMTipoOpcoes(){
        for(MInstancia instancia : _periodo.getProviderOpcoes().listAvailableOptions(periodo)){
            Assert.assertEquals(_periodo, instancia.getMTipo());
        }
    }

    @Test
    public void testKeyValueMapping(){
        for(MInstancia instancia : _periodo.getProviderOpcoes().listAvailableOptions(periodo)){
            String key = periodo.getOptionsConfig().getKeyFromOptions(instancia);
            Assert.assertEquals(instancia, periodo.getOptionsConfig().getValueFromKey(key));
            Assert.assertEquals(periodo.getOptionsConfig().getLabelFromKey(key), instancia.getSelectLabel());
        }
    }

    @Test
    public void testSelectLabel() {
        MILista lista = _periodo.getProviderOpcoes().listAvailableOptions(periodo);
        MInstancia instancia1 = lista.get(0);
        Assert.assertEquals(label1, instancia1.getSelectLabel());
        MInstancia instancia2 = lista.get(0);
        Assert.assertEquals(label2, instancia2.getSelectLabel());

        Assert.assertNotNull(_periodo.getSelectLabel());
    }
}
