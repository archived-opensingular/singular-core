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

package org.opensingular.server.module.admin;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.context.SFormConfig;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.wicket.component.SingularSaveButton;
import org.opensingular.form.wicket.component.SingularValidationButton;
import org.opensingular.form.wicket.panel.SingularFormPanel;
import org.opensingular.server.commons.wicket.view.template.Content;

@SuppressWarnings("serial")
public class PainelSaudeContent extends Content {
	
    @Inject
    @Named("formConfigWithDatabase")
    private SFormConfig<String> formConfig;
    
    @Inject
    private PainelSaudeService painelService;

	public PainelSaudeContent(String id) {
		super(id);
		buildContent();
	}
	
	private void buildContent(){
		Form<Void> form = new Form<>("form");
		add(form);
		
		WebMarkupContainer containerBD = createContainerBD();
		form.add(containerBD);
//		containerBD.setVisible(false);
		
		Button buttonBD = new Button("buttonDB"){
			@Override
			public void onSubmit() {
//				containerBD.setVisible(true);
			}
		};
		buttonBD.setDefaultFormProcessing(false);
		
		Button buttonRede = new Button("buttonRede");
		Button buttonWS = new Button("buttonWS");
		Button buttonCaches = new Button("buttonCaches");
		
		form.add(buttonBD);
		form.add(buttonRede);
		form.add(buttonWS);
		form.add(buttonCaches);
	}

	private WebMarkupContainer createContainerBD() {
		WebMarkupContainer containerDB = new WebMarkupContainer("containerDB");
		SingularFormPanel<String> panelBD = new SingularFormPanel<String>("panelDB", formConfig) {
			@Override
			protected SInstance createInstance(SFormConfig<String> singularFormConfig) {
				SInstance createInstance = SDocumentFactory.empty().createInstance(new RefType() {
					@Override
					protected SType<?> retrieve() {
						return singularFormConfig.getTypeLoader().loadTypeOrException(SDbHealth.TYPE_FULL_NAME);
					}
				});
				HealthInfo infoHealthTest = painelService.getAllDbMetaData();
				try {
					TransformPojoUtil.pojoToSInstance(infoHealthTest, createInstance, false);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return createInstance; 
			}
		};
		
		SingularValidationButton checkButton = new SingularValidationButton("checkButtonDB", panelBD.getRootInstance()){
			@Override
			protected void onValidationSuccess(AjaxRequestTarget target, Form<?> form,
					IModel<? extends SInstance> instanceModel) {
				// TODO Auto-generated method stub
			}
		};
		SingularSaveButton saveButton = new SingularSaveButton("saveButtonDB", panelBD.getRootInstance()){
			@Override
			protected void onValidationSuccess(AjaxRequestTarget target, Form<?> form,
					IModel<? extends SInstance> instanceModel) {
				// TODO Auto-generated method stub
			}
		};
		containerDB.add(panelBD);
		containerDB.add(checkButton);
		containerDB.add(saveButton);
		
		return containerDB;
	}

	@Override
	protected IModel<?> getContentTitleModel() {
		return new Model<>("Painel Saude");
	}

	@Override
	protected IModel<?> getContentSubtitleModel() {
		return new Model<>("");
	}
	
}
