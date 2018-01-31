package org.opensingular.form.io;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.opensingular.form.SType;
import org.opensingular.form.STypeSimple;

/*
 * Author: Thais N. Pereira
 */
//TODO thais -  ao invés de utilizar concatenação de strings com o '+' utilize um StringBuilder e vá fazendo appends. Utilizar apenas uma instância de StringBuilder
//TODO thais - lembre-se de alterar todo o código para inglês: nomes de métodos e nomes de variáveis.
public class XSDConverter {
	
	private String xsd = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			"<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n";

	//TODO thais -  crie uma sobrecarga desse método em que possa escolher se o xsd retornado será ou não formatado (sem formatação seria sem quebras de linhas, tabulações e espaços)
	public void converter(Class<? extends SType<?>> sType) {		
		readStype(sType, null);
		xsd = xsd + "</xs:schema>";
	}
	
	private void readStype(Class<? extends SType<?>> sType, String superClass) {
		Field[] attributes = sType.getDeclaredFields();
		List<Field> stypeAttributes = new ArrayList<>();
		
		for (Field attribute : attributes) {
			if (SType.class.isAssignableFrom(attribute.getType())) {
				stypeAttributes.add(attribute);	
			}
		}
		
		if (!stypeAttributes.isEmpty()) {
			writeXsd(stypeAttributes, superClass, sType);
			stypeAttributes.forEach(a -> readStype((Class<? extends SType<?>>) a.getType(), a.getType().getSimpleName()));
		}
	}
	
	private void writeXsd(List<Field> stypeAttributes, String superClass, Class<? extends SType<?>> sType) {
		xsd = (superClass == null) ? xsd + xsdElement(sType.getSimpleName(), sType.getSimpleName()) : xsd;
		xsdComplexType(superClass == null ? sType.getSimpleName() : superClass, stypeAttributes);
	}

	
	private String xsdElement(String name, String type) {
		return "\t<xs:element name=\""+ name +"\" type=\""+ type +"\" />\n";
	}
	
	private void xsdComplexType(String type, List<Field> stypeAttributes) {
		xsd = xsd + "\t<xs:complexType name=\""+ type +"\">\n" +
				"\t\t<xs:sequence>\n";
		
		stypeAttributes.forEach(a -> xsd = xsd + "\t\t"+ xsdElement(a.getName(), getType(a)));
		
		xsd = xsd + "\t\t</xs:sequence>\n" +
				"\t</xs:complexType>\n";
		
	}
	
	private String getType(Field attribute) {
		Class<?> typeClass = attribute.getType();
		String type = STypeSimple.class.isAssignableFrom(attribute.getType()) ? "xs:"+getSimpleType(typeClass) : typeClass.getSimpleName();
		
		return type;
	}
	
	//TODO thais - substituir as constantes String abaixo pelo class name das classes. Ex: STypeString.class.getName()
	private String getSimpleType(Class<?> typeClass) {
		switch (typeClass.getSimpleName()) {
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
				return getSimpleType(typeClass.getSuperclass());
		}
		
	}

	//TODO thais -  ao final retorne a String ao invés de dar print no console. Para fazer isso no StringBuilder basta chamar o toString()
	public void getXsd() {
		System.out.println(xsd);
	}
}
