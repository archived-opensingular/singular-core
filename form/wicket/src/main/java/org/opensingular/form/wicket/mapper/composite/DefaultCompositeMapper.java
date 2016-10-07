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