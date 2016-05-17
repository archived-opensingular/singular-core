/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.studio.component.custom.comment;

import java.io.Serializable;
import java.util.Optional;

import br.net.mirante.singular.form.wicket.enums.AnnotationMode;
import br.net.mirante.singular.studio.component.CaseBase;
import br.net.mirante.singular.studio.component.ResourceRef;

public class CaseAnnotation extends CaseBase implements Serializable {

    public CaseAnnotation() {
        super("Annotation");
        setDescriptionHtml("Anotações e comentários associados a elementos de um form");
        final Optional<ResourceRef> pageWithAnnotation = ResourceRef.forSource(
                PageWithAnnotation.class);
        if (pageWithAnnotation.isPresent()) {
            getAditionalSources().add(pageWithAnnotation.get());
        }
    }

    @Override
    public AnnotationMode annotation() {
        return AnnotationMode.EDIT;
    }
}
