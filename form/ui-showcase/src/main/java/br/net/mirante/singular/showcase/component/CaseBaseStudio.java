/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component;

import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.wicket.enums.AnnotationMode;
import br.net.mirante.singular.studio.core.CollectionDefinition;

import java.util.Optional;

public class CaseBaseStudio extends CaseBase {

    public CaseBaseStudio(Class<?> caseClass, String componentName, String subCaseName, AnnotationMode annotation) {
        super(caseClass, ShowCaseType.STUDIO, componentName, subCaseName, annotation);
    }

    @Override
    public Optional<ResourceRef> getMainSourceResourceName() {
        return Optional.ofNullable(new ResourceRef(caseClass, ""));
    }

    @SuppressWarnings("unchecked")
    public Class<? extends CollectionDefinition> getCollectionDefinition() {
        return (Class<? extends CollectionDefinition>) caseClass;
    }
}
