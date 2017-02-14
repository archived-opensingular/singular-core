package org.opensingular.server.module.admin.healthPanel.validation;

import org.opensingular.form.type.core.SIString;
import org.opensingular.form.validation.IInstanceValidatable;

public interface IProtocolChecker {
	public void protocolCheck(IInstanceValidatable<SIString> validatable);
}
