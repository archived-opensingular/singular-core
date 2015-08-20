package br.net.mirante.singular.view.page.dashboard;

import br.net.mirante.singular.view.template.Content;
import br.net.mirante.singular.view.template.Template;

public class DashboardPage extends Template {

    @Override
    protected Content getContent(String id) {
        return new DashboardContent(id);
    }

    @Override
    protected String getPageTitleLocalKey() {
        return "label.page.title.local";
    }
}
