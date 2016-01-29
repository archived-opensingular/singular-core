package br.net.mirante.singular.showcase.view.page.form.examples.canabidiol;

import java.util.HashMap;
import java.util.Map;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SList;
import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.TipoBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.AtrBootstrap;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoPorRadioView;
import br.net.mirante.singular.form.mform.core.AtrCore;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.util.transformer.Value;

@MInfoTipo(nome = "MTipoDescricaoProduto", pacote = SPackagePeticaoCanabidiol.class)
public class STypeDescricaoProduto extends STypeComposite<SIComposite> {


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

    private STypeInteger nomeComercial;
    private STypeString composicao;
    private STypeString enderecoFabricante;
    private STypeString descricaoQuantidade;

    @Override
    protected void onLoadType(TipoBuilder tb) {
        super.onLoadType(tb);

        nomeComercial = this.addCampoInteger("nomeComercial");

        nomeComercial
                .as(AtrCore::new)
                .obrigatorio()
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
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
                .label("Composição")
                .visivel(false)
                .visivel(instancia -> {
                    boolean truth = Value.of(instancia, nomeComercial) != null && ((Integer) Value.of(instancia, nomeComercial)) < 8;
                    return truth;
                })
                .dependsOn(nomeComercial)
                .as(AtrBootstrap::new)
                .colPreference(6);

        composicao
                .withView(new MSelecaoPorRadioView().layoutVertical());

        composicao.withSelectionFromProvider(
                optionsInstance -> {
                    SList<?> lista = composicao.novaLista();
                    String value = composicoes.get(Value.of(optionsInstance, nomeComercial));
                    if (value != null) {
                        lista.addValor(value);
                    }
                    return lista;
                }
        );


        enderecoFabricante = this.addCampoString("enderecoFabricante");

        enderecoFabricante
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
                .label("Endereço do fabricante")
                .visivel(false)
                .visivel(instancia -> Value.of(instancia, nomeComercial) != null && ((Integer) Value.of(instancia, nomeComercial)) < 8)
                .dependsOn(nomeComercial)
                .as(AtrBootstrap::new)
                .colPreference(6);

        enderecoFabricante
                .withView(new MSelecaoPorRadioView().layoutVertical());

        enderecoFabricante.withSelectionFromProvider(
                optionsInstance -> {
                    SList<?> lista = enderecoFabricante.novaLista();
                    String value = enderecos.get(Value.of(optionsInstance, nomeComercial));
                    if (value != null) {
                        lista.addValor(value);
                    }
                    return lista;
                }
        );


        STypeComposite<?> outroMedicamento = this.addCampoComposto("outro");
        outroMedicamento
                .as(AtrCore::new)
                .as(AtrBasic::new)
                .label("Outro Medicamento")
                .dependsOn(nomeComercial)
                .visivel(false)
                .visivel(instancia -> Value.of(instancia, nomeComercial) != null && ((Integer) Value.of(instancia, nomeComercial)) == 8);

        outroMedicamento
                .addCampoString("outroNome")
                .as(AtrCore::new)
                .obrigatorio(instancia -> Value.of(instancia, nomeComercial) != null && ((Integer) Value.of(instancia, nomeComercial)) == 8)
                .as(AtrBasic::new)
                .label("Nome Comercial")
                .as(AtrBootstrap::new)
                .colPreference(6);

        outroMedicamento
                .addCampoString("outroComposicao")
                .as(AtrCore::new)
                .obrigatorio(instancia -> Value.of(instancia, nomeComercial) != null && ((Integer) Value.of(instancia, nomeComercial)) == 8)
                .as(AtrBasic::new)
                .label("Composição")
                .as(AtrBootstrap::new)
                .colPreference(6);

        outroMedicamento
                .addCampoString("outroEndereco")
                .as(AtrCore::new)
                .obrigatorio(instancia -> Value.of(instancia, nomeComercial) != null && ((Integer) Value.of(instancia, nomeComercial)) == 8)
                .as(AtrBasic::new)
                .label("Endereço do Fabricante")
                .as(AtrBootstrap::new)
                .colPreference(12);


        descricaoQuantidade = this.addCampoString("descricaoQuantidade");

        descricaoQuantidade
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
                .label("Quantidade solicitada a ser importada no período de 1 (um) ano")
                .subtitle("Informar as unidades do produto (ex: quantidade de frascos, tubos, caixas)");

    }

    public STypeInteger getNomeComercial() {
        return nomeComercial;
    }

    public STypeString getComposicao() {
        return composicao;
    }

    public STypeString getEnderecoFabricante() {
        return enderecoFabricante;
    }

    public STypeString getDescricaoQuantidade() {
        return descricaoQuantidade;
    }
}
