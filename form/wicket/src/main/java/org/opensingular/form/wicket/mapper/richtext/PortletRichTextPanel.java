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
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.model.SInstanceValueModel;
import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.lib.wicket.util.resource.DefaultIcons;

import java.util.Optional;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.EMPTY;

public class PortletRichTextPanel extends Panel implements Loggable {

    private HiddenField<String> hiddenInput;
    private Label label;
    private WicketBuildContext ctx;
    private boolean readOnlyMode;
    private WebMarkupContainer buttonEditar;
    private WebMarkupContainer htmlContent;
    private String previewFrameUuid = UUID.randomUUID().toString();

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(OnDomReadyHeaderItem.forScript(updatePreviewScript()));
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
    }

    private String updatePreviewScript() {
        String script = "(function(){";
        script += "$('#" + htmlContent.getMarkupId(true) + "').html(\"<iframe ";
        script += " id=\\\"" + previewFrameUuid + "\\\" style=\\\"height: 100%; width: 100%;\\\" frameborder=\\\"0\\\"></iframe>\");";
        script += " var frame = $('#" + previewFrameUuid + "')[0].contentWindow";
        script += " || $('#" + previewFrameUuid + "')[0].contentDocument.document";
        script += " || $('#" + previewFrameUuid + "')[0].contentDocument;";
        script += " frame.document.open();";
        script += " frame.document.write( $(\"#" + hiddenInput.getMarkupId(true) + "\").val());";
        script += " frame.document.close();";
        script += "}());";
        return script;
    }

    private void build(WicketBuildContext ctx) {
        label = new Label("label", Model.of(Optional.ofNullable(ctx.getCurrentInstance().asAtr().getLabel()).orElse(EMPTY)));
        hiddenInput = new HiddenField<>("hiddenInput", new SInstanceValueModel<>(ctx.getModel()));
        hiddenInput.setEnabled(ctx.getViewMode() == ViewMode.EDIT);
        htmlContent = new WebMarkupContainer("htmlContent");

        add(label);
        add(hiddenInput);
        add(htmlContent);
        buttonEditar = createButtonOpenEditor();
        buttonEditar.add(configureLabelButton());
        add(buttonEditar);
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
                        isReadOnlyMode(), PortletRichTextPanel.this.ctx, hiddenInput, previewFrameUuid));
            }

            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                tag.put("onclick", "window.open('" + getURL() + "', '_blank" + previewFrameUuid + "');");
            }

        };
    }

    public boolean isReadOnlyMode() {
        return readOnlyMode || !this.isEnabledInHierarchy();
    }
}