package br.net.mirante.singular.showcase.input.core.multiselect;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoLista;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.options.MISelectItem;
import br.net.mirante.singular.form.mform.options.MTipoSelectItem;

public class CaseInputCoreMultiSelectCompositePackage extends MPacote {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        MTipoComposto<?> tipoMyForm = pb.createTipoComposto("testForm");

        /**
         * Neste caso os campos de chave e valor utilizados serão os padrões "id" e "value".
         */
        MTipoSelectItem tipoIngrediente = pb.createTipo("tipoIngrediente", MTipoSelectItem.class);
        tipoIngrediente.withSelectionOf(
                tipoIngrediente.create("h2o", "Água"),
                tipoIngrediente.create("h2o2", "Água Oxigenada"),
                tipoIngrediente.create("o2", "Gás Oxigênio"),
                tipoIngrediente.create("C12H22O11", "Açúcar")
        );
        MTipoLista<MTipoSelectItem, MISelectItem> ingredienteQuimico =
                tipoMyForm.addCampoListaOf("ingredientes", tipoIngrediente);
        ingredienteQuimico.as(AtrBasic::new).label("Componentes Químicos");

        /**
         * Neste caso os campos de chave e valor utilizados serão os definidos por
         * "sku" e "nome".
         */
        MTipoSelectItem productType =  pb.createTipo("product",MTipoSelectItem.class);
        productType.withKeyValueField("sku","nome");
        productType.withSelectionOf(
                productType.create("SKU123456", "Bola"),
                productType.create("SKU654321", "Cubo"),
                productType.create("SKU987654", "Cilindro"),
                productType.create("SKU456789", "Pirâmide")
                );
        MTipoLista<MTipoSelectItem, MISelectItem> produtos =
                tipoMyForm.addCampoListaOf("products", productType);
        produtos.as(AtrBasic::new).label("Produtos");
    }
}
