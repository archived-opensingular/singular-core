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

package org.opensingular.lib.commons.util;

import java.util.regex.Pattern;

public class HTMLUtil {

    private HTMLUtil() {}

    /**
     * <p>Identifica se o conteudo passo é html</p>
     * pattern: &lt;([\d[a-zA-Z]]+).*?>[\s\S]*&lt;/\s*?\1>
     * <ul>
     * <li>&lt;([\d[a-zA-Z]]+).*?>  inicio de uma tag, ex: &lt;div id='um'&gt;</li>
     * <li>[\s\S]*  qualquer filho dentro de uma tag</li>
     * <li>&lt;/\s*?\1>  tenta fazer match com o grupo encontrado no inicio ([\d[a-zA-Z]]+)</li>
     * </ul>
     *
     * @param content conteudo
     * @return se é html
     */
    public static boolean isHTML(String content) {
        return Pattern.compile("<([\\d[a-zA-Z]]+).*?>[\\s\\S]*</\\s*?\\1>").matcher(content).lookingAt();
    }

    public static String escapeHtml(String value) {
        return (value == null) ? null : value.replaceAll("<", "&lt;");
    }
}