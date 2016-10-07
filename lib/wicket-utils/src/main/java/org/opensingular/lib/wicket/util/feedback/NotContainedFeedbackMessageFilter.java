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

package org.opensingular.lib.wicket.util.feedback;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;

@SuppressWarnings("serial")
public class NotContainedFeedbackMessageFilter implements IFeedbackMessageFilter {

    private final MarkupContainer container;

    public NotContainedFeedbackMessageFilter(MarkupContainer container) {
        this.container = container;
    }

    @Override
    public boolean accept(FeedbackMessage message) {
        Component reporter = message.getReporter();
        return (reporter == null) || (container != null && !container.contains(reporter, true));
    }
}   