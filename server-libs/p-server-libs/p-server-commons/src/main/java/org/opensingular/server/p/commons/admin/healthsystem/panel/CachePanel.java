/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.server.p.commons.admin.healthsystem.panel;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.opensingular.server.commons.admin.AdminFacade;
import org.opensingular.server.commons.wicket.view.SingularToastrHelper;

import de.alpharogroup.wicket.js.addon.toastr.ToastrType;

@SuppressWarnings("serial")
public class CachePanel extends Panel {
	
	@Inject
    private AdminFacade adminFacade;

	public CachePanel(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		add(new AjaxButton("clearAllCaches") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				adminFacade.clearCaches();
				
				new SingularToastrHelper(this).
					addToastrMessage(ToastrType.SUCCESS, "Caches cleaned!");
			}
		});
		
	}
}
