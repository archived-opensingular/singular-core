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

package org.opensingular.lib.wicket.util.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

public class ConditionalAttributeAppender extends AttributeAppender {
    private IModel<Boolean> enabled;

    public ConditionalAttributeAppender(String attribute, IModel<?> replaceModel) {
        super(attribute, replaceModel);
    }

    public ConditionalAttributeAppender(String attribute, Serializable value) {
        super(attribute, value);
    }

    public ConditionalAttributeAppender(String attribute, Serializable value, String separator) {
        super(attribute, value, separator);
    }

    public ConditionalAttributeAppender(String attribute, IModel<?> appendModel, String separator) {
        super(attribute, appendModel, separator);
    }

    @Override
    public boolean isEnabled(Component component) {
        return enabled.getObject();
    }

    public void setEnabled(IModel<Boolean> enabled) {
        this.enabled = enabled;
    }
}