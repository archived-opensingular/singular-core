package org.opensingular.server.module.admin;

import org.opensingular.server.commons.wicket.view.template.Content;
import org.opensingular.server.commons.wicket.view.template.Template;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("painelSaude")
public class PainelSaudePage extends Template {

    public PainelSaudePage(){
    }

	@Override
	protected Content getContent(String id) {
		return new PainelSaudeContent(id);
	}
	
	@Override
	protected boolean withMenu() {
		return false;
	}
}
