package br.net.mirante.singular.studio.wicket;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.context.SFormConfig;
import br.net.mirante.singular.form.document.RefType;
import br.net.mirante.singular.form.wicket.panel.SingularFormPanel;
import br.net.mirante.singular.studio.core.CollectionCanvas;
import br.net.mirante.singular.studio.spring.StudioCollectionToolboxBean;

import javax.inject.Inject;

@SuppressWarnings("serial")
public class SingularStudioFormPanel<TYPE extends SType<?>> extends SingularFormPanel<String> implements SingularStudioPanel {

    private final SingularStudioCollectionPanel.PanelControl panelControl;
    private final CollectionCanvas canvas;

    @Inject
    private StudioCollectionToolboxBean studioCollectionToolboxBean;


    /**
     * Construtor do painel
     *
     * @param id                 o markup id wicket
     * @param panelControl
     * @param formID
     * @param singularFormConfig configuração para manipulação do documento a ser criado ou
     * @param canvas
     */
    public SingularStudioFormPanel(String id, SingularStudioCollectionPanel.PanelControl panelControl, Object formID, SFormConfig<String> singularFormConfig, CollectionCanvas canvas) {
        super(id, singularFormConfig);
        this.panelControl = panelControl;
        this.canvas = canvas;
    }

    @Override
    protected SInstance createInstance(SFormConfig<String> singularFormConfig) {
        return studioCollectionToolboxBean.getDocumentFactory().createInstance(new RefType() {
            @Override
            protected SType<?> retrieve() {
                return sType();
            }
        });
    }

    @Override
    public StudioCollectionToolboxBean getToolbox() {
        return studioCollectionToolboxBean;
    }

    @Override
    public CollectionCanvas getCanvas() {
        return canvas;
    }
}
