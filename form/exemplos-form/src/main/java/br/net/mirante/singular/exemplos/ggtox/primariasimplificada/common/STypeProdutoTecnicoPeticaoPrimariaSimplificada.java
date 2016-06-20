package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.type.core.STypeBoolean;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.view.SViewListByForm;
import br.net.mirante.singular.form.view.SViewListByMasterDetail;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeProdutoTecnicoPeticaoPrimariaSimplificada extends STypeComposite<SIComposite> {

    public STypeList<STypeFabricante, SIComposite>       fabricantes;
    public STypeFabricanteConformeMatriz                 fabricante;
    public STypeString                                   nomeProdutoTecnico;
    public STypeAnvisaNumeroProcesso                     numeroProcessoProdutoTecnico;
    public STypeBoolean                                  finalidadeConformeMatriz;
    public STypeBoolean                                  classeConformeMatriz;
    public STypeBoolean                                  modoAcaoConformeMatriz;
    public STypeList<STypeIngredienteAtivo, SIComposite> ingredientesAtivos;
    public STypeIngredienteAtivo                         ingredienteAtivo;


    @Override
    protected void onLoadType(TypeBuilder builder) {
        super.onLoadType(builder);

        asAtr().label("Produto Técnico");

        nomeProdutoTecnico = addField("nomeProdutoTecnico", STypeString.class);
        numeroProcessoProdutoTecnico = addField("numeroProcessoProdutoTecnico", STypeAnvisaNumeroProcesso.class);
        finalidadeConformeMatriz = addFieldBoolean("finalidadeConformeMatriz");
        classeConformeMatriz = addFieldBoolean("classeConformeMatriz");
        modoAcaoConformeMatriz = addFieldBoolean("modoAcaoConformeMatriz");
        ingredientesAtivos = addFieldListOf("ingredientesAtivos", STypeIngredienteAtivo.class);
        ingredienteAtivo = ingredientesAtivos.getElementsType();

        nomeProdutoTecnico
                .asAtr()
                .label("Nome do Produto Técnico")
                .required()
                .asAtrBootstrap()
                .colPreference(6);

        numeroProcessoProdutoTecnico
                .asAtr()
                .label("Número Processo do Produto Técnico")
                .required()
                .asAtrBootstrap()
                .colPreference(6);

        numeroProcessoProdutoTecnico
                .addInstanceValidator(si -> {
                    //TODO Deve validar o numero do processo
                });

        fabricantes = addFieldListOf("fabricantes", STypeFabricante.class);
        fabricantes.withView(SViewListByForm::new);
        fabricantes.withMiniumSizeOf(1);
        fabricantes.asAtr().label("Fabricantes");
        fabricantes.withView(new SViewListByMasterDetail()
                .col(fabricantes.getElementsType().cnpj)
                .col(fabricantes.getElementsType().nome)
                .col(fabricantes.getElementsType().cidade)
                .col(fabricantes.getElementsType().estado)
        );

        fabricante = addField("fabricante", STypeFabricanteConformeMatriz.class);

        finalidadeConformeMatriz
                .asAtr()
                .label("Declaro que a finalidade está conforme a petição Matriz.")
                .asAtrBootstrap()
                .colPreference(4);

        finalidadeConformeMatriz
                .addInstanceValidator(validator -> {
                    if (!(validator.getInstance().getValue() != null && validator.getInstance().getValue())) {
                        validator.error("É obrigatório declarar que a finalidade está conforme a matriz.");
                    }
                });

        classeConformeMatriz
                .asAtr()
                .label("Declaro que a classe está conforme a petição Matriz.")
                .asAtrBootstrap()
                .colPreference(4)
                .newRow();

        classeConformeMatriz
                .addInstanceValidator(validator -> {
                    if (!(validator.getInstance().getValue() != null && validator.getInstance().getValue())) {
                        validator.error("É obrigatório declarar que a classe está conforme a matriz.");
                    }
                });

        modoAcaoConformeMatriz
                .asAtr()
                .label("Declaro que o modo de ação está conforme a petição Matriz.")
                .asAtrBootstrap()
                .colPreference(5)
                .newRow();

        modoAcaoConformeMatriz
                .addInstanceValidator(validator -> {
                    if (!(validator.getInstance().getValue() != null && validator.getInstance().getValue())) {
                        validator.error("É obrigatório declarar que o modo de ação está conforme a matriz.");
                    }
                });

        ingredientesAtivos
                .asAtr()
                .label("Ingrediente Ativo");

        ingredientesAtivos.withView(new SViewListByMasterDetail()
                .col(ingredienteAtivo.numeroCAS, "CAS")
                .col(ingredienteAtivo.nomeQuimico, "Nome químico")
                .col(ingredienteAtivo.grupoQuimico, "Grupo químico"));


    }
}
