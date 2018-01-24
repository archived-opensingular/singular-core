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

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.decorator.action.ISInstanceActionCapable;
import org.opensingular.form.decorator.action.ISInstanceActionsProvider;
import org.opensingular.form.decorator.action.SInstanceAction;
import org.opensingular.form.type.basic.AtrBootstrap;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.type.core.SPackageBootstrap;
import org.opensingular.form.wicket.IWicketComponentMapper;
import org.opensingular.form.wicket.SValidationFeedbackHandler;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.behavior.DisabledClassBehavior;
import org.opensingular.form.wicket.feedback.FeedbackFence;
import org.opensingular.form.wicket.mapper.decorator.SInstanceActionsPanel;
import org.opensingular.form.wicket.mapper.decorator.SInstanceActionsProviders;
import org.opensingular.form.wicket.model.AttributeModel;
import org.opensingular.form.wicket.model.ISInstanceAwareModel;
import org.opensingular.form.wicket.model.SInstanceFieldModel;
import org.opensingular.lib.commons.lambda.IFunction;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSCol;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSGrid;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSRow;
import org.opensingular.lib.wicket.util.bootstrap.layout.IBSComponentFactory;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.opensingular.lib.wicket.util.util.Shortcuts.$b;
import static org.opensingular.lib.wicket.util.util.WicketUtils.$m;

public abstract class AbstractCompositeMapper implements IWicketComponentMapper, ISInstanceActionCapable {

    static final HintKey<HashMap<String, Integer>> COL_WIDTHS               = HashMap::new;

    private final SInstanceActionsProviders        instanceActionsProviders = new SInstanceActionsProviders(this);

    @Override
    public void buildView(WicketBuildContext ctx) {
        getViewBuilder(ctx).buildView();
    }

    protected abstract ICompositeViewBuilder getViewBuilder(WicketBuildContext ctx);

    @Override
    public void addSInstanceActionsProvider(int sortPosition, ISInstanceActionsProvider provider) {
        this.getInstanceActionsProviders().addSInstanceActionsProvider(sortPosition, provider);
    }

    protected SInstanceActionsProviders getInstanceActionsProviders() {
        return instanceActionsProviders;
    }

    protected static abstract class AbstractCompositeViewBuilder implements ICompositeViewBuilder, Serializable {

        protected AbstractCompositeMapper           mapper;
        protected WicketBuildContext                ctx;
        protected ISInstanceAwareModel<SIComposite> model;

        @SuppressWarnings("unchecked")
        AbstractCompositeViewBuilder(WicketBuildContext ctx, AbstractCompositeMapper mapper) {
            this.ctx = ctx;
            this.mapper = mapper;
            this.model = (ISInstanceAwareModel<SIComposite>) this.ctx.getModel();
        }

