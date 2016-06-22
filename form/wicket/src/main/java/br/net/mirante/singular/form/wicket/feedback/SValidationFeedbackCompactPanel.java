/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.feedback;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedback;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.SFormUtil;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.validation.IValidationError;
import br.net.mirante.singular.form.validation.ValidationError;
import br.net.mirante.singular.form.validation.ValidationErrorLevel;
import br.net.mirante.singular.form.wicket.SValidationFeedbackHandler;
import br.net.mirante.singular.form.wicket.util.WicketFormUtils;
import br.net.mirante.singular.util.wicket.jquery.JQuery;
import br.net.mirante.singular.util.wicket.model.IReadOnlyModel;

public class SValidationFeedbackCompactPanel extends Panel implements IFeedback {

    private final Component fence;
    private boolean         showBox = false;

    public SValidationFeedbackCompactPanel(String id, Component fence) {
        super(id);
        this.fence = fence;

        add($b.classAppender("singular-validation-feedback-compact-panel"));

        add(new Label("firstMessage", $m.get(this::firstMessageOrQuantity)));

        add(new WebMarkupContainer("feedbackul")
            .add(new ListView<IValidationError>("messages", newValidationErrorsModel()) {
                @Override
                protected void populateItem(ListItem<IValidationError> item) {
                    item.add(newMessageDisplayComponent("message", item.getModel()));
                }
            }));

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
            }
        });
    }

    private String firstMessageOrQuantity() {
        List<IValidationError> list = getMessages();
        if (list.isEmpty()) {
            return null;
        } else if (list.size() == 1) {
            return list.get(0).getMessage() + " xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
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
        //        return $m.get(() -> getValidationFeedbackHandler().collectNestedErrors());
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

    protected Component newMessageDisplayComponent(String id, IModel<IValidationError> error) {
        final Component component = new Label(id, $m.map(error, it -> it.getMessage()
            + " xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
            ));
        component.setEscapeModelStrings(SValidationFeedbackCompactPanel.this.getEscapeModelStrings());
        component.add($b.classAppender($m.map(error, it -> getCSSClass(it))));

        if (component instanceof Label) {
            final Label label = (Label) component;

            if (error instanceof SFeedbackMessage) {
                final SFeedbackMessage bfm = (SFeedbackMessage) error;

                final SInstance instance = bfm.getInstanceModel().getObject();
                final SInstance parentContext = WicketFormUtils.resolveInstance(getFence()).orElse(null);
                final Optional<Component> reporter = WicketFormUtils.findChildByInstance((MarkupContainer) getFence(), instance);

                final String labelPath = StringUtils.defaultString(
                    reporter.map(it -> WicketFormUtils.generateTitlePath(getFence(), parentContext, it, instance)).orElse(null),
                    SFormUtil.generatePath(instance, it -> Objects.equals(it, parentContext)));

                label.setDefaultModelObject(labelPath + " : " + bfm.getMessage());
            }
        }

        return component;
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        if (isShowBox()) {
            final Optional<ValidationErrorLevel> level = SValidationFeedbackHandler.get(getFence())
                .findNestedErrorsMaxLevel();
            if (level.isPresent()) {
                final String alertLevel = level.get().isWarning()
                    ? "alert alert-warning"
                    : "alert alert-danger";
                new AttributeAppender("class", alertLevel).onComponentTag(this, tag);
            }
        }
    }

    public Component getFence() {
        return fence;
    }

    public boolean isShowBox() {
        return showBox;
    }
    public SValidationFeedbackCompactPanel setShowBox(boolean showBox) {
        this.showBox = showBox;
        return this;
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
