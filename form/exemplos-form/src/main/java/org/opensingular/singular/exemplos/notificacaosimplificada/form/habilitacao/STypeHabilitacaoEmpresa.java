package org.opensingular.singular.exemplos.notificacaosimplificada.form.habilitacao;

import org.opensingular.singular.exemplos.notificacaosimplificada.form.STypeLocalFabricacao;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.type.core.STypeDate;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.util.transformer.Value;

@SInfoType(name = "STypeHabilitacaoEmpresa", spackage = SPackageHabilitacaoEmpresa.class)
public class STypeHabilitacaoEmpresa extends STypeComposite<SIComposite> {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        asAtr().label("Habilitação Empresa");

        STypeString habilitarPor = addFieldString("habilitarPor");
        habilitarPor
                .asAtr().required()
                .label("Habilitar por");
        habilitarPor
                .withRadioView()
                .selectionOf("RE", "Petição de CBPF");

        STypeComposite<SIComposite> dadosRE = addFieldComposite("dadosRE");
        dadosRE
                .asAtr().label("Dados da RE")
                .dependsOn(habilitarPor)
                .visible(ins -> "RE".equalsIgnoreCase(Value.of(ins, habilitarPor)));
        STypeInteger numero = dadosRE.addFieldInteger("numero");
        numero
                .asAtrBootstrap().colPreference(4)
                .asAtr().label("Número da RE de CBPF (boas práticas de fabricação)")
                .required();

        STypeDate dataPublicacao = dadosRE.addFieldDate("dataPublicacao");
        dataPublicacao
                .asAtrBootstrap().colPreference(4)
                .asAtr().label("Data de publicação")
                .required();

        STypeString link = dadosRE.addFieldString("link");
        link
                .asAtrBootstrap().newRow().colPreference(8)
                .asAtr().label("Link da RE de CBPF publicada no D.O.U")
                .required();

        STypeComposite<SIComposite> dadosPeticaoCBPF = addFieldComposite("dadosPeticaoCBPF");
        dadosPeticaoCBPF
                .asAtr().label("Dados da Petição de CBPF")
                .dependsOn(habilitarPor)
                .visible(ins -> "Petição de CBPF".equalsIgnoreCase(Value.of(ins, habilitarPor)));
        STypeString numeroExpediente = dadosPeticaoCBPF.addFieldString("numeroExpediente");
        numeroExpediente
                .asAtrBootstrap().colPreference(4)
                .asAtr().label("Número do Expediente")
                .required();

        STypeString numeroProtocolo = dadosPeticaoCBPF.addFieldString("numeroProtocolo");
        numeroProtocolo
                .asAtrBootstrap().newRow().colPreference(4)
                .asAtr().label("Número do Expediente")
                .enabled(false);

        STypeDate data = dadosPeticaoCBPF.addFieldDate("data");
        data
                .asAtrBootstrap().colPreference(4)
                .asAtr().label("Data")
                .enabled(false);

        STypeString assunto = dadosPeticaoCBPF.addFieldString("assunto");
        assunto
                .asAtrBootstrap().newRow().colPreference(8)
                .asAtr().label("Assunto da Petição")
                .enabled(false);

        STypeLocalFabricacao tipoProducao = addField("tipoProducao", STypeLocalFabricacao.class);
        tipoProducao.tipoLocalFabricacao.asAtr().label("Tipo de Produção");
        tipoProducao.asAtr().label("");
    }
}
