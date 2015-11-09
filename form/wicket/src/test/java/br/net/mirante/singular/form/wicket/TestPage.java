package br.net.mirante.singular.form.wicket;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.wicket.model.MInstanciaRaizModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;

@SuppressWarnings("serial")
public class TestPage extends WebPage {
    
    private MDicionario dicionario;
    private Form<?> submittedForm;
    private MIComposto currentInstance;
    
    public void build() {
	queue(createForm());
        queue(createSaveButton());
    }

    @SuppressWarnings("rawtypes")
    private Form createForm() {
	return (Form) new Form<>("test-form").queue(createContainer());
    }

    @SuppressWarnings("unchecked")
    private BSGrid createContainer() {
	BSGrid container = new BSGrid("generated-content");
        WicketBuildContext ctx = new WicketBuildContext(container.newColInRow());
	UIBuilderWicket.buildForEdit(ctx, createTipoModel(dicionario));
	return container;
    }

    @SuppressWarnings("rawtypes")
    private MInstanciaRaizModel createTipoModel(MDicionario dicionario) {
	return newModelFromInstance(currentInstance);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private MInstanciaRaizModel newModelFromInstance(MInstancia instance) {
	return new MInstanciaRaizModel(instance) {
		protected MTipo getTipoRaiz() {
		    return instance.getMTipo();
		}
	    };
    }
    
    private AjaxButton createSaveButton() {
	return new AjaxButton("save-btn") {
	    @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
		super.onSubmit(target, form);
		submittedForm = form;
            }
        };
    }

    public TestPage(final PageParameters parameters) {
        super(parameters);
    }

    public void setDicionario(MDicionario dicionario) {
	this.dicionario = dicionario;
    }
    
    public void setNewInstanceOfType(String formType) {
        currentInstance = (MIComposto) type(formType).novaInstancia();
    }
    
    private MTipo<?> type(String type){
	return dicionario.getTipo(type);
    }
    
    public Form<?> getSubmittedForm() {
	return submittedForm;
    }
    
    public MIComposto getCurrentInstance() {
	return currentInstance;
    }
    
}
