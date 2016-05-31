package br.net.mirante.singular.showcase.component.form.file;

import org.apache.commons.lang3.StringUtils;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeAttachmentList;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.showcase.component.CaseItem;
import br.net.mirante.singular.showcase.component.Group;
import br.net.mirante.singular.showcase.component.Resource;

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
