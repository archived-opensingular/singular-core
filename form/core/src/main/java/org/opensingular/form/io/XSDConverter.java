package org.opensingular.form.io;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.type.core.STypeBoolean;
import org.opensingular.form.type.core.STypeDate;
import org.opensingular.form.type.core.STypeDateTime;
import org.opensingular.form.type.core.STypeDecimal;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeLong;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.core.STypeTime;
import org.opensingular.internal.lib.commons.xml.MElement;
import org.opensingular.internal.lib.commons.xml.MParser;
import org.w3c.dom.Node;

/*
 * Author: Thais N. Pereira
 */
//TODO thais remover códigos comentados e méotodos e variáveis não utilizados
public class XSDConverter {
	
	public static final String XSD_SINGULAR_NAMESPACE_URI = "http://opensingular.org/FormSchema";
    public static final String XSD_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema";
    public static final String XSD_NAMESPACE_PREFIX = "xs";
    private static final String XSD_SCHEMA = XSD_NAMESPACE_PREFIX + ":schema";
    private static final String XSD_ELEMENT = XSD_NAMESPACE_PREFIX + ":element";
    private static final String XSD_COMPLEX_TYPE = XSD_NAMESPACE_PREFIX + ":complexType";
    private static final String XSD_SEQUENCE = XSD_NAMESPACE_PREFIX + ":sequence";
    
    private static XsdTypeMapping typeMapping;
	
    public MElement toXsd(SType<?> sType) {
    	MElement root = MElement.newInstance(XSD_NAMESPACE_URI, XSD_SCHEMA);

		MElement element = root.addElementNS(XSD_NAMESPACE_URI, XSD_ELEMENT);
		element.setAttribute("name", sType.getNameSimple());
		element.setAttribute("type", sType.getNameSimple());
		
    	toXsdFromSType(sType, root);
    	
    	return root;
    }
	
	private void toXsdFromSType(SType<?> sType, MElement parent) {
		if (sType.isList()) {
			Collection<SType<?>> attributesType = toXsdFromList((STypeList<?, ?>) sType, parent);
			attributesType.forEach(type -> toXsdFromSType(type, parent));
		} else if (sType.isComposite()) {
			Collection<SType<?>> attributesType = toXsdFromComposite((STypeComposite<?>) sType, parent);
			attributesType.forEach(type -> toXsdFromSType(type, parent));
		}
	}

	private Collection<SType<?>> toXsdFromList(STypeList<?, ?> sType, MElement parent) {
		Collection<SType<?>> attributesType = new ArrayList<>();

		// TODO O que fazer caso a lista seja de um tipo simples??
		
		if (sType.getElementsType().isComposite()) {
			attributesType = toXsdFromComposite((STypeComposite<?>) sType.getElementsType(), parent);
		}
		
		return attributesType;
	}

	private Collection<SType<?>> toXsdFromComposite(STypeComposite<?> sType, MElement parent) {
		String name = null;
		 
		MElement element = parent.addElementNS(XSD_NAMESPACE_URI, XSD_COMPLEX_TYPE);

		if (sType.getSuperType().getNameSimple().equals(STypeComposite.class.getSimpleName())) {
			name = sType.getNameSimple();
		} else {
			name = sType.getSuperType().getNameSimple();
		}
		
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
			element = element.addElementNS(XSD_NAMESPACE_URI, XSD_COMPLEX_TYPE);
			element = element.addElementNS(XSD_NAMESPACE_URI, XSD_SEQUENCE);
			element = element.addElementNS(XSD_NAMESPACE_URI, XSD_ELEMENT);
			element.setAttribute("name", getType(((STypeList<?, ?>) sType).getElementsType()));
			element.setAttribute("type", getType(((STypeList<?, ?>) sType).getElementsType()));
			element.setAttribute("maxOccurs", "unbounded");
			
		} else {
			element.setAttribute("name", sType.getNameSimple());
			element.setAttribute("type", getType(sType));
		}
		
