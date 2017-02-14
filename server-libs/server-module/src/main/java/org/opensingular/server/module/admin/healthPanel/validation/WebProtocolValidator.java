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

package org.opensingular.server.module.admin.healthPanel.validation;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;
import java.util.Hashtable;
import java.util.Scanner;

import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.opensingular.form.type.core.SIString;
import org.opensingular.form.validation.IInstanceValidatable;

public class WebProtocolValidator {
	public static void protocolValidation(IInstanceValidatable<SIString> validatable) {
		String url = validatable.getInstance().getValue();
		try {
			if(url.contains("ldap://") || url.contains("ldaps://")){
				Hashtable<String, String> ldapInfo = new Hashtable<>();
				ldapInfo.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
				ldapInfo.put(Context.PROVIDER_URL, url);
				ldapInfo.put("com.sun.jndi.ldap.read.timeout", "2000");
				
				// nao tem como definir o tempo limite pra tentar conectar
				DirContext dirContext = new InitialDirContext(ldapInfo);
				dirContext.close();

			}
			// OK
			else if(url.contains("tcp://")){
				url = url.replace("tcp://", "");
				String[] piecesSocketPath = url.split(":");
				Socket testClient = new Socket(piecesSocketPath[0], Integer.valueOf(piecesSocketPath[piecesSocketPath.length-1]));
				testClient.close();

			}
			
			else if(url.contains("udp://")){
				url = url.replace("udp://", "");
				String[] piecesUrl = url.split(":");
				
				// TODO arrumar ping
				Runtime.getRuntime().exec("PING " + piecesUrl[0]);
				Scanner test = new Scanner(Runtime.getRuntime().exec("PING " + piecesUrl[0]).getInputStream());
				while(test.hasNextLine()){
					System.out.println(test.nextLine());
				}
				
				test.close();
				InetSocketAddress address = new InetSocketAddress(InetAddress.getByName(piecesUrl[0]),
						Integer.valueOf(piecesUrl[piecesUrl.length - 1]));

				SocketChannel sc = SocketChannel.open();
				sc.configureBlocking(false);

				boolean reachable = InetAddress.getByName(piecesUrl[0]).isReachable(2000);
				boolean connectSocketChannel = sc.connect(address);
				sc.close();
				
				byte [] bytes1 = new byte[128];
				DatagramSocket ds = new DatagramSocket();
				DatagramPacket dp1 = new DatagramPacket(bytes1, bytes1.length, address);
				
				DatagramChannel dChannel = DatagramChannel.open();
				dChannel.connect(address);
				dChannel.configureBlocking(true);
				ds = dChannel.socket();
				ds.setSoTimeout(1000);
				
				ds.send(dp1);
				
				dp1 = new DatagramPacket(bytes1, bytes1.length);
				Thread.sleep(1000);
				try {
					ds.receive(dp1);
				} catch (SocketTimeoutException e) {
					e.printStackTrace(); // timeout não significa que a porta está fechada.
				}
				
			}else{
				// file, ftp, gopher, http, https, jar, mailto, netdoc
				URLConnection openConnection = new URL(url).openConnection();
				openConnection.setConnectTimeout(2000);
				openConnection.connect();
			}
		} catch (Exception e) {
			validatable.error(e.getMessage());
			e.printStackTrace();
		}
	}
}
