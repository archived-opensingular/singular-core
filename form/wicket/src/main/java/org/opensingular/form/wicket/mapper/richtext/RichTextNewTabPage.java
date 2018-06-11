/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.wicket.mapper.richtext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.filter.HeaderResponseContainer;
import org.apache.wicket.markup.head.filter.JavaScriptFilteredIntoFooterHeaderResponse;
import org.apache.wicket.markup.html.IHeaderResponseDecorator;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.opensingular.form.SFormUtil;
import org.opensingular.form.SType;
import org.opensingular.form.view.richtext.RichTextAction;
import org.opensingular.form.view.richtext.RichTextContentContext;
import org.opensingular.form.view.richtext.RichTextContext;
import org.opensingular.form.view.richtext.RichTextInsertContext;
import org.opensingular.form.view.richtext.RichTextSelectionContext;
import org.opensingular.form.view.richtext.SViewByRichTextNewTab;
import org.opensingular.form.wicket.component.BFModalWindow;
import org.opensingular.form.wicket.component.SingularButton;
import org.opensingular.form.wicket.panel.SingularFormPanel;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.lib.wicket.util.modal.BSModalBorder;
import org.opensingular.lib.wicket.util.template.RecursosStaticosSingularTemplate;
import org.opensingular.lib.wicket.util.template.SingularTemplate;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("richtextnewtabpage")
public class RichTextNewTabPage extends WebPage implements Loggable {

    private static final IHeaderResponseDecorator JAVASCRIPT_DECORATOR = response -> new JavaScriptFilteredIntoFooterHeaderResponse(response, SingularTemplate.JAVASCRIPT_CONTAINER);
    private static final String JAVASCRIPT_CONTAINER = "javascript-container";

    private static final String INNER_TEXT = "innerText";
    private static final String INDEX = "index";
    private static final String SELECTED = "selected";

    private ISupplier<SViewByRichTextNewTab> viewSupplier;
    private IModel<String> modelTextArea;

    private boolean visibleMode;
    private AbstractDefaultAjaxBehavior eventSaveCallbackBehavior;
    private BFModalWindow bfModalWindow;
    private AjaxButton submitButton;
    private HiddenField<String> hiddenInput;
    private String markupId;

    /**
     * The new Rich Text Page constructor.
     *
     * @param title        The title of page.
     * @param visibleMode  True if is just visible model; False if is editable.
     * @param viewSupplier The suplier of new Tab View.
     * @param hiddenInput  The hidden input of the Page who calls.
     * @param markupId     The markupId of the Label of the Page who calls.
     */
    public RichTextNewTabPage(String title, boolean visibleMode, ISupplier<SViewByRichTextNewTab> viewSupplier,
            HiddenField<String> hiddenInput, String markupId) {
        this.visibleMode = visibleMode;
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

        try (PackageTextTemplate packageTextTemplate = new PackageTextTemplate(getClass(), "PortletRichTextPanel.js")) {
            final Map<String, String> params = new HashMap<>();

            /*If don't contains the View, i add a view with empty buttons, for default use.*/
            if (!viewSupplier.optional().isPresent()) {
                viewSupplier = (ISupplier<SViewByRichTextNewTab>) SViewByRichTextNewTab::new;
            }

            params.put("submitButtonId", submitButton.getMarkupId());
            params.put("classDisableDoubleClick", viewSupplier.get()
                    .getConfiguration()
                    .getDoubleClickDisabledClasses()
                    .stream()
                    .reduce(new StringBuilder(), (s, b) -> s.append(b).append(", "), StringBuilder::append).toString());
            params.put("hiddenInput", this.hiddenInput.getMarkupId());
            params.put("htmlContainer", this.markupId);
            params.put("callbackUrl", eventSaveCallbackBehavior.getCallbackUrl().toString());
            params.put("isEnabled", String.valueOf(visibleMode));

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
        eventSaveCallbackBehavior = new AbstractDefaultAjaxBehavior() {
            @Override
            protected void respond(AjaxRequestTarget target) {

                IRequestParameters requestParameters = RichTextNewTabPage.this.getRequest().getRequestParameters();
                String text = requestParameters.getParameterValue(INNER_TEXT).toString();
                Integer index = requestParameters.getParameterValue(INDEX).toInt();
                String selected = requestParameters.getParameterValue(SELECTED).toString();

                RichTextAction richTextAction = viewSupplier.get().getTextActionList().get(index);
                if (richTextAction != null) {
                    if (richTextAction.getForm().isPresent()) {
                        configureModal(target, text, index, selected, richTextAction);
                    } else {
                        RichTextContext richTextContext = returnRichTextContextInitialized(richTextAction, selected, text);
                        richTextAction.onAction(richTextContext, Optional.empty());
                        changeValueRichText(target, richTextContext, richTextAction.getType());
                    }
                }
            }

            private void configureModal(AjaxRequestTarget target, String text, Integer index, String selected, RichTextAction richTextAction) {
                Class<? extends SType<?>> stypeActionButton = (Class<? extends SType<?>>) richTextAction.getForm().orElse(null);
                SingularFormPanel singularFormPanel = new SingularFormPanel("modalBody", stypeActionButton);
                singularFormPanel.setOutputMarkupId(true);
                bfModalWindow.setBody(singularFormPanel);
                bfModalWindow.addButton(BSModalBorder.ButtonStyle.CANCEL, Model.of("Cancelar"), createCancelButton());
                bfModalWindow.addButton(BSModalBorder.ButtonStyle.CONFIRM, Model.of("Confirmar"), createConfirmButton(singularFormPanel, index, selected, text));
                bfModalWindow.setTitleText(Model.of(SFormUtil.getTypeLabel(stypeActionButton).orElse(richTextAction.getLabel())));
                bfModalWindow.show(target);
            }

            private SingularButton createConfirmButton(SingularFormPanel singularFormPanel, int actionIndex, String selected, String text) {
                return new SingularButton("btnConfirmar", singularFormPanel.getInstanceModel()) {

                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        RichTextAction richTextAction = viewSupplier.get().getTextActionList().get(actionIndex);
                        RichTextContext richTextContext = returnRichTextContextInitialized(richTextAction, selected, text);
                        richTextAction.onAction(richTextContext, Optional.of(singularFormPanel.getInstance()));

                        if (richTextContext.getValue() != null) {
                            changeValueRichText(target, richTextContext, richTextAction.getType());
                            bfModalWindow.hide(target);
                        }
                    }
                };
            }

            private AjaxButton createCancelButton() {
                return new AjaxButton("btnCancelar") {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        bfModalWindow.hide(target);
                    }
                };
            }
        };
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

