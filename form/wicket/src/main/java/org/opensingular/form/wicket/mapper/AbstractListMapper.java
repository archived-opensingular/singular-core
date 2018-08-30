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

package org.opensingular.form.wicket.mapper;

import org.apache.commons.collections.Factory;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.view.list.AbstractSViewListWithControls;
import org.opensingular.form.wicket.IWicketComponentMapper;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.feedback.SValidationFeedbackPanel;
import org.opensingular.form.wicket.mapper.buttons.AddButton;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.bootstrap.layout.TemplatePanel;
import org.opensingular.lib.wicket.util.util.Shortcuts;

import java.util.Optional;
import java.util.Set;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;

public abstract class AbstractListMapper implements IWicketComponentMapper {

    protected static void buildFooter(BSContainer<?> footer, Form<?> form, IModel<SIList<SInstance>> list, WicketBuildContext ctx) {
        Factory createAddButton = () -> new AddButton("_add", form, list);
        buildFooter(footer, ctx, createAddButton);
        createFeedBackPanelFooter(footer, ctx);
    }

    @SuppressWarnings("unchecked")
    protected static void buildFooter(BSContainer<?> footer, Form<?> form, WicketBuildContext ctx) {
        Factory createAddButton = () -> new AddButton("_add", form, (IModel<SIList<SInstance>>) ctx.getModel());
        buildFooter(footer, ctx, createAddButton);
        createFeedBackPanelFooter(footer, ctx);
    }

    private static void createFeedBackPanelFooter(BSContainer<?> footer, WicketBuildContext ctx) {
        SValidationFeedbackPanel feedback = ctx.createFeedbackPanel("feedback");
        AttributeAppender style = Shortcuts.$b.attrAppender("style", "margin-top: 15px; color: #e73d4a", ";");
        feedback.add(style);
        footer.appendTag("div", feedback);
    }

    public static void buildFooter(BSContainer<?> footer, WicketBuildContext ctx, Factory createAddButton) {
        final TemplatePanel template = footer.newTemplateTag(tp -> createButtonMarkup(ctx));
        template.add((Component) createAddButton.create());
        footer.add($b.onConfigure(c -> c.setVisible(canAddItems(ctx))));
        personalizeCSS(footer);
    }

    public static boolean canAddItems(WicketBuildContext ctx) {
        return ((AbstractSViewListWithControls<?>) ctx.getView()).isAddEnabled((SIList<?>) ctx.getModel().getObject())
            && ctx.getViewMode().isEdition();
    }

    protected static String createButtonMarkup(WicketBuildContext ctx) {
        String label = defineLabel(ctx);

        return String.format("<button wicket:id=\"_add\" class=\"btn btn-add\" type=\"button\" title=\"%s\"><i class=\"fa fa-plus\"></i>%s</button>", label, label);
    }

    protected static void personalizeCSS(BSContainer<?> footer) {
        footer.add(new ClassAttributeModifier() {
            @Override
            protected Set<String> update(Set<String> oldClasses) {
                oldClasses.remove("text-right");
                return oldClasses;
            }
        });
    }

    public static String defineLabel(WicketBuildContext ctx) {
        SType<?> type = ctx.getCurrentInstance().getType();
        AbstractSViewListWithControls<?> view = (AbstractSViewListWithControls<?>) ctx.getView();
        return view.label().orElse(
            Optional.ofNullable(Optional.ofNullable(type.asAtr().getItemLabel()).orElseGet(() -> type.asAtr().getLabel()))
                .map((x) -> {
                    String[] parts = x.trim().split(" ");
                    return "Adicionar " + parts[0];
                })
                .orElse("Adicionar item"));
    }

    protected void addInitialNumberOfLines(SType<?> currentType, SIList<?> list, ISupplier<? extends AbstractSViewListWithControls<?>> viewSupplier) {
        final AbstractSViewListWithControls<?> view = viewSupplier.get();
        if (currentType.isList() && list.isEmpty()) {
            for (int i = 0; i < view.getInitialNumberOfLines(); i++) {
                list.addNew();
            }
        }
    }
}