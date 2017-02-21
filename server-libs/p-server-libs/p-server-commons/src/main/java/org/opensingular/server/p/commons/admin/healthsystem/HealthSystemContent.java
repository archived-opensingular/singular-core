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

package org.opensingular.server.p.commons.admin.healthsystem;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.opensingular.server.p.commons.admin.healthsystem.panel.*;
import org.opensingular.server.commons.wicket.view.template.Content;

@SuppressWarnings("serial")
public class HealthSystemContent extends Content {
	
    private static final String CONTAINER_ALL_CONTENT = "containerAllContent";

	public HealthSystemContent(String id) {
		super(id);
		buildContent();
	}
	
	private void buildContent(){
		Form<Void> form = new Form<>("form");
		add(form);
		
		form.add(new WebMarkupContainer(CONTAINER_ALL_CONTENT));
		
		AjaxButton buttonDb = new AjaxButton("buttonDb") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				form.replace(new DbPanel(CONTAINER_ALL_CONTENT));
				target.add(form);
			}
		};
		
		AjaxButton buttonCache = new AjaxButton("buttonCache") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				form.replace(new CachePanel(CONTAINER_ALL_CONTENT));
				target.add(form);
			}
		};
		
		AjaxButton buttonJobs = new AjaxButton("buttonJobs") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				form.replace(new JobPanel(CONTAINER_ALL_CONTENT));
				target.add(form);
			}
		};
		
		AjaxButton buttonPermissions = new AjaxButton("buttonPermissions") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				form.replace(new PermissionPanel(CONTAINER_ALL_CONTENT));
				target.add(form);
			}
		};
		
		AjaxButton buttonWeb = new AjaxButton("buttonWeb") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				form.replace(new WebPanel(CONTAINER_ALL_CONTENT));
				target.add(form);
			}
		};

		form.add(buttonCache);
		form.add(buttonDb);
		form.add(buttonPermissions);
		form.add(buttonJobs);
		form.add(buttonWeb);
	}

	@Override
	protected IModel<?> getContentTitleModel() {
		return new Model<>("Health System");
	}

	@Override
	protected IModel<?> getContentSubtitleModel() {
		return new Model<>("");
	}
	
}
