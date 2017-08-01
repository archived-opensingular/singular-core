package org.opensingular.lib.wicket.util.template.admin;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;

public interface SingularAdminApp {
    default MarkupContainer buildPageFooter(String id) {
        return new WebMarkupContainer(id);
    }

    default MarkupContainer buildPageHeader(String id, boolean withMenu, SingularAdminTemplate adminTemplate) {
        return new WebMarkupContainer(id);
    }

    default MarkupContainer buildPageBody(String id, boolean withMenu, SingularAdminTemplate adminTemplate) {
        MarkupContainer pageBody = new WebMarkupContainer(id);
        if (!withMenu) {
            pageBody.add($b.classAppender("page-full-width"));
        }
        return pageBody;
    }
}