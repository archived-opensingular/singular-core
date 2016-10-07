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

package org.opensingular.lib.wicket.util.resource;

public enum Color {
    DEFAULT /*          */(""),
    BLUE_HOKI /*        */("blue-hoki"),
    BLUE_SHARP /*       */("blue-sharp"),
    GREEN_SHARP /*      */("green-sharp"),
    GREY_GALLERY /*     */("grey-gallery"),
    PURPLE_PLUM /*      */("purple-plum"),
    PURPLE_SOFT /*      */("purple-soft"),
    RED_HAZE /*         */("red-haze"),
    RED_SUNGLO /*       */("red-sunglo"),
    YELLOW_CASABLANCA /**/("yellow-casablanca"),
    YELLOW_CRUSTA /*    */("yellow-crusta");

    private final String cssClass;

    Color(String cssClass) {
        this.cssClass = cssClass;
    }

    public String getCssClass() {
        return cssClass;
    }

    public String getFontCssClass() {
        return (this.equals(DEFAULT) ? "theme-font-color": "font-".concat(cssClass));
    }

    @Override
    public String toString() {
        return getCssClass();
    }
}
