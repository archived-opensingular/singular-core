package br.net.mirante.singular.form.wicket.test.base;

import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.wicket.SingularFormConfigWicketImpl;
import br.net.mirante.singular.form.wicket.SingularFormContextWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.model.MInstanceRootModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * This is an example page to you to use in your form tests.
 * You can create your own {@link SDictionary} with its packages and types and
 * by using the {@link TestPage#setNewInstanceOfType(String)} you are able
 * to inform which type will be used in your tests. The page will render a
 * component with id `test-form:generated-content` containing your form
 * and its fields.
 * <p>
 * Usage:
 * <pre>
 * 	driver = new WicketTester(new TestApp());
 * page = new TestPage();
 * page.build();
 * driver.startPage(page);
 * </pre>
 *
 * @author Fabricio Buzeto
 */
@SuppressWarnings("serial")
public class TestPage extends WebPage {

    private SDictionary dicionario;
    private Form<?> submittedForm;
    private SIComposite currentInstance;
    private SingularFormContextWicket singularFormContext = new SingularFormConfigWicketImpl().getContext();
    private ViewMode viewMode;
    private boolean annotationEnabled =false;

    public TestPage() {
        setAsEditView();
    }

    public void setAsEditView() {
        viewMode = ViewMode.EDITION;
    }
    public void setAsVisualizationView() {
        viewMode = ViewMode.VISUALIZATION;
    }
    public void enableAnnotation(){ annotationEnabled = true; }


    public TestPage(final PageParameters parameters) {
        super(parameters);
        if (parameters.get("viewMode").isNull()) {
            setAsEditView();
        } else {
            viewMode = ViewMode.valueOf(parameters.get("viewMode").toString());
        }
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
        WicketBuildContext ctx = new WicketBuildContext(container.newColInRow(), bodyContainer, createTipoModel(dicionario));
        if(annotationEnabled) ctx.enableAnnotation();
        singularFormContext.getUIBuilder().build(ctx, viewMode);
        return container;
    }

    @SuppressWarnings("rawtypes")
    private MInstanceRootModel createTipoModel(SDictionary dicionario) {
        return newModelFromInstance(currentInstance);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private MInstanceRootModel newModelFromInstance(SInstance instance) {
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

    public void setDicionario(SDictionary dicionario) {
        this.dicionario = dicionario;
    }

    public void setNewInstanceOfType(String formType) {
        currentInstance = (SIComposite) type(formType).novaInstancia();
    }

    private SType<?> type(String type) {
        return dicionario.getTipo(type);
    }

    public Form<?> getSubmittedForm() {
        return submittedForm;
    }

    public SIComposite getCurrentInstance() {
        return currentInstance;
    }

    public void setCurrentInstance(SIComposite currentInstance) {
        this.currentInstance = currentInstance;
    }
}
