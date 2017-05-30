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

package org.opensingular.form.wicket.mapper.composite;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.opensingular.lib.wicket.util.util.WicketUtils.$m;

import java.util.HashMap;
import java.util.Optional;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.type.core.SPackageBootstrap;
import org.opensingular.form.wicket.IWicketComponentMapper;
import org.opensingular.form.wicket.SValidationFeedbackHandler;
import org.opensingular.form.wicket.UIBuilderWicket;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.behavior.DisabledClassBehavior;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.feedback.FeedbackFence;
import org.opensingular.form.wicket.mapper.annotation.AnnotationComponent;
import org.opensingular.form.wicket.model.AttributeModel;
import org.opensingular.form.wicket.model.ISInstanceAwareModel;
import org.opensingular.form.wicket.model.SInstanceFieldModel;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSCol;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSGrid;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSRow;
import org.opensingular.lib.wicket.util.bootstrap.layout.IBSComponentFactory;

public abstract class AbstractCompositeMapper implements IWicketComponentMapper {

    static final HintKey<HashMap<String, Integer>> COL_WIDTHS = HashMap::new;

    @Override
    public void buildView(WicketBuildContext ctx) {
        getViewBuilder(ctx).buildView();
    }

    protected abstract ICompositeViewBuilder getViewBuilder(WicketBuildContext ctx);

    static abstract class AbstractCompositeViewBuilder implements ICompositeViewBuilder {

        protected WicketBuildContext                ctx;
        protected ISInstanceAwareModel<SIComposite> model;

        @SuppressWarnings("unchecked")
        AbstractCompositeViewBuilder(WicketBuildContext ctx) {
            this.ctx = ctx;
            this.model = (ISInstanceAwareModel<SIComposite>) this.ctx.getModel();
        }

        @Override
        public void buildView() {

            if (renderAnnotations()) {
                ctx.getContainer().appendTag("div", new AnnotationComponent("annotation", ctx, model));
            }
            
            final BSGrid grid = createCompositeGrid(ctx);

            if (!findFeedbackAwareParent().isPresent()) {
                final BSContainer<?>       rootContainer     = ctx.getContainer();
                final BSContainer<?>       externalContainer = ctx.getExternalContainer();
                SValidationFeedbackHandler feedbackHandler   = SValidationFeedbackHandler.bindTo(new FeedbackFence(rootContainer, externalContainer));
                feedbackHandler.findNestedErrorsMaxLevel();
                grid.appendTag("div", ctx.createFeedbackPanel("feedback").setShowBox(true));
            }

            buildFields(ctx, grid);
        }

        private SIComposite getInstance() {
            return ctx.getCurrentInstance();
        }

        protected STypeComposite<?> getInstanceType() {
            return getInstance().getType();
        }

        protected void buildField(UIBuilderWicket wicketBuilder, final BSRow row, final SInstanceFieldModel<SInstance> mCampo) {
            final SInstance iCampo   = mCampo.getObject();
            final ViewMode  viewMode = ctx.getViewMode();
            final BSCol     col      = row.newCol();
            configureColspan(ctx, iCampo, col);
            wicketBuilder.build(ctx.createChild(col, true, mCampo), viewMode);
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
            final SType<?>                 tCampo        = iCampo.getType();
            final HashMap<String, Integer> hintColWidths = ctx.getHint(COL_WIDTHS);

            String tCampoName = tCampo.getName();

            return (hintColWidths.containsKey(tCampoName))
                    ? hintColWidths.get(tCampoName)
                    : iCampo.asAtrBootstrap().getColPreference(BSCol.MAX_COLS);
        }

        protected SInstanceFieldModel<SInstance> fieldModel(SType<?> tCampo) {
            return new SInstanceFieldModel<>(model, tCampo.getNameSimple());
        }

        protected BSCol addLabelIfNeeded(WicketBuildContext ctx, final BSGrid grid) {
            IModel<String> label = $m.ofValue(trimToEmpty(getInstance().asAtr().getLabel()));
            if (isNotBlank(label.getObject())) {
                //subtitle
                AttributeModel<String> subtitle = new AttributeModel<>(model, SPackageBasic.ATR_SUBTITLE);
                BSCol column = grid.newColInRow();
                
                column.appendTag("h5", new Label("_title", label));    
                ctx.configureContainer(label);
                column.setVisible(!ctx.getParent().isTitleInBlock());

                if(isNotBlank(subtitle.getObject())){
                    column.newTag("span", true, "class='help-block subtitle'", column.newComponent(id -> (Label) new Label(id, subtitle).setEscapeModelStrings(true)));
                }
                
                return column;
            }
            return null;
        }
        
        protected void addSubtitleIfNeeded(WicketBuildContext ctx, final BSGrid grid){
            AttributeModel<String> subtitle = new AttributeModel<>(model, SPackageBasic.ATR_SUBTITLE);
            if(isNotBlank(subtitle.getObject())){
                BSCol column = grid.newColInRow();
                column.newTag("span", true, "class='subtitle'", column.newComponent(id -> (Label) new Label(id, subtitle).setEscapeModelStrings(true)));
            }
        }
        

        protected Optional<MarkupContainer> findFeedbackAwareParent() {
            return Optional.ofNullable(ctx.getContainer().visitParents(MarkupContainer.class, (c, v) -> {
                if (SValidationFeedbackHandler.isBound(c))
                    v.stop(c);
            }));
        }

        private boolean renderAnnotations() {
            return ctx.getRootContext().getAnnotationMode().enabled() &&
                    getInstance().asAtrAnnotation().isAnnotated();
        }
        protected BSGrid createCompositeGrid(WicketBuildContext ctx) {

            final BSContainer<?> parentCol = ctx.getContainer();
            final BSGrid         grid      = parentCol.newGrid();

            addLabelIfNeeded(ctx, grid);

            grid.add(DisabledClassBehavior.getInstance());
            grid.setDefaultModel(model);

            return grid;
        }

        protected void buildFields(WicketBuildContext ctx, BSGrid grid) {
            BSRow row = grid.newRow();

            final WicketBuildContext             rootContext = ctx.getRootContext();
            final IBSComponentFactory<Component> factory     = rootContext.getPreFormPanelFactory();

            if (factory != null) {
                grid.newComponent(factory);
                rootContext.setPreFormPanelFactory(null);
                row = grid.newRow();
            }

            int rowColTotal = 0;

            for (SType<?> tCampo : getInstanceType().getFields()) {
                final Boolean newRow = tCampo.getAttributeValue(SPackageBootstrap.ATR_COL_ON_NEW_ROW);
                if (newRow != null && newRow) {
                    row = grid.newRow();
                }

                final SInstanceFieldModel<SInstance> instanceModel = fieldModel(tCampo);

                rowColTotal += getPrefColspan(ctx, instanceModel.getObject());
                if (rowColTotal > BSGrid.MAX_COLS) {
                    // row = grid.newRow();  //TODO? descomentar para quebrar rows a cada 12 cols.
                    rowColTotal = 0;
                }

                buildField(ctx.getUiBuilderWicket(), row, instanceModel);
            }
        }

    }

    interface ICompositeViewBuilder {
        void buildView();
    }

}