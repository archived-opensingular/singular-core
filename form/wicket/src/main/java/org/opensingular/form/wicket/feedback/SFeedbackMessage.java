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

package org.opensingular.form.wicket.feedback;

import org.opensingular.form.SInstance;
import org.apache.wicket.Component;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

public class SFeedbackMessage extends FeedbackMessage {

    private final IModel<? extends SInstance> instanceModel;

    public SFeedbackMessage(
        Component reporter,
        Serializable message,
        int level,
        IModel<? extends SInstance> instanceModel) {
        super(reporter, message, level);
        this.instanceModel = instanceModel;
    }

    public IModel<? extends SInstance> getInstanceModel() {
        return instanceModel;
    }
    public SInstance getInstance() {
        IModel<? extends SInstance> model = getInstanceModel();
        return (getInstanceModel() == null) ? null : model.getObject();
    }
}
