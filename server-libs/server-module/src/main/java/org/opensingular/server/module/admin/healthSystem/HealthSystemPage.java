package org.opensingular.server.module.admin.healthsystem;

import org.opensingular.server.commons.wicket.view.template.Content;
import org.opensingular.server.commons.wicket.view.template.Template;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("painelSaude")
public class HealthSystemPage extends Template {

    public HealthSystemPage(){
    }

	@Override
	protected Content getContent(String id) {
		return new HealthSystemContent(id);
	}
	
	@Override
	protected boolean withMenu() {
		return false;
	}
}
