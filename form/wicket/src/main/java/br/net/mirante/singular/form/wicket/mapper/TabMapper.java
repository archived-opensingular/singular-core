package br.net.mirante.singular.form.wicket.mapper;

import java.util.List;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.basic.view.MTabView;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.model.MInstanciaCampoModel;
import br.net.mirante.singular.form.wicket.panel.BSPanelGrid;

public class TabMapper extends DefaultCompostoMapper {

    @Override
    public void buildView(WicketBuildContext ctx) {

        final MIComposto instance = (MIComposto) ctx.getModel().getObject();
        final MTipoComposto<MIComposto> tComposto = (MTipoComposto<MIComposto>) instance.getMTipo();
        MTabView tabView = (MTabView) tComposto.getView();

        BSPanelGrid panel = new BSPanelGrid("panel") {
            @Override
            public void updateTab(List<String> subtree) {
                renderTab(subtree, this, ctx);
            }
        };

        for (MTabView.MTab tab : tabView.getTabs()) {
            panel.addTab(tab.getTitulo(), tab.getNomesTipo());
        }

        ctx.getContainer().newTag("div", panel);

        MTabView.MTab tabDefault = tabView.getTabDefault();

        renderTab(tabDefault.getNomesTipo(), panel, ctx);

    }

    private void renderTab(List<String> nomesTipo, BSPanelGrid panel, WicketBuildContext ctx) {
        for (String nomeTipo : nomesTipo) {
            MInstanciaCampoModel<MInstancia> subtree = new MInstanciaCampoModel<>(ctx.getModel(), nomeTipo);
            WicketBuildContext child = ctx.createChild(panel.getContainer().newGrid().newColInRow(), true, subtree);
            child.init(ctx.getUiBuilderWicket(), ctx.getViewMode());
            child.getUiBuilderWicket().build(child, child.getViewMode());
        }
    }
}
