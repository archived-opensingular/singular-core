package org.opensingular.form.io;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.type.core.STypeBoolean;
import org.opensingular.form.type.core.STypeDate;
import org.opensingular.form.type.core.STypeDateTime;
import org.opensingular.form.type.core.STypeDecimal;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeLong;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.core.STypeTime;

/*
 * Author: Thais N. Pereira
 */

public class XSDConverter {
	
	private StringBuilder xsd = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			"<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n");

	public void converter(SType<?> sType) {		
		toXsdFromSType(sType, null);
		xsd.append("</xs:schema>");

		System.out.println(getXsd());
	}
	
	public void toXsd(SType<?> sType, Boolean noFormatting) {		
		toXsdFromSType(sType, null);
		xsd.append("</xs:schema>");
		
		if (noFormatting) {
			xsd.replace(0, xsd.length(), xsd.toString().replaceAll("[\n|\t]", ""));
		}
		
		System.out.println(getXsd());
	}
	
	private void toXsdFromSType(SType<?> sType, String parent) {
		Field[] attributes = sType.getClass().getDeclaredFields();
		List<Field> sTypeAttributes = new ArrayList<>();
		
		for (Field attribute : attributes) {
			
			if (SType.class.isAssignableFrom(attribute.getType())) {
				sTypeAttributes.add(attribute);	
			}
		}
		
		if (!sTypeAttributes.isEmpty()) {		
			if (sType instanceof STypeSimple) {
//				toXsdFromSimple((STypeSimple<?, ?>) sType);
			} else if (sType instanceof STypeList) {
				Collection<SType<?>> attr = toXsdFromList((STypeList<?, ?>) sType, parent);
				attr.forEach(s -> toXsdFromSType(s, s.getClass().getSimpleName()));
			} else if (sType instanceof STypeComposite) {
				Collection<SType<?>> attr = toXsdFromComposite((STypeComposite<?>) sType, parent);
				attr.forEach(s -> toXsdFromSType(s, s.getClass().getSimpleName()));
			}
		}
	}

	private Collection<SType<?>> toXsdFromList(STypeList<?, ?> sType, String parent) {
		Collection<SType<?>> attributes = new ArrayList<>();

		if (STypeComposite.class.isAssignableFrom(sType.getElementsType().getClass())) {
			attributes = toXsdFromComposite((STypeComposite<?>) sType.getElementsType(), sType.getElementsType().getClass().getSimpleName());
		}
		
		return attributes;
	}

	private Collection<SType<?>> toXsdFromComposite(STypeComposite<?> sType, String parent) {
		String name = (parent == null) ? sType.getNameSimple() : parent;
		
		if (parent == null) {
			xsd.append("\t<xs:element name=\""+ sType.getNameSimple() +"\" type=\""+ sType.getNameSimple() +"\"/>\n");
		} 

		xsd.append("\t<xs:complexType name=" + name + ">\n");
		xsd.append("\t\t<xs:sequence>\n");

//		xsdComplexType(parent == null ? sType.getNameSimple() : parent, sTypeAttributes);
		
		sType.getFields().forEach(s -> addElement(s, parent));

//		sTypeAttributes.forEach(a -> xsd.append("\t\t"+ xsdElement(a.getName(), getType(a))));

		xsd.append("\t\t</xs:sequence>\n");
		xsd.append("\t</xs:complexType>\n");

//		sType.getFields().forEach(s -> toXsdFromSType(s, s.getClass().getSimpleName()));
		
		return sType.getFields();

	}
	
	private void addElement(SType<?> sType, String parent) {
		
		if (sType.isList()) {
			xsd.append("\t\t\t<xs:element maxOccurs=\"unbounded\" name=\""+ sType.getNameSimple() +"\" type=\""+ getType(((STypeList<?, ?>) sType).getElementsType()) +"\"/>\n");
		} else {
			xsd.append("\t\t\t<xs:element name=\""+ sType.getNameSimple() +"\" type=\""+ getType(sType) +"\"/>\n");
		}
	}
	
	private String getType(SType<?> sType) {
		String name = sType.getClass().getSimpleName();

		if (name.equals(STypeString.class.getSimpleName()) || name.equals(String.class.getSimpleName()))
			return "xs:string";
		if (name.equals(STypeLong.class.getSimpleName()) || name.equals(Long.class.getSimpleName()))
			return "xs:long";
		if (name.equals(STypeInteger.class.getSimpleName()) || name.equals(Integer.class.getSimpleName()))
			return "xs:integer";
		if (name.equals(STypeBoolean.class.getSimpleName()) || name.equals(Boolean.class.getSimpleName()))
			return "xs:boolean";
		if (name.equals(STypeDecimal.class.getSimpleName()) || name.equals(BigDecimal.class.getSimpleName()))
			return "xs:decimal";
		if (name.equals(STypeDate.class.getSimpleName()) || name.equals(Date.class.getSimpleName()))
			return "xs:date";
		if (name.equals(STypeDateTime.class.getSimpleName()))
			return "xs:dateTime";
		if (name.equals(STypeTime.class.getSimpleName()))
			return "xs:time";
		if (sType instanceof STypeSimple<?, ?>) {
			return getType(sType.getSuperType());
		}
		return name;
	}

	public String getXsd() {
		return xsd.toString();
	}

}
