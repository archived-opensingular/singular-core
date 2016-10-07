/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.wicket.util.util;

/**
 * Utility class for JavaScript escaping.
 * Escapes based on the JavaScript 1.5 recommendation.
 *
 * <p>Reference:
 * <a href="https://developer.mozilla.org/en-US/docs/JavaScript/Guide/Values,_variables,_and_literals#String_literals">
 * JavaScript Guide</a> on Mozilla Developer Network.
 *
 * This class was copied from spring, so we don't have a
 * dependency caused by just one method.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Rossen Stoyanchev
 * @since 1.1.1
 */
public class JavaScriptUtils {

    /**
     * Turn JavaScript special characters into escaped characters.
     *
     * @param input the input string
     * @return the string with escaped characters
     */
    public static String javaScriptEscape(String input) {
        if (input == null) {
            return input;
        }

        StringBuilder filtered = new StringBuilder(input.length());
        char prevChar = '\u0000';
        char c;
        for (int i = 0; i < input.length(); i++) {
            c = input.charAt(i);
            if (c == '"') {
                filtered.append("\\\"");
            }
            else if (c == '\'') {
                filtered.append("\\'");
            }
            else if (c == '\\') {
                filtered.append("\\\\");
            }
            else if (c == '/') {
                filtered.append("\\/");
            }
            else if (c == '\t') {
                filtered.append("\\t");
            }
            else if (c == '\n') {
                if (prevChar != '\r') {
                    filtered.append("\\n");
                }
            }
            else if (c == '\r') {
                filtered.append("\\n");
            }
            else if (c == '\f') {
                filtered.append("\\f");
            }
            else if (c == '\b') {
                filtered.append("\\b");
            }
            // No '\v' in Java, use octal value for VT ascii char
            else if (c == '\013') {
                filtered.append("\\v");
            }
            else if (c == '<') {
                filtered.append("\\u003C");
            }
            else if (c == '>') {
                filtered.append("\\u003E");
            }
            // Unicode for PS (line terminator in ECMA-262)
            else if (c == '\u2028') {
                filtered.append("\\u2028");
            }
            // Unicode for LS (line terminator in ECMA-262)
            else if (c == '\u2029') {
                filtered.append("\\u2029");
            }
            else {
                filtered.append(c);
            }
            prevChar = c;

        }
        return filtered.toString();
    }

}
