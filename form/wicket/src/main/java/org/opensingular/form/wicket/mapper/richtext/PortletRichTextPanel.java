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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.model.SInstanceValueModel;
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

    private List<BtnRichText> btnRichTextList = new ArrayList<>();

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        try (PackageTextTemplate packageTextTemplate = new PackageTextTemplate(getClass(), "PortletRichTextPanel.js")) {
            final Map<String, String> params = new HashMap<>();


            //TODO isso terá que ser alterado pois pode ter um id NULL.
            gerarMassa("ExtraButtons");
            gerarMassa("ExtraButtons2");
            gerarMassa("ExtraButtons3");
            gerarMassa("ExtraButtons4");
            params.put("buttonsList", btnRichTextList.toString().replaceAll("\\[", "").replaceAll("]", ""));
            String listaIds = btnRichTextList.parallelStream()
                    .map(BtnRichText::getId)
                    .collect(StringBuilder::new, (a, b) -> a.append(b).append(","), StringBuilder::append).toString();
            params.put("btnList", listaIds);

            params.put("label", (String) label.getDefaultModel().getObject());
            params.put("htmlContainer", htmlContent.getMarkupId());
            params.put("hiddenInput", hiddenInput.getMarkupId());
            params.put("hash", hash);
            params.put("html", richTextNewTabHtml(listaIds).retrieveHtml());
            params.put("isEnabled", String.valueOf(visibleMode));
            packageTextTemplate.interpolate(params);
            response.render(JavaScriptHeaderItem.forScript(packageTextTemplate.getString(), hash));
        } catch (IOException e) {
            getLogger().error(e.getMessage(), e);
        }
    }

    public RichTextNewTabHtml richTextNewTabHtml(String listaIds) {
        return new RichTextNewTabHtml(RequestCycle.get().getRequest().getFilterPath(), listaIds);
    }

    public PortletRichTextPanel(String id, WicketBuildContext ctx) {
        super(id);
        this.ctx = ctx;
        hash = RandomStringUtils.random(10, true, false);

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
        Button buttonEditar = createButtonOpenEditor();
        buttonEditar.add(configureLabelButton());
        add(buttonEditar);

        htmlContent.setEscapeModelStrings(false);
    }

    private Button createButtonOpenEditor() {
        return new Button("button") {
            @Override
            protected String getOnClickScript() {
                return "openNewTabWithCKEditor" + hash + "();";
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

    private void gerarMassa(String id) {
        addButton(new BtnRichText(id, id, id, id) {
            @Override
            public void getAction(CkEditorContext editorContext) {
                editorContext.getValue();
            };
        });
    }

}