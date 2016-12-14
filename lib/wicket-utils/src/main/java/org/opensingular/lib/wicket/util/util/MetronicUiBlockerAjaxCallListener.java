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

package org.opensingular.lib.wicket.util.util;

import org.opensingular.lib.commons.lambda.ISupplier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.model.StringResourceModel;

@SuppressWarnings("serial")
public class MetronicUiBlockerAjaxCallListener extends AjaxCallListener {

    private final ISupplier<String> targetIdSupplier;

    /**
     * Bloqueia e desploqueia o elemento com o id informado
     * 
     * @param targetId
     */
    public MetronicUiBlockerAjaxCallListener(String targetId) {
        this(() -> targetId);
    }

    public MetronicUiBlockerAjaxCallListener(ISupplier<String> targetIdSupplier) {
        super();
        this.targetIdSupplier = targetIdSupplier;
    }

    @Override
    public CharSequence getBeforeSendHandler(Component component) {
        return String.format("App.blockUI({target:'#%s', boxed: true, message: '%s'});",
                targetIdSupplier.get(), new StringResourceModel("label.metronic.block", component, null).getObject());
    }

    @Override
    public CharSequence getCompleteHandler(Component component) {
        return String.format("App.unblockUI('#%s');", targetIdSupplier.get());
    }

}
