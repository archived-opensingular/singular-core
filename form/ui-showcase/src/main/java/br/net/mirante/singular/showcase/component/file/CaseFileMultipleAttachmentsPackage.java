package br.net.mirante.singular.showcase.component.file;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeAttachmentList;
import br.net.mirante.singular.form.mform.STypeComposite;

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
                .asAtrBasic()
                .label("Layouts Rotulagem");
    }
}
