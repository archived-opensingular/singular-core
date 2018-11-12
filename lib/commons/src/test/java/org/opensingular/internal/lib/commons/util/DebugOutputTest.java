package org.opensingular.internal.lib.commons.util;

import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel C. Bordin
 * @since 2018-09-11
 */
public class DebugOutputTest {

    @Test
    public void flat() {
        DebugOutput debug = new DebugOutput(new CharArrayWriter());
        debug.append("A");
        debug.append("B");
        debug.append('C');
        debug.append(null);
        debug.println();
        assertContent(debug, "ABCnull");
    }

    @Test
    public void subLevel() {
        DebugOutput debug = new DebugOutput(new CharArrayWriter(), 4);
        debug.append("A");
        debug.append("B");
        debug.append((String) null);
        debug.println();
        debug.println();
        debug.print("line\n");
        debug.print(10);
        debug.print((Object) "last");

        assertContent(debug, "    ABnull", "", "    line", "    10last");
    }

    @Test
    public void subLevelsWithChars() {
        DebugOutput debug = new DebugOutput(new CharArrayWriter(), 4);
        debug.append('A');
        debug.append('B');
        debug.append('\n');
        debug.append('C');
        debug.append('\n');

        assertContent(debug, "    AB", "    C");
    }

    @Test
    public void subLevelsWithPositionalChar() {
        DebugOutput debug = new DebugOutput(new CharArrayWriter(), 2);
        debug.append("ABCD", 1, 3);
        debug.append("EF\nG", 1, 3);
        debug.append(null, 1, 3);

        assertContent(debug, "  BCF", "  null");
    }

    @Test
    public void subDebug() {
        DebugOutput debug = new DebugOutput(new CharArrayWriter());
        debug.println((Object) null);
        debug.addSubLevel().println("XX").println("YY");
        debug.print((Object) null);
        debug.addSubLevel().println("ZZ");
        debug.println();
        debug.println("last");

        assertContent(debug, "null", "   XX", "   YY", "null","   ZZ","", "last");
    }

    @Test
    public void debugByColumn() {
        DebugOutput debug = new DebugOutput(new CharArrayWriter());
        DebugOutputTable table = debug.table();
        table.addColumn(1);
        table.addColumn(3);
        table.addColumn(4);

        table.println("A", "B", "C");
        table.println();
        table.println(null, null, "DD");
        table.println("AA", "B");
        table.println("A", "B");

        assertContent(debug, "A B   C", "", "      DD", "AA B", "A  B");
    }

    @Test
    public void debugByColumnFromList() {
        List<String> values = Lists.newArrayList("First", "Second", "Last");

        DebugOutput debug = new DebugOutput(new CharArrayWriter());
        debug.table(values, (t, v) -> {
            t.setValue(0, v);
            t.setValue(1, v.length());
        });
        assertContent(debug, "First  5", "Second 6", "Last   4");
    }

    @Test
    public void debugByColumnFromListWithTitle() {
        List<String> values = Lists.newArrayList("First", "Second", "Last");

        DebugOutput debug = new DebugOutput(new CharArrayWriter());
        debug.table(values, (t, v) -> {
            t.setValue(0, v);
            t.setValue(1, v.length());
        }, t -> t.addLine("Name Client", "Size"));
        assertContent(debug, "Name Client Size", "First       5", "Second      6", "Last        4");
    }

    @Test
    public void debugByColumnFromListWithTitle2() {
        List<String> values = Lists.newArrayList("First", "Second", "Last");

        DebugOutput debug = new DebugOutput(new CharArrayWriter());
        debug.table(values, (t, v) -> {
            t.setValue(0, v);
            t.setValue(1, v.length());
            t.setValue(2, v.substring(0, 1));
        }, t -> values.forEach(t::addValue));
        assertContent(debug, "First  Second Last", "First  5      F", "Second 6      S", "Last   4      L");
    }


    private void assertContent(@Nonnull DebugOutput debug, String... expectedLines) {
        String content = debug.getAppendable().toString();
        List<String> lines;
        try {
            lines = IOUtils.readLines(new CharArrayReader(content.toCharArray()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ArrayList<String> expected = Lists.newArrayList(expectedLines);
        Assertions.assertThat(lines).hasSameElementsAs(expected);
    }
}