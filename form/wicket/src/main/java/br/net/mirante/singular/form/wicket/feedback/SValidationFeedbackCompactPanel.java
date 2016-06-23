/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.feedback;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.*;
import static java.util.stream.Collectors.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.StyleAttributeModifier;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.feedback.IFeedback;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.validation.IValidationError;
import br.net.mirante.singular.form.validation.ValidationErrorLevel;
import br.net.mirante.singular.form.wicket.SValidationFeedbackHandler;
import br.net.mirante.singular.util.wicket.jquery.JQuery;
import br.net.mirante.singular.util.wicket.model.IReadOnlyModel;
import br.net.mirante.singular.util.wicket.util.JavaScriptUtils;

public class SValidationFeedbackCompactPanel extends Panel implements IFeedback {

    private final Component fence;

    public SValidationFeedbackCompactPanel(String id, Component fence) {
        super(id);
        this.fence = fence;

        add($b.classAppender("singular-validation-feedback-compact-panel"));

        add(new Label("firstMessage", $m.get(this::firstMessageOrQuantity)));

        add(new StyleAttributeModifier() {
            @Override
            protected Map<String, String> update(Map<String, String> oldStyles) {
                final Map<String, String> newStyles = new HashMap<>(oldStyles);
                if (getMessages().isEmpty()) {
                    newStyles.put("display", "none");
                } else {
                    newStyles.put("display", "block");
                }
                return newStyles;
            }
        });

        add(new Behavior() {

            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                super.renderHead(component, response);
                SValidationFeedbackCompactPanel fp = (SValidationFeedbackCompactPanel) component;
                if (fp.anyMessage(ValidationErrorLevel.ERROR)) {
                    response.render(OnDomReadyHeaderItem.forScript(
                            JQuery.$(fp) + ".closest('.can-have-error').addClass('has-error');"));
                } else if (fp.anyMessage(ValidationErrorLevel.WARNING)) {
                    response.render(OnDomReadyHeaderItem.forScript(
                            JQuery.$(fp) + ".closest('.can-have-error').addClass('has-warning');"));
                } else {
                    response.render(OnDomReadyHeaderItem.forScript(
                            JQuery.$(fp) + ".closest('.can-have-error').removeClass('has-error').removeClass('has-warning');"));
                }
                response.render(OnDomReadyHeaderItem.forScript(""
                        + "var $this = " + JQuery.$(SValidationFeedbackCompactPanel.this) + ";"
                        + "var $fence = " + JQuery.$(fence) + ";"
                        + "$fence.find(':input')"
                        + "  .on('focus', function(){ $this.addClass('singular-active'); })"
                        + "  .on('blur',  function(){ $this.removeClass('singular-active'); });"
                        //                    + "$this"
                        //                    + "  .on('mouseover', function(){ $this.addClass('singular-active'); })"
                        //                    + "  .on('mouseout',  function(){ $this.removeClass('singular-active'); });"
                        + ""));

                List<IValidationError> messages = getMessages();
                if (!messages.isEmpty()) {
                    String errors = messages.stream()
                            .map(IValidationError::getMessage)
                            .collect(joining("</li><li>", "<ul class='list-unstyled'><li>", "</li></ul>"));
                    if (messages.size() > 1) {
                        response.render(OnDomReadyHeaderItem.forScript(""
                                + "var $feedback = " + JQuery.$(component) + ";"
                                + "var $formGroup = $feedback.parent();"
                                + "$formGroup"
                                + "  .data('content', '" + JavaScriptUtils.javaScriptEscape(errors) + "')"
                                + "  .popover({"
                                + "    'html':true,"
                                + "    'placement':'bottom',"
                                + "    'trigger':'hover'"
                                + "  });"
                                + ""));
                    }
                }
            }
        });
    }

    private String firstMessageOrQuantity() {
        List<IValidationError> list = getMessages();
        if (list.isEmpty()) {
            return null;
        } else if (list.size() == 1) {
            return list.get(0).getMessage();
        } else {
            return list.size() + " erros encontrados";
        }
    }

    public boolean anyMessage() {
        return getValidationFeedbackHandler().containsNestedErrors();
    }

    public List<IValidationError> getMessages() {
        List<IValidationError> list = getValidationFeedbackHandler().collectNestedErrors();
//        if (!list.isEmpty()) {
//            IValidationError first = list.get(0);
//            list.add(new ValidationError(first.getInstanceId(), first.getErrorLevel(), "Erro 2"));
//            list.add(new ValidationError(first.getInstanceId(), first.getErrorLevel(), "Erro 3"));
//            list.add(new ValidationError(first.getInstanceId(), first.getErrorLevel(), "Erro 4"));
//        }
        return list;
    }

    public boolean anyMessage(ValidationErrorLevel level) {
        return getValidationFeedbackHandler().containsNestedErrors(level);
    }

    protected IModel<? extends List<IValidationError>> newValidationErrorsModel() {
        return new IReadOnlyModel<List<IValidationError>>() {
            @Override
            public List<IValidationError> getObject() {
                return getMessages();
            }
        };
    }

    protected SValidationFeedbackHandler getValidationFeedbackHandler() {
        return SValidationFeedbackHandler.get(getFence());
    }

    public Component getFence() {
        return fence;
    }
}
