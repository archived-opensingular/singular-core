package org.opensingular.form.wicket;

import org.opensingular.lib.commons.lambda.ITriConsumer;

public abstract class WicketBuildListeners {

    private WicketBuildListeners() {}

    public static <T> IWicketBuildListener onBeforeBuildIfMapperIs(Class<T> mapperType, ITriConsumer<WicketBuildContext, IWicketComponentMapper, T> handler) {
        return new IWicketBuildListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onBeforeBuild(WicketBuildContext ctx, IWicketComponentMapper mapper) {
                if (mapperType.isAssignableFrom(mapper.getClass()))
                    handler.accept(ctx, mapper, (T) mapper);
            }
        };
    }

    public static <T> IWicketBuildListener onAfterBuildIfMapperIs(Class<T> type, ITriConsumer<WicketBuildContext, IWicketComponentMapper, T> handler) {
        return new IWicketBuildListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onAfterBuild(WicketBuildContext ctx, IWicketComponentMapper mapper) {
                if (type.isAssignableFrom(mapper.getClass()))
                    handler.accept(ctx, mapper, (T) mapper);
            }
        };
    }

}
