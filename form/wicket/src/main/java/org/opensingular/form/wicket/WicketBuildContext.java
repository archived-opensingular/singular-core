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

package org.opensingular.form.wicket;

import static com.google.common.collect.Lists.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import org.apache.wicket.Component;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;
import org.opensingular.form.wicket.mapper.TabMapper;
import org.opensingular.form.wicket.model.SInstanceFieldModel;
import org.slf4j.LoggerFactory;

import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.form.SInstance;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.view.SView;
import org.opensingular.form.view.ViewResolver;
import org.opensingular.form.wicket.IWicketComponentMapper.HintKey;
import org.opensingular.form.wicket.behavior.ConfigureByMInstanciaAttributesBehavior;
import org.opensingular.form.wicket.enums.AnnotationMode;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.feedback.SValidationFeedbackCompactPanel;
import org.opensingular.form.wicket.feedback.SValidationFeedbackPanel;
import org.opensingular.form.wicket.mapper.ListBreadcrumbMapper;
import org.opensingular.form.wicket.model.ISInstanceAwareModel;
import org.opensingular.form.wicket.model.SInstanceValueModel;
import org.opensingular.form.wicket.util.WicketFormProcessing;
import org.opensingular.form.wicket.util.WicketFormUtils;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSCol;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.bootstrap.layout.IBSComponentFactory;
import org.opensingular.lib.wicket.util.model.IReadOnlyModel;

@SuppressWarnings("serial")
public class WicketBuildContext implements Serializable {

    static final HintKey<HashMap<String, Integer>>                       COL_WIDTHS                                    = () -> new HashMap<>();

    public static final MetaDataKey<WicketBuildContext>                  METADATA_KEY                                  = new MetaDataKey<WicketBuildContext>() {};

    public static final HintKey<IModel<String>>                          TITLE_KEY                                     = () -> null;
    public static final HintKey<Boolean>                                 RECEIVES_INVISIBLE_INNER_COMPONENT_ERRORS_KEY = () -> null;

    private final List<WicketBuildContext>                               children                                      = newArrayList();
    private final HashMap<HintKey<?>, Serializable>                      hints                                         = new HashMap<>();

    private final WicketBuildContext                                     parent;
    private final BSContainer<?>                                         container;
    private final boolean                                                hintsInherited;
    private final BSContainer<?>                                         externalContainer;

    private IModel<? extends SInstance>                                  model;
    private UIBuilderWicket                                              uiBuilderWicket;
    private ViewMode                                                     viewMode;

    private AnnotationMode                                               annotation                                    = AnnotationMode.NONE;

    private boolean                                                      showBreadcrumb;
    private boolean                                                      nested                                        = false;
    private boolean                                                      titleInBlock                                  = false;
    private List<String>                                                 breadCrumbs                                   = newArrayList();
    private Deque<ListBreadcrumbMapper.BreadCrumbPanel.BreadCrumbStatus> breadCrumbStatus                              = newLinkedList();
    private ListBreadcrumbMapper.BreadCrumbPanel.BreadCrumbStatus        selectedBreadCrumbStatus;

    private IBSComponentFactory<Component>                               preFormPanelFactory;

    private SView                                                        view;

    public WicketBuildContext(BSCol container, BSContainer<?> externalContainer, IModel<? extends SInstance> model) {
        this(null, container, externalContainer, false, model);
    }

    protected WicketBuildContext(WicketBuildContext parent,
        BSContainer<?> container,
        BSContainer<?> externalContainer,
        boolean hintsInherited,
        IModel<? extends SInstance> model) {

        this.parent = parent;
        if (parent != null) {
            parent.children.add(this);
        }
        this.container = container;
        this.hintsInherited = hintsInherited;
        this.externalContainer = externalContainer;
        this.model = model;
        WicketFormUtils.markAsCellContainer(container);
        container.add(ConfigureByMInstanciaAttributesBehavior.getInstance());
        container.setMetaData(METADATA_KEY, this);
    }

    public WicketBuildContext createChild(BSContainer<?> childContainer, boolean hintsInherited, IModel<? extends SInstance> model) {
        return configureNestedContext(new WicketBuildContext(this, childContainer, getExternalContainer(), hintsInherited, model)
            .setAnnotationMode(getAnnotationMode()));
    }

