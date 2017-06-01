package org.opensingular.form.decorator.action;

public interface ISInstanceActionCapable {

    default void addSInstanceActionsProvider(ISInstanceActionsProvider provider) {
        this.addSInstanceActionsProvider(Integer.MIN_VALUE, provider);
    }
    void addSInstanceActionsProvider(int sortPosition, ISInstanceActionsProvider provider);

}
