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

import static org.apache.commons.lang3.StringUtils.defaultString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.wicket.Component;
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
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.lib.wicket.util.behavior.BSSelectInitBehaviour;
import org.opensingular.lib.wicket.util.behavior.DatePickerInitBehaviour;
import org.opensingular.lib.wicket.util.behavior.PicklistInitBehaviour;
import org.opensingular.lib.wicket.util.bootstrap.datepicker.BSDatepickerConstants;
import org.opensingular.lib.wicket.util.feedback.BSFeedbackPanel;
import org.opensingular.lib.wicket.util.jquery.JQuery;
import org.opensingular.lib.wicket.util.resource.Icone;

public class BSControls extends BSContainer<BSControls> implements IBSGridCol<BSControls> {

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
        return this.appendCheckbox(checkbox, new Label("_", labelModel));
    }

    public BSControls appendCheckbox(Component checkbox, Component label) {
        this
            .appendTag("div", true, "class='checkbox'", new BSContainer<>("_" + checkbox.getId())
                .appendTag("label", new BSContainer<>("_")
                    .appendTag("input", false, "type='checkbox'", checkbox)
                    .appendTag("span", label)));
        return this;
    }

    public BSControls appendCheckboxChoice(Component checkbox) {
        return super.appendTag("div", true, "class='checkbox-list'", checkbox);
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

    public BSControls appendDatepicker(Component datepicker) {
        return this.appendDatepicker(datepicker, null);
    }

    public BSControls appendDatepicker(Component datepicker, Map<String, String> extraAttributes) {
        Map<String, String> attrs = new HashMap<String, String>();
        attrs.put("data-date-format", "dd/mm/yyyy");
        attrs.put("data-date-start-date", "01/01/1900");
        attrs.put("data-date-end-date", "31/12/2999");
        attrs.put("data-date-start-view", "days");
        attrs.put("data-date-min-view-mode", "days");
        if (extraAttributes != null)
            attrs.putAll(extraAttributes);

        this.appendInputGroup(componentId -> {
            BSInputGroup inputGroup = newInputGroup();
            return (BSInputGroup) inputGroup
                .appendExtraClasses(" date ")
                .appendExtraAttributes(attrs)
                .appendInputText(datepicker.setMetaData(BSDatepickerConstants.KEY_CONTAINER, inputGroup))
                .appendButtonAddon(Icone.CALENDAR)
                .add(new DatePickerInitBehaviour());
        });
        return this;
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

        return super.appendTag("select", true,
            ((bootstrap)
                ? "class='bs-select form-control' title='" + getString("BSControls.Select.Title") + "'"
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

    public BSControls appendTextarea(Component textarea, Integer linhas) {
        return super.appendTag("textarea", true, "class='form-control' rows='" + linhas + "'", textarea);
    }

    public BSControls appendHeading(Component text, int level) {
        return super.appendTag("h" + level, true, "class='form-section'", text);
    }

    public BSControls appendInputButton(Component button) {
        return appendInputButton(null, button);
    }

    public BSControls appendInputButton(String extraClasses, Component button) {
        return super.appendTag("input",
            false,
            "type='button' class='btn btn-default " + defaultString(extraClasses) + "'", button);
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
