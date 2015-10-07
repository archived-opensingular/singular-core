package br.net.mirante.singular.util.wicket.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;

public class StatelessBehaviors {

    public static final Behavior DISABLED_ATTR  = new AttributeAppender("class", "disabled", " ") {
                                                    public boolean isEnabled(Component component) {
                                                        return super.isEnabled(component)
                                                            && !component.isEnabledInHierarchy();
                                                    }
                                                };
    public static final Behavior REQUIRED_AFTER = new Behavior() {
                                                    public void afterRender(Component component) {
                                                        component.getRequestCycle().getResponse().write("<span class='required'>*</span>");
                                                    }
                                                };
}
