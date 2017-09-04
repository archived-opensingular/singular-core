package org.opensingular.lib.commons.ui;

import java.io.Serializable;

@FunctionalInterface
public interface Icon extends Serializable {

    String getCssClass();

    static Icon of(String cssClass){
        return () -> cssClass;
    }
}
