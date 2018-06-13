package org.opensingular.form.wicket.mapper.richtext;

import java.util.Optional;

import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.IRequestParameters;
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
import org.opensingular.lib.wicket.util.modal.BSModalBorder;

public class RichTextButtonAjaxBehavior extends AbstractDefaultAjaxBehavior {

    private static final String INNER_TEXT = "innerText";
    private static final String INDEX = "index";
    private static final String SELECTED = "selected";

    private BFModalWindow bfModalWindow;
    private WebPage webPage;
    private ISupplier<SViewByRichTextNewTab> viewSupplier;

    RichTextButtonAjaxBehavior(BFModalWindow bfModalWindow, WebPage webPage, ISupplier<SViewByRichTextNewTab> viewSupplier) {
        this.bfModalWindow = bfModalWindow;
        this.webPage = webPage;
        this.viewSupplier = viewSupplier;
    }

    @Override
    protected void respond(AjaxRequestTarget target) {
        IRequestParameters requestParameters = webPage.getRequest().getRequestParameters();
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

    private AjaxButton createCancelButton() {
        return new AjaxButton("btnCancelar") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                bfModalWindow.hide(target);
            }
        };
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
