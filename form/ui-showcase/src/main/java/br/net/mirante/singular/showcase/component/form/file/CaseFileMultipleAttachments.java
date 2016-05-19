package br.net.mirante.singular.showcase.component.form.file;

import java.util.Optional;

import br.net.mirante.singular.showcase.component.CaseBase;
import br.net.mirante.singular.showcase.component.ResourceRef;

public class CaseFileMultipleAttachments extends CaseBase {

    public CaseFileMultipleAttachments() {
        super("Multiple Attachments");
        setDescriptionHtml("Campo para anexar vários arquivos");
        final Optional<ResourceRef> page = ResourceRef.forSource(PageWithAttachment.class);
        if(page.isPresent()) {
            getAditionalSources().add(page.get());
        }
    }
}
