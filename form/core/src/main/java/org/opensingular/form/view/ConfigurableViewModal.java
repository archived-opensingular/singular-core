package org.opensingular.form.view;

public interface ConfigurableViewModal<S extends SView> extends ConfigurableModal<S> {
    boolean isEnforceValidationOnAdd();

    String getEnforcedValidationMessage();

    boolean isValidateAllLineOnConfirmAndCancel();

    String getEditActionLabel();

    boolean isEditEnabled();

}
