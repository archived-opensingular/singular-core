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

package org.opensingular.flow.core.variable;

import org.opensingular.flow.core.property.MetaData;

import javax.annotation.Nonnull;
import java.util.Optional;

public class VarDefinitionImpl implements VarDefinition {

    private final String ref;

    private final String name;

    private final VarType type;

    private boolean required;

    private MetaData metaData;

    public VarDefinitionImpl(VarDefinition toCopy) {
        this(toCopy.getRef(), toCopy.getName(), toCopy.getType(), toCopy.isRequired());
        copy(toCopy);
    }

    public VarDefinitionImpl(String ref, String name, VarType type, boolean required) {
        this.ref = ref;
        this.name = name;
        this.type = type;
        this.required = required;
    }

    @Override
    @Nonnull
    public Optional<MetaData> getMetaDataOpt() {
        return Optional.ofNullable(metaData);
    }
    @Override
    @Nonnull
    public MetaData getMetaData() {
        if (metaData == null) {
            metaData = new MetaData();
        }
        return metaData;
    }

    @Override
    public VarDefinition copy() {
        return new VarDefinitionImpl(this);
    }

    protected void copy(VarDefinition toCopy) {
        required = toCopy.isRequired();
    }

    @Override
    public String getRef() {
        return ref;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public VarType getType() {
        return type;
    }

    @Override
    public void setRequired(boolean value) {
        required = value;
    }

    @Override
    public VarDefinition required() {
        required = true;
        return this;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public int hashCode() {
        return ref.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof VarDefinition))
            return false;
        return ref.equalsIgnoreCase(((VarDefinition) obj).getRef());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [ref_=" + ref + ", name=" + name + ", type=" + type + "]";
    }
}
