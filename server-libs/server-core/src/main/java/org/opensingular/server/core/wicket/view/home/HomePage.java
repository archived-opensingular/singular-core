package org.opensingular.server.core.wicket.view.home;

        import org.opensingular.server.commons.wicket.view.template.Content;
        import org.opensingular.server.core.wicket.template.ServerTemplate;
        import org.wicketstuff.annotation.mount.MountPath;

@MountPath("home")
public class HomePage extends ServerTemplate {


    @Override
    protected Content getContent(String id) {
        return new HomeContent(id);
    }
}
