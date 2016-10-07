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

package org.opensingular.singular.form.showcase.view.page.form.crud;

import org.opensingular.singular.form.showcase.view.template.Content;
import org.opensingular.singular.form.showcase.view.template.Template;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.util.string.StringValue;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("form/edit")
@SuppressWarnings("serial")
public class FormPage extends Template {

    protected static final String TYPE_NAME  = "type";
    protected static final String MODEL_ID   = "id";
    protected static final String VIEW_MODE  = "viewMode";
    protected static final String ANNOTATION = "annotation";

    @Override
    protected Content getContent(String id) {
        StringValue type = getPageParameters().get(TYPE_NAME);
        StringValue idExampleData = getPageParameters().get(MODEL_ID);
        StringValue viewMode = getPageParameters().get(VIEW_MODE);
        StringValue annotation = getPageParameters().get(ANNOTATION);

        return new FormContent(id, type, idExampleData, viewMode, annotation);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(OnDomReadyHeaderItem.forScript("$('#_menuItemDemo').addClass('active');"));
    }

}
