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

package org.opensingular.form.wicket.panel;

import org.opensingular.form.wicket.component.BFModalWindow;
import org.opensingular.form.wicket.modal.IOpenSingularFormModalEvent;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.modal.BSModalEventListenerBehavior;
import org.opensingular.lib.wicket.util.modal.BSModalWindow;
import org.opensingular.lib.wicket.util.modal.IOpenModalEvent;

public class SFormModalEventListenerBehavior extends BSModalEventListenerBehavior {

    public SFormModalEventListenerBehavior(BSContainer<?> modalItemsContainer) {
        super(modalItemsContainer);
    }
    @Override
    protected BSModalWindow newModalWindow(String modalId, IOpenModalEvent event) {
        return (event instanceof IOpenSingularFormModalEvent)
            ? new BFModalWindow(modalId)
            : super.newModalWindow(modalId, event);
    }
}
