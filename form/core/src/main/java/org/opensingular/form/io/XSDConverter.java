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

package org.opensingular.form.io;

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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

/**
 * @author Thais N. Pereira
 */
public class XSDConverter {

    public static final  String XSD_SINGULAR_NAMESPACE_URI = "http://opensingular.org/FormSchema";
    public static final  String XSD_NAMESPACE_URI          = "http://www.w3.org/2001/XMLSchema";
    public static final  String XSD_NAMESPACE_PREFIX       = "xs";
    private static final String XSD_SCHEMA                 = XSD_NAMESPACE_PREFIX + ":schema";
    private static final String XSD_ELEMENT                = XSD_NAMESPACE_PREFIX + ":element";
    private static final String XSD_COMPLEX_TYPE           = XSD_NAMESPACE_PREFIX + ":complexType";
    private static final String XSD_SEQUENCE               = XSD_NAMESPACE_PREFIX + ":sequence";

    private static MElement root;

    private static XsdTypeMapping typeMapping;

    private static Map<String, STypeComposite<?>> mapOfComplexType = new HashMap<>();
    private static Map<STypeComposite<?>, String> mapOfComposite   = new HashMap<>();

    private XSDConverter() {

    }

    /**
     * Instantiates (if it's not instantiated yet) and returns the XsdTypeMapping
     * @return The XsdTypeMapping
     */
    private static XsdTypeMapping getMapping() {
        if (typeMapping == null) {
            typeMapping = new XsdTypeMapping();
        }
        return typeMapping;
    }

    /**
     * Converts a SType to XSD
     * @param sType The SType that will be converted
     * @return The MElement that represents the root of the XSD with all it's children
     */
    public static MElement toXsd(SType<?> sType) {
        root = MElement.newInstance(XSD_NAMESPACE_URI, XSD_SCHEMA);

        MElement firstElement = addXsdElement(sType, root);

        mapOfComplexType.clear();
        mapOfComposite.clear();

        createMapOfComposite(sType);

        toXsdFromSType(sType, firstElement);

        return root;
    }
    
    /**
     * Inserts the tag element on the XSD
     * @param sType The SType that will be represented by the tag element
     * @param parent The parent tag of the element
     * @return The tag element that was included on the XSD
     */
    private static MElement addXsdElement(SType<?> sType, MElement parent) {
        MElement element = parent.addElementNS(XSD_NAMESPACE_URI, XSD_ELEMENT);

        if (sType.isList()) {
            return setXsdListElementDefinition(element, sType);

        } else {
            String typeName = getTypeName(sType);

            element.setAttribute("name", sType.getNameSimple());

            if (Collections.frequency(mapOfComposite.values(), typeName) > 1 || sType instanceof STypeSimple<?, ?>) {
                element.setAttribute("type", typeName);
            }

            if (sType instanceof STypeSimple<?, ?> && (!sType.isRequired() && !XSD_SCHEMA.equals(parent.getNodeName()))) {
                element.setAttribute("minOccurs", "0");
            }
        }

        return element;
    }

    /**
     * Creates a map of all the STypeComposite that is part of the SType that is being converted
     * @param sType The SType that will be inserted on the map
     */
    private static void createMapOfComposite(SType<?> sType) {

        if (sType.isList()) {
            STypeList<?, ?> sTypeList = (STypeList<?, ?>) sType;
            insertIntoMapOfComposite(sTypeList.getElementsType());
            createMapOfComposite(sTypeList.getElementsType());
        } else if (sType.isComposite()) {
            STypeComposite<?> sTypeComposite = (STypeComposite<?>) sType;
            if (mapOfComposite.isEmpty()) {
                insertIntoMapOfComposite(sTypeComposite);
            }
            Collection<SType<?>> collection = sTypeComposite.getFields();
            collection.forEach(type -> {
                insertIntoMapOfComposite(type);
                createMapOfComposite(type);
            });
        }
    }
    
    /**
     * Checks if the SType is a list or a composite and calls the right method to include the complex element on XSD
     * @param sType The SType that will be inserted on the XSD
     * @param element The parent element 
     */
    private static void toXsdFromSType(SType<?> sType, MElement element) {
        if (sType.isList()) {
            toXsdFromList((STypeList<?, ?>) sType, element);
        } else if (sType.isComposite()) {
            toXsdFromComposite((STypeComposite<?>) sType, element);
        }
    }
    
