package br.net.mirante.singular.form.wicket;

import static org.apache.commons.lang3.StringUtils.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoLista;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.basic.view.MListaMultiPanelView;
import br.net.mirante.singular.form.mform.basic.view.MListaSimpleTableView;
import br.net.mirante.singular.form.mform.basic.view.MTabView;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.mform.core.MTipoBoolean;
import br.net.mirante.singular.form.mform.core.MTipoData;
import br.net.mirante.singular.form.mform.core.MTipoInteger;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.util.comuns.MTipoAnoMes;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSCol;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSLabel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSRow;
import br.net.mirante.singular.util.wicket.bootstrap.layout.TemplatePanel;
import br.net.mirante.singular.util.wicket.model.ValueModel;
import br.net.mirante.singular.util.wicket.util.IBehaviorsMixin;
import br.net.mirante.singular.util.wicket.util.IModelsMixin;

public class UIBuilderWicket {

    private static final IModelsMixin         $m              = new IModelsMixin() {};
    private static final IBehaviorsMixin      $b              = new IBehaviorsMixin() {};
    private static final WicketMapperRegistry MAPPER_REGISTRY = new WicketMapperRegistry();
    static {
        MAPPER_REGISTRY.registerMapper(MTipoBoolean.class, MView.class, BooleanMapper::new);
        MAPPER_REGISTRY.registerMapper(MTipoInteger.class, MView.class, IntegerMapper::new);
        MAPPER_REGISTRY.registerMapper(MTipoString.class, MView.class, StringMapper::new);
        MAPPER_REGISTRY.registerMapper(MTipoData.class, MView.class, DateMapper::new);
        MAPPER_REGISTRY.registerMapper(MTipoAnoMes.class, MView.class, YearMonthMapper::new);
        MAPPER_REGISTRY.registerMapper(MTipoComposto.class, MView.class, DefaultCompostoMapper::new);
        MAPPER_REGISTRY.registerMapper(MTipoComposto.class, MTabView.class, DefaultCompostoMapper::new);
        MAPPER_REGISTRY.registerMapper(MTipoLista.class, MView.class, DefaultListaMapper::new);
        MAPPER_REGISTRY.registerMapper(MTipoLista.class, MListaSimpleTableView.class, ListaSimpleTableMapper::new);
        MAPPER_REGISTRY.registerMapper(MTipoLista.class, MListaMultiPanelView.class, ListaMultiPanelMapper::new);
    }

    public static void buildForEdit(WicketBuildContext ctx, IModel<? extends MInstancia> model) {
        Object obj = model.getObject();
        MInstancia instancia = (MInstancia) obj;
        MView view = instancia.getView();
        IWicketComponentMapper mapper = MAPPER_REGISTRY.getMapper(instancia)
            .orElseThrow(() -> createErro(instancia, view, "Não há mappeamento de componente Wicket para o tipo"));
        mapper.buildView(ctx, view, model);
    }

    private static RuntimeException createErro(MInstancia instancia, MView view, String msg) {
        return new RuntimeException(
            msg + " (instancia=" + instancia.getCaminhoCompleto()
                + ", tipo=" + instancia.getMTipo().getNome()
                + ", classeInstancia=" + instancia.getClass()
                + ", tipo=" + instancia.getMTipo()
                + ", view=" + view
                + ")");
    }

    static class BooleanMapper implements IWicketComponentMapper {
        @Override
        public void buildView(WicketBuildContext ctx, MView view, IModel<? extends MInstancia> model) {
            String label = trimToEmpty(model.getObject().as(MPacoteBasic.aspect()).getLabel());
            BSControls formGroup = ctx.getContainer()
                .newComponent(BSControls::new);
            IModel<String> labelModel = $m.ofValue(label);
            formGroup.appendLabel(new BSLabel("label", ""));
            formGroup.appendCheckbox(
                new CheckBox(model.getObject().getNome(), new MInstanciaValorModel<>(model)),
                labelModel);
            formGroup.appendFeedback();
        }
    }

    static interface ControlsFieldComponentMapper extends IWicketComponentMapper {
        static final HintKey<Boolean> NO_DECORATION = new HintKey<Boolean>() {};
        void appendInput(BSControls formGroup, IModel<? extends MInstancia> model, IModel<String> labelModel);
        @Override
        default void buildView(WicketBuildContext ctx, MView view, IModel<? extends MInstancia> model) {
            final boolean hintNoDecoration = ctx.getHint(NO_DECORATION, false);

            String label = trimToEmpty(model.getObject().as(MPacoteBasic.aspect()).getLabel());
            ValueModel<String> labelModel = $m.ofValue(label);
            BSControls controls = ctx.getContainer().newFormGroup();

            BSLabel bsLabel = new BSLabel("label", labelModel);
            if (hintNoDecoration)
                bsLabel.add($b.classAppender("visible-sm visible-xs"));

            controls.appendLabel(bsLabel);
            appendInput(controls, model, labelModel);
            controls.appendFeedback();
        }
    }

