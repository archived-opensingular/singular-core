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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.model.SInstanceValueModel;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.lib.wicket.util.resource.DefaultIcons;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.opensingular.lib.wicket.util.jquery.JQuery.$;
import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;

public class PortletRichTextPanel extends Panel implements Loggable {

    private HiddenField hiddenInput;
    private Label htmlContent;
    private Label label;
    private String hash;
    private WicketBuildContext ctx;
    private boolean visibleMode = true;
    private CallbackAjaxBehaviour eventSaveCallbackBehavior;

    private List<BtnRichText> btnRichTextList = new ArrayList<>();

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
    }

    public PortletRichTextPanel(String id, WicketBuildContext ctx) {
        super(id);
        this.ctx = ctx;
        hash = RandomStringUtils.random(10, true, false);

        //TODO REMOVER
        gerarMassa("ExtraButtons");
        gerarMassa("ExtraButtons2");
        gerarMassa("ExtraButtons3");
        gerarMassa("ExtraButtons4");
    }

    public WebMarkupContainer configureLabelButton() {
        IModel<String> buttonMsg = new Model<>();
        WebMarkupContainer containerLabel = new WebMarkupContainer("containerLabel");
        Label labelMsg = new Label("buttonMsg", buttonMsg);
        WebMarkupContainer iconeClass = new WebMarkupContainer("iconeClass");
        if (visibleMode) {
            buttonMsg.setObject("Editar");
            iconeClass.add(new AttributeAppender("class", DefaultIcons.PENCIL));
        } else {
            buttonMsg.setObject("Visualizar");
            iconeClass.add(new AttributeAppender("class", DefaultIcons.EYE));
        }
        containerLabel.add(iconeClass);
        containerLabel.add(labelMsg);

        return containerLabel;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        eventSaveCallbackBehavior = new CallbackAjaxBehaviour(p -> {
            p.getPageParameters();
        });
        add(eventSaveCallbackBehavior);
        build(ctx);
        addBehaviours();
    }

    private void addBehaviours() {
        add($b.onReadyScript(c -> $(htmlContent).append(".html(").append($(hiddenInput).append(".val())"))));
    }

    private void build(WicketBuildContext ctx) {
        label = new Label("label", Model.of(Optional.ofNullable(ctx.getCurrentInstance().asAtr().getLabel()).orElse(EMPTY)));
        htmlContent = new Label("htmlContent", new SInstanceValueModel<>(ctx.getModel()));
        hiddenInput = new HiddenField<>("hiddenInput", new SInstanceValueModel<>(ctx.getModel()));

        add(label);
        add(htmlContent);
        add(hiddenInput);
        WebMarkupContainer buttonEditar = createButtonOpenEditor();
        buttonEditar.add(configureLabelButton());
        add(buttonEditar);

        htmlContent.setEscapeModelStrings(false);
    }

    private AbstractLink createButtonOpenEditor() {

        PageParameters pageParameters = new PageParameters();
        pageParameters.add("enabled", String.valueOf(visibleMode));
        pageParameters.add("btnRichTextList", btnRichTextList);
        pageParameters.add("htmlEventSave", eventSaveCallbackBehavior.getCallbackUrl());

        return new BookmarkablePageLink("button", RichTextNewTabPage.class, pageParameters) {

            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                tag.put("target", "_blank");
            }

        };

    }

    public boolean isVisibleMode() {
        return visibleMode;
    }

    public void setVisibleMode(boolean visibleMode) {
        this.visibleMode = visibleMode;
    }

    public void addButton(BtnRichText btnRichText) {
        this.btnRichTextList.add(btnRichText);
    }

    private static class CallbackAjaxBehaviour extends AbstractDefaultAjaxBehavior {
        private final IConsumer<AjaxRequestTarget> callback;

        private CallbackAjaxBehaviour(IConsumer<AjaxRequestTarget> callback) {
            this.callback = callback;
        }

        @Override
        protected void respond(AjaxRequestTarget ajaxRequestTarget) {
            callback.accept(ajaxRequestTarget);
        }
    }

    //TODO REMOVER
    private void gerarMassa(String id) {
        addButton(new BtnRichText(id, id, id, "TESTEEE") {
            @Override
            public void getAction(CkEditorContext editorContext) {
                editorContext.getValue();
            }
        });
    }
}