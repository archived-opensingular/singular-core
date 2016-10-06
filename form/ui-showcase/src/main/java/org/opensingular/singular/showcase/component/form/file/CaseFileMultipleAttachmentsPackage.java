package org.opensingular.singular.showcase.component.form.file;

import org.apache.commons.lang3.StringUtils;

import org.opensingular.singular.form.PackageBuilder;
import org.opensingular.singular.form.SInstance;
import org.opensingular.singular.form.SPackage;
import org.opensingular.singular.form.STypeAttachmentList;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.showcase.component.CaseItem;
import org.opensingular.singular.showcase.component.Group;
import org.opensingular.singular.showcase.component.Resource;

/**
 * Campo para anexar vários arquivos
 */
@CaseItem(componentName = "Multiple Attachments", group = Group.FILE,
resources = @Resource(PageWithAttachment.class))
public class CaseFileMultipleAttachmentsPackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        final STypeAttachmentList layoutsRotulagem = tipoMyForm
                .addFieldListOfAttachment("layoutsRotulagem", "layout");
        layoutsRotulagem
                .withMiniumSizeOf(1);
        layoutsRotulagem
                .withMaximumSizeOf(4);
        layoutsRotulagem
                .asAtr()
                .label("Layouts Rotulagem");

        tipoMyForm.asAtr().displayString(cc -> cc.instance()
                .findNearest(layoutsRotulagem)
                .map(SInstance::toStringDisplay)
                .orElse(StringUtils.EMPTY));
    }
}
