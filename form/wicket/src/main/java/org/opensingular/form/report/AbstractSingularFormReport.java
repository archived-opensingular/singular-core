/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.report;

import org.opensingular.form.InstanceSerializableRef;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.io.SFormXMLUtil;
import org.opensingular.internal.lib.commons.injection.SingularInjector;
import org.opensingular.lib.commons.context.ServiceRegistryLocator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractSingularFormReport<F extends SInstance> implements SingularFormReport<F> {

    private InstanceSerializableRef<F> filter;
    private Map<String, Serializable> params = new HashMap<>();

    @Inject
    private SDocumentFactory documentFactory;

    public AbstractSingularFormReport() {
        ServiceRegistryLocator.locate().lookupService(SingularInjector.class).ifPresent(s -> s.inject(AbstractSingularFormReport.this));
    }

    private Optional<RefType> createRefType() {
        RefType refType = new RefType() {
            transient SType<?> refType = null;

            @Nullable
            @Override
            protected SType<?> retrieve() {
                if (refType == null) {
                    refType = getFilterType(SDictionary.create().createNewPackage(this.getClass().getName()));
                }
                return refType;
            }
        };

        if (refType.get() == null) {
            return Optional.empty();
        } else {
            return Optional.of(refType);
        }
    }

    private Optional<SInstance> createInstance() {
        return createRefType().map(r -> documentFactory.createInstance(r));
    }

    @Override
    public void loadReportInstance(String xml) {
        Optional<RefType> ref = createRefType();
        if (ref.isPresent() && xml != null) {
            setFilterValue(SFormXMLUtil.fromXML(ref.get(), xml, documentFactory));
        }
    }

    @Override
    public String dumpReportInstanceXML() {
        F value = getFilterValue();
        if (value == null){
            return null;
        }
        return SFormXMLUtil.toStringXMLOrEmptyXML(value);
    }

    @Override
    public void setParam(String key, Serializable val) {
        params.put(key, val);
    }

    @Override
    public Serializable getParam(String key) {
        return params.get(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public F getFilterValue() {
        if (filter == null) {
            filter = (InstanceSerializableRef<F>) createInstance().map(SInstance::getSerializableRef).orElse(null);
        }
        return Optional.ofNullable(filter).map(InstanceSerializableRef::get).orElse(null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setFilterValue(F value) {
        if (value != null) {
            this.filter = (InstanceSerializableRef<F>) value.getSerializableRef();
        } else {
            this.filter = null;
        }
    }

}