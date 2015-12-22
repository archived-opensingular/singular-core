package br.net.mirante.singular.form.wicket.panel;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.mform.core.attachment.handlers.InMemoryAttachmentPersitenceHandler;
import br.net.mirante.singular.form.mform.document.SDocument;
import br.net.mirante.singular.form.mform.document.ServiceRegistry;
import br.net.mirante.singular.form.wicket.SingularFormContextWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.model.MInstanceRootModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.feedback.FencedFeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class BelverBasePanel extends Panel {

    private final ServiceRegistry serviceRegistry;
    private final Class<? extends MPacote> mPacoteClass;
    private final String rootPath;

    private BSGrid container = new BSGrid("generated");

    private MInstanceRootModel<? extends MInstancia> rootInstance;

    public BelverBasePanel(final String id,
                           final ServiceRegistry serviceRegistry,
                           final Class<? extends MPacote> mPacoteClass,
                           final String rootPath) {
        super(id);
        this.serviceRegistry = serviceRegistry;
        this.mPacoteClass = mPacoteClass;
        this.rootPath = rootPath;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        createInstance();
        updateContainer();
        add(buildFeedbackPanel());
    }

    private void createInstance() {
        final MDicionario dicionario = MDicionario.create();
        final MPacote pacote = dicionario.carregarPacote(mPacoteClass);
        MTipo<?> tipo = pacote.getTipoLocal(rootPath);
        rootInstance = new MInstanceRootModel<>(tipo.novaInstancia());
        bindDefaultServices(getRootInstance().getObject().getDocument());
    }

    public void updateContainer() {
        container = new BSGrid("generated");
        addOrReplace(container);
        buildContainer();
    }

    private void buildContainer() {
        WicketBuildContext ctx = new WicketBuildContext(container.newColInRow(), buildBodyContainer());
        getSingularFormContext().getUIBuilder().build(ctx, getRootInstance(), getViewMode());
    }

    private BSContainer<?> buildBodyContainer() {
        BSContainer<?> bodyContainer = new BSContainer<>("body-container");
        addOrReplace(bodyContainer);
        return bodyContainer;
    }

    private void bindDefaultServices(SDocument document) {
        document.setAttachmentPersistenceHandler(ServiceRef.of(new InMemoryAttachmentPersitenceHandler()));
        document.bindLocalService(SDocument.FILE_PERSISTENCE_SERVICE, IAttachmentPersistenceHandler.class,
                ServiceRef.of(getServiceRegistry().lookupService(IAttachmentPersistenceHandler.class)));
        document.addServiceRegistry(getServiceRegistry());
    }

    private Component buildFeedbackPanel() {
        return new FencedFeedbackPanel("feedback").add(new Behavior() {
            @Override
            public void onConfigure(Component component) {
                component.setVisible(((FencedFeedbackPanel) component).anyMessage());
            }
        });
    }

    public ViewMode getViewMode() {
        return ViewMode.EDITION;
    }

    public SingularFormContextWicket getSingularFormContext() {
        return getServiceRegistry().lookupService(SingularFormContextWicket.class);
    }

    public IModel<? extends MInstancia> getRootInstance() {
        return rootInstance;
    }

    public ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }
}
