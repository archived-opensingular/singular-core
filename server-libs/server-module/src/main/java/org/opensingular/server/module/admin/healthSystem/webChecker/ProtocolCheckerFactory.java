package org.opensingular.server.module.admin.healthsystem.webchecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class ProtocolCheckerFactory {
	private static Map<String, IProtocolChecker> protocolMap = new HashMap<>();
	private static List<String> protocols;
	
	public ProtocolCheckerFactory() {
		setMapOfProtocols();
		setSupportedProtocols();
	}

	private void setMapOfProtocols() {
		protocolMap.put("IP", new IpChecker());
		protocolMap.put("TCP", new TcpChecker());
		protocolMap.put("HTTP", new HttpChecker());
		protocolMap.put("HTTPS", new HttpChecker());
		protocolMap.put("LDAP", new LdapChecker());
		protocolMap.put("LDAPS", new LdapChecker());
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
	
	/**
	 * Retorna uma implementação de IProtocolChecker de acordo com a url informada, caso não encontre uma implementação 
	 * do protocolo, retornará uma excecão 
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static IProtocolChecker getProtocolChecker(String url) throws Exception{
		for (String string : getSupportedProtocols()) {
			Pattern pattern = Pattern.compile("^(?i)"+string+"(?i)");
			Matcher matcher = pattern.matcher(url);
			
			if(matcher.find()){
				return protocolMap.get(string);
			}
		}
		throw new Exception("Protocolo não suportado!");
	}
	
	/**
	 * 
	 * @return lista de string com os protocolos suportados
	 */
	public static List<String> getSupportedProtocols(){
		return protocols;
	}
}
