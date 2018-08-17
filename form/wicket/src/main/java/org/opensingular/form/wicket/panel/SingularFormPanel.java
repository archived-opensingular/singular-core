/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.wicket.panel;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.resource.JQueryPluginResourceReference;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.decorator.action.ISInstanceActionCapable;
import org.opensingular.form.decorator.action.SInstanceAnnotationActionsProvider;
import org.opensingular.form.decorator.action.SInstanceHelpActionsProvider;
import org.opensingular.form.document.RefSDocumentFactory;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.wicket.IWicketBuildListener;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.WicketBuildListeners;
import org.opensingular.form.wicket.enums.AnnotationMode;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.model.ISInstanceAwareModel;
import org.opensingular.form.wicket.model.SInstanceRootModel;
import org.opensingular.form.wicket.util.WicketFormProcessing;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSGrid;
import org.opensingular.lib.wicket.util.bootstrap.layout.IBSComponentFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Painel que encapusla a lógica de criação de forms dinâmicos.
 * <p>Deve ser utilizado um dos métodos setInstanceXXXXXX() para configura o tipo ou formulário específico a ser
 * editado.</p>
 */
public class SingularFormPanel extends Panel {

    private           ISInstanceAwareModel<SInstance> instanceModel  = new SInstanceRootModel<>();
    private           boolean                         nested;
    // Container onde os componentes serão adicionados
    private           BSGrid                          container      = new BSGrid("generated");
    //Pode ser transient pois é usado apenas uma vez na inicialização do painel
    private transient Supplier<? extends SInstance>   instanceCreator;
    //Pode ser transient pois é usado apenas uma vez na inicialização do painel
    private transient Consumer<SInstance>             instanceInitializer;

    private ViewMode                            viewMode       = ViewMode.EDIT;

    private AnnotationMode                      annotationMode = AnnotationMode.NONE;

    private boolean                             firstRender    = true;

    private IBSComponentFactory<Component>      preFormPanelFactory;

    private RefSDocumentFactory                 documentFactoryRef;

    private List<IWicketBuildListener>          buildListeners;

    final BSContainer<?>                        modalItems     = new BSContainer<>("modalItems");

    //Set this if is this panel is nested inside a modal, to prevent z-index errors
    private BSContainer<?> modalContainer = null;
    private boolean firstFieldFocusEnabled = true;

    /**
     * Construtor do painel.
     *
     * @param id o markup id wicket
     */
    public SingularFormPanel(@Nonnull String id) {
        this(id, false);
    }

    /**
     * Construtor do painel. <p>Veja {@link #setInstanceFromType(Class)} )}.</p>
     *
     * @param id        o markup id wicket
     * @param typeClass Tipo a ser utilizado para montar o formulário.
     */
    public SingularFormPanel(@Nonnull String id, @Nonnull Class<? extends SType<?>> typeClass) {
        this(id, false);
        setInstanceFromType(typeClass);
    }

    /**
     * Construtor do painel. <p>Veja {@link #setInstanceFromType(RefType)}.</p>
     *
     * @param id      o markup id wicket
     * @param refType Tipo a ser utilizado para montar o formulário.
     */
    public SingularFormPanel(@Nonnull String id, @Nonnull RefType refType) {
        this(id, false);
        setInstanceFromType(refType);
    }

    /**
     * Construtor do painel. <p>Veja {@link #setInstanceFromType(ISupplier)}.</p>
     *
     * @param id        o markup id wicket
     * @param typeSupplier Supllier do tipo a ser utilizado para montar o formulário.
     */
    public SingularFormPanel(@Nonnull String id, @Nonnull ISupplier<SType<?>> typeSupplier) {
        this(id, false);
        setInstanceFromType(typeSupplier);
    }

    /**
     * Construtor do painel. <p>Veja {@link #setInstance(SInstance)}.</p>
     *
     * @param id       o markup id wicket
     * @param instance Conteúdo do painel.
     */
    public SingularFormPanel(@Nonnull String id, @Nonnull SInstance instance) {
        this(id, false);
        setInstance(instance);
    }

    /**
     * Construtor do painel. <p>Veja {@link #setInstance(SInstance)}.</p>
     *
     * @param id              o markup id wicket
     * @param instanceCreator Criado da instância a ser trabalhada no painel.
     */
    public SingularFormPanel(@Nonnull String id, @Nonnull Supplier<SInstance> instanceCreator) {
        this(id, false);
        setInstanceCreator(instanceCreator);
    }

    /**
     * Construtor do painel.
     *
     * @param id     o markup id wicket
     * @param nested indica se o painel está aninhado em uma modal.
     */
    public SingularFormPanel(@Nonnull String id, boolean nested) {
        super(id);
        this.nested = nested;
    }

    /**
     * Construtor do painel.
     *
     * @param id       o markup id wicket
     * @param instanceModel o model com o conteúdo do painel.
     */
    public SingularFormPanel(@Nonnull String id, @Nonnull ISInstanceAwareModel<SInstance> instanceModel) {
        this(id, false);
        this.instanceModel = instanceModel;
    }

