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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.lib.commons.ui.Alignment;
import org.opensingular.lib.wicket.util.behavior.BSSelectInitBehaviour;
import org.opensingular.lib.wicket.util.behavior.DatePickerInitBehaviour;
import org.opensingular.lib.wicket.util.behavior.DatePickerSettings;
import org.opensingular.lib.wicket.util.behavior.PicklistInitBehaviour;
import org.opensingular.lib.wicket.util.feedback.BSFeedbackPanel;
import org.opensingular.lib.wicket.util.jquery.JQuery;

import static org.apache.commons.lang3.StringUtils.defaultString;

public class BSControls extends BSContainer<BSControls> implements IBSGridCol<BSControls> {


    public enum DatePickerViewMode {
        DAYS, MONTH, YEAR, DECADE, CENTURY, MILLENIUM;

        public String toString() {
            return name().toLowerCase();
        }
    }


    public static final String                       DATEPICKER_DEFAULT_DATE_FORMAT = "dd/MM/yyyy";
    public static final String                       DATEPICKER_DEFAULT_START_DATE  = "01/01/1900";
    public static final String                       DATEPICKER_DEFAULT_END_DATE    = "31/12/2999";
    public static final MetaDataKey<MarkupContainer> DATEPICKER_KEY_CONTAINER       = new MetaDataKey<MarkupContainer>() {};
    public static final MetaDataKey<BSContainer<?>>  CHECKBOX_DIV                   = new MetaDataKey<BSContainer<?>>() {};
    public static final MetaDataKey<BSContainer<?>>  CHECKBOX_LABEL                 = new MetaDataKey<BSContainer<?>>() {};

    private IFeedbackPanelFactory feedbackPanelFactory;

    public BSControls(String id) {
        this(id, true);
    }

    public BSControls(String id, boolean addGridBehavior) {
        super(id);
        if (addGridBehavior) {
            add(newBSGridColBehavior());
        }
    }

    public BSControls appendCheckbox(Component checkbox) {
        return this.appendCheckbox(checkbox, Model.of(""));
    }

    public BSControls appendCheckbox(Component checkbox, IModel<?> labelModel) {
        return this.appendCheckbox(checkbox, new Label("_", labelModel), null);
    }

    public BSControls appendCheckbox(Component checkbox, Component label, Alignment alignment) {
        final BSContainer<?> checkboxDiv = new BSContainer<>("_" + checkbox.getId());
        final BSContainer<?> checkboxLabel = new BSContainer<>("_");

        checkbox.setMetaData(CHECKBOX_DIV, checkboxDiv);
        checkbox.setMetaData(CHECKBOX_LABEL, checkboxLabel);

        checkboxLabel.appendTag("input", false, "type='checkbox'", checkbox);
        if (label != null) {
            checkboxLabel.appendTag("span", label);
        }
        String style = "";
        if (alignment != null) {
            style = "style= 'text-align:" + alignment.name().toLowerCase() + "'";
        }

        return this
                .appendTag("div", true, "class='checkbox'" + style, checkboxDiv
                        .appendTag("label", checkboxLabel));
    }

    public BSControls appendCheckboxWithoutLabel(Component checkbox, Alignment alignment) {
        return appendCheckbox(checkbox, null, alignment);
    }


    public BSControls appendCheckboxChoice(Component checkbox, boolean inline) {
        return super.appendTag("div", true, inline ? "class='checkbox-inline'" : "class='checkbox-list'", checkbox);
    }

    public BSControls appendCheckboxChoice(Component checkbox) {
        return this.appendCheckboxChoice(checkbox, false);
    }

    public BSControls appendLabel(Component label) {
        return this.appendTag("label", label);
    }

    public BSControls appendInputEmail(Component input) {
        return super.appendTag("input", false, "type='email' class='form-control'", input);
    }

    public BSControls appendInputPassword(Component input) {
        return super.appendTag("input", false, "type='password' class='form-control'", input);
    }

