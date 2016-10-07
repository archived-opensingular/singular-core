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

package org.opensingular.lib.wicket.util.jquery;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.wicket.Component;
import org.apache.wicket.Page;

import org.opensingular.lib.wicket.util.util.WicketUtils;
import org.opensingular.lib.wicket.util.util.JavaScriptUtils;

public class JQuery {

//    public static StringBuilder $(Component component) {
//        if (component instanceof Page) {
//            return new StringBuilder("$(document)");
//        }
//        return $("#" + component.getMarkupId());
//    }

    public static StringBuilder convertEvent(Component component, String originalEvent, String newEvent) {
        return $(component).append(""
            + ".on('" + originalEvent + "', function(){"
            + " $(this).trigger('" + newEvent + "');"
            + "});");
    }

    public static CharSequence redirectEvent(
                                             Component originalComponent, String originalEvent,
                                             Component newComponent, String newEvent) {

        return on(originalComponent, originalEvent, $(newComponent) + ".trigger('" + newEvent + "');"
            + "console.log('redirecting event " + originalEvent + " to " + newEvent + "');");
    }

    public static StringBuilder $(Component... components) {
        final Component[] allComponents = components;
        final String selector = Arrays.stream(allComponents).filter(WicketUtils.$L.notNull())
            .map(it -> (it instanceof Page) ? "document" : "#" + it.getMarkupId())
            .collect(Collectors.joining(","));
        return $(selector);
    }

    public static StringBuilder $(Component component, String subSelector) {
        return $("#" + component.getMarkupId() + " " + subSelector);
    }

    public static StringBuilder $(CharSequence selector) {
        return new StringBuilder("$('").append(JavaScriptUtils.javaScriptEscape(selector.toString())).append("')");
    }

    public static StringBuilder setTimeout(long millis, CharSequence script) {
        return new StringBuilder()
            .append("setTimeout(function(){").append(script).append("},").append(millis).append(");");
    }

    public static String ready(CharSequence script) {
        return "$(function(){" + script + ";})";
    }

    public static String on(Component component, String event, CharSequence script) {
        final String scriptString = script.toString();
        final String function = (scriptString.startsWith("function"))
            ? scriptString
            : "function(e){" + scriptString + ";}";
        return $(component) + ".on('" + event + "'," + function + ");";
    }
}
