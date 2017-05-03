package org.opensingular.form.wicket;

import java.io.Serializable;

import org.opensingular.form.wicket.enums.ViewMode;

/**
 * Interface listener para o ciclo de build
 */
public interface IWicketBuildListener extends Serializable {

    default void onBuildContextInitialized(WicketBuildContext ctx, ViewMode viewMode) {}

    default void onMapperResolved(WicketBuildContext child, IWicketComponentMapper mapper, ViewMode viewMode) {}

    default WicketBuildContext decorateContext(WicketBuildContext ctx, IWicketComponentMapper mapper, ViewMode viewMode) { return null; }

    default void onBeforeBuild(WicketBuildContext ctx, IWicketComponentMapper mapper, ViewMode viewMode) {}

    default void onAfterBuild(WicketBuildContext ctx, IWicketComponentMapper mapper, ViewMode viewMode) {}
}