    public WicketBuildContext createChild(BSContainer<?> childContainer, BSContainer<?> externalContainer, boolean hintsInherited, IModel<? extends SInstance> model) {
        return configureNestedContext(new WicketBuildContext(this, childContainer, externalContainer, hintsInherited, model)
            .setAnnotationMode(getAnnotationMode()));
    }

    private WicketBuildContext configureNestedContext(WicketBuildContext context) {
        context.setNested(nested);
        return context;
    }

    public WicketBuildContext init(UIBuilderWicket uiBuilderWicket, ViewMode viewMode) {

        final SInstance instance = getCurrentInstance();

        this.view = ViewResolver.resolve(instance);
        this.uiBuilderWicket = uiBuilderWicket;
        this.viewMode = viewMode;

        if (isRootContext()) {
            initContainerBehavior();
        }

        if (getContainer().getDefaultModel() == null) {
            getContainer().setDefaultModel(getModel());
        }

        WicketFormUtils.setInstanceId(getContainer(), instance);
        WicketFormUtils.setRootContainer(getContainer(), getRootContainer());

        return this;
    }

    public AnnotationMode getAnnotationMode() {
        return annotation;
    }

    public WicketBuildContext setAnnotationMode(AnnotationMode mode) {
        Objects.requireNonNull(mode);
        annotation = mode;
        return this;
    }

    /**
     * Adiciona um behavior que executa o update atributes do SDocument em toda requisição.
     * <p>
     * Normalmente este método não deve ser chamado externamente,
     * porem pode existir situações em que o container root não é atualizado
     * e novos componentes filhos são adicionados.
     *
     * @see SDocument
     * @see TabMapper
     */
    public void initContainerBehavior() {
        getContainer().add(new InitRootContainerBehavior(getModel()));
    }

    /**
     * Configura formComponentes, adicionando comportamentos de acordo com sua definição.
     *
     * @param mapper        o mapper
     * @param formComponent o componente que tem como model IMInstanciaAwareModel
     */
    public <C extends FormComponent<?>> C configure(IWicketComponentMapper mapper, C formComponent) {
        final IModel<?> defaultModel = formComponent.getDefaultModel();
        if (defaultModel != null && ISInstanceAwareModel.class.isAssignableFrom(defaultModel.getClass())) {
            WicketFormUtils.setCellContainer(formComponent, getContainer());
            formComponent.add(ConfigureByMInstanciaAttributesBehavior.getInstance());
            if (formComponent.getLabel() == null) {
                // formComponent.setDescription(IReadOnlyModel.of(() -> resolveSimpleLabel(formComponent)));
                formComponent.setLabel(IReadOnlyModel.of(() -> resolveFullPathLabel(formComponent)));
            }
            final ISInstanceAwareModel<?> model = (ISInstanceAwareModel<?>) defaultModel;
            // final SType<?> tipo = model.getMInstancia().getType();
            // if (tipo.hasDependentTypes() || tipo.dependsOnAnyTypeInHierarchy())
            mapper.addAjaxUpdate(
                formComponent,
                ISInstanceAwareModel.getInstanceModel(model),
                new OnFieldUpdatedListener());
        }
        return formComponent;
    }

    public void configureContainer(IModel<String> title) {
        setHint(TITLE_KEY, title);
    }

    public Optional<IModel<String>> resolveContainerTitle() {
        return Optional.ofNullable(getHint(TITLE_KEY));
    }

    //    public boolean resolveReceivesInvisibleInnerComponentErrors() {
    //        return Boolean.TRUE.equals(getHint(RECEIVES_INVISIBLE_INNER_COMPONENT_ERRORS_KEY));
    //    }

    public static Optional<WicketBuildContext> find(Component comp) {
        return Optional.ofNullable(comp.getMetaData(METADATA_KEY));
    }

    public static Optional<WicketBuildContext> findNearest(Component comp) {
        do {
            Optional<WicketBuildContext> ctx = find(comp);
            if (ctx.isPresent())
                return ctx;
            comp = comp.getParent();
        } while (comp != null);
        return Optional.empty();
    }

    public static Stream<WicketBuildContext> streamParentContexts(Component comp) {
        return findNearest(comp)
            .map(ctx -> ctx.streamParentContexts())
            .orElse(Stream.empty());
    }

