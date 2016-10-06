/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario;

import br.net.mirante.singular.exemplos.notificacaosimplificada.service.DominioService;
import org.opensingular.singular.form.PackageBuilder;
import org.opensingular.singular.form.SInfoPackage;
import org.opensingular.singular.form.SInstance;
import org.opensingular.singular.form.SPackage;

@SInfoPackage(name = SPackageVocabularioControlado.PACOTE)
public class SPackageVocabularioControlado extends SPackage {

    public static final String PACOTE        = "mform.peticao.anvisa.dominio";
    public static final String TIPO          = "VocabularioControlado";
    public static final String NOME_COMPLETO = PACOTE + "." + TIPO;

    public static DominioService dominioService(SInstance ins) {
        return ins.getDocument().lookupService(DominioService.class);
    }

    public SPackageVocabularioControlado() {
        super(PACOTE);
    }


    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        pb.createType(STypeEmbalagemPrimaria.class);
        pb.createType(STypeCategoriaRegulatoria.class);
        pb.createType(STypeEmbalagemSecundaria.class);
        pb.createType(STypeLinhaProducao.class);
        pb.createType(STypeFormaFarmaceutica.class);
        pb.createType(STypeUnidadeMedida.class);
        pb.createType(STypeFarmacopeia.class);
    }

}

