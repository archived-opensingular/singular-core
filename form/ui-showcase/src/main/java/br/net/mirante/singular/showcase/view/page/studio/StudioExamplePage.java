package br.net.mirante.singular.showcase.view.page.studio;

import br.net.mirante.singular.showcase.view.template.Content;
import br.net.mirante.singular.showcase.view.template.Template;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("studio")
public class StudioExamplePage extends Template {

    @Override
    protected Content getContent(String id) {
        return new StudioContentExample(id);
    }
}
