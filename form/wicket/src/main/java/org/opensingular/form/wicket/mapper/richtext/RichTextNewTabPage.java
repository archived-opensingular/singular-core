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

package org.opensingular.form.wicket.mapper.richtext;

import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.filter.HeaderResponseContainer;
import org.apache.wicket.markup.head.filter.JavaScriptFilteredIntoFooterHeaderResponse;
import org.apache.wicket.markup.html.IHeaderResponseDecorator;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.PageExpiredException;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.opensingular.form.view.richtext.RichTextAction;
import org.opensingular.form.view.richtext.SViewByRichTextNewTab;
import org.opensingular.form.wicket.component.BFModalWindow;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.lib.wicket.util.template.RecursosStaticosSingularTemplate;
import org.opensingular.lib.wicket.util.template.SingularTemplate;
import org.wicketstuff.annotation.mount.MountPath;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@MountPath("richtextnewtabpage")
public class RichTextNewTabPage extends WebPage implements Loggable {

    private static final IHeaderResponseDecorator JAVASCRIPT_DECORATOR = response -> new JavaScriptFilteredIntoFooterHeaderResponse(response, SingularTemplate.JAVASCRIPT_CONTAINER);
    private static final String JAVASCRIPT_CONTAINER = "javascript-container";

    private ISupplier<SViewByRichTextNewTab> viewSupplier;
    private IModel<String> modelTextArea;

    private boolean                     readOnly;
    private AbstractDefaultAjaxBehavior eventSaveCallbackBehavior;
    private BFModalWindow               bfModalWindow;
    private AjaxButton                  submitButton;
    private HiddenField<String>         hiddenInput;
    private String                      markupId;

    /**
     * Default constructor
     */
    public RichTextNewTabPage(){
        throw new PageExpiredException("Construtor without arguments was called!");
    }

    /**
     * The new Rich Text Page constructor.
     *
     * @param title        The title of page.
     * @param readOnly  True if is just readOnly model; False if is editable.
     * @param viewSupplier The suplier of new Tab View.
     * @param hiddenInput  The hidden input of the Page who calls.
     * @param markupId     The markupId of the Label of the Page who calls.
     */
    public RichTextNewTabPage(String title, boolean readOnly, ISupplier<SViewByRichTextNewTab> viewSupplier,
                              HiddenField<String> hiddenInput, String markupId) {
        this.readOnly = readOnly;
        this.viewSupplier = viewSupplier;
        this.hiddenInput = hiddenInput;
        this.markupId = markupId;
        add(new Label("title", Model.of(title)));
        this.modelTextArea = hiddenInput.getModel();

    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        Form form = new Form("form");
        createTextArea(form);
        createSubmitButton(form);
        createModal(form);
        createCallBackBehavior();
        add(form);
        getApplication().setHeaderResponseDecorator(JAVASCRIPT_DECORATOR);
        add(new HeaderResponseContainer(JAVASCRIPT_CONTAINER, JAVASCRIPT_CONTAINER));
    }


    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        try (PackageTextTemplate packageTextTemplate = new PackageTextTemplate(getClass(), "RichTextNewTabPage.js")) {
            final Map<String, String> params = new HashMap<>();

            /*If don't contains the View, i add a view with empty buttons, for default use.*/
            if (!viewSupplier.optional().isPresent()) {
                viewSupplier = (ISupplier<SViewByRichTextNewTab>) SViewByRichTextNewTab::new;
                getLogger().info("SViewByRichTextNewTab was insert in the RichTextNewTabPage.");
            }

            params.put("submitButtonId", submitButton.getMarkupId());
            params.put("classDisableDoubleClick", viewSupplier.get()
                    .getConfiguration()
                    .getDoubleClickDisabledClasses()
                    .stream()
                    .reduce(new StringBuilder(), (s, b) -> s.append(b).append(", "), StringBuilder::append).toString());
            params.put("hiddenInput", this.hiddenInput.getMarkupId());
            params.put("showSaveButton", String.valueOf(this.viewSupplier.get().isShowSaveButton()));
            params.put("htmlContainer", this.markupId);
            params.put("callbackUrl", eventSaveCallbackBehavior.getCallbackUrl().toString());
            params.put("isEnabled", String.valueOf(!readOnly));

            params.put("buttonsList", this.renderButtonsList());
            packageTextTemplate.interpolate(params);
            response.render(JavaScriptHeaderItem.forScript(packageTextTemplate.getString(), this.getId()));

        } catch (IOException e) {
            getLogger().error(e.getMessage(), e);
        }
        RecursosStaticosSingularTemplate.getStyles("singular").forEach(response::render);
        RecursosStaticosSingularTemplate.getJavaScriptsUrls().forEach(response::render);

    }

    /**
     * Method to create a text containing all the configuration of the buttons to pass to JS.
     * It use "#$" to separate any element of RichTextAction class, and ",," for any button.
     *
     * @return A text formmated contain list of buttons to JS.
     */
    private String renderButtonsList() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < viewSupplier.get().getTextActionList().size(); i++) {
            RichTextAction richTextAction = viewSupplier.get().getTextActionList().get(i);
            String actionButtonFormatted = i + "#$" + richTextAction.getLabel()
                    + "#$" + richTextAction.getIcon().getCssClass()
                    + "#$" + richTextAction.getLabelInline()
                    + ",,";
            sb.append(actionButtonFormatted);
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 2);
        }
        return sb.toString();
    }

    /**
     * Method that create a CallBackBehavior when the button is clicked.
     */
    private void createCallBackBehavior() {
        eventSaveCallbackBehavior = new RichTextButtonAjaxBehavior(bfModalWindow, this, viewSupplier);
        add(eventSaveCallbackBehavior);
    }

    /**
     * Button called when button is clicked, or button of modal is clicked.
     *
     * @param form Form that contains the button.
     */
    private void createSubmitButton(Form form) {
        submitButton = new AjaxButton("submitButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                /*Submit is call in the JS to update the model of textArea.*/
            }
        };
        form.add(submitButton);
    }

    /**
     * Modal that show's the button Stype if exists.
     *
     * @param form form where the modal's will be placed.
     */
    private void createModal(Form form) {
        bfModalWindow = new BFModalWindow("modalCkEditor", false, true);
        form.add(bfModalWindow);
    }

    private void createTextArea(Form form) {
        form.add(new TextArea<>("conteudo", modelTextArea));
    }

}
