package org.opensingular.form.io;

import org.junit.Test;
import org.opensingular.form.io.sample.STypeExemplo;
import org.opensingular.form.type.util.STypeLatitudeLongitude;

/*
 * Author: Thais N. Pereira
 */

public class TestXSDConverter {
	@Test
	public void testXsdConverter() {
		XSDConverter conversor = new XSDConverter();
		conversor.conversor(STypeExemplo.class);
		conversor.getXsd();
	}
}
