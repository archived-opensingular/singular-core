package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.view.SViewListByMasterDetail;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeIngredienteAtivoPeticaoPrimariaSimplificada extends STypeComposite<SIComposite> {

    public static final String FIELD_NAME_LIST_ATIVOS = "ingredientesAtivos";

    @Override
    protected void onLoadType(TypeBuilder builder) {
        super.onLoadType(builder);

        this
                .asAtrAnnotation()
                .setAnnotated();

        final STypeList<STypeIngredienteAtivo, SIComposite> ingredientesAtivos  = this.addFieldListOf(FIELD_NAME_LIST_ATIVOS, STypeIngredienteAtivo.class);

        ingredientesAtivos.withView(new SViewListByMasterDetail()
                .col(ingredientesAtivos.getElementsType().nomeComumPortugues)
                .col(ingredientesAtivos.getElementsType().numeroCAS, "CAS")
                .col(ingredientesAtivos.getElementsType().nomeQuimico, "Nome químico"));


    }
}