    /**
     * Define a que a instância a ser editada ser uma nova instância do type da classe informada.
     * @return
     */
    public final SingularFormPanel setInstanceFromType(@Nonnull Class<? extends SType<?>> typeClass) {
        Objects.requireNonNull(typeClass);
        setInstanceFromType(RefType.of(typeClass));
        return this;
    }

    /**
     * Define a que a instância a ser editada ser uma nova instância a partir do criador de tipo informado.
     * @return
     */
    public final SingularFormPanel setInstanceFromType(@Nonnull ISupplier<SType<?>> typeSupplier) {
        Objects.requireNonNull(typeSupplier);
        setInstanceFromType(RefType.of(typeSupplier));
        return this;
    }

    /**
     * Define que a instância a ser editada será da referência ao tipo de formulário informado.
     * @return
     */
    public final SingularFormPanel setInstanceFromType(@Nonnull RefType refType) {
        Objects.requireNonNull(refType);
        SInstance instance;
        if (documentFactoryRef == null) {
            documentFactoryRef = SDocumentFactory.empty().getDocumentFactoryRef();
        }
        instance = documentFactoryRef.get().createInstance(refType);
        setInstance(instance);
        return this;
    }

    /** Define o criador da instância a ser o conteúdo do painel.
     * @return */
    public final SingularFormPanel setInstanceCreator(@Nonnull Supplier<? extends SInstance> instanceCreator) {
        Objects.requireNonNull(instanceCreator);
        this.instanceCreator = instanceCreator;
        return this;
    }

    /**
     * Método wicket, local onde os componentes são adicionados
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new WebMarkupContainer("modalContainer")
            .add(modalItems));

        if (instanceModel.getObject() == null) {
            if (instanceCreator != null) {
                SInstance instance = instanceCreator.get();
                if (instance == null) {
                    throw new SingularFormException("O instanceCreator retornou null");
                }
                instanceModel.setObject(instance);
            } else {
                throw new SingularFormException(
                    "A SInstance do painel está null. Chame um dos métodos setInstanceXXX() antes que o método " +
                        getClass().getSimpleName() +
                        ".onInitialize() seja invocado. Se a chamada deste método foi mediante super" +
                        ".onInitialize(), a instância pode ser configurada imediatamente antes dessa chamada.");
            }
        }
        if (instanceInitializer != null) {
            instanceInitializer.accept(instanceModel.getObject());
        }
        updateContainer();

        add(new ModalEventListenerBehavior(modalItems));
    }

    /**
     * Cria ou substitui o container
     */
    public final void updateContainer() {
        container = new BSGrid("generated");
        addOrReplace(container);

        // Constrói o body container
        BSContainer<?> bodyContainer = new BSContainer<>("body-container");
        bodyContainer.setOutputMarkupId(true);
        addOrReplace(bodyContainer);

        BSContainer<?> externalContainer;
        if(modalContainer != null){
            externalContainer = modalContainer;
        } else {
            externalContainer = bodyContainer;
        }
        // Chama o builder wicket para construção do formulário
        WicketBuildContext ctx = new WicketBuildContext(container.newColInRow(), externalContainer, getInstanceModel());
        ctx.setAnnotationMode(getAnnotationMode());
        ctx.setNested(nested);
        ctx.setPreFormPanelFactory(preFormPanelFactory);
        ctx.addListeners(getBuildListeners());
        addOrReplace(ctx.createFeedbackPanel("feedback", this).setShowBox(true));

        ctx.build(getViewMode());
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        WicketFormProcessing.onFormPrepare(this, getInstanceModel(), false);
        if (nested) {
            container.add(new AttributeAppender("style", "padding:0px;"));
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem
            .forReference(new JQueryPluginResourceReference(SingularFormPanel.class, "SingularFormPanel.js")));
        if (firstFieldFocusEnabled && firstRender && viewMode.isEdition()) {
            response.render(
                OnDomReadyHeaderItem.forScript("SingularFormPanel.initFocus('" + this.getMarkupId() + "');"));
            firstRender = false;
        }
    }

    /**
     * Indica qual o modo de uso de anotação, sendo que o default é desativado.
     */
    @Nonnull
    public final AnnotationMode getAnnotationMode() {
        return annotationMode;
    }

    /** Define como o formulário deve se comportar em relação as anotações.
     * @return */
    public SingularFormPanel setAnnotationMode(@Nonnull AnnotationMode annotationMode) {
        this.annotationMode = Objects.requireNonNull(annotationMode);
        return this;
    }

    /**
     * Retorna o model da instância sendo trabalhar pelo painel.
     */
    @Nonnull
    public final IModel<? extends SInstance> getInstanceModel() {
        return instanceModel;
    }

    /**
     * Retorna a instância atual do painel (ou dispara exception se ainda estiver nula).
     */
    @Nonnull
    public final SInstance getInstance() {
        SInstance instance = getInstanceModel().getObject();
        if (instance == null) {
            throw new SingularFormException("A instância ainda não foi inicializada ou atribuida ao painel");
        }
        return instance;
    }

