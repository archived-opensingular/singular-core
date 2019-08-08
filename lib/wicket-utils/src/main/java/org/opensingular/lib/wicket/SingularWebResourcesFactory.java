package org.opensingular.lib.wicket;

import org.apache.wicket.Application;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.apache.wicket.request.resource.caching.NoOpResourceCachingStrategy;
import org.apache.wicket.resource.CssUrlReplacer;

import java.util.List;

public interface SingularWebResourcesFactory {
    String LOGO        = "logo";
    String FAVICON     = "favicon";
    String ERROR_IMAGE = "errorImage";

    List<CssHeaderItem> getStyleHeaders();

    List<JavaScriptHeaderItem> getScriptHeaders();

    CssHeaderItem newCssHeader(String path);

    JavaScriptHeaderItem newJavaScriptHeader(String path);

    ResourceReference getJQuery();

    IResource getFavicon();

    IResource getLogo();

    IResource gerErrorImage();

    default ResourceReference getFaviconResourceReference() {
        return new SharedResourceReference(FAVICON);
    }

    default ResourceReference getLogoResourceReference() {
        return new SharedResourceReference(LOGO);
    }

    default ResourceReference gerErrorImageResourceReference() {
        return new SharedResourceReference(ERROR_IMAGE);
    }

    default void setupApplication(Application application) {
        application.getSharedResources().add(LOGO, getLogo());
        application.getSharedResources().add(FAVICON, getFavicon());
        application.getSharedResources().add(ERROR_IMAGE, gerErrorImage());
        application.getJavaScriptLibrarySettings().setJQueryReference(getJQuery());
        application.getResourceSettings().setCssCompressor(new CssUrlReplacer());
        application.getResourceSettings().setCachingStrategy(new NoOpResourceCachingStrategy());
    }
}