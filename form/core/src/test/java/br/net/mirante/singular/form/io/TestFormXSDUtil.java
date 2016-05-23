package br.net.mirante.singular.form.io;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import br.net.mirante.singular.form.AssertionsSForm;
import br.net.mirante.singular.form.AssertionsSType;
import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.TestCaseForm;

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
        PackageBuilder sPackage = createTestDictionary().createNewPackage("test.xsd");
        SType<?> type = FormXsdUtil.xsdToSType(sPackage, xsd);
        return AssertionsSForm.assertType(type).isNotNull();
    }
}
