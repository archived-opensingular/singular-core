package br.net.mirante.singular.studio.wicket;

import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.studio.core.CollectionConfigCollector;
import org.apache.wicket.markup.html.panel.Panel;

public class SingularStudioListPanel<TYPE extends SType<?>> extends Panel {

    private final SingularStudioCollectionPanel.PanelControl panelControl;

    public SingularStudioListPanel(String id, SingularStudioCollectionPanel.PanelControl panelControl, CollectionConfigCollector<TYPE> configCollector) {
        super(id);
        this.panelControl = panelControl;
    }
}
