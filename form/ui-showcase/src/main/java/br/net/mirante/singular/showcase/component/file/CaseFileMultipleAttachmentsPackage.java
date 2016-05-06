package br.net.mirante.singular.showcase.component.file;

import br.net.mirante.singular.form.*;
import org.apache.commons.lang3.StringUtils;

public class CaseFileMultipleAttachmentsPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
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
