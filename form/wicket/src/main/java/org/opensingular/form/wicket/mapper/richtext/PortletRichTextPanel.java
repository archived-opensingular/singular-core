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

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.opensingular.form.view.richtext.SViewByRichTextNewTab;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.model.SInstanceValueModel;
import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.lib.wicket.util.resource.DefaultIcons;

import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.opensingular.lib.wicket.util.jquery.JQuery.$;
import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;

public class PortletRichTextPanel extends Panel implements Loggable {

    private HiddenField<String> hiddenInput;
    private Label               htmlContent;
    private Label               label;
    private WicketBuildContext  ctx;
    private boolean             readOnlyMode = true;
    private WebMarkupContainer  buttonEditar;

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
    }

    public PortletRichTextPanel(String id, WicketBuildContext ctx, boolean readOnlyMode) {
        super(id);
        this.ctx = ctx;
        this.readOnlyMode = readOnlyMode;
    }

    public WebMarkupContainer configureLabelButton() {
        return new WebMarkupContainer("containerLabel") {

            IModel<String> buttonMsg = new Model<>();
            Label labelMsg = new Label("buttonMsg", buttonMsg);
            WebMarkupContainer iconeClass = new WebMarkupContainer("iconeClass");

            @Override
            protected void onInitialize() {
                super.onInitialize();
                this.add(iconeClass);
                this.add(labelMsg);
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                if (!isReadOnlyMode()) {
                    buttonMsg.setObject("Editar");
                    iconeClass.add(new AttributeModifier("class", DefaultIcons.PENCIL));
                } else {
                    buttonMsg.setObject("Visualizar");
                    iconeClass.add(new AttributeModifier("class", DefaultIcons.EYE));
                }

            }
        };
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
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
        buttonEditar = createButtonOpenEditor();
        buttonEditar.add(configureLabelButton());
        add(buttonEditar);


        htmlContent.setEscapeModelStrings(false);
    }


    private WebMarkupContainer createButtonOpenEditor() {
        return new Link<String>("button") {

            @Override
            protected void onConfigure() {
                super.onConfigure();
                this.setVisible(PortletRichTextPanel.this.isEnabledInHierarchy());
            }

            @Override
            public void onClick() {
                throw new RestartResponseException(new RichTextNewTabPage(label.getDefaultModelObject().toString(),
                        isReadOnlyMode(),
                        PortletRichTextPanel.this.ctx,
                        hiddenInput, htmlContent.getMarkupId()));
            }

            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                tag.put("onclick", "window.open('" + getURL() + "', '_blank" + htmlContent.getMarkupId() + "');");
            }

        };
    }

    public boolean isReadOnlyMode() {
        return readOnlyMode || !this.isEnabledInHierarchy();
    }
}