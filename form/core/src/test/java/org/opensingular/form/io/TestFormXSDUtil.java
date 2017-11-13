/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.io;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.TestCaseForm;
import org.opensingular.form.helpers.AssertionsSType;
import org.opensingular.form.helpers.AssertionsXML;
import org.opensingular.form.type.core.STypeBoolean;
import org.opensingular.form.type.core.STypeDate;
import org.opensingular.form.type.core.STypeDateTime;
import org.opensingular.form.type.core.STypeDecimal;
import org.opensingular.form.type.core.STypeHTML;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeLong;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.core.STypeTime;

/**
 * Testa se o correto funcionamento da conversão de um XSD em Stype. Mais
 * exemplos de XSD em http://www.w3schools.com/xml/schema_example.asp
 *
 * @author Daniel C. Bordin
 */
@RunWith(Parameterized.class)
public class TestFormXSDUtil extends TestCaseForm {

    public TestFormXSDUtil(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test
    public void toXsdSimpleType() {
        SDictionary dictionary = createTestDictionary();
        AssertionsXML xml = toXsd(dictionary.getType(STypeString.class));
        xml = xml.getOnlyChild("xs:element");
        xml.hasAttributes(3).isAttribute("name", "String").isAttribute("type", "xs:string").isAttribute("xsf:maxLength",
                "100").hasNoChildren();

        xml = toXsdWebService(dictionary.getType(STypeString.class));
        xml = xml.getOnlyChild("xs:element");
        xml.hasAttributes(2).isAttribute("name", "String").isAttribute("type", "xs:string").hasNoChildren();

        xml = toXsd(dictionary.getType(STypeInteger.class));
        xml = xml.getOnlyChild("xs:element");
        xml.isAttribute("name", "Integer").isAttribute("type", "xs:integer").attributeNotPresent("minOccurs")
                .hasNoChildren();

        xml = toXsd(dictionary.getType(STypeLong.class));
        xml = xml.getOnlyChild("xs:element");
        xml.isAttribute("name", "Long").isAttribute("type", "xs:long").attributeNotPresent("minOccurs").hasNoChildren();

        xml = toXsd(dictionary.getType(STypeDecimal.class));
        xml = xml.getOnlyChild("xs:element");
        xml.isAttribute("name", "Decimal").isAttribute("type", "xs:decimal").hasNoChildren();

        xml = toXsd(dictionary.getType(STypeBoolean.class));
        xml = xml.getOnlyChild("xs:element");
        xml.isAttribute("name", "Boolean").isAttribute("type", "xs:boolean").hasNoChildren();

        xml = toXsd(dictionary.getType(STypeDate.class));
        xml = xml.getOnlyChild("xs:element");
        xml.isAttribute("name", "Date").isAttribute("type", "xs:date").hasNoChildren();

        xml = toXsd(dictionary.getType(STypeTime.class));
        xml = xml.getOnlyChild("xs:element");
        xml.isAttribute("name", "Time").isAttribute("type", "xs:time").hasNoChildren();

        xml = toXsd(dictionary.getType(STypeDateTime.class));
        xml = xml.getOnlyChild("xs:element");
        xml.isAttribute("name", "DateTime").isAttribute("type", "xs:dateTime").hasNoChildren();

        xml = toXsd(dictionary.getType(STypeHTML.class));
        xml = xml.getOnlyChild("xs:element");
        xml.isAttribute("name", "HTML").isAttribute("type", "xs:string").hasNoChildren();
    }

    @Test
    public void toXsdCompositeType() {
        PackageBuilder pkg = createTestPackage();
        STypeComposite<SIComposite> order = pkg.createCompositeType("order");
        order.addFieldString("description").asAtr().required();
        order.addFieldString("observation");
        order.addFieldInteger("number").asAtr().required();
        order.addFieldDate("submission");

        AssertionsXML xml = toXsd(order);
        xml = xml.getOnlyChild("xs:element");
        xml.isAttribute("name", "order").attributeNotPresent("type").attributeNotPresent("minOccurs");
        xml = xml.getOnlyChild("xs:complexType");
        xml = xml.getOnlyChild("xs:sequence");
        xml.hasChildren(4);
        xml.getChild(0).isName("xs:element").isAttribute("name", "description").isAttribute("type", "xs:string")
                .attributeNotPresent("minOccurs").hasNoChildren();
        xml.getChild(1).isName("xs:element").isAttribute("name", "observation").isAttribute("type", "xs:string")
                .isAttribute("minOccurs", "0").hasNoChildren();
        xml.getChild(2).isName("xs:element").isAttribute("name", "number").isAttribute("type", "xs:integer")
                .attributeNotPresent("minOccurs").hasNoChildren();
        xml.getChild(3).isName("xs:element").isAttribute("name", "submission").isAttribute("type", "xs:date")
                .isAttribute("minOccurs", "0").hasNoChildren();


        STypeComposite<SIComposite> orderSpecial = pkg.createType("orderSpecial", order);
        orderSpecial.addFieldBoolean("special");

        xml = toXsd(orderSpecial);
        xml = xml.getOnlyChild("xs:element");
        xml.isAttribute("name", "orderSpecial").attributeNotPresent("type").attributeNotPresent("minOccurs");
        xml = xml.getOnlyChild("xs:complexType");
        xml = xml.getOnlyChild("xs:sequence");
        xml.hasChildren(5);
        xml.getChild(4).isName("xs:element").isAttribute("name", "special").isAttribute("type", "xs:boolean")
                .isAttribute("minOccurs", "0").hasNoChildren();

    }

    @Test
    public void toXsdListTypeOfComposite() {
        PackageBuilder pkg = createTestPackage();
        STypeList<STypeComposite<SIComposite>, SIComposite> items = pkg.createListOfNewCompositeType("items", "item");
        STypeComposite<SIComposite> item = items.getElementsType();
        item.addFieldString("name").asAtr().required();

        AssertionsXML xml = toXsd(items);
        xml = xml.getOnlyChild("xs:element");
        xml.isAttribute("name", "items").hasAttributes(1);
        xml = xml.getOnlyChild("xs:complexType").hasAttributes(0);
        xml = xml.getOnlyChild("xs:sequence").hasAttributes(0);
        xml = xml.getOnlyChild("xs:element").hasAttributes(2).isAttribute("name", "item").isAttribute("maxOccurs",
                "unbounded");
        xml = xml.getOnlyChild("xs:complexType").hasAttributes(0);
        xml = xml.getOnlyChild("xs:sequence").hasAttributes(0);
        xml = xml.getOnlyChild("xs:element").hasAttributes(3).isAttribute("name", "name").isAttribute("type",
                "xs:string").isAttribute("xsf:maxLength", "100").hasNoChildren();

        items.withMiniumSizeOf(2).withMaximumSizeOf(4);
        xml = toXsd(items);
        xml = xml.getOnlyChild("xs:element");
        xml = xml.getOnlyChild("xs:complexType").hasAttributes(0);
        xml = xml.getOnlyChild("xs:sequence").hasAttributes(0);
        xml = xml.getOnlyChild("xs:element").hasAttributes(3).isAttribute("name", "item").isAttribute("minOccurs", "2")
                .isAttribute("maxOccurs", "4");
    }

    private AssertionsXML toXsd(SType<?> type) {
        return toXsd(type, FormToXSDConfig.newForUserDisplay());
    }

    private AssertionsXML toXsdWebService(SType<?> type) {
        return toXsd(type, FormToXSDConfig.newForWebServiceDefinition());
    }

    private AssertionsXML toXsd(SType<?> type, FormToXSDConfig config) {
        AssertionsXML xml = new AssertionsXML(FormXSDUtil.toXsd(type, config)).isNotNull();
        xml.isName(FormXSDUtil.XSD_NAMESPACE_PREFIX, "schema");
        assertNameSpaceXsd(xml);
        xml.hasChildren(1);
        return xml;
    }

    private void assertNameSpaceXsd(AssertionsXML xml) {
        xml.isNameSpaceUri(FormXSDUtil.XSD_NAMESPACE_URI);
        for (AssertionsXML x : xml) {
            assertNameSpaceXsd(x);
        }
    }

    @Test
    public void testSimpleCase() {
        //@formatter:off
        String xsd =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
        "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n" +
        "<xs:element name=\"shipto\">" +
        "  <xs:complexType>" +
        "    <xs:sequence>" +
        "      <xs:element name=\"name\" type=\"xs:string\" minOccurs=\"0\"/>" +
        "      <xs:element name=\"address\" type=\"xs:string\"/>" +
        "      <xs:element name=\"city\" type=\"xs:positiveInteger\"/>" +
        "      <xs:element name=\"country\" type=\"xs:decimal\"/>" +
        "    </xs:sequence>" +
        "  </xs:complexType>" +
        "</xs:element>" +
        "</xs:schema>";
        //@formatter:on

        AssertionsSType type = parseXsd(xsd);

        type.isComposite(4);
        type.isString("name").isNotRequired();
        type.isString("address").isRequired();
        type.isInteger("city").isRequired();
        type.isDecimal("country").isRequired();
    }

    @Test
    public void testLongCase() {
        //@formatter:off
        String xsd =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
        "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n" +
        "\n" +
        "<xs:element name=\"shiporder\">\n" +
        "  <xs:complexType>\n" +
        "    <xs:sequence>\n" +
        "      <xs:element name=\"orderperson\" type=\"xs:string\"/>\n" +
        "      <xs:element name=\"shipto\">\n" +
        "        <xs:complexType>\n" +
        "          <xs:sequence>\n" +
        "            <xs:element name=\"name\" type=\"xs:string\"/>\n" +
        "            <xs:element name=\"address\" type=\"xs:string\"/>\n" +
        "            <xs:element name=\"city\" type=\"xs:string\"/>\n" +
        "            <xs:element name=\"country\" type=\"xs:string\"/>\n" +
        "          </xs:sequence>\n" +
        "        </xs:complexType>\n" +
        "      </xs:element>\n" +
        "      <xs:element name=\"item\" maxOccurs=\"unbounded\">\n" +
        "        <xs:complexType>\n" +
        "          <xs:sequence>\n" +
        "            <xs:element name=\"title\" type=\"xs:string\"/>\n" +
        "            <xs:element name=\"note\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
        "            <xs:element name=\"quantity\" type=\"xs:positiveInteger\"/>\n" +
        "            <xs:element name=\"price\" type=\"xs:decimal\"/>\n" +
        "          </xs:sequence>\n" +
        "        </xs:complexType>\n" +
        "      </xs:element>\n" +
        "    </xs:sequence>\n" +
        "    <xs:attribute name=\"orderid\" type=\"xs:string\" use=\"required\"/>\n" +
        "    <xs:attribute name=\"orderPriority\" type=\"xs:string\"/>\n" +
        "  </xs:complexType>\n" +
        "</xs:element>\n" +
        "</xs:schema>";
        //@formatter:on

        AssertionsSType type = parseXsd(xsd);

        type.isComposite(5);
        type.isString("orderperson").isRequired();

        type.isComposite("shipto", 4).isRequired();
        type.isString("shipto.name").isRequired();
        type.isString("shipto.address").isRequired();
        type.isString("shipto.city").isRequired();
        type.isString("shipto.country").isRequired();

        type.isList("itemList").isRequired();
        type.isComposite("itemList.item", 4);
        type.isString("itemList.item.title").isRequired();
        type.isString("itemList.item.note").isNotRequired();
        type.isInteger("itemList.item.quantity").isRequired();
        type.isDecimal("itemList.item.price").isRequired();

        type.isString("orderid").isRequired();
        type.isString("orderPriority").isNotRequired();
    }

    private AssertionsSType parseXsd(String xsd) {
        PackageBuilder sPackage = createTestPackage();
        SType<?> type = FormXSDUtil.xsdToSType(sPackage, xsd);
        return new AssertionsSType(type).isNotNull();
    }
}
