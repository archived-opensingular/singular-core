package br.net.mirante.singular.view.page.peticao;

import br.net.mirante.singular.view.template.Content;
import br.net.mirante.singular.view.template.Template;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("peticao/novo")
public class PeticaoPage extends Template {

    @Override
    protected Content getContent(String id) {
        return new PeticaoContent(id);
    }
}
