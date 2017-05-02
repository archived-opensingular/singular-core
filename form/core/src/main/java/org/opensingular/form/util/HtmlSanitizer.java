package org.opensingular.form.util;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

public class HtmlSanitizer {

    private static HtmlPolicyBuilder policyBuilder;
    private static PolicyFactory htmlPolicy;

    static {
        policyBuilder = new HtmlPolicyBuilder();
        addBasicTags();
        addLocalLinks();
        addStyle();
        addTable();
        PolicyFactory customPolicy = policyBuilder.toFactory();

        htmlPolicy = Sanitizers.FORMATTING
                .and(Sanitizers.LINKS)
                .and(Sanitizers.TABLES)
                .and(Sanitizers.BLOCKS)
                .and(Sanitizers.IMAGES)
                .and(Sanitizers.STYLES)
                .and(customPolicy);
    }

    public static String sanitize(String value) {
        return htmlPolicy.sanitize(value);
    }

    private static void addStyle() {
        policyBuilder.allowElements("style").allowTextIn("style");
    }

    private static void addTable() {
        policyBuilder.allowElements("table")
                .allowAttributes("border", "cellpadding", "cellspacing", "width", "style", "class")
                .onElements("table");
        policyBuilder.allowElements("td")
                .allowAttributes("colspan", "rowspan", "style", "class", "align", "valign")
                .onElements("td");
    }

    private static void addBasicTags() {
         policyBuilder.allowElements("hr")
                .allowAttributes("size").onElements("hr");
    }

    private static void addLocalLinks() {
        policyBuilder.allowElements("a")
                .allowAttributes("href").onElements("a")
                .allowAttributes("target").onElements("a")
                .disallowUrlProtocols("http", "https");
    }

}
