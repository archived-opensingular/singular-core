/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.notificacaosimplificada.form.habilitacao;

import br.net.mirante.singular.exemplos.notificacaosimplificada.form.SPackageNotificacaoSimplificada;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.STypeLocalFabricacao;
import br.net.mirante.singular.exemplos.notificacaosimplificada.service.DominioService;
import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.core.STypeDate;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.util.transformer.Value;

public class SPackageHabilitacaoEmpresa extends SPackage {

    public static final String PACOTE        = "mform.peticao.notificacaosimplificada.habilitacao";
    public static final String TIPO          = "HabilitacaoEmpresa";
    public static final String NOME_COMPLETO = PACOTE + "." + TIPO;

    public static DominioService dominioService(SInstance ins) {
        return ins.getDocument().lookupService(DominioService.class);
    }

    public SPackageHabilitacaoEmpresa() {
        super(PACOTE);
    }


    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        pb.getDictionary().loadPackage(SPackageNotificacaoSimplificada.class);

        final STypeComposite<?> habilitacaoEmpresa = pb.createCompositeType(TIPO);

        STypeString habilitarPor = habilitacaoEmpresa.addFieldString("habilitarPor");
        habilitarPor
                .asAtr().required()
                .label("Habilitar por");
        habilitarPor
                .withRadioView()
                .selectionOf("RE", "Petição de CBPF");

        STypeComposite<SIComposite> dadosRE = habilitacaoEmpresa.addFieldComposite("dadosRE");
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

        STypeComposite<SIComposite> dadosPeticaoCBPF = habilitacaoEmpresa.addFieldComposite("dadosPeticaoCBPF");
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

        STypeLocalFabricacao tipoProducao = habilitacaoEmpresa.addField("tipoProducao", STypeLocalFabricacao.class);
        tipoProducao.tipoLocalFabricacao.asAtr().label("Tipo de Produção");
        tipoProducao.asAtr().label("");

    }

}

