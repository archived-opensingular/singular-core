package org.opensingular.singular.server.core.wicket.view.home;

        import org.opensingular.singular.server.commons.wicket.view.template.Content;
        import org.opensingular.singular.server.core.wicket.template.ServerTemplate;
        import org.wicketstuff.annotation.mount.MountPath;

@MountPath("home")
public class HomePage extends ServerTemplate {


    @Override
    protected Content getContent(String id) {
        return new HomeContent(id);
    }
}
