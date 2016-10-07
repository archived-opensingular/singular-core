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

package org.opensingular.server.commons.flow;

import org.opensingular.flow.core.ITaskPageStrategy;
import org.opensingular.flow.core.MUser;
import org.opensingular.flow.core.TaskInstance;
import org.apache.wicket.markup.html.WebPage;

public class SingularServerTaskPageStrategy implements ITaskPageStrategy {

    private SingularWebRef webRef;

    public SingularServerTaskPageStrategy(Class<? extends WebPage> page) {
        this.webRef = new SingularWebRef(page);
    }

    public SingularServerTaskPageStrategy() {

    }

    public static final SingularServerTaskPageStrategy of(Class<? extends WebPage> page) {
        return new SingularServerTaskPageStrategy(page);
    }

    @Override
    public SingularWebRef getPageFor(TaskInstance taskInstance, MUser user) {
        return webRef;
    }

}
