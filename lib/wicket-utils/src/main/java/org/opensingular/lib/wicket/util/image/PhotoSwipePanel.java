package org.opensingular.lib.wicket.util.image;

import static org.opensingular.lib.wicket.util.util.Shortcuts.*;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.wicket.util.jquery.JQuery;

public class PhotoSwipePanel extends Panel {

    public PhotoSwipePanel(String id) {
        super(id);
        add($b.classAppender("pswp hidden"));
        add($b.attr("tabindex", "-1"));
        add($b.attr("role", "dialog"));
        add($b.attr("aria-hidden", "true"));
    }

    public PhotoSwipePanel(String id, IModel<Image[]> images) {
        super(id);
        add($b.classAppender("pswp"));
        add($b.attr("tabindex", "-1"));
        add($b.attr("role", "dialog"));
        add($b.attr("aria-hidden", "true"));

        add($b.onReadyScript(() -> {
            Image[] imageArray = images.getObject();
            if ((imageArray == null) || (imageArray.length == 0)) {
                return "";
            }
            return JQuery.onWindowLoad(""
                + "\n var $this = " + JQuery.$(PhotoSwipePanel.this) + ";"
                + "\n var $imgs = " + JQuery.$(imageArray) + ";"
                + "\n var img2data = function(img){ return ({ src:img.src, w:img.naturalWidth, h:img.naturalHeight }); };"
                + "\n $imgs.on('click', function(){"
                + "\n   $this.photoswipe({ 'items':$.map($imgs, img2data) });"
                + "\n });"
                + "\n");
        }));
    }

    public PhotoSwipePanel(String id, ISupplier<String> imageDataJsFunction) {
        super(id);
        add($b.classAppender("pswp"));
        add($b.attr("tabindex", "-1"));
        add($b.attr("role", "dialog"));
        add($b.attr("aria-hidden", "true"));

        add($b.onReadyScript(() -> {
            return JQuery.onWindowLoad(""
                + "\n var $this = " + JQuery.$(PhotoSwipePanel.this) + ";"
                + "\n var imgFunc = " + imageDataJsFunction + ";"
                + "\n $this.photoswipe({ 'items':imgFunc($this) });"
                + "\n");
        }));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forCSS(".pswp { z-index:9999 !important; }", getMarkupId() + "_style"));
    }
}
