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

package org.opensingular.form.wicket.model;

import org.opensingular.form.InstanceSerializableRef;
import org.opensingular.form.SInstance;
import org.opensingular.form.event.SInstanceEvent;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * Model para referência da MInstancia raiz da edição atual (não necessariamente
 * a raiz do Document) que já faz a correta serialização de MInstancia e
 * posterior resolução do respectivo Dicionário para viabilziar deserialziação.
 * </p>
 *
 * @author Daniel C. Bordin
 * @see {@link InstanceSerializableRef}
 */
public class SInstanceRootModel<I extends SInstance> extends AbstractSInstanceModel<I> implements ISInstanceEventCollector<I> {

    private InstanceSerializableRef<I> instanceRef;

    public SInstanceRootModel() {
    }

    @SuppressWarnings("unchecked")
    public SInstanceRootModel(I object) {
        instanceRef = (InstanceSerializableRef<I>) object.getSerializableRef();
    }

    @Override
    public I getObject() {
        if (instanceRef == null) {
            return null;
        }
        instanceRef.get().attachEventCollector();
        return instanceRef.get();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setObject(I object) {
        detachEventCollector();
        instanceRef = (InstanceSerializableRef<I>) object.getSerializableRef();
    }

    @Override
    public void detach() {
        super.detach();
        detachEventCollector();
    }

    protected void detachEventCollector() {
        if (instanceRef != null && instanceRef.get() != null) {
            instanceRef.get().detachEventCollector();
        }
    }

    @Override
    public List<SInstanceEvent> getInstanceEvents() {
        if (instanceRef.get() != null) {
            instanceRef.get().getInstanceEvents();
        }
        return Collections.emptyList();
    }

    @Override
    public void clearInstanceEvents() {
        if (instanceRef.get() != null) {
            instanceRef.get().clearInstanceEvents();
        }
    }

    @Override
    public int hashCode() {
        if (instanceRef != null && instanceRef.get() != null) {
            return instanceRef.get().hashCode();
        }
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SInstanceRootModel<?> other = (SInstanceRootModel<?>) obj;
        if (instanceRef == null) {
            return other.instanceRef == null;
        } else if (other.instanceRef == null) {
            return false;
        }
        return Objects.equals(instanceRef.get(), other.instanceRef.get());
    }
}
