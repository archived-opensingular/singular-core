package org.opensingular.form.io;

import org.junit.Test;
import org.opensingular.form.SDictionary;
import org.opensingular.form.io.sample.STypeExemplo;

/*
 * Author: Thais N. Pereira
 */

public class TestXSDConverter {
	@Test
	public void testXsdConverter() {
		STypeExemplo e = SDictionary.create().getType(STypeExemplo.class);
		XSDConverter converter = new XSDConverter();
		converter.converter(e);

		//TODO thais -  fazer uma assertiva sobre o resultado. Ex: verificar se o xsd retornado é o esperado.
	}
}
