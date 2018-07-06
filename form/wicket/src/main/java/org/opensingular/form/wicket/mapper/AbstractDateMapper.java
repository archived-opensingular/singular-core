package org.opensingular.form.wicket.mapper;

import java.io.Serializable;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SInstance;
import org.opensingular.form.wicket.IAjaxUpdateListener;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.behavior.AjaxUpdateInputBehavior;
import org.opensingular.form.wicket.behavior.InputMaskBehavior;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.lib.wicket.util.behavior.DatePickerInitBehaviour;
import org.opensingular.lib.wicket.util.behavior.DatePickerSettings;
import org.opensingular.lib.wicket.util.behavior.SingularDatePickerSettings;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSControls;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSInputGroup;
import org.opensingular.lib.wicket.util.resource.DefaultIcons;

import static org.opensingular.lib.wicket.util.bootstrap.datepicker.BSDatepickerConstants.JS_CHANGE_EVENT;

@SuppressWarnings("serial")
public abstract class AbstractDateMapper extends AbstractControlsFieldComponentMapper {

    private IConsumer<? extends Component> textFieldConfigurer = IConsumer.noop();

    private Component button;
    private TextField inputText;
    private boolean createButton = true;

    @SuppressWarnings("unchecked")
    @Override
    public Component appendInput(WicketBuildContext ctx, BSControls formGroup, IModel<String> labelModel) {
        inputText = createInputText(ctx, labelModel);
        BSInputGroup bsInputGroup = (BSInputGroup) formGroup.appendDatepicker(inputText
                , getOptions(ctx.getModel()), getDatePickerSettings(ctx));
        if (isCreateButton()) {
            button = bsInputGroup.newButtonAddon(DefaultIcons.CALENDAR);
        }
        return inputText;
    }

    /**
     * Method responsible for configure the input.
     *
     * @param ctx        The wicket Context.
     * @param labelModel The label of the input.
     */
    public TextField createInputText(WicketBuildContext ctx, IModel<String> labelModel) {
        TextField comp = getInputData(ctx.getModel());
        configureInputDateText(ctx, labelModel, comp);
        return comp;
    }

    private void configureInputDateText(WicketBuildContext ctx, IModel<String> labelModel, TextField comp) {
        if (labelModel != null) {
            comp.setLabel(labelModel);
        }
        comp
                .setOutputMarkupId(true)
                .add(getInputMaskBehavior())
                .add(new DatePickerInitBehaviour(getDatePickerSettings(ctx)));

        if (textFieldConfigurer != null) {
            ((IConsumer) textFieldConfigurer).accept(comp);
        }
    }

    /**
     * Settings for the DatePicker.
     * <code> http://bootstrap-datepicker.readthedocs.io/en/latest/</code>
     *
     * @param ctx The ctx that contanins model and view.
     * @return Return a DatePickerSettings.
     * @see SingularDatePickerSettings
     */
    protected DatePickerSettings getDatePickerSettings(WicketBuildContext ctx) {
        return null;
    }

    protected InputMaskBehavior getInputMaskBehavior() {
        return new InputMaskBehavior(InputMaskBehavior.Masks.FULL_DATE);
    }

    protected abstract TextField getInputData(IModel<? extends SInstance> model);

    protected Map<String, ? extends Serializable> getOptions(IModel<? extends SInstance> model) {
        return null;
    }


    public boolean isCreateButton() {
        return createButton;
    }

    public void setCreateButton(boolean createButton) {
        this.createButton = createButton;
    }

    public void setTextFieldConfigurer(IConsumer<? extends Component> textFieldConfigurer) {
        this.textFieldConfigurer = textFieldConfigurer;
    }

    @Override
    public void addAjaxUpdate(WicketBuildContext ctx, Component component, IModel<SInstance> model, IAjaxUpdateListener listener) {
        addAjaxEvent(model, listener, inputText, button);
    }

    /**
     * Method to add AjaxEvent's to the Date Mapper. This event's should be add to works fine with dependsON.
     * If this ajaxEvent don't have, can have a error if have a dependsOn with exists = false.
     *
     * @param model    The model for process and validate.
     * @param listener The listener for process and validate.
     * @param componet The component that will be the ajax Event's adding.
     * @param button   The button addon, if exits. <code>isCreateButton()</code>
     */
    public static void addAjaxEvent(IModel<SInstance> model, IAjaxUpdateListener listener, TextField componet, Component button) {
        componet
                .add(new SingularEventBehavior()
                        .setProcessEvent(JS_CHANGE_EVENT, componet)
                        .setValidateEvent("blur", componet)
                        .setSupportComponents(button))
                .add(AjaxUpdateInputBehavior.forProcess(model, listener))
                .add(AjaxUpdateInputBehavior.forValidate(model, listener));
    }

}
