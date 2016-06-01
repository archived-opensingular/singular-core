package br.net.mirante.singular.studio.wicket;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.context.SFormConfig;
import br.net.mirante.singular.form.document.RefType;
import br.net.mirante.singular.form.wicket.panel.SingularFormPanel;
import br.net.mirante.singular.studio.core.CollectionCanvas;
import br.net.mirante.singular.studio.spring.StudioCollectionToolboxBean;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;

import javax.inject.Inject;

@SuppressWarnings("serial")
public class SingularStudioFormPanel extends SingularStudioPanel {

    @Inject
    private StudioCollectionToolboxBean studioCollectionToolboxBean;

    private Form<?> form;
    private BSContainer formPanel;


    /**
     * Construtor do painel
     *
     * @param id           o markup id wicket
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

        queue(form = new Form<>("studio-form"));
        queue(formPanel = new BSContainer("form-panel"));

        formPanel.appendTag("div", true, "",
                new SingularFormPanel<Class<SType<?>>>("singular-form-panel", studioCollectionToolboxBean) {
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