    /**
     * Inserts a list complex element on the XSD
     * @param element The parent element 
     * @param sType The SType that will be inserted on the XSD
     * @return The tag element with its complex type children
     */
    private static MElement setXsdListElementDefinition(MElement element, SType<?> sType) {
    	MElement tagElement = element;
    	tagElement.setAttribute("name", sType.getNameSimple());
    	tagElement = tagElement.addElementNS(XSD_NAMESPACE_URI, XSD_COMPLEX_TYPE);
    	tagElement = tagElement.addElementNS(XSD_NAMESPACE_URI, XSD_SEQUENCE);
    	tagElement = addXsdElement(((STypeList<?, ?>) sType).getElementsType(), tagElement);
        setMinAndMaxOccursOfXsdListElement(tagElement, (STypeList<?, ?>) sType);

        return tagElement;
    }
    
    /**
     * Checks if the SType is a STypeSimple or a STypeComposite and calls the method that will get the name of the SType
     * @param sType
     * @return The name of the SType
     */
    private static String getTypeName(SType<?> sType) {

        try {
            return getSimpleTypeName(sType);
        } catch (SingularFormException e) {
            if (sType instanceof STypeSimple<?, ?>) {
                return getTypeName(sType.getSuperType());
            } else if (sType.isComposite()) {
                return getComplexTypeName(sType);
            }

            throw new SingularFormException("Could not identify the type", e);
        }
    }

    /**
     * Inserts a new element to the map of STypeComposite
     * @param sType The SType that will be inserted on the map
     */
    private static void insertIntoMapOfComposite(SType<?> sType) {
        if (sType.isComposite()) {
        	mapOfComposite.put((STypeComposite<?>) sType, getComplexTypeName(sType));
        }
    }

    /**
     * Gets the list type and calls the right method to include the element on XSD
     * @param sType The SType that will be inserted on the XSD
     * @param element The parent element 
     */
    private static void toXsdFromList(STypeList<?, ?> sType, MElement element) {
        if (sType.getElementsType().isComposite()) {
            toXsdFromComposite((STypeComposite<?>) sType.getElementsType(), element);
        }
    }

    /**
     * Checks if the STypeComposite appears one or more times on the mapOfComposite and calls the right method to include the element on XSD
     * @param sType The SType that will be inserted on the XSD
     * @param element The parent element 
     */
    private static void toXsdFromComposite(STypeComposite<?> sType, MElement element) {

        String name = getTypeName(sType);

        if (Collections.frequency(mapOfComposite.values(), name) > 1) {
            addNotUniqueComplexType(sType, name);
        } else {
            addUniqueComplexType(sType, element);
        }
    }

    /**
     * Sets the attributes minOccurs and maxOccurs of the tag element
     * @param element The tag element which the attributes will be added to
     * @param sType The SType represented by the tag element
     */
    private static void setMinAndMaxOccursOfXsdListElement(MElement element, STypeList<?, ?> sType) {
        Integer min = Optional.ofNullable(sType.getMinimumSize()).orElse(0);
        if (min != 1) {
            element.setAttribute("minOccurs", min.toString());
        }
        Integer max = sType.getMaximumSize();
        if (max == null) {
            element.setAttribute("maxOccurs", "unbounded");
        } else if (max != 1) {
            element.setAttribute("maxOccurs", max.toString());
        }
    }

    /**
     * Returns the name of the STypeSimple with the namespace prefix
     * @param sType
     * @return The name of the STypeSimple
     */
    private static String getSimpleTypeName(SType<?> sType) {
        return XSD_NAMESPACE_PREFIX + ":" + getMapping().findXsdType(sType);
    }

    /**
     * Returns the name of the STypeComposite
     * @param sType
     * @return The name of the STypeComposite
     */
    private static String getComplexTypeName(SType<?> sType) {
        String sTypeCompositeName = STypeComposite.class.getSimpleName();
        String name               = sType.getNameSimple();
        for (SType<?> s = sType; !s.getSuperType().getNameSimple().equals(sTypeCompositeName); s = s.getSuperType()) { //NOSONAR
            name = s.getSuperType().getNameSimple();
        }

        return name;
    }

