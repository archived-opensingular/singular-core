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

package org.opensingular.server.core.wicket.view.home;

import org.opensingular.server.commons.wicket.view.template.Content;
import org.apache.wicket.model.IModel;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$m;

public class HomeContent extends Content {

    public HomeContent(String id) {
        super(id);
    }

    public HomeContent(String id, boolean withInfoLink, boolean withBreadcrumb) {
        super(id, withInfoLink, withBreadcrumb);
    }

    @Override
    protected IModel<?> getContentTitleModel() {
        return $m.ofValue("Página inicial");
    }

    @Override
    protected IModel<?> getContentSubtitleModel() {
        return $m.ofValue("Página inicial");
    }
}
