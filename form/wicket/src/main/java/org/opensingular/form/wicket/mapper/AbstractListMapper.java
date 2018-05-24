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
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.DefaultItemReuseStrategy;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.view.AbstractSViewListWithControls;
import org.opensingular.form.view.SViewListByTable;
import org.opensingular.form.wicket.IWicketComponentMapper;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.feedback.SValidationFeedbackPanel;
import org.opensingular.form.wicket.mapper.components.ConfirmationModal;
import org.opensingular.form.wicket.model.SInstanceListItemModel;
import org.opensingular.form.wicket.repeater.PathInstanceItemReuseStrategy;
import org.opensingular.form.wicket.util.WicketFormProcessing;
import org.opensingular.form.wicket.util.WicketFormUtils;
import org.opensingular.lib.commons.lambda.IFunction;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.wicket.util.ajax.ActionAjaxButton;
import org.opensingular.lib.wicket.util.behavior.FadeInOnceBehavior;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.bootstrap.layout.TemplatePanel;
import org.opensingular.lib.wicket.util.jquery.JQuery;
import org.opensingular.lib.wicket.util.resource.DefaultIcons;
import org.opensingular.lib.wicket.util.scripts.Scripts;
import org.opensingular.lib.wicket.util.util.Shortcuts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;

public abstract class AbstractListMapper implements IWicketComponentMapper {

    protected static AddButton appendAddButton(
        final IModel<SIList<SInstance>> mList,
        final Form<?> form,
        final WicketBuildContext ctx,
        final BSContainer<?> cell,
        boolean footer) {

        AddButton btn = new AddButton("_add", form, ctx, mList);
        cell.newTemplateTag(t -> ""
            + "<button"
            + " wicket:id='_add'"
            + " class='btn btn-sm " + (footer ? "" : "pull-right") + "'"
            + " style='" + MapperCommons.BUTTON_STYLE + ";"
            + (footer ? "margin-top:3px;margin-right:7px;" : "") + "'><i style='" + MapperCommons.ICON_STYLE + "' class='" + DefaultIcons.PLUS + "'></i>"
            + "</button>").add(btn);

        return btn;
    }

    protected static InserirButton appendInserirButton(ElementsView elementsView, Form<?> form, WicketBuildContext ctx, Item<SInstance> item, BSContainer<?> cell) {
        InserirButton btn = new InserirButton("_inserir_", elementsView, form, ctx, item);
        cell
            .newTemplateTag(tp -> ""
                + "<button"
                + " wicket:id='_inserir_'"
                + " class='btn btn-sm'"
                + " style='" + MapperCommons.BUTTON_STYLE + ";margin-top:3px;'><i style='" + MapperCommons.ICON_STYLE + "' class='" + DefaultIcons.PLUS + "'></i>"
                + "</button>")
            .add(btn);
        return btn;
    }

    protected static RemoverButton appendRemoverButton(ElementsView elementsView, Form<?> form, WicketBuildContext ctx, Item<SInstance> item,
        BSContainer<?> cell, ConfirmationModal confirmationModal, ISupplier<SViewListByTable> viewSupplier) {
        RemoverButton btn = new RemoverButton("_remover_", form, ctx, elementsView, item, confirmationModal);

        cell
            .newTemplateTag(tp -> ""
                + "<button wicket:id='_remover_' class='singular-remove-btn'>"
                + "     <i "
                + "      style='" + MapperCommons.ICON_STYLE + " 'class='" + DefaultIcons.REMOVE + "' />"
                + "</button>")
            .add(btn);

        btn.add($b.onConfigure(c -> viewSupplier.optional()
            .ifPresent(view -> c.setVisible(view.isDeleteEnabled(item.getModelObject())))));
        return btn;
    }

    @SuppressWarnings("unchecked")
    protected static void buildFooter(BSContainer<?> footer,
        Form<?> form,
        WicketBuildContext ctx) {
        Factory createAddButton = () -> new AddButton("_add", form, ctx, (IModel<SIList<SInstance>>) ctx.getModel());
        buildFooter(footer, ctx, createAddButton);

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
        return ((AbstractSViewListWithControls<?>) ctx.getView()).isNewEnabled((SIList<?>) ctx.getModel().getObject())
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

    protected static abstract class ElementsView extends RefreshingView<SInstance> {

        private final WebMarkupContainer        parentContainer;
        private IFunction<Component, Component> renderedChildFunction = c -> c;

        public ElementsView(String id, IModel<SIList<SInstance>> model, WebMarkupContainer parentContainer) {
            super(id, model);
            this.parentContainer = parentContainer;
            setItemReuseStrategy(DefaultItemReuseStrategy.getInstance());
        }

        public ElementsView setRenderedChildFunction(IFunction<Component, Component> renderedChildFunction) {
            this.renderedChildFunction = renderedChildFunction;
            return this;
        }

        @Override
        protected Iterator<IModel<SInstance>> getItemModels() {
            List<IModel<SInstance>> list = new ArrayList<>();
            SIList<SInstance> sList = getModelObject();
            for (int i = 0; i < sList.size(); i++) {
                list.add(new SInstanceListItemModel<>(getDefaultModel(), i));
            }
            return list.iterator();
        }

        @SuppressWarnings("unchecked")
        public SIList<SInstance> getModelObject() {
            return (SIList<SInstance>) getDefaultModelObject();
        }

        @SuppressWarnings("unchecked")
        public IModel<SIList<SInstance>> getModel() {
            return (IModel<SIList<SInstance>>) getDefaultModel();
        }

        @Override
        protected void onBeforeRender() {
            WicketFormProcessing.onFormPrepare(this, this.getModel(), false);
            super.onBeforeRender();
        }

        public void insertItem(AjaxRequestTarget target, int index) {
            try {
                setItemReuseStrategy(new PathInstanceItemReuseStrategy());
                SInstance newInstance = getModelObject().addNewAt(index);

                for (Component child : this) {
                    updateChildModelIndex(index, child, 1);
                }

                this.onPopulate();

                findChildByInstance(newInstance).ifPresent(item -> {
                    Component child = renderedChildFunction.apply(item);
                    target.prependJavaScript(getInsertPreScript(index, "\"<div id='" + child.getMarkupId() + "'></div>\""));
                    target.add(child.add(new FadeInOnceBehavior()));
                });
            } finally {
                setItemReuseStrategy(DefaultItemReuseStrategy.getInstance());
            }
        }

        private void updateChildModelIndex(int index, Component child, int increment) {
            IModel<?> childModel = child.getDefaultModel();
            if (childModel instanceof SInstanceListItemModel<?>) {
                SInstanceListItemModel<?> itemModel = (SInstanceListItemModel<?>) childModel;
                if (itemModel.getIndex() >= index) {
                    int newIndex = itemModel.getIndex() + increment;
                    itemModel.setIndex(newIndex);
                    ((Item<?>) child).setIndex(newIndex);
                }
            }
        }

        public void removeItem(AjaxRequestTarget target, Item<SInstance> item) {
            try {
                setItemReuseStrategy(new PathInstanceItemReuseStrategy());
                final SInstance instance = item.getModelObject();
                final SIList<SInstance> list = getModelObject();
                final int index = list.indexOf(instance);

                list.remove(instance);

                //update current children model indexes
                for (Component child : this) {
                    updateChildModelIndex(index, child, -1);
                }

                Component child = renderedChildFunction.apply(item);
                target.appendJavaScript(JQuery.$(child).append(".fadeOut(200,function(){$(this).remove();});"));
                item.remove();
            } finally {
                setItemReuseStrategy(DefaultItemReuseStrategy.getInstance());
            }
        }

        private CharSequence getInsertPreScript(int index, String emptyMarkupString) {
            final StringBuilder $parent = JQuery.$(parentContainer);
            if (index == 0) {
                return $parent.append(".prepend(").append(emptyMarkupString).append(");");
            }
            return findChildByInstance(getModelObject().get(index - 1))
                .map(component -> $parent.append(".find('#")
                    .append(renderedChildFunction.apply(component).getMarkupId())
                    .append("').after(").append(emptyMarkupString).append(");"))
                .orElseGet(() -> $parent.append(".append(").append(emptyMarkupString).append(");"));
        }

        private Optional<Component> findChildByInstance(SInstance instance) {
            return findChildByInstance(this, instance);
        }

        private Optional<Component> findChildByInstance(Iterable<Component> container, SInstance instance) {
            final SIList<SInstance> instances = this.getModelObject();
            int index = instances.indexOf(instance);
            if (index >= 0) {
                for (Component child : container) {
                    SInstance childInstance = (SInstance) child.getDefaultModelObject();
                    if (Objects.equals(instance.getPathFull(), childInstance.getPathFull())) {
                        return Optional.of(child);
                    }
                }
            }
            return Optional.empty();
        }
    }

    protected static class InserirButton extends ActionAjaxButton {
        private final WicketBuildContext ctx;private final Item<SInstance> item;
        private final ElementsView elementsView;

        protected InserirButton(String id, ElementsView elementsView, Form<?> form, WicketBuildContext ctx, Item<SInstance> item) {
            super(id, form);
            this.setDefaultFormProcessing(false);
            this.ctx = ctx;
            this.elementsView = elementsView;
            this.item = item;
            add($b.attr("title", "Nova Linha"));
        }

        @Override
        protected void onAction(AjaxRequestTarget target, Form<?> form) {
            elementsView.insertItem(target, item.getIndex());
            target.focusComponent(this);
            WicketFormProcessing.onFieldProcess(ctx.getContainer(), target, ctx.getModel());
        }
    }

    protected static class RemoverButton extends ActionAjaxButton {
        private final WicketBuildContext ctx;private final ElementsView elementsView;
        private final Item<SInstance> item;
        private final ConfirmationModal confirmationModal;

        protected RemoverButton(String id, Form<?> form, WicketBuildContext ctx, ElementsView elementsView, Item<SInstance> item, ConfirmationModal confirmationModal) {
            super(id, form);
            this.setOutputMarkupId(true);
            this.setDefaultFormProcessing(false);
            this.ctx = ctx;
            this.elementsView = elementsView;
            this.item = item;
            add($b.attr("title", "Remover Linha"));

            this.confirmationModal = confirmationModal;
        }

        @Override
        protected void onAction(AjaxRequestTarget target, Form<?> form) {
            target.add(WicketFormUtils.findUpdatableComponentInHierarchy(confirmationModal));
            confirmationModal.show(target, this::removeItem);
        }

        private void removeItem(AjaxRequestTarget target) {
            elementsView.removeItem(target, item);
            target.appendJavaScript(JQuery.$(this).append(".prop('disabled',true);"));
            if (elementsView.getModelObject().isEmpty()) {
                target.add(this.getForm());
            }
            WicketFormProcessing.onFieldProcess(ctx.getContainer(), target, ctx.getModel());
        }
    }

    protected static final class AddButton extends AjaxLink<String> {

        private final Form<?> form;
        private final IModel<SIList<SInstance>> listModel;
        private final WicketBuildContext        ctx;

        public AddButton(String id, Form<?> form, WicketBuildContext ctx, IModel<SIList<SInstance>> mList) {
            super(id);
            this.form = form;
                        this.ctx = ctx;
            this.listModel = mList;
            add($b.attr("title", "Adicionar Linha"));
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            final SIList<SInstance> list = listModel.getObject();
            if (list.getType().getMaximumSize() != null && list.getType().getMaximumSize() == list.size()) {
                target.appendJavaScript(";bootbox.alert('A quantidade máxima de valores foi atingida.');");
                target.appendJavaScript(Scripts.multipleModalBackDrop());
            } else {
                list.addNew();
                target.add(form);
                target.focusComponent(this);
            }
            WicketFormProcessing.onFieldProcess(ctx.getContainer(), target, ctx.getModel());
        }
    }
}