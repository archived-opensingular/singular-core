package org.opensingular.lib.wicket.util.application;

import org.opensingular.lib.wicket.util.template.SkinOptions;

public interface SkinnableApplication {

    default void initSkins(SkinOptions skinOptions) {
        skinOptions.addDefaulSkin("singular");
        skinOptions.addSkin("anvisa");

    }
}