    public BSControls appendInputText(Component input) {
        return super.appendTag("input", false, "type='text' class='form-control'", input);
    }

    public BSControls appendInputHidden(Component input) {
        return super.appendTag("input", false, "type='hidden' class='form-control'", input);
    }

    public BSControls appendRadioChoice(Component input) {
        return super.appendTag("div", true, "class='radio-list'", input);
    }

    public BSControls appendDatepicker(Component datepicker, Map<String, ? extends Serializable> options) {
        return this.appendDatepicker(datepicker, options, null);
    }

    public BSControls appendDatepicker(Component datepicker, Map<String, ? extends Serializable> extraAttributes, DatePickerSettings datePickerSettings) {
        Map<String, Serializable> attrs = new HashMap<>();
        attrs.put("data-date-format", "dd/mm/yyyy");
        attrs.put("data-date-start-date", DATEPICKER_DEFAULT_START_DATE);
        attrs.put("data-date-end-date", DATEPICKER_DEFAULT_END_DATE);
        attrs.put("data-date-start-view", "days");
        attrs.put("data-date-min-view-mode", DatePickerViewMode.DAYS);
        attrs.put("data-date-max-view-mode", DatePickerViewMode.MILLENIUM);
        if (extraAttributes != null)
            attrs.putAll(extraAttributes);

        BSInputGroup inputGroup = newInputGroup();
        inputGroup
                .appendExtraClasses(" date ")
                .appendExtraAttributes(attrs)
                .appendInputText(datepicker.setMetaData(DATEPICKER_KEY_CONTAINER, inputGroup))
                .add(new DatePickerInitBehaviour(datePickerSettings));

        this.appendInputGroup(componentId -> inputGroup);
        return inputGroup;
    }

    public BSControls appendSelect(Component select) {
        return appendSelect(select, false, true);
    }

    public BSControls appendSelect(Component select, boolean multiple) {
        return appendSelect(select, multiple, true);
    }

    public BSControls appendSelect(Component select, boolean multiple, boolean bootstrap) {
        if (multiple) {
            select.add(new BSSelectInitBehaviour());
        }
        select.add(new AttributeModifier("title", new ResourceModel("BSControls.Select.Title", "")));
        return super.appendTag("select", true,
                ((bootstrap)
                        ? "class='bs-select form-control'"
                        : "class='form-control'")
                        + (multiple ? "multiple" : ""),
                select);
    }

    @Deprecated
    public Component appendPicklist(Component select) {
        TemplatePanel tt = super.newTemplateTag(t -> "<div><select wicket:id=" + select.getId() + "  multiple=\"multiple\"></select></div>");
        return tt.add(select.add(new PicklistInitBehaviour()));
    }

    public BSControls appendStaticText(Component text) {
        return super.appendTag("p", true, "class='form-control-static'", text);
    }

    public BSControls appendTextarea(Component textarea, Integer lines) {
        return super.appendTag("textarea", true, "class='form-control' rows='" + lines + "'", textarea);
    }

    public BSControls appendHeading(Component text, int level) {
        return super.appendTag("h" + level, true, "class='form-section'", text);
    }

    public BSControls appendInputButton(Component button) {
        return appendInputButton(null, button);
    }

    public BSControls appendSubmitButton(Component button) {
        return appendSubmitButton(null, button);
    }

    public BSControls appendInputButton(String extraClasses, Component button) {
        return super.appendTag("input",
                false,
                "type='button' class='btn btn-default " + defaultString(extraClasses) + "'", button);
    }

    public BSControls appendSubmitButton(String extraClasses, Component button) {
        return super.appendTag("button",
                true,
                "type='submit' class='btn " + defaultString(extraClasses) + " '", button);
    }

    public BSControls appendLinkButton(IModel<?> linkText, AbstractLink link) {
        return appendLinkButton(null, linkText, link);
    }

