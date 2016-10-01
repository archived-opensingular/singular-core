package br.net.mirante.singular.util.wicket.application;

import br.net.mirante.singular.util.wicket.template.SkinOptions;

public interface SkinnableApplication {

    default void initSkins(SkinOptions skinOptions) {
        skinOptions.addDefaulSkin("singular");
        skinOptions.addSkin("anvisa");

    }
}
