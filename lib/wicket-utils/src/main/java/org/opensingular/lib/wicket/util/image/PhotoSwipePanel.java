package org.opensingular.lib.wicket.util.image;

import static org.opensingular.lib.wicket.util.util.Shortcuts.*;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.panel.Panel;

public class PhotoSwipePanel extends Panel {

    private final PhotoSwipeBehavior psBehavior;

    public PhotoSwipePanel(String id, PhotoSwipeBehavior photoSwipeBehavior) {
        super(id);
        setOutputMarkupId(true);
        setOutputMarkupPlaceholderTag(true);
        add(this.psBehavior = photoSwipeBehavior);
        add($b.classAppender("pswp"));
        add($b.attr("tabindex", "-1"));
        add($b.attr("role", "dialog"));
        add($b.attr("aria-hidden", "true"));
        setVisible(false);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(OnDomReadyHeaderItem.forScript(psBehavior.getJavaScriptCallback()));
    }
}
