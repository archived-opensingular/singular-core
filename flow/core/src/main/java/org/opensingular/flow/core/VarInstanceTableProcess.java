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

import java.util.List;
import java.util.Objects;

import org.opensingular.flow.core.entity.IEntityProcessInstance;
import org.opensingular.flow.core.entity.IEntityVariableInstance;
import org.opensingular.flow.core.property.MetaDataRef;
import org.opensingular.flow.core.variable.VarInstance;
import org.opensingular.flow.core.variable.VarInstanceMapImpl;

public class VarInstanceTableProcess extends VarInstanceMapImpl {

    private static final MetaDataRef<Integer> PROP_DB_COD = new MetaDataRef<>("persitence.dbCod", Integer.class);

    // TODO transformar o valor abaixo em RefProcessInstance (igual a
    // RefProcessDefinition)
    private ProcessInstance instancia;

    public VarInstanceTableProcess(ProcessDefinition<?> definition) {
        super(definition.getVariables());
    }

    VarInstanceTableProcess(ProcessInstance instancia) {
        this(instancia.<ProcessDefinition<?>>getProcessDefinition());
        bind(instancia.getEntity());
        this.instancia = instancia;
    }

    private void bind(IEntityProcessInstance iModelProcessInstance) {
        List<? extends IEntityVariableInstance> variaveis_ = iModelProcessInstance.getVariables();
        if (variaveis_ != null) {
            for (IEntityVariableInstance dadosVariavel : variaveis_) {
                VarInstance v = getVariavel(dadosVariavel.getName());
                if (v == null) {
                    v = addDefinicao(getVarService().newDefinitionString(dadosVariavel.getName(), dadosVariavel.getName(), null));
                }
                v.setValor(dadosVariavel.getValue());
                v.getMetaData().set(PROP_DB_COD, dadosVariavel.getCod());
            }
        }
    }

    boolean isBinded() {
        return instancia != null;
    }

    @Override
    protected boolean wantToKnowAboutChanges() {
        return true;
    }

    @Override
    public void onValueChanged(VarInstance changedVar) {
        if (isBinded()) {
            Integer dbCod = changedVar.getMetaData().get(PROP_DB_COD);
            Integer dbCod2 = instancia.getPersistenceService().updateVariableValue(instancia.getInternalEntity(), changedVar, dbCod);
            if (!Objects.equals(dbCod, dbCod2)) {
                changedVar.getMetaData().set(PROP_DB_COD, dbCod2);
            }
        }
    }
}
