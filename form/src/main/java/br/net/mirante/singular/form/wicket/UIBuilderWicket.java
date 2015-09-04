package br.net.mirante.singular.form.wicket;

import static org.apache.commons.lang3.StringUtils.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
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
        MAPPER_REGISTRY.registerMapper(MTipoComposto.class, MView.class, CompostoMapper::new);
        MAPPER_REGISTRY.registerMapper(MTipoComposto.class, MTabView.class, CompostoMapper::new);
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

    static class CompostoMapper implements IWicketComponentMapper {
        static final HintKey<Map<String, Integer>> COL_WIDTHS = new HintKey<Map<String, Integer>>() {};
        @Override
        @SuppressWarnings("unchecked")
        public void buildView(WicketBuildContext ctx, MView view, IModel<? extends MInstancia> iModel) {
            final Map<String, Integer> hintColWidths = ctx.getHint(COL_WIDTHS, new HashMap<>());

            MInstancia instancia = iModel.getObject();
            MIComposto composto = (MIComposto) instancia;
            MTipoComposto<MIComposto> tComposto = (MTipoComposto<MIComposto>) composto.getMTipo();

            BSCol parentCol = ctx.getContainer();
            BSGrid grid = parentCol.newGrid();
            BSRow row = grid.newRow();

            for (String nomeCampo : tComposto.getCampos()) {
                final MTipo<?> tCampo = tComposto.getCampo(nomeCampo);
                final MInstanciaCampoModel<MInstancia> mCampo = new MInstanciaCampoModel<>(iModel, tCampo.getNomeSimples());
                final MInstancia iCampo = mCampo.getObject();
                final IModel<String> label = $m.ofValue(trimToEmpty(iCampo.getValorAtributo(MPacoteBasic.ATR_LABEL)));
                final int colspan = (hintColWidths.containsKey(nomeCampo)) ? hintColWidths.get(nomeCampo) : BSCol.MAX_COLS;
                if (iCampo instanceof MIComposto) {
                    final MICompostoModel<MIComposto> mCampoComposto = new MICompostoModel<>(mCampo);

                    final BSCol col = row.newCol().md(colspan);
                    if (isNotBlank(label.getObject()))
                        col.appendTag("h3", new Label("_title", label));
                    buildForEdit(ctx.createChild(col.newGrid().newColInRow(), true), mCampoComposto);

                } else {
                    buildForEdit(ctx.createChild(row.newCol().md(colspan), true), mCampo);
                }
            }
        }
    }

    static class DefaultListaMapper implements IWicketComponentMapper {
        @Override
        public void buildView(WicketBuildContext ctx, MView view, IModel<? extends MInstancia> model) {
            MInstancia instancia = model.getObject();
            MILista<?> iLista = (MILista<?>) instancia;

            configureCurrentContext(ctx, iLista);

            if (iLista.isEmpty()) {
                int tamanhoInicial = ObjectUtils.defaultIfNull(iLista.as(AtrBasic.class).getTamanhoInicial(), 0);
                for (int i = tamanhoInicial; i > 0; i--)
                    iLista.addNovo();
            }

            final IModel<String> label = $m.ofValue(trimToEmpty(iLista.getValorAtributo(MPacoteBasic.ATR_LABEL)));
            BSCol parentCol = ctx.getContainer();
            if (isNotBlank(label.getObject()))
                parentCol.appendTag("h3", new Label("_title", label));

            BSGrid grid = parentCol.newGrid();
            for (int i = 0; i < iLista.size(); i++) {
                MInstanciaItemListaModel<MInstancia> mItem = new MInstanciaItemListaModel<>(model, i);
                BSCol col = newElementCol(ctx, grid, iLista, mItem, i);
                WicketBuildContext childCtx = ctx.createChild(col, true);
                configureChildContext(childCtx, iLista, i);
                buildForEdit(childCtx, mItem);
            }
        }
        protected BSCol newElementCol(WicketBuildContext parentCtx, BSGrid grid, MILista<?> instanciaLista, IModel<MInstancia> itemModel, int index) {
            return grid.newColInRow();
        }
        protected void configureCurrentContext(WicketBuildContext ctx, MILista<?> instanciaLista) {}
        protected void configureChildContext(WicketBuildContext childCtx, MILista<?> instanciaLista, int index) {}
    }

    static class ListaSimpleTableMapper extends DefaultListaMapper {
        @Override
        protected void configureChildContext(WicketBuildContext childCtx, MILista<?> instanciaLista, int index) {
            childCtx.setHint(ControlsFieldComponentMapper.NO_DECORATION, index > 0);
        }
        @Override
        protected void configureCurrentContext(WicketBuildContext ctx, MILista<?> iLista) {
            MTipo<?> tElementos = iLista.getTipoElementos();
            if (tElementos instanceof MTipoComposto<?>) {
                Set<String> camposElemento = ((MTipoComposto<?>) tElementos).getCampos();
                if (!camposElemento.isEmpty()) {
                    int baseColWidth = BSCol.MAX_COLS / camposElemento.size();
                    int largerColWidth = baseColWidth + (BSCol.MAX_COLS - camposElemento.size() * baseColWidth);
                    Map<String, Integer> colWidths = new HashMap<>();
                    Iterator<String> iter = camposElemento.iterator();
                    while (iter.hasNext()) {
                        String nome = iter.next();
                        int colWidth = (iter.hasNext()) ? baseColWidth : largerColWidth;
                        colWidths.put(nome, colWidth);
                    }
                    ctx.setHint(CompostoMapper.COL_WIDTHS, colWidths);
                }
            }
        }
    }

    static class ListaMultiPanelMapper extends DefaultListaMapper {
        @Override
        protected BSCol newElementCol(WicketBuildContext parentCtx, BSGrid grid, MILista<?> instanciaLista, IModel<MInstancia> itemModel, int index) {
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
