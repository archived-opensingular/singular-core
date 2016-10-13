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

import org.opensingular.lib.wicket.util.bootstrap.BootstrapSize;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import org.opensingular.lib.commons.lambda.IFunction;
import org.opensingular.lib.wicket.util.scripts.Scripts;

import static org.apache.commons.lang3.StringUtils.defaultString;

@SuppressWarnings({ "unchecked", "serial" })
public class BSContainer<THIS extends BSContainer<THIS>> extends Panel {

    private String tagName;
    private String cssClass = null, innerStyle = null;
    protected final RepeatingView items = new RepeatingView("_");

    public BSContainer(String id) {
        super(id);
    }

    public BSContainer(String id, IModel<?> model) {
        super(id, model);
    }

    public BSContainer(String id, String tagName) {
        this(id);
        setTagName(tagName);
    }

    public BSContainer(String id, String tagName, IModel<?> model) {
        this(id, model);
        setTagName(tagName);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);
        add(items);
        add(new AttributeAppender("class", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return getCssClass();
            }
        }, " "));
        add(new AttributeAppender("style", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return getInnerStyle();
            }
        }, " "));
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);

        if (getTagName() != null) {
            tag.setName(getTagName());
        }
    }

    public THIS appendGrid(IBSComponentFactory<BSGrid> factory) {
        return appendComponent(factory);
    }

    public BSGrid newGrid() {
        return newComponent(BSGrid::new);
    }

    public BSControls newFormGroup() {
        return newFormGroup(BootstrapSize.SM);
    }

    public BSControls newFormGroup(BootstrapSize bsSize) {
        return newComponent(componentId -> {
            BSControls controls = new BSControls(componentId, false)
                    .setCssClass("form-group" + bsSize.apply("form-group"));
            controls.add(new AttributeAppender("class", "can-have-error", " "));
            return controls;
        });
    }

    public THIS appendTag(String tag, Component component) {
        return appendTag(tag, true, "", component);
    }

    public THIS appendTag(String tag, boolean closeTag, String attrs, Component component) {
        newTag(tag, closeTag, attrs, component);
        return (THIS) this;
    }

    public THIS appendTag(String tag, boolean closeTag, String attrs, IBSComponentFactory<Component> factory) {
        newTag(tag, closeTag, attrs, factory.newComponent(items.newChildId()));
        return (THIS) this;
    }

    public <C extends Component> C newTag(String tag, C component) {
        return newTag(tag, true, "", component);
    }

    public <C extends Component> C newTagWithFactory(String tag, boolean closeTag, String attrs, IBSComponentFactory<C> factory) {
        return newTag(tag, closeTag, attrs, factory.newComponent(items.newChildId()));
    }

    public <C extends Component> C newTag(String tag, boolean closeTag, String attrs, C component) {
        TemplatePanel container = newComponent(id -> new TemplatePanel(id, () ->
                "<" + tag + " wicket:id='" + component.getId() + "' " + defaultString(attrs) + ">"
                        + (closeTag ? "</" + tag + ">\n" : "\n")));
        container
                .add(component)
                .setRenderBodyOnly(true).setOutputMarkupId(false).setOutputMarkupPlaceholderTag(false);
        return component;
    }

    public TemplatePanel newTemplateTag(IFunction<TemplatePanel, String> markupFunc) {
        TemplatePanel container = newComponent(id -> new TemplatePanel(id, markupFunc));
        container.setRenderBodyOnly(true).setOutputMarkupId(false).setOutputMarkupPlaceholderTag(false);
        return container;
    }

    public <C extends Component> THIS appendComponent(IBSComponentFactory<C> factory) {
        newComponent(factory);
        return (THIS) this;
    }

    public <C extends Component> C newComponent(IBSComponentFactory<C> factory) {
        C comp = factory.newComponent(items.newChildId());
        items.add(comp);
        return comp;
    }

    public THIS setTagName(String tagName) {
        this.tagName = tagName;
        return (THIS) this;
    }

    public THIS setCssClass(String cssClass) {
        this.cssClass = cssClass;
        return (THIS) this;
    }
    public THIS setInnerStyle(String innerStyle) {
        this.innerStyle = innerStyle;
        return (THIS) this;
    }

    public String getTagName() {
        return tagName;
    }

    public String getCssClass() {
        return cssClass;
    }

    public String getInnerStyle() {return innerStyle; }

    public RepeatingView getItems() {return items;}

    public void addInfoMessage(String message) {
        final AjaxRequestTarget target = getRequestCycle().find(AjaxRequestTarget.class);
        if (target != null) {
            target.appendJavaScript(";bootbox.alert('" + message + "');");
            target.appendJavaScript(Scripts.multipleModalBackDrop());
        }
    }

    public String newChildId(){
        return items.newChildId();
    }
}
