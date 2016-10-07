package org.opensingular.singular.form.showcase.view.page.studio;

import org.opensingular.singular.form.showcase.view.template.Template;
import org.opensingular.singular.form.showcase.component.ShowCaseType;
import org.opensingular.singular.form.showcase.view.template.Content;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("studio")
public class StudioHomePage extends Template {

    public StudioHomePage() {
        getPageParameters().add(ShowCaseType.SHOWCASE_TYPE_PARAM, ShowCaseType.STUDIO);
    }

    @Override
    protected Content getContent(String id) {
        return new StudioHomeContent(id);
    }
}
