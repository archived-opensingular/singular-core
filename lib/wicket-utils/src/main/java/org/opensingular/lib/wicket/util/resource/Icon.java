package org.opensingular.lib.wicket.util.resource;

import java.io.Serializable;

@FunctionalInterface
public interface Icon extends Serializable {

    String getCssClass();

}
