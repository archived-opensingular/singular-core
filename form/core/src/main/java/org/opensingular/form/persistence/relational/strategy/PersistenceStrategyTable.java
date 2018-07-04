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

package org.opensingular.form.persistence.relational.strategy;

import java.util.List;

import org.opensingular.form.ICompositeInstance;
import org.opensingular.form.SInstance;
import org.opensingular.form.persistence.relational.RelationalData;
import org.opensingular.form.persistence.relational.RelationalSQL;

/**
 * Table-based strategy for saving/loading SInstance data.
 *
 * @author Edmundo Andrade
 */
public class PersistenceStrategyTable implements PersistenceStrategy {
    @Override
    public void save(SInstance instance, List<RelationalData> toList) {
        for (SInstance item : getChildren(instance)) {
            RelationalSQL.persistenceStrategy(item.getType()).save(item, toList);
        }
    }

    @Override
    public void load(SInstance instance, List<RelationalData> fromList) {
        for (SInstance item : getChildren(instance)) {
            RelationalSQL.persistenceStrategy(item.getType()).load(item, fromList);
        }
    }

    protected List<? extends SInstance> getChildren(SInstance instance) {
        if (instance instanceof ICompositeInstance) {
            return ((ICompositeInstance) instance).getAllChildren();
        }
        return instance.getChildren();
    }
}
