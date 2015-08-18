package br.net.mirante.singular.util.wicket.bootstrap.layout;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.Markup;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.panel.IMarkupSourcingStrategy;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.panel.PanelMarkupSourcingStrategy;

import br.net.mirante.singular.util.wicket.lambda.IFunction;
import br.net.mirante.singular.util.wicket.lambda.ISupplier;

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
        setTemplateFunction(templateFunction);
    }

    protected TemplatePanel(String id) {
        super(id);
//        setRenderBodyOnly(true);
    }

    protected final IFunction<TemplatePanel, String> getTemplateFunction() {
        return templateFunction;
    }

    protected final void setTemplateFunction(IFunction<TemplatePanel, String> templateFunction) {
        this.templateFunction = templateFunction;
    }

    protected void onBeforeComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
    }

    protected void onAfterComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
    }

    @Override
    protected IMarkupSourcingStrategy newMarkupSourcingStrategy() {
        return new PanelMarkupSourcingStrategy(false) {
            @Override
            public IMarkupFragment getMarkup(MarkupContainer parent, Component child) {
                Markup markup = Markup.of("<wicket:panel>" + getTemplateFunction().apply(TemplatePanel.this) + "</wicket:panel>");

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
