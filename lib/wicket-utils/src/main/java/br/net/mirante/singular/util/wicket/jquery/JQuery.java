package br.net.mirante.singular.util.wicket.jquery;

import br.net.mirante.singular.util.wicket.util.JavaScriptUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.wicket.Component;
import org.apache.wicket.Page;

import java.util.Arrays;
import java.util.stream.Collectors;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$L;

public class JQuery {

    public static StringBuilder $(Component component) {
        if (component instanceof Page) {
            return new StringBuilder("$(document)");
        }
        return $("#" + component.getMarkupId());
    }

    public static StringBuilder convertEvent(Component component, String originalEvent, String newEvent) {
        return $(component).append(""
                + ".on('" + originalEvent + "', function(){"
                + " $(this).trigger('" + newEvent + "');"
                + "});");
    }

    public static CharSequence redirectEvent(
            Component originalComponent, String originalEvent,
            Component newComponent, String newEvent) {

        return on(originalComponent, originalEvent, $(newComponent) + ".trigger('" + newEvent + "');");
    }

    public static StringBuilder $(Component component, Component... moreComponents) {
        final Component[] allComponents = ArrayUtils.add(moreComponents, component);
        final String selector = Arrays.stream(allComponents).filter($L.notNull())
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
