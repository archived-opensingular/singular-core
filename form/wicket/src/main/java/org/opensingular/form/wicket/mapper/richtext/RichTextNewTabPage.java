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

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.PageExpiredException;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.opensingular.form.view.richtext.RichTextAction;
import org.opensingular.form.view.richtext.SViewByRichTextNewTab;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.component.BFModalWindow;
import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.lib.wicket.util.template.SingularTemplate;
import org.wicketstuff.annotation.mount.MountPath;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@MountPath("richtextnewtabpage")
public class RichTextNewTabPage extends SingularTemplate implements Loggable {

    private WicketBuildContext wicketBuildContext;
    private IModel<String>     modelTextArea;

    private Model<Boolean>                     readOnly;
    private AbstractDefaultAjaxBehavior eventSaveCallbackBehavior;
    private BFModalWindow               bfModalWindow;
    private AjaxButton                  submitButton;
    private HiddenField<String>         hiddenInput;
    private Model<String                     >               previewFrameMarkupId;
    private Model<String>               title;

    /**
     * Default constructor
     */
    public RichTextNewTabPage() {
        throw new PageExpiredException("Construtor without arguments was called!");
    }

    /**
     * The new Rich Text Page constructor.
     *
     * @param title                The title of page.
     * @param readOnly             True if is just readOnly model; False if is editable.
     * @param wicketBuildContext   The WicketBuildContext
     * @param hiddenInput          The hidden input of the Page who calls.
     * @param previewFrameMarkupId The previewFrameMarkupId of the Label of the Page who calls.
     */
    public RichTextNewTabPage(String title, boolean readOnly, WicketBuildContext wicketBuildContext,
                              HiddenField<String> hiddenInput, String previewFrameMarkupId) {
        this.readOnly             = Model.of(readOnly);
        this.wicketBuildContext   = wicketBuildContext;
        this.hiddenInput          = hiddenInput;
        this.previewFrameMarkupId = Model.of(previewFrameMarkupId);
        this.title = Model.of(title);
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
        add(createPageContainer());
    }

    private Component createPageContainer() {
        Component container = new TransparentWebMarkupContainer("pageContainer");
        if (retrieveView().isA4LayoutEnabled()) {
            container.add(AttributeAppender.replace("style", "width: 215mm; margin-right: auto; margin-left: auto"));
        }
        return container;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(new PackageResourceReference(RichTextNewTabPage.class,
                "RichTextNewTabPage.css")));

        try (PackageTextTemplate packageTextTemplate = new PackageTextTemplate(getClass(), "RichTextNewTabPage.js")) {
            final Map<String, Object> params = new HashMap<>();
            SViewByRichTextNewTab     view   = retrieveView();

            params.put("submitButtonId", submitButton.getMarkupId());
            params.put("classDisableDoubleClick", view
                    .getConfiguration()
                    .getDoubleClickDisabledClasses()
                    .stream()
                    .reduce(new StringBuilder(), (s, b) -> s.append(b).append(", "), StringBuilder::append).toString());
            params.put("hiddenInput", this.hiddenInput.getMarkupId());
            params.put("showSaveButton", String.valueOf(view.isShowSaveButton()));
            params.put("previewFrameMarkupId", this.previewFrameMarkupId.getObject());
            params.put("callbackUrl", eventSaveCallbackBehavior.getCallbackUrl().toString());
            params.put("isEnabled", String.valueOf(!readOnly.getObject()));
            params.put("a4LayoutEnabled", view.isA4LayoutEnabled());
            params.put("sourceViewEnabled", view.isSourceViewEnabled());

            params.put("buttonsList", this.renderButtonsList(view));
            packageTextTemplate.interpolate(params);
            response.render(JavaScriptHeaderItem.forScript(packageTextTemplate.getString(), this.getId()));

        } catch (IOException e) {
            getLogger().error(e.getMessage(), e);
        }
    }

    private SViewByRichTextNewTab retrieveView() {
        SViewByRichTextNewTab view = wicketBuildContext.getViewSupplier(SViewByRichTextNewTab.class).get();
        /*If don't contains the View, i add a view with empty buttons, for default use.*/
        if (view == null) {
            view = new SViewByRichTextNewTab();
            getLogger().info("SViewByRichTextNewTab was insert in the RichTextNewTabPage.");
        }
        return view;
    }

    /**
     * Method to create a text containing all the configuration of the buttons to pass to JS.
     * It use "#$" to separate any element of RichTextAction class, and ",," for any button.
     *
     * @param view
     * @return A text formmated contain list of buttons to JS.
     */
    private String renderButtonsList(SViewByRichTextNewTab view) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < view.getTextActionList().size(); i++) {
            final RichTextAction richTextAction = view.getTextActionList().get(i);
            if (richTextAction.isVisible(wicketBuildContext.getCurrentInstance())) {
                String actionButtonFormatted = i + "#$" + richTextAction.getLabel()
                        + "#$" + richTextAction.getIcon().getCssClass()
                        + "#$" + richTextAction.getLabelInline()
                        + ",,";
                sb.append(actionButtonFormatted);
            }
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
        eventSaveCallbackBehavior = new RichTextButtonAjaxBehavior(bfModalWindow, this, wicketBuildContext);
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

    @Override
    protected IModel<String> createPageTitleModel() {
        return title;
    }

    @Override
    protected void detachModel() {
        super.detachModel();
        title.detach();
        previewFrameMarkupId.detach();
        modelTextArea.detach();
        hiddenInput.detach();
        readOnly.detach();
    }
}
