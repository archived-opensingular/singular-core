/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.flow.core;

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
