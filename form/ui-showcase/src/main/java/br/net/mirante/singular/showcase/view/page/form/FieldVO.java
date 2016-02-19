package br.net.mirante.singular.showcase.view.page.form;

import java.io.Serializable;

@SuppressWarnings("serial")
public class FieldVO implements Serializable {

    private String name, type;

    public FieldVO(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
