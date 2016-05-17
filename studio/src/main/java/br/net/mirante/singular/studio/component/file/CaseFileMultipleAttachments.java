/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.studio.component.file;

import java.util.Optional;

import br.net.mirante.singular.studio.component.CaseBase;
import br.net.mirante.singular.studio.component.ResourceRef;

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
