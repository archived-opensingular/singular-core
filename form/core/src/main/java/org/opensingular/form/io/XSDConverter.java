package org.opensingular.form.io;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.opensingular.form.SType;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.type.core.STypeBoolean;
import org.opensingular.form.type.core.STypeDate;
import org.opensingular.form.type.core.STypeDateTime;
import org.opensingular.form.type.core.STypeDecimal;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeLong;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.core.STypeTime;
import org.opensingular.form.type.country.brazil.STypeCEP;

/*
 * Author: Thais N. Pereira
 */

public class XSDConverter {
	
	private String xsd = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			"<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n";
	
	public void conversor(Class<? extends SType<?>> sType) {		
		lerStype(sType, null);
		xsd = xsd + "</xs:schema>";
	}
	
	private void lerStype(Class<? extends SType<?>> sType, String pai) {
		Field[] atributos = sType.getDeclaredFields();
		List<Field> atributosSType = new ArrayList<>();
		
		for (Field atributo : atributos) {
			if (SType.class.isAssignableFrom(atributo.getType())) {
				atributosSType.add(atributo);	
			}
		}
		
		if (!atributosSType.isEmpty()) {
			preencherXsd(atributosSType, pai, sType);
			atributosSType.forEach(a -> lerStype((Class<? extends SType<?>>) a.getType(), a.getType().getSimpleName()));
		}
	}
	
	private void preencherXsd(List<Field> atributosSType, String pai, Class<? extends SType<?>> sType) {
		xsd = (pai == null) ? xsd + xsdElement(sType.getSimpleName(), sType.getSimpleName()) : xsd;
		xsdComplexType(pai == null ? sType.getSimpleName() : pai, atributosSType);
	}

	
	private String xsdElement(String nome, String classe) {
		return "\t<xs:element name=\""+ nome +"\" type=\""+ classe +"\" />\n";
	}
	
	private void xsdComplexType(String classe, List<Field> atributosSType) {
		xsd = xsd + "\t<xs:complexType name=\""+ classe +"\">\n" +
				"\t\t<xs:sequence>\n";
		
		atributosSType.forEach(a -> xsd = xsd + "\t\t"+ xsdElement(a.getName(), getType(a)));
		
		xsd = xsd + "\t\t</xs:sequence>\n" +
				"\t</xs:complexType>\n";
		
	}
	
	private String getType(Field atributo) {
		Class<?> classe = atributo.getType();
		String type = STypeSimple.class.isAssignableFrom(atributo.getType()) ? "xs:"+getSimpleType(classe) : classe.getSimpleName();
		
		return type;
	}
	
	private String getSimpleType(Class<?> classe) {
		switch (classe.getSimpleName()) {
			case "STypeString":
				return "string";
			case "STypeLong":
				return "long";
			case "STypeInteger":
				return "integer";
			case "STypeBoolean":
				return "boolean";
			case "STypeDecimal":
				return "decimal";
			case "STypeDate":
				return "date";
			case "STypeDateTime":
				return "dateTime";
			case "STypeTime":
				return "time";
			default:
				return getSimpleType(classe.getSuperclass());
		}
		
	}
	
	public void getXsd() {
		System.out.println(xsd);
	}
}
