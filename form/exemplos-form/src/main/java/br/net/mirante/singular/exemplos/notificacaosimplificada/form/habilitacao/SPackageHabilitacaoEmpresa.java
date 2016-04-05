/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.notificacaosimplificada.form.habilitacao;

import br.net.mirante.singular.exemplos.notificacaosimplificada.form.SPackageNotificacaoSimplificada;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.STypeLocalFabricacao;
import br.net.mirante.singular.exemplos.notificacaosimplificada.service.DominioService;
import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.core.STypeDate;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.util.transformer.Value;

@SInfoType(spackage = SPackageHabilitacaoEmpresa.class)
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
                .asAtrBasic().required()
                .label("Habilitar por");
        habilitarPor
                .withRadioView()
                .withSelection()
                .add("RE")
                .add("Petição de CBPF");

        STypeComposite<SIComposite> dadosRE = habilitacaoEmpresa.addFieldComposite("dadosRE");
        dadosRE
                .asAtrBasic().label("Dados da RE")
                .dependsOn(habilitarPor)
                .visible(ins -> "RE".equalsIgnoreCase(Value.of(ins, habilitarPor)));
        STypeInteger numero = dadosRE.addFieldInteger("numero");
        numero
                .asAtrBootstrap().colPreference(4)
                .asAtrBasic().label("Número da RE de CBPF (boas práticas de fabricação)")
                .required();

        STypeDate dataPublicacao = dadosRE.addFieldDate("dataPublicacao");
        dataPublicacao
                .asAtrBootstrap().colPreference(4)
                .asAtrBasic().label("Data de publicação")
                .required();

        STypeString link = dadosRE.addFieldString("link");
        link
                .asAtrBootstrap().newRow().colPreference(8)
                .asAtrBasic().label("Link da RE de CBPF publicada no D.O.U")
                .required();

        STypeComposite<SIComposite> dadosPeticaoCBPF = habilitacaoEmpresa.addFieldComposite("dadosPeticaoCBPF");
        dadosPeticaoCBPF
                .asAtrBasic().label("Dados da Petição de CBPF")
                .dependsOn(habilitarPor)
                .visible(ins -> "Petição de CBPF".equalsIgnoreCase(Value.of(ins, habilitarPor)));
        STypeString numeroExpediente = dadosPeticaoCBPF.addFieldString("numeroExpediente");
        numeroExpediente
                .asAtrBootstrap().colPreference(4)
                .asAtrBasic().label("Número do Expediente")
                .required();

        STypeString numeroProtocolo = dadosPeticaoCBPF.addFieldString("numeroProtocolo");
        numeroProtocolo
                .asAtrBootstrap().newRow().colPreference(4)
                .asAtrBasic().label("Número do Expediente")
                .enabled(false);

        STypeDate data = dadosPeticaoCBPF.addFieldDate("data");
        data
                .asAtrBootstrap().colPreference(4)
                .asAtrBasic().label("Data")
                .enabled(false);

        STypeString assunto = dadosPeticaoCBPF.addFieldString("assunto");
        assunto
                .asAtrBootstrap().newRow().colPreference(8)
                .asAtrBasic().label("Assunto da Petição")
                .enabled(false);

        STypeLocalFabricacao tipoProducao = habilitacaoEmpresa.addField("tipoProducao", STypeLocalFabricacao.class);
        tipoProducao.tipoLocalFabricacao.asAtrBasic().label("Tipo de Produção");
        tipoProducao.asAtrBasic().label("");

    }

}

