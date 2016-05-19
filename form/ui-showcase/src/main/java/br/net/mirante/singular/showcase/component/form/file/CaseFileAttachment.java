/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.file;

import java.io.Serializable;
import java.util.Optional;

import br.net.mirante.singular.showcase.component.CaseBase;
import br.net.mirante.singular.showcase.component.ResourceRef;

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