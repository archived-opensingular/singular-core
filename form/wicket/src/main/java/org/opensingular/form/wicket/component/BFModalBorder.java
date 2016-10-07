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

package org.opensingular.form.wicket.component;

import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.model.IModel;

import org.opensingular.form.wicket.feedback.SFeedbackPanel;
import org.opensingular.lib.wicket.util.feedback.BSFeedbackPanel;
import org.opensingular.lib.wicket.util.modal.BSModalBorder;

public class BFModalBorder extends BSModalBorder {

    public BFModalBorder(String id, IModel<?> model) {
        super(id, model);
    }
    public BFModalBorder(String id) {
        super(id);
    }

    @Override
    protected BSFeedbackPanel newFeedbackPanel(String id, BSModalBorder fence, IFeedbackMessageFilter messageFilter) {
        return new SFeedbackPanel(id, fence, messageFilter);
    }
}
