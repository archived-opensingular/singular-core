package br.net.mirante.singular.util.wicket.application;

import br.net.mirante.singular.util.wicket.template.SkinOptions;
import org.apache.wicket.markup.head.CssHeaderItem;

public interface SkinnableApplication {

    default void initSkins(SkinOptions skinOptions) {
        skinOptions.addDefaulSkin("Default", CssHeaderItem.forUrl("/singular-static/resources/metronic/layout4/css/themes/default.css"));
        skinOptions.addSkin("Anvisa", CssHeaderItem.forUrl("/singular-static/resources/singular/themes/anvisa.css"));
        skinOptions.addSkin("Montreal", CssHeaderItem.forUrl("/singular-static/resources/singular/themes/montreal.css"));
    }
}
