package br.net.mirante.singular.showcase.component.file;

import br.net.mirante.singular.showcase.component.CaseBase;
import br.net.mirante.singular.showcase.component.ResourceRef;
import br.net.mirante.singular.showcase.component.custom.comment.PageWithAnnotation;

import java.io.Serializable;
import java.util.Optional;

public class CaseFileAttachment extends CaseBase implements Serializable {

    public CaseFileAttachment() {
        super("Attachment");
        setDescriptionHtml("Campo para anexar arquivos");
        final Optional<ResourceRef> page = ResourceRef.forSource(PageWithAttachment.class);
        if(page.isPresent()) {
            getAditionalSources().add(page.get());
        }

    }
}
