package org.opensingular.server.module.wicket.view.util;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$m;

import org.opensingular.server.commons.wicket.view.template.Content;
import org.opensingular.server.commons.wicket.view.template.Template;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("painelSaude")
public class PainelSaudePage extends Template {

    public PainelSaudePage(){
    }

	@Override
	protected Content getContent(String id) {
		// TODO verificar maneira correta de passar o formVersionEntityPK
		Long valor = (long) 1;
		return new PainelSaudeContent(id);
	}
}
