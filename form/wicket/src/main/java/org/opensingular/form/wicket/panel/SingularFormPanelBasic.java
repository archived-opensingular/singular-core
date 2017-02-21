/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.resource.JQueryPluginResourceReference;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.document.RefType;
import org.opensingular.form.wicket.SingularFormConfigWicketImpl;
import org.opensingular.form.wicket.SingularFormContextWicket;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.enums.AnnotationMode;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.model.SInstanceRootModel;
import org.opensingular.form.wicket.util.WicketFormProcessing;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSGrid;
import org.opensingular.lib.wicket.util.bootstrap.layout.IBSComponentFactory;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Painel que encapusla a lógica de criação de forms dinâmicos.
 * <p>Deve ser utilizado um dos métodos setInstanceXXXXXX() para configura o tipo ou formulário específico a ser
 * editado.</p>
 */
public abstract class SingularFormPanelBasic extends Panel {

    // Container onde os componentes serão adicionados
    private BSGrid container = new BSGrid("generated");

    private final SInstanceRootModel<SInstance> rootInstance = new SInstanceRootModel<>();

    private ViewMode viewMode = ViewMode.EDIT;

    private AnnotationMode annotationMode = AnnotationMode.NONE;

    private final boolean nested;

    private boolean firstRender = true;

    private IBSComponentFactory<Component> preFormPanelFactory;

    private ISupplier<SingularFormContextWicket> formContextWicketSupplier;

    /**
     * Construtor do painel
     *
     * @param id o markup id wicket
     */
    public SingularFormPanelBasic(String id) {
        this(id, false);
    }

    /**
     * Construtor do painel.
     *
     * @param id                 o markup id wicket
     * @param singularFormConfig configuração para manipulação do documento a ser criado ou
     *                           recuperado.
     */
    public SingularFormPanelBasic(String id, boolean nested) {
        super(id);
        this.nested = nested;
    }

    /**
     * Define a que a instância a ser editada ser uma nova instância do type da classe informada.
     */
    public final void setInstanceFromType(@Nonnull Class<? extends SType> typeClass) {
        Objects.requireNonNull(typeClass);
        setInstanceFromType(RefType.of(typeClass));
    }

    /**
     * Define que a instância a ser editada será da referência ao tipo de formulário informado.
     */
    public final void setInstanceFromType(@Nonnull RefType refType) {
        Objects.requireNonNull(refType);
        setInstance(refType.get().newInstance());
    }

    /**
     * Define a instância a ser editada no painel. A mesma deve estar corretamente configurada para ser serializável.
     */
    public final void setInstance(@Nonnull SInstance instance) {
        Objects.requireNonNull(instance);
        if (rootInstance.getSInstance() != null) {
            throw new SingularFormException("A SInstance já está setada nesse painel");
        }
        rootInstance.setObject(instance);
    }

    /**
     * Método wicket, local onde os componentes são adicionados
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();
        if (rootInstance.getObject() == null) {
            throw new SingularFormException(
                    "A SInstance do painel está null. Chame um dos métodos setInstanceXXX() antes que o método " +
                            getClass().getSimpleName() +
                            ".onInitialize() seja invocado. Se a chamada deste método foi mediante super.onInitialize" +
                            "(), a instância pode ser configurada imediatamente antes dessa chamada.");
        }
        updateContainer();
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

        // Chama o builder wicket para construção do formulário
        WicketBuildContext ctx = new WicketBuildContext(container.newColInRow(), bodyContainer, getRootInstance());
        ctx.setAnnotationMode(getAnnotationMode());
        ctx.setNested(nested);
        ctx.setPreFormPanelFactory(preFormPanelFactory);
        add(ctx.createFeedbackPanel("feedback", this).setShowBox(true));

        SingularFormContextWicket formContext = resolveFormConfigWicket();
        formContext.getUIBuilder().build(ctx, getViewMode());
    }

    public void setFormContextWicketSupplier(ISupplier<SingularFormContextWicket> formContextWicketSupplier) {
        this.formContextWicketSupplier = formContextWicketSupplier;
    }

    private SingularFormContextWicket resolveFormConfigWicket() {
        if (formContextWicketSupplier == null) {
            return (new SingularFormConfigWicketImpl()).createContext();
        }
        SingularFormContextWicket formContextWicket = formContextWicketSupplier.get();
        if (formContextWicket == null) {
            throw new SingularFormException(
                    "O formContextWicketSupplier configurado retornou null (" + formContextWicket.getClass().getName() +
                            ")");
        }
        return formContextWicket;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        WicketFormProcessing.onFormPrepare(this, getRootInstance(), false);
        if (nested) {
            container.add(new AttributeAppender("style", "padding:0px;"));
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem
                .forReference(new JQueryPluginResourceReference(SingularFormPanelBasic.class, "SingularFormPanel.js")));
        if (firstRender && viewMode.isEdition()) {
            response.render(
                    OnDomReadyHeaderItem.forScript("SingularFormPanel.initFocus('" + this.getMarkupId() + "');"));
            firstRender = false;
        }
    }


    public final AnnotationMode getAnnotationMode() {
        return annotationMode;
    }

    public void setAnnotationMode(AnnotationMode annotationMode) {
        this.annotationMode = annotationMode;
    }

    public final IModel<? extends SInstance> getRootInstance() {
        return rootInstance;
    }

    public final ViewMode getViewMode() {
        return viewMode;
    }

    public final void setViewMode(ViewMode viewMode) {
        this.viewMode = viewMode;
    }

    public final String getRootTypeSubtitle() {
        return getRootInstance().getObject().asAtr().getSubtitle();
    }

    public final void setPreFormPanelFactory(IBSComponentFactory<Component> preFormPanelFactory) {
        this.preFormPanelFactory = preFormPanelFactory;
    }
}
