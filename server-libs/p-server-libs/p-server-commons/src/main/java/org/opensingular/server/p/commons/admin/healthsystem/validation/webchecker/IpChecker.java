package org.opensingular.server.p.commons.admin.healthsystem.validation.webchecker;

import java.net.InetAddress;

import org.opensingular.form.type.core.SIString;
import org.opensingular.form.validation.IInstanceValidatable;

public class IpChecker implements IProtocolChecker {

	@Override
	public void protocolCheck(IInstanceValidatable<SIString> validatable) {
		String url = validatable.getInstance().getValue().replace("ip://", "");
		String[] piecesUrl = url.split(":");
		
		try {
			if(!InetAddress.getByName(piecesUrl[0]).isReachable(2000)){
				throw new Exception("Address not reacheble!");
			}
		} catch (Exception e) {
			validatable.error(e.getMessage());
		}
	}
}
