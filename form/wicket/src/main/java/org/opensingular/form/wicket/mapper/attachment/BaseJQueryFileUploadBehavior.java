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

package org.opensingular.form.wicket.mapper.attachment;

import org.apache.wicket.Component;
import org.apache.wicket.IResourceListener;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

public abstract class BaseJQueryFileUploadBehavior<T> extends Behavior implements IResourceListener {

    private Component component;
    private IModel<T> model;

    public BaseJQueryFileUploadBehavior(IModel<T> model) {
        this.model = model;
    }

    protected T currentInstance() {
        return model.getObject();
    }

    protected StringValue getParamFileId(String fileId) {
        return params().getParameterValue(fileId);
    }

    protected IRequestParameters params() {
        WebRequest request = (WebRequest) RequestCycle.get().getRequest();
        return request.getRequestParameters();
    }

    @Override
    public void bind(Component component) {
        this.component = component;
    }

    public String getUrl() {
        return component.urlFor(this, IResourceListener.INTERFACE, new PageParameters()).toString();
    }
}