        @Override
        public void buildView() {

            final BSGrid grid = createCompositeGrid(ctx);

            if (!findFeedbackAwareParent().isPresent()) {
                final BSContainer<?> rootContainer = ctx.getContainer();
                final BSContainer<?> externalContainer = ctx.getExternalContainer();
                SValidationFeedbackHandler feedbackHandler = SValidationFeedbackHandler.bindTo(new FeedbackFence(rootContainer, externalContainer));
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

        protected void buildField(final BSRow row, final SInstanceFieldModel<SInstance> mField) {
            SInstance iField = mField.getObject();
            BSCol col = row.newCol();
            configureColspan(ctx, iField, col);
            ctx.createChild(col, mField, ctx.getConfirmationModal()).build();
        }

        protected void configureColspan(WicketBuildContext ctx, final SInstance fieldInstance, BSCol col) {
            final int colPref = getPrefColspan(ctx, fieldInstance);

            final Optional<AtrBootstrap> atr = Optional.ofNullable(fieldInstance.asAtrBootstrap());
            final Optional<Integer> colXs = atr.map(AtrBootstrap::getColXs);
            final Optional<Integer> colSm = atr.map(AtrBootstrap::getColSm);
            final Optional<Integer> colMd = atr.map(AtrBootstrap::getColMd);
            final Optional<Integer> colLg = atr.map(AtrBootstrap::getColLg);

            /*
             * Heuristica de distribuicao de tamanho das colunas, futuramente pode ser
             * parametrizado ou transoformado em uma configuracao
             */
            col.xs(colXs.orElse(Integer.min(colPref * 4, BSCol.MAX_COLS)));
            col.sm(colSm.orElse(Integer.min(colPref * 3, BSCol.MAX_COLS)));
            col.md(colMd.orElse(Integer.min(colPref * 2, BSCol.MAX_COLS)));
            col.lg(colLg.orElse(Integer.min(colPref, BSCol.MAX_COLS)));
        }

        protected int getPrefColspan(WicketBuildContext ctx, final SInstance iField) {
            final SType<?> fieldType = iField.getType();
            final HashMap<String, Integer> hintColWidths = ctx.getHint(COL_WIDTHS);

            String fieldName = fieldType.getName();

            return (hintColWidths.containsKey(fieldName))
                ? hintColWidths.get(fieldName)
                : iField.asAtrBootstrap().getColPreference(BSCol.MAX_COLS);
        }

        protected SInstanceFieldModel<SInstance> fieldModel(SType<?> field) {
            return new SInstanceFieldModel<>(model, field.getNameSimple());
        }

        protected BSCol addLabelIfNeeded(WicketBuildContext ctx, final BSGrid grid) {
            if (ctx.getHint(HIDE_LABEL))
                return null;
            
            final List<SInstanceAction> actionsIterator = mapper.getInstanceActionsProviders().actionList(model);
            final IModel<String> label = $m.ofValue(trimToEmpty(getInstance().asAtr().getLabel()));

            final boolean hasLabel = isNotBlank(label.getObject());
            final boolean hasActions = !actionsIterator.isEmpty();

            if (hasLabel || hasActions) {
                BSCol column = grid.newColInRow();

                if (hasLabel) {
                    column.appendTag("h4", new Label("_title", label)
                        .add($b.classAppender("singular-composite-title")));
                    ctx.configureContainer(label);
                }

                IFunction<AjaxRequestTarget, List<?>> internalContextListProvider = target -> Arrays.asList(
                    mapper,
                    RequestCycle.get().find(AjaxRequestTarget.class),
                    model,
                    model.getObject(),
                    ctx,
                    ctx.getContainer());

                SInstanceActionsPanel.addPrimarySecondaryPanelsTo(
                    column,
                    mapper.getInstanceActionsProviders(),
                    model,
                    false,
                    internalContextListProvider);

                return column;
            }

            return null;
        }

        protected void addSubtitleIfNeeded(WicketBuildContext ctx, final BSGrid grid) {
            AttributeModel<String> subtitle = new AttributeModel<>(model, SPackageBasic.ATR_SUBTITLE);
            if (isNotBlank(subtitle.getObject())) {
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

        protected BSGrid createCompositeGrid(WicketBuildContext ctx) {

            final BSContainer<?> parentCol = ctx.getContainer();
            final BSGrid grid = parentCol.newGrid();

            addLabelIfNeeded(ctx, grid);

            grid.add(DisabledClassBehavior.getInstance());
            grid.setDefaultModel(model);

            return grid;
        }

        protected void buildFields(WicketBuildContext ctx, BSGrid grid) {
            BSRow row = grid.newRow();

            final WicketBuildContext rootContext = ctx.getRootContext();
            final IBSComponentFactory<Component> factory = rootContext.getPreFormPanelFactory();

            if (factory != null) {
                grid.newComponent(factory);
                rootContext.setPreFormPanelFactory(null);
                row = grid.newRow();
            }

            int rowColTotal = 0;

            for (SType<?> field : getInstanceType().getFields()) {
                final Boolean newRow = field.getAttributeValue(SPackageBootstrap.ATR_COL_ON_NEW_ROW);
                if (newRow != null && newRow) {
                    row = grid.newRow();
                }

                final SInstanceFieldModel<SInstance> instanceModel = fieldModel(field);

                rowColTotal += getPrefColspan(ctx, instanceModel.getObject());
                if (rowColTotal > BSGrid.MAX_COLS) {
                    // row = grid.newRow();  //TODO? descomentar para quebrar rows a cada 12 cols.
                    rowColTotal = 0;
                }

                buildField(row, instanceModel);
            }
        }

    }

    interface ICompositeViewBuilder {
        void buildView();
    }

}