package br.net.mirante.singular.showcase.view.page.peticao;

import br.net.mirante.singular.showcase.view.SingularWicketContainer;
import br.net.mirante.singular.showcase.view.template.Content;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

@SuppressWarnings("serial")
public class ListPeticaoContent extends Content
            implements SingularWicketContainer<ListPeticaoContent, Void> {

    public ListPeticaoContent(String id) {
        super(id, false, true);
    }

    /**
     * TODO documentar as seções que tem implementação obrigatório
     * para extender de content:
     *
     *  contentTitle
     *  contentSubtitle
     *  breadcrumb
     *  _Info
     *
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();
        queue(setUpInsertButton());
    }
    private MarkupContainer setUpInsertButton() {
        return new Form<>("form").add(new AjaxButton("insert") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                setResponsePage(PeticaoPage.class);
            }

        });
    }

    @Override
    protected IModel<?> getContentTitlelModel() {
        return new ResourceModel("label.content.title");
    }

    @Override
    protected IModel<?> getContentSubtitlelModel() {
        return new ResourceModel("label.content.subtitle");
    }
}