    public Stream<WicketBuildContext> streamParentContexts() {
        final Builder<WicketBuildContext> sb = Stream.builder();
        for (WicketBuildContext ctx = this; ctx != null; ctx = ctx.getParent())
            sb.add(ctx);
        return sb.build();
    }

    public static Optional<WicketBuildContext> findTopLevel(Component comp) {
        return findNearest(comp).map(it -> it.getRootContext());
    }

    protected static <T> String resolveSimpleLabel(FormComponent<?> formComponent) {
        IModel<?> model = formComponent.getModel();
        if (model instanceof ISInstanceAwareModel<?>) {
            SInstance instancia = ((ISInstanceAwareModel<?>) model).getMInstancia();
            return instancia.asAtr().getLabel();
        }
        return "[" + formComponent.getId() + "]";
    }

    /**
     * Calcula o caminho completo de labels do campo, concatenando os nomes separados por ' > ',
     * para ser usado em mensagens de erro.
     * Exemplo: "O campo 'Contato > Endereços > Endereço > Logradouro' é obrigatório"
     */
    protected static <T> String resolveFullPathLabel(FormComponent<?> formComponent) {
        IModel<?> model = formComponent.getModel();
        if (model instanceof ISInstanceAwareModel<?>) {
            SInstance instancia = ((ISInstanceAwareModel<?>) model).getMInstancia();
            List<String> labels = new ArrayList<>();
            while (instancia != null) {
                labels.add(instancia.asAtr().getLabel());
                instancia = instancia.getParent();
            }
            labels.removeIf(it -> Strings.defaultIfEmpty(it, "").trim().isEmpty());
            Collections.reverse(labels);
            if (!labels.isEmpty())
                return Strings.join(" > ", labels);
        }
        return "[" + formComponent.getId() + "]";
    }

    public WicketBuildContext getRootContext() {
        WicketBuildContext ctx = this;
        while (!ctx.isRootContext())
            ctx = ctx.getParent();
        return ctx;
    }

    /**
     * @return true if this is the root of a Context tree.
     */
    public boolean isRootContext() {
        return (this.getParent() == null);
    }

    public BSContainer<?> getRootContainer() {
        return getRootContext().getContainer();
    }

    public WicketBuildContext getParent() {
        return parent;
    }

    public List<WicketBuildContext> getChildren() {
        return newArrayList(children);
    }

    public BSContainer<?> getContainer() {
        return container;
    }

    public SValidationFeedbackPanel createFeedbackPanel(String id) {
        return createFeedbackPanel(id, feedback -> ISValidationFeedbackHandlerListener.refresh(feedback));
    }

    public SValidationFeedbackPanel createFeedbackPanel(String id, Function<Component, ISValidationFeedbackHandlerListener> listenerFunc) {
        return createFeedbackPanel(() -> new SValidationFeedbackPanel(id, getContainer()), listenerFunc);
    }

    public SValidationFeedbackCompactPanel createFeedbackCompactPanel(String id) {
        return createFeedbackCompactPanel(id, ISValidationFeedbackHandlerListener::refresh);
    }

    public SValidationFeedbackCompactPanel createFeedbackCompactPanel(String id, Function<Component, ISValidationFeedbackHandlerListener> listenerFunc) {
        return createFeedbackPanel(() -> new SValidationFeedbackCompactPanel(id, getContainer()), listenerFunc);
    }

    private <C extends Component> C createFeedbackPanel(ISupplier<C> factory, Function<Component, ISValidationFeedbackHandlerListener> listenerFunc) {
        C feedback = factory.get();
        SValidationFeedbackHandler handler = SValidationFeedbackHandler.bindTo(getContainer()).addInstanceModel(getModel());
        if (listenerFunc != null) {
            ISValidationFeedbackHandlerListener listener = listenerFunc.apply(feedback);
            if (listener != null)
                handler.addListener(listener);
        }
        return feedback;
    }

    public BSContainer<?> getExternalContainer() {
        return externalContainer;
    }

