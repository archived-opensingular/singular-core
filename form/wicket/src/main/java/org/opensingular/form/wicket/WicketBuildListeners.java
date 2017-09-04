package org.opensingular.form.wicket;

import org.opensingular.form.SInstance;
import org.opensingular.lib.commons.lambda.IPredicate;
import org.opensingular.lib.commons.lambda.ITriConsumer;

public class WicketBuildListeners {

    private WicketBuildListeners() {}

    public static <T> IWicketBuildListener onBeforeBuildIfMapperIs(Class<T> mapperType, ITriConsumer<WicketBuildContext, IWicketComponentMapper, T> handler) {
        return onBeforeBuildIfMapperIs(mapperType, null, handler);
    }

    public static <T> IWicketBuildListener onBeforeBuildIfMapperIs(Class<T> mapperType, IPredicate<SInstance> active, ITriConsumer<WicketBuildContext, IWicketComponentMapper, T> handler) {
        return new IWicketBuildListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onBeforeBuild(WicketBuildContext ctx, IWicketComponentMapper mapper) {
                if (mapperType.isAssignableFrom(mapper.getClass()))
                    handler.accept(ctx, mapper, (T) mapper);
            }
            @Override
            public boolean isActive(WicketBuildContext ctx, IWicketComponentMapper mapper) {
                return (active != null) ? active.test(ctx.getCurrentInstance()) : true;
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
