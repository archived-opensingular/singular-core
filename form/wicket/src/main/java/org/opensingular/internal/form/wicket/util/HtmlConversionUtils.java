package org.opensingular.internal.form.wicket.util;

import static org.apache.commons.lang3.StringUtils.*;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.web.util.HtmlUtils;

public class HtmlConversionUtils {

    public static String toHtmlMessage(String message) {
        return toHtmlMessage(message, null);
    }
    public static String toHtmlMessage(String message, String forcedFormat) {
        switch (defaultIfBlank(forcedFormat, "").toLowerCase()) {

            case "markdown":
            case "commonmark":
                Parser parser = Parser.builder().build();
                Node node = parser.parse(message);
                return HtmlRenderer.builder().build().render(node);

            case "text":
            case "plaintext":
            case "plain text":
                return "<p>" + HtmlUtils.htmlUnescape(message) + "</p>";

            case "html":
                return message;

            default:
                return toHtmlMessage(message, resolveMessageFormat(message));
        }
    }

    public static String resolveMessageFormat(String msg) {
        String s = msg.trim();
        if (s.startsWith("<") || s.contains("</") || s.contains("/>"))
            return "html";
        if (s.contains("**") || s.contains("##") || s.contains("]("))
            return "markdown";
        return "plaintext";
    }

}
