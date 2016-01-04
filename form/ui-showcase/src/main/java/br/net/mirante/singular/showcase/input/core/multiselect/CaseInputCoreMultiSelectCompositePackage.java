package br.net.mirante.singular.showcase.input.core.multiselect;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;

public class CaseInputCoreMultiSelectCompositePackage extends MPacote {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        MTipoComposto<?> tipoMyForm = pb.createTipoComposto("testForm");

        /**
         * Neste caso os campos de chave e valor utilizados serão os padrões "id" e "value".
         */
        MTipoComposto tipoIngrediente = pb.createTipoComposto("tipoIngrediente");
        tipoIngrediente.withSelectionOf(
                tipoIngrediente.create("h2o", "Água"),
                tipoIngrediente.create("h2o2", "Água Oxigenada"),
                tipoIngrediente.create("o2", "Gás Oxigênio"),
                tipoIngrediente.create("C12H22O11", "Açúcar")
        );
        MTipoLista<MTipoComposto<MIComposto>, MIComposto> ingredienteQuimico =
                tipoMyForm.addCampoListaOf("ingredientes", tipoIngrediente);
        ingredienteQuimico.as(AtrBasic::new).label("Componentes Químicos");

        /**
         * Neste caso os campos de chave e valor utilizados serão os definidos por
         * "sku" e "nome".
         */
        MTipoComposto productType =  pb.createTipoComposto("product");
        productType.withKeyValueField("sku","nome");
        productType.withSelectionOf(
                productType.create("SKU123456", "Bola"),
                productType.create("SKU654321", "Cubo"),
                productType.create("SKU987654", "Cilindro"),
                productType.create("SKU456789", "Pirâmide")
                );
        MTipoLista<MTipoComposto<MIComposto>, MIComposto> produtos =
                tipoMyForm.addCampoListaOf("products", productType);
        produtos.as(AtrBasic::new).label("Produtos");
    }
}
