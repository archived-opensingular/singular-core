package org.opensingular.lib.commons.util;

import java.util.regex.Pattern;

public class HTMLUtil {

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

}