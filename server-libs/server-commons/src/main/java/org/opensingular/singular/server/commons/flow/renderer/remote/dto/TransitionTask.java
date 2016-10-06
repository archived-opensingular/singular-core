package org.opensingular.singular.server.commons.flow.renderer.remote.dto;

public class TransitionTask {
    private String abbreviation;
    private String name;

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TransitionTask(String abbreviation, String name) {
        this.abbreviation = abbreviation;
        this.name = name;
    }
}
