package org.opensingular.form.wicket;

import java.io.Serializable;

/**
 * Interface listener para o ciclo de build
 */
public interface IWicketBuildListener extends Serializable {
    //@formatter:off

    default void onBuildContextInitialized(WicketBuildContext ctx) {}

    default void onMapperResolved(WicketBuildContext child, IWicketComponentMapper mapper) {}

    default WicketBuildContext decorateContext(WicketBuildContext ctx, IWicketComponentMapper mapper) { return null; }

    default void onBeforeBuild(WicketBuildContext ctx, IWicketComponentMapper mapper) {}

    default void onAfterBuild(WicketBuildContext ctx, IWicketComponentMapper mapper) {}
    
    //@formatter:on
}
