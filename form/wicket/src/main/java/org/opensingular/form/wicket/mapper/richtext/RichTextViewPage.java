package org.opensingular.form.wicket.mapper.richtext;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.opensingular.lib.wicket.util.template.SingularTemplate;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("richtextview")
public class RichTextViewPage extends SingularTemplate {
    private final IModel<String> title;

    public RichTextViewPage(IModel<String> title, IModel<String> content) {
        this.title = title;
        add(new Label("docTitle", title));
        add(new Label("docContent", content).setEscapeModelStrings(false));
    }

    @Override
    protected IModel<String> createPageTitleModel() {
        return new Model<>("Visualizar");
    }

    @Override
    protected IModel<String> getPageTitleModel() {
        return title;
    }
}