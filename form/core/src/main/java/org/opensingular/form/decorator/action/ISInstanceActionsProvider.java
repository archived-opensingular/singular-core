package org.opensingular.form.decorator.action;

import java.io.Serializable;

import org.opensingular.form.SInstance;

public interface ISInstanceActionsProvider extends Serializable {

    Iterable<SInstanceAction> getActions(ISInstanceActionCapable target, SInstance instance);

}
