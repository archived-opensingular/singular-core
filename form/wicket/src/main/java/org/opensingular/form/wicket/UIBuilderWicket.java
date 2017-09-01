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
import org.opensingular.form.SingularFormException;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.panel.BreadPanel;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSRow;

import javax.annotation.Nonnull;
import java.util.Deque;
import java.util.LinkedList;

/**
 * Utility class for constructing a Wicket Componente for a {@link SInstance}.
 */
final class UIBuilderWicket {

    private UIBuilderWicket() {}

    public static void build(WicketBuildContext ctx, ViewMode viewMode) {
        final Deque<IWicketBuildListener> listeners = new LinkedList<>(ctx.getListeners());
        listeners.removeIf(it -> !it.isActive(ctx, null));

        ctx.init(viewMode);

        // onBuildContextInitialized
        listeners.stream().forEach(it -> it.onBuildContextInitialized(ctx));

        final IWicketComponentMapper mapper = resolveMapper(ctx.getCurrentInstance());

        if (mapper instanceof IWicketBuildListener) {
            IWicketBuildListener listenerMapper = (IWicketBuildListener) mapper;
            if (listenerMapper.isActive(ctx, mapper))
                listeners.addFirst(listenerMapper);
        }

        // onMapperResolved
        listeners.stream().forEach(it -> it.onMapperResolved(ctx, mapper));

        // decorateContext
        WicketBuildContext childCtx = ctx;
        for (IWicketBuildListener listener : listeners) {
            WicketBuildContext newContext = listener.decorateContext(ctx, mapper);
            if (newContext != null && newContext != childCtx) {
                childCtx = newContext;
                childCtx.init(viewMode);
            }
        }

        if (ctx.getParent() == null || ctx.isShowBreadcrumb()) {
            BreadPanel panel = new BreadPanel("panel", ctx.getBreadCrumbs()) {
                @Override
                public boolean isVisible() {
                    return !this.isEmpty();
                }
            };

            BSRow row = ctx.getContainer().newGrid().newRow();
            row.newCol().appendTag("div", panel);
            childCtx = ctx.createChild(row.newCol(), ctx.getModel());
            childCtx.init(viewMode);
        }

        // onBeforeBuild
        final WicketBuildContext finalCtx = childCtx;
        listeners.stream().forEach(it -> it.onBeforeBuild(finalCtx, mapper));

        mapper.buildView(finalCtx);

        // onAfterBuild
        listeners.stream().forEach(it -> it.onAfterBuild(finalCtx, mapper));
    }

    @Nonnull
    private static IWicketComponentMapper resolveMapper(@Nonnull SInstance instance) {
        return instance.getAspect(IWicketComponentMapper.ASPECT_WICKET_MAPPER).orElseThrow(
                () -> new SingularFormException("Não há mappeamento de componente Wicket para o tipo", instance));
    }
}