    /**
     * Method that contains the logic to return the correct implementation of RichTextContext
     *
     * @param richTextAction the RichText that contains the attribute Type.
     * @param selected       The selected of Text.
     * @param content        The Context of Text.
     * @return the RichText implementation according the Type of RichText.
     */
    private RichTextContext returnRichTextContextInitialized(RichTextAction richTextAction, String selected, String content) {
        if (richTextAction.getType().equals(RichTextSelectionContext.class)) {
            return new RichTextSelectionContext(selected);
        }
        if (richTextAction.getType().equals(RichTextContentContext.class)) {
            return new RichTextContentContext(content);
        }
        if (richTextAction.getType().equals(RichTextInsertContext.class)) {
            return new RichTextInsertContext();
        }
        throw new NullPointerException("Don't find any implementation of the RichTextContext!");
    }

    /**
     * Method that change the value of CkEditor.
     *
     * @param target          The AjaxTarget with is responsible of execute a JS for update CKEditor value.
     * @param richTextContext The richTextContext with is responsible to contains the value to be placed.
     * @param typeRichText    The Type of RichTextContext.
     */
    private void changeValueRichText(AjaxRequestTarget target, RichTextContext richTextContext, Class<? extends RichTextContext> typeRichText) {
        //Caso a String seja vazia significa que o texto deverá ser limpado.
        if (richTextContext.getValue() != null) {
            if (typeRichText.equals(RichTextInsertContext.class) || typeRichText.equals(RichTextSelectionContext.class)) {
                target.appendJavaScript("CKEDITOR.instances['ck-text-area'].insertHtml('" + richTextContext.getValue() + "');");
            } else if (typeRichText.equals(RichTextContentContext.class)) {
                target.appendJavaScript("CKEDITOR.instances['ck-text-area'].setData('" +
                        formatHtmlValue(richTextContext) + "');");
            }
        }
    }

    /**
     * Method to remove the break lines (\n) and the carriage return (\r). If don't do that the JS will return a exception.
     *
     * @param richTextContext The content that will be placed in the CKeditor.
     * @return The HTML or text formatted.
     */
    private String formatHtmlValue(RichTextContext richTextContext) {
        return richTextContext.getValue().replaceAll("\r", "").replaceAll("\n", "");
    }

}
