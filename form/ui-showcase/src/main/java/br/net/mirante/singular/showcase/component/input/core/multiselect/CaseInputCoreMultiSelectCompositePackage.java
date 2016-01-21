package br.net.mirante.singular.showcase.component.input.core.multiselect;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoLista;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;

public class CaseInputCoreMultiSelectCompositePackage extends MPacote {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        MTipoComposto<?> tipoMyForm = pb.createTipoComposto("testForm");

        /**
         * Neste caso os campos de chave e valor utilizados serão os padrões "id" e "value".
         */
        MTipoComposto<MIComposto> tipoIngrediente = (MTipoComposto<MIComposto>) pb.createTipoComposto("tipoIngrediente");
        tipoIngrediente.addCampoString("id");
        tipoIngrediente.addCampoString("label");
        tipoIngrediente.withSelectionFromProvider("label", (inst, lb) ->
                        lb
                                .add().set("id", "h2o").set("label", "Água")
                                .add().set("id", "h2o2").set("label", "Água Oxigenada")
                                .add().set("id", "o2").set("label", "Gás Oxigênio")
                                .add().set("id", "C12H22O11").set("label", "Açúcar")
        );
        MTipoLista<MTipoComposto<MIComposto>, MIComposto> ingredienteQuimico =
                tipoMyForm.addCampoListaOf("ingredientes", tipoIngrediente);
        ingredienteQuimico.as(AtrBasic::new).label("Componentes Químicos");

        /**
         * Neste caso os campos de chave e valor utilizados serão os definidos por
         * "sku" e "nome".
         */
        MTipoComposto<MIComposto> productType = (MTipoComposto<MIComposto>) pb.createTipoComposto("product");
        productType.addCampoString("sku");
        productType.addCampoString("nome");
        productType.withSelectionFromProvider("nome", (inst, lb) ->
                lb
                        .add().set("sku", "SKU123456").set("nome", "Bola")
                        .add().set("sku", "SKU654321").set("nome", "Cubo")
                        .add().set("sku", "SKU987654").set("nome", "Cilindro")
                        .add().set("sku", "SKU456789").set("nome", "Pirâmide"));
        MTipoLista<MTipoComposto<MIComposto>, MIComposto> produtos =
                tipoMyForm.addCampoListaOf("products", productType);
        produtos.as(AtrBasic::new).label("Produtos");
    }
}
