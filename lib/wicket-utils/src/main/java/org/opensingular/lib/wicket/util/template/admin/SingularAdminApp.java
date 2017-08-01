package org.opensingular.lib.wicket.util.template.admin;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;

public interface SingularAdminApp {
    default MarkupContainer buildPageFooter(String id){
        return new WebMarkupContainer(id);
    }

    default MarkupContainer buildPageHeader(String id){
        return new WebMarkupContainer(id);
    }

    default MarkupContainer buildPageBody(String id){
        return new WebMarkupContainer(id);
    }
}