		return element;
	}
	
	private String getType(SType<?> sType) {

        //TODO thais utilizar o método XsdTypeMapping#findXsdType ao invés do código abaixo.

		String name = sType.getSuperType().getNameSimple();

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
	
	
	
	
	
	public static SType<?> xsdToSType(PackageBuilder packageForNewTypes, InputStream in) {
        MElement xsdDefinition;
        try {
            xsdDefinition = MParser.parse(in, true, false);
        } catch (Exception e) {
            throw new SingularFormException("Erro lendo xml (parse)", e);
        }
        return xsdToSType(packageForNewTypes, xsdDefinition);
    }

    public static SType<?> xsdToSType(PackageBuilder packageForNewTypes, String xsdDefinition) {
        return xsdToSType(packageForNewTypes, SFormXMLUtil.parseXml(xsdDefinition));
    }

    private static SType<?> xsdToSType(PackageBuilder packageForNewTypes, MElement root) {

        XsdContext    xsdContext = new XsdContext(packageForNewTypes);
        ElementReader element    = new ElementReader(xsdContext, root);

        if (!element.isTagXsdSchema()) {
            throw new SingularFormException(
                    "O XSD não é válido: a tag raiz é '" + element.getNodeName() + "' e deveria ser 'xs:schema'");
        }

        readXsd(null, element, element);

        Collection<SType<?>> types = packageForNewTypes.getPackage().getLocalTypes();
        return types.size() >= 1 ? types.iterator().next() : null;
    }

    private static void readXsd(SType<?> typeContext, ElementReader parent, ElementReader root) {
        for (ElementReader element : parent) {
            if (element.isTagXsdElement()) {
                readXsdElementDefinition(typeContext, parent, element, root, true);
            } //else if (element.isTagComplexType() || element.isTagSequence()) {
//                if (typeContext instanceof STypeComposite) {
//                    readXsd(typeContext, element);
//                } else {
//                    element.checkUnexpectedNodeFor(typeContext);
//                }
//            } else if (element.isTagAttribute()) {
//                readXsdAtributeDefinition(element, typeContext);
//            } else {
//                element.checkUnknownNodeTreatment();
//            }

        }
    }

    private static void readXsdElementDefinition(SType<?> typeContext, ElementReader parent, ElementReader element, ElementReader root, boolean generateLabel) {
        String   name          = element.getAttrRequired("name");
        SType<?> typeOfNewType = detectType(element, root);
//        SType<?> newType;
        
        
//        if (typeOfNewType.isComposite()) {
//        	STypeComposite<?> type = (STypeComposite<?>) typeOfNewType;
//        	addAttributes(type, root);
//
//        }
//        if (typeContext == null) {
//            if (typeOfNewType.isList()) {
//                throw new SingularFormException(element.errorMsg("Tipo raiz não esperado como lista"));
//            }
//            typeOfNewType = parent.getPkg().createType(name, typeOfNewType);
//            if (generateLabel) {
//            	typeOfNewType.asAtr().label(StringUtils.capitalize(name));
//            }
//            readXsd(typeOfNewType, element);
//        } else if (typeContext.isComposite()) {
//            if (typeOfNewType.isList()) {
//                ElementReader listElementType = findListElement(element);
//                typeOfNewType = ((STypeComposite) typeContext).addFieldListOfComposite(name, listElementType.getAttr("name"));
//                if (generateLabel) {
//                	typeOfNewType.asAtr().label(StringUtils.capitalize(name));
//                }
//                SType<?> elementType = ((STypeList<?, ?>) typeOfNewType).getElementsType();
//                readXsd(elementType, findNextComplexType(listElementType));
//            } else {
//            	typeOfNewType = ((STypeComposite) typeContext).addField(name, typeOfNewType);
//                if (generateLabel) {
//                	typeOfNewType.asAtr().label(StringUtils.capitalize(name));
//                }
//                readXsd(typeOfNewType, element);
//            }
//
//        } else {
//            element.checkUnexpectedNodeFor(typeContext);
//            return;
//        }
//        readXsdOwnAttributes(element, newType);
    }
    
    private static SType<?> detectType(ElementReader element, ElementReader root) {
    	String xsdTypeName = element.getAttr("type");
    	if (!StringUtils.isBlank(xsdTypeName)) {
//            return detectType(element, xsdTypeName);
    		XsdContext xsdContext = element.getXsdContext();
    		if (xsdContext.isXsdType(xsdTypeName)) {
    			String name = getTypeNameWithoutNamespace(xsdTypeName);
    			Class<? extends SType<?>> type = getMapping().findSType(name);
    			if (type != null) {
    				return xsdContext.getType(type);
    			}
    		} else {
    			STypeComposite<?> type = element.getPkg().createCompositeType(xsdTypeName);
    			addAttributes(type, root);
    			return type;
//            	detectComplexType(element);
    		}
    	} else {
    		Optional<ElementReader> complexTypeChildrenOpt = element.streamChildren().filter(ElementReader::isTagComplexType).findFirst();
    		if (complexTypeChildrenOpt.isPresent()) {
    			ElementReader complexTypeChildren = complexTypeChildrenOpt.get();

    			if (isList(complexTypeChildren)) {
    				return element.getXsdContext().getType(STypeList.class);
    			}
    		}
    		throw new SingularFormException(element.errorMsg("Não preparado para detectar o tipo"));
    	}
    	throw new SingularFormException(element.errorMsg("Não preparado para tratar o tipo '" + xsdTypeName + "'"));      
    }
    
    private static String getTypeNameWithoutNamespace(String name) {
        int pos = name.indexOf(':');
        return pos == -1 ? name : name.substring(pos + 1);
    }
    
    private static XsdTypeMapping getMapping() {
        if (typeMapping == null) {
            typeMapping = new XsdTypeMapping();
        }
        return typeMapping;
    }
    
    private static <T extends STypeComposite<?>> void addAttributes(T type, ElementReader root) {
    	ElementReader tagComplexType = findNextComplexTypeWithAttrName(root, type.getNameSimple());
    	ElementReader tagSequence = tagComplexType.streamChildren().findFirst().get();
    	
    	findComplexTypeElements(tagSequence).forEach(tagElement -> {
    		SType<?> sType = detectType(tagElement, root);
    		if (sType.isList()) {
    			type.addFieldListOf(tagElement.getAttr("name"), detectTypeOfListElement(tagElement, root));
    		} else {
    			type.addField(tagElement.getAttr("name"), sType);
    		}
    	});
    }
    
    private static ElementReader findNextComplexTypeWithAttrName(ElementReader root, String name) {
    	return root
    			.streamChildren()
    			.filter(ElementReader::isTagComplexType)
    			.filter(tagElement -> tagElement.getAttr("name").equals(name))
    			.findFirst()
    			.orElseThrow(() -> new SingularFormException(root.errorMsg(" Could no get the underlying complex type")));
    }
    
    private static List<ElementReader> findComplexTypeElements(ElementReader tag) {
    	List<ElementReader> elements = new ArrayList<>();
    	tag.streamChildren()
			.filter(ElementReader::isTagXsdElement)
			.forEach(tagElement -> elements.add(tagElement));
    			
    	return elements;
    }
    
    private static SType<?> detectTypeOfListElement(ElementReader element, ElementReader root) {
    	Optional<ElementReader> complexTypeChildrenOpt = element.streamChildren().filter(ElementReader::isTagComplexType).findFirst();
        if (complexTypeChildrenOpt.isPresent()) {
            ElementReader complexTypeChildren = complexTypeChildrenOpt.get();

            if (isList(complexTypeChildren)) {
            	ElementReader listElement = lookAheadForListElementType(complexTypeChildren).get();
          	  	SType<?> listElementType = detectType(listElement, root);
          	  	
          	  	return listElementType;
            }
        }
        
        throw new SingularFormException(element.errorMsg("Não preparado para detectar o tipo"));
    
    }
    
    private static boolean isList(ElementReader complexTypeElement) {
        if (!complexTypeElement.isTagComplexType()) {
            throw new SingularFormException(complexTypeElement.errorMsg(" this type is not a complex type, therefore we can not look ahead for list pattern"));
        }
        return lookAheadForListElementType(complexTypeElement).isPresent();
    }

    private static Optional<ElementReader> lookAheadForListElementType(ElementReader complexTypeElement) {
        if (!complexTypeElement.isTagComplexType()) {
            throw new SingularFormException(complexTypeElement.errorMsg(" this type is not a complex type, therefore we can not look ahead for list pattern"));
        }
        List<ElementReader> typeList = complexTypeElement
                .streamChildren()
                .filter(ElementReader::isTagSequence)
                .flatMap(ElementReader::streamChildren)
                .filter(ElementReader::isTagXsdElement)
                .filter(tagElement -> tagElement.getAttrMaxOccurs() > 1).collect(Collectors.toList());
        if (typeList.size() > 1) {
            throw new SingularFormException(complexTypeElement.errorMsg(" this type should not have two childrens "));
        } else {
            return typeList.stream().findFirst();
        }
    }

//    private static ElementReader findNextComplexType(ElementReader listElementType) {
//        return listElementType
//                .streamChildren()
//                .filter(ElementReader::isTagComplexType)
//                .findFirst()
//                .orElseThrow(() -> new SingularFormException(listElementType.errorMsg(" Could no get the underlying complex type")));
//    } 

//    private static ElementReader findListElement(ElementReader element) {
//        return element
//                .streamChildren()
//                .filter(ElementReader::isTagComplexType)
//                .findFirst()
//                .map(XSDConverter::lookAheadForListElementType)
//                .map(Optional::get)
//                .orElseThrow(() -> new SingularFormException(element.errorMsg(" Could not identify the list elements type ")));
//    }
//
//    private static void readXsdAtributeDefinition(ElementReader element, SType<?> typeContext) {
//        if (!typeContext.isComposite()) {
//            element.checkUnexpectedNodeFor(typeContext);
//            return;
//        }
//        String   name          = element.getAttrRequired("name");
//        SType<?> typeOfNewType = detectType(element, element.getAttrRequired("type"));
//        SType<?> newType       = ((STypeComposite) typeContext).addField(name, typeOfNewType);
//        readXsdOwnAttributes(element, newType);
//    }
//
//    private static void readXsdOwnAttributes(ElementReader element, SType<?> newType) {
//        if (element.isTagAttribute()) {
//            String value = element.getAttr("use");
//            if (value != null) {
//                newType.asAtr().required("required".equals(value));
//            }
//        } else {
//            readXsdOwnAttributeMinOccurs(element, newType);
//            readXsdOwnAttributeMaxOccurs(element, newType);
//        }
//    }
//
//    private static void readXsdOwnAttributeMinOccurs(ElementReader element, SType<?> newType) {
//        Integer minOccurs = element.getAttrInteger("minOccurs");
//        if (minOccurs == null || minOccurs == 1) {
//            newType.asAtr().required();
//            if (newType.isList()) {
//                ((STypeList) newType).withMiniumSizeOf(1);
//            }
//        } else if (minOccurs.intValue() > 1) {
//            if (newType.isList()) {
//                ((STypeList) newType).withMiniumSizeOf(minOccurs);
//            } else {
//                throw new SingularFormException(element.errorMsgInvalidAttribute("minOccurs"), newType);
//            }
//        }
//    }
//
//    private static void readXsdOwnAttributeMaxOccurs(ElementReader element, SType<?> newType) {
//        String value = element.getAttr("maxOccurs");
//        if ("unbounded".equalsIgnoreCase(value)) {
//            if (!newType.isList()) {
//                throw new SingularFormException(element.errorMsgInvalidAttribute("maxOccurs"), newType);
//            }
//        } else if (value != null) {
//            int maxOccurs = Integer.parseInt(value);
//            if (newType.isList()) {
//                ((STypeList) newType).withMaximumSizeOf(maxOccurs);
//            } else if (maxOccurs != 1) {
//                throw new SingularFormException(element.errorMsgInvalidAttribute("maxOccurs"), newType);
//            }
//        }
//    }
//    
//    private static SType<?> detectComplexType(ElementReader element) {
//    	Optional<ElementReader> complexTypeChildrenOpt = element.streamChildren().filter(ElementReader::isTagComplexType).findFirst();
//        if (complexTypeChildrenOpt.isPresent()) {
//            ElementReader complexTypeChildren = complexTypeChildrenOpt.get();
//            if (isList(complexTypeChildren)) {
//                return element.getXsdContext().getType(STypeList.class);
//            } else {
//                return element.getXsdContext().getType(STypeComposite.class);
//            }
//        }
//        throw new SingularFormException(element.errorMsg("Não preparado para detectar o tipo"));
//    }
//
//    /**
//     * Look ahead of the current complexTypeElement in search of a list pattern like the xsd excerpt below:
//     * <p>
//     * &lt;xs:element name=&quot;documentos&quot;&gt;
//     * &lt;xs:complexType&gt;
//     * &lt;xs:sequence&gt;
//     * &lt;xs:element maxOccurs=&quot;10&quot; minOccurs=&quot;0&quot; name=&quot;documento&quot;&gt;
//     * &lt;xs:complexType&gt;
//     * &lt;xs:sequence minOccurs=&quot;0&quot;&gt;
//     * &lt;xs:element minOccurs=&quot;0&quot; name=&quot;fileId&quot; type=&quot;xs:string&quot;/&gt;
//     * &lt;xs:element minOccurs=&quot;0&quot; name=&quot;name&quot; type=&quot;xs:string&quot;/&gt;
//     * &lt;xs:element minOccurs=&quot;0&quot; name=&quot;hashSHA1&quot; type=&quot;xs:string&quot;/&gt;
//     * &lt;xs:element minOccurs=&quot;0&quot; name=&quot;fileSize&quot; type=&quot;xs:integer&quot;/&gt;
//     * &lt;/xs:sequence&gt;
//     * &lt;/xs:complexType&gt;
//     * &lt;/xs:element&gt;
//     * &lt;/xs:sequence&gt;
//     * &lt;/xs:complexType&gt;
//     * &lt;/xs:element&gt;
//     * <p>
//     * the xsd above represent a list of attachment called 'documentos' whose each element is called 'documento'
//     *
//     * @param complexTypeElement
//     * @return
//     */   
//
//    private static SType<?> detectType(ElementReader node, String xsdTypeName) {
//        XsdContext xsdContext = node.getXsdContext();
//        if (xsdContext.isXsdType(xsdTypeName)) {
//            String                    name = getTypeNameWithoutNamespace(xsdTypeName);
//            Class<? extends SType<?>> type = getMapping().findSType(name);
//            if (type != null) {
//                return xsdContext.getType(type);
//            }
//        } 
////        else {
////        	PackageBuilder sPackage = node.getPkg();
////        	Class<? extends STypeComposite<?>>type  = (Class<? extends STypeComposite<?>>) sPackage.createCompositeType(xsdTypeName).getClass();
////        	return xsdContext.getType(type);
////        }
//        throw new SingularFormException(node.errorMsg("Não preparado para tratar o tipo '" + xsdTypeName + "'"));
//    }
//
    

    private static class XsdContext {

        private final PackageBuilder pkg;

        private XsdContext(PackageBuilder pkg) {
            this.pkg = pkg;
        }

        private boolean isXsdNamespace(Node node) {
            return XSD_NAMESPACE_URI.equals(node.getNamespaceURI());
        }

        public boolean isNodeXsd(Node node, String expectedName) {
            return isXsdNamespace(node) && isNodeNameEqualsWithoutNamespace(node.getNodeName(), expectedName);
        }

        private boolean isNodeNameEqualsWithoutNamespace(String nodeName, String expectedName) {
            if (expectedName == null) {
                return false;
            }
            int pos = nodeName.indexOf(':');
            if (pos == -1) {
                return nodeName.equals(expectedName);
            }
            return (nodeName.length() - pos - 1 == expectedName.length()) && nodeName.startsWith(expectedName, pos + 1);
        }

        public boolean isXsdType(String xsdTypeName) {
            return xsdTypeName.startsWith("xs:");
        }

        public <T extends SType<?>> T getType(Class<T> sTypeClass) {
            return pkg.getType(sTypeClass);
        }

        public PackageBuilder getPkg() {
            return pkg;
        }
    }

    private static class ElementReader implements Iterable<ElementReader> {

        private final XsdContext xsdContext;
        private final MElement   element;

        private ElementReader(XsdContext xsdContext, MElement element) {
            this.xsdContext = xsdContext;
            this.element = element;
        }

        public boolean isTagXsdSchema() {
            return xsdContext.isNodeXsd(element, "schema");
        }

        public boolean isTagXsdElement() {
            return xsdContext.isNodeXsd(element, "element");
        }

        public boolean isTagComplexType() {
            return xsdContext.isNodeXsd(element, "complexType");
        }

        public boolean isTagSequence() {
            return xsdContext.isNodeXsd(element, "sequence");
        }

        public boolean isTagAttribute() {
            return xsdContext.isNodeXsd(element, "attribute");
        }

        public String getNodeName() {
            return element.getNodeName();
        }

        @Override
        public Iterator<ElementReader> iterator() {
            return new Iterator<ElementReader>() {
                private MElement current = element.getPrimeiroFilho();

                @Override
                public boolean hasNext() {
                    return current != null;
                }

                @Override
                public ElementReader next() {
                    if (current == null) {
                        throw new NoSuchElementException();
                    }
                    ElementReader result = new ElementReader(xsdContext, current);
                    current = current.getProximoIrmao();
                    return result;
                }
            };
        }

        public String getFullPath() {
            return element.getFullPath();
        }

        public void checkUnknownNodeTreatment() {
            throw new SingularFormException(
                    "Node '" + getFullPath() + "' não esperado ou tratamento de leitura não implementado");
        }

        public void checkUnexpectedNodeFor(SType<?> typeContext) {
            throw new SingularFormException(
                    "Não era esperada o nó " + element.getFullPath() + " para o tipo " + typeContext.getName());
        }

        public String getAttrRequired(String attributeName) {
            String attr = getAttr(attributeName);
            if (attr == null) {
                throw new SingularFormException(
                        "Era esperado o atributo '" + attributeName + "' em " + element.getFullPath());
            }
            return attr;
        }

        public String getAttr(String attributeName) {
            return StringUtils.trimToNull(element.getAttribute(attributeName));
        }

        public Integer getAttrInteger(String attributeName) {
            String value = getAttr(attributeName);
            return value == null ? null : Integer.valueOf(value);
        }

        public int getAttrMaxOccurs() {
            String value = getAttr("maxOccurs");
            if (value == null) {
                return 1;
            } else if ("unbounded".equalsIgnoreCase(value)) {
                return Integer.MAX_VALUE;
            }
            return Integer.parseInt(value);
        }

        public XsdContext getXsdContext() {
            return xsdContext;
        }

        public String errorMsg(String msg) {
            return "Erro processando nó XML '" + getFullPath() + "': " + msg;
        }

        public String errorMsgInvalidAttribute(String attrName) {
            return errorMsg("Valor inválido para o atributo " + attrName + "='" + getAttr(attrName) + "'");
        }

        public PackageBuilder getPkg() {
            return getXsdContext().getPkg();
        }

        public Stream<ElementReader> streamChildren() {
            return StreamSupport
                    .stream(Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED & Spliterator.NONNULL),
                            false);
        }
    }

}
