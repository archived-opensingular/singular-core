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

package org.opensingular.lib.wicket.util.util;

import java.util.HashMap;
import java.util.Map;

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

    private static final Map<Character, String> ESCAPED_CHARACTERS;

    static {
        ESCAPED_CHARACTERS = new HashMap<>();

    }

    private JavaScriptUtils() {}

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

            String escaped = escapeSpecialChars(c);

            if (escaped == null) {
                escaped = escapeControlChars(c, prevChar);
            }

            if (escaped == null) {
                escaped = escapeUnicodeChars(c);
            }

            if (escaped == null) {
                filtered.append(c);
            } else {
                filtered.append(escaped);
            }

            prevChar = c;

        }
        return filtered.toString();
    }

    private static String escapeSpecialChars(char c) {
        if (c == '"') {
            return "\\\"";
        }
        else if (c == '\'') {
            return "\\'";
        }
        else if (c == '\\') {
            return "\\\\";
        }
        else if (c == '/') {
            return "\\/";
        }
        else if (c == '<') {
            return "\\u003C";
        }
        else if (c == '>') {
            return "\\u003E";
        } else {
            return null;
        }
    }

    private static String escapeControlChars(char c, char prevChar) {
        if (c == '\t') {
            return "\\t";
        }
        else if (c == '\n') {
            if (prevChar != '\r') {
                return "\\n";
            }
        }
        else if (c == '\r') {
            return "\\n";
        }
        else if (c == '\f') {
            return "\\f";
        }
        else if (c == '\b') {
            return "\\b";
        }

        return null;
    }

    private static String escapeUnicodeChars(char c) {
        // No '\v' in Java, use octal value for VT ascii char
        if (c == '\013') {
            return "\\v";
        }
        // Unicode for PS (line terminator in ECMA-262)
        else if (c == '\u2028') {
            return "\\u2028";
        }
        // Unicode for LS (line terminator in ECMA-262)
        else if (c == '\u2029') {
            return "\\u2029";
        } else {
            return null;
        }
    }

}
