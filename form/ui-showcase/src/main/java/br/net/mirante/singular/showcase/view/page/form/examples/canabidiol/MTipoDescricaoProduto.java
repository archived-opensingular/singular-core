package br.net.mirante.singular.showcase.view.page.form.examples.canabidiol;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.TipoBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoPorRadioView;
import br.net.mirante.singular.form.mform.core.MTipoInteger;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.util.transformer.Val;
import br.net.mirante.singular.form.wicket.AtrBootstrap;

import java.util.HashMap;
import java.util.Map;

@MInfoTipo(nome = "MTipoDescricaoProduto", pacote = MPacotePeticaoCanabidiol.class)
public class MTipoDescricaoProduto extends MTipoComposto<MIComposto> {


    private static Map<Integer, String> composicoes = new HashMap<>();
    private static Map<Integer, String> enderecos = new HashMap<>();

    static {
        composicoes.put(1, "Aguardando informação");
        composicoes.put(2, "Aguardando informação");
        composicoes.put(3, "Aguardando informação");
        composicoes.put(4, "Canabidiol - 8,33mg/mL");
        composicoes.put(5, "Canabidiol - 14 a 25%");
        composicoes.put(6, "Canabidiol - 14 a 25%");
        composicoes.put(7, "22:1 Canabidiol/THC");
    }

    static {
        enderecos.put(1, "Hempmeds 12255, Crosthwaite Circle - Poway, CA, 92064 (EUA)");
        enderecos.put(2, "Hempmeds 12255, Crosthwaite Circle - Poway, CA, 92064 (EUA)");
        enderecos.put(3, "Hempmeds 12255, Crosthwaite Circle - Poway, CA, 92064 (EUA)");
        enderecos.put(4, "580, Burbank St. Broomfield, CO 80020 (EUA)");
        enderecos.put(5, "Hempmeds 12255, Crosthwaite Circle - Poway, CA, 92064 (EUA)");
        enderecos.put(6, "Hempmeds 12255, Crosthwaite Circle - Poway, CA, 92064 (EUA)");
        enderecos.put(7, "2560, Paragon Dr. Colorado Springs, CO 80918 (EUA)");
    }

    private MTipoInteger nomeComercial;
    private MTipoString composicao;
    private MTipoString enderecoFabricante;
    private MTipoString descricaoQuantidade;

    @Override
    protected void onCargaTipo(TipoBuilder tb) {
        super.onCargaTipo(tb);

        nomeComercial = this.addCampoInteger("nomeComercial");

        nomeComercial
                .as(AtrBasic::new)
                .label("Nome Comercial");

        nomeComercial
                .withSelection()
                .add(1, "Cibdex Hemp CBD Complex 1oz (Gotas) – Fabricante: Cibdex Inc.")
                .add(2, "Cibdex Hemp CBD Complex 2oz (Gotas) – Fabricante: Cibdex Inc.")
                .add(3, "Cibdex Hemp CBD Complex (Cápsulas) – Fabricante: Cibdex Inc.")
                .add(4, "Hemp CBD Oil 2000mg Canabidiol - 240mL - Fabricante: Bluebird Botanicals")
                .add(5, "Real Scientific Hemp Oil (RSHO) CBD 14-25% - 3g (Pasta) – Fabricante: Hemp Meds Px")
                .add(6, "Real Scientific Hemp Oil (RSHO) CBD 14-25% - 10g (Pasta) – Fabricante: Hemp Meds Px")
                .add(7, "Revivid LLC Hemp Tinctute 500mg (22:1 CBD/THC) - 30mL (Gotas) – Fabricante: Revivid")
                .add(8, "Outro");

        nomeComercial
                .withView(new MSelecaoPorRadioView().layoutVertical());


        composicao = this.addCampoString("composicao");

        composicao
                .as(AtrBasic::new)
                .label("Composição")
                .visivel(false)
                .visivel(instancia -> {
                    boolean truth = Val.of(instancia, nomeComercial) != null && ((Integer) Val.of(instancia, nomeComercial)) < 8;
                    return truth;
                })
                .dependsOn(nomeComercial)
                .as(AtrBootstrap::new)
                .colPreference(6);

        composicao
                .withView(new MSelecaoPorRadioView().layoutVertical());

        composicao.withSelectionFromProvider(
                optionsInstance -> {
                    MILista<?> lista = composicao.novaLista();
                    String value = composicoes.get(Val.of(optionsInstance, nomeComercial));
                    if (value != null) {
                        lista.addValor(value);
                    }
                    return lista;
                }
        );


        enderecoFabricante = this.addCampoString("enderecoFabricante");

        enderecoFabricante
                .as(AtrBasic::new)
                .label("Endereço do fabricante")
                .visivel(false)
                .visivel(instancia -> Val.of(instancia, nomeComercial) != null && ((Integer) Val.of(instancia, nomeComercial)) < 8)
                .dependsOn(nomeComercial)
                .as(AtrBootstrap::new)
                .colPreference(6);

        enderecoFabricante
                .withView(new MSelecaoPorRadioView().layoutVertical());

        enderecoFabricante.withSelectionFromProvider(
                optionsInstance -> {
                    MILista<?> lista = enderecoFabricante.novaLista();
                    String value = enderecos.get(Val.of(optionsInstance, nomeComercial));
                    if (value != null) {
                        lista.addValor(value);
                    }
                    return lista;
                }
        );


        MTipoComposto<?> outroMedicamento = this.addCampoComposto("outro");
        outroMedicamento
                .as(AtrBasic::new)
                .label("Outro Medicamento")
                .dependsOn(nomeComercial)
                .visivel(false)
                .visivel(instancia -> Val.of(instancia, nomeComercial) != null && ((Integer) Val.of(instancia, nomeComercial)) == 8);

        outroMedicamento
                .addCampoString("outroNome")
                .as(AtrBasic::new)
                .label("Nome Comercial")
                .as(AtrBootstrap::new)
                .colPreference(6);

        outroMedicamento
                .addCampoString("outroComposicao")
                .as(AtrBasic::new)
                .label("Composição")
                .as(AtrBootstrap::new)
                .colPreference(6);

        outroMedicamento
                .addCampoString("outroEndereco")
                .as(AtrBasic::new)
                .label("Endereço do Fabricante")
                .as(AtrBootstrap::new)
                .colPreference(12);


        descricaoQuantidade = this.addCampoString("descricaoQuantidade");

        descricaoQuantidade
                .as(AtrBasic::new)
                .label("Quantidade solicitada a ser importada no período de 1 (um) ano")
                .subtitle("Informar as unidades do produto (ex: quantidade de frascos, tubos, caixas)");

    }

    public MTipoInteger getNomeComercial() {
        return nomeComercial;
    }

    public MTipoString getComposicao() {
        return composicao;
    }

    public MTipoString getEnderecoFabricante() {
        return enderecoFabricante;
    }

    public MTipoString getDescricaoQuantidade() {
        return descricaoQuantidade;
    }
}
