package org.opensingular.server.p.commons.admin.healthsystem.validation.webchecker;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.opensingular.form.type.core.SIString;
import org.opensingular.form.validation.IInstanceValidatable;

public class LdapChecker implements IProtocolChecker {

	@Override
	public void protocolCheck(IInstanceValidatable<SIString> validatable) {
		Hashtable<String, String> ldapInfo = new Hashtable<>();
		ldapInfo.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		ldapInfo.put(Context.PROVIDER_URL, validatable.getInstance().getValue());
		ldapInfo.put("com.sun.jndi.ldap.read.timeout", "2000");
		
		DirContext dirContext;
		try {
			dirContext = new InitialDirContext(ldapInfo);
			dirContext.close();
		} catch (Exception e) {
			validatable.error(e.getMessage());
		}
	}
}
