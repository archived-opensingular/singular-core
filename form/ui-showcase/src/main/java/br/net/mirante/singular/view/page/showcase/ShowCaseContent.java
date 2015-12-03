package br.net.mirante.singular.view.page.showcase;

import br.net.mirante.singular.showcase.ShowCaseTable;
import br.net.mirante.singular.util.wicket.util.WicketUtils;
import br.net.mirante.singular.view.SingularWicketContainer;
import br.net.mirante.singular.view.template.Content;
import br.net.mirante.singular.wicket.UIAdminWicketFilterContext;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import javax.inject.Inject;

import static br.net.mirante.singular.showcase.ShowCaseTable.ShowCaseItem;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

public class ShowCaseContent extends Content implements SingularWicketContainer<ShowCaseContent, Void> {

    @Inject
    private UIAdminWicketFilterContext uiAdminWicketFilterContext;

    public ShowCaseContent(String id, Integer showCaseComponentNameHash) {
        super(id, false, false);
        ShowCaseItem showCaseItem = new ShowCaseTable()
                .findCaseItemByComponentNameHash(showCaseComponentNameHash);
        add(new ShowCaseItemDetailPanel("itemDetail", $m.ofValue(showCaseItem)));
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
