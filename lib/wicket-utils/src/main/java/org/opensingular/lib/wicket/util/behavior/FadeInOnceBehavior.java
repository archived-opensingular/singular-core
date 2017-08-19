package org.opensingular.lib.wicket.util.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.util.time.Duration;
import org.opensingular.lib.wicket.util.jquery.JQuery;

public class FadeInOnceBehavior extends AttributeAppender {

    private final Duration duration;

    public FadeInOnceBehavior() {
        this(Duration.milliseconds(500));
    }
    public FadeInOnceBehavior(Duration duration) {
        super("style", "display:none !important", ";");
        this.duration = duration;
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        response.render(OnDomReadyHeaderItem.forScript(
            JQuery.$(component)
                + ".fadeIn(" + duration.getMilliseconds() + ");"));
    }

    @Override
    public boolean isTemporary(Component component) {
        return true;
    }
}