    /**
     * Inserts an complex element of the STypeComposite on the XSD.
     * Since the STypeComposite appears more than once, the ComplexType is included separately so that it can be reused for other elements.
     * @param sType The SType that will be inserted on the XSD
     * @param name The name of the ComplexType
     */
    private static void addNotUniqueComplexType(STypeComposite<?> sType, String name) {
        if (!sType.isTypeOf(mapOfComplexType.get(name))) {

            MElement element = root.addElementNS(XSD_NAMESPACE_URI, XSD_COMPLEX_TYPE);

            element.setAttribute("name", name);
            element = element.addElementNS(XSD_NAMESPACE_URI, XSD_SEQUENCE);

            addComplexTypeElements(sType, element);
        }

        if (!mapOfComplexType.containsKey(name)) {
            insertIntoMapOfComplexTypes(name, sType);
        }
    }

    /**
     * Inserts an complex element of the STypeComposite on the XSD.
     * Since the STypeComposite appears just once, the ComplexType is included together with the element.
     * @param sType The SType that will be inserted on the XSD
     * @param parentElement The parentElement that represents the tag element
     */
    private static void addUniqueComplexType(STypeComposite<?> sType, MElement parentElement) {
        MElement element = parentElement.addElementNS(XSD_NAMESPACE_URI, XSD_COMPLEX_TYPE);
        element = element.addElementNS(XSD_NAMESPACE_URI, XSD_SEQUENCE);

        addComplexTypeElements(sType, element);
    }

    /**
     * Inserts the fields types of the parent SType as child elements of the ComplexType
     * @param sType The parent SType
     * @param parentComplexType The parent ComplexType tag
     */
    private static void addComplexTypeElements(STypeComposite<?> sType, MElement parentComplexType) {
        for (SType<?> type : sType.getFields()) {
            MElement element = addXsdElement(type, parentComplexType);
            toXsdFromSType(type, element);
        }
    }

    /**
     * Inserts the complex type into a map
     * @param name The name of the complex type
     * @param sType The SType represented by the complex type
     */
    private static void insertIntoMapOfComplexTypes(String name, STypeComposite<?> sType) {
        String   sTypeCompositeName = STypeComposite.class.getSimpleName();
        SType<?> s                  = sType;

        while (!s.getSuperType().getNameSimple().equals(sTypeCompositeName)) {
            s = s.getSuperType();
        }

        mapOfComplexType.put(name, (STypeComposite<?>) s);
    }

    /**
     * Converts a XSD to SType
     * @param packageForNewTypes The PackageBuilder of the SType
     * @param in The InputStream with the XSD document
     * @return The SType
     */
    public static SType<?> xsdToSType(PackageBuilder packageForNewTypes, InputStream in) {
        MElement xsdDefinition;
        try {
            xsdDefinition = MParser.parse(in, true, false);
        } catch (Exception e) {
            throw new SingularFormException("Erro lendo xml (parse)", e);
        }
        return xsdToSType(packageForNewTypes, xsdDefinition);
    }

    /**
     * Converts a XSD to SType
     * @param packageForNewTypes The PackageBuilder of the SType
     * @param xsdDefinition The String with the XSD
     * @return The SType
     */
    public static SType<?> xsdToSType(PackageBuilder packageForNewTypes, String xsdDefinition) {
        return xsdToSType(packageForNewTypes, SFormXMLUtil.parseXml(xsdDefinition));
    }

    /**
     * Converts a XSD to SType
     * @param packageForNewTypes The PackageBuilder of the SType
     * @param root The MElement that is the root of the XSD
     * @return The SType
     */
    private static SType<?> xsdToSType(PackageBuilder packageForNewTypes, MElement root) {

        XsdContext    xsdContext = new XsdContext(packageForNewTypes);
        ElementReader element    = new ElementReader(xsdContext, root);

        mapOfComplexType.clear();

        if (!element.isTagXsdSchema()) {
            throw new SingularFormException(
                    "O XSD não é válido: a tag raiz é '" + element.getNodeName() + "' e deveria ser 'xs:schema'");
        }

        readXsd(element);

        Collection<SType<?>> types = packageForNewTypes.getPackage().getLocalTypes();
        return !types.isEmpty() ? types.iterator().next() : null;
    }

