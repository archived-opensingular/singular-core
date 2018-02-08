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
import org.opensingular.internal.lib.commons.xml.MElement;

/*
 * Author: Thais N. Pereira
 */

public class XSDConverter {
	
	public static final String XSD_SINGULAR_NAMESPACE_URI = "http://opensingular.org/FormSchema";
    public static final String XSD_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema";
    public static final String XSD_NAMESPACE_PREFIX = "xs";
    private static final String XSD_SCHEMA = XSD_NAMESPACE_PREFIX + ":schema";
    private static final String XSD_ELEMENT = XSD_NAMESPACE_PREFIX + ":element";
    private static final String XSD_COMPLEX_TYPE = XSD_NAMESPACE_PREFIX + ":complexType";
    private static final String XSD_SEQUENCE = XSD_NAMESPACE_PREFIX + ":sequence";
	
    public MElement toXsd(SType<?> sType) {
    	MElement element = MElement.newInstance(XSD_NAMESPACE_URI, XSD_SCHEMA);
    	toXsdFromSType(sType, null, element);
    	
    	return element;
    }
	
	private void toXsdFromSType(SType<?> sType, String typeName, MElement parent) {
    	//TODO não pode ser assim, pois o tipo pode ser todo dinâmico.
		Field[] attributes = sType.getClass().getDeclaredFields();
		List<Field> sTypeAttributes = new ArrayList<>();
		
		for (Field attribute : attributes) {
			
			if (SType.class.isAssignableFrom(attribute.getType())) {
				sTypeAttributes.add(attribute);	
			}
		}
		
		if (!sTypeAttributes.isEmpty()) {		
			if (sType instanceof STypeList) {
				Collection<SType<?>> attr = toXsdFromList((STypeList<?, ?>) sType, typeName, parent);
				attr.forEach(s -> toXsdFromSType(s, s.getClass().getSimpleName(), parent));
			} else if (sType instanceof STypeComposite) {
				Collection<SType<?>> attr = toXsdFromComposite((STypeComposite<?>) sType, typeName, parent);
				attr.forEach(s -> toXsdFromSType(s, s.getClass().getSimpleName(), parent));
			}
		}
	}

	private Collection<SType<?>> toXsdFromList(STypeList<?, ?> sType, String typeName, MElement parent) {
		Collection<SType<?>> attributes = new ArrayList<>();

		if (STypeComposite.class.isAssignableFrom(sType.getElementsType().getClass())) {
			attributes = toXsdFromComposite((STypeComposite<?>) sType.getElementsType(), sType.getElementsType().getClass().getSimpleName(), parent);
		}
		
		return attributes;
	}

	private Collection<SType<?>> toXsdFromComposite(STypeComposite<?> sType, String typeName, MElement parent) {
		String name = (typeName == null) ? sType.getNameSimple() : typeName;
		
		if (typeName == null) {
			MElement element = parent.addElementNS(XSD_NAMESPACE_URI, XSD_ELEMENT);
			element.setAttribute("name", sType.getNameSimple());
			element.setAttribute("type", sType.getNameSimple());
		} 

		MElement element = parent.addElementNS(XSD_NAMESPACE_URI, XSD_COMPLEX_TYPE);
		element.setAttribute("name", name);
		element = element.addElementNS(XSD_NAMESPACE_URI, XSD_SEQUENCE);

		for (SType<?> child : sType.getFields()) {
			addElement(child, element);
		}
		
		return sType.getFields();
	}
	
	private MElement addElement(SType<?> sType, MElement parent) {
		MElement element = parent.addElementNS(XSD_NAMESPACE_URI, XSD_ELEMENT);
		
		if (sType.isList()) {
			element.setAttribute("name", sType.getNameSimple());
			element.setAttribute("type", getType(((STypeList<?, ?>) sType).getElementsType()));
			element.setAttribute("maxOccurs", "unbounded");
		} else {
			element.setAttribute("name", sType.getNameSimple());
			element.setAttribute("type", getType(sType));
		}
		
		return element;
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
}
