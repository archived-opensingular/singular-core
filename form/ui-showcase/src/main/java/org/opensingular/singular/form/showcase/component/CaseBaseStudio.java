/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.showcase.component;

import org.opensingular.form.wicket.enums.AnnotationMode;

import java.util.Optional;

public class CaseBaseStudio extends CaseBase {

    public CaseBaseStudio(Class<?> caseClass, String componentName, String subCaseName, AnnotationMode annotation) {
        super(caseClass, ShowCaseType.STUDIO, componentName, subCaseName, annotation);
    }

    @Override
    public Optional<ResourceRef> getMainSourceResourceName() {
        return Optional.ofNullable(new ResourceRef(caseClass, ""));
    }


//    public Class<? extends CollectionDefinition> getCollectionDefinition() {
//        return (Class<? extends CollectionDefinition>) caseClass;
//    }
}
