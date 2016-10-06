/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.wicket.mapper.composite;

import org.opensingular.form.view.SViewByBlock;
import org.opensingular.form.view.SViewTab;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSGrid;

@SuppressWarnings("serial")
public class DefaultCompositeMapper extends AbstractCompositeMapper {

    @Override
    protected ICompositeViewBuilder getViewBuilder(WicketBuildContext ctx) {
        return new CompositeViewBuilder(ctx);
    }

    private static class CompositeViewBuilder extends AbstractCompositeViewBuilder {
        CompositeViewBuilder(WicketBuildContext ctx) {
            super(ctx);
        }

        @Override
        protected void buildFields(WicketBuildContext ctx, BSGrid grid) {
            if ((ctx.getCurrentInstance().getParent() == null && !ctx.isNested())
                    || (ctx.getParent().getView() instanceof SViewTab && !(ctx.getView() instanceof SViewByBlock))) {
                grid.setCssClass("singular-container");
            }
            super.buildFields(ctx, grid);
        }
    }

}