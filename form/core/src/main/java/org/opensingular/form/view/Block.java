package org.opensingular.form.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 08/06/2016.
 */
public class Block implements Serializable {

    private String name;
    private List<String> types = new ArrayList<>();

    public Block() {
    }

    public Block(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }
}
