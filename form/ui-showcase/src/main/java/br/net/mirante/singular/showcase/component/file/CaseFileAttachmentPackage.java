/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.file;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.type.basic.AtrBasic;
import br.net.mirante.singular.form.type.basic.AtrBootstrap;
import br.net.mirante.singular.form.type.core.attachment.STypeAttachment;

public class CaseFileAttachmentPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        STypeAttachment anexo = tipoMyForm.addField("anexo", STypeAttachment.class);
        anexo.as(AtrBasic.class).label("Anexo");
        anexo.as(AtrBasic.class).required(true);
        anexo.as(AtrBootstrap.class).colPreference(3);
    }
}
