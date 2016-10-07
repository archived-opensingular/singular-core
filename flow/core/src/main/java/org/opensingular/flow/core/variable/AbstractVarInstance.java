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

public abstract class AbstractVarInstance implements VarInstance {

    private MetaData metaData;

    private final VarDefinition definition;

    private VarInstanceMap<?> changeListener;

    public AbstractVarInstance(VarDefinition definition) {
        this.definition = definition;
    }


    @Override
    public VarDefinition getDefinicao() {
        return definition;
    }

    @Override
    public String getStringDisplay() {
        return getDefinicao().toDisplayString(this);
    }

    @Override
    public String getStringPersistencia() {
        return getDefinicao().toPersistenceString(this);
    }

    @Override
    public MetaData getMetaData() {
        if (metaData == null) {
            metaData = new MetaData();
        }
        return metaData;
    }

    @Override
    public void setChangeListner(VarInstanceMap<?> changeListener) {
        this.changeListener = changeListener;
    }

    protected final boolean needToNotifyAboutValueChanged() {
        return changeListener != null;
    }

    protected void notifyValueChanged() {
        changeListener.onValueChanged(this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [definicao=" + getRef() + ", codigo=" + getValor() + "]";
    }
}
