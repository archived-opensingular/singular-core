package org.opensingular.form.wicket.mapper.tablelist;

import org.apache.wicket.markup.html.form.Form;
import org.opensingular.form.wicket.mapper.components.MetronicPanel;
import org.opensingular.lib.commons.lambda.IBiConsumer;
import org.opensingular.lib.commons.lambda.IFunction;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.bootstrap.layout.TemplatePanel;

public abstract class TableListPanel extends MetronicPanel {

    public TableListPanel(String id) {
        super(id);
    }

    public TableListPanel(String id, boolean withForm) {
        super(id, withForm);
    }

    @Override
    public IFunction<TemplatePanel, String> getTemplateFunction() {
        String wrapper = withForm ? "<form wicket:id='_fo'>%s</form>" : "%s";
        return (tp) -> String.format(wrapper, ""
                + "  <div class='list-table-input'>"
                + "    <div wicket:id='_hd' class='list-table-heading'></div>"
                + "    <div class='list-table-body' wicket:id='_co' >"
                + "    </div>"
                + "    <div wicket:id='_ft' class='list-table-footer'></div>"
                + "  </div>"
                + "");
    }

    public static final class TableListPanelBuilder {

        private TableListPanelBuilder() {
        }

        public static TableListPanel build(String id,
                IBiConsumer<BSContainer<?>, Form<?>> buildHeading,
                IBiConsumer<BSContainer<?>, Form<?>> buildContent,
                IBiConsumer<BSContainer<?>, Form<?>> buildFooter) {
            return build(id, true, buildHeading, buildContent, buildFooter);
        }

        public static TableListPanel build(String id,
                boolean withForm,
                IBiConsumer<BSContainer<?>, Form<?>> buildHeading,
                IBiConsumer<BSContainer<?>, Form<?>> buildContent,
                IBiConsumer<BSContainer<?>, Form<?>> buildFooter) {

            return new TableListPanel(id, withForm) {
                @Override
                protected void buildHeading(BSContainer<?> heading, Form<?> form) {
                    buildHeading.accept(heading, form);
                }

                @Override
                protected void buildFooter(BSContainer<?> footer, Form<?> form) {
                    buildFooter.accept(footer, form);
                }

                @Override
                protected void buildContent(BSContainer<?> content, Form<?> form) {
                    buildContent.accept(content, form);
                }
            };
        }

    }
}
