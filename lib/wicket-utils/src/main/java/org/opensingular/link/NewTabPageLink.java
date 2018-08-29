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

package org.opensingular.link;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.component.IRequestablePage;
import org.opensingular.lib.commons.lambda.ISupplier;

/**
 * A link that opens ca new tab
 */
public class NewTabPageLink extends Link<ISupplier<IRequestablePage>> {
    private final IModel<String> target = new Model<>("_blank");

    public NewTabPageLink(String id, ISupplier<IRequestablePage> pageFactory) {
        super(id, new Model<>(pageFactory));
    }

    @Override
    public void onClick() {
        throw new RestartResponseException(getModelObject().get());
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        tag.put("target", target.getObject());
    }

    /**
     * Set the _target value, default is _blank
     */
    public void setTarget(String target) {
        this.target.setObject(target);
    }
}