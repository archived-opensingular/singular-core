package br.net.mirante.singular.view.page.form;

import org.wicketstuff.annotation.mount.MountPath;

import br.net.mirante.singular.view.template.Content;
import br.net.mirante.singular.view.template.Template;

@MountPath("form/list")
@SuppressWarnings("serial")
public class ListPage extends Template {

    protected Content getContent(String id) {
        return new ListContent(id);
    }
}