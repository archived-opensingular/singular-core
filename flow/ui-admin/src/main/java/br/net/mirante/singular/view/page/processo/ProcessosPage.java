package br.net.mirante.singular.view.page.processo;

import br.net.mirante.singular.view.template.Content;
import br.net.mirante.singular.view.template.Template;

public class ProcessosPage extends Template {

    @Override
    protected Content getContent(String id) {
        return new ProcessosContent(id, withSideBar());
    }

    @Override
    protected String getPageTitleLocalKey() {
        return "label.page.title.local";
    }

    @Override
    protected boolean withSideBar() {
        return false;
    }
}
