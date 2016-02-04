package br.net.mirante.singular.form.wicket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.wicket.Component;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.mform.basic.view.ViewResolver;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper.HintKey;
import br.net.mirante.singular.form.wicket.behavior.ConfigureByMInstanciaAttributesBehavior;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;
import br.net.mirante.singular.form.wicket.model.SInstanceCampoModel;
import br.net.mirante.singular.form.wicket.resource.FormDefaultStyles;
import br.net.mirante.singular.form.wicket.util.WicketFormProcessing;
import br.net.mirante.singular.form.wicket.util.WicketFormUtils;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSCol;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.model.IReadOnlyModel;

@SuppressWarnings({"serial", "rawtypes"})
public class WicketBuildContext implements Serializable {

    public static final MetaDataKey<WicketBuildContext> METADATA_KEY = new MetaDataKey<WicketBuildContext>() {};

    public static final HintKey<IModel<String>> TITLE_KEY                                     = () -> null;
    public static final HintKey<Boolean>        RECEIVES_INVISIBLE_INNER_COMPONENT_ERRORS_KEY = () -> null;

    private final WicketBuildContext parent;
    private final BSContainer<?> container;
    private final HashMap<HintKey<?>, Serializable> hints = new HashMap<>();
    private final boolean hintsInherited;
    private final BSContainer externalContainer;
    private final BSContainer rootContainer;

    private IModel<? extends SInstance> model;
    private UIBuilderWicket uiBuilderWicket;
    private ViewMode viewMode;
    private boolean annotationEnabled = false;
    private MView view;

    public WicketBuildContext(BSCol container, BSContainer bodyContainer, IModel<? extends SInstance> model) {
        this(null, container, bodyContainer, false, model);
    }

    public WicketBuildContext(WicketBuildContext parent, BSContainer<?> container, BSContainer externalContainer,
                              boolean hintsInherited, IModel<? extends SInstance> model) {
        this.parent = parent;
        this.container = container;
        this.hintsInherited = hintsInherited;
        this.externalContainer = externalContainer;
        this.rootContainer = ObjectUtils.defaultIfNull((parent == null) ? null : parent.getRootContainer(), container);
        this.model = model;
        WicketFormUtils.markAsCellContainer(container);
        container.add(ConfigureByMInstanciaAttributesBehavior.getInstance());
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

        if (getContainer() != null) {
            getContainer().add(new Behavior() {
                @Override
                public void renderHead(Component component, IHeaderResponse response) {
                    super.renderHead(component, response);
                    response.render(CssReferenceHeaderItem.forReference(FormDefaultStyles.RESOURCE_REFERENCE));
                }
            });
        }

        return this;
    }

    /**
     * Adiciona um behavior que executa o update atributes do SDocument em toda requisição.
     *
     * Normalmente este método não deve ser chamado externamente,
     * porem pode existir situações em que o container root não é atualizado
     * e novos componentes filhos são adicionados.
     *
     * @see br.net.mirante.singular.form.mform.document.SDocument
     * @see br.net.mirante.singular.form.wicket.mapper.TabMapper
     */
    public void initContainerBehavior(){
        getContainer().add(new InitRootContainerBehavior(getModel()));
    }

    /**
     * Configura formComponentes, adicionando comportamentos de acordo com sua definição.
     *
     * @param mapper o mapper
     * @param formComponent o componente que tem como model IMInstanciaAwareModel
     *
     */
    public void configure(IWicketComponentMapper mapper, FormComponent<?> formComponent) {
        final IModel defaultModel = formComponent.getDefaultModel();
        if (defaultModel != null && IMInstanciaAwareModel.class.isAssignableFrom(defaultModel.getClass())) {
            WicketFormUtils.setCellContainer(formComponent, getContainer());
            formComponent.add(ConfigureByMInstanciaAttributesBehavior.getInstance());
            if (formComponent.getLabel() == null) {
                // formComponent.setDescription(IReadOnlyModel.of(() -> resolveSimpleLabel(formComponent)));
                formComponent.setLabel(IReadOnlyModel.of(() -> resolveFullPathLabel(formComponent)));
            }
            final IMInstanciaAwareModel<?> model = (IMInstanciaAwareModel<?>) defaultModel;
            final SType<?> tipo = model.getMInstancia().getMTipo();
            if (tipo.hasDependentTypes() || tipo.dependsOnAnyTypeInHierarchy()) {
                mapper.addAjaxUpdate(formComponent,
                        IMInstanciaAwareModel.getInstanceModel(model),
                        new OnFieldUpdatedListener());
            }
        }
    }

    public WicketBuildContext createChild(BSContainer<?> childContainer, boolean hintsInherited, IModel<? extends SInstance> model) {
        return new WicketBuildContext(this, childContainer, getExternalContainer(), hintsInherited, model);
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

    public static Optional<WicketBuildContext> get(Component comp) {
        return Optional.ofNullable(comp.getMetaData(METADATA_KEY));
    }

    protected static <T> String resolveSimpleLabel(FormComponent<?> formComponent) {
        IModel<?> model = formComponent.getModel();
        if (model instanceof IMInstanciaAwareModel<?>) {
            SInstance instancia = ((IMInstanciaAwareModel<?>) model).getMInstancia();
            return instancia.as(SPackageBasic.aspect()).getLabel();
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
        if (model instanceof IMInstanciaAwareModel<?>) {
            SInstance instancia = ((IMInstanciaAwareModel<?>) model).getMInstancia();
            List<String> labels = new ArrayList<>();
            while (instancia != null) {
                labels.add(instancia.as(SPackageBasic.aspect()).getLabel());
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

    public BSContainer getRootContainer() {
        return rootContainer;
    }

    public WicketBuildContext getParent() {
        return parent;
    }

    public BSContainer<?> getContainer() {
        return container;
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
            SInstanceCampoModel<SInstance> subtree = new SInstanceCampoModel<>(originalModel, nomeTipo);
            setModel(subtree);
            getUiBuilderWicket().build(this, viewMode);
        }

        setModel(originalModel);

    }

    private static final class InitRootContainerBehavior extends Behavior {
        private final IModel<? extends SInstance> instanceModel;

        public InitRootContainerBehavior(IModel<? extends SInstance> instanceModel) {
            this.instanceModel = instanceModel;
        }

        @Override
        public void onConfigure(Component component) {
            instanceModel.getObject().getDocument().updateAttributes(null);
        }
    }

    private static final class OnFieldUpdatedListener implements IAjaxUpdateListener {
        @Override
        public void onUpdate(Component s, AjaxRequestTarget t, IModel<? extends SInstance> m) {
            WicketFormProcessing.onFieldUpdate((FormComponent<?>) s, Optional.of(t), m);
        }

        @Override
        public void onError(Component source, AjaxRequestTarget target, IModel<? extends SInstance> instanceModel) {
            WicketFormProcessing.onFormError((FormComponent<?>) source, Optional.of(target), instanceModel);
        }
    }

    public UIBuilderWicket getUiBuilderWicket() {
        return uiBuilderWicket;
    }

    public ViewMode getViewMode() {
        return viewMode;
    }

    public MView getView() {
        return view;
    }

    public IModel<? extends SInstance> getModel() {
        return model;
    }

    public void setModel(IModel<? extends SInstance> model) {
        this.model = model;
    }

    @SuppressWarnings("unchecked")
    public <T extends SInstance> T getCurrentInstance() {
        return (T) getModel().getObject();
    }

    public boolean isAnnotationEnabled() {  return annotationEnabled;   }

    public void enableAnnotation() {   this.annotationEnabled = true;}
    public void disableAnnotation() {   this.annotationEnabled = false;}
}
