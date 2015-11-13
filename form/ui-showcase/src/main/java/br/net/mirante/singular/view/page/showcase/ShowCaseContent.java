package br.net.mirante.singular.view.page.showcase;

import javax.inject.Inject;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import br.net.mirante.singular.util.wicket.util.WicketUtils;
import br.net.mirante.singular.view.SingularWicketContainer;
import br.net.mirante.singular.view.template.Content;
import br.net.mirante.singular.wicket.UIAdminWicketFilterContext;

public class ShowCaseContent extends Content implements SingularWicketContainer<ShowCaseContent, Void> {

    @Inject
    private UIAdminWicketFilterContext uiAdminWicketFilterContext;

    public ShowCaseContent(String id) {
        super(id, false, true);
    }

    @Override
    protected WebMarkupContainer getBreadcrumbLinks(String id) {
        return (WebMarkupContainer) new Fragment(id, "breadcrumbShowCase", this)
                .add(new WebMarkupContainer("breadcrumbShowCaseLink")
                        .add(WicketUtils.$b.attr("href", uiAdminWicketFilterContext.getRelativeContext()
                                .concat("showcase/menu"))));
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
