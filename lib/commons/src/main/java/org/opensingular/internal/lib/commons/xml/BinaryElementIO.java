package org.opensingular.internal.lib.commons.xml;

import org.apache.commons.io.IOUtils;
import org.opensingular.lib.commons.base.SingularException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Reads and write a {@link Element} and its content in a binary format that is much faster and compacter then write it
 * the XML format. Applies some level of compression by using codes for each string.
 * <p><b>ATTENTION</b>: This binary format supports only DOM's node of the type Element, Attribute and Text Value. Other
 * DOM's type aren't saved. Also, name space formats aren't saved.</p>
 *
 * @author Daniel C. Bordin
 * @since 2018-10-02
 */
public class BinaryElementIO {

    /**
     * Writes the binary representation of the {@link Element} into the output.
     */
    public static void write(@Nonnull OutputStream out, @Nonnull Element element) throws IOException {
        DataOutputStream out2 = new DataOutputStream(out);
        DictionaryWrite  dic  = new DictionaryWrite();
        write(out2, dic, element);
    }

    /**
     * Generates the binary representation of the {@link Element}.
     */
    public static byte[] write(@Nonnull Element element) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            write(out, element);
            return out.toByteArray();
        } catch (IOException e) {
            throw SingularException.rethrow(e);
        }
    }

    private static void write(@Nonnull DataOutputStream out, @Nonnull DictionaryWrite dic, @Nonnull Element element)
            throws IOException {
        writeEntry(out, dic.getElement(element));
        NamedNodeMap nodes = element.getAttributes();
        if (nodes != null) {
            for (int i = 0; i < nodes.getLength(); i++) {
                Node att = nodes.item(i);
                writeEntry(out, dic.getAttribute(att.getNodeName()));
                writeEntry(out, dic.getValue(att.getNodeValue()));
            }
        }
        for (Node node = element.getFirstChild(); node != null; node = node.getNextSibling()) {
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    write(out, dic, (Element) node);
                    break;
                case Node.TEXT_NODE:
                    writeEntry(out, dic.getValue(node.getNodeValue()));
                    break;
                default:
                    throw new SingularException("Invalid Node: " + node.getNodeType());
            }
        }
        writeEntry(out, dic.getElementEnd());
    }

    private static void writeEntry(@Nonnull DataOutputStream out2, @Nonnull DictionaryEntry entry) throws IOException {
        out2.writeInt(entry.index);
        if (entry.newEntry) {
            out2.writeShort(entry.type.ordinal());
            byte[] content = entry.value.getBytes(StandardCharsets.UTF_8);
            out2.writeInt(content.length);
            IOUtils.write(content, out2);
        }
    }

    /**
     * Converts the binary representation to a DOM's {@link Element}.
     */
    @Nonnull
    public static MElement read(@Nonnull byte[] content) throws IOException {
        try (ByteArrayInputStream in = new ByteArrayInputStream(content)) {
            return read(in);
        }
    }

    /**
     * Reads the {@link Element} from the indicated stream.
     */
    @Nonnull
    public static MElement read(@Nonnull InputStream in) throws IOException {
        DictionaryRead  dic   = new DictionaryRead();
        DataInputStream in2   = new DataInputStream(in);
        DictionaryEntry entry = readNext(in2, dic);
        if (entry.type != EntryType.ELEMENT) {
            throw new SingularException("Invalid Form. Type= " + entry.type);
        }
        return readElement(in2, dic, null, entry);
    }

    @Nonnull
    private static MElement readElement(DataInputStream in, DictionaryRead dic, MElement parent,
                                        @Nonnull DictionaryEntry entry) throws IOException {
        MElement xml;
        if (parent == null) {
            xml = MElement.newInstance(entry.value);
        } else {
            xml = parent.addElement(entry.value);
        }
        DictionaryEntry current;
        while ((current = readNext(in, dic)).type != EntryType.END_ELEMENT) {
            switch (current.type) {
                case ELEMENT:
                    readElement(in, dic, xml, current);
                    break;
                case ATTRIBUTE:
                    String value = readNext(in, dic).value;
                    xml.setAttribute(current.value, value);
                    break;
                case VALUE:
                    Document d = xml.getOwnerDocument();
                    Text txt = d.createTextNode(current.value);
                    xml.appendChild(txt);
                    break;
                case END_ELEMENT:
                    break;
            }
        }
        return xml;
    }

    @Nonnull
    private static DictionaryEntry readNext(@Nonnull DataInputStream in, @Nonnull DictionaryRead dic)
            throws IOException {
        Integer         index = in.readInt();
        DictionaryEntry entry = dic.get(index);
        if (entry == null) {
            EntryType type    = EntryType.values()[in.readShort()];
            int       length  = in.readInt();
            byte[]    content = new byte[length];
            IOUtils.read(in, content);
            String value = new String(content, StandardCharsets.UTF_8);
            entry = dic.register(index, type, value);
        }
        return entry;
    }

    private static class DictionaryWrite {
        private Map<DictionaryEntry, DictionaryEntry> elements   = new HashMap<>();
        private Map<DictionaryEntry, DictionaryEntry> attributes = new HashMap<>();
        private Map<DictionaryEntry, DictionaryEntry> values     = new HashMap<>();
        private int                                   count      = 0;

        private final DictionaryEntry elementEnd;

        private DictionaryWrite() {
            this.elementEnd = new DictionaryEntry(count, EntryType.END_ELEMENT, null);
            this.count++;
        }

        @Nonnull
        DictionaryEntry getElementEnd() {
            return elementEnd;
        }

        @Nonnull
        public DictionaryEntry getElement(@Nonnull Element element) {
            return get(elements, EntryType.ELEMENT, element.getNodeName());
        }

        @Nonnull
        public DictionaryEntry getAttribute(String nodeName) {
            return get(attributes, EntryType.ATTRIBUTE, nodeName);
        }

        @Nonnull
        public DictionaryEntry getValue(String value) {
            return get(values, EntryType.VALUE, value);
        }

        @Nonnull
        private DictionaryEntry get(@Nonnull Map<DictionaryEntry, DictionaryEntry> map, @Nonnull EntryType type,
                                    String value) {
            DictionaryEntry entry = new DictionaryEntry(count, type, value);
            DictionaryEntry e     = map.get(entry);
            if (e == null) {
                map.put(entry, entry);
                count++;
                e = entry;
                e.newEntry = true;
            } else {
                e.newEntry = false;
            }
            return e;
        }
    }

    private static class DictionaryRead {
        private Map<Integer, DictionaryEntry> entries = new HashMap<>();

        private DictionaryRead() {
            register(0, EntryType.END_ELEMENT, null);
        }

        @Nullable
        private DictionaryEntry get(Integer index) {
            return entries.get(index);
        }

        @Nonnull
        public DictionaryEntry register(@Nonnull Integer index, @Nonnull EntryType type, @Nullable String value) {
            DictionaryEntry entry = new DictionaryEntry(index, type, value);
            entries.put(index, entry);
            return entry;
        }
    }

    private static class DictionaryEntry {
        public final Integer   index;
        public final EntryType type;
        public final String    value;
        boolean newEntry;

        private DictionaryEntry(@Nonnull Integer index, @Nonnull EntryType type, @Nullable String value) {
            this.index = index;
            this.type = type;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o == null || getClass() != o.getClass()) {
                return false;
            }
            DictionaryEntry that = (DictionaryEntry) o;
            return (type == that.type) && Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            int result = type.hashCode();
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
        }
    }

    private enum EntryType {
        ELEMENT, ATTRIBUTE, VALUE, END_ELEMENT
    }
}
