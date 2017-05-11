package org.opensingular.form.wicket.panel;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;

public interface IShowModalEvent extends Serializable {

    Component getModalContent(String id);

    AjaxRequestTarget getTarget();
}
