package br.net.mirante.singular.studio.wicket;

import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.studio.core.CollectionConfigCollector;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;

import java.io.Serializable;

public class SingularStudioCollectionPanel<TYPE extends SType<?>> extends Panel {

    private final PanelControl panelControl = new PanelControl();
    private boolean showList = true;
    private Object formID;
    private CollectionConfigCollector<TYPE> configCollector;

    public SingularStudioCollectionPanel(String id, CollectionConfigCollector<TYPE> configCollector) {
        super(id);
        this.configCollector = configCollector;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onConfigure() {
        super.onConfigure();
        if (showList) {
            this.addOrReplace(new SingularStudioListPanel("content", panelControl, configCollector));
        } else {
            this.addOrReplace(new SingularStudioFormPanel("content", panelControl, configCollector, formID, null));
        }
    }

    public class PanelControl implements Serializable {

        public void setList() {
            SingularStudioCollectionPanel.this.showList = true;
        }

        public void setForm(Object formID) {
            SingularStudioCollectionPanel.this.showList = false;
            SingularStudioCollectionPanel.this.formID = formID;
        }

        public void showList(AjaxRequestTarget target) {
            setList();
            target.add(SingularStudioCollectionPanel.this);
        }

        /**
         * @param target
         * @param formID Se nulo, cadastrar um novo form
         */
        public void showForm(AjaxRequestTarget target, Object formID) {
            setForm(formID);
            target.add(SingularStudioCollectionPanel.this);
        }
    }
}
