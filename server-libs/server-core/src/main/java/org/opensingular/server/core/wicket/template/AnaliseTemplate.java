package org.opensingular.server.core.wicket.template;


import org.opensingular.server.commons.wicket.view.template.Menu;


public abstract class AnaliseTemplate extends ServerTemplate {

    @Override
    protected String getPageTitleLocalKey() {
        return "anl.page.title";
    }

    @Override
    protected Menu configureMenu(String id) {
        return new MenuAnalise(id);
    }


}
