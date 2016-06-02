/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper;

import static br.net.mirante.singular.form.wicket.mapper.annotation.AnnotationComponent.*;
import static br.net.mirante.singular.util.wicket.util.Shortcuts.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.util.HashMap;
import java.util.Optional;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.type.core.SPackageBootstrap;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper;
import br.net.mirante.singular.form.wicket.SValidationFeedbackHandler;
import br.net.mirante.singular.form.wicket.UIBuilderWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.behavior.DisabledClassBehavior;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.feedback.SValidationFeedbackPanel;
import br.net.mirante.singular.form.wicket.model.AbstractSInstanceModel;
import br.net.mirante.singular.form.wicket.model.SInstanceCampoModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSCol;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSRow;

@SuppressWarnings("serial")
public class DefaultCompostoMapper implements IWicketComponentMapper {

    static final HintKey<HashMap<String, Integer>> COL_WIDTHS = () -> new HashMap<>();
    static final HintKey<Boolean>                  INLINE     = () -> false;

    @Override
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
            //            container.newTagWithFactory("ul", true, "class='page-breadcrumb breadcrumb'", (id) -> buildBreadCrumbBar(id, Arrays.asList("Bread", "Crumb")));

            final BSGrid grid = createCompositeGrid(ctx);

            if (!findFeedbackAwareParent().isPresent()) {
                final BSContainer<?> rootContainer = ctx.getContainer();
                SValidationFeedbackHandler feedbackHandler = SValidationFeedbackHandler.bindTo(rootContainer);
                feedbackHandler.findNestedErrorsMaxLevel();
                grid.appendTag("div", new SValidationFeedbackPanel("feedback", rootContainer).setShowBox(true));
            }

            buildFields(ctx, grid);
            if (renderAnnotations()) {
                ctx.getRootContext().updateAnnotations(
                    appendAnnotationToggleButton(grid.newRow(), instance),
                    instance);
            }
        }

        protected Optional<MarkupContainer> findFeedbackAwareParent() {
            return Optional.ofNullable(ctx.getContainer().visitParents(MarkupContainer.class, (c, v) -> {
                if (SValidationFeedbackHandler.isBound(c))
                    v.stop(c);
            }));
        }

        private boolean renderAnnotations() {
            return ctx.getRootContext().annotation().enabled() &&
                instance.asAtrAnnotation().isAnnotated();
        }

        protected BSGrid createCompositeGrid(WicketBuildContext ctx) {
            final BSContainer<?> parentCol = ctx.getContainer();
            final BSGrid grid = parentCol.newGrid();

            addLabelIfNeeded(ctx, grid);

            grid.add(DisabledClassBehavior.getInstance());
            grid.setDefaultModel(model);
            grid.setCssClass("composite-box-grid");
            return grid;
        }

        protected void buildFields(WicketBuildContext ctx, BSGrid grid) {
            BSRow row = grid.newRow();
            int rowColTotal = 0;
            for (SType<?> tCampo : type.getFields()) {
                final Boolean newRow = tCampo.getAttributeValue(SPackageBootstrap.ATR_COL_ON_NEW_ROW);
                if (newRow != null && newRow) {
                    row = grid.newRow();
                }
                
                final SInstanceCampoModel<SInstance> instanceModel = fieldModel(tCampo);

                rowColTotal += getPrefColspan(ctx, instanceModel.getObject());
                if (rowColTotal > BSGrid.MAX_COLS) {
                    // row = grid.newRow();  //TODO? descomentar para quebrar rows a cada 12 cols.
                    rowColTotal = 0;
                }

                buildField(ctx.getUiBuilderWicket(), row, instanceModel);
            }
        }

        protected SInstanceCampoModel<SInstance> fieldModel(SType<?> tCampo) {
            return new SInstanceCampoModel<>(model, tCampo.getNameSimple());
        }

        protected BSCol addLabelIfNeeded(WicketBuildContext ctx, final BSGrid grid) {
            IModel<String> label = $m.ofValue(trimToEmpty(instance.asAtr().getLabel()));
            if (isNotBlank(label.getObject())) {
                BSCol column = grid.newColInRow();
                column.appendTag("h3", new Label("_title", label));
                ctx.configureContainer(label);
                return column;
            }
            return null;
        }

        protected void buildField(UIBuilderWicket wicketBuilder, final BSRow row, final SInstanceCampoModel<SInstance> mCampo) {

            final SInstance iCampo = mCampo.getObject();
            final ViewMode viewMode = ctx.getViewMode();

            final BSCol col = row.newCol();
            configureColspan(ctx, iCampo, col);
            if (iCampo instanceof SIComposite) {
                wicketBuilder.build(ctx.createChild(col.newGrid().newColInRow(), true, mCampo), viewMode);
            } else {
                wicketBuilder.build(ctx.createChild(col, true, mCampo), viewMode);
            }
        }

        protected void configureColspan(WicketBuildContext ctx, final SInstance iCampo, BSCol col) {
            final int colPref = getPrefColspan(ctx, iCampo);

            final Optional<Integer> colXs = Optional.ofNullable(iCampo.asAtrBootstrap().getColXs());
            final Optional<Integer> colSm = Optional.ofNullable(iCampo.asAtrBootstrap().getColSm());
            final Optional<Integer> colMd = Optional.ofNullable(iCampo.asAtrBootstrap().getColMd());
            final Optional<Integer> colLg = Optional.ofNullable(iCampo.asAtrBootstrap().getColLg());

            /*
             * Heuristica de distribuicao de tamanho das colunas, futuramente pode ser
             * parametrizado ou transoformado em uma configuracao
             */
            col.xs(colXs.orElse(Integer.min(colPref * 4, BSCol.MAX_COLS)));
            col.sm(colSm.orElse(Integer.min(colPref * 3, BSCol.MAX_COLS)));
            col.md(colMd.orElse(Integer.min(colPref * 2, BSCol.MAX_COLS)));
            col.lg(colLg.orElse(Integer.min(colPref, BSCol.MAX_COLS)));
        }

        protected int getPrefColspan(WicketBuildContext ctx, final SInstance iCampo) {
            final SType<?> tCampo = iCampo.getType();
            final HashMap<String, Integer> hintColWidths = ctx.getHint(DefaultCompostoMapper.COL_WIDTHS);

            return (hintColWidths.containsKey(tCampo.getName()))
                ? hintColWidths.get(tCampo.getName())
                : iCampo.asAtrBootstrap().getColPreference(BSCol.MAX_COLS);
        }
    }

}