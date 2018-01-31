package org.opensingular.form.io;

import org.junit.Test;
import org.opensingular.form.io.sample.STypeExemplo;

/*
 * Author: Thais N. Pereira
 */

//TODO thais - alterar nas configurações do eclipse par usar 4 espaços ao invés de tabulações na formatação do código.
public class TestXSDConverter {
	@Test
	public void testXsdConverter() {
		XSDConverter converter = new XSDConverter();
		converter.converter(STypeExemplo.class);
		converter.getXsd();

		//TODO thais -  fazer uma assertiva sobre o resultado. Ex: verificar se o xsd retornado é o esperado.
	}
}
