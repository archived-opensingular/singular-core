package org.opensingular.form.decorator.action;

import static org.junit.Assert.*;

import org.junit.Test;

public class SIconTest {

    @Test
    public void testLineIcons() {
        assertEquals("icon-action-redo", SIcon.resolve("action-redo").getCssClass());
    }

    @Test
    public void testFontAwesome() {
        assertEquals("fa fa-trash-o", SIcon.resolve("trash-o").getCssClass());
    }

    @Test
    public void testGlyphIcons() {
        assertEquals("glyphicon glyphicon-piggy-bank", SIcon.resolve("piggy-bank").getCssClass());
    }

    @Test
    public void testUnresolved() {
        assertEquals("unresolvedicon", SIcon.resolve("unresolvedicon").getCssClass());
    }

    @Test
    public void testForced() {
        assertEquals("fa fa-check", SIcon.resolve("fa fa-check").getCssClass());
    }

    @Test
    public void defaultMethods() {
        assertEquals("bla", new SIcon() {
            @Override
            public String getId() {
                return "bla";
            }
        }.getCssClass());
    }

}
