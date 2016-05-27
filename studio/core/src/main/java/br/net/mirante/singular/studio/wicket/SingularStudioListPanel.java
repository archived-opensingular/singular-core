package br.net.mirante.singular.studio.wicket;

import org.apache.wicket.markup.html.panel.Panel;

public class SingularStudioListPanel extends Panel {

    private final SingularStudioCollectionPanel.PanelControl panelControl;

    public SingularStudioListPanel(String id, SingularStudioCollectionPanel.PanelControl panelControl) {
        super(id);
        this.panelControl = panelControl;
    }
}
