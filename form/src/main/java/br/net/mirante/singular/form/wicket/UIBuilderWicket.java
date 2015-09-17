package br.net.mirante.singular.form.wicket;

import static org.apache.commons.lang3.StringUtils.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
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
import br.net.mirante.singular.form.wicket.model.MInstanciaCampoModel;
import br.net.mirante.singular.form.wicket.model.MInstanciaItemListaModel;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSCol;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSLabel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSRow;
import br.net.mirante.singular.util.wicket.bootstrap.layout.IBSComponentFactory;
import br.net.mirante.singular.util.wicket.bootstrap.layout.IBSGridCol.BSGridSize;
import br.net.mirante.singular.util.wicket.bootstrap.layout.TemplatePanel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.table.BSTRow;
import br.net.mirante.singular.util.wicket.bootstrap.layout.table.BSTSection;
import br.net.mirante.singular.util.wicket.form.YearMonthField;
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
        MAPPER_REGISTRY.registerMapper(MTipoLista.class, MView.class, ListaTableMapper::new);
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

    interface ControlsFieldComponentMapper extends IWicketComponentMapper {
        HintKey<Boolean> NO_DECORATION = new HintKey<Boolean>() {};
        Component appendInput(BSControls formGroup, IModel<? extends MInstancia> model, IModel<String> labelModel);
        @Override
        default void buildView(WicketBuildContext ctx, MView view, IModel<? extends MInstancia> model) {
            final boolean hintNoDecoration = ctx.getHint(NO_DECORATION, false);

            MInstancia instancia = model.getObject();
            String label = trimToEmpty(instancia.as(MPacoteBasic.aspect()).getLabel());
            ValueModel<String> labelModel = $m.ofValue(label);
            BSControls controls = ctx.getContainer().newFormGroup();

            BSLabel bsLabel = new BSLabel("label", labelModel);
            if (hintNoDecoration)
                bsLabel.add($b.classAppender("visible-sm visible-xs"));

            controls.appendLabel(bsLabel);
            Component comp = appendInput(controls, model, labelModel);
            controls.appendFeedback();

            Integer size = instancia.as(MPacoteBasic.aspect()).getTamanhoEdicao();
            if ((comp instanceof TextField<?>) && (size != null)) {
                comp.add($b.attr("size", size));
            }
        }
    }

    static class StringMapper implements ControlsFieldComponentMapper {
        @Override
        public Component appendInput(BSControls formGroup, IModel<? extends MInstancia> model, IModel<String> labelModel) {
            FormComponent<?> comp;
            if (model.getObject().as(AtrBasic::new).isMultiLinha())
                formGroup
                    .appendTextarea(comp = new TextArea<>(model.getObject().getNome(), new MInstanciaValorModel<>(model))
                        .setLabel(labelModel));
            else
                formGroup
                    .appendInputText(comp = new TextField<>(model.getObject().getNome(), new MInstanciaValorModel<>(model), String.class)
                        .setLabel(labelModel));
            return comp;
        }
    }

    static class DateMapper implements ControlsFieldComponentMapper {
        @Override
        public Component appendInput(BSControls formGroup, IModel<? extends MInstancia> model, IModel<String> labelModel) {
            TextField<?> comp = new TextField<>(model.getObject().getNome(), new MInstanciaValorModel<>(model), Date.class);
            formGroup.appendInputText(comp.setLabel(labelModel));
            return comp;
        }
    }

    static class YearMonthMapper implements ControlsFieldComponentMapper {
        @Override
        public Component appendInput(BSControls formGroup, IModel<? extends MInstancia> model, IModel<String> labelModel) {
            YearMonthField comp = new YearMonthField(model.getObject().getNome(), new MInstanciaValorModel<>(model));
            formGroup.appendInputText(comp.setLabel(labelModel));
            return comp;
        }
    }

    static class IntegerMapper implements ControlsFieldComponentMapper {
        @Override
        public Component appendInput(BSControls formGroup, IModel<? extends MInstancia> model, IModel<String> labelModel) {
            TextField<Integer> comp = new TextField<>(model.getObject().getNome(), new MInstanciaValorModel<>(model), Integer.class);
            formGroup.appendInputText(comp.setLabel(labelModel));
            return comp;
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

            BSContainer<?> parentCol = ctx.getContainer();
            BSGrid grid = parentCol.newGrid();
            BSRow row = grid.newRow();

            for (String nomeCampo : tComposto.getCampos()) {
                final MTipo<?> tCampo = tComposto.getCampo(nomeCampo);
                final MInstanciaCampoModel<MInstancia> mCampo = new MInstanciaCampoModel<>(model, tCampo.getNomeSimples());
                final MInstancia iCampo = mCampo.getObject();
                final IModel<String> label = $m.ofValue(trimToEmpty(iCampo.as(AtrBasic::new).getLabel()));
                final int colspan = (hintColWidths.containsKey(nomeCampo))
                    ? hintColWidths.get(nomeCampo)
                    : iCampo.as(AtrWicket::new).getLarguraPref(BSCol.MAX_COLS);
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

    static class TableRowCompostoMapper implements IWicketComponentMapper {
        static final HintKey<HashMap<String, Integer>> COL_WIDTHS = new HintKey<HashMap<String, Integer>>() {};
        static final HintKey<Integer>                  COL        = new HintKey<Integer>() {};
        @Override
        @SuppressWarnings("unchecked")
        public void buildView(WicketBuildContext ctx, MView view, IModel<? extends MInstancia> model) {
            MInstancia instancia = model.getObject();
            MIComposto composto = (MIComposto) instancia;
            MTipoComposto<MIComposto> tComposto = (MTipoComposto<MIComposto>) composto.getMTipo();

            BSContainer<?> parentCol = ctx.getContainer();
            BSTRow tr = parentCol.newTag("tr", true, "",
                    (IBSComponentFactory<BSTRow>) id -> new BSTRow(id, BSGridSize.XS));

            for (String nomeCampo : tComposto.getCampos()) {
                final MTipo<?> tCampo = tComposto.getCampo(nomeCampo);
                final MInstanciaCampoModel<MInstancia> mCampo = new MInstanciaCampoModel<>(model, tCampo.getNomeSimples());
                final MInstancia iCampo = mCampo.getObject();
                if (iCampo instanceof MIComposto) {
                    buildForEdit(ctx.createChild(tr.newCol(), true), mCampo);
                } else {
                    buildForEdit(ctx.createChild(tr.newCol(), true), mCampo);
                }
            }
        }
    }

    static abstract class AbstractListaMapper implements IWicketComponentMapper {
        interface NewElementCol extends Serializable {
            BSContainer<?> create(WicketBuildContext parentCtx, BSGrid grid, IModel<MInstancia> itemModel, int index);
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
        public AbstractListaMapper() {
            this(null, null, null);
        }
        public AbstractListaMapper(
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
            final IModel<String> label = $m.ofValue(trimToEmpty(iLista.as(AtrBasic::new).getLabel()));
            BSContainer<?> parentCol = ctx.getContainer();
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
                setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance());
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
                BSContainer<?> col = ObjectUtils.defaultIfNull(newElementCol, ItemsView::defaultNewElementCol)
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
            static BSContainer<?> defaultNewElementCol(WicketBuildContext parentCtx, BSGrid grid, IModel<MInstancia> itemModel, int index) {
                return grid.newColInRow();
            }
        }
    }

    static class ListaTableMapper implements IWicketComponentMapper {
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

            final TemplatePanel template = parentCol.newTag("div", new TemplatePanel("t", () -> ""
                + "<table wicket:id='table' class='table table-condensed table-unstyled'>"
                + "<thead wicket:id='head'></thead>"
                + "<tbody>"
                + "<wicket:container wicket:id='items'><tr wicket:id='row'></tr></wicket:container>"
                + "</tbody>"
                + "</table>"));
            final WebMarkupContainer table = new WebMarkupContainer("table");
            final TRsView trView = new TRsView("items", mLista, ctx);
            final BSTSection thead = new BSTSection("head").setTagName("thead");

            final MTipo<?> tElementos = iLista.getTipoElementos();
            if (iLista.getTipoElementos() instanceof MTipoComposto<?>) {
                MTipoComposto<MIComposto> tComposto = (MTipoComposto<MIComposto>) tElementos;
                BSTRow tr = thead.newRow();
                for (String nomeCampo : tComposto.getCampos()) {
                    final MTipo<?> tCampo = tComposto.getCampo(nomeCampo);
                    tr.newTHeaderCell($m.ofValue(tCampo.as(AtrBasic::new).getLabel()));
                }
            } else {
                thead.setVisible(false);
            }

            template
                .add(table
                    .add(thead)
                    .add(trView));
        }
        private static final class TRsView extends RefreshingView<MInstancia> {
            private WicketBuildContext ctx;
            private TRsView(String id, IModel<MILista<MInstancia>> model, WicketBuildContext ctx) {
                super(id, model);
                setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance());
                this.ctx = ctx;
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
            @SuppressWarnings("unchecked")
            protected void populateItem(Item<MInstancia> item) {
                final IModel<MInstancia> itemModel = item.getModel();
                final BSTRow tr = new BSTRow("row", BSGridSize.MD);
                item.add(tr);

                MInstancia instancia = itemModel.getObject();
                if (instancia instanceof MIComposto) {
                    MIComposto composto = (MIComposto) instancia;
                    MTipoComposto<MIComposto> tComposto = (MTipoComposto<MIComposto>) composto.getMTipo();
                    for (String nomeCampo : tComposto.getCampos()) {
                        final MTipo<?> tCampo = tComposto.getCampo(nomeCampo);
                        final MInstanciaCampoModel<MInstancia> mCampo = new MInstanciaCampoModel<>(itemModel, tCampo.getNomeSimples());
                        buildForEdit(ctx.createChild(tr.newCol(), true), mCampo);
                    }
                } else {
                    buildForEdit(ctx.createChild(tr.newCol(), true), itemModel);
                }
            }
        }
    }

    static class ListaSimpleTableMapper extends AbstractListaMapper {
        public ListaSimpleTableMapper() {
            super(
                null,
                ListaSimpleTableMapper::configureCurrentContext,
                ListaSimpleTableMapper::configureChildContext);
        }
        private static void configureCurrentContext(WicketBuildContext ctx, IModel<MILista<MInstancia>> model) {
            MTipo<?> tElementos = model.getObject().getTipoElementos();
            if (tElementos instanceof MTipoComposto<?>) {
                MTipoComposto<?> tElemento = (MTipoComposto<?>) tElementos;
                Set<String> camposElemento = tElemento.getCampos();
                if (!camposElemento.isEmpty()) {
                    ctx.setHint(DefaultCompostoMapper.COL_WIDTHS, resolveColWidths(tElemento));
                }
            }
        }
        private static HashMap<String, Integer> resolveColWidths(MTipoComposto<?> tElemento) {
            Set<String> camposSemLargura = new HashSet<>();

            HashMap<String, Integer> colWidths = new HashMap<>();
            int colunasRestantes = BSCol.MAX_COLS;
            for (String nomeCampo : tElemento.getCampos()) {
                MTipo<?> tCampo = tElemento.getCampo(nomeCampo);
                int larguraPref = tCampo.as(AtrWicket::new).getLarguraPref(-1);
                if (larguraPref >= 0) {
                    colWidths.put(nomeCampo, larguraPref);
                    colunasRestantes -= larguraPref;
                } else {
                    camposSemLargura.add(nomeCampo);
                }
            }
            if (!camposSemLargura.isEmpty()) {
                if (colunasRestantes <= 0) {
                    // caso não sobre nenhuma coluna livre, atribuir largura 1 para os campos restantes
                    colunasRestantes = camposSemLargura.size();
                }
                int baseColWidth = colunasRestantes / camposSemLargura.size();
                int largerColWidth = baseColWidth + (colunasRestantes - camposSemLargura.size() * baseColWidth);
                for (Iterator<String> it = camposSemLargura.iterator(); it.hasNext();) {
                    String nome = it.next();
                    int colWidth = (it.hasNext()) ? baseColWidth : largerColWidth;
                    colWidths.put(nome, colWidth);
                }
            }
            return colWidths;
        }
        private static void configureChildContext(WicketBuildContext ctx, IModel<MILista<MInstancia>> model, int index) {
            ctx.setHint(ControlsFieldComponentMapper.NO_DECORATION, index > 0);
        }
    }

    static class ListaMultiPanelMapper extends AbstractListaMapper {
        public ListaMultiPanelMapper() {
            super(
                ListaMultiPanelMapper::newElementCol,
                null,
                null);
        }
        private static BSCol newElementCol(WicketBuildContext parentCtx, BSGrid grid, IModel<MInstancia> itemModel, int index) {
            BSContainer<?> panel = grid.newColInRow()
                .newTag("div", true, "class='panel panel-default'",
                    (IBSComponentFactory<BSContainer<?>>) id -> new BSContainer<>(id));

            MInstancia iItem = itemModel.getObject();
            String label = iItem.as(AtrBasic::new).getLabel();
            if (StringUtils.isNotBlank(label)) {
                panel.newTag("div", true, "class='panel-heading'",
                    (IBSComponentFactory<Label>) id -> new Label(id, label));
            }
            BSGrid panelBody = panel.newTag("div", true, "class='panel-body'",
                (IBSComponentFactory<BSGrid>) BSGrid::new);
            return panelBody.newColInRow();
        }
    }
}
