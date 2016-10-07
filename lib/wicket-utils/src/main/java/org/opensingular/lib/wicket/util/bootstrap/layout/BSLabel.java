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

package org.opensingular.lib.wicket.util.bootstrap.layout;

import java.io.Serializable;
import java.util.Optional;

import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;

import static org.opensingular.lib.wicket.util.util.WicketUtils.*;

public class BSLabel extends Label implements IBSGridCol<BSLabel> {

    private Component targetComponent;
    private String    tagName;

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
    protected void onInitialize() {
        super.onInitialize();
        add($b.classAppender("control-label"));

        add(newBSGridColBehavior());

        add($b.attr("for", $m.get(() -> findTargetFormComponent().map(it -> it.getMarkupId()).orElse(""))));

        // altera propriedade label dos componentes se com o valor desta
        // label, se a primeira for null
        IModel<String> labelModel = $m.get(() -> getDefaultModelObjectAsString());
        add($b.onConfigure(c ->
            findTargetFormComponent()
                .filter(it -> it.getLabel() == null)
                .ifPresent(it -> it.setLabel(labelModel))
            ));
    }

    protected Optional<? extends Component> findTargetComponent() {
        return Optional.ofNullable(targetComponent);
    }
    protected Optional<? extends FormComponent<?>> findTargetFormComponent() {
        return findTargetComponent().flatMap($L.instanceOf(FormComponent.class));
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);

        if (getTagName() != null) {
            tag.setName(getTagName());
        }
    }

    public BSLabel setTargetComponent(Component target) {
        this.targetComponent = target;
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
