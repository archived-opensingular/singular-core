package br.net.mirante.singular.form.wicket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.validation.MInstanciaValidator;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper.HintKey;
import br.net.mirante.singular.form.wicket.behavior.RequiredByMTipoObrigatorioBehavior;
import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSCol;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.model.IReadOnlyModel;

public class WicketBuildContext implements Serializable {

    private final WicketBuildContext                parent;
    private final BSContainer<?>                    container;
    private final HashMap<HintKey<?>, Serializable> hints = new HashMap<>();
    private final boolean                           hintsInherited;

    public WicketBuildContext(BSCol container) {
        this(null, container, false);
    }
    public WicketBuildContext(WicketBuildContext parent, BSContainer<?> container, boolean hintsInherited) {
        this.parent = parent;
        this.container = container;
        this.hintsInherited = hintsInherited;
    }
    public WicketBuildContext getParent() {
        return parent;
    }
    public BSContainer<?> getContainer() {
        return container;
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
        return new WicketBuildContext(this, childContainer, hintsInherited);
    }

    public <T, FC extends FormComponent<T>> FC configure(FC formComponent) {
        formComponent.add(RequiredByMTipoObrigatorioBehavior.getInstance());
        formComponent.add(new MInstanciaValidator<>());
        formComponent.setLabel((IReadOnlyModel<String>) () -> getLabel(formComponent));
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
}