    static class StringMapper implements ControlsFieldComponentMapper {
        @Override
        public void appendInput(BSControls formGroup, IModel<? extends MInstancia> model, IModel<String> labelModel) {
            if (model.getObject().as(AtrBasic.class).isMultiLinha())
                formGroup
                    .appendTextarea(new TextArea<>(model.getObject().getNome(), new MInstanciaValorModel<>(model))
                        .setLabel(labelModel));
            else
                formGroup
                    .appendInputText(new TextField<>(model.getObject().getNome(), new MInstanciaValorModel<>(model), String.class)
                        .setLabel(labelModel));
        }
    }

    static class DateMapper implements ControlsFieldComponentMapper {
        @Override
        public void appendInput(BSControls formGroup, IModel<? extends MInstancia> model, IModel<String> labelModel) {
            formGroup
                .appendInputText(new TextField<>(model.getObject().getNome(), new MInstanciaValorModel<>(model), Date.class)
                    .setLabel(labelModel));
        }
    }

    static class YearMonthMapper implements ControlsFieldComponentMapper {
        @Override
        public void appendInput(BSControls formGroup, IModel<? extends MInstancia> model, IModel<String> labelModel) {
            formGroup
                .appendInputText(new YearMonthField(model.getObject().getNome(), new MInstanciaValorModel<>(model))
                    .setLabel(labelModel));
        }
    }

    static class IntegerMapper implements ControlsFieldComponentMapper {
        @Override
        public void appendInput(BSControls formGroup, IModel<? extends MInstancia> model, IModel<String> labelModel) {
            formGroup
                .appendInputText(new TextField<>(model.getObject().getNome(), new MInstanciaValorModel<>(model), Integer.class)
                    .setLabel(labelModel));
        }
    }

    static class DefaultCompostoMapper implements IWicketComponentMapper {
        static final HintKey<HashMap<String, Integer>> COL_WIDTHS = new HintKey<HashMap<String, Integer>>() {};
        @Override
        @SuppressWarnings("unchecked")
        public void buildView(WicketBuildContext ctx, MView view, IModel<? extends MInstancia> model) {
            final Map<String, Integer> hintColWidths = ctx.getHint(COL_WIDTHS, new HashMap<>());

            MInstancia instancia = model.getObject();
            MIComposto composto = (MIComposto) instancia;
            MTipoComposto<MIComposto> tComposto = (MTipoComposto<MIComposto>) composto.getMTipo();

            BSCol parentCol = ctx.getContainer();
            BSGrid grid = parentCol.newGrid();
            BSRow row = grid.newRow();

            for (String nomeCampo : tComposto.getCampos()) {
                final MTipo<?> tCampo = tComposto.getCampo(nomeCampo);
                final MInstanciaCampoModel<MInstancia> mCampo = new MInstanciaCampoModel<>(model, tCampo.getNomeSimples());
                final MInstancia iCampo = mCampo.getObject();
                final IModel<String> label = $m.ofValue(trimToEmpty(iCampo.getValorAtributo(MPacoteBasic.ATR_LABEL)));
                final int colspan = (hintColWidths.containsKey(nomeCampo)) ? hintColWidths.get(nomeCampo) : BSCol.MAX_COLS;
                if (iCampo instanceof MIComposto) {
                    final BSCol col = row.newCol().md(colspan);
                    if (isNotBlank(label.getObject()))
                        col.appendTag("h3", new Label("_title", label));
                    buildForEdit(ctx.createChild(col.newGrid().newColInRow(), true), mCampo);
                } else {
                    buildForEdit(ctx.createChild(row.newCol().md(colspan), true), mCampo);
                }
            }
        }
    }

    static class DefaultListaMapper implements IWicketComponentMapper {
        interface NewElementCol extends Serializable {
            BSCol create(WicketBuildContext parentCtx, BSGrid grid, IModel<MInstancia> itemModel, int index);
        }
        interface ConfigureCurrentContext extends Serializable {
            void configure(WicketBuildContext ctx, IModel<MILista<MInstancia>> model);
        }
        interface ConfigureChildContext extends Serializable {
            void configure(WicketBuildContext ctx, IModel<MILista<MInstancia>> model, int index);
        }
        private final ConfigureCurrentContext configureCurrentContext;
        private final ConfigureChildContext   configureChildContext;
        private final NewElementCol           newElementCol;
        public DefaultListaMapper() {
            this(null, null, null);
        }
        public DefaultListaMapper(
            NewElementCol newElementCol,
            ConfigureCurrentContext configureCurrentContext,
            ConfigureChildContext configureChildContext)
        {
            this.newElementCol = newElementCol;
            this.configureCurrentContext = configureCurrentContext;
            this.configureChildContext = configureChildContext;
        }
        @Override
        @SuppressWarnings("unchecked")
        public void buildView(WicketBuildContext ctx, MView view, IModel<? extends MInstancia> model) {
            IModel<MILista<MInstancia>> mLista = $m.get(() -> (MILista<MInstancia>) model.getObject());

            if (configureCurrentContext != null)
                configureCurrentContext.configure(ctx, mLista);

            MILista<?> iLista = mLista.getObject();
            final IModel<String> label = $m.ofValue(trimToEmpty(iLista.as(AtrBasic.class).getLabel()));
            BSCol parentCol = ctx.getContainer();
            if (isNotBlank(label.getObject()))
                parentCol.appendTag("h3", new Label("_title", label));

            TemplatePanel template = parentCol.newTag("div", new TemplatePanel("t", () ->
                "<div wicket:id='lista'><div wicket:id='grid'></div></div>"));
            template.add(new ItemsView("lista", mLista, ctx, newElementCol, configureChildContext));
        }

