package br.net.mirante.singular.view.page.form;

import org.wicketstuff.annotation.mount.MountPath;

import br.net.mirante.singular.view.template.Content;
import br.net.mirante.singular.view.template.Template;

@MountPath("form")
public class FormPage extends Template {

    @Override
    protected Content getContent(String id) {
        return new FormContent(id, withSideBar());
    }

    @Override
    protected boolean withMenu() {
        return false;
    }
}
