package br.net.mirante.singular.form.wicket.mapper;

import static br.net.mirante.singular.util.wicket.util.Shortcuts.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.basic.view.MTableListaView;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper;
import br.net.mirante.singular.form.wicket.UIBuilderWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.model.MInstanciaCampoModel;
import br.net.mirante.singular.form.wicket.model.MInstanciaItemListaModel;
import br.net.mirante.singular.util.wicket.ajax.ActionAjaxButton;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.IBSGridCol.BSGridSize;
import br.net.mirante.singular.util.wicket.bootstrap.layout.TemplatePanel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.table.BSTRow;
import br.net.mirante.singular.util.wicket.bootstrap.layout.table.BSTSection;
import br.net.mirante.singular.util.wicket.resource.Icone;

public class TableListaMapper implements IWicketComponentMapper {
    @Override
    @SuppressWarnings("unchecked")
    public void buildView(WicketBuildContext ctx, MView view, IModel<? extends MInstancia> model) {
        final IModel<MILista<MInstancia>> mLista = $m.get(() -> (MILista<MInstancia>) model.getObject());
        final MILista<?> iLista = mLista.getObject();
        final IModel<String> label = $m.ofValue(trimToEmpty(iLista.as(MPacoteBasic.aspect()).getLabel()));

        ctx.setHint(ControlsFieldComponentMapper.NO_DECORATION, true);

        final BSContainer<?> parentCol = ctx.getContainer();
        if (isNotBlank(label.getObject()))
            parentCol.appendTag("h3", new Label("_title", label));

        final TemplatePanel template = parentCol.newTemplateTag(t -> ""
            + "<form wicket:id='form'>"
            + "<table wicket:id='table' class='table table-condensed table-unstyled'>"
            + "<thead wicket:id='head'></thead>"
            + "<tbody><wicket:container wicket:id='items'><tr wicket:id='row'></tr></wicket:container></tbody>"
            + "<tfoot wicket:id='footer'>"
            + "<tr>"
            + "<td colspan='99'>"
            + "<button"
            + " wicket:id='_adicionar_'"
            + " class='btn btn-success btn-sm'"
            + " style='padding:5px 3px 1px;margin-top:3px;margin-right:7px;'><i class='" + Icone.PLUS + "'></i>"
            + "</button>"
            + "</td>"
            + "</tr>"
            + "</tfoot>"
            + "</table>"
            + "</form>");
        final Form<?> form = new Form<>("form");
        final WebMarkupContainer table = new WebMarkupContainer("table");
        final TableListaMapper.TRsView trView = new TRsView("items", mLista, ctx, view, form);
        final BSTSection thead = new BSTSection("head").setTagName("thead");
        final WebMarkupContainer footer = new WebMarkupContainer("footer");

        form.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);

        final MTipo<?> tElementos = iLista.getTipoElementos();
        if (iLista.getTipoElementos() instanceof MTipoComposto<?>) {
            MTipoComposto<MIComposto> tComposto = (MTipoComposto<MIComposto>) tElementos;
            BSTRow tr = thead.newRow();
            if ((view instanceof MTableListaView) && (((MTableListaView) view).isPermiteInsercaoDeLinha())) {
                tr.newTHeaderCell($m.ofValue(""));
            }
            for (String nomeCampo : tComposto.getCampos()) {
                final MTipo<?> tCampo = tComposto.getCampo(nomeCampo);
                tr.newTHeaderCell($m.ofValue(tCampo.as(AtrBasic::new).getLabel()));
            }
        } else {
            thead.setVisible(false);
        }

        if ((view instanceof MTableListaView) && ((MTableListaView) view).isPermiteAdicaoDeLinha()) {
            AdicionarButton btn = new AdicionarButton("_adicionar_", form, mLista);
            footer.add(btn);
            if (!((MTableListaView) view).isPermiteInsercaoDeLinha()) {
                btn.add($b.classAppender("pull-right"));
            }
        } else {
            footer.setVisible(false);
        }

