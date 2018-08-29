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
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.opensingular.lib.commons.lambda.IFunction;

public class RenderHeadFunctionalBehavior extends Behavior {

    private final IFunction<Component, Boolean> isEnabled;
    private final IFunction<Component, CharSequence> scriptFunction;

    public RenderHeadFunctionalBehavior(IFunction<Component, CharSequence> scriptFunction, IFunction<Component, Boolean> isEnabled) {
        this.isEnabled = isEnabled;
        this.scriptFunction = scriptFunction;
    }


    public static RenderHeadFunctionalBehavior of(IFunction<Component, CharSequence> scriptFunction, IFunction<Component, Boolean> isEnabled) {
        return new RenderHeadFunctionalBehavior(scriptFunction, isEnabled);
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        response.render(OnDomReadyHeaderItem.forScript(""
                + "(function(){"
                + "'use strict';"
                + scriptFunction.apply(component)
                + "})();"));
    }

    @Override
    public boolean isEnabled(Component component) {
        return isEnabled.apply(component);
    }

}
