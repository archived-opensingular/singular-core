/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.opensingular.form.exemplos.emec.credenciamentoescolagoverno.form;

import org.opensingular.form.exemplos.notificacaosimplificada.service.DominioService;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SInstance;
import org.opensingular.form.SPackage;

public class SPackageCredenciamentoEscolaGoverno extends SPackage {

    public static final String PACOTE = "mform.peticao.emec.credenciamento.escolagoverno";

    public static DominioService dominioService(SInstance ins) {
        return ins.getDocument().lookupService(DominioService.class);
    }

    public SPackageCredenciamentoEscolaGoverno() {
        super(PACOTE);
    }

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        super.onLoadPackage(pb);
        pb.createType(STypeEstado.class);
        pb.createType(STypeSexo.class);
        pb.createType(STypeMunicipio.class);

        pb.createType(STypeCurso.class);
        pb.createType(STypePDI.class);
        pb.createType(STypePDIProjetoPedagogico.class);
        pb.createType(STypePDIDocumentos.class);
        pb.createType(STypeMantenedora.class);
        pb.createType(STypeCorpoDirigente.class);
        pb.createType(STypeCredenciamentoEscolaGoverno.class);
    }
}
