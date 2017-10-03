package org.opensingular.lib.wicket.util.template.admin;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;

public interface SingularAdminApp {
    default MarkupContainer buildPageFooter(String id) {
        return new WebMarkupContainer(id);
    }

    default MarkupContainer buildPageHeader(String id, boolean withMenu, SingularAdminTemplate adminTemplate) {
        return new WebMarkupContainer(id);
    }

    /**
     * Page Body, deve ser um TransparentWebMarkupContainer. Por é pai de todos os filhos <wicket:child></wicket:child>
     * @param id o id do component
     * @param withMenu se deve conter menu
     * @param adminTemplate o template utilizado
     * @return o componente criado
     */
    default TransparentWebMarkupContainer buildPageBody(String id, boolean withMenu, SingularAdminTemplate adminTemplate) {
        TransparentWebMarkupContainer pageBody = new TransparentWebMarkupContainer(id);
        if (!withMenu) {
            pageBody.add($b.classAppender("page-full-width"));
        }
        return pageBody;
    }
}