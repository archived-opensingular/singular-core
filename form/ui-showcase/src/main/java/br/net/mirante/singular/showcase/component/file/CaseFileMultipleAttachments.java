package br.net.mirante.singular.showcase.component.file;

import br.net.mirante.singular.showcase.component.CaseBase;
import br.net.mirante.singular.showcase.component.ResourceRef;

import java.util.Optional;

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
