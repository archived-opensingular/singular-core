package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.STypeList;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.view.SViewListByMasterDetail;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeIngredienteAtivoPeticaoPrimariaSimplificada extends STypeComposite<SIComposite> {

    public static final String FIELD_NAME_LIST_ATIVOS = "ingredientesAtivos";
    public STypeList<STypeIngredienteAtivo, SIComposite> ingredientesAtivos;

    @Override
    protected void onLoadType(TypeBuilder builder) {
        super.onLoadType(builder);

        this
                .asAtrAnnotation()
                .setAnnotated();

        ingredientesAtivos = this.addFieldListOf(FIELD_NAME_LIST_ATIVOS, STypeIngredienteAtivo.class);

        ingredientesAtivos.withView(new SViewListByMasterDetail()
                .col(ingredientesAtivos.getElementsType().nomeComumPortugues)
                .col(ingredientesAtivos.getElementsType().numeroCAS, "CAS")
                .col(ingredientesAtivos.getElementsType().nomeQuimico, "Nome químico"));


    }
}