        private static final class ItemsView extends RefreshingView<MInstancia> {
            private WicketBuildContext    ctx;
            private NewElementCol         newElementCol;
            private ConfigureChildContext configureChildContext;
            private ItemsView(String id, IModel<?> model, WicketBuildContext ctx, NewElementCol newElementCol, ConfigureChildContext configureChildContext) {
                super(id, model);
                this.ctx = ctx;
                this.newElementCol = newElementCol;
                this.configureChildContext = configureChildContext;
            }
            @Override
            protected Iterator<IModel<MInstancia>> getItemModels() {
                List<IModel<MInstancia>> list = new ArrayList<>();
                MILista<?> miLista = (MILista<?>) getDefaultModelObject();
                for (int i = 0; i < miLista.size(); i++)
                    list.add(new MInstanciaItemListaModel<>(getDefaultModel(), i));
                return list.iterator();
            }
            @Override
            protected void populateItem(Item<MInstancia> item) {
                BSGrid grid = new BSGrid("grid");
                item.add(grid);

                int index = item.getIndex();
                BSCol col = ObjectUtils.defaultIfNull(newElementCol, ItemsView::defaultNewElementCol)
                    .create(ctx, grid, item.getModel(), index);
                WicketBuildContext childCtx = ctx.createChild(col, true);
                if (configureChildContext != null)
                    configureChildContext.configure(childCtx, this.getModel(), index);
                buildForEdit(childCtx, item.getModel());
            }
            @SuppressWarnings("unchecked")
            public IModel<MILista<MInstancia>> getModel() {
                return (IModel<MILista<MInstancia>>) getDefaultModel();
            }
            static BSCol defaultNewElementCol(WicketBuildContext parentCtx, BSGrid grid, IModel<MInstancia> itemModel, int index) {
                return grid.newColInRow();
            }
        }
    }

    static class ListaSimpleTableMapper extends DefaultListaMapper {
        public ListaSimpleTableMapper() {
            super(
                null,
                ListaSimpleTableMapper::configureCurrentContext,
                ListaSimpleTableMapper::configureChildContext);
        }
        static void configureCurrentContext(WicketBuildContext ctx, IModel<MILista<MInstancia>> model) {
            MTipo<?> tElementos = model.getObject().getTipoElementos();
            if (tElementos instanceof MTipoComposto<?>) {
                Set<String> camposElemento = ((MTipoComposto<?>) tElementos).getCampos();
                if (!camposElemento.isEmpty()) {
                    int baseColWidth = BSCol.MAX_COLS / camposElemento.size();
                    int largerColWidth = baseColWidth + (BSCol.MAX_COLS - camposElemento.size() * baseColWidth);
                    HashMap<String, Integer> colWidths = new HashMap<>();
                    Iterator<String> iter = camposElemento.iterator();
                    while (iter.hasNext()) {
                        String nome = iter.next();
                        int colWidth = (iter.hasNext()) ? baseColWidth : largerColWidth;
                        colWidths.put(nome, colWidth);
                    }
                    ctx.setHint(DefaultCompostoMapper.COL_WIDTHS, colWidths);
                }
            }
        }
        static void configureChildContext(WicketBuildContext ctx, IModel<MILista<MInstancia>> model, int index) {
            ctx.setHint(ControlsFieldComponentMapper.NO_DECORATION, index > 0);
        }
    }

    static class ListaMultiPanelMapper extends DefaultListaMapper {
        public ListaMultiPanelMapper() {
            super(
                ListaMultiPanelMapper::newElementCol,
                null,
                null);
        }
        static BSCol newElementCol(WicketBuildContext parentCtx, BSGrid grid, IModel<MInstancia> itemModel, int index) {
            BSContainer<?> panel = grid.newColInRow()
                .newTag("div", true, "class='panel panel-default'", id -> new BSContainer<>(id));

            MInstancia iItem = itemModel.getObject();
            String label = iItem.as(AtrBasic.class).getLabel();
            if (StringUtils.isNotBlank(label)) {
                panel.newTag("div", true, "class='panel-heading'", id -> new Label(id, label));
            }
            BSGrid panelBody = panel.newTag("div", true, "class='panel-body'", id -> new BSGrid(id));
            return panelBody.newColInRow();
        }
    }
}
