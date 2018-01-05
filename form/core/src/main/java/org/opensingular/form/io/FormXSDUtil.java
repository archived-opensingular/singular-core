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
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.internal.lib.commons.xml.MElement;
import org.opensingular.internal.lib.commons.xml.MParser;
import org.w3c.dom.Node;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class FormXSDUtil {

    public static final String XSD_SINGULAR_NAMESPACE_URI = "http://opensingular.org/FormSchema";
    public static final String XSD_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema";
    public static final String XSD_NAMESPACE_PREFIX = "xs";
    private static final String XSD_SCHEMA = XSD_NAMESPACE_PREFIX + ":schema";
    private static final String XSD_ELEMENT = XSD_NAMESPACE_PREFIX + ":element";
    private static final String XSD_COMPLEX_TYPE = XSD_NAMESPACE_PREFIX + ":complexType";
    private static final String XSD_SEQUENCE = XSD_NAMESPACE_PREFIX + ":sequence";

    private static XsdTypeMapping typeMapping;

    private FormXSDUtil() {
    }

    private static XsdTypeMapping getMapping() {
        if (typeMapping == null) {
            typeMapping = new XsdTypeMapping();
        }
        return typeMapping;
    }

    /** Converts a {@link SType} definition to a XSD format.*/
    @Nonnull
    public static MElement toXsd(@Nonnull SType<?> type, @Nonnull FormToXSDConfig config) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(config);
        MElement element = MElement.newInstance(XSD_NAMESPACE_URI, XSD_SCHEMA);
        if (config.isGenerateCustomAttribute()) {
            element.setAttribute("xmlns:xsf", XSD_SINGULAR_NAMESPACE_URI);
        }
        toXsdFromSType(element, type, config);
        return element;
    }

    private static MElement toXsdFromSType(MElement parent, SType<?> type, @Nonnull FormToXSDConfig config) {
        if (type instanceof STypeSimple) {
            return toXsdFromSimple(parent, (STypeSimple<?, ?>) type, config);
        } else if (type instanceof STypeComposite) {
            return toXsdFromComposite(parent, (STypeComposite<?>) type, config);
        } else if (type instanceof STypeList) {
            return toXsdFromList(parent, (STypeList<?, ?>) type, config);
        } else {
            throw new SingularFormException("Unkown SType Class to be convert to XSD", type);
        }
    }

    private static MElement toXsdFromList(MElement parent, STypeList<?, ?> type, @Nonnull FormToXSDConfig config) {
        MElement list = createXsdElement(parent, type);

        MElement element = list.addElementNS(XSD_NAMESPACE_URI, XSD_COMPLEX_TYPE);
        element = element.addElementNS(XSD_NAMESPACE_URI, XSD_SEQUENCE);
        element = toXsdFromSType(element, type.getElementsType(), config);
        Integer min = type.getMinimumSize();
        if (min != null) {
            element.setAttribute("minOccurs", min.toString());
        }
        Integer max = type.getMaximumSize();
        if (max == null) {
            element.setAttribute("maxOccurs", "unbounded");
        } else {
            element.setAttribute("maxOccurs", max.toString());
        }

        return list;
    }

    private static MElement toXsdFromComposite(MElement parent, STypeComposite<?> type, @Nonnull FormToXSDConfig config) {
        MElement composite = createXsdElement(parent, type);

        MElement element = composite.addElementNS(XSD_NAMESPACE_URI, XSD_COMPLEX_TYPE);
        element = element.addElementNS(XSD_NAMESPACE_URI, XSD_SEQUENCE);
        element.setAttribute("minOccurs", "0");
        for (SType<?> child : type.getFields()) {
            toXsdFromSType(element, child, config);
        }
        return composite;
    }

    private static MElement toXsdFromSimple(MElement parent, STypeSimple<?, ?> type, @Nonnull FormToXSDConfig config) {
        MElement simple = createXsdElement(parent, type);
        String xsdType = getMapping().findXsdType(type);
        simple.setAttribute("type", XSD_NAMESPACE_PREFIX + ":" + xsdType);
        if (!type.isRequired() && !XSD_SCHEMA.equals(parent.getNodeName())) {
            simple.setAttribute("minOccurs", "0");
        }
        if (config.isGenerateCustomAttribute()) {
            if (type.hasAttributeDefinedInHierarchy(SPackageBasic.ATR_MAX_LENGTH)) {
                Integer maxLength = type.getAttributeValue(SPackageBasic.ATR_MAX_LENGTH);
                if (maxLength != null) {
                    simple.setAttributeNS(XSD_SINGULAR_NAMESPACE_URI, "xsf:maxLength", maxLength.toString());
                }
            }
        }
        return simple;
    }

    @Nonnull
    private static MElement createXsdElement(@Nonnull MElement parent, @Nonnull SType<?> type) {
        MElement element = parent.addElementNS(XSD_NAMESPACE_URI, XSD_ELEMENT);
        element.setAttribute("name", type.getNameSimple());
        return element;
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

        XsdContext xsdContext = new XsdContext(packageForNewTypes);
        ElementReader element = new ElementReader(xsdContext, root);

        if (!element.isTagXsdSchema()) {
            throw new SingularFormException(
                    "O XSD não é válido: a tag raiz é '" + element.getNodeName() + "' e deveria ser 'xs:schema'");
        }

        readXsd(null, element);

        Collection<SType<?>> types = packageForNewTypes.getPackage().getLocalTypes();
        return types.size() == 1 ? types.iterator().next() : null;
    }

    private static void readXsd(SType<?> typeContext, ElementReader parent) {
        for (ElementReader element : parent) {
            if (element.isTagXsdElement()) {
                readXsdElementDefinition(typeContext, parent, element, true);
            } else if (element.isTagComplexType() || element.isTagSequence()) {
                if (typeContext instanceof STypeComposite) {
                    readXsd(typeContext, element);
                } else {
                    element.checkUnexpectedNodeFor(typeContext);
                }
            } else if (element.isTagAttribute()) {
                readXsdAtributeDefinition(element, typeContext);
            } else {
                element.checkUnknownNodeTreatment();
            }

        }
    }

    private static void readXsdElementDefinition(SType<?> typeContext, ElementReader parent, ElementReader element, boolean generateLabel) {
        String name = element.getAttrRequired("name");
        SType<?> typeOfNewType = detectType(element);
        SType<?> newType;
        if (typeContext == null) {
            if (element.isList()) {
                throw new SingularFormException(element.errorMsg("Tipo raiz não esperado como lista"));
            }
            newType = parent.getPkg().createType(name, typeOfNewType);
            if(generateLabel){
                newType.asAtr().label(StringUtils.capitalize(name));
            }
            readXsd(newType, element);
        } else if (typeContext.isComposite()) {
            if (element.isList()) {
                if (typeOfNewType.getClass() == STypeComposite.class) {
                    newType = ((STypeComposite) typeContext).addFieldListOfComposite(name + "List", name);
                } else {
                    newType = ((STypeComposite) typeContext).addFieldListOf(name, typeOfNewType);
                }
                if(generateLabel){
                    newType.asAtr().label(StringUtils.capitalize(name));
                }
                readXsd(((STypeList) newType).getElementsType(), element);
            } else {
                newType = ((STypeComposite) typeContext).addField(name, typeOfNewType);
                if(generateLabel){
                    newType.asAtr().label(StringUtils.capitalize(name));
                }
                readXsd(newType, element);
            }
            
        } else {
            element.checkUnexpectedNodeFor(typeContext);
            return;
        }
        readXsdOwnAttributes(element, newType);
    }

    private static void readXsdAtributeDefinition(ElementReader element, SType<?> typeContext) {
        if (!typeContext.isComposite()) {
            element.checkUnexpectedNodeFor(typeContext);
            return;
        }
        String name = element.getAttrRequired("name");
        SType<?> typeOfNewType = detectType(element, element.getAttrRequired("type"));
        SType<?> newType = ((STypeComposite) typeContext).addField(name, typeOfNewType);
        readXsdOwnAttributes(element, newType);
    }

    private static void readXsdOwnAttributes(ElementReader element, SType<?> newType) {
        if (element.isTagAttribute() ) {
            String value = element.getAttr("use");
            if (value != null) {
                newType.asAtr().required("required".equals(value));
            }
        } else {
            readXsdOwnAttributeMinOccurs(element, newType);
            readXsdOwnAttributeMaxOccurs(element, newType);
        }
    }

    private static void readXsdOwnAttributeMinOccurs(ElementReader element, SType<?> newType) {
        Integer minOccurs = element.getAttrInteger("minOccurs");
        if (minOccurs == null || minOccurs.intValue() == 1) {
            newType.asAtr().required();
            if (newType.isList()) {
                ((STypeList) newType).withMiniumSizeOf(1);
            }
        } else if (minOccurs.intValue() > 1) {
            if (newType.isList()) {
                ((STypeList) newType).withMiniumSizeOf(minOccurs);
            } else {
                throw new SingularFormException(element.errorMsgInvalidAttribute("minOccurs"), newType);
            }
        }
    }

    private static void readXsdOwnAttributeMaxOccurs(ElementReader element, SType<?> newType) {
        String value = element.getAttr("maxOccurs");
        if ("unbounded".equalsIgnoreCase(value)) {
            if (!newType.isList()) {
                throw new SingularFormException(element.errorMsgInvalidAttribute("maxOccurs"), newType);
            }
        } else if (value != null) {
            int maxOccurs = Integer.parseInt(value);
            if (newType.isList()) {
                ((STypeList) newType).withMaximumSizeOf(maxOccurs);
            } else if (maxOccurs != 1) {
                throw new SingularFormException(element.errorMsgInvalidAttribute("maxOccurs"), newType);
            }
        }
    }

    private static SType<?> detectType(ElementReader element) {
        String xsdTypeName = element.getAttr("type");
        if (!StringUtils.isBlank(xsdTypeName)) {
            return detectType(element, xsdTypeName);
        }
        if (element.streamChildren().anyMatch(e -> e.isTagComplexType())) {
            return element.getXsdContext().getType(STypeComposite.class);
        }
        throw new SingularFormException(element.errorMsg("Não preparado para detectar o tipo"));
    }

    private static SType<?> detectType(ElementReader node, String xsdTypeName) {
        XsdContext xsdContext = node.getXsdContext();
        if (xsdContext.isXsdType(xsdTypeName)) {
            String name = getTypeNameWithoutNamespace(xsdTypeName);
            Class<? extends SType<?>> type = getMapping().findSType(name);
            if (type != null) {
                return xsdContext.getType(type);
            }
        }
        throw new SingularFormException(node.errorMsg("Não preparado para tratar o tipo '" + xsdTypeName + "'"));
    }

    private static String getTypeNameWithoutNamespace(String name) {
        int pos = name.indexOf(':');
        return pos == -1 ? name : name.substring(pos + 1);
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
        private final MElement element;

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

        public boolean isList() {
            return getAttrMaxOccurs() > 1;
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
