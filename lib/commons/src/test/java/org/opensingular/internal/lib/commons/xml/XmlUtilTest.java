package org.opensingular.internal.lib.commons.xml;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.opensingular.lib.commons.base.SingularException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Daniel C. Bordin
 * @since 2018-10-25
 */
public class XmlUtilTest {

    private static final String HEADER_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    @Test
    public void parse() {

        Element empty = XmlUtil.parseXml(HEADER_XML + "<empty/>");
        assertNull(XmlUtil.getValueText(empty));
        assertNull(XmlUtil.getValueText((Node) empty));
        assertEquals(HEADER_XML + "<empty/>", XmlUtil.toStringExact(empty));

        Element full = XmlUtil.parseXml(HEADER_XML + "<full>A</full>");
        assertEquals("A", XmlUtil.getValueText(full));
        assertEquals("A", XmlUtil.getValueText((Node) full));
        assertEquals(HEADER_XML + "<full>A</full>", XmlUtil.toStringExact(full));

        assertNull(XmlUtil.parseXmlOptional(null));
        assertNull(XmlUtil.parseXmlOptional(""));
        assertNull(XmlUtil.parseXmlOptional(" "));

        Assertions.assertThatThrownBy(() -> XmlUtil.parseXml(null)).isExactlyInstanceOf(SingularException.class)
                .hasMessageContaining("XML String has a empty content");
        Assertions.assertThatThrownBy(() -> XmlUtil.parseXml(" ")).isExactlyInstanceOf(SingularException.class)
                .hasMessageContaining("XML String has a empty content");

        Assertions.assertThatThrownBy(() -> XmlUtil.parseXmlOptional("XXPP")).isExactlyInstanceOf(SingularException.class)
                .hasMessageContaining("Fail to read XML");
        Assertions.assertThatThrownBy(() -> XmlUtil.parseXml("XXPP")).isExactlyInstanceOf(SingularException.class)
                .hasMessageContaining("Fail to read XML");
    }

    @Test
    public void createElements() {
        Element xml = XmlUtil.newRootElement("root");
        assertEquals(HEADER_XML + "<root/>", XmlUtil.toStringExact(xml));

        XmlUtil.addElement(xml, "child");
        assertEquals(HEADER_XML + "<root><child/></root>", XmlUtil.toStringExact(xml));
    }
}