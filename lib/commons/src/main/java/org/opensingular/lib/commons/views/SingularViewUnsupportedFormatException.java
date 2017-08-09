/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package org.opensingular.lib.commons.views;

import org.opensingular.lib.commons.views.format.ViewOutputHtml;

/**
 * Indiecates that the {@link ViewGenerator} doesn't supports the particular type of {@link ViewOutputHtml}.
 *
 * @author Daniel C. Bordin on 24/07/2017.
 */
public class SingularViewUnsupportedFormatException extends SingularViewException {

    public SingularViewUnsupportedFormatException(Object target, ViewOutput<java.io.Writer> view) {
        super("There is no implementation supporting the format " + getFormatName(view, target));
        if (view != null) {
            add("viewClass", view.getClass());
            ViewOutputFormat format = view.getFormat();
            if (format != null) {
                add("formatClass", format.getClass());
            }
        }
    }

    public SingularViewUnsupportedFormatException(Object target, ViewOutputFormat format) {
        super("There is no implementation supporting the format " + getFormatName(format, target));
        if (format != null) {
            add("formatClass", format.getClass());
        }
    }

    private static String getFormatName(ViewOutput<java.io.Writer> view, Object target) {
        return getFormatName(view == null ? null :view.getFormat(), target);
    }

    private static String getFormatName(ViewOutputFormat format, Object target) {
        String s = format == null ? null : format.getName();
        if (target != null) {
            s += " for the target " + target + " (class " + target.getClass().getSimpleName() + ")";
        }
        return s;
    }
}
