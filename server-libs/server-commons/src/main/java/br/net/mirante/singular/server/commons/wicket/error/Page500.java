package br.net.mirante.singular.server.commons.wicket.error;

import br.net.mirante.singular.server.commons.wicket.view.template.Content;
import br.net.mirante.singular.server.commons.wicket.view.template.Template;
import org.apache.wicket.devutils.stateless.StatelessComponent;
import org.wicketstuff.annotation.mount.MountPath;

/**
 * Created by nuk on 01/07/16.
 */
@StatelessComponent
@MountPath("error/500")
public class Page500 extends Template {

    private Exception exception;

    public Page500(Exception exception) {

        this.exception = exception;
    }

    @Override
    protected Content getContent(String id) {
        return new Page500Content(id, exception);
    }
}
