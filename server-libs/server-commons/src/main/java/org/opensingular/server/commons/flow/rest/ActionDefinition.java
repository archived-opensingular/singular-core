package org.opensingular.server.commons.flow.rest;

import java.io.Serializable;

public class ActionDefinition implements Serializable {

    private static final long serialVersionUID = -8568631297079891552L;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ActionDefinition that = (ActionDefinition) o;

        return !(name != null ? !name.equals(that.name) : that.name != null);

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
