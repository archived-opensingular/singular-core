package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.type.core.attachment.STypeAttachment;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeRequerente extends STypeEntidade {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        asAtr()
                .label("Requerente")
                .enabled(false);

        withInitListener( si -> {
            final SIComposite composite = (SIComposite) si;
            composite.setValue(STypeEntidade.TIPO_PESSOA         , "Juridica");
            composite.setValue(STypeEntidade.CNPJ                , "91.725.509/0001-57");
            composite.setValue(STypeEntidade.NOME                , "Cecília e Mirella Limpeza ME");
            composite.setValue(STypeEntidade.ENDERECO_ELETRONICO , "representantes@ceciliamirella.com.br");
            composite.setValue(STypeEntidade.CEP                 , "19042-070");
            composite.setValue(STypeEntidade.ENDERECO            , "Rua Alexandre Bacarin");

            final SIComposite estado = (SIComposite) composite.getField(STypeEntidade.ESTADO);
            estado.setValue(STypeEntidade.SIGLA_UF, "SP");
            estado.setValue(STypeEntidade.NOME_UF , "São Paulo");

            final SIComposite cidade = (SIComposite) composite.getField(STypeEntidade.CIDADE);
            cidade.setValue(STypeEntidade.UF_CIDADE, "SP");
            cidade.setValue(STypeEntidade.NOME_CIDADE , "Presidente Prudente");
            cidade.setValue(STypeEntidade.ID_CIDADE , 3541406);

            composite.setValue(STypeEntidade.BAIRRO  , "Parque Alvorada");
            composite.setValue(STypeEntidade.TELEFONE, "(18) 2583-6223");
            composite.setValue(STypeEntidade.FAX     , "4052 1282");
            composite.setValue(STypeEntidade.CELULAR , "(18) 99244-3094");
        });

        final STypeAttachment comprovanteRegistroEstado = addField("comprovanteRegistroEstado", STypeAttachment.class);

        comprovanteRegistroEstado
                .asAtr()
                .label("Comprovante de registro em orgão competente nessa modalidade do estado, Distrito Federal ou município")
                .asAtrBootstrap()
                .colPreference(12);
    }

}