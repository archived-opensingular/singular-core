package org.opensingular.server.module.admin.healthPanel.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProtocolCheckerFactory {
	private static Map<String, IProtocolChecker> protocolMap = new HashMap<>();
	private static List<String> protocols;
	
	public ProtocolCheckerFactory() {
		protocolMap.put("IP", new IpChecker());
		protocolMap.put("TCP", new TcpChecker());
		protocolMap.put("HTTP", new HttpChecker());
		protocolMap.put("HTTPS", new HttpChecker());
		protocolMap.put("LDAP", new LdapChecker());
		protocolMap.put("LDAPS", new LdapChecker());
		
		setSupportedProtocols();
	}
	
	private static void setSupportedProtocols(){
		protocols = new ArrayList<>();
		
		protocols.add("IP");
		protocols.add("TCP");
		protocols.add("HTTP");
		protocols.add("HTTPS");
		protocols.add("LDAP");
		protocols.add("LDAPS");
		
	}
	
	public static IProtocolChecker getProtocolChecker(String url) throws Exception{
		for (String string : getSupportedProtocols()) {
			if(url.matches("^(?i)"+string+"(?i)")){
				return protocolMap.get(string);
			}
		}
		throw new Exception("Protocolo não suportado!");
	}
	
	public static List<String> getSupportedProtocols(){
		maneiraIncorretaDeFazer();
		return protocols;
	}
	
	private static void maneiraIncorretaDeFazer(){
		if(protocols == null){
			protocolMap.put("IP", new IpChecker());
			protocolMap.put("TCP", new TcpChecker());
			protocolMap.put("HTTP", new HttpChecker());
			protocolMap.put("HTTPS", new HttpChecker());
			protocolMap.put("LDAP", new LdapChecker());
			protocolMap.put("LDAPS", new LdapChecker());
			
			setSupportedProtocols();
		}
	}
}
