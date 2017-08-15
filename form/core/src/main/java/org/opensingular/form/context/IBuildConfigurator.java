package org.opensingular.form.context;

import java.io.Serializable;

public interface IBuildConfigurator<M extends UIComponentMapper> extends Serializable {

    void onPreBuild(M mapper, IFormBuildContext ctx);

    default void onPostBuild(M mapper, IFormBuildContext ctx) {}
}
