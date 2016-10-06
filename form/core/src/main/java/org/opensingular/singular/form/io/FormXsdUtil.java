package org.opensingular.singular.form.io;

import br.net.mirante.singular.form.*;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.internal.xml.MElement;
import org.opensingular.singular.form.internal.xml.MParser;
import org.opensingular.singular.form.type.core.STypeDecimal;
import org.opensingular.singular.form.type.core.STypeInteger;
import org.opensingular.singular.form.type.core.STypeString;
import org.opensingular.singular.form.PackageBuilder;
import org.opensingular.singular.form.SType;
import org.opensingular.singular.form.STypeList;
import org.opensingular.singular.form.SingularFormException;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;

import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class FormXsdUtil {

    private FormXsdUtil() {
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
        return xsdToSType(packageForNewTypes, MformPersistenciaXML.parseXml(xsdDefinition));
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
                String name = element.getAttrRequired("name");
                SType<?> typeOfNewType = detectType(element);
                SType<?> newType;
                if (typeContext == null) {
                    if (element.isList()) {
                        throw new SingularFormException(element.errorMsg("Tipo raiz não esperado como lista"));
                    }
                    newType = parent.getPkg().createType(name, typeOfNewType);
                    readXsd(newType, element);
                } else if (typeContext instanceof STypeComposite) {
                    if (element.isList()) {
                        if (typeOfNewType.getClass() == STypeComposite.class) {
                            newType = ((STypeComposite) typeContext).addFieldListOfComposite(name + "List", name);
                        } else {
                            newType = ((STypeComposite) typeContext).addFieldListOf(name, typeOfNewType);
                        }
                        readXsd(((STypeList) newType).getElementsType(), element);
                    } else {
                        newType = ((STypeComposite) typeContext).addField(name, typeOfNewType);
                        readXsd(newType, element);
                    }
                } else {
                    element.checkUnexpectedNodeFor(typeContext);
                    continue;
                }
                readXsdAttributes(element, newType);
            } else if (element.isTagComplexType() || element.isTagSequence()) {
                if (typeContext instanceof STypeComposite) {
                    readXsd(typeContext, element);
                } else {
                    element.checkUnexpectedNodeFor(typeContext);
                    continue;
                }
            } else if (element.isTagAttribute()) {
                if (!(typeContext instanceof STypeComposite)) {
                    element.checkUnexpectedNodeFor(typeContext);
                    continue;
                }
                String name = element.getAttrRequired("name");
                SType<?> typeOfNewType = detectType(element, element.getAttrRequired("type"));
                SType<?> newType = ((STypeComposite) typeContext).addField(name, typeOfNewType);
                readXsdAttributes(element, newType);

            } else {
                element.checkUnknownNodeTreatment();
            }
        }
    }

    private static void readXsdAttributes(ElementReader element, SType<?> newType) {
        if (element.isTagAttribute() ) {
            String value = element.getAttr("use");
            if (value != null) {
                newType.asAtr().required("required".equals(value));
            }
        } else {
            Integer minOccurs = element.getAttrInteger("minOccurs");
            if (minOccurs == null || minOccurs.intValue() == 1) {
                newType.asAtr().required();
                if (newType instanceof STypeList) {
                    ((STypeList) newType).withMiniumSizeOf(1);
                }
            } else if (minOccurs.intValue() > 1) {
                if (newType.isList()) {
                    ((STypeList) newType).withMiniumSizeOf(minOccurs.intValue());
                } else {
                    throw new SingularFormException(element.errorMsgInvalidAttribute("minOccurs"));
                }
            }
            String value = element.getAttr("maxOccurs");
            if (value != null) {
                if ("unbounded".equalsIgnoreCase(value)) {
                    if (!newType.isList()) {
                        throw new SingularFormException(element.errorMsgInvalidAttribute("maxOccurs"));
                    }
                } else {
                    int maxOccurs = Integer.parseInt(value);
                    if (newType.isList()) {
                        ((STypeList) newType).withMaximumSizeOf(maxOccurs);
                    } else if (maxOccurs != 1) {
                        throw new SingularFormException(element.errorMsgInvalidAttribute("maxOccurs"));
                    }
                }
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
            if ("string".equals(name)) {
                return xsdContext.getType(STypeString.class);
            } else if ("decimal".equals(name)) {
                return xsdContext.getType(STypeDecimal.class);
            } else if ("positiveInteger".equals(name)) {
                return xsdContext.getType(STypeInteger.class);
            }
        }
        throw new SingularFormException(node.errorMsg("Não preparado para tratar o tipo '" + xsdTypeName + "'"));
    }

    private static String getTypeNameWithoutNamespace(String name) {
        int pos = name.indexOf(':');
        return pos == -1 ? name : name.substring(pos + 1);
    }

    private static class XsdContext {

        private static final String XSD_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema";

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
        private PackageBuilder pa;
        private boolean list;

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
            return value == null ? null : Integer.parseInt(value);
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
