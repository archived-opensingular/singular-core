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

package org.opensingular.server.module.wicket.view.util;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.opensingular.form.SInstance;
import org.opensingular.form.context.SFormConfig;
import org.opensingular.form.service.IFormService;
import org.opensingular.form.wicket.panel.SingularFormPanel;
import org.opensingular.server.commons.wicket.view.template.Content;

public class PainelSaudeContent extends Content {
	
	private IModel<Long> formVersionEntityPK;
	
	@Inject
    private IFormService formService;

    @Inject
    @Named("formConfigWithDatabase")
    private SFormConfig<TableInfo> formConfig;
    
    @Inject
    private PainelSaudeService painelService;

	public PainelSaudeContent(String id) {
		super(id);
		// TODO Auto-generated constructor stub
	}
	
private void buildContent(){
		
		
		painelService.getAllDbMetaData();
//		Application application = 
//		getSession().getApplication();
		
//		Connection connection = ((SessionImpl) sessao).connection();
		Form<Void> form = new Form<>("form");
		
		
//		final FormVersionEntity formVersionEntity 
////		= formService.loadFormVersionEntity(formVersionEntityPK.getObject());
//		
//		= new FormVersionEntity();
//		
//        final FormKey           formKey           = formService.keyFromObject(formVersionEntity.getFormEntity().getCod());
//		
//        final RefType refType = new RefType() {
//            @Override
//            protected SType<?> retrieve() {
//                return formConfig.getTypeLoader().loadTypeOrException(formVersionEntity.getFormEntity().getFormType().getAbbreviation());
//            }
//        };
//		
//        
        SingularFormPanel<TableInfo> panelBD = new SingularFormPanel<TableInfo>("panelBD", formConfig) {

			@Override
			protected SInstance createInstance(SFormConfig<TableInfo> singularFormConfig) {
				return null;
//				return formService.loadSInstance(formKey, refType, singularFormConfig.getDocumentFactory(), formVersionEntityPK.getObject());
			}
			
		};
//		panelBD.setViewMode(ViewMode.EDIT);

		
		Button buttonBD = new Button("buttonBD"){
			@Override
			public void onSubmit() {
			}
		};
		
		
		Button buttonRede = new Button("buttonRede");
		Button buttonWS = new Button("buttonWS");
		
		form.add(buttonBD);
//		form.add(panelBD);
		
		form.add(buttonRede);
		form.add(buttonWS);
		
		add(form);
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
