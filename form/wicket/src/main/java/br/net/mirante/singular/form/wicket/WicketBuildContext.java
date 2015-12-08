package br.net.mirante.singular.form.wicket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.AbstractChoice;
import org.apache.wicket.markup.html.form.AbstractTextComponent;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.collections.MultiMap;
import org.apache.wicket.util.string.Strings;

import br.net.mirante.singular.form.mform.MInstances;
import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper.HintKey;
import br.net.mirante.singular.form.wicket.behavior.ConfigureByMInstanciaAttributesBehavior;
import br.net.mirante.singular.form.wicket.behavior.IAjaxUpdateListener;
import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;
import br.net.mirante.singular.form.wicket.util.WicketFormProcessing;
import br.net.mirante.singular.form.wicket.util.WicketFormUtils;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSCol;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.model.IReadOnlyModel;
import org.apache.commons.lang3.ObjectUtils;
@SuppressWarnings({"serial","rawtypes"})
public class WicketBuildContext implements Serializable {

    private final WicketBuildContext                parent;
    private final BSContainer<?>                    container;
    private final HashMap<HintKey<?>, Serializable> hints = new HashMap<>();
    private final boolean                           hintsInherited;
    private final BSContainer                       externalContainer;
    private final BSContainer                       rootContainer;
    private MultiMap<MTipo<?>, MTipo<?>>            dependencyMap;

    public WicketBuildContext(BSCol container, BSContainer bodyContainer) {
        this(null, container, bodyContainer, false);
    }

    public WicketBuildContext(WicketBuildContext parent, BSContainer<?> container, BSContainer externalContainer, boolean hintsInherited) {
        this.parent = parent;
        this.container = container;
        this.hintsInherited = hintsInherited;
        this.externalContainer = externalContainer;
        this.rootContainer = ObjectUtils.defaultIfNull((parent == null) ? null : parent.getRootContainer(), container);
        WicketFormUtils.markAsCellContainer(container);
        container.add(ConfigureByMInstanciaAttributesBehavior.getInstance());
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

    public WicketBuildContext createChild(BSContainer<?> childContainer, boolean hintsInherited) {
        return new WicketBuildContext(this, childContainer, getExternalContainer(), hintsInherited);
    }

    public <T, FC extends FormComponent<T>> FC configure(FC formComponent) {
        WicketFormUtils.setCellContainer(formComponent, getContainer());

        formComponent.add(ConfigureByMInstanciaAttributesBehavior.getInstance());

        if (formComponent.getLabel() == null)
            formComponent.setLabel((IReadOnlyModel<String>) () -> getLabel(formComponent));

        IMInstanciaAwareModel<?> model = (IMInstanciaAwareModel<?>) formComponent.getDefaultModel();
//        MTipo<?> tipo = model.getMInstancia().getMTipo();
//        List<MTipo<?>> dependentes = getRootContext().getDependencyMap().get(tipo);
//        if (dependentes != null && !dependentes.isEmpty()) {
            addAjaxUpdateToComponent(
                formComponent,
                IMInstanciaAwareModel.getInstanceModel(model),
                (s, t, m) -> WicketFormProcessing.onFieldUpdated((FormComponent<?>) s, Optional.of(t), m.getObject()));
//        }

        return formComponent;
    }

    protected static <T> String getLabel(FormComponent<?> formComponent) {
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
    protected static <T> String getLabelFullPath(FormComponent<?> formComponent) {
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
    public MultiMap<MTipo<?>, MTipo<?>> getDependencyMap() {
        return (isRootContext()) ? dependencyMap : getRootContext().getDependencyMap();
    }

    public void init(MInstancia instance) {
        if (isRootContext()) {
            MultiMap<MTipo<?>, MTipo<?>> dependents = new MultiMap<>();
            MInstances.streamDescendants(instance.getDocument().getRoot(), true)
                .forEach(ins -> {
                    Supplier<Collection<MTipo<?>>> func = ins.getValorAtributo(MPacoteBasic.ATR_DEPENDS_FUNCTION);
                    if (func != null) {
                        for (MTipo<?> dependency : func.get()) {
                            dependents.addValue(dependency, ins.getMTipo());
                        }
                    }
                });
            this.dependencyMap = dependents;
        }
        WicketFormUtils.setInstanceId(getContainer(), instance);
        WicketFormUtils.setRootContainer(getContainer(), getRootContainer());
    }

    // TODO refatorar este método para ele ser estensível e configurável de forma global
    protected void addAjaxUpdateToComponent(Component component, IModel<MInstancia> model, IAjaxUpdateListener listener) {
        if ((component instanceof RadioChoice) ||
            (component instanceof CheckBoxMultipleChoice) ||
            (component instanceof RadioGroup) ||
            (component instanceof CheckGroup)) {
            component.add(new AjaxFormChoiceComponentUpdatingBehavior() {
                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    listener.onUpdate(this.getComponent(), target, model);
                }
            });

        } else if (component instanceof AbstractTextComponent<?>) {
            component.add(new AjaxFormComponentUpdatingBehavior("blur") {
                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    listener.onUpdate(this.getComponent(), target, model);
                }
            });
        }
    }

}
