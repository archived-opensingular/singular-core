package org.opensingular.form.io;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
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
import org.opensingular.internal.lib.commons.xml.MElement;
import org.opensingular.internal.lib.commons.xml.MParser;
import org.w3c.dom.Node;

/**
 * 
 * @author Thais N. Pereira
 *
 */
public class XSDConverter {
	
	public static final String XSD_SINGULAR_NAMESPACE_URI = "http://opensingular.org/FormSchema";
    public static final String XSD_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema";
    public static final String XSD_NAMESPACE_PREFIX = "xs";
    private static final String XSD_SCHEMA = XSD_NAMESPACE_PREFIX + ":schema";
    private static final String XSD_ELEMENT = XSD_NAMESPACE_PREFIX + ":element";
    private static final String XSD_COMPLEX_TYPE = XSD_NAMESPACE_PREFIX + ":complexType";
    private static final String XSD_SEQUENCE = XSD_NAMESPACE_PREFIX + ":sequence";
    
    private static XsdTypeMapping typeMapping;
    
    private static Map<String, STypeComposite<?>> mapOfComplexType = new HashMap<>();
    
    private XSDConverter() {
    	
    }
    
    private static XsdTypeMapping getMapping() {
        if (typeMapping == null) {
            typeMapping = new XsdTypeMapping();
        }
        return typeMapping;
    }
	
    public static MElement toXsd(SType<?> sType) {
    	MElement root = MElement.newInstance(XSD_NAMESPACE_URI, XSD_SCHEMA);

		MElement element = root.addElementNS(XSD_NAMESPACE_URI, XSD_ELEMENT);
		element.setAttribute("name", sType.getNameSimple());
		element.setAttribute("type", sType.getNameSimple());
		
		mapOfComplexType.clear();
		
    	toXsdFromSType(sType, root);
    	
    	return root;
    }
	
	private static void toXsdFromSType(SType<?> sType, MElement parent) {
		if (sType.isList()) {
			Collection<SType<?>> attributesType = toXsdFromList((STypeList<?, ?>) sType, parent);
			attributesType.forEach(type -> toXsdFromSType(type, parent));
		} else if (sType.isComposite()) {
			Collection<SType<?>> attributesType = toXsdFromComposite((STypeComposite<?>) sType, parent);
			attributesType.forEach(type -> toXsdFromSType(type, parent));
		}
	}

	private static Collection<SType<?>> toXsdFromList(STypeList<?, ?> sType, MElement parent) {
		Collection<SType<?>> attributesType = new ArrayList<>();
		
		if (sType.getElementsType().isComposite()) {
			attributesType = toXsdFromComposite((STypeComposite<?>) sType.getElementsType(), parent);
		}
		
		return attributesType;
	}

	private static Collection<SType<?>> toXsdFromComposite(STypeComposite<?> sType, MElement parent) {
		
		String name = getTypeName(sType);
		
		if (!sType.isTypeOf(mapOfComplexType.get(name))) {
			
			MElement element = parent.addElementNS(XSD_NAMESPACE_URI, XSD_COMPLEX_TYPE);
	
			element.setAttribute("name", name);
			element = element.addElementNS(XSD_NAMESPACE_URI, XSD_SEQUENCE);
			
			for (SType<?> child : sType.getFields()) {
				addXsdElement(child, element);
			}
		}
		
		if (!mapOfComplexType.containsKey(name)) {
			insertIntoMapOfComplexTypes(name, sType);
		}
		
		return sType.getFields();
	}
	
	private static String getTypeName(SType<?> sType) {

		try {
			return getSimpleTypeName(sType);
		} catch (SingularFormException e) {
			if (sType instanceof STypeSimple<?, ?>) {
				return getTypeName(sType.getSuperType());
			} else if (sType.isComposite()) {
				return getComplexTypeName(sType);
			}
			
			throw new SingularFormException("Could not identify the type");
		}
	}
	
