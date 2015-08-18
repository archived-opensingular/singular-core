package br.net.mirante.singular.util.wicket.jquery;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$L;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.wicket.Component;
import org.apache.wicket.Page;

import br.net.mirante.singular.util.wicket.util.JavaScriptUtils;

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

    public static StringBuilder $(Component component, Component... moreComponents) {
        String selector = Stream.concat(Stream.of(component), Stream.of(moreComponents))
            .filter($L.notNull())
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
}
