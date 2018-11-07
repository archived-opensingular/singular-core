package org.opensingular.lib.wicket.util.image;

import static org.opensingular.lib.wicket.util.util.Shortcuts.*;

import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.wicket.util.image.PhotoSwipeBehavior.CoreOptions;
import org.opensingular.lib.wicket.util.image.PhotoSwipeBehavior.DefaultUIOptions;
import org.opensingular.lib.wicket.util.jquery.JQuery;

public class PhotoSwipePanel extends Panel {

    private final PhotoSwipeBehavior          photoSwipeBehavior;
    private final AbstractDefaultAjaxBehavior closeBehavior = new AbstractDefaultAjaxBehavior() {
                                                                @Override
                                                                protected void respond(AjaxRequestTarget target) {
                                                                    hide(target);
                                                                }
                                                            };

    public PhotoSwipePanel(String id) {
        this(id, PhotoSwipeBehavior.forURLs(Model.of()));
    }
    public PhotoSwipePanel(String id, IModel<String[]> urlsModel) {
        this(id, PhotoSwipeBehavior.forURLs(urlsModel));
    }
    public PhotoSwipePanel(String id, PhotoSwipeBehavior photoSwipeBehavior) {
        super(id);

        setOutputMarkupId(true);
        setOutputMarkupPlaceholderTag(true);
        setVisible(false);

        add(this.photoSwipeBehavior = photoSwipeBehavior);
        add($b.classAppender("pswp"));
        add($b.attr("tabindex", "-1"));
        add($b.attr("role", "dialog"));
        add($b.attr("aria-hidden", "true"));

        add(closeBehavior);
    }

    public PhotoSwipeBehavior getPhotoSwipeBehavior() {
        return photoSwipeBehavior;
    }

    public PhotoSwipePanel setImageDataFromURLs(IModel<String[]> urls) {
        photoSwipeBehavior.setImageDataFromURLs(urls);
        return this;
    }
    public PhotoSwipePanel setImageDataFromImages(IModel<Image[]> images) {
        photoSwipeBehavior.setImageDataFromImages(images);
        return this;
    }
    public PhotoSwipePanel setImageDataJsFunction(ISupplier<String> imageDataJsFunction) {
        photoSwipeBehavior.setImageDataJsFunction(imageDataJsFunction);
        return this;
    }
    public PhotoSwipePanel setCoreOptions(CoreOptions coreOptions) {
        photoSwipeBehavior.setCoreOptions(coreOptions);
        return this;
    }
    public PhotoSwipePanel setDefaultUIOptions(DefaultUIOptions defaultUIOptions) {
        photoSwipeBehavior.setDefaultUIOptions(defaultUIOptions);
        return this;
    }

    public void show(AjaxRequestTarget target) {
        target.add(this.setVisible(true));
    }
    public void hide(AjaxRequestTarget target) {
        target.add(this.setVisible(false));
        target.prependJavaScript(JQuery.$(this) + ".photoswipe('close');");
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(OnDomReadyHeaderItem.forScript(photoSwipeBehavior.getJavaScriptCallback()));
        response.render(OnDomReadyHeaderItem.forScript(JQuery.$(this) + ".on('photoswipe:close', " + closeBehavior.getCallbackFunction() + ");"));
    }
}