        template
            .add(form
                .add(table
                    .add(thead)
                    .add(trView)
                    .add(footer)));
    }

    private static final class TRsView extends RefreshingView<MInstancia> {
        private WicketBuildContext ctx;
        private MView              view;
        private Form<?>            form;
        private TRsView(String id, IModel<MILista<MInstancia>> model, WicketBuildContext ctx, MView view, Form<?> form) {
            super(id, model);
            setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance());
            this.ctx = ctx;
            this.view = view;
            this.form = form;
        }
        @Override
        protected Iterator<IModel<MInstancia>> getItemModels() {
            List<IModel<MInstancia>> list = new ArrayList<>();
            MILista<MInstancia> miLista = getModelObject();
            for (int i = 0; i < miLista.size(); i++)
                list.add(new MInstanciaItemListaModel<>(getDefaultModel(), i));
            return list.iterator();
        }
        @SuppressWarnings("unchecked")
        private MILista<MInstancia> getModelObject() {
            return (MILista<MInstancia>) getDefaultModelObject();
        }
        @SuppressWarnings("unchecked")
        private IModel<MILista<MInstancia>> getModel() {
            return (IModel<MILista<MInstancia>>) getDefaultModel();
        }
        @Override
        @SuppressWarnings("unchecked")
        protected void populateItem(Item<MInstancia> item) {
            final IModel<MInstancia> itemModel = item.getModel();
            final BSTRow tr = new BSTRow("row", BSGridSize.MD);
            item.add(tr);

            if ((view instanceof MTableListaView) && (((MTableListaView) view).isPermiteInsercaoDeLinha())) {
                tr.newCol()
                    .newTemplateTag(tp -> ""
                        + "<button"
                        + " wicket:id='_inserir_'"
                        + " class='btn btn-success btn-sm'"
                        + " style='padding:5px 3px 1px;margin-top:3px;'><i class='" + Icone.PLUS + "'></i>"
                        + "</button>")
                    .add(new InserirButton("_inserir_", form, TRsView.this.getModel(), item));
            }

            MInstancia instancia = itemModel.getObject();
            if (instancia instanceof MIComposto) {
                MIComposto composto = (MIComposto) instancia;
                MTipoComposto<MIComposto> tComposto = (MTipoComposto<MIComposto>) composto.getMTipo();
                for (String nomeCampo : tComposto.getCampos()) {
                    final MTipo<?> tCampo = tComposto.getCampo(nomeCampo);
                    final MInstanciaCampoModel<MInstancia> mCampo = new MInstanciaCampoModel<>(itemModel, tCampo.getNomeSimples());
                    UIBuilderWicket.buildForEdit(ctx.createChild(tr.newCol(), true), mCampo);
                }
            } else {
                UIBuilderWicket.buildForEdit(ctx.createChild(tr.newCol(), true), itemModel);
            }

            if (view instanceof MTableListaView) {
                MTableListaView tableView = (MTableListaView) view;
                if (tableView.isPermiteExclusaoDeLinha()) {
                    tr.newCol()
                        .newTemplateTag(tp -> ""
                            + "<button"
                            + " wicket:id='_remover_'"
                            + " class='btn btn-danger btn-sm'"
                            + " style='padding:5px 3px 1px;margin-top:3px;'><i class='" + Icone.MINUS + "'></i>"
                            + "</button>")
                        .add(new RemoverButton("_remover_", form, TRsView.this.getModel(), item));
                }
            }
        }
        private final class InserirButton extends ActionAjaxButton {
            private final IModel<MILista<MInstancia>> modelLista;
            private final Item<MInstancia>            item;
            private InserirButton(String id, Form<?> form, IModel<MILista<MInstancia>> mLista, Item<MInstancia> item) {
                super(id, form);
                this.setDefaultFormProcessing(false);
                this.modelLista = mLista;
                this.item = item;
            }
            @Override
            protected void onAction(AjaxRequestTarget target, Form<?> form) {
                final int index = item.getIndex();
                MILista<MInstancia> lista = modelLista.getObject();
                lista.addNovoAt(index);
                List<MInstanciaItemListaModel<?>> itemModels = new ArrayList<>();
                for (Component child : TRsView.this) {
                    IModel<?> childModel = child.getDefaultModel();
                    if (childModel instanceof MInstanciaItemListaModel<?>)
                        itemModels.add((MInstanciaItemListaModel<?>) childModel);
                }
                for (MInstanciaItemListaModel<?> itemModel : itemModels)
                    if (itemModel.getIndex() >= index)
                        itemModel.setIndex(itemModel.getIndex() + 1);
                target.add(form);
                target.focusComponent(this);
            }
        }
        private final class RemoverButton extends ActionAjaxButton {
            private final IModel<MILista<MInstancia>> modelLista;
            private final Item<MInstancia>            item;
            private RemoverButton(String id, Form<?> form, IModel<MILista<MInstancia>> mLista, Item<MInstancia> item) {
                super(id, form);
                this.setDefaultFormProcessing(false);
                this.modelLista = mLista;
                this.item = item;
            }
            @Override
            protected void onAction(AjaxRequestTarget target, Form<?> form) {
                final int index = item.getIndex();
                MILista<MInstancia> lista = modelLista.getObject();
                lista.remove(index);
                List<MInstanciaItemListaModel<?>> itemModels = new ArrayList<>();
                for (Component child : TRsView.this) {
                    IModel<?> childModel = child.getDefaultModel();
                    if (childModel instanceof MInstanciaItemListaModel<?>)
                        itemModels.add((MInstanciaItemListaModel<?>) childModel);
                }
                for (MInstanciaItemListaModel<?> itemModel : itemModels)
                    if (itemModel.getIndex() > index)
                        itemModel.setIndex(itemModel.getIndex() - 1);
                    else if (itemModel.getIndex() == index)
                        itemModel.setIndex(Integer.MAX_VALUE);
                target.add(form);
            }
        }
    }

    private static final class AdicionarButton extends ActionAjaxButton {
        private final IModel<MILista<MInstancia>> modelLista;
        private AdicionarButton(String id, Form<?> form, IModel<MILista<MInstancia>> mLista) {
            super(id, form);
            this.setDefaultFormProcessing(false);
            modelLista = mLista;
        }
        @Override
        protected void onAction(AjaxRequestTarget target, Form<?> form) {
            MILista<MInstancia> lista = modelLista.getObject();
            lista.addNovo();
            target.add(form);
            target.focusComponent(this);
        }
    }
}