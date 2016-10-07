/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.opensingular.form.exemplos.opas.gestaoobrasservicosaquisicoes.form;

import org.opensingular.form.exemplos.notificacaosimplificada.service.DominioService;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SInstance;
import org.opensingular.form.SPackage;

public class SPackageGestaoObrasServicosAquisicoes extends SPackage {

    public static final String PACOTE = "mform.peticao.opas.gestaoobrasservicosaquisicoes";

    public static DominioService dominioService(SInstance ins) {
        return ins.getDocument().lookupService(DominioService.class);
    }

    public SPackageGestaoObrasServicosAquisicoes() {
        super(PACOTE);
    }

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        super.onLoadPackage(pb);
//        pb.createType(STypeEstado.class);
//        pb.createType(STypeMunicipio.class);
//
//        pb.createType(STypeCurso.class);
//        pb.createType(STypePDI.class);
//        pb.createType(STypePDIProjetoPedagogico.class);
//        pb.createType(STypePDIDocumentos.class);
//        pb.createType(STypeMantenedora.class);
        pb.createType(STypeValorEmpenhadoObra.class);
        pb.createType(STypeObra.class);
        pb.createType(STypeAldeia.class);
        pb.createType(STypeChecklist.class);
        pb.createType(STypeProcesso.class);
        pb.createType(STypeGestaoObras.class);
    }
}
