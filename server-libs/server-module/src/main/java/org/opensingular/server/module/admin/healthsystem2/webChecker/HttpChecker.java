package org.opensingular.server.module.admin.healthsystem.webchecker;

import java.net.URL;
import java.net.URLConnection;

import org.opensingular.form.type.core.SIString;
import org.opensingular.form.validation.IInstanceValidatable;

public class HttpChecker implements IProtocolChecker {

	@Override
	public void protocolCheck(IInstanceValidatable<SIString> validatable) {
		URLConnection openConnection;
		try {
			openConnection = new URL(validatable.getInstance().getValue()).openConnection();
			openConnection.setConnectTimeout(2000);
			openConnection.connect();
		} catch (Exception e) {
			validatable.error(e.getMessage());
		}
	}

}
