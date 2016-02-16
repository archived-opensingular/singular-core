package br.net.mirante.singular.form.wicket.panel;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.feedback.FencedFeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.ServiceRef;
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

/**
 * Painel que encapusla a lógica de criação de forms dinâmicos
 */
public abstract class SingularFormPanel extends Panel {

    /**
     * Referência ao ServiceRegistry utilizado na aplicação cliente
     */
    private final ServiceRegistry serviceRegistry;

    /**
     * Container onde os componentes serão adicionados
     */
    private BSGrid container = new BSGrid("generated");

    /**
     * Instancia root do pacote
     */
    private MInstanceRootModel<? extends SInstance> rootInstance;

    /**
     * ViewMode, por padrão é de edição
     */
    private ViewMode viewMode = ViewMode.EDITION;

    /**
     * Permite apresentar anotações em conjunto.
     */
    private boolean annotationEnabled = false;

    /**
     * Construtor principal do painel
     * 
     * @param id
     *            o markup id wicket
     * @param serviceRegistry
     *            utilizado para lookup de serviços
     */
    public SingularFormPanel(final String id,
                             final ServiceRegistry serviceRegistry) {
        super(id);
        this.serviceRegistry = serviceRegistry;
    }

    /**
     * Método abstrato utilizado para recuperar o tipo root
     * 
     * @return o tipo root para criação do form
     */
    protected abstract SType<?> getTipo();

    /**
     * Cria ou substitui o container
     */
    public void updateContainer() {
        container = new BSGrid("generated");
        addOrReplace(container);
        buildContainer();
    }

    /**
     * Implementação padrão para popular a instancia, caso seja necessário
     * popular a partir de banco de dados é necessário sobrescrever este método
     * 
     * @param tipo
     *            o tipo 'root'
     * @return instancia criada e populada
     */
    protected MInstanceRootModel<SInstance> populateInstance(final SType<?> tipo) {
        return new MInstanceRootModel<>(tipo.novaInstancia());
    }

    /**
     * Cria a instancia a partir do tipo.
     */
    private void createInstance() {
        SType<?> tipo = getTipo();
        rootInstance = populateInstance(tipo);
        bindDefaultServices(getRootInstance().getObject().getDocument());
        bindAdditionalServices(getRootInstance().getObject().getDocument());
    }

    /**
     * Faz o bind dos serviços padrões
     * 
     * @param document
     *            o document
     */
    protected void bindDefaultServices(final SDocument document) {
        document.setAttachmentPersistenceTemporaryHandler(ServiceRef.of(new InMemoryAttachmentPersitenceHandler()));
        document.addServiceRegistry(getServiceRegistry());
        document.setAttachmentPersistencePermanentHandler(
                ServiceRef.of(getServiceRegistry().lookupService(IAttachmentPersistenceHandler.class)));
    }

    /**
     * Método chamado após criação da instancia, deve ser sobrescrito para
     * incluir services adicionais
     * 
     * @param document
     *            o document
     */
    protected void bindAdditionalServices(final SDocument document) {}

    /**
     * Método wicket, local onde os componentes são adicionados
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();
        createInstance();
        updateContainer();
        add(buildFeedbackPanel());
    }

    /**
     * Chama o builder wicket para construção do formulário
     */
    private void buildContainer() {
        WicketBuildContext ctx = new WicketBuildContext(container.newColInRow(), buildBodyContainer(), getRootInstance());
        if(annotationEnabled()){    ctx.enableAnnotation();}
        getSingularFormContext().getUIBuilder().build(ctx, getViewMode());
    }

    public boolean annotationEnabled(){return annotationEnabled;};

    public void enableAnnotation() {this.annotationEnabled = true;}
    public void disableAnnotation() {this.annotationEnabled = false;}

    /**
     * Constrói o body container
     * 
     * @return body container utilizado no builder
     */
    private BSContainer<?> buildBodyContainer() {
        BSContainer<?> bodyContainer = new BSContainer<>("body-container");
        addOrReplace(bodyContainer);
        return bodyContainer;
    }

    /**
     * Constroi o feedback panel
     * @return componente criado
     */
    private Component buildFeedbackPanel() {
        return new FencedFeedbackPanel("feedback").add(new Behavior() {
            @Override
            public void onConfigure(Component component) {
                component.setVisible(((FencedFeedbackPanel) component).anyMessage());
            }
        });
    }

    /**
     * recupera o formcontext
     * 
     * @return implementação de form context
     */
    public SingularFormContextWicket getSingularFormContext() {
        return getServiceRegistry().lookupService(SingularFormContextWicket.class);
    }

    public IModel<? extends SInstance> getRootInstance() {
        return rootInstance;
    }

    public ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    public ViewMode getViewMode() {
        return viewMode;
    }

    public void setViewMode(ViewMode viewMode) {
        this.viewMode = viewMode;
    }
}
