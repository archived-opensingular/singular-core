package org.opensingular.form.decorator.action;

import java.io.Serializable;

public interface ISInstanceActionCapable extends Serializable {

    default void addSInstanceActionsProvider(ISInstanceActionsProvider provider) {
        this.addSInstanceActionsProvider(Integer.MIN_VALUE, provider);
    }
    void addSInstanceActionsProvider(int sortPosition, ISInstanceActionsProvider provider);

}
