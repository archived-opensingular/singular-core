/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper.composite;

import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

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
            if (ctx.getCurrentInstance().getParent() == null) {
                grid.setCssClass("singular-container");
                //TEMPORARIO
                grid.add($b.attrAppender("style", "padding-top:15px", ";"));
            }
            super.buildFields(ctx, grid);
        }
    }

}