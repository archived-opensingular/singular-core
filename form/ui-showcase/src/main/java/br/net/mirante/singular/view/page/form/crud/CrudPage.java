package br.net.mirante.singular.view.page.form.crud;

import org.wicketstuff.annotation.mount.MountPath;

import br.net.mirante.singular.view.template.Content;
import br.net.mirante.singular.view.template.Template;

@MountPath("form/crud")
public class CrudPage extends Template {

	@Override
	protected Content getContent(String id) {
		return new CrudContent(id);
	}
	
}
