/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.util.wicket.bootstrap.layout;

import org.opensingular.singular.util.wicket.bootstrap.BootstrapSize;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import org.opensingular.singular.commons.lambda.IFunction;
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
        }
    }

    public String newChildId(){
        return items.newChildId();
    }
}
