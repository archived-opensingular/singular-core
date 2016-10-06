/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.wicket.util.bootstrap.layout;

import java.nio.charset.Charset;
import java.util.Optional;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.Markup;
import org.apache.wicket.markup.MarkupParser;
import org.apache.wicket.markup.MarkupResourceStream;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.panel.IMarkupSourcingStrategy;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.panel.PanelMarkupSourcingStrategy;
import org.apache.wicket.util.resource.StringResourceStream;

import org.opensingular.lib.commons.base.SingularUtil;
import org.opensingular.lib.commons.lambda.IFunction;
import org.opensingular.lib.commons.lambda.ISupplier;

@SuppressWarnings("serial")
public class TemplatePanel extends Panel {

    private IFunction<TemplatePanel, String> templateFunction;

    public TemplatePanel(String id, ISupplier<String> templateSupplier) {
        this(id, p -> templateSupplier.get());
    }

    public TemplatePanel(String id, String template) {
        this(id, p -> template);
    }

    public TemplatePanel(String id, IFunction<TemplatePanel, String> templateFunction) {
        this(id);
        this.templateFunction = templateFunction;
    }
    public TemplatePanel(String id) {
        super(id);
    }

    protected void onBeforeComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
    }

    protected void onAfterComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
    }
    
    public IFunction<TemplatePanel, String> getTemplateFunction() {
        return templateFunction;
    }
    
    @Override
    protected IMarkupSourcingStrategy newMarkupSourcingStrategy() {
        return new PanelMarkupSourcingStrategy(false) {
            @Override
            public IMarkupFragment getMarkup(MarkupContainer parent, Component child) {
                // corrige o problema de encoding
                StringResourceStream stringResourceStream = new StringResourceStream("<wicket:panel>" + getTemplateFunction().apply(TemplatePanel.this) + "</wicket:panel>", "text/html");
                stringResourceStream.setCharset(Charset.forName(Optional.ofNullable(Application.get().getMarkupSettings().getDefaultMarkupEncoding()).orElse("UTF-8")));
                
                MarkupParser markupParser = new MarkupParser(new MarkupResourceStream(stringResourceStream));
                markupParser.setWicketNamespace(MarkupParser.WICKET);
                Markup markup;
                try {
                    markup = markupParser.parse();
                } catch (Exception e) {
                    throw SingularUtil.propagate(e);
                }
                
                // If child == null, than return the markup fragment starting
                // with <wicket:panel>
                if (child == null)
                {
                    return markup;
                }

                // Copiado da superclasse. buscando markup do child
                IMarkupFragment associatedMarkup = markup.find(child.getId());
                if (associatedMarkup != null) {
                    return associatedMarkup;
                }
                associatedMarkup = searchMarkupInTransparentResolvers(parent, parent.getMarkup(), child);
                if (associatedMarkup != null) {
                    return associatedMarkup;
                }
                return findMarkupInAssociatedFileHeader(parent, child);
            }

            @Override
            public void onComponentTagBody(Component component, MarkupStream markupStream, ComponentTag openTag) {
                TemplatePanel.this.onBeforeComponentTagBody(markupStream, openTag);
                super.onComponentTagBody(component, markupStream, openTag);
                TemplatePanel.this.onAfterComponentTagBody(markupStream, openTag);
            }
        };
    }
}
