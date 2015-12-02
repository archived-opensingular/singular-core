package br.net.mirante.singular.form.wicket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.AbstractChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.SDocument;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.function.IBehavior;
import br.net.mirante.singular.form.mform.function.IBehaviorContext;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper.HintKey;
import br.net.mirante.singular.form.wicket.behavior.ConfigureByMInstanciaAttributesBehavior;
import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;
import br.net.mirante.singular.form.wicket.util.WicketFormUtils;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSCol;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.model.IReadOnlyModel;

@SuppressWarnings({"serial","rawtypes"})
public class WicketBuildContext implements Serializable {

    private static final MetaDataKey<Integer>  KEY_INSTANCE_ID        = new MetaDataKey<Integer>() {};
    public static final MetaDataKey<Component> KEY_INSTANCE_CONTAINER = new MetaDataKey<Component>() {};

    private final WicketBuildContext                parent;
    private final BSContainer<?>                    container;
    private final HashMap<HintKey<?>, Serializable> hints = new HashMap<>();
    private final boolean                           hintsInherited;
    private final  BSContainer                      externalContainer;

    public WicketBuildContext(BSCol container, BSContainer bodyContainer) {
        this(null, container, bodyContainer, false);
    }

    public WicketBuildContext(WicketBuildContext parent, BSContainer<?> container,  BSContainer externalContainer, boolean hintsInherited) {
        this.parent = parent;
        this.container = container;
        this.hintsInherited = hintsInherited;
        this.externalContainer = externalContainer;
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
        formComponent.add(ConfigureByMInstanciaAttributesBehavior.getInstance());
//        formComponent.add(new MInstanciaValueValidator<>());
        formComponent.setLabel((IReadOnlyModel<String>) () -> getLabel(formComponent));
        formComponent.setMetaData(KEY_INSTANCE_CONTAINER, getContainer());

        IMInstanciaAwareModel<?> model = (IMInstanciaAwareModel<?>) formComponent.getDefaultModel();
        MTipo<?> tipo = model.getMInstancia().getMTipo();

        if (tipo.as(MPacoteBasic.aspect()).hasOnChange()) {
            if (formComponent instanceof TextField<?> ||
                formComponent instanceof AbstractChoice<?, ?>) {
                formComponent.add(new AjaxFormComponentUpdatingBehavior("change") {
                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        IMInstanciaAwareModel<?> model = (IMInstanciaAwareModel<?>) this.getComponent().getDefaultModel();
                        MInstancia instance = model.getMInstancia();
                        IBehavior<MInstancia> onChange = instance.as(MPacoteBasic.aspect()).getOnChange();
                        if (onChange != null) {
                            onChange.on(new IBehaviorContext() {
                                @Override
                                public IBehaviorContext update(MTipo<?>... fields) {
                                    for (MTipo<?> field : fields)
                                        target.add(instance.findNearest(field)
                                            .flatMap(target -> WicketFormUtils.findChildByInstance(formComponent.getPage(), target))
                                            .map(target -> Optional.ofNullable(target.getMetaData(KEY_INSTANCE_CONTAINER)).orElse(target))
                                            .get());
                                    return this;
                                }
                            }, model.getMInstancia());
                        }
                    }
                });
            }
        }
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
    public void setContainerInstance(MInstancia instancia) {
        getContainer().setMetaData(KEY_INSTANCE_ID, instancia.getId());
    }
    public boolean isContainerForInstance(MInstancia instance) {
        return Objects.equals(instance.getId(), getContainer().getMetaData(KEY_INSTANCE_ID));
    }
    public Optional<MInstancia> findContainerInstance(SDocument document) {
        return ((MIComposto) document.getRoot()).streamDescendants(true)
            .filter(it -> Objects.equals(it.getId(), getContainer().getMetaData(KEY_INSTANCE_ID)))
            .findFirst();
    }
    public Optional<MarkupContainer> findContainerForInstance(Component start, MInstancia instance) {
        return WicketFormUtils.streamAscendants(start)
            .filter(it -> Objects.equals(instance.getId(), it.getMetaData(KEY_INSTANCE_ID)))
            .findFirst();
    }
}
