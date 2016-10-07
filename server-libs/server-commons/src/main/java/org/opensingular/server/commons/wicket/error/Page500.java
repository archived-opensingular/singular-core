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

package org.opensingular.server.commons.wicket.error;

import org.opensingular.server.commons.wicket.view.template.Content;
import org.opensingular.server.commons.wicket.view.template.Template;
import org.apache.wicket.devutils.stateless.StatelessComponent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.wicketstuff.annotation.mount.MountPath;

@StatelessComponent
@MountPath("public/error/500")
public class Page500 extends Template {

    private Exception exception;

    public Page500(Exception exception) {
        this.exception = exception;
    }

    public Page500() {
    }

    @Override
    protected Content getContent(String id) {
        return new Page500Content(id, exception);
    }

    @Override
    protected WebMarkupContainer configureHeader(String id) {
        return (WebMarkupContainer) new WebMarkupContainer(id).setVisible(false);
    }

    @Override
    protected boolean withMenu() {
        return false;
    }

    @Override
    protected boolean withTopAction() {
        return false;
    }

    @Override
    protected boolean withSideBar() {
        return false;
    }

}
