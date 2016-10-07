/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