    /**
     * Reads all the element tags on the XSD
     * @param root The root tag of the XSD
     */
    private static void readXsd(ElementReader root) {
        for (ElementReader element : root) {
            if (element.isTagXsdElement()) {
                SType<?> sType = detectType(element, root);
                readXsdOwnAttributeMinOccurs(element, sType);
                readXsdOwnAttributeMaxOccurs(element, sType);
            }
        }
    }

    /**
     * Returns the SType that is equivalent to the given element tag
     * @param element The element tag
     * @param root The root tag of the XSD
     * @return The SType that is equivalent to the given element tag
     */
    private static SType<?> detectType(ElementReader element, ElementReader root) {

        String xsdTypeName = element.getAttr("type");

        if (!StringUtils.isBlank(xsdTypeName)) {
            XsdContext xsdContext = element.getXsdContext();
            if (xsdContext.isXsdType(xsdTypeName)) {
                String                    name = getTypeNameWithoutNamespace(xsdTypeName);
                Class<? extends SType<?>> type = getMapping().findSType(name);
                if (type != null) {
                    SType<?> sType = xsdContext.getType(type);
                    return sType;
                }
            } else {
                STypeComposite<?> sType;
                if (!mapOfComplexType.containsKey(xsdTypeName)) {
                    sType = element.getPkg().createCompositeType(xsdTypeName);
                    addAttributes(sType, root, null, xsdTypeName);
                    insertIntoMapOfComplexTypes(xsdTypeName, sType);
                } else {
                    sType = mapOfComplexType.get(xsdTypeName);
                }
                readXsdOwnAttributeMinOccurs(element, sType);
                readXsdOwnAttributeMaxOccurs(element, sType);
                return sType;
            }
        } else {
            Optional<ElementReader> complexTypeChildrenOpt = element.streamChildren().filter(ElementReader::isTagComplexType).findFirst();
            if (complexTypeChildrenOpt.isPresent()) {
                ElementReader complexTypeChildren = complexTypeChildrenOpt.get();

                if (isList(complexTypeChildren)) {
                    return element.getXsdContext().getType(STypeList.class);
                } else {
                    STypeComposite<?> newType = element.getPkg().createCompositeType(element.getAttr("name"));
                    addAttributes(newType, root, complexTypeChildren, xsdTypeName);

                    readXsdOwnAttributeMinOccurs(element, newType);
                    readXsdOwnAttributeMaxOccurs(element, newType);
                    return newType;
                }
            }
            throw new SingularFormException(element.errorMsg("Não preparado para detectar o tipo"));
        }
        throw new SingularFormException(element.errorMsg("Não preparado para tratar o tipo '" + xsdTypeName + "'"));
    }
    
    /**
     * Reads the attribute minOccurs of the element tag and configures the SType with its minimum size
     * @param element The element tag
     * @param newType The SType that is represented by the element tag
     */
    private static void readXsdOwnAttributeMinOccurs(ElementReader element, SType<?> newType) {
        Integer minOccurs = element.getAttrInteger("minOccurs");
        if (minOccurs == null || minOccurs == 1) {
            newType.asAtr().required();
            if (newType.isList()) {
                ((STypeList<?, ?>) newType).withMiniumSizeOf(1);
            }
        } else if (minOccurs.intValue() > 1) {
            if (newType.isList()) {
                ((STypeList<?, ?>) newType).withMiniumSizeOf(minOccurs);
            } else {
                throw new SingularFormException(element.errorMsgInvalidAttribute("minOccurs"), newType);
            }
        }
    }

    /**
     * Reads the attribute maxOccurs of the element tag and configures the SType with its maximum size
     * @param element The element tag
     * @param newType The SType that is represented by the element tag
     */
    private static void readXsdOwnAttributeMaxOccurs(ElementReader element, SType<?> newType) {
        String value = element.getAttr("maxOccurs");
        if (value != null && !"unbounded".equalsIgnoreCase(value)) {
            int maxOccurs = Integer.parseInt(value);
            if (newType.isList()) {
                ((STypeList<?, ?>) newType).withMaximumSizeOf(maxOccurs);
            } 
        }
    }

    /**
     * Returns the name attribute of an element tag without the namespace
     * @param name The name attribute of the element tag
     * @return The name attribute of an element tag without the namespace
     */
    private static String getTypeNameWithoutNamespace(String name) {
        int pos = name.indexOf(':');
        return pos == -1 ? name : name.substring(pos + 1);
    }

