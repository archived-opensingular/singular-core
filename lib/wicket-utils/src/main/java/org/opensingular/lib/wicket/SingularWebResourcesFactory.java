package org.opensingular.lib.wicket;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;

import java.util.List;

public interface SingularWebResourcesFactory {
    List<CssHeaderItem> getStyleHeaders();

    List<JavaScriptHeaderItem> getScriptHeaders();

    CssHeaderItem newCssHeader(String path);

    JavaScriptHeaderItem newJavaScriptHeader(String path);

    IResource getFavicon();

    ResourceReference getJQuery();

    IResource getLogo();
}