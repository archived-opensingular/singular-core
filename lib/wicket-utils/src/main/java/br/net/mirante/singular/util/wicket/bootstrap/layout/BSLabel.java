package br.net.mirante.singular.util.wicket.bootstrap.layout;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$L;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.findChildren;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;

public class BSLabel extends Label implements IBSGridCol<BSLabel> {

    private Set<String>     targetComponentIds = new HashSet<>();
    private MarkupContainer container;
    private String          tagName;

    public BSLabel(String id, IModel<?> model) {
        super(id, model);
        setTagName("label");
    }

    public BSLabel(String id, Serializable label) {
        super(id, label);
        setTagName("label");
    }

    public BSLabel(String id, String label) {
        super(id, label);
        setTagName("label");
    }

    public BSLabel(String id) {
        super(id);
        setTagName("label");
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onInitialize() {
        super.onInitialize();
        add($b.classAppender("control-label"));

        add(newBSGridColBehavior());

        // adiciona classe 'required' se algum FormComponent do
        // container é required
        add($b.classAppender("required", $m.get(() ->
            (container == null) ? false :
                findChildren(container, FormComponent.class)
                    .filter(comp -> targetComponentIds.isEmpty() || targetComponentIds.contains(comp.getId()))
                    .flatMap($L.instancesOf(FormComponent.class))
                    .anyMatch(it -> it.isRequired())
            )));

        // altera propriedade label dos componentes se com o valor desta
        // label, se a primeira for null
        IModel<?> labelModel = getDefaultModel();
        add($b.onConfigure(c -> {
            if (container != null)
                findChildren(container, Component.class)
                    .filter(comp -> targetComponentIds.isEmpty() || targetComponentIds.contains(comp.getId()))
                    .flatMap($L.instancesOf(FormComponent.class))
                    .filter(it -> it.getLabel() == null)
                    .forEach(fc -> fc.setLabel(labelModel));
        }));

    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);

        if (getTagName() != null) {
            tag.setName(getTagName());
        }
    }

    public BSLabel setContainer(MarkupContainer container) {
        this.container = container;
        return this;
    }

    public BSLabel setTargetComponentIds(String targetIds) {
        this.targetComponentIds = new HashSet<>(Arrays.asList(
            (targetIds == null) ? new String[0] : targetIds.split("[, ]+")));
        return this;
    }

    public BSLabel setTagName(String tagName) {
        this.tagName = tagName;
        return this;
    }

    public String getTagName() {
        return tagName;
    }
}
