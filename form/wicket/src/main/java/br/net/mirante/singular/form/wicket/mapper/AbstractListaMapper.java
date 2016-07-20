/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.Factory;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.IItemFactory;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.SIList;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.STypeList;
import br.net.mirante.singular.form.view.AbstractSViewListWithControls;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.model.SInstanceItemListaModel;
import br.net.mirante.singular.form.wicket.repeater.PathInstanceItemReuseStrategy;
import br.net.mirante.singular.form.wicket.util.WicketFormProcessing;
import br.net.mirante.singular.util.wicket.ajax.ActionAjaxButton;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.TemplatePanel;
import br.net.mirante.singular.util.wicket.resource.Icone;

public abstract class AbstractListaMapper implements IWicketComponentMapper {

    protected static AddButton appendAddButton(final IModel<SIList<SInstance>> mLista, final Form<?> form,
                                               final BSContainer<?> cell, boolean footer) {
        AddButton btn = new AddButton("_add", form, mLista);
        cell.newTemplateTag(t -> ""
                + "<button"
                + " wicket:id='_add'"
                + " class='btn btn-sm " + (footer ? "" : "pull-right") + "'"
                + " style='" + MapperCommons.BUTTON_STYLE + ";"
                + (footer ? "margin-top:3px;margin-right:7px;" : "") + "'><i style='" + MapperCommons.ICON_STYLE + "' class='" + Icone.PLUS + "'></i>"
                + "</button>"
        ).add(btn);

        return btn;
    }

    protected static InserirButton appendInserirButton(ElementsView elementsView, Form<?> form, Item<SInstance> item, BSContainer<?> cell) {
        InserirButton btn = new InserirButton("_inserir_", elementsView, form, elementsView.getModel(), item);
        cell
                .newTemplateTag(tp -> ""
                        + "<button"
                        + " wicket:id='_inserir_'"
                        + " class='btn btn-success btn-sm'"
                        + " style='" + MapperCommons.BUTTON_STYLE + ";margin-top:3px;'><i style='" + MapperCommons.ICON_STYLE + "' class='" + Icone.PLUS + "'></i>"
                        + "</button>")
                .add(btn);
        return btn;
    }

    protected static RemoverButton appendRemoverButton(ElementsView elementsView, Form<?> form, Item<SInstance> item, BSContainer<?> cell) {
        RemoverButton btn = new RemoverButton("_remover_", form, elementsView, item);
        cell
                .newTemplateTag(tp -> ""
                        + "<button wicket:id='_remover_' class='singular-remove-btn'>"
                        + "     <i "
                        + "      style='" + MapperCommons.ICON_STYLE + " 'class='" + Icone.REMOVE + "' />"
                        + "</button>")
                .add(btn);
        return btn;
    }

    protected static abstract class ElementsView extends RefreshingView<SInstance> {

        public ElementsView(String id, IModel<SIList<SInstance>> model) {
            super(id, model);
            setItemReuseStrategy(new PathInstanceItemReuseStrategy());
        }

        @Override
        protected Iterator<IModel<SInstance>> getItemModels() {
            List<IModel<SInstance>> list  = new ArrayList<>();
            SIList<SInstance>       sList = getModelObject();
            for (int i = 0; i < sList.size(); i++)
                list.add(new SInstanceItemListaModel<>(getDefaultModel(), i));
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
        protected IItemFactory<SInstance> newItemFactory() {
            IItemFactory<SInstance> factory = super.newItemFactory();
            return new IItemFactory<SInstance>() {
                @Override
                public Item<SInstance> newItem(int index, IModel<SInstance> model) {
                    Item<SInstance> item = factory.newItem(index, model);
                    WicketFormProcessing.onFormPrepare(item, model, false);
                    return item;
                }
            };
        }
    }

    protected static class InserirButton extends ActionAjaxButton {
        private final IModel<SIList<SInstance>> modelLista;
        private final Item<SInstance>           item;
        private final ElementsView              elementsView;

        protected InserirButton(String id, ElementsView elementsView, Form<?> form, IModel<SIList<SInstance>> mLista, Item<SInstance> item) {
            super(id, form);
            this.setDefaultFormProcessing(false);
            this.elementsView = elementsView;
            this.modelLista = mLista;
            this.item = item;
            add($b.attr("title", "Nova Linha"));
        }

        @Override
        protected void onAction(AjaxRequestTarget target, Form<?> form) {
            final int         index = item.getIndex();
            SIList<SInstance> lista = modelLista.getObject();
            lista.addNewAt(index);
            List<SInstanceItemListaModel<?>> itemModels = new ArrayList<>();
            for (Component child : elementsView) {
                IModel<?> childModel = child.getDefaultModel();
                if (childModel instanceof SInstanceItemListaModel<?>)
                    itemModels.add((SInstanceItemListaModel<?>) childModel);
            }
            for (SInstanceItemListaModel<?> itemModel : itemModels)
                if (itemModel.getIndex() >= index)
                    itemModel.setIndex(itemModel.getIndex() + 1);
            target.add(form);
            target.focusComponent(this);
        }
    }

