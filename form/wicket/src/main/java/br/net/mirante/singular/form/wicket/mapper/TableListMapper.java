package br.net.mirante.singular.form.wicket.mapper;

import java.util.Set;

import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

import com.google.common.base.Strings;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SList;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.mform.basic.ui.AtrBootstrap;
import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;
import br.net.mirante.singular.form.mform.basic.view.MTableListaView;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.mform.core.SPackageCore;
import br.net.mirante.singular.form.wicket.UIBuilderWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.mapper.components.MetronicPanel;
import br.net.mirante.singular.form.wicket.model.SInstanceCampoModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.IBSGridCol.BSGridSize;
import br.net.mirante.singular.util.wicket.bootstrap.layout.TemplatePanel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.table.BSTDataCell;
import br.net.mirante.singular.util.wicket.bootstrap.layout.table.BSTRow;
import br.net.mirante.singular.util.wicket.bootstrap.layout.table.BSTSection;
import br.net.mirante.singular.util.wicket.model.ValueModel;
import static br.net.mirante.singular.util.wicket.util.Shortcuts.$b;
import static br.net.mirante.singular.util.wicket.util.Shortcuts.$m;

public class TableListMapper extends AbstractListaMapper {

    public void buildView(WicketBuildContext ctx) {

        if (!(ctx.getView() instanceof MTableListaView)) {
            throw new SingularFormException("TableListMapper deve ser utilizado com MTableListaView");
        }

        if (ctx.getCurrentInstance() instanceof SList) {

            final IModel<SList<SInstance>> list = $m.get(() -> (ctx.getCurrentInstance()));
            final MTableListaView view = (MTableListaView) ctx.getView();
            final boolean isEdition = ctx.getViewMode() == null || ctx.getViewMode().isEdition();

            ctx.setHint(ControlsFieldComponentMapper.NO_DECORATION, true);
            ctx.getContainer().appendComponent(id -> MetronicPanel.MetronicPanelBuilder.build(
                    id,
                    (header, form) -> buildHeader(header, form, list, ctx, view, isEdition),
                    (content, form) -> builContent(content, form, list, ctx, view, isEdition),
                    (footer, form) -> footer.setVisible(false)
            ));
        }
    }

    private void buildHeader(BSContainer<?> header, Form<?> form, IModel<SList<SInstance>> list,
                             WicketBuildContext ctx, MTableListaView view, boolean isEdition) {

        final ValueModel<String> label = $m.ofValue(ctx.getCurrentInstance().getMTipo().asAtrBasic().getLabel());

        header.appendTag("span", new Label("_title", label));
        header.add($b.visibleIf($m.get(() -> !Strings.isNullOrEmpty(label.getObject()))));

        if (view.isPermiteAdicaoDeLinha() && isEdition) {
            appendAddButton(list, form, header);
        }

    }

    private void builContent(BSContainer<?> content, Form<?> form, IModel<SList<SInstance>> list,
                             WicketBuildContext ctx, MTableListaView view, boolean isEdition) {


        final String markup = ""
                + " <table class='table table-condensed table-unstyled'>                                             "
                + "      <thead wicket:id='_h'></thead>                                                              "
                + "      <tbody><wicket:container wicket:id='_e'><tr wicket:id='_r'></tr></wicket:container></tbody> "
                + "      <tfoot wicket:id='_ft'>                                                                     "
                + "          <tr><td colspan='99' wicket:id='_fb'></td></tr>                                         "
                + "      </tfoot>                                                                                    "
                + " </table>                                                                                         ";

        final TemplatePanel template = content.newTemplateTag(tp -> markup);
        final BSTSection tableHeader = new BSTSection("_h").setTagName("thead");
        final ElementsView trView = new TableElementsView("_e", list, ctx, form);
        final WebMarkupContainer footer = new WebMarkupContainer("_ft");
        final BSContainer<?> footerBody = new BSContainer<>("_fb");

        final SType<SInstance> tipoElementos = list.getObject().getTipoElementos();

        if (tipoElementos instanceof STypeComposite) {

            final STypeComposite<?> sTypeComposite = (STypeComposite<?>) tipoElementos;
            final BSTRow row = tableHeader.newRow();

            if (view.isPermiteInsercaoDeLinha()) {
                row.newTHeaderCell($m.ofValue(""));
            }

            int sumWidthPref = sTypeComposite.getFields().stream().mapToInt((x) -> x.as(AtrBootstrap::new).getColPreference(1)).sum();

            for (SType<?> tCampo : sTypeComposite.getFields()) {
                final Integer preferentialWidth = tCampo.as(AtrBootstrap::new).getColPreference(1);
                final BSTDataCell cell = row.newTHeaderCell($m.ofValue(tCampo.as(SPackageBasic.aspect()).getLabel()));
                final String width = String.format("width:%.0f%%;", (100.0 * preferentialWidth) / sumWidthPref);
                final boolean isCampoObrigatorio = tCampo.as(SPackageCore.aspect()).isObrigatorio();

                cell.setInnerStyle(width);
                cell.add(new ClassAttributeModifier() {
                    @Override
                    protected Set<String> update(Set<String> oldClasses) {
                        if (isCampoObrigatorio) {
                            oldClasses.add("singular-form-required");
                        } else {
                            oldClasses.remove("singular-form-required");
                        }
                        return oldClasses;
                    }
                });
            }

        } else {
            tableHeader.setVisible(false);
        }

        footer.setVisible(!(view.isPermiteAdicaoDeLinha() && isEdition));

        template.add(tableHeader)
                .add(trView)
                .add(footer.add(footerBody));
    }


    private static final class TableElementsView extends ElementsView {

        private final WicketBuildContext ctx;
        private final MView view;
        private final Form<?> form;
        private final ViewMode viewMode;
        private final UIBuilderWicket wicketBuilder;

        private TableElementsView(String id, IModel<SList<SInstance>> model, WicketBuildContext ctx, Form<?> form) {
            super(id, model);
            this.wicketBuilder = ctx.getUiBuilderWicket();
            this.ctx = ctx;
            this.view = ctx.getView();
            this.form = form;
            this.viewMode = ctx.getViewMode();
        }

        @Override
        protected void populateItem(Item<SInstance> item) {

            final BSTRow row = new BSTRow("_r", BSGridSize.MD);

            if ((view instanceof MTableListaView) && (((MTableListaView) view).isPermiteInsercaoDeLinha()))
                appendInserirButton(this, form, item, row.newCol());

            final IModel<SInstance> itemModel = item.getModel();
            final SInstance instancia = itemModel.getObject();

            if (instancia instanceof SIComposite) {
                final SIComposite composto = (SIComposite) instancia;
                final STypeComposite<SIComposite> tComposto = (STypeComposite<SIComposite>) composto.getMTipo();
                for (SType<?> tCampo : tComposto.getFields()) {
                    final SInstanceCampoModel<SInstance> mCampo;
                    mCampo = new SInstanceCampoModel<>(item.getModel(), tCampo.getNomeSimples());
                    wicketBuilder.build(ctx.createChild(row.newCol(), true, mCampo), viewMode);
                }
            } else {
                wicketBuilder.build(ctx.createChild(row.newCol(), true, itemModel), viewMode);
            }

            if ((view instanceof MTableListaView) && ((MTableListaView) view).isPermiteExclusaoDeLinha()
                    && viewMode.isEdition()) {
                appendRemoverButton(this, form, item, row.newCol());
            }

            item.add(row);
        }
    }
}
