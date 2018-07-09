package org.opensingular.form.wicket.mapper;

import java.io.Serializable;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SInstance;
import org.opensingular.form.wicket.IAjaxUpdateListener;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.behavior.AjaxUpdateInputBehavior;
import org.opensingular.form.wicket.behavior.InputMaskBehavior;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.lib.wicket.util.behavior.DatePickerSettings;
import org.opensingular.lib.wicket.util.behavior.SingularDatePickerSettings;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSControls;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSInputGroup;
import org.opensingular.lib.wicket.util.resource.DefaultIcons;

import static org.opensingular.form.wicket.mapper.SingularEventsHandlers.OPTS_ORIGINAL_PROCESS_EVENT;
import static org.opensingular.form.wicket.mapper.SingularEventsHandlers.OPTS_ORIGINAL_VALIDATE_EVENT;
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
        inputText = createInputText(ctx.getModel(), labelModel);
        BSInputGroup bsInputGroup = (BSInputGroup) formGroup.appendDatepicker(inputText
                , getOptions(ctx.getModel()), getDatePickerSettings(ctx));
        if (isCreateButton()) {
            button = bsInputGroup.newButtonAddon(DefaultIcons.CALENDAR);
        }
        return inputText;
    }

    /**
     * Method responsible for create the input.
     * This method could be used for create a input data configurated.
     *
     * @param model      The model of the input.
     * @param labelModel The label of the input.
     */
    public TextField createInputText(IModel<? extends SInstance> model, IModel<String> labelModel) {
        TextField comp = getInputData(model);
        configureInputDateText(labelModel, comp);
        return comp;
    }

    /**
     * Settings for the DatePicker.
     * This settings can be configurated using the <code>SViewDate</code>.
     *
     * @param ctx The ctx that contanins model and view.
     * @return Return a DatePickerSettings.
     * @see SingularDatePickerSettings
     */
    protected DatePickerSettings getDatePickerSettings(WicketBuildContext ctx) {
        return null;
    }

    /**
     * The mask of the input, by default will use the FULL_DATE mask.
     *
     * @return
     */
    protected InputMaskBehavior getInputMaskBehavior() {
        return new InputMaskBehavior(InputMaskBehavior.Masks.FULL_DATE);
    }

    /**
     * The input data with the configuration necessery, some converter for example.
     *
     * @param model The model of the instance
     * @return TextField of the date.
     */
    protected abstract TextField getInputData(IModel<? extends SInstance> model);

    /**
     * Method for configure the options of the Bootstrap DatePicker.
     *
     * @param model The model of the instance.
     * @return return a Map with the key (datePicker option) and the value.
     */
    protected Map<String, ? extends Serializable> getOptions(IModel<? extends SInstance> model) {
        return null;
    }

    /**
     * Method responsible for enable or disabled the creation of the button addon.
     * By default the button will be created.
     *
     * @return True for create;
     */
    protected boolean isCreateButton() {
        return createButton;
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
     * @param model     The model for process and validate.
     * @param listener  The listener for process and validate.
     * @param component The component that will be the ajax Event's adding.
     * @param button    The button addon, if exits. <code>isCreateButton()</code>
     */
    static void addAjaxEvent(IModel<SInstance> model, IAjaxUpdateListener listener, TextField component, Component button) {//TODO avaliar se é necessario utilizar o SingularEventBehavior.
        component.add(new SingularEventsHandlers(SingularEventsHandlers.FUNCTION.ADD_TEXT_FIELD_HANDLERS)
                .setOption(OPTS_ORIGINAL_PROCESS_EVENT, JS_CHANGE_EVENT)
                .setOption(OPTS_ORIGINAL_VALIDATE_EVENT, JS_CHANGE_EVENT))
                .add(AjaxUpdateInputBehavior.forProcess(model, listener))
                .add(AjaxUpdateInputBehavior.forValidate(model, listener));
//                .add(new SingularEventBehavior()
//                        .setProcessEvent(JS_CHANGE_EVENT, component)
//                        .setValidateEvent(JS_CHANGE_EVENT, component)
//                        .setSupportComponents(button));
    }

    /**
     * Method responsible for configure the input of the date Text.
     *
     * @param labelModel The label of the input.
     * @param comp       The textFieldComponent that will be configurated.
     */
    private void configureInputDateText(IModel<String> labelModel, TextField comp) {
        if (labelModel != null) {
            comp.setLabel(labelModel);
        }

        comp.add(AttributeAppender.append("autocomplete", "off"))
                .setOutputMarkupId(true)
                .add(getInputMaskBehavior());

        if (textFieldConfigurer != null) {
            ((IConsumer) textFieldConfigurer).accept(comp);
        }
    }


}
