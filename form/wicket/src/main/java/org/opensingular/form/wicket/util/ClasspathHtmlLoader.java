package org.opensingular.form.wicket.util;

import org.apache.commons.io.IOUtils;
import org.opensingular.form.SingularFormException;
import org.opensingular.lib.wicket.util.util.JavaScriptUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ClasspathHtmlLoader {

    private String   name;
    private Class<?> scope;

    public ClasspathHtmlLoader(String name, Class<?> scope) {
        this.name = name;
        this.scope = scope;
    }

    public String loadHtml() {
        InputStream htmlInputStream = scope.getResourceAsStream(name);
        if (htmlInputStream != null) {
            return javascriptEscape(htmlInputStream);
        }
        return null;
    }

    private String javascriptEscape(InputStream htmlInputStream) {
        try {
            return JavaScriptUtils.javaScriptEscape(IOUtils.toString(htmlInputStream, StandardCharsets.UTF_8.name()));
        } catch (IOException e) {
            throw new SingularFormException("Não foi possivel extrair o conteudo html", e);
        }
    }

}