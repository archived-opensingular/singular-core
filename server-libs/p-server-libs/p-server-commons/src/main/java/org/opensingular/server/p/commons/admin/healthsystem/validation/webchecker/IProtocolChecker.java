package org.opensingular.server.p.commons.admin.healthsystem.validation.webchecker;

import org.opensingular.form.type.core.SIString;
import org.opensingular.form.validation.IInstanceValidatable;

public interface IProtocolChecker {
	public void protocolCheck(IInstanceValidatable<SIString> validatable);
}
