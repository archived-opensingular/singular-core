package org.opensingular.form.wicket.mapper.buttons;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.DefaultItemReuseStrategy;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.view.AbstractSViewListWithControls;
import org.opensingular.form.wicket.mapper.components.ConfirmationModal;
import org.opensingular.form.wicket.model.SInstanceListItemModel;
import org.opensingular.form.wicket.repeater.PathInstanceItemReuseStrategy;
import org.opensingular.form.wicket.util.WicketFormProcessing;
import org.opensingular.lib.commons.lambda.IFunction;
import org.opensingular.lib.wicket.util.behavior.FadeInOnceBehavior;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.jquery.JQuery;

public abstract class ElementsView extends RefreshingView<SInstance> {

    private final WebMarkupContainer parentContainer;
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

    private Optional<Component> findChildByInstance(Iterable<org.apache.wicket.Component> container, SInstance
            instance) {
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

    //Buttons
    protected AddButton appendAddButton(final IModel<SIList<SInstance>> mList, final Form<?> form,
            final BSContainer<?> cell, boolean footer) {
        return new AddButton("_add", form, mList).createAddButton(cell, footer);

    }

    protected RemoverButton appendRemoverButton(ElementsView elementsView, Form<?> form, Item<SInstance> item,
            BSContainer<?> cell, ConfirmationModal confirmationModal, AbstractSViewListWithControls viewListByTable) {
        return new RemoverButton("_remover_", form, elementsView,item,confirmationModal).createRemoverButton(cell, viewListByTable);
    }

    protected InserirButton appendInserirButton(ElementsView elementsView, Form<?> form, Item<SInstance> item,
            BSContainer<?> cell) {
        return new InserirButton("_inserir_", elementsView, form, item).createInserirButton(cell);
    }

}