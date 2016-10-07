package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.type.core.attachment.STypeAttachment;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeRequerente extends STypeEntidade {

    public STypeAttachment comprovanteRegistroEstado;

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        this
                .asAtrAnnotation()
                .setAnnotated();

        asAtr()
                .label("Requerente");

        getField(TIPO_PESSOA).asAtr().enabled(false);
        getField(CNPJ).asAtr().enabled(false);
        getField(CPF).asAtr().enabled(false);
        getField(NOME).asAtr().enabled(false);
        getField(ENDERECO_ELETRONICO).asAtr().enabled(false);
        getField(CEP).asAtr().enabled(false);
        getField(ENDERECO).asAtr().enabled(false);
        getField(ESTADO).asAtr().enabled(false);
        getField(CIDADE).asAtr().enabled(false);
        getField(BAIRRO).asAtr().enabled(false);
        getField(TELEFONE).asAtr().enabled(false);
        getField(FAX).asAtr().enabled(false);
        getField(CELULAR).asAtr().enabled(false);

        withInitListener(si -> {
            si.setValue(STypeEntidade.TIPO_PESSOA, "Jurídica");
            si.setValue(STypeEntidade.CNPJ, "91.725.509/0001-57");
            si.setValue(STypeEntidade.NOME, "Cecília e Mirella Limpeza ME");
            si.setValue(STypeEntidade.ENDERECO_ELETRONICO, "representantes@ceciliamirella.com.br");
            si.setValue(STypeEntidade.CEP, "19042-070");
            si.setValue(STypeEntidade.ENDERECO, "Rua Alexandre Bacarin");

            final SIComposite estado = (SIComposite) si.getField(STypeEntidade.ESTADO);
            estado.setValue(STypeEntidade.SIGLA_UF, "SP");
            estado.setValue(STypeEntidade.NOME_UF, "São Paulo");

            final SIComposite cidade = (SIComposite) si.getField(STypeEntidade.CIDADE);
            cidade.setValue(STypeEntidade.UF_CIDADE, "SP");
            cidade.setValue(STypeEntidade.NOME_CIDADE, "Presidente Prudente");
            cidade.setValue(STypeEntidade.ID_CIDADE, 3541406);

            si.setValue(STypeEntidade.BAIRRO, "Parque Alvorada");
            si.setValue(STypeEntidade.TELEFONE, "(18) 2583-6223");
            si.setValue(STypeEntidade.FAX, "4052 1282");
            si.setValue(STypeEntidade.CELULAR, "(18) 99244-3094");
        });

        comprovanteRegistroEstado = addField("comprovanteRegistroEstado", STypeAttachment.class);

        comprovanteRegistroEstado
                .asAtr()
                .label("Comprovante de registro em orgão competente nessa modalidade do estado, Distrito Federal ou município")
                .asAtrBootstrap()
                .colPreference(12);
    }

}