    protected static class RemoverButton extends ActionAjaxButton {
        private final ElementsView    elementsView;
        private final Item<SInstance> item;

        protected RemoverButton(String id, Form<?> form, ElementsView elementsView, Item<SInstance> item) {
            super(id, form);
            this.setDefaultFormProcessing(false);
            this.elementsView = elementsView;
            this.item = item;
            add($b.attr("title", "Remover Linha"));
        }

        @Override
        protected void onAction(AjaxRequestTarget target, Form<?> form) {
            final int         index = item.getIndex();
            SIList<SInstance> lista = elementsView.getModelObject();
            lista.remove(index);
            List<SInstanceItemListaModel<?>> itemModels = new ArrayList<>();
            for (Component child : elementsView) {
                IModel<?> childModel = child.getDefaultModel();
                if (childModel instanceof SInstanceItemListaModel<?>)
                    itemModels.add((SInstanceItemListaModel<?>) childModel);
            }
            for (SInstanceItemListaModel<?> itemModel : itemModels)
                if (itemModel.getIndex() > index)
                    itemModel.setIndex(itemModel.getIndex() - 1);
                else if (itemModel.getIndex() == index)
                    itemModel.setIndex(Integer.MAX_VALUE);
            target.add(form);
        }
    }

    protected static final class AddButton extends ActionAjaxButton {
        private final IModel<SIList<SInstance>> modelLista;

        public AddButton(String id, Form<?> form, IModel<SIList<SInstance>> mLista) {
            super(id, form);
            this.setDefaultFormProcessing(false);
            modelLista = mLista;
            add($b.attr("title", "Adicionar Linha"));
        }

        @Override
        protected void onAction(AjaxRequestTarget target, Form<?> form) {
            final SIList<SInstance> lista = modelLista.getObject();
            if (lista.getType().getMaximumSize() != null && lista.getType().getMaximumSize() == lista.size()) {
                target.appendJavaScript(";bootbox.alert('A Quantidade máxima de valores foi atingida.');");
            } else {
                lista.addNew();
                target.add(form);
                target.focusComponent(this);
            }
        }

    }

    protected void addMinimumSize(SType<?> currentType, SIList<?> list) {
        if (currentType instanceof STypeList && list.isEmpty()) {
            final STypeList<?,?> tl = (STypeList<?,?>) currentType;
            if (tl.getMinimumSize() != null) {
                for (int i = 0; i < tl.getMinimumSize(); i++) {
                    list.addNew();
                }
            } else if(tl.isRequired()){
                list.addNew();
            }
        }
    }


    protected static void buildFooter(BSContainer<?> footer,
                                      Form<?> form,
                                      WicketBuildContext ctx) {
        Factory createAddButton = () -> new AddButton("_add", form, (IModel<SIList<SInstance>>) ctx.getModel());
        buildFooter(footer, ctx, createAddButton);
    }

    public static void buildFooter(BSContainer<?> footer, WicketBuildContext ctx, Factory createAddButton) {
        if (canAddItems(ctx)) {
            final TemplatePanel template = footer.newTemplateTag(tp -> createButtonMarkup(ctx));
            template.add((Component) createAddButton.create());
        } else {
            footer.setVisible(false);
        }

        personalizeCSS(footer);
    }

    protected static boolean canAddItems(WicketBuildContext ctx) {
        return ((AbstractSViewListWithControls<?>) ctx.getView()).isNewEnabled()
                && ctx.getViewMode().isEdition();
    }

    public static String definirLabel(WicketBuildContext ctx) {
        SType<?> type = ctx.getCurrentInstance().getType();
        AbstractSViewListWithControls<?> view = (AbstractSViewListWithControls<?>) ctx.getView();
        return (String) view.label().orElse(
                Optional.ofNullable(Optional.ofNullable(type.asAtr().getItemLabel()).orElseGet(()->type.asAtr().getLabel()))
                        .map((x) -> {
                            String[] parts = x.trim().split(" ");
                            return "Adicionar " + parts[0];
                        })
                        .orElse("Adicionar item")
        );
    }

    protected static String createButtonMarkup(WicketBuildContext ctx) {
        String label = definirLabel(ctx);

        return String.format("<button wicket:id=\"_add\" class=\"btn btn-add\" type=\"button\" title=\"%s\"><i class=\"fa fa-plus\"></i>%s</button>", label, label);
    }

    protected static void personalizeCSS(BSContainer<?> footer) {
        footer.add(new ClassAttributeModifier() {
            protected Set<String> update(Set<String> oldClasses) {
                oldClasses.remove("text-right");
                return oldClasses;
            }
        });
    }
}