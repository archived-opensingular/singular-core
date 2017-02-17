package org.opensingular.server.module.admin.healthsystem.tmp;

import org.opensingular.form.type.core.SIString;
import org.opensingular.form.validation.IInstanceValidatable;

public interface IProtocolChecker {
	public void protocolCheck(IInstanceValidatable<SIString> validatable);
}
