package br.net.mirante.singular.util.wicket.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;

public class StatelessBehaviors {

    public static final Behavior DISABLED_ATTR        = new AttributeAppender("class", "disabled", " ") {
                                                          public boolean isEnabled(Component component) {
                                                              return !component.isEnabledInHierarchy();
                                                          }
                                                      };
    public static final Behavior REQUIRED_AFTER_LABEL = new Behavior() {
                                                          public void renderHead(Component component, IHeaderResponse response) {
                                                              response.render(OnDomReadyHeaderItem
                                                                  .forScript("$('#" + component.getMarkupId() + "')"
                                                                      + ".append(\" <span class='required'>*</span>\");"));
                                                          }
                                                      };
}
