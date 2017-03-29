package org.opensingular.lib.wicket.util;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;

public class SingleFormDummyPage extends WebPage {

    public static final String CONTENT_ID  = "content";
    public static final String FORM_ID     = "form";
    public static final String PARENT_PATH = FORM_ID + ":" + CONTENT_ID + ":";

    public SingleFormDummyPage() {
        add(newForm(FORM_ID)
            .add(newContentPanel(CONTENT_ID)));
    }

    private Form<Object> newForm(String formId) {
        return new Form<>(formId);
    }

    protected Component newContentPanel(String contentId) {
        return new WebMarkupContainer(contentId);
    }
}