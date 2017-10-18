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

package org.opensingular.form.wicket.test.base;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;

import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;

public abstract class TestPanel extends Panel {

    private Component container;
    private BSContainer bodyContainer;

    public TestPanel(String id) {
        super(id);
        container = buildContainer("container");
        bodyContainer = buildBodyContainer("body-container");
        bodyContainer.setOutputMarkupId(true);
        add(container);
        add(bodyContainer);
    }

    public abstract Component buildContainer(String id);


    public BSContainer buildBodyContainer(String id) {
        return new BSContainer(id);
    }

    public Component getContainer() {
        return container;
    }

    public BSContainer getBodyContainer() {
        return bodyContainer;
    }
}
