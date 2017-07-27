package org.opensingular.internal.form.wicket.util;

import static org.apache.commons.lang3.StringUtils.*;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.opensingular.lib.commons.util.HTMLUtil;

public class HtmlConversionUtils {

    public static String toHtmlMessage(String message) {
        return toHtmlMessage(message, null);
    }
    public static String toHtmlMessage(String message, String forcedFormat) {
        if (isEmpty(message))
            return "";

        switch (defaultIfBlank(forcedFormat, "").toLowerCase()) {

            case "html": // no escaping (doesn't prevent XSS)
                return message;

            case "markdown":
            case "commonmark": { // embedded HTML allowed
                Parser parser = Parser.builder().build();
                Node node = parser.parse(message);
                return HtmlRenderer.builder().build().render(node);
            }

            case "puremarkdown":
            case "purecommonmark": { // no HTML allowed
                Parser parser = Parser.builder().build();
                Node node = parser.parse(HTMLUtil.escapeHtml(message));
                return HtmlRenderer.builder().build().render(node);
            }

            case "text":
            case "plaintext":
            case "plain text":
            default:
                return "<p>" + HTMLUtil.escapeHtml(message) + "</p>";
        }
    }
}
