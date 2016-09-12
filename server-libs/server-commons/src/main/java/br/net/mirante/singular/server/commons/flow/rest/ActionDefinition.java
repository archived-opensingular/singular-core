package br.net.mirante.singular.server.commons.flow.rest;

public class ActionDefinition  {

    private String name;

    public ActionDefinition(String name) {
        this.name = name;
    }

    public ActionDefinition() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
