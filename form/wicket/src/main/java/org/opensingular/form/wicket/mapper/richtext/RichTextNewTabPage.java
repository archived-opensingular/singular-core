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
import java.util.List;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.filter.HeaderResponseContainer;
import org.apache.wicket.markup.head.filter.JavaScriptFilteredIntoFooterHeaderResponse;
import org.apache.wicket.markup.html.IHeaderResponseDecorator;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.lib.wicket.util.template.SingularTemplate;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("/RichTextNewTabPage")
public class RichTextNewTabPage extends WebPage implements Loggable {

    public static final IHeaderResponseDecorator JAVASCRIPT_DECORATOR = (response) -> new JavaScriptFilteredIntoFooterHeaderResponse(response, SingularTemplate.JAVASCRIPT_CONTAINER);
    public static final String JAVASCRIPT_CONTAINER = "javascript-container";
    private String buttonsExtraRichText = "";
    private String htmlEventSave;
    private String hiddenInput;
    private String enabled;
    private IModel<String> model = new Model<>("TESTANDO");

    public RichTextNewTabPage(PageParameters parameters) {
        super(parameters);
        this.enabled = getPageParameters().get("enabled").toString();
        this.htmlEventSave = getPageParameters().get("htmlEventSave").toString();
        this.buttonsExtraRichText = getPageParameters().get("btnRichTextList").toString();
//        this.htmlContent = markupId;
//        this.hiddenInput = markupId;
        //TODO TODO O ID DEVE COMECAR COM extra
    }
    public RichTextNewTabPage(String teste){
        System.out.println(teste);
    }

    public RichTextNewTabPage(String filterPath, List<BtnRichText> btnRichTextList, String markupId, String markupId1, String enabled) {
//        this.baseurl = baseurl;
//        this.btnRichTextList = btnRichTextList;

        this.hiddenInput = markupId;
        this.enabled = enabled;

    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        try (PackageTextTemplate packageTextTemplate = new PackageTextTemplate(getClass(), "PortletRichTextPanel.js")) {
            final Map<String, String> params = new HashMap<>();

            params.put("label", "TESTE");
            params.put("htmlContainer", "");
            params.put("hiddenInput", hiddenInput);
            params.put("hash", "");
            params.put("html", htmlEventSave);
            params.put("isEnabled", String.valueOf(enabled));
            params.put("buttonsList", buttonsExtraRichText.replaceAll("\\[", "").replaceAll("]", ""));
            packageTextTemplate.interpolate(params);
            response.render(JavaScriptHeaderItem.forScript(packageTextTemplate.getString(), this.getId()));
        } catch (IOException e) {
            getLogger().error(e.getMessage(), e);
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Form form = new Form("form");
        form.add(criarTextArea());

        AjaxButton linkSave = new AjaxButton("onSave", form) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                model.getObject();
                //TODO Oq fazer depois que salvar??

//                target.appendJavaScript("window.close();");
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onError(target, form);
            }
        };

        hiddenInput = linkSave.getMarkupId();
        form.add(linkSave);
        add(form);
        getApplication().setHeaderResponseDecorator(JAVASCRIPT_DECORATOR);
        add(new HeaderResponseContainer(JAVASCRIPT_CONTAINER, JAVASCRIPT_CONTAINER));
    }

    private TextArea<String> criarTextArea() {
        final TextArea<String> components = new TextArea<>("conteudo", model);
        addBehavior(components);
        return components;
    }

    public void addBehavior(TextArea<String> components) {
        components.add(new Behavior() {
            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                super.renderHead(component, response);
            }

            @Override
            public boolean isEnabled(Component component) {
                return component.isVisibleInHierarchy() && component.isEnabledInHierarchy();
            }
        });
    }

    private String ckEditorJs() {
        return "$(function () { var plugin = 'finishAndClose,cancel';            "
                + "            var editor = CKEDITOR.replace(\"ck-text-area\", {\n"
                + "            extraPlugins: plugin,\n"
                + "            allowedContent: true,\n"
                + "            skin: 'office2013',\n"
                + "            language: 'pt-br',\n"
                + "            width: '215mm',\n"
                + "            savePlugin: {\n"
                + "                onSave: function (data) {\n"
                + "                    var jQuerRefOfHtmlContainer = $('#' + 'ck-text-area');\n"
                + "                    jQuerRefOfHtmlContainer.html(data);\n"
                + "\n"
                + "                    var jQueryRefOfHiddenInput = $('#' + hiddenInput);\n"
                + "                    jQueryRefOfHiddenInput.val(data);\n"
                + "                    jQueryRefOfHiddenInput.trigger(\"singular:process\");\n"
                + "                }\n"
                + "            },\n"
                + "            toolbar: [\n"
                + "                {name: 'document', items: ['Closed', 'FinishAndClose', 'Cancel', 'Preview', 'Print']},\n"
                + "                {\n"
                + "                    name: 'clipboard',\n"
                + "                    items: ['Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Undo', 'Redo']\n"
                + "                },\n"
                + "                {name: 'editing', items: ['Find', 'Replace', '-', 'Scayt']},\n"
                + "                {\n"
                + "                    name: 'basicstyles',\n"
                + "                    items: ['Bold', 'Italic', 'Underline', 'Strike', 'Subscript', 'Superscript', '-', 'RemoveFormat']\n"
                + "                },\n"
                + "                {\n"
                + "                    name: 'paragraph',\n"
                + "                    items: ['NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'Blockquote', 'CreateDiv', '-', 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock']\n"
                + "                },\n"
                + "                {name: 'links', items: ['Link', 'Unlink']},\n"
                + "                {name: 'insert', items: ['Table', 'HorizontalRule', 'SpecialChar', 'PageBreak']},\n"
                + "                '/',\n"
                + "                {name: 'styles', items: ['Styles', 'Format', 'FontSize']},\n"
                + "                {name: 'colors', items: ['TextColor', 'BGColor']},\n"
                + "                {name: 'tools', items: ['ShowBlocks']}\n"
                + "            ],\n"
                + "            on: {\n"
                + "                'instanceReady': function (evt) {\n"
                + "                    $('.cke_contents').height($('html').height() - $('.cke_contents').offset().top - $('.cke_bottom').height() - 20);\n"
                + "                }\n"
                + "            }\n"
                + "        });\n"
                + "\n"
                + "            CKEDITOR.config.disableNativeSpellChecker = false;\n"
                + "        });";
    }
}
