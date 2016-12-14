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

public enum TaskType implements IEntityTaskType {

    Java("J", "Java", "design/imagens/execute.png"),
    People("P", "Humana", "design/imagens/pessoa.png"),
    Wait("E", "Espera", "design/imagens/wait.png"),
    End("F", "Fim", "design/imagens/jbpm_end.png");

    private final String abbreviation;
    private final String description;
    private final String image;

    private TaskType(String abbreviation, String description, String image) {
        this.abbreviation = abbreviation;
        this.description = description;
        this.image = image;
    }

    public static TaskType valueOfAbbreviation(String abbreviation) {
        for (TaskType taskType : values()) {
            if (abbreviation.equalsIgnoreCase(taskType.getAbbreviation())) {
                return taskType;
            }
        }

        return null;
    }

    @Override
    public String getImage() {
        return image;
    }

    @Override
    public final boolean isEnd() {
        return this == TaskType.End;
    }

    @Override
    public final boolean isJava() {
        return this == TaskType.Java;
    }

    @Override
    public final boolean isPeople() {
        return this == TaskType.People;
    }

    @Override
    public final boolean isWait() {
        return this == TaskType.Wait;
    }

    @Override
    public String getAbbreviation() {
        return abbreviation;
    }
    
    @Override
    public String getDescription() {
        return description;
    }
}
