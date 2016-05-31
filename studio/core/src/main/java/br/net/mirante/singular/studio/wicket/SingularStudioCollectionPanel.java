package br.net.mirante.singular.studio.wicket;

import br.net.mirante.singular.commons.lambda.IFunction;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.studio.core.CollectionCanvas;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;

import java.io.Serializable;
import java.util.Optional;

public abstract class SingularStudioCollectionPanel<TYPE extends SType<?>> extends Panel {

    private final PanelControl panelControl = new PanelControl();
    private boolean showList = true;
    private Object formID;
    private CollectionCanvas<TYPE> canvas;


    public SingularStudioCollectionPanel(String content, CollectionCanvas<TYPE> canvas) {
        super(content);
        this.canvas = canvas;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onConfigure() {
        super.onConfigure();
        if (showList) {
            this.addOrReplace(new SingularStudioListPanel("content", (IFunction<Class<SType<?>>, SType<?>>)this::loadType, panelControl, canvas));
        } else {
            this.addOrReplace(new SingularStudioFormPanel("content", panelControl, formID, null));
        }
    }

    public abstract SType<?> loadType(Class<SType<?>> sTypeClass);

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
