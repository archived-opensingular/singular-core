/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
