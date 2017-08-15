package org.opensingular.lib.commons.canvas;

import org.junit.Test;
import org.opensingular.lib.commons.canvas.builder.RawHtmlBuilder;

import static org.junit.Assert.assertEquals;

public class RawHtmlBuilderTest {

    @Test
    public void simpleTagTest() throws Exception {
        RawHtmlBuilder rawHtmlBuilder = new RawHtmlBuilder("div");
        assertEquals("<div></div>", rawHtmlBuilder.build());
    }

    @Test
    public void simpleTagNestedTest() throws Exception {
        RawHtmlBuilder rawHtmlBuilder = new RawHtmlBuilder("div");
        rawHtmlBuilder.newChild("span");
        assertEquals("<div><span></span></div>", rawHtmlBuilder.build());
    }

    @Test
    public void testWriteContent() throws Exception {
        RawHtmlBuilder rawHtmlBuilder = new RawHtmlBuilder("div");
        rawHtmlBuilder.appendText("Danilo");
        assertEquals("<div>Danilo</div>", rawHtmlBuilder.build());
    }

    @Test
    public void testEscapedText() throws Exception {
        RawHtmlBuilder rawHtmlBuilder = new RawHtmlBuilder("div");
        rawHtmlBuilder.appendText("Fé");
        assertEquals("<div>F&eacute;</div>", rawHtmlBuilder.build());
    }

    @Test
    public void testTagWithAttributes() throws Exception {
        RawHtmlBuilder rawHtmlBuilder = new RawHtmlBuilder("div");
        rawHtmlBuilder.putAttribute("id", "myDiv");
        rawHtmlBuilder.putAttribute("class", "text");
        assertEquals("<div id='myDiv' class='text'></div>", rawHtmlBuilder.build());
    }

    @Test
    public void testTagAppendAttributes() throws Exception {
        RawHtmlBuilder rawHtmlBuilder = new RawHtmlBuilder("div");
        rawHtmlBuilder.putAttribute("class", "text");
        rawHtmlBuilder.appendAttribute("class", "text2", " ");
        assertEquals("<div class='text text2'></div>", rawHtmlBuilder.build());
    }

    @Test
    public void testTagAppendAttributesWithoutPreviousValue() throws Exception {
        RawHtmlBuilder rawHtmlBuilder = new RawHtmlBuilder("div");
        rawHtmlBuilder.appendAttribute("class", "text", " ");
        assertEquals("<div class='text'></div>", rawHtmlBuilder.build());
    }
}