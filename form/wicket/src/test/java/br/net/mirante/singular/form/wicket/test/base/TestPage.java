package br.net.mirante.singular.form.wicket.test.base;

import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.wicket.UIBuilderWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.model.MInstanceRootModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * This is an example page to you to use in your form tests.
 * You can create your own {@link MDicionario} with its packages and types and
 * by using the {@link TestPage#setNewInstanceOfType(String)} you are able
 * to inform which type will be used in your tests. The page will render a
 * component with id `test-form:generated-content` containing your form
 * and its fields.
 * <p>
 * Usage:
 * <pre>
 * 	driver = new WicketTester(new TestApp());
 * page = new TestPage(null);
 * page.build();
 * driver.startPage(page);
 * </pre>
 *
 * @author Fabricio Buzeto
 */
@SuppressWarnings("serial")
public class TestPage extends WebPage {

    private MDicionario dicionario;
    private Form<?> submittedForm;
    private MIComposto currentInstance;

    public TestPage(final PageParameters parameters) {
        super(parameters);
    }

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
        BSGrid bodyContainer = new BSGrid("body-container");
        add(bodyContainer);
        WicketBuildContext ctx = new WicketBuildContext(container.newColInRow(), bodyContainer);
        UIBuilderWicket.buildForEdit(ctx, createTipoModel(dicionario));
        return container;
    }

    @SuppressWarnings("rawtypes")
    private MInstanceRootModel createTipoModel(MDicionario dicionario) {
        return newModelFromInstance(currentInstance);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private MInstanceRootModel newModelFromInstance(MInstancia instance) {
        return new MInstanceRootModel(instance);
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

    public void setDicionario(MDicionario dicionario) {
        this.dicionario = dicionario;
    }

    public void setNewInstanceOfType(String formType) {
        currentInstance = (MIComposto) type(formType).novaInstancia();
    }

    private MTipo<?> type(String type) {
        return dicionario.getTipo(type);
    }

    public Form<?> getSubmittedForm() {
        return submittedForm;
    }

    public MIComposto getCurrentInstance() {
        return currentInstance;
    }

}
