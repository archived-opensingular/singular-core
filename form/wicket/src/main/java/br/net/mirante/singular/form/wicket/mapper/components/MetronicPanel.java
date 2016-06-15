/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper.components;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.type.basic.SPackageBasic;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;

import br.net.mirante.singular.commons.lambda.IBiConsumer;
import br.net.mirante.singular.commons.lambda.IFunction;
import br.net.mirante.singular.form.wicket.component.SingularForm;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.TemplatePanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Set;

public abstract class MetronicPanel extends TemplatePanel {

    private SingularForm<?> form = null;
    private boolean withForm;

    public MetronicPanel(String id) {
        this(id, true);
    }

    public MetronicPanel(String id, boolean withForm) {
        super(id);
        this.withForm = withForm;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        setTemplateFunction(getTemplate(withForm));
        setRenderBodyOnly(true);
        setOutputMarkupId(false);
        setOutputMarkupPlaceholderTag(false);
        BSContainer<?>     heading   = new BSContainer<>("_hd");
        BSContainer<?>     footer    = new BSContainer<>("_ft");
        BSContainer<?>     content   = new BSContainer<>("_co");
        WebMarkupContainer container = this;
        if (withForm) {
            form = new SingularForm<>("_fo");
            add(form);
            container = form;
        }
        container.add(heading);
        container.add(footer);
        container.add(content);
        buildHeading(heading, form);
        buildContent(content, form);
        buildFooter(footer, form);
    }

    public Form<?> getForm() {
        return form;
    }

    protected abstract void buildHeading(BSContainer<?> heading, Form<?> form);

    protected abstract void buildFooter(BSContainer<?> footer, Form<?> form);

    protected abstract void buildContent(BSContainer<?> content, Form<?> form);

    public void replaceContent(IBiConsumer<BSContainer<?>, Form<?>> buildContent) {
        BSContainer<?> content = new BSContainer<>("_co");
        buildContent.accept(content, form);
        form.replace(content);
    }

    protected String getPanelWrapperClass() {
        return "panel panel-default";
    }

    protected String getPanelHeadingClass() {
        return "panel-heading";
    }

    protected String getPanelBodyClass() {
        return "panel-body";
    }

    protected String getPanelFooterClass() {
        return "panel-footer";
    }

    protected IFunction<TemplatePanel, String> getTemplate(boolean withForm) {
        String wrapper = withForm ? "<form wicket:id='_fo'>%s</form>" : "%s";
        return (tp) -> String.format(wrapper, ""
                + "  <div class='" + getPanelWrapperClass() + "'>"
                + "    <div wicket:id='_hd' class='" + getPanelHeadingClass() + "'></div>"
                + "    <div class='" + getPanelBodyClass() + "' wicket:id='_co' >"
                + "    </div>"
                + "    <div wicket:id='_ft' class='" + getPanelFooterClass() + " text-right'></div>"
                + "  </div>"
                + "");
    }

    public static ClassAttributeModifier dependsOnModifier(IModel<? extends SInstance> model) {
        return new ClassAttributeModifier() {
            @Override
            protected Set<String> update(Set<String> oldClasses) {
                if (model.getObject().getAttributeValue(SPackageBasic.ATR_DEPENDS_ON_FUNCTION) != null) {
                    oldClasses.add("dependant-input-group");
                }
                return oldClasses;
            }
        };
    }

    public static final class MetronicPanelBuilder {

        private MetronicPanelBuilder() {
        }

        public static MetronicPanel build(String id,
                                          IBiConsumer<BSContainer<?>, Form<?>> buildHeading,
                                          IBiConsumer<BSContainer<?>, Form<?>> buildContent,
                                          IBiConsumer<BSContainer<?>, Form<?>> buildFooter) {
            return build(id, true, buildHeading, buildContent, buildFooter);
        }

        public static MetronicPanel build(String id,
                                          boolean withForm,
                                          IBiConsumer<BSContainer<?>, Form<?>> buildHeading,
                                          IBiConsumer<BSContainer<?>, Form<?>> buildContent,
                                          IBiConsumer<BSContainer<?>, Form<?>> buildFooter) {

            return new MetronicPanel(id, withForm) {
                @Override
                protected void buildHeading(BSContainer<?> heading, Form<?> form) {
                    buildHeading.accept(heading, form);
                }

                @Override
                protected void buildFooter(BSContainer<?> footer, Form<?> form) {
                    buildFooter.accept(footer, form);
                }

                @Override
                protected void buildContent(BSContainer<?> content, Form<?> form) {
                    buildContent.accept(content, form);
                }
            };
        }

    }

}