    public <T extends Serializable> WicketBuildContext setHint(HintKey<T> key, T value) {
        hints.put(key, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T getHint(HintKey<T> key) {
        if (hints.containsKey(key)) {
            return (T) hints.get(key);
        } else if (hintsInherited && getParent() != null) {
            return getParent().getHint(key);
        } else {
            return key.getDefaultValue();
        }
    }

    public void rebuild(List<String> nomesTipo) {
        IModel<? extends SInstance> originalModel = getModel();
        for (String nomeTipo : nomesTipo) {
            SInstanceFieldModel<SInstance> subtree = new SInstanceFieldModel<>(originalModel, nomeTipo);
            setModel(subtree);
            getUiBuilderWicket().build(this, viewMode);
        }

        setModel(originalModel);

    }

    public void popBreadCrumb() {
        getBreadCrumbs().remove(getBreadCrumbs().size() - 1);
    }

    private static final class InitRootContainerBehavior extends Behavior {

        private final IModel<? extends SInstance> instanceModel;

        public InitRootContainerBehavior(IModel<? extends SInstance> instanceModel) {
            this.instanceModel = instanceModel;
        }

        @Override
        public void onConfigure(Component component) {
            if (instanceModel.getObject() != null) {
                instanceModel.getObject().getDocument().updateAttributes(null);
            }
        }
    }

    private static final class OnFieldUpdatedListener implements IAjaxUpdateListener {

        private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(WicketFormProcessing.class);

        @Override
        public void onValidate(Component s, AjaxRequestTarget t, IModel<? extends SInstance> m) {
            WicketFormProcessing.onFieldValidate((FormComponent<?>) s, Optional.of(t), m);
        }

        @Override
        public void onProcess(Component s, AjaxRequestTarget t, IModel<? extends SInstance> m) {
            long ms = Calendar.getInstance().getTimeInMillis();
            WicketFormProcessing.onFieldProcess((FormComponent<?>) s, Optional.of(t), m);
            LOGGER.info("[SINGULAR] Tempo processando (ms): " + (Calendar.getInstance().getTimeInMillis() - ms));
        }

        @Override
        public void onError(Component source, AjaxRequestTarget target, IModel<? extends SInstance> instanceModel) {
            WicketFormProcessing.onFormError((FormComponent<?>) source, Optional.of(target), instanceModel);
        }
    }

    public UIBuilderWicket getUiBuilderWicket() {
        return (uiBuilderWicket != null) ? uiBuilderWicket : getParent().getUiBuilderWicket();
    }

    public ViewMode getViewMode() {
        return (viewMode != null) ? viewMode : getParent().getViewMode();
    }

    public SView getView() {
        return view;
    }

    public IModel<? extends SInstance> getModel() {
        return model;
    }

    public IModel<?> getValueModel() {
        return new SInstanceValueModel<>(getModel());
    }

    public void setModel(IModel<? extends SInstance> model) {
        this.model = model;
    }

    public boolean isShowBreadcrumb() {
        return showBreadcrumb;
    }

    public void setShowBreadcrumb(boolean showBreadcrumb) {
        this.showBreadcrumb = showBreadcrumb;
    }

    public List<String> getBreadCrumbs() {
        if (isRootContext()) {
            return breadCrumbs;
        }
        return getRootContext().getBreadCrumbs();
    }

    public Deque<ListBreadcrumbMapper.BreadCrumbPanel.BreadCrumbStatus> getBreadCrumbStatus() {
        if (isRootContext()) {
            return breadCrumbStatus;
        }
        return getRootContext().getBreadCrumbStatus();
    }

    public ListBreadcrumbMapper.BreadCrumbPanel.BreadCrumbStatus getSelectedBreadCrumbStatus() {
        return selectedBreadCrumbStatus;
    }

    public void setSelectedBreadCrumbStatus(ListBreadcrumbMapper.BreadCrumbPanel.BreadCrumbStatus selectedBreadCrumbStatus) {
        this.selectedBreadCrumbStatus = selectedBreadCrumbStatus;
    }

    @SuppressWarnings("unchecked")
    public <T extends SInstance> T getCurrentInstance() {
        return (T) getModel().getObject();
    }

    public boolean isTitleInBlock() {
        return titleInBlock;
    }

    public void setTitleInBlock(boolean titleInBlock) {
        this.titleInBlock = titleInBlock;
    }

    public boolean isNested() {
        return nested;
    }

    public void setNested(boolean nested) {
        this.nested = nested;
    }

    public IBSComponentFactory<Component> getPreFormPanelFactory() {
        return preFormPanelFactory;
    }

    public void setPreFormPanelFactory(IBSComponentFactory<Component> preFormPanelFactory) {
        this.preFormPanelFactory = preFormPanelFactory;
    }

}