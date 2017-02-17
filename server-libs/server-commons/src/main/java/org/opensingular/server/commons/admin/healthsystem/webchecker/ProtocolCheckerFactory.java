/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.server.commons.admin.healthsystem.webchecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Named;

@Named
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
	
	public static List<String> getSupportedProtocols(){
		return protocols;
	}
}
