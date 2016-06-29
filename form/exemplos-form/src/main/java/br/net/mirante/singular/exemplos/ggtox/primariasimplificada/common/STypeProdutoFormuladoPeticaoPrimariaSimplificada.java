package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.STypeList;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.view.SViewListByMasterDetail;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeProdutoFormuladoPeticaoPrimariaSimplificada extends STypeComposite<SIComposite> {


    public STypeFormuladorConformeMatriz formulador;
    public STypeList<STypeFormulador, SIComposite> formuladores;


    @Override
    protected void onLoadType(TypeBuilder builder) {
        super.onLoadType(builder);
        this
                .asAtr()
                .label("Produto Formulado");

        formuladores = addFieldListOf("formuladores", STypeFormulador.class);

        formuladores
                .asAtr()
                .label("Formuladores");
        formuladores
                .withView(new SViewListByMasterDetail()
                                .col(formuladores.getElementsType().cnpj)
                                .col(formuladores.getElementsType().nome)
                                .col(formuladores.getElementsType().cidade)
                                .col(formuladores.getElementsType().estado)
                );
        formuladores
                .withMiniumSizeOf(1);


        formulador = addField("formulador", STypeFormuladorConformeMatriz.class);
        formulador
                .asAtrBootstrap()
                .colPreference(6)
                .newRow();

    }
}
