package br.net.mirante.singular.studio.wicket;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.context.SFormConfig;
import br.net.mirante.singular.form.wicket.panel.SingularFormPanel;

public class SingularStudioFormPanel<TYPE extends SType<?>> extends SingularFormPanel<String> {

    private final SingularStudioCollectionPanel.PanelControl panelControl;

    /**
     * Construtor do painel
     *
     * @param id                 o markup id wicket
     * @param panelControl
     * @param formID
     * @param singularFormConfig configuração para manipulação do documento a ser criado ou
     */
    public SingularStudioFormPanel(String id, SingularStudioCollectionPanel.PanelControl panelControl, Object formID, SFormConfig<String> singularFormConfig) {
        super(id, singularFormConfig);
        this.panelControl = panelControl;
    }

    @Override
    protected SInstance createInstance(SFormConfig<String> singularFormConfig) {
        return null;
    }
}
