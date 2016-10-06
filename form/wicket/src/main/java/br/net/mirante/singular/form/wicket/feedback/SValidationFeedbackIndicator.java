/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.feedback;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.*;
import static java.util.stream.Collectors.*;

import org.apache.wicket.Component;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedback;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;

import org.opensingular.singular.form.validation.IValidationError;
import org.opensingular.singular.form.validation.ValidationErrorLevel;
import br.net.mirante.singular.form.wicket.SValidationFeedbackHandler;
import br.net.mirante.singular.util.wicket.jquery.JQuery;
import br.net.mirante.singular.util.wicket.util.JavaScriptUtils;

public class SValidationFeedbackIndicator extends WebMarkupContainer implements IFeedback {

    private final Component fence;

    public SValidationFeedbackIndicator(String id, Component fence) {
        super(id);
        this.fence = fence;
        add($b.classAppender("singular-feedback-indicator fa fa-exclamation-triangle text-danger"));
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisible(anyMessage());
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        if (this.anyMessage(ValidationErrorLevel.ERROR)) {
            response.render(OnDomReadyHeaderItem.forScript(
                JQuery.$(this) + ".closest('.can-have-error').addClass('has-error');"));
        } else if (this.anyMessage(ValidationErrorLevel.WARNING)) {
            response.render(OnDomReadyHeaderItem.forScript(
                JQuery.$(this) + ".closest('.can-have-error').addClass('has-warning');"));
        } else {
            response.render(OnDomReadyHeaderItem.forScript(
                JQuery.$(this) + ".closest('.can-have-error').removeClass('has-error').removeClass('has-warning');"));
        }

        String errors = getValidationFeedbackHandler().collectNestedErrors().stream()
            .map(it -> it.getMessage())
            .collect(joining("</li><li>", "<ul><li>", "</li></ul>"));
        response.render(OnDomReadyHeaderItem.forScript(""
            + JQuery.$(this)
            + "  .data('content', '" + JavaScriptUtils.javaScriptEscape(errors) + "')"
            + "  .popover({"
            + "    'html':true,"
            + "    'placement':'top',"
            + "    'trigger':'hover'"
            + "  });"));
    }

    public boolean anyMessage() {
        return getValidationFeedbackHandler().containsNestedErrors();
    }

    public boolean anyMessage(ValidationErrorLevel level) {
        return getValidationFeedbackHandler().containsNestedErrors(level);
    }

    protected SValidationFeedbackHandler getValidationFeedbackHandler() {
        return SValidationFeedbackHandler.get(getFence());
    }

    public Component getFence() {
        return fence;
    }

    /**
     * Gets the css class for the given message.
     * 
     * @param message
     *            the message
     * @return the css class; by default, this returns feedbackPanel + the message level, eg
     *         'feedbackPanelERROR', but you can override this method to provide your own
     */
    protected String getCSSClass(final IValidationError message) {
        String cssClass;
        switch (message.getErrorLevel()) {
            case WARNING:
                cssClass = getString(FeedbackMessage.WARNING_CSS_CLASS_KEY);
                break;
            case ERROR:
                cssClass = getString(FeedbackMessage.ERROR_CSS_CLASS_KEY);
                break;
            default:
                cssClass = "feedbackPanel" + message.getErrorLevel();
        }
        return cssClass;
    }
}
