package br.net.mirante.singular.form.wicket.mapper;

import java.util.List;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.basic.view.MTabView;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.model.SInstanceCampoModel;
import br.net.mirante.singular.form.wicket.panel.BSPanelGrid;

public class TabMapper extends DefaultCompostoMapper {

    @Override
    @SuppressWarnings("unchecked")
    public void buildView(WicketBuildContext ctx) {

        final SIComposite instance = (SIComposite) ctx.getModel().getObject();
        final STypeComposite<SIComposite> tComposto = (STypeComposite<SIComposite>) instance.getType();
        MTabView tabView = (MTabView) tComposto.getView();

        BSPanelGrid panel = new BSPanelGrid("panel") {
            @Override
            public void updateTab(List<String> subtree) {
                renderTab(subtree, this, ctx);
            }
        };

        for (MTabView.MTab tab : tabView.getTabs()) {
            panel.addTab(tab.getId(), tab.getTitulo(), tab.getNomesTipo());
        }

        ctx.getContainer().newTag("div", panel);

        MTabView.MTab tabDefault = tabView.getTabDefault();

        renderTab(tabDefault.getNomesTipo(), panel, ctx);

    }

    private void renderTab(List<String> nomesTipo, BSPanelGrid panel, WicketBuildContext ctx) {
        for (String nomeTipo : nomesTipo) {
            final SInstanceCampoModel<SInstance> subtree = new SInstanceCampoModel<>(ctx.getModel(), nomeTipo);
            final WicketBuildContext childContext = ctx.createChild(panel.getContainer().newGrid().newColInRow(), true, subtree);
            childContext.init(ctx.getUiBuilderWicket(), ctx.getViewMode());
            childContext.getUiBuilderWicket().build(childContext, childContext.getViewMode());
            childContext.initContainerBehavior();
        }
    }
}
