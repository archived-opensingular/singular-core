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

import org.apache.commons.lang3.StringEscapeUtils;
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

@MountPath("/RichTextNewTabPage") //TODO CAIXA BAIXA SEM /
public class RichTextNewTabPage extends WebPage implements Loggable {

    public static final IHeaderResponseDecorator JAVASCRIPT_DECORATOR = (response) -> new JavaScriptFilteredIntoFooterHeaderResponse(response, SingularTemplate.JAVASCRIPT_CONTAINER);
    public static final String JAVASCRIPT_CONTAINER = "javascript-container";
    private final ISupplier<SViewByRichTextNewTab> viewSupplier;
    private HiddenField<String> hiddenInput;
    private String htmlContainer;
    private IModel<String> modelTextArea;

    private boolean visibleMode;
    private AbstractDefaultAjaxBehavior eventSaveCallbackBehavior;
    private BFModalWindow bfModalWindow;
    private TextArea<String> textArea;

    public RichTextNewTabPage(String title, boolean visibleMode, ISupplier<SViewByRichTextNewTab> viewSupplier,
            HiddenField<String> hiddenInput, String htmlContainer) {
        this.visibleMode = visibleMode;
        this.viewSupplier = viewSupplier;
        this.hiddenInput = hiddenInput;
        this.htmlContainer = htmlContainer;
        modelTextArea = this.hiddenInput.getModel();
        add(new Label("title", Model.of(title)));

        //IMPORTANTE -> TODO O ID DEVE COMECAR COM extra

    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        try (PackageTextTemplate packageTextTemplate = new PackageTextTemplate(getClass(), "PortletRichTextPanel.js")) {
            final Map<String, String> params = new HashMap<>();

            params.put("htmlContainer", this.htmlContainer);
            params.put("hiddenInput", this.hiddenInput.getMarkupId());
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

    private String renderButtonsList() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < viewSupplier.get().getBtnRichTextList().size(); i++) {
            RichTextAction richTextAction = viewSupplier.get().getBtnRichTextList().get(i);
            sb.append(i)
                    .append("#$")
                    .append(richTextAction.getLabel())
                    .append("#$")
                    .append(richTextAction.getIconUrl())
                    .append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Form form = new Form("form");
        form.add(criarTextArea());

        createModal(form);
        createCallBackBehavior();

        add(form);
        getApplication().setHeaderResponseDecorator(JAVASCRIPT_DECORATOR);
        add(new HeaderResponseContainer(JAVASCRIPT_CONTAINER, JAVASCRIPT_CONTAINER));

    }

    private void createCallBackBehavior() {
        eventSaveCallbackBehavior = new AbstractDefaultAjaxBehavior() {
            @Override
            protected void respond(AjaxRequestTarget target) {

                IRequestParameters requestParameters = RichTextNewTabPage.this.getRequest().getRequestParameters();
                String text = requestParameters.getParameterValue("innerText").toString();
                modelTextArea.setObject(text);
                Integer index = requestParameters.getParameterValue("index").toInt();

                Integer cursorPosition = requestParameters.getParameterValue("cursorPosition").toInt();


                //TODO REMOVER INDEX FIXO.
                RichTextAction richTextAction = viewSupplier.get().getBtnRichTextList().get(index);
                if (richTextAction != null) {
                    if (richTextAction.getForm().isPresent()) {
                        SingularFormPanel singularFormPanel = new SingularFormPanel("modalBody", (Class<? extends SType<?>>) richTextAction.getForm().get());
                        bfModalWindow.setBody(singularFormPanel);
                        bfModalWindow.addButton(BSModalBorder.ButtonStyle.CANCEL, Model.of("Cancelar"), createCancelButton());
                        bfModalWindow.addButton(BSModalBorder.ButtonStyle.CONFIRM, Model.of("Confirmar"), createConfirmButton(singularFormPanel, index, cursorPosition));
                        bfModalWindow.setTitleText(Model.of(richTextAction.getLabel())); //TODO VERIFICAR SE PRECISA ALTERAR
                        bfModalWindow.show(target);
                    } else {
                        RichTextContext richTextContext = returnRichTextContextInitialized(richTextAction);
                        richTextAction.onAction(richTextContext, Optional.empty());
                        changeValueRichText(target, richTextContext, richTextAction.getType(), cursorPosition);
                    }
                }


            }

            private SingularButton createConfirmButton(SingularFormPanel singularFormPanel, int actionIndex, Integer cursorPosition) {
                return new SingularButton("btnConfirmar", singularFormPanel.getInstanceModel()) {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        RichTextAction richTextAction = viewSupplier.get().getBtnRichTextList().get(actionIndex);
                        RichTextContext richTextContext = returnRichTextContextInitialized(richTextAction);

                        richTextAction.onAction(richTextContext, Optional.of(singularFormPanel.getInstance()));

                        changeValueRichText(target, richTextContext, richTextAction.getType(), cursorPosition);
                        bfModalWindow.hide(target);
                    }

                    //TODO realizar action.
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
     * Método que contem a logica para retornar o RichText especifico de acordo com o que foi criado no SType.
     *
     * @param richTextAction a interface RichText da lista.
     * @return retorna o RichText especifico de acordo com o type.
     */
    private RichTextContext returnRichTextContextInitialized(RichTextAction richTextAction) {
        if (richTextAction.getType().equals(RichTextInsertContext.class)) {
            return new RichTextInsertContext();
        }
        if (richTextAction.getType().equals(RichTextSelectionContext.class)) {
            return new RichTextSelectionContext() {
                @Override
                public String getTextSelected() {
                    return "teste";
                }
            };
        }
        if (richTextAction.getType().equals(RichTextContentContext.class)) {
            return new RichTextContentContext() {
                @Override
                public String getContent() {
                    return modelTextArea.getObject();
                }
            };
        }
        return null;
    }

    private void changeValueRichText(AjaxRequestTarget target, RichTextContext richTextContext, Class typeRichText, Integer cursorPosition) {
        if (richTextContext.getValue() != null) {
            if (typeRichText.equals(RichTextInsertContext.class)) {
                String modalTextArea = StringEscapeUtils.unescapeHtml4(modelTextArea.getObject());
                String partInit = modalTextArea.substring(0, cursorPosition);
                String partFinal = modalTextArea.substring(cursorPosition, modalTextArea.length() -1);
                String partExtra = richTextContext.getValue();
                modelTextArea.setObject(partInit + partExtra + partFinal);
            } else {
                modelTextArea.setObject(richTextContext.getValue());
            }
            textArea.modelChanged();
            target.add(textArea, this.getPage());
        }
    }

    private void createModal(Form form) {
        //TODO Entender melhor os parametros do ModalWIndow (true, true)
        bfModalWindow = new BFModalWindow("modalCkEditor", true, true);
        WebMarkupContainer container = new WebMarkupContainer("modalBody");
        container.setOutputMarkupId(true);
        bfModalWindow.setBody(container);

        form.add(bfModalWindow);
    }

    private TextArea<String> criarTextArea() {
        textArea = new TextArea<>("conteudo", modelTextArea);
        return textArea;
    }


}
