package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.view.SViewListByMasterDetail;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeIngredienteAtivoPeticaoPrimariaSimplificada extends STypeComposite<SIComposite> {


    @Override
    protected void onLoadType(TypeBuilder builder) {
        super.onLoadType(builder);

        this
                .asAtrAnnotation()
                .setAnnotated();

        final STypeList<STypeIngredienteAtivo, SIComposite> ingredientesAtivos  = this.addFieldListOf("ingredientesAtivos", STypeIngredienteAtivo.class);

        ingredientesAtivos.withView(new SViewListByMasterDetail()
                .col(ingredientesAtivos.getElementsType().numeroCAS, "CAS")
                .col(ingredientesAtivos.getElementsType().nomeQuimico, "Nome químico")
                .col(ingredientesAtivos.getElementsType().grupoQuimico, "Grupo químico"));

//        ingredientesAtivos
//                .asAtr()
//                .label("Ingredientes Ativos");

    }
}
