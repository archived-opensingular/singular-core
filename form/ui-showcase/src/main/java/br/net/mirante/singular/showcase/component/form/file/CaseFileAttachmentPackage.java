/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.file;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.type.basic.AtrBasic;
import br.net.mirante.singular.form.type.basic.AtrBootstrap;
import br.net.mirante.singular.form.type.core.attachment.STypeAttachment;
import br.net.mirante.singular.showcase.component.CaseItem;
import br.net.mirante.singular.showcase.component.Group;
import br.net.mirante.singular.showcase.component.Resource;

/**
 * Campo para anexar arquivos
 */
@CaseItem(componentName = "Attachment", group = Group.FILE,
resources = @Resource(PageWithAttachment.class))
public class CaseFileAttachmentPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        STypeAttachment anexo = tipoMyForm.addField("anexo", STypeAttachment.class);
        anexo.as(AtrBasic.class).label("Anexo");
        anexo.as(AtrBasic.class).required(true);
        anexo.as(AtrBootstrap.class).colPreference(3);

        tipoMyForm.addField("a1", STypeAttachment.class).asAtr().label("a1");
        tipoMyForm.addField("a2", STypeAttachment.class).asAtr().label("a2");
        tipoMyForm.addField("a3", STypeAttachment.class).asAtr().label("a3");

    }
}