	private static String getSimpleTypeName(SType<?> sType) {
		return XSD_NAMESPACE_PREFIX + ":" + getMapping().findXsdType(sType);
	}
	
	private static String getComplexTypeName(SType<?> sType) {
		String sTypeCompositeName = STypeComposite.class.getSimpleName();
		String name = sType.getNameSimple();
		for (SType<?> s = sType; !s.getSuperType().getNameSimple().equals(sTypeCompositeName); s = s.getSuperType()) {
			name = s.getSuperType().getNameSimple();
		}
		
		return name;
	}
	
	private static MElement addXsdElement(SType<?> sType, MElement parent) {
		MElement element = parent.addElementNS(XSD_NAMESPACE_URI, XSD_ELEMENT);
		
		if (sType.isList()) {
			setXsdListElementDefinition(element, sType);
			
		} else {
			element.setAttribute("name", sType.getNameSimple());
			element.setAttribute("type", getTypeName(sType));
		}
		
		return element;
	}
	
	private static void setXsdListElementDefinition(MElement element, SType<?> sType) {
		element.setAttribute("name", sType.getNameSimple());
		element = element.addElementNS(XSD_NAMESPACE_URI, XSD_COMPLEX_TYPE);
		element = element.addElementNS(XSD_NAMESPACE_URI, XSD_SEQUENCE);
		element = element.addElementNS(XSD_NAMESPACE_URI, XSD_ELEMENT);
		element.setAttribute("name", ((STypeList<?, ?>) sType).getElementsType().getNameSimple());
		element.setAttribute("type", getTypeName(((STypeList<?, ?>) sType).getElementsType()));
		setMinAndMaxOccursOfXsdListElement(element, (STypeList<?, ?>) sType);
	}
	
	private static void setMinAndMaxOccursOfXsdListElement(MElement element, STypeList<?, ?> sType) {
		Integer min = sType.getMinimumSize();
        if (min != null) {
            element.setAttribute("minOccurs", min.toString());
        }
        Integer max = sType.getMaximumSize();
        if (max == null) {
            element.setAttribute("maxOccurs", "unbounded");
        } else {
            element.setAttribute("maxOccurs", max.toString());
        }
	}
	
	private static void insertIntoMapOfComplexTypes(String name, STypeComposite<?> sType) {
		String sTypeCompositeName = STypeComposite.class.getSimpleName();
		SType<?> s = sType;
		
		while (!s.getSuperType().getNameSimple().equals(sTypeCompositeName)) {
			s = s.getSuperType();
		}

		mapOfComplexType.put(name, (STypeComposite<?>) s);
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
        
        mapOfComplexType.clear();

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
            	detectType(element, root);
            } 
        }
    }

    private static SType<?> detectType(ElementReader element, ElementReader root) {
    	
    	String xsdTypeName = element.getAttr("type");
    	
    	if (!StringUtils.isBlank(xsdTypeName)) {
    		XsdContext xsdContext = element.getXsdContext();
    		if (xsdContext.isXsdType(xsdTypeName)) {
    			String name = getTypeNameWithoutNamespace(xsdTypeName);
    			Class<? extends SType<?>> type = getMapping().findSType(name);
    			if (type != null) {
    				return xsdContext.getType(type);
    			}
    		} else {
    			STypeComposite<?> type = null; 
    			if (!mapOfComplexType.containsKey(xsdTypeName)) {
	    			type = element.getPkg().createCompositeType(xsdTypeName);
	    			addAttributes(type, root);
	    			insertIntoMapOfComplexTypes(xsdTypeName, type);
    			} else {
    				type = mapOfComplexType.get(xsdTypeName);
    			}
    			
    			return type;
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
    			.filter(tagComplexType -> tagComplexType.getAttr("name").equals(name))
    			.findFirst()
    			.orElseThrow(() -> new SingularFormException(root.errorMsg(" Could not get the underlying complex type")));
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
