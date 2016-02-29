package br.net.mirante.singular.form.wicket.mapper;

import java.util.HashMap;
import java.util.Optional;

import br.net.mirante.singular.form.mform.core.annotation.AtrAnnotation;
import br.net.mirante.singular.util.wicket.resource.Icone;
import br.net.mirante.singular.util.wicket.resource.IconeView;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.AtrBootstrap;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper;
import br.net.mirante.singular.form.wicket.UIBuilderWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.behavior.DisabledClassBehavior;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.model.AbstractSInstanceModel;
import br.net.mirante.singular.form.wicket.model.SInstanceCampoModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSCol;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSRow;
import static br.net.mirante.singular.util.wicket.util.Shortcuts.$m;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

@SuppressWarnings("serial")
public class DefaultCompostoMapper implements IWicketComponentMapper {

    static final HintKey<HashMap<String, Integer>> COL_WIDTHS = () -> new HashMap<>();
    static final HintKey<Boolean>                  INLINE     = () -> false;

    public void buildView(WicketBuildContext ctx) {
        new CompostoViewBuilder(ctx).buildView();
    }

    public static class CompostoViewBuilder {

        protected WicketBuildContext                          ctx;
        protected AbstractSInstanceModel<? extends SInstance> model;
        protected SIComposite                                 instance;
        protected STypeComposite<SIComposite>                 type;

        @SuppressWarnings("unchecked")
        public CompostoViewBuilder(WicketBuildContext ctx) {
            this.ctx = ctx;
            model = (AbstractSInstanceModel<? extends SInstance>) this.ctx.getModel();
            instance = ctx.getCurrentInstance();
            type = (STypeComposite<SIComposite>) instance.getType();
        }

        public void buildView() {
            final BSGrid grid = createCompositeGrid(ctx);
            buildFields(ctx, grid.newRow());
            if(ctx.getRootContext().annotation().enabled() && instance.as(AtrAnnotation::new).isAnnotated()){

                BSContainer toggleContainer = new BSContainer<>("_toggle_btn_");
                toggleContainer.setInnerStyle("position:absolute;top:15px;right: 15px;");

                BSContainer icon = new BSContainer<>("_toggle_icon_");
                if(instance.as(AtrAnnotation::new).hasAnnotation()) {
                    icon.appendTag("i", true, "class='fa fa-comment-o'", new Label("_x_", $m.ofValue()));
                }else{
                    icon.appendTag("i", true, "class='fa fa-plus'", new Label("_x_", $m.ofValue()));
                }

                String colorCSS = "btn-default";
                if(instance.as(AtrAnnotation::new).hasAnnotation()) {
                    if(instance.as(AtrAnnotation::new).annotation().getApproved()) {
                        colorCSS = "btn-info";
                    }else{
                        colorCSS = "btn-danger";
                    }
                }

                toggleContainer.appendTag("a",true,
                        "href='javascript:;' class='btn btn-circle btn-icon-only "+colorCSS+"'", icon);

                grid.appendTag("div",true,"class='annotation-toggle-container'",toggleContainer);
                ctx.getRootContext().updateAnnotations(grid);
            }
        }

        protected BSGrid createCompositeGrid(WicketBuildContext ctx) {
            final BSContainer<?> parentCol = ctx.getContainer();
            final BSGrid grid = parentCol.newGrid();

            addLabelIfNeeded(ctx, grid);

            grid.add(DisabledClassBehavior.getInstance());
            grid.setDefaultModel(model);
            return grid;
        }

        protected void buildFields(WicketBuildContext ctx, BSRow row) {
            for (SType<?> tCampo : type.getFields()) {
                buildField(ctx.getUiBuilderWicket(), row, fieldModel(tCampo));
            }
        }

        protected SInstanceCampoModel<SInstance> fieldModel(SType<?> tCampo) {
            return new SInstanceCampoModel<>(model, tCampo.getSimpleName());
        }

        protected BSCol addLabelIfNeeded(WicketBuildContext ctx, final BSGrid grid) {
            IModel<String> label = $m.ofValue(trimToEmpty(instance.as(AtrBasic::new).getLabel()));
            if (isNotBlank(label.getObject())) {
                BSCol column = grid.newColInRow();
                column.appendTag("h3", new Label("_title", label));
                ctx.configureContainer(label);
                return column;
            }
            return null;
        }

        protected void buildField(UIBuilderWicket wicketBuilder, final BSRow row,
                                  final SInstanceCampoModel<SInstance> mCampo) {

            final SType<?> type = mCampo.getMInstancia().getType();
            final SInstance iCampo = mCampo.getObject();
            final ViewMode viewMode = ctx.getViewMode();

            if (iCampo instanceof SIComposite) {
                final BSCol col = configureColspan(ctx, type, iCampo, row.newCol());
                wicketBuilder.build(ctx.createChild(col.newGrid().newColInRow(), true, mCampo), viewMode);
            } else {
                wicketBuilder.build(ctx.createChild(configureColspan(ctx, type, iCampo, row.newCol()), true, mCampo), viewMode);
            }
        }

        protected BSCol configureColspan(WicketBuildContext ctx, SType<?> tCampo, final SInstance iCampo, BSCol col) {
            final HashMap<String, Integer> hintColWidths = ctx.getHint(DefaultCompostoMapper.COL_WIDTHS);
            /*
            * Heuristica de distribuicao de tamanho das colunas, futuramente pode ser
            * parametrizado ou transoformado em uma configuracao
            */
            final int colPref;

            if (hintColWidths.containsKey(tCampo.getName())) {
                colPref = hintColWidths.get(tCampo.getName());
            } else {
                colPref = iCampo.as(AtrBootstrap::new).getColPreference(BSCol.MAX_COLS);
            }

            final Optional<Integer> colXs = Optional.ofNullable(iCampo.as(AtrBootstrap::new).getColXs());
            final Optional<Integer> colSm = Optional.ofNullable(iCampo.as(AtrBootstrap::new).getColSm());
            final Optional<Integer> colMd = Optional.ofNullable(iCampo.as(AtrBootstrap::new).getColMd());
            final Optional<Integer> colLg = Optional.ofNullable(iCampo.as(AtrBootstrap::new).getColLg());

            col.xs(colXs.orElse(Integer.min(colPref * 4, BSCol.MAX_COLS)));
            col.sm(colSm.orElse(Integer.min(colPref * 3, BSCol.MAX_COLS)));
            col.md(colMd.orElse(Integer.min(colPref * 2, BSCol.MAX_COLS)));
            col.lg(colLg.orElse(Integer.min(colPref, BSCol.MAX_COLS)));

            return col;
        }
    }
}