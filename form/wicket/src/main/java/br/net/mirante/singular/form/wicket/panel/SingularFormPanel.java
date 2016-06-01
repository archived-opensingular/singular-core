/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.panel;

import java.io.Serializable;
import java.util.Objects;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.feedback.FencedFeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.context.SFormConfig;
import br.net.mirante.singular.form.document.RefSDocumentFactory;
import br.net.mirante.singular.form.document.SDocumentFactory;
import br.net.mirante.singular.form.document.ServiceRegistry;
import br.net.mirante.singular.form.document.TypeLoader;
import br.net.mirante.singular.form.wicket.SingularFormContextWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.enums.AnnotationMode;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.model.MInstanceRootModel;
import br.net.mirante.singular.form.wicket.util.WicketFormProcessing;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;

/**
 * Painel que encapusla a lógica de criação de forms dinâmicos
 */
public abstract class SingularFormPanel<KEY extends Serializable> extends Panel {

    /**
     * Container onde os componentes serão adicionados
     */
    private BSGrid container = new BSGrid("generated");

    /**
     * Instancia root do pacote
     */
    private final MInstanceRootModel<SInstance> rootInstance;

    /**
     * ViewMode, por padrão é de edição
     */
    private ViewMode viewMode = ViewMode.EDITION;

    /**
     * Permite apresentar anotações em conjunto.
     */
    private AnnotationMode annotation = AnnotationMode.NONE;

    private RefSDocumentFactory documentFactoryRef;

    private transient SFormConfig<KEY> singularFormConfig;

    /**
     * Construtor do painel
     *
     * @param id
     *            o markup id wicket
     * @param singularFormConfig
     *            configuração para manipulação do documento a ser criado ou
     *            recuperado.
     */
    public SingularFormPanel(String id, SFormConfig<KEY> singularFormConfig) {
        super(id);
        this.rootInstance = new MInstanceRootModel<>();
        this.singularFormConfig = Objects.requireNonNull(singularFormConfig);
        this.documentFactoryRef = singularFormConfig.getDocumentFactory().getDocumentFactoryRef();
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
     * @param singularFormConfig
     *            Configuração do formulário em termos de recuperação de
     *            referências e configurador inicial da instancia e SDocument
     * @return Não pode ser Null
     */
    protected abstract SInstance createInstance(SFormConfig<KEY> singularFormConfig);

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

    /**
     * Chama o builder wicket para construção do formulário
     */
    private void buildContainer() {
        WicketBuildContext ctx = new WicketBuildContext(container.newColInRow(), buildBodyContainer(), getRootInstance());
        ctx.annotation(annotation());
        getSingularFormContext().getUIBuilder().build(ctx, getViewMode());
    }

    public AnnotationMode annotation(){return annotation;};

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

    public SFormConfig<KEY> getSingularFormConfig() {
        return singularFormConfig;
    }

    public String getRootTypeSubtitle() {
        return getRootInstance().getObject().asAtr().getSubtitle();
    }

}
