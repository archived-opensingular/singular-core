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

package org.opensingular.server.p.commons.admin.healthsystem.validation.webchecker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum ProtocolCheckerFactory {

	IP {
		@Override
		public IProtocolChecker checker() {
			return new IpChecker();
		}
	}, TCP {
		@Override
		public IProtocolChecker checker() {
			return new TcpChecker();
		}
	}, HTTP {
		@Override
		public IProtocolChecker checker() {
			return new HttpChecker();
		}
	}, HTTPS {
		@Override
		public IProtocolChecker checker() {
			return new HttpChecker();
		}
	}, LDAP {
		@Override
		public IProtocolChecker checker() {
			return new LdapChecker();
		}
	}, LDAPS {
		@Override
		public IProtocolChecker checker() {
			return new LdapChecker();
		}
	};

	public abstract IProtocolChecker checker();

	/**
	 * Retorna uma implementação de IProtocolChecker de acordo com a url informada, caso não encontre uma implementação 
	 * do protocolo, retornará uma excecão 
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static IProtocolChecker getProtocolChecker(String url) throws Exception{
		for (ProtocolCheckerFactory checkerFactory : ProtocolCheckerFactory.values()) {
			Pattern pattern = Pattern.compile("^(?i)"+checkerFactory+"(?i)");
			Matcher matcher = pattern.matcher(url);
			
			if(matcher.find()){
				return checkerFactory.checker();
			}
		}
		throw new Exception("Protocolo não suportado!");
	}
}