    /**
     * Reads the element tag of the XSD document and add attributes (fields) to the given SType.
     * @param type The SType which the fields will be add to.
     * @param root The parent of all elements on the XSD.
     * @param complexType The complex type tag that contains the element tag.
     * @param xsdTypeName The attribute name of the complex type tag.
     */
    private static <T extends STypeComposite<?>> void addAttributes(T type, ElementReader root, ElementReader complexType, String xsdTypeName) {
    	
    	ElementReader tagComplexType = complexType;

        if (!StringUtils.isBlank(xsdTypeName)) {
            tagComplexType = findNextComplexTypeWithAttrName(root, type.getNameSimple());
        }

        Optional<ElementReader> tagSequenceOpt = tagComplexType.streamChildren().findFirst();
        if (tagSequenceOpt.isPresent()) {
        	ElementReader tagSequence = tagSequenceOpt.get();

	        findComplexTypeElements(tagSequence).forEach(tagElement -> {
	            SType<?> sType = detectType(tagElement, root);
	            SType<?> field;
	            if (sType.isList()) {
	                ElementReader listElement = findListElement(tagElement);
	                SType<?>      listElementType = detectType(listElement, root);
	                field = type.addFieldListOf(tagElement.getAttr("name"), listElement.getAttr("name"), listElementType);
	                readXsdOwnAttributeMinOccurs(listElement, field);
	                readXsdOwnAttributeMaxOccurs(listElement, field);
	            } else {
	                field = type.addField(tagElement.getAttr("name"), sType);
	                readXsdOwnAttributeMinOccurs(tagElement, field);
	                readXsdOwnAttributeMaxOccurs(tagElement, field);
	            }
	        });
        }
    }
    
    /**
     * Checks if the given complex type tag is a list
     * @param complexTypeElement The complex type tag
     * @return TRUE if the given tag is a list or FALSE if not
     */
    private static boolean isList(ElementReader complexTypeElement) {
        if (!complexTypeElement.isTagComplexType()) {
            throw new SingularFormException(complexTypeElement.errorMsg(" this type is not a complex type, therefore we can not look ahead for list pattern"));
        }
        return lookAheadForListElementType(complexTypeElement).isPresent();
    }

    /**
     * Searches for a complex tag on the XSD document that has the given name as attribute
     * @param root The root tag of the XSD
     * @param name The name of the complex type tag
     * @return The complex type tag that has the given name as attribute
     */
    private static ElementReader findNextComplexTypeWithAttrName(ElementReader root, String name) {
        return root
                .streamChildren()
                .filter(ElementReader::isTagComplexType)
                .filter(tagComplexType -> tagComplexType.getAttr("name").equals(name))
                .findFirst()
                .orElseThrow(() -> new SingularFormException(root.errorMsg(" Could not get the underlying complex type")));
    }

    /**
     * Creates a list with all the child elements of the complex type
     * @param tag The complex type tag
     * @return The list with the child elements
     */
    private static List<ElementReader> findComplexTypeElements(ElementReader tag) {
        List<ElementReader> elements = new ArrayList<>();
        tag.streamChildren()
                .filter(ElementReader::isTagXsdElement)
                .forEach(tagElement -> elements.add(tagElement));

        return elements;
    }

    /**
     * Returns the first element tag on the given complex type list
     * @param element The element tag
     * @return The first element tag on the given complex type list
     */
    private static ElementReader findListElement(ElementReader element) {
        Optional<ElementReader> complexTypeChildrenOpt = element.streamChildren().filter(ElementReader::isTagComplexType).findFirst();
        if (complexTypeChildrenOpt.isPresent()) {
            ElementReader complexTypeChildren = complexTypeChildrenOpt.get();

            if (isList(complexTypeChildren)) {
            	Optional<ElementReader> firstListElementType = lookAheadForListElementType(complexTypeChildren);
            	if (firstListElementType.isPresent()) {
            		return firstListElementType.get();
            	}
            }
        }

        throw new SingularFormException(element.errorMsg("Não preparado para detectar o tipo"));

    }

    /**
     * Returns an Optional with the first element tag on the given complex type list
     * @param complexTypeElement The complex type tag
     * @return An Optional with the first element tag on the given complex type list
     */
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
