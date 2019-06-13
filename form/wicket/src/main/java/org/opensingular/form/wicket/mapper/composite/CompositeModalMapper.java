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

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SIComposite;
import org.opensingular.form.view.SViewByBlock;
import org.opensingular.form.view.SViewTab;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.component.SingularButton;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.mapper.AbstractControlsFieldComponentMapper;
import org.opensingular.form.wicket.mapper.decorator.SInstanceActionsProviders;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSGrid;
import org.opensingular.lib.wicket.util.bootstrap.layout.TemplatePanel;

import static org.opensingular.form.wicket.AjaxUpdateListenersFactory.SINGULAR_PROCESS_EVENT;
import static org.opensingular.lib.wicket.util.util.Shortcuts.$b;
import static org.opensingular.lib.wicket.util.util.Shortcuts.$m;

public class CompositeModalMapper extends DefaultCompositeMapper {

    private SInstanceActionsProviders instanceActionsProviders = new SInstanceActionsProviders(this);

    @Override
    protected ICompositeViewBuilder getViewBuilder(WicketBuildContext ctx) {
        return new CompositeModalMapper.CompositeModalViewBuilder(ctx, this);
    }

    private class CompositeModalViewBuilder extends AbstractCompositeViewBuilder {

        CompositeModalViewBuilder(WicketBuildContext ctx, AbstractCompositeMapper mapper) {
            super(ctx, mapper);
        }

        @Override
        public void buildView() {

            ctx.setHint(AbstractControlsFieldComponentMapper.NO_DECORATION, Boolean.FALSE);
            final IModel<SIComposite> model = (IModel<SIComposite>) ctx.getModel();


            final ViewMode       viewMode        = ctx.getViewMode();
            final BSContainer<?> currentExternal = new BSContainer<>("externalContainerAtual");
            final BSContainer<?> currentSibling  = new BSContainer<>("externalContainerIrmao");

            ctx.getExternalContainer().appendTag("div", true, null, currentExternal);
            ctx.getExternalContainer().appendTag("div", true, null, currentSibling);

            final CompositeModal modal = new CompositeModal("mods", model, newItemLabelModel(model), ctx, viewMode, currentSibling) {
                @Override
                protected WicketBuildContext buildModalContent(BSContainer<?> modalBody, ViewMode viewModeModal) {
                    buildFields(ctx, modalBody.newGrid());
                    return ctx;
                }
            };
            modal.add($b.onEnterDelegate(modal.addButton, SINGULAR_PROCESS_EVENT));


            currentExternal.appendTag("div", true, null, modal);

            SingularButton button = getButton(model, modal);
            TemplatePanel  panel  = ctx.getContainer().newTemplateTag(t -> "<a wicket:id=\"btn\" class=\"btn btn-default\">Editar</a>");
            panel.add(button);

        }

        public SingularButton getButton(IModel<SIComposite> model, CompositeModal modal) {
            return new SingularButton("btn", model) {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    modal.show(target);
                }
            };
        }

        @Override
        protected void buildFields(WicketBuildContext ctx, BSGrid grid) {
            if (((ctx.getCurrentInstance().getParent() == null) && (!ctx.isNested())) ||
                    ((ctx.getParent().getView() instanceof SViewTab) && !(ctx.getView() instanceof SViewByBlock))) {
                grid.setCssClass("singular-container");
            }
            super.buildFields(ctx, grid);
        }


        private IModel<String> newItemLabelModel(IModel<SIComposite> listModel) {
            //Alteração do model para evitar que haja perda de referencias na renderização das tabelas na tela
            return $m.get(() -> listModel.getObject().asAtr().getLabel());
        }

    }
}
