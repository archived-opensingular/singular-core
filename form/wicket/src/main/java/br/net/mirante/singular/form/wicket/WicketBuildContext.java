package br.net.mirante.singular.form.wicket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.mform.basic.view.ViewResolver;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper.HintKey;
import br.net.mirante.singular.form.wicket.behavior.ConfigureByMInstanciaAttributesBehavior;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;
import br.net.mirante.singular.form.wicket.model.MInstanciaCampoModel;
import br.net.mirante.singular.form.wicket.util.WicketFormProcessing;
import br.net.mirante.singular.form.wicket.util.WicketFormUtils;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSCol;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.model.IReadOnlyModel;

@SuppressWarnings({ "serial", "rawtypes" })
public class WicketBuildContext implements Serializable {

    private final WicketBuildContext                parent;
    private final BSContainer<?>                    container;
    private final HashMap<HintKey<?>, Serializable> hints = new HashMap<>();
    private final boolean                           hintsInherited;
    private final BSContainer                       externalContainer;
    private final BSContainer                       rootContainer;

    private IModel<? extends MInstancia> model;
    private UIBuilderWicket              uiBuilderWicket;
    private ViewMode                     viewMode;
    private MView                        view;

    public WicketBuildContext(BSCol container, BSContainer bodyContainer, IModel<? extends MInstancia> model) {
        this(null, container, bodyContainer, false, model);
    }

    public WicketBuildContext(WicketBuildContext parent, BSContainer<?> container, BSContainer externalContainer,
        boolean hintsInherited, IModel<? extends MInstancia> model) {
        this.parent = parent;
        this.container = container;
        this.hintsInherited = hintsInherited;
        this.externalContainer = externalContainer;
        this.rootContainer = ObjectUtils.defaultIfNull((parent == null) ? null : parent.getRootContainer(), container);
        this.model = model;
        WicketFormUtils.markAsCellContainer(container);
        container.add(ConfigureByMInstanciaAttributesBehavior.getInstance());
    }

    public WicketBuildContext init(UIBuilderWicket uiBuilderWicket,
        ViewMode viewMode) {

        final MInstancia instance = getCurrenttInstance();

        this.view = ViewResolver.resolve(instance);
        this.uiBuilderWicket = uiBuilderWicket;
        this.viewMode = viewMode;

        if (isRootContext()) {
            getContainer().add(new InitRootContainerBehavior(getModel()));
        }

        if (getContainer().getDefaultModel() == null) {
            getContainer().setDefaultModel(getModel());
        }

        WicketFormUtils.setInstanceId(getContainer(), instance);
        WicketFormUtils.setRootContainer(getContainer(), getRootContainer());

        return this;
    }

    public <T, FC extends FormComponent<T>> FC configure(IWicketComponentMapper mapper, FC formComponent) {
        WicketFormUtils.setCellContainer(formComponent, getContainer());

        formComponent.add(ConfigureByMInstanciaAttributesBehavior.getInstance());

        if (formComponent.getLabel() == null) {
            // formComponent.setDescription(IReadOnlyModel.of(() -> resolveSimpleLabel(formComponent)));
            formComponent.setLabel(IReadOnlyModel.of(() -> resolveFullPathLabel(formComponent)));
        }

        IMInstanciaAwareModel<?> model = (IMInstanciaAwareModel<?>) formComponent.getDefaultModel();
        MTipo<?> tipo = model.getMInstancia().getMTipo();
        if (tipo.hasDependentTypes() || tipo.dependsOnAnyTypeInHierarchy()) {
            mapper.addAjaxUpdate(
                formComponent,
                IMInstanciaAwareModel.getInstanceModel(model),
                new OnFieldUpdatedListener());
        }

        return formComponent;
    }

    public WicketBuildContext createChild(BSContainer<?> childContainer, boolean hintsInherited, IModel<? extends MInstancia> model) {
        return new WicketBuildContext(this, childContainer, getExternalContainer(), hintsInherited, model);
    }

    protected static <T> String resolveSimpleLabel(FormComponent<?> formComponent) {
        IModel<?> model = formComponent.getModel();
        if (model instanceof IMInstanciaAwareModel<?>) {
            MInstancia instancia = ((IMInstanciaAwareModel<?>) model).getMInstancia();
            return instancia.as(MPacoteBasic.aspect()).getLabel();
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
            MInstancia instancia = ((IMInstanciaAwareModel<?>) model).getMInstancia();
            List<String> labels = new ArrayList<>();
            while (instancia != null) {
                labels.add(instancia.as(MPacoteBasic.aspect()).getLabel());
                instancia = instancia.getPai();
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
        IModel<? extends MInstancia> originalModel = getModel();
        for (String nomeTipo : nomesTipo) {
            MInstanciaCampoModel<MInstancia> subtree = new MInstanciaCampoModel<>(originalModel, nomeTipo);
            setModel(subtree);
            getUiBuilderWicket().build(this, viewMode);
        }

        setModel(originalModel);

    }

    private static final class InitRootContainerBehavior extends Behavior {
        private final IModel<? extends MInstancia> instanceModel;
        public InitRootContainerBehavior(IModel<? extends MInstancia> instanceModel) {
            this.instanceModel = instanceModel;
        }
        @Override
        public void onConfigure(Component component) {
            instanceModel.getObject().getDocument().updateAttributes(null);
        }
    }

    private static final class OnFieldUpdatedListener implements IAjaxUpdateListener {
        @Override
        public void onUpdate(Component s, AjaxRequestTarget t, IModel<? extends MInstancia> m) {
            WicketFormProcessing.onFieldUpdate((FormComponent<?>) s, Optional.of(t), m);
        }
        @Override
        public void onError(Component source, AjaxRequestTarget target, IModel<? extends MInstancia> instanceModel) {
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

    public IModel<? extends MInstancia> getModel() {
        return model;
    }

    public void setModel(IModel<? extends MInstancia> model) {
        this.model = model;
    }

    @SuppressWarnings("unchecked")
    public <T extends MInstancia> T getCurrenttInstance() {
        return (T) getModel().getObject();
    }
}
