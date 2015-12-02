package br.net.mirante.singular.view.page.showcase;

import br.net.mirante.singular.util.wicket.util.WicketUtils;
import br.net.mirante.singular.view.SingularWicketContainer;
import br.net.mirante.singular.view.template.Content;
import br.net.mirante.singular.wicket.UIAdminWicketFilterContext;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import javax.inject.Inject;

import static br.net.mirante.singular.showcase.ShowCaseTable.ShowCaseItem;

public class ShowCaseContent extends Content implements SingularWicketContainer<ShowCaseContent, Void> {

    @Inject
    private UIAdminWicketFilterContext uiAdminWicketFilterContext;

    public ShowCaseContent(String id) {
        super(id, false, true);
        add(buildShowCaseForm());
    }

    public Form buildShowCaseForm() {
        Form form = new Form("showCaseForm");

        WebMarkupContainer showCaseItemDetailContainer = new WebMarkupContainer("showCaseItemDetailContainer");
        showCaseItemDetailContainer.setVisible(false);

        form.add(showCaseItemDetailContainer);
        form.add(new ShowCaseMenuPanel("showCaseMenu") {
            @Override
            public void onMenuItemClick(AjaxRequestTarget target, IModel<ShowCaseItem> m) {
                showCaseItemDetailContainer.setVisible(true);
                showCaseItemDetailContainer.addOrReplace(new ShowCaseItemDetailPanel("showCaseItemDetail", m));
                target.add(showCaseItemDetailContainer);
            }
        });

        return form;
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
