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

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.filter.HeaderResponseContainer;
import org.apache.wicket.markup.head.filter.JavaScriptFilteredIntoFooterHeaderResponse;
import org.apache.wicket.markup.html.IHeaderResponseDecorator;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.opensingular.form.view.richtext.BtnRichText;
import org.opensingular.form.wicket.component.BFModalWindow;
import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.lib.wicket.util.template.SingularTemplate;
import org.springframework.util.CollectionUtils;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("/RichTextNewTabPage")
public class RichTextNewTabPage extends WebPage implements Loggable {

    public static final IHeaderResponseDecorator JAVASCRIPT_DECORATOR = (response) -> new JavaScriptFilteredIntoFooterHeaderResponse(response, SingularTemplate.JAVASCRIPT_CONTAINER);
    public static final String JAVASCRIPT_CONTAINER = "javascript-container";
    private String hiddenInput;
    private String htmlContainer;
    private IModel<String> model = new Model<>("TESTANDO");

    private boolean visibleMode;
    private List<BtnRichText> btnRichTextList;

    public RichTextNewTabPage(String title, boolean visibleMode, List<BtnRichText> btnRichTextList,
            String hiddenInput, String htmlContainer) {
        this.visibleMode = visibleMode;
        this.btnRichTextList = btnRichTextList;
        this.hiddenInput = hiddenInput;
        this.htmlContainer = htmlContainer;
        add(new Label("title", Model.of(title)));

        //IMPORTANTE -> TODO O ID DEVE COMECAR COM extra

    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        try (PackageTextTemplate packageTextTemplate = new PackageTextTemplate(getClass(), "PortletRichTextPanel.js")) {
            final Map<String, String> params = new HashMap<>();

            params.put("htmlContainer", this.htmlContainer);
            params.put("hiddenInput", this.hiddenInput);
            params.put("hash", "");
            params.put("isEnabled", String.valueOf(visibleMode));
            if(CollectionUtils.isEmpty(btnRichTextList)){
                params.put("buttonsList", "");
            } else {
                params.put("buttonsList", btnRichTextList.toString().replaceAll("\\[", "").replaceAll("]", ""));
            }
            packageTextTemplate.interpolate(params);
            response.render(JavaScriptHeaderItem.forScript(packageTextTemplate.getString(), this.getId()));
        } catch (IOException e) {
            getLogger().error(e.getMessage(), e);
        }
        response.render(JavaScriptHeaderItem.forUrl("https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"));
        response.render(JavaScriptHeaderItem.forUrl("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Form form = new Form("form");
        form.add(criarTextArea());

        BFModalWindow bfModalWindow = new BFModalWindow("modalCkEditor");
        bfModalWindow.setTitleText(Model.of("teste"));
//        modalBorder.addButton(BSModalBorder.ButtonStyle.BLUE, Model.of(getString("label.filter")), new AjaxButton("click"){
//            @Override
//            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
//                super.onSubmit(target, form);
//            }
//        });
        bfModalWindow.setVisible(true);
        form.add(bfModalWindow);
        add(form);
        getApplication().setHeaderResponseDecorator(JAVASCRIPT_DECORATOR);
        add(new HeaderResponseContainer(JAVASCRIPT_CONTAINER, JAVASCRIPT_CONTAINER));

    }

    private TextArea<String> criarTextArea() {
        return new TextArea<>("conteudo", model);
    }


}
