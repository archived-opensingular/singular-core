package br.net.mirante.singular.form.wicket.mapper;

import java.util.List;
import java.util.function.Consumer;

import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.basic.view.MTabView;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.model.MInstanciaCampoModel;
import br.net.mirante.singular.form.wicket.panel.BSPanelGrid;

public class TabMapper extends DefaultCompostoMapper {

    @Override
    public void buildView(WicketBuildContext ctx, IModel<? extends MInstancia> model) {

        final MIComposto instance = (MIComposto) model.getObject();
        final MTipoComposto<MIComposto> tComposto = (MTipoComposto<MIComposto>) instance.getMTipo();
        MTabView tabView = (MTabView) tComposto.getView();

        BSPanelGrid panel = new BSPanelGrid("panel");

        for (MTabView.MTab tab : tabView.getTabs()) {
            panel.addTab(tab.getTitulo(), tab.getNomesTipo());
        }

        ctx.getContainer().newTag("div", panel);

        MTabView.MTab tabDefault = tabView.getTabDefault();

        WicketBuildContext child = ctx.createChild(panel.getContainer().newGrid().newColInRow(), true);
        child.init(model, ctx.getUiBuilderWicket(), ctx.getViewMode());

        Consumer<List<String>> callback = tab -> renderTab(model, tab, child);

        panel.registerOnTabChange(callback);

        renderTab(model, tabDefault.getNomesTipo(), child);

    }

    private void renderTab(IModel<? extends MInstancia> model, List<String> nomesTipo, WicketBuildContext ctx) {
        for (String nomeTipo : nomesTipo) {
            MInstanciaCampoModel<MInstancia> subtree = new MInstanciaCampoModel<>(model, nomeTipo);
            super.buildView(ctx, subtree);
        }
    }
}
