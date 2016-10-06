/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.exemplos.emec.credenciamentoescolagoverno.form;

import br.net.mirante.singular.exemplos.notificacaosimplificada.service.DominioService;
import org.opensingular.singular.form.PackageBuilder;
import org.opensingular.singular.form.SInstance;
import org.opensingular.singular.form.SPackage;

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
