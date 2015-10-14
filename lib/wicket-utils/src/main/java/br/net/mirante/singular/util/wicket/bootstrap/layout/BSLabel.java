package br.net.mirante.singular.util.wicket.bootstrap.layout;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.*;
import static java.util.stream.Collectors.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

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
        add($b.classAppender("required", $m.get(() -> firstRequiredInput().map(it -> it.isRequired()).orElse(false))));
        add($b.attr("for", firstRequiredInput().map(it -> it.getMarkupId()).orElse("")));

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

    @SuppressWarnings("rawtypes")
    protected Optional<FormComponent> firstRequiredInput() {
        return (container == null)
            ? Optional.empty()
            : findChildren(container, FormComponent.class)
                .filter(comp -> targetComponentIds.isEmpty() || targetComponentIds.contains(comp.getId()))
                .flatMap($L.instancesOf(FormComponent.class))
                .findFirst();
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
    public BSLabel setTargetComponents(Component... targets) {
        this.targetComponentIds = new HashSet<>(
            Stream.of(targets)
                .map(it -> it.getId()).collect(toList()));
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
