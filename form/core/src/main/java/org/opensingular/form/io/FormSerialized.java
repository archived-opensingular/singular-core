/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.io;


import org.opensingular.form.document.RefSDocumentFactory;
import org.opensingular.form.document.RefType;
import org.opensingular.form.validation.ValidationError;
import org.opensingular.lib.commons.context.ServiceRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Transitory object for saving a serializable version of a {@link org.opensingular.form.SInstance}.
 *
 * @author Daniel C. Bordin
 */
public final class FormSerialized implements Serializable {

    private final RefSDocumentFactory                       sDocumentFactoryRef;
    private final RefType                                   refRootType;
    private final String                                    rootTypeName;
    private final byte[] contentInstance;
    private final byte[] contentAnnotations;
    private       String                                    focusFieldPath;
    private       Map<String, ServiceRegistry.ServiceEntry> services;
    private       List<ValidationError>                     validationErrors;

    FormSerialized(@Nonnull RefType refRootType, @Nonnull String rootTypeName, @Nonnull byte[] contentInstance,
            @Nullable byte[] contentAnnotations, RefSDocumentFactory sDocumentFactoryRef) {
        this.refRootType = Objects.requireNonNull(refRootType);
        this.rootTypeName = Objects.requireNonNull(rootTypeName);
        this.sDocumentFactoryRef = Objects.requireNonNull(sDocumentFactoryRef);
        this.contentInstance = Objects.requireNonNull(contentInstance);
        this.contentAnnotations = contentAnnotations;
    }

    String getRootTypeName() {
        return rootTypeName;
    }

    RefType getRefRootType() {
        return refRootType;
    }

    String getFocusFieldPath() {
        return focusFieldPath;
    }

    @Nullable
    byte[] getContentAnnotations() {
        return contentAnnotations;
    }

    byte[] getContentInstance() {
        return contentInstance;
    }

    void setFocusFieldPath(String focusFieldPath) {
        this.focusFieldPath = focusFieldPath;
    }

    public Map<String, ServiceRegistry.ServiceEntry> getServices() {
        return services;
    }

    void setServices(Map<String, ServiceRegistry.ServiceEntry> services) {
        this.services = services;
    }

    RefSDocumentFactory getSDocumentFactoryRef() {
        return sDocumentFactoryRef;
    }

    public List<ValidationError> getValidationErrors() {
        return validationErrors;
    }

    void setValidationErrors(Collection<ValidationError> validationErrors) {
        this.validationErrors = (validationErrors == null) ? null : new ArrayList<>(validationErrors);
    }
}
