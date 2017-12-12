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

package org.opensingular.flow.core;

import org.opensingular.flow.core.entity.IEntityFlowInstance;
import org.opensingular.flow.core.entity.IEntityVariableInstance;
import org.opensingular.flow.core.property.MetaDataKey;
import org.opensingular.flow.core.variable.VarInstance;
import org.opensingular.flow.core.variable.VarInstanceMapImpl;

import java.util.List;
import java.util.Objects;

public class VarInstanceTableFlow extends VarInstanceMapImpl {

    private static final MetaDataKey<Integer> PROP_DB_COD = MetaDataKey.of("persitence.dbCod", Integer.class);

    // TODO transformar o valor abaixo em RefFlowInstance (igual a RefFlowDefinition)
    private FlowInstance instance;

    public VarInstanceTableFlow(FlowDefinition<?> definition) {
        super(definition.getVariables());
    }

    VarInstanceTableFlow(FlowInstance instance) {
        this(instance.<FlowDefinition<?>>getFlowDefinition());
        bind(instance.getEntity());
        this.instance = instance;
    }

    private void bind(IEntityFlowInstance entityFlowInstance) {
        List<? extends IEntityVariableInstance> variables = entityFlowInstance.getVariables();
        if (variables != null) {
            for (IEntityVariableInstance variableEntity : variables) {
                VarInstance v = getVariable(variableEntity.getName());
                if (v == null) {
                    v = addDefinition(getVarService().newDefinitionString(variableEntity.getName(), variableEntity.getName(), null));
                }
                v.setValueFromPersistence(variableEntity.getValue());
                v.setMetaDataValue(PROP_DB_COD, variableEntity.getCod());
            }
        }
    }

    boolean isBinded() {
        return instance != null;
    }

    @Override
    protected boolean wantToKnowAboutChanges() {
        return true;
    }

    @Override
    public void onValueChanged(VarInstance changedVar) {
        if (isBinded()) {
            Integer dbCod = changedVar.getMetaDataValueOpt(PROP_DB_COD).orElse(null);
            Integer dbCod2 = instance.getPersistenceService().updateVariableValue(instance.getInternalEntity(), changedVar, dbCod);
            if (!Objects.equals(dbCod, dbCod2)) {
                changedVar.setMetaDataValue(PROP_DB_COD, dbCod2);
            }
        }
    }
}
