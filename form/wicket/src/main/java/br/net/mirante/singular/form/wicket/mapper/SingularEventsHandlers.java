package br.net.mirante.singular.form.wicket.mapper;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.request.resource.PackageResourceReference;

import static org.apache.wicket.markup.head.JavaScriptHeaderItem.forReference;

public class SingularEventsHandlers extends Behavior {

    private final FUNCTION function;

    public SingularEventsHandlers(FUNCTION function) {
        this.function = function;
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        response.render(forReference(new PackageResourceReference(SingularEventsHandlers.class, "SingularEventsHandlers.js")));
        response.render(OnDomReadyHeaderItem.forScript(function.getScript(component)));
    }

    public enum FUNCTION {

        ADD_MOUSEDOWN_HANDLERS {
            @Override
            String getScript(Component component) {
                return "window.SEH.addMousedownHandlers("+component.getMarkupId(true)+");";
            }
        },
        ADD_TEXT_FIELD_HANDLERS {
            @Override
            String getScript(Component component) {
                return "window.SEH.addTextFieldHandlers("+component.getMarkupId(true)+");";
            }
        };

        abstract String getScript(Component component);
    }
}
