package br.net.mirante.singular.form.wicket.mapper;

import static br.net.mirante.singular.util.wicket.util.Shortcuts.*;
import static org.apache.commons.lang3.StringUtils.*;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.basic.view.MTableListaView;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.wicket.UIBuilderWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.model.AtributoModel;
import br.net.mirante.singular.form.wicket.model.MInstanciaCampoModel;
import br.net.mirante.singular.form.wicket.model.MTipoElementosModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.IBSGridCol.BSGridSize;
import br.net.mirante.singular.util.wicket.bootstrap.layout.TemplatePanel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.table.BSTRow;
import br.net.mirante.singular.util.wicket.bootstrap.layout.table.BSTSection;

public class TableListaMapper extends AbstractListaMapper {

    @Override
    @SuppressWarnings("unchecked")
    public void buildView(WicketBuildContext ctx, MView view, IModel<? extends MInstancia> model) {
        final IModel<MILista<MInstancia>> mLista = $m.get(() -> (MILista<MInstancia>) model.getObject());
        final IModel<String> label = new AtributoModel<>(mLista, MPacoteBasic.ATR_LABEL);
        final IModel<MTipo<MInstancia>> tipoElementos = new MTipoElementosModel(mLista);

        ctx.setHint(ControlsFieldComponentMapper.NO_DECORATION, true);

        final BSContainer<?> parentCol = ctx.getContainer();

        final TemplatePanel template = parentCol.newTemplateTag(t -> ""
            + "<form wicket:id='_f'>"
            + "<div class='panel panel-default'>"
            + "  <div class='panel-heading' wicket:id='_title'></div>"
            + "  <div class='panel-body'>"
            + "    <table wicket:id='_t' class='table table-condensed table-unstyled'>"
            + "      <thead wicket:id='_h'></thead>"
            + "      <tbody><wicket:container wicket:id='_e'><tr wicket:id='_r'></tr></wicket:container></tbody>"
            + "      <tfoot wicket:id='_ft'>"
            + "        <tr><td colspan='99' wicket:id='_fb'></td></tr>"
            + "      </tfoot>"
            + "    </table>"
            + "  </div>"
            + "</div>"
            + "</form>");
        final Form<?> form = new Form<>("_f");
        final WebMarkupContainer table = new WebMarkupContainer("_t");
        final BSTSection thead = new BSTSection("_h").setTagName("thead");
        final ElementsView trView = new TableElementsView("_e", mLista, ctx, view, form);
        final WebMarkupContainer footer = new WebMarkupContainer("_ft");
        final BSContainer<?> footerBody = new BSContainer<>("_fb");
        final Label title = new Label("_title", label);

        form.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);

        if (isBlank(label.getObject()))
            title.setVisible(false);

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

        if ((view instanceof MTableListaView) && ((MTableListaView) view).isPermiteAdicaoDeLinha()) {
            AdicionarButton btn = appendAdicionarButton(mLista, form, footerBody);
            if (!((MTableListaView) view).isPermiteInsercaoDeLinha())
                btn.add($b.classAppender("pull-right"));
        } else {
            footer.setVisible(false);
        }

        template
            .add(form
                .add(title)
                .add(table
                    .add(thead)
                    .add(trView)
                    .add(footer
                        .add(footerBody))));
    }

    private static final class TableElementsView extends ElementsView {
        private final WicketBuildContext ctx;
        private final MView              view;
        private final Form<?>            form;
        private TableElementsView(String id, IModel<MILista<MInstancia>> model, WicketBuildContext ctx, MView view, Form<?> form) {
            super(id, model);
            this.ctx = ctx;
            this.view = view;
            this.form = form;
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
                    UIBuilderWicket.buildForEdit(ctx.createChild(tr.newCol(), true), mCampo);
                }
            } else {
                UIBuilderWicket.buildForEdit(ctx.createChild(tr.newCol(), true), itemModel);
            }

            if ((view instanceof MTableListaView) && ((MTableListaView) view).isPermiteExclusaoDeLinha())
                appendRemoverButton(this, form, item, tr.newCol());

            item.add(tr);
        }
    }
}
