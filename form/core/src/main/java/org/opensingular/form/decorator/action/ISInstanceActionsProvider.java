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

package org.opensingular.form.decorator.action;

import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Provedor de ações sobre instâncias.
 */
public interface ISInstanceActionsProvider extends Serializable {

    /**
     * Retorna as ações apropriadas para uma instância.
     */
    Iterable<SInstanceAction> getActions(ISInstanceActionCapable target, SInstance instance);

    default Iterable<SInstanceAction> getActions(ISInstanceActionCapable target, SInstance instance, ActionClassifier actionClassifier) {
        return getActions(target, instance);
    }
    
    default List<SInstanceAction> getListFieldActions(ISInstanceActionCapable target, SIList<?> instance, String field) {
        return Collections.emptyList();
    }
    default List<SInstanceAction> getListFieldActions(ISInstanceActionCapable target, SIList<?> instance, String field, ActionClassifier actionClassifier) {
        return getListFieldActions(target, instance, field);
    }

}
