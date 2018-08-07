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

package org.opensingular.lib.wicket.util.template.admin;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;

public interface SingularAdminApp {
    default MarkupContainer buildPageFooter(String id) {
        return new WebMarkupContainer(id);
    }

    default MarkupContainer buildPageHeader(String id, boolean withMenu, SingularAdminTemplate adminTemplate) {
        return new WebMarkupContainer(id);
    }

    /**
     * Page Body, deve ser um TransparentWebMarkupContainer. Por é pai de todos os filhos <wicket:child></wicket:child>
     * @param id o id do component
     * @param withMenu se deve conter menu
     * @param adminTemplate o template utilizado
     * @return o componente criado
     */
    default TransparentWebMarkupContainer buildPageBody(String id, boolean withMenu, SingularAdminTemplate adminTemplate) {
        TransparentWebMarkupContainer pageBody = new TransparentWebMarkupContainer(id);
        if (!withMenu) {
            pageBody.add($b.classAppender("page-full-width"));
        }
        return pageBody;
    }
}