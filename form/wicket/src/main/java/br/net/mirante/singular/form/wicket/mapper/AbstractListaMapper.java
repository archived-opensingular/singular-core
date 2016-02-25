package br.net.mirante.singular.form.wicket.mapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.IItemFactory;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SList;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeLista;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper;
import br.net.mirante.singular.form.wicket.model.SInstanceItemListaModel;
import br.net.mirante.singular.form.wicket.repeater.PathInstanceItemReuseStrategy;
import br.net.mirante.singular.form.wicket.util.WicketFormProcessing;
import br.net.mirante.singular.util.wicket.ajax.ActionAjaxButton;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.resource.Icone;

public abstract class AbstractListaMapper implements IWicketComponentMapper {

    protected static AddButton appendAddButton(final IModel<SList<SInstance>> mLista, final Form<?> form,
                                               final BSContainer<?> cell, boolean footer) {
        AddButton btn = new AddButton("_add", form, mLista);
        cell.newTemplateTag(t -> ""
                        + "<button"
                        + " wicket:id='_add'"
                        + " class='btn btn-success btn-sm " + (footer ? "" : "pull-right") + "'"
                        + " style='padding:5px 3px 1px;"
                        + (footer ? "margin-top:3px;margin-right:7px;" : "") + "'><i class='" + Icone.PLUS + "'></i>"
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
                        + " style='padding:5px 3px 1px;margin-top:3px;'><i class='" + Icone.PLUS + "'></i>"
                        + "</button>")
                .add(btn);
        return btn;
    }

    protected static RemoverButton appendRemoverButton(ElementsView elementsView, Form<?> form, Item<SInstance> item, BSContainer<?> cell) {
        RemoverButton btn = new RemoverButton("_remover_", form, elementsView, item);
        cell
                .newTemplateTag(tp -> ""
                        + "<button"
                        + " wicket:id='_remover_'"
                        + " class='btn btn-danger btn-sm'"
                        + " style='padding:5px 3px 1px;margin-top:3px;'><i class='" + Icone.MINUS + "'></i>"
                        + "</button>")
                .add(btn);
        return btn;
    }

    protected static abstract class ElementsView extends RefreshingView<SInstance> {

        public ElementsView(String id, IModel<SList<SInstance>> model) {
            super(id, model);
            setItemReuseStrategy(new PathInstanceItemReuseStrategy());
        }

        @Override
        protected Iterator<IModel<SInstance>> getItemModels() {
            List<IModel<SInstance>> list = new ArrayList<>();
            SList<SInstance> sList = getModelObject();
            for (int i = 0; i < sList.size(); i++)
                list.add(new SInstanceItemListaModel<>(getDefaultModel(), i));
            return list.iterator();
        }

        @SuppressWarnings("unchecked")
        public SList<SInstance> getModelObject() {
            return (SList<SInstance>) getDefaultModelObject();
        }

        @SuppressWarnings("unchecked")
        public IModel<SList<SInstance>> getModel() {
            return (IModel<SList<SInstance>>) getDefaultModel();
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
        private final IModel<SList<SInstance>> modelLista;
        private final Item<SInstance> item;
        private final ElementsView elementsView;

        private InserirButton(String id, ElementsView elementsView, Form<?> form, IModel<SList<SInstance>> mLista, Item<SInstance> item) {
            super(id, form);
            this.setDefaultFormProcessing(false);
            this.elementsView = elementsView;
            this.modelLista = mLista;
            this.item = item;
        }

        @Override
        protected void onAction(AjaxRequestTarget target, Form<?> form) {
            final int index = item.getIndex();
            SList<SInstance> lista = modelLista.getObject();
            lista.addNovoAt(index);
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
        private final ElementsView elementsView;
        private final Item<SInstance> item;

        private RemoverButton(String id, Form<?> form, ElementsView elementsView, Item<SInstance> item) {
            super(id, form);
            this.setDefaultFormProcessing(false);
            this.elementsView = elementsView;
            this.item = item;
        }

        @Override
        protected void onAction(AjaxRequestTarget target, Form<?> form) {
            final int index = item.getIndex();
            SList<SInstance> lista = elementsView.getModelObject();
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
        private final IModel<SList<SInstance>> modelLista;

        private AddButton(String id, Form<?> form, IModel<SList<SInstance>> mLista) {
            super(id, form);
            this.setDefaultFormProcessing(false);
            modelLista = mLista;
        }

        @Override
        protected void onAction(AjaxRequestTarget target, Form<?> form) {
            final SList<SInstance> lista = modelLista.getObject();
            if (lista.getType().getMaximumSize() != null && lista.getType().getMaximumSize() == lista.size()) {
                target.appendJavaScript(";bootbox.alert('A Quantidade máxima de valores foi atingida.');");
            } else {
                lista.addNovo();
                target.add(form);
                target.focusComponent(this);
            }
        }

    }

    protected void addMinimumSize(SType<?> currentType, SList<?> list) {
        if (currentType instanceof STypeLista && list.isEmpty()) {
            final STypeLista tl = (STypeLista) currentType;
            if (tl.getMinimumSize() != null) {
                for (int i = 0; i < tl.getMinimumSize(); i++) {
                    list.addNovo();
                }
            }
        }
    }
}