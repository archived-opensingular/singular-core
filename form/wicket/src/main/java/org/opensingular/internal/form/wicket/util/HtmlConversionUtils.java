package org.opensingular.internal.form.wicket.util;

import static org.apache.commons.lang3.StringUtils.*;
import static org.opensingular.lib.commons.util.HTMLUtil.*;

import java.util.function.Function;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import com.google.common.collect.ImmutableMap;

public final class HtmlConversionUtils {

    private static final ImmutableMap<String, Function<String, String>> RENDERERS = ImmutableMap.<String, Function<String, String>> builder()
        .put("html", Function.identity()) // no escaping (doesn't prevent XSS)

        .put("markdown", HtmlConversionUtils::renderMarkdown)
        .put("commonmark", HtmlConversionUtils::renderMarkdown)

        .put("puremarkdown", HtmlConversionUtils::renderPureMarkdown) // no HTML allowed
        .put("purecommonmark", HtmlConversionUtils::renderPureMarkdown) // no HTML allowed

        .put("text", HtmlConversionUtils::renderText)
        .put("plain", HtmlConversionUtils::renderText)
        .put("plaintext", HtmlConversionUtils::renderText)
        .build();

    private HtmlConversionUtils() {}

    public static String toHtmlMessage(String message) {
        return toHtmlMessage(message, null);
    }

    public static String toHtmlMessage(String message, String forcedFormat) {
        if (isEmpty(message))
            return "";

        final String format = defaultString(forcedFormat).trim().toLowerCase();

        return RENDERERS.getOrDefault(format, HtmlConversionUtils::renderText)
            .apply(message);
    }

    private static String renderText(String message) {
        return "<p>" + escapeHtml(message) + "</p>";
    }
    private static String renderPureMarkdown(String message) {
        return renderMarkdown(escapeHtml(message));
    }
    private static String renderMarkdown(String message) {
        Parser parser = Parser.builder().build();
        Node node = parser.parse(message);
        return HtmlRenderer.builder().build().render(node);
    }
}
