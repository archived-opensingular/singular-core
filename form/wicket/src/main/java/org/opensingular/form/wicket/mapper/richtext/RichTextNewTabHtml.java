package org.opensingular.form.wicket.mapper.richtext;

import org.opensingular.form.wicket.util.ClasspathHtmlLoader;

public class RichTextNewTabHtml {

    private static final String BASE_URL_PLACEHOLDER = "#BASE_URL_PLACEHOLDER#";

    private ClasspathHtmlLoader classpathHtmlLoader = new ClasspathHtmlLoader("PortletRichTextNewTab.html", this.getClass());
    private String              loadedHtml          = null;
    private String baseurl;

    public RichTextNewTabHtml(String baseurl) {
        this.baseurl = baseurl;
    }

    public String retrieveHtml() {
        if (loadedHtml == null) {
            loadedHtml = classpathHtmlLoader.loadHtml();
        }
        return loadedHtml.replace(BASE_URL_PLACEHOLDER, baseurl);
    }

}
