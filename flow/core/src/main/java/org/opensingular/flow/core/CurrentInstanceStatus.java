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

/**
 * Enum for check the status of the current instance.
 * The value 'S' is used for represents the current instance.
 * The value 'N' represents that the task instance is not the current instance.
 * The value 'X' is used in default case for  maintain backwards compatibility.
 */
public enum CurrentInstanceStatus {

    YES("S", "Sim"),
    NO("N", "Não"),
    UNDEFINED("X", "Não definido");

    public static final String CLASS_NAME = "org.opensingular.flow.core.CurrentInstanceStatus";

    private final String description;
    private final String abbreviation;

    CurrentInstanceStatus(String abbreviation, String description) {
        this.abbreviation = abbreviation;
        this.description = description;
    }

    public static CurrentInstanceStatus valueOfEnum(String abbreviation) {
        for (CurrentInstanceStatus type : CurrentInstanceStatus.values()) {
            if (abbreviation.trim().equals(type.getAbbreviation())) {
                return type;
            }
        }
        return null;
    }

    public boolean isCurrent() {
        return this == CurrentInstanceStatus.YES;
    }

    public String getDescription() {
        return description;
    }

    public String getAbbreviation() {
        return abbreviation;
    }
}
