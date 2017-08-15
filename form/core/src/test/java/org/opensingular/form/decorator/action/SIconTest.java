package org.opensingular.form.decorator.action;

import static java.util.stream.Collectors.*;
import static org.junit.Assert.*;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.Test;

public class SIconTest {

    @Test
    public void testLineIcons() {
        assertEquals(setOf("icon-action-redo"), SIcon.resolve("action-redo").getIconCssClasses());
    }

    @Test
    public void testFontAwesome() {
        assertEquals(setOf("fa fa-trash-o"), SIcon.resolve("trash-o").getIconCssClasses());
    }

    @Test
    public void testGlyphIcons() {
        assertEquals(setOf("glyphicon glyphicon-piggy-bank"), SIcon.resolve("piggy-bank").getIconCssClasses());
    }

    @Test
    public void testUnresolved() {
        assertEquals(setOf("unresolvedicon"), SIcon.resolve("unresolvedicon").getIconCssClasses());
    }

    @Test
    public void testForced() {
        assertEquals(setOf("fa fa-check"), SIcon.resolve("fa fa-check").getIconCssClasses());
    }

    @Test
    public void defaultMethods() {
        assertEquals(setOf("bla"), SIcon.resolve("bla").getIconCssClasses());
    }

    private static Set<String> setOf(String... s) {
        final Pattern regex = Pattern.compile("\\s");
        return Stream.of(s)
            .flatMap(it -> regex.splitAsStream(it))
            .collect(toSet());
    }
}
