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

package org.opensingular.lib.wicket.util.bootstrap.layout;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.border.Border;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;

public class BSWellBorder extends Border {

    public final static String LARGE = "padding: 12px";
    public final static String SMALL = "padding: 6px;";

    public BSWellBorder(String id) {
        this(id, null);
    }

    public BSWellBorder(String id, String sizeClass) {
        super(id);
        addToBorder(buildWell(sizeClass));
    }

    private WebMarkupContainer buildWell(String sizeClass) {
        return new WebMarkupContainer("well") {
            @Override
            protected void onInitialize() {
                super.onInitialize();
                add($b.attrAppender("style", sizeClass, StringUtils.SPACE));
            }
        };
    }

    public static BSWellBorder large(String id) {
        return new BSWellBorder(id, LARGE);
    }

    public static BSWellBorder small(String id) {
        return new BSWellBorder(id, SMALL);
    }

}
