package br.net.mirante.singular.form.wicket.mapper;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.basic.view.MTableListaView;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.wicket.UIBuilderWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.mapper.components.MetronicPanel;
import br.net.mirante.singular.form.wicket.model.AtributoModel;
import br.net.mirante.singular.form.wicket.model.MInstanciaCampoModel;
import br.net.mirante.singular.form.wicket.model.MTipoElementosModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.IBSGridCol.BSGridSize;
import br.net.mirante.singular.util.wicket.bootstrap.layout.TemplatePanel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.table.BSTRow;
import br.net.mirante.singular.util.wicket.bootstrap.layout.table.BSTSection;
import com.google.common.base.Strings;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

import static br.net.mirante.singular.util.wicket.util.Shortcuts.$b;
import static br.net.mirante.singular.util.wicket.util.Shortcuts.$m;

public class TableListaMapper extends AbstractListaMapper {

    @Override
    public void buildForEdit(WicketBuildContext ctx, MView view, IModel<? extends MInstancia> model){
        buildView(ctx, view, model, ViewMode.EDITION);
    }

    @Override
    public void buildForView(WicketBuildContext ctx, MView view, IModel<? extends MInstancia> model){
        buildView(ctx, view, model, ViewMode.VISUALIZATION);
    }

    @SuppressWarnings("unchecked")
    public void buildView(WicketBuildContext ctx, MView view, IModel<? extends MInstancia> model, ViewMode viewMode) {
        final IModel<MILista<MInstancia>> mLista = $m.get(() -> (MILista<MInstancia>) model.getObject());
        final IModel<String> label = new AtributoModel<>(mLista, MPacoteBasic.ATR_LABEL);

        ctx.setHint(ControlsFieldComponentMapper.NO_DECORATION, true);

        final BSContainer<?> parentCol = ctx.getContainer();

        parentCol.appendComponent(id ->
                        MetronicPanel.MetronicPanelBuilder.build(id,
                                (header, form) ->
                                        buildHeader(header, form, label),
                                (content, form) ->
                                        builContent(content, form, mLista, ctx.getUiBuilderWicket(), ctx, view, viewMode),
                                (footer, form) ->
                                        footer.setVisible(false)
                        )
        );

    }

    private void buildHeader(BSContainer<?> header, Form<?> form, IModel<String> label) {
        header.appendTag("span", new Label("_title", label));
        header.add($b.visibleIf($m.get(() -> !Strings.isNullOrEmpty(label.getObject()))));
    }

    private void builContent(BSContainer<?> content,
                             Form<?> form,
                             IModel<MILista<MInstancia>> mLista,
                             UIBuilderWicket wicketBuilder,
                             WicketBuildContext ctx,
                             MView view,
                             ViewMode viewMode) {

        final IModel<MTipo<MInstancia>> tipoElementos = new MTipoElementosModel(mLista);

        final TemplatePanel template = content.newTemplateTag(t -> ""
                + "    <table class='table table-condensed table-unstyled'>"
                + "      <thead wicket:id='_h'></thead>"
                + "      <tbody><wicket:container wicket:id='_e'><tr wicket:id='_r'></tr></wicket:container></tbody>"
                + "      <tfoot wicket:id='_ft'>"
                + "        <tr><td colspan='99' wicket:id='_fb'></td></tr>"
                + "      </tfoot>"
                + "    </table>");
        final BSTSection thead = new BSTSection("_h").setTagName("thead");
        final ElementsView trView = new TableElementsView("_e", mLista, wicketBuilder, ctx, view, form, viewMode);
        final WebMarkupContainer footer = new WebMarkupContainer("_ft");
        final BSContainer<?> footerBody = new BSContainer<>("_fb");


        final MTipo<?> tElementos = tipoElementos.getObject();
        if (tElementos instanceof MTipoComposto<?>) {
            MTipoComposto<MIComposto> tComposto = (MTipoComposto<MIComposto>) tElementos;
            BSTRow tr = thead.newRow();
            if ((view instanceof MTableListaView) && (((MTableListaView) view).isPermiteInsercaoDeLinha())) {
                tr.newTHeaderCell($m.ofValue(""));
            }
            for (MTipo<?> tCampo : tComposto.getFields()) {
                tr.newTHeaderCell($m.ofValue(tCampo.as(MPacoteBasic.aspect()).getLabel()));
            }
        } else {
            thead.setVisible(false);
        }

        if ((view instanceof MTableListaView) && ((MTableListaView) view).isPermiteAdicaoDeLinha()
                && viewMode.isEdition()) {
            AdicionarButton btn = appendAdicionarButton(mLista, form, footerBody);
            if (!((MTableListaView) view).isPermiteInsercaoDeLinha()) {
                btn.add($b.classAppender("pull-right"));
            }
        } else {
            footer.setVisible(false);
        }
        template
                .add(thead)
                .add(trView)
                .add(footer
                        .add(footerBody));
    }


    private static final class TableElementsView extends ElementsView {

        private final WicketBuildContext ctx;
        private final MView view;
        private final Form<?> form;
        private final ViewMode viewMode;
        private final UIBuilderWicket wicketBuilder;

        private TableElementsView(String id,
                                  IModel<MILista<MInstancia>> model,
                                  UIBuilderWicket wicketBuilder,
                                  WicketBuildContext ctx,
                                  MView view,
                                  Form<?> form,
                                  ViewMode viewMode) {
            super(id, model);
            this.wicketBuilder = wicketBuilder;
            this.ctx = ctx;
            this.view = view;
            this.form = form;
            this.viewMode = viewMode;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void populateItem(Item<MInstancia> item) {
            final BSTRow tr = new BSTRow("_r", BSGridSize.MD);

            if ((view instanceof MTableListaView) && (((MTableListaView) view).isPermiteInsercaoDeLinha()))
                appendInserirButton(this, form, item, tr.newCol());

            final IModel<MInstancia> itemModel = item.getModel();
            final MInstancia instancia = itemModel.getObject();
            if (instancia instanceof MIComposto) {
                MIComposto composto = (MIComposto) instancia;
                MTipoComposto<MIComposto> tComposto = (MTipoComposto<MIComposto>) composto.getMTipo();
                for (MTipo<?> tCampo : tComposto.getFields()) {
                    final MInstanciaCampoModel<MInstancia> mCampo =
                            new MInstanciaCampoModel<>(item.getModel(), tCampo.getNomeSimples());
                    wicketBuilder.build(ctx.createChild(tr.newCol(), true), mCampo, viewMode);
                }
            } else {
                wicketBuilder.build(ctx.createChild(tr.newCol(), true), itemModel, viewMode);
            }

            if ((view instanceof MTableListaView) && ((MTableListaView) view).isPermiteExclusaoDeLinha()
                    && viewMode.isEdition()) {
                appendRemoverButton(this, form, item, tr.newCol());
            }

            item.add(tr);
        }
    }
}
