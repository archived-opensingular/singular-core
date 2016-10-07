/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.wicket.panel;

import org.opensingular.form.SInstance;
import org.opensingular.form.context.SFormConfig;
import org.opensingular.form.document.RefSDocumentFactory;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.document.ServiceRegistry;
import org.opensingular.form.document.TypeLoader;
import org.opensingular.form.wicket.SingularFormContextWicket;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.enums.AnnotationMode;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.model.SInstanceRootModel;
import org.opensingular.form.wicket.util.WicketFormProcessing;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSGrid;
import org.opensingular.lib.wicket.util.bootstrap.layout.IBSComponentFactory;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.feedback.FencedFeedbackPanel;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.resource.JQueryPluginResourceReference;

import java.io.Serializable;
import java.util.Objects;

/**
 * Painel que encapusla a lógica de criação de forms dinâmicos
 */
public abstract class SingularFormPanel<FORM_KEY extends Serializable> extends Panel {

    /**
     * Container onde os componentes serão adicionados
     */
    private BSGrid                              container = new BSGrid("generated");

    /**
     * Instancia root do pacote
     */
    private final SInstanceRootModel<SInstance> rootInstance;

    /**
     * ViewMode, por padrão é de edição
     */
    private ViewMode viewMode = ViewMode.EDIT;

    private RefSDocumentFactory documentFactoryRef;

    private transient SFormConfig<FORM_KEY> singularFormConfig;

    private final boolean nested;

    private IBSComponentFactory<Component> preFormPanelFactory;

    /**
     * Construtor do painel
     *
     * @param id                 o markup id wicket
     * @param singularFormConfig configuração para manipulação do documento a ser criado ou
     *                           recuperado.
     */
    public SingularFormPanel(String id, SFormConfig<FORM_KEY> singularFormConfig) {
        this(id, singularFormConfig, false);
    }

    /**
     * Construtor do painel
     *
     * @param id                 o markup id wicket
     * @param singularFormConfig configuração para manipulação do documento a ser criado ou
     *                           recuperado.
     */
    public SingularFormPanel(String id, SFormConfig<FORM_KEY> singularFormConfig, boolean nested) {
        super(id);
        this.rootInstance = new SInstanceRootModel<>();
        this.singularFormConfig = Objects.requireNonNull(singularFormConfig);
        this.documentFactoryRef = singularFormConfig.getDocumentFactory().getDocumentFactoryRef();
        this.nested = nested;
    }

    /**
     * Cria ou substitui o container
     */
    public void updateContainer() {
        container = new BSGrid("generated");
        addOrReplace(container);
        buildContainer();
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        WicketFormProcessing.onFormPrepare(this, getRootInstance(), false);
        if (nested) {
            container.add(new AttributeAppender("style", "padding:0px;"));
        }
    }

    /**
     * <p>
     * Cria ou recupera a instancia a ser trabalhada no painel.
     * </p>
     * <p>
     * A instância deve ser criada utilizando {@link TypeLoader} e
     * {@link SDocumentFactory} de modo a viabilizar recuperar a instância
     * corretamente no caso de deserialização. Para tando, deve ser utilizada as
     * objetos passados no parâmetro singularFormConfig.
     * </p>
     *
     * @param singularFormConfig Configuração do formulário em termos de recuperação de
     *                           referências e configurador inicial da instancia e SDocument
     * @return Não pode ser Null
     */
    protected abstract SInstance createInstance(SFormConfig<FORM_KEY> singularFormConfig);

    /**
     * Método wicket, local onde os componentes são adicionados
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();
        SInstance instance = createInstance(singularFormConfig);
        rootInstance.setObject(instance);
        updateContainer();
        add(buildFeedbackPanel());
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem.forReference(new JQueryPluginResourceReference(SingularFormPanel.class, "SingularFormPanel.js")));
        response.render(OnDomReadyHeaderItem.forScript("SingularFormPanel.initFocus('" + this.getMarkupId() + "');"));
    }

    /**
     * Chama o builder wicket para construção do formulário
     */
    private void buildContainer() {
        WicketBuildContext ctx = new WicketBuildContext(container.newColInRow(), buildBodyContainer(), getRootInstance());
        ctx.setAnnotationMode(getAnnotationMode());
        ctx.setNested(nested);
        ctx.setPreFormPanelFactory(preFormPanelFactory);
        getSingularFormContext().getUIBuilder().build(ctx, getViewMode());
    }

    public AnnotationMode getAnnotationMode() {
        return AnnotationMode.NONE;
    }

    /**
     * Constrói o body container
     *
     * @return body container utilizado no builder
     */
    private BSContainer<?> buildBodyContainer() {
        BSContainer<?> bodyContainer = new BSContainer<>("body-container");
        bodyContainer.setOutputMarkupId(true);
        addOrReplace(bodyContainer);
        return bodyContainer;
    }

    /**
     * Constroi o feedback panel
     *
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
    private SingularFormContextWicket getSingularFormContext() {
        return getServiceRegistry().lookupService(SingularFormContextWicket.class);
    }

    public final IModel<? extends SInstance> getRootInstance() {
        return rootInstance;
    }

    public ServiceRegistry getServiceRegistry() {
        return documentFactoryRef.get().getServiceRegistry();
    }

    public ViewMode getViewMode() {
        return viewMode;
    }

    public void setViewMode(ViewMode viewMode) {
        this.viewMode = viewMode;
    }

    public SFormConfig<FORM_KEY> getSingularFormConfig() {
        return singularFormConfig;
    }

    public String getRootTypeSubtitle() {
        return getRootInstance().getObject().asAtr().getSubtitle();
    }

    public void setPreFormPanelFactory(IBSComponentFactory<Component> preFormPanelFactory) {
        this.preFormPanelFactory = preFormPanelFactory;
    }
}
