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

package org.opensingular.form.wicket.feedback;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SFormUtil;
import org.opensingular.form.SInstance;
import org.opensingular.form.validation.IValidationError;
import org.opensingular.form.validation.ValidationErrorLevel;
import org.opensingular.form.wicket.SValidationFeedbackHandler;
import org.opensingular.form.wicket.util.WicketFormUtils;
import org.opensingular.lib.wicket.util.jquery.JQuery;
import org.opensingular.lib.wicket.util.model.IReadOnlyModel;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;
import static org.opensingular.lib.wicket.util.util.WicketUtils.$m;

public class SValidationFeedbackPanel extends AbstractSValidationFeedbackPanel {

    private boolean showBox = false;

    public SValidationFeedbackPanel(String id, Component fence) {
        super(id, fence);

        WebMarkupContainer feedbackul = new WebMarkupContainer("feedbackul") {
            protected void onConfigure() {
                super.onConfigure();
                boolean visible = anyMessage();
                setVisible(visible);
            }
        };
        add(feedbackul
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
                SValidationFeedbackPanel fp = (SValidationFeedbackPanel) component;
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
            }
        });
    }

    public boolean anyMessage() {
        return getValidationFeedbackHandler().containsNestedErrors();
    }

    public boolean anyMessage(ValidationErrorLevel level) {
        return getValidationFeedbackHandler().containsNestedErrors(level);
    }

    protected IModel<? extends List<IValidationError>> newValidationErrorsModel() {
        return (IReadOnlyModel<List<IValidationError>>) () -> getValidationFeedbackHandler().collectNestedErrors();
    }

    protected SValidationFeedbackHandler getValidationFeedbackHandler() {
        return SValidationFeedbackHandler.get(getFence());
    }

    protected Component newMessageDisplayComponent(String id, IModel<IValidationError> error) {
        final Component component = new Label(id, $m.map(error, IValidationError::getMessage));
        component.setEscapeModelStrings(SValidationFeedbackPanel.this.getEscapeModelStrings());
        component.add($b.classAppender($m.map(error, this::getCSSClass)));

        final Label label = (Label) component;

        if (error instanceof SFeedbackMessage) {
            final SFeedbackMessage bfm = (SFeedbackMessage) error;

            final SInstance           instance      = bfm.getInstanceModel().getObject();
            final SInstance           parentContext = WicketFormUtils.resolveInstance(getFence()).orElse(null);
            final Optional<Component> reporter      = WicketFormUtils.findChildByInstance(getFence(), instance);

            final String labelPath = StringUtils.defaultString(
                    reporter.map(it -> WicketFormUtils.generateTitlePath(getFence(), parentContext, it, instance)).orElse(null),
                    SFormUtil.generatePath(instance, it -> Objects.equals(it, parentContext)));

            label.setDefaultModelObject(labelPath + " : " + bfm.getMessage());
        }

        return component;
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        if (isShowBox()) {
            SValidationFeedbackHandler.get(getFence()).findNestedErrorsMaxLevel().ifPresent(level -> {
                new AttributeAppender("class", level.isWarning() ? "alert alert-warning" : "alert alert-danger").onComponentTag(this, tag);
            });
        }
    }

    public boolean isShowBox() {
        return showBox;
    }

    public SValidationFeedbackPanel setShowBox(boolean showBox) {
        this.showBox = showBox;
        return this;
    }

    /**
     * Gets the css class for the given message.
     *
     * @param message the message
     * @return the css class; by default, this returns feedbackPanel + the message level, eg
     * 'feedbackPanelERROR', but you can override this method to provide your own
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
