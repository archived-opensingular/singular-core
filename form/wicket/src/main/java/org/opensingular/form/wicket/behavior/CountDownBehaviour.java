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

package org.opensingular.form.wicket.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;

public class CountDownBehaviour extends Behavior {

    @Override
    public void renderHead(Component component, IHeaderResponse response) {

        String js = "";

        js += " $('#" + component.getMarkupId(true) + "').maxlength({ ";
        js += "     alwaysShow: true,";
        js += "     validate: true";
        js += " }); ";

        response.render(CssReferenceHeaderItem.forCSS(".bootstrap-maxlength { z-index : 999999 !important;}", null));
        response.render(OnDomReadyHeaderItem.forScript(js));
        super.renderHead(component, response);
    }

}
