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

package org.opensingular.flow.core.entity;

import java.util.Date;
import java.util.List;

import org.opensingular.flow.core.IEntityTaskType;

public interface IEntityTaskVersion extends IEntityByCod<Integer> {

    IEntityProcessVersion getProcessVersion();

    String getName();

    void setName(String name);

    IEntityTaskType getType();

    IEntityTaskDefinition getTaskDefinition();

    List<? extends IEntityTaskTransitionVersion> getTransitions();

    default Date getVersionDate(){
        return getProcessVersion().getVersionDate();
    }

    default IEntityTaskTransitionVersion getTransition(String abbreviation) {
        for (IEntityTaskTransitionVersion entityTaskTransition : getTransitions()) {
            if (entityTaskTransition.getAbbreviation().equalsIgnoreCase(abbreviation)) {
                return entityTaskTransition;
            }
        }
        return null;
    }

    default String getAbbreviation(){
        return getTaskDefinition().getAbbreviation();
    }

    default boolean isEnd() {
        return getType().isEnd();
    }

    default boolean isPeople() {
        return getType().isPeople();
    }

    default boolean isWait() {
        return getType().isWait();
    }

    default boolean isJava() {
        return getType().isJava();
    }

    default String getDetail() {
        return "(" + getType().getAbbreviation() + ") " + getName();
    }
}
