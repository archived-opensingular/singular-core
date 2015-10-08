package br.net.mirante.singular.form.wicket.mapper;

import static br.net.mirante.singular.util.wicket.util.Shortcuts.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper;
import br.net.mirante.singular.form.wicket.UIBuilderWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.model.MInstanciaItemListaModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import br.net.mirante.singular.util.wicket.bootstrap.layout.TemplatePanel;

public abstract class AbstractListaMapper implements IWicketComponentMapper {
    interface NewElementCol extends Serializable {
        BSContainer<?> create(WicketBuildContext parentCtx, BSGrid grid, IModel<MInstancia> itemModel, int index);
    }
    interface ConfigureCurrentContext extends Serializable {
        void configure(WicketBuildContext ctx, IModel<MILista<MInstancia>> model);
    }
    interface ConfigureChildContext extends Serializable {
        void configure(WicketBuildContext ctx, IModel<MILista<MInstancia>> model, int index);
    }
    private final AbstractListaMapper.ConfigureCurrentContext configureCurrentContext;
    private final AbstractListaMapper.ConfigureChildContext   configureChildContext;
    private final AbstractListaMapper.NewElementCol           newElementCol;
    public AbstractListaMapper() {
        this(null, null, null);
    }
    public AbstractListaMapper(
        AbstractListaMapper.NewElementCol newElementCol,
        AbstractListaMapper.ConfigureCurrentContext configureCurrentContext,
        AbstractListaMapper.ConfigureChildContext configureChildContext)
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
        private AbstractListaMapper.NewElementCol         newElementCol;
        private AbstractListaMapper.ConfigureChildContext configureChildContext;
        private ItemsView(String id, IModel<?> model, WicketBuildContext ctx, AbstractListaMapper.NewElementCol newElementCol, AbstractListaMapper.ConfigureChildContext configureChildContext) {
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
            UIBuilderWicket.buildForEdit(childCtx, item.getModel());
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