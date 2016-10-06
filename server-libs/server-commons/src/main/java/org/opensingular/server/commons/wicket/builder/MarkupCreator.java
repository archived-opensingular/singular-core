package org.opensingular.server.commons.wicket.builder;

import java.util.Arrays;
import java.util.Optional;

public class MarkupCreator {

    public static String div(String wicketID) {
        return div(wicketID, null);
    }

    public static String p(String wicketID) {
        return p(wicketID, null);
    }

    public static String hr(String wicketID) {
        return hr(wicketID, null);
    }

    public static String span(String wicketID) {
        return span(wicketID, null);
    }

    public static String h5(String wicketID) {
        return h5(wicketID, null);
    }

    public static String h5(String wicketID, HTMLParameters parameters, String... nesteds) {
        return newTag("h5", wicketID, parameters, nesteds);
    }

    public static String hr(String wicketID, HTMLParameters parameters, String... nesteds) {
        return newTag("hr", wicketID, parameters, nesteds);
    }

    public static String button(String wicketID, HTMLParameters parameters, String... nesteds) {
        return newTag("button", wicketID, parameters, nesteds);
    }

    public static String span(String wicketID, HTMLParameters parameters, String... nesteds) {
        return newTag("span", wicketID, parameters, nesteds);
    }

    public static String div(String wicketID, HTMLParameters parameters, String... nesteds) {
        return newTag("div", wicketID, parameters, nesteds);
    }

    public static String p(String wicketID, HTMLParameters parameters, String... nesteds) {
        return newTag("p", wicketID, parameters, nesteds);
    }

    private static String newTag(String tag, String wicketID, HTMLParameters parameters, String... nesteds) {

        final StringBuilder builder = new StringBuilder();

        builder.append("<").append(tag).append(" wicket:id='").append(wicketID).append("' ");
        Optional.ofNullable(parameters)
                .map(HTMLParameters::getParametersMap)
                .ifPresent(p -> p.forEach((k, v) -> builder.append(k).append("='").append(v).append("' ")));
        builder.append(">");
        if (nesteds != null) {
            Arrays.stream(nesteds).forEach(builder::append);
        }
        builder.append("</").append(tag).append(">");

        return builder.toString();
    }

}
