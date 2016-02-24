package br.net.mirante.singular.pet.server.wicket.view.home;

        import br.net.mirante.singular.pet.module.wicket.view.template.Content;
        import br.net.mirante.singular.pet.server.wicket.template.ServerTemplate;
        import org.wicketstuff.annotation.mount.MountPath;

@MountPath("home")
public class HomePage extends ServerTemplate {


    @Override
    protected Content getContent(String id) {
        return new HomeContent(id);
    }
}
