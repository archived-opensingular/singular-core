package br.net.mirante.singular.form.wicket.component;

import java.lang.reflect.InvocationTargetException;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IFormSubmitter;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

public class SingularForm<T> extends Form<T> {

    public SingularForm(String id, IModel<T> model) {
        super(id, model);
    }

    public SingularForm(String id) {
        super(id);
    }

    protected boolean isIgnoreValidation() {
        return false;
    }
    protected boolean isIgnoreErrors() {
        return false;
    }

    //@formatter:off
    @Override
    protected void callOnError(IFormSubmitter submitter)
    {
        if (!isIgnoreErrors())
            super.callOnError(submitter);
    }
    
    @Override
    public void process(IFormSubmitter submittingComponent) {
        if (!isEnabledInHierarchy() || !isVisibleInHierarchy())
        {
            // since process() can be called outside of the default form workflow, an additional
            // check is needed

            // FIXME throw listener exception
            return;
        }

        final boolean ignoreValidation = isIgnoreValidation();
        final boolean ignoreErrors = isIgnoreErrors();

        // run validation
        if (!ignoreValidation)
            validate();

        // If a validation error occurred
        if (!ignoreErrors && hasError())
        {
            // mark all children as invalid
            markFormComponentsInvalid();

            // let subclass handle error
            callOnError(submittingComponent);
        }
        else
        {
            // mark all children as valid
            markFormComponentsValid();

            // before updating, call the interception method for clients
            beforeUpdateFormComponentModels();

            // Update model using form data
            updateFormComponentModels();

            // validate model objects after input values have been bound
            internalOnValidateModelObjects();
            if (!ignoreErrors && hasError())
            {
                callOnError(submittingComponent);
                return;
            }

            // Form has no error
            delegateSubmit(submittingComponent);
        }
    }
    
    /**
     * Calls {@linkplain #onValidateModelObjects()} on this form and all nested forms that are
     * visible and enabled
     */
    private void internalOnValidateModelObjects()
    {
        onValidateModelObjects();
        visitChildren(Form.class, new IVisitor<Form<?>, Void>()
        {
            @Override
            public void component(Form<?> form, IVisit<Void> visit)
            {
                if (form.isSubmitted())
                {
                    invokeOnValidateModelObjects(form);
                }
                else
                {
                    visit.dontGoDeeper();
                }
            }
        });
    }
    //@formatter:on

    private void invokeOnValidateModelObjects(Form<?> form) {
        if (form instanceof SingularForm<?>) {
            ((SingularForm<?>) form).onValidateModelObjects();
        } else {
            try {
                Form.class.getMethod("onValidateModelObjects").invoke(form);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
    }
}