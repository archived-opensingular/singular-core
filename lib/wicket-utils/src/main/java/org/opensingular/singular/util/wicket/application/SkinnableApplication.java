package org.opensingular.singular.util.wicket.application;

import org.opensingular.singular.util.wicket.template.SkinOptions;

public interface SkinnableApplication {

    default void initSkins(SkinOptions skinOptions) {
        skinOptions.addDefaulSkin("singular");
        skinOptions.addSkin("anvisa");

    }
}
