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

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.opensingular.server.commons.wicket.view.template.Content;

import java.util.logging.Logger;


public class Page410Content extends Content {

    private final static Logger LOGGER = Logger.getLogger("GENERAL_LOGGER");

    public Page410Content(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        pageHead.setVisible(false);
    }

    @Override
    protected IModel<?> getContentSubtitleModel() {
        return Model.of("");
    }

    @Override
    protected IModel<?> getContentTitleModel() {
        return Model.of("");
    }

}
