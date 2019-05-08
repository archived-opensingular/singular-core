package org.opensingular.form.wicket.mapper.chosen;

import org.apache.wicket.Component;
import org.apache.wicket.StyleAttributeModifier;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceReferenceRequestHandler;
import org.apache.wicket.request.resource.PackageResourceReference;

import java.util.Map;

public abstract class ChosenMultiSelectField<T> extends GenericPanel<T> {

    private static final JavaScriptReferenceHeaderItem CHOSEN_JS = JavaScriptReferenceHeaderItem
            .forReference(new PackageResourceReference(ChosenMultiSelectField.class, "chosen.jquery.min.js"));

    private static final CssReferenceHeaderItem CHOSEN_CSS = CssReferenceHeaderItem
            .forReference(new PackageResourceReference(ChosenMultiSelectField.class, "chosen.min.css"));

    /**
     *
     */
    private ListMultipleChoice<?> choiceField;
    private Component loadingComponent;

    private String emptyLabel = "Todos(as)";

    public ChosenMultiSelectField(String id) {
        super(id);
    }

    public ChosenMultiSelectField(String id, IModel<T> model) {
        super(id, model);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CHOSEN_JS);
        response.render(CHOSEN_CSS);
        response.render(OnDomReadyHeaderItem.forScript(hideLoadingScript()));
        response.render(OnDomReadyHeaderItem.forScript(initChoosenScript()));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        loadingComponent = createLoadingComponent("spinner");
        choiceField = createChoiceField("choice");
        add(loadingComponent);
        add(choiceField);
        choiceField.add(new StyleAttributeModifier() {
            @Override
            protected Map<String, String> update(Map<String, String> styles) {
                styles.put("display", "none");
                return styles;
            }
        });
    }

    protected abstract ListMultipleChoice<?> createChoiceField(final String markupId);

    protected Component createLoadingComponent(final String markupId) {
        final IRequestHandler handler = new ResourceReferenceRequestHandler(AbstractDefaultAjaxBehavior.INDICATOR);
        return new Label(markupId, "<span class='form-control'><img alt='Loading...' src='" + RequestCycle.get().urlFor(handler) + "'/></span>")
                .setEscapeModelStrings(false);
    }

    protected String hideLoadingScript() {
        return "Wicket.Event.add('" + choiceField.getMarkupId() + "', " + "'chosen:ready', function(){$('#" + loadingComponent.getMarkupId() + "').hide()})";
    }

    protected String initChoosenScript() {
        final JSONObject config = new JSONObject();
        config.put("no_results_text", "Nenhum resultado encontrado!");
        config.put("placeholder_text_multiple", emptyLabel);
        config.put("placeholder_text_single", emptyLabel);
        config.put("hide_results_on_select", false);
        config.put("width", "100%");
        return String.format("$('#%s').chosen(%s);", choiceField.getMarkupId(), config.toString());
    }

    public ChosenMultiSelectField<T> setEmptyLabel(String emptyLabel) {
        this.emptyLabel = emptyLabel;
        return this;
    }
}