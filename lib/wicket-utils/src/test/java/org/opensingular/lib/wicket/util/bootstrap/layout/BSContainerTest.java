package org.opensingular.lib.wicket.util.bootstrap.layout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;
import org.opensingular.lib.wicket.util.SingleFormDummyPage;
import org.opensingular.lib.wicket.util.WicketUtilsDummyApplication;
import org.opensingular.lib.wicket.util.bootstrap.layout.IBSGridCol.BSGridSize;
import org.opensingular.lib.wicket.util.feedback.BSFeedbackPanel;

public class BSContainerTest extends Object {

    @Test
    public void test() {
        WicketTester tester = new WicketTester(new WicketUtilsDummyApplication());

        tester.startPage(new SingleFormDummyPage() {
            @Override
            protected Component newContentPanel(String contentId) {
                new BSContainer<>(contentId, "div", valueModel());

                BSContainer<?> content = new BSContainer<>(contentId, "div")
                    .setInnerStyle("inner");
                content
                    .appendGrid(BSGrid::new)
                    .appendTag("div", new TemplatePanel("div", ""))
                    .newTemplateTag(p -> "<div wicket:id='border1'></div><div wicket:id='border2'></div><div wicket:id='border3'></div>")
                    .add(new BSWellBorder("border1"))
                    .add(BSWellBorder.large("border2"))
                    .add(BSWellBorder.small("border3"));
                content
                    .newGrid()
                    .setDefaultGridSize(BSGridSize.MD)
                    .setAddContainerClass(true)
                    .setAddContainerFluidClass(true)
                    .appendRow(BSRow::new)
                    .appendRow1Col(BSCol::new)
                    .addInfoMessage("msg");
                content
                    .newGrid()
                    .newRow()
                    .appendCol(1, BSCol::new)
                    .appendColTag(1, "div", new WebMarkupContainer("div"))
                    .newFormGroup(1)
                    .newTagWithFactory("div", true, "", WebMarkupContainer::new);
                content.newGrid()
                    .newRow()
                    .newCol()
                    .newTag("div", new WebMarkupContainer("div"));
                content
                    .newGrid()
                    .newColInRow()
                    .newFormGroup()
                    .appendCheckbox(new CheckBox("checkbox", Model.of()))
                    .appendCheckboxChoice(new CheckBoxMultipleChoice<>("checkboxchoice", Model.ofList(new ArrayList<>()), Model.ofList(new ArrayList<>())))
                    .appendDatepicker(new TextField<>("datepicker", valueModel()))
                    .appendDatepicker(new TextField<>("datepicker", valueModel()), new HashMap<>())
                    .appendDiv(new WebMarkupContainer("div"))
                    .appendFeedback(new BSFeedbackPanel("feedback"))
                    .appendFeedback(content, IFeedbackMessageFilter.ALL, c -> c.getId())
                    .appendGrid(gridId -> new BSGrid(gridId))
                    .appendHeading(new BSLabel("heading", 1), 4)
                    .appendHelpBlock(Model.of())
                    .appendInputText(new TextField<>("textfield", valueModel()))
                    .appendInputEmail(new EmailTextField("emailtextfield", Model.of()))
                    .appendInputHidden(new HiddenField<>("hidden", valueModel()))
                    .appendInputPassword(new PasswordTextField("password", Model.of()).setRequired(false))
                    .appendInputButton(new Button("button") {})
                    .appendLabel(new BSLabel("label", "label"))
                    .appendLink(valueModel(), new ExternalLink("link", Model.of("http://localhost:8080")))
                    .appendLinkButton(valueModel(), new ExternalLink("linkbutton", Model.of("http://localhost:8080")))
                    .appendRadioChoice(new RadioChoice<>("radiochoice", valueModel(), listModel()))
                    .appendSelect(new DropDownChoice<>("select", valueModel(), listModel()))
                    .appendSelect(new ListMultipleChoice<>("select", listModel(), listModel()), true)
                    .appendStaticText(new BSLabel("statictext", valueModel()))
                    .appendTag("i", new WebMarkupContainer("i"))
                    .appendTextarea(new TextArea<>("textarea", valueModel()), 3);
                return content;
            }
        });

        tester.submitForm(SingleFormDummyPage.FORM_ID);
        tester.assertNoErrorMessage();
    }

    protected static Model<Serializable> valueModel() {
        return Model.of();
    }

    protected static IModel<List<Serializable>> listModel() {
        return Model.ofList(new ArrayList<>());
    }
}
