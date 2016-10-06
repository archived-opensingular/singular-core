/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.wicket.model;

import org.apache.wicket.model.IModel;

import org.opensingular.form.SIComposite;
import org.opensingular.form.type.core.annotation.AnnotationClassifier;
import org.opensingular.form.type.core.annotation.SIAnnotation;

public class SIAnnotationModel<C extends Enum<C> & AnnotationClassifier> extends AbstractSInstanceModel<SIAnnotation> {

    private IModel<? extends SIComposite> referencedInstanceModel;
    private C                             classifier;

    public SIAnnotationModel(IModel<? extends SIComposite> referencedInstanceModel, C classifier) {
        this.referencedInstanceModel = referencedInstanceModel;
        this.classifier = classifier;
    }

    @Override
    public SIAnnotation getObject() {
        return referencedInstanceModel.getObject().asAtrAnnotation().annotation(classifier);
    }

    @Override
    public void detach() {
        super.detach();
        referencedInstanceModel.detach();
    }
}
