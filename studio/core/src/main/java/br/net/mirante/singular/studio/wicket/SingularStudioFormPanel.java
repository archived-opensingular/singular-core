package br.net.mirante.singular.studio.wicket;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.context.SFormConfig;
import br.net.mirante.singular.form.document.RefType;
import br.net.mirante.singular.form.wicket.panel.SingularFormPanel;
import br.net.mirante.singular.studio.core.CollectionCanvas;
import br.net.mirante.singular.studio.spring.StudioCollectionToolboxBean;
import org.apache.wicket.markup.html.WebMarkupContainer;

import javax.inject.Inject;

@SuppressWarnings("serial")
public class SingularStudioFormPanel  extends SingularStudioPanel {

    @Inject
    private StudioCollectionToolboxBean studioCollectionToolboxBean;


    /**
     * Construtor do painel
     *
     * @param id                 o markup id wicket
     * @param panelControl
     * @param formID
     * @param canvas
     */
    public SingularStudioFormPanel(String id, SingularStudioCollectionPanel.PanelControl panelControl, CollectionCanvas canvas, Object formID) {
        super(id, panelControl, canvas);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        queue(new WebMarkupContainer("portletContainer"));
        queue(new SingularFormPanel<Class<SType<?>>>("portletBodyContainer", studioCollectionToolboxBean){
            @Override
            protected SInstance createInstance(SFormConfig<Class<SType<?>>> singularFormConfig) {
                return studioCollectionToolboxBean.getDocumentFactory().createInstance(new RefType() {
                    @Override
                    protected SType<?> retrieve() {
                        return sType();
                    }
                });
            }
        });
    }
}
