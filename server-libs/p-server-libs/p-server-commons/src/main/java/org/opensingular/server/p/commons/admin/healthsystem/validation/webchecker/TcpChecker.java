package org.opensingular.server.p.commons.admin.healthsystem.validation.webchecker;

import java.net.Socket;

import org.opensingular.form.type.core.SIString;
import org.opensingular.form.validation.IInstanceValidatable;

import javax.net.ssl.SSLSocketFactory;

public class TcpChecker implements IProtocolChecker {

	@Override
	public void protocolCheck(IInstanceValidatable<SIString> validatable) {
		String url = validatable.getInstance().getValue().replace("tcp://", "");
		String[] piecesSocketPath = url.split(":");
		Socket testClient;
		try {
			testClient = SSLSocketFactory.getDefault().createSocket(piecesSocketPath[0], Integer.parseInt(piecesSocketPath[piecesSocketPath.length-1]));
			testClient.close();
		} catch (Exception e) {
			validatable.error(e.getMessage());
		} 
	}
}