    /**
     * Define a instância a ser editada no painel. A mesma deve estar corretamente configurada para ser serializável.
     *
     * @param instance Conteúdo do painel.
     */
    public final SingularFormPanel setInstance(@Nonnull SInstance instance) {
        Objects.requireNonNull(instance);
        if (instanceModel.getSInstance() != null) {
            throw new SingularFormException("A SInstance já está setada nesse painel");
        }
        instanceModel.setObject(instance);
        return this;
    }

    /**
     * Retorna a configuração para exibição do formulário da instância (edição, readonly). O default é ser edição.
     */
    @Nonnull
    public final ViewMode getViewMode() {
        return viewMode;
    }

    /** Define como o formulário deve ser tratado (edição, readonly, etc.) .
     * @return */
    public final SingularFormPanel setViewMode(@Nonnull ViewMode viewMode) {
        this.viewMode = Objects.requireNonNull(viewMode);
        return this;
    }

    public final String getRootTypeSubtitle() {
        return getInstance().asAtr().getSubtitle();
    }

    public final SingularFormPanel setPreFormPanelFactory(IBSComponentFactory<Component> preFormPanelFactory) {
        this.preFormPanelFactory = preFormPanelFactory;
        return this;
    }

    /**
     * Definice um código a ser chamado para inicialziar a instância durante a chamada do método {@link #onInitialize()}
     * do Wicket.
     * @return
     */
    public final SingularFormPanel setInstanceInitializer(Consumer<SInstance> instanceInitializer) {
        if (this.instanceInitializer != null) {
            throw new SingularFormException("O instanceInitializer ja está configurado");
        }
        this.instanceInitializer = instanceInitializer;
        return this;
    }

    /** Define a fábrica para criar instâncias a ser utilizada pelo painel.
     * @return */
    public SingularFormPanel setDocumentFactory(@Nonnull RefSDocumentFactory documentFactoryRef) {
        this.documentFactoryRef = Objects.requireNonNull(documentFactoryRef);
        return this;
    }

    /** Define a fábrica para criar instâncias a ser utilizada pelo painel.
     * @return */
    public SingularFormPanel setDocumentFactory(@Nonnull SDocumentFactory documentFactory) {
        this.documentFactoryRef = Objects.requireNonNull(documentFactory).getDocumentFactoryRef();
        return this;
    }

    /**
     * Retorna a fábrica utilizada para criar instâncias a partir de um Type. Pode ser null se a instância ainda não
     * tiver sido atribuida.
     */
    @Nonnull
    public final Optional<SDocumentFactory> getDocumentFactory() {
        return getDocumentFactoryRef().map(RefSDocumentFactory::get);
    }

    /**
     * Retorna a fábrica utilizada para criar instâncias a partir de um Type. Pode ser null se a instância ainda não
     * tiver sido atribuida.
     */
    @Nonnull
    public final Optional<RefSDocumentFactory> getDocumentFactoryRef() {
        if (documentFactoryRef == null) {
            return Optional.ofNullable(getInstanceModel().getObject()).map(SInstance::getDocument).map(
                SDocument::getDocumentFactoryRef);
        }
        return Optional.of(documentFactoryRef);
    }

    protected List<IWicketBuildListener> getDefaultBuildListeners() {
        return Arrays.asList(

            WicketBuildListeners.onBeforeBuildIfMapperIs(ISInstanceActionCapable.class,
                (ctx, mapper, iac) -> iac.addSInstanceActionsProvider(Integer.MAX_VALUE, new SInstanceHelpActionsProvider())),

            WicketBuildListeners.onBeforeBuildIfMapperIs(ISInstanceActionCapable.class,
                (ctx, mapper, iac) -> iac.addSInstanceActionsProvider(Integer.MAX_VALUE, new SInstanceAnnotationActionsProvider(
                    (instance) -> SingularFormPanel.this.getAnnotationMode().enabled(),
                    (instance) -> SingularFormPanel.this.getAnnotationMode().editable()) //
                )) //
        );
    }

    public SingularFormPanel addBuildListener(IWicketBuildListener listener) {
        getBuildListeners().add(listener);
        return this;
    }
    protected List<IWicketBuildListener> getBuildListeners() {
        if (buildListeners == null)
            buildListeners = new ArrayList<>(getDefaultBuildListeners());
        return buildListeners;
    }

    static void pushChildren(Deque<Component> stack, MarkupContainer container) {
        for (Component child : container)
            stack.push(child);
    }

    public void setNested(boolean nested) {
        this.nested = nested;
    }

    public void setFirstFieldFocusEnabled(boolean firstFieldFocusEnabled) {
        this.firstFieldFocusEnabled = firstFieldFocusEnabled;
    }

    /**
     * Set the container to be used as the modal container, usefful when this form is nested inside a component
     * with setted z-index
     * @param modalContainer the modal container
     */
    public void setModalContainer(BSContainer<?> modalContainer) {
        this.modalContainer = modalContainer;
    }
}