    public BSControls appendLinkButton(String extraClasses, IModel<?> linkText, AbstractLink link) {
        if (linkText != null)
            link.setBody(linkText);
        return super.appendTag("a", true, "class='btn btn-default " + defaultString(extraClasses) + "'", link);
    }

    public BSControls appendLink(IModel<?> linkText, AbstractLink link) {
        if (linkText != null)
            link.setBody(linkText);
        return super.appendTag("a", true, "class='btn btn-link'", link);
    }

    public BSControls appendInputGroup(IBSComponentFactory<BSInputGroup> factory) {
        return appendComponent(factory);
    }

    public BSInputGroup newInputGroup() {
        return newComponent(BSInputGroup::new);
    }

    public BSControls appendFeedback(Component feedbackComponent) {
        return super.appendTag("span", true, "class='help-block feedback'", feedbackComponent);
    }

    public BSControls appendFeedback(Component fence, IFeedbackMessageFilter filter, IConsumer<Component> feedbackComponentConsumer) {
        IFeedbackPanelFactory factory = ObjectUtils.defaultIfNull(feedbackPanelFactory, IFeedbackPanelFactory.DEFAULT);
        Component feedbackComponent = factory.newFeedbackPanel("controlErrors", fence, filter);
        appendTag("span", true, "class='help-block'", feedbackComponent);
        feedbackComponentConsumer.accept(feedbackComponent);
        return this;
    }

    public Label newHelpBlock(IModel<String> textModel, boolean escapeLabelString) {
        return super.newTag("span", true, "class='help-block subtitle_comp'", newComponent(id -> (Label) new Label(id, textModel).setEscapeModelStrings(escapeLabelString)));
    }

    public Label newHelpBlock(IModel<String> textModel) {
        return newHelpBlock(textModel, true);
    }

    public BSControls appendHelpBlock(IModel<String> textModel, boolean escapeLabelString) {
        newHelpBlock(textModel, escapeLabelString);
        return this;
    }

    public BSControls appendHelpBlock(IModel<String> textModel) {
        return appendHelpBlock(textModel, true);
    }

    public BSControls appendDiv(Component div) {
        return super.appendTag("div", div);
    }

    public BSControls setFeedbackPanelFactory(IFeedbackPanelFactory feedbackPanelFactory) {
        this.feedbackPanelFactory = feedbackPanelFactory;
        return this;
    }

    public interface IFeedbackPanelFactory extends Serializable {

        Component newFeedbackPanel(String id, Component fence, IFeedbackMessageFilter filter);

        IFeedbackPanelFactory DEFAULT = (String id, Component fence, IFeedbackMessageFilter filter) -> {
            BSFeedbackPanel bsFeedbackPanel = new BSFeedbackPanel(id, fence, filter);
            bsFeedbackPanel.add(new Behavior() {
                @Override
                public void renderHead(Component component, IHeaderResponse response) {
                    super.renderHead(component, response);
                    FeedbackPanel fp = (FeedbackPanel) component;
                    if (fp.anyErrorMessage()) {
                        response.render(OnDomReadyHeaderItem.forScript(
                                JQuery.$(fp) + ".closest('.can-have-error').addClass('has-error');"));
                    } else {
                        response.render(OnDomReadyHeaderItem.forScript(
                                JQuery.$(fp) + ".closest('.can-have-error').removeClass('has-error').removeClass('has-warning');"));
                    }
                    if (fp.anyMessage(FeedbackMessage.WARNING)) {
                        response.render(OnDomReadyHeaderItem.forScript(
                                JQuery.$(fp) + ".closest('.can-have-error').addClass('has-warning');"));
                    } else {
                        response.render(OnDomReadyHeaderItem.forScript(
                                JQuery.$(fp) + ".closest('.can-have-error').removeClass('has-error').removeClass('has-warning');"));
                    }
                }
            });
            return bsFeedbackPanel;
        };
    }
}
