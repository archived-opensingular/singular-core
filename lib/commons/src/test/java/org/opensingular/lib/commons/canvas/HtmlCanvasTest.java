package org.opensingular.lib.commons.canvas;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class HtmlCanvasTest {

    HtmlCanvas htmlCanvas;

    @Before
    public void setUp() throws Exception {
        htmlCanvas = new HtmlCanvas(true);
    }

    @Test
    public void shouldReturnEmpty() throws Exception {
        assertEquals("<div></div>", htmlCanvas.build());
    }

    @Test
    public void shouldContainsTitle() {
        htmlCanvas.addSubtitle("Danilo");
        assertThat(htmlCanvas.build(), Matchers.containsString("Danilo"));
    }

    @Test
    public void shouldContainsTitleFirstTitleLevel() {
        htmlCanvas.addSubtitle("Titulo");
        htmlCanvas.addSubtitle("Sub Titulo");
        htmlCanvas.addSubtitle("Sub Titulo 2");
        htmlCanvas.addSubtitle("Sub Titulo 3");
        htmlCanvas.addSubtitle("Sub Titulo 4");
        assertThat(htmlCanvas.build(), Matchers.containsString("Titulo"));
        assertThat(htmlCanvas.build(), Matchers.containsString("1 Sub Titulo"));
        assertThat(htmlCanvas.build(), Matchers.containsString("2 Sub Titulo 2"));
        assertThat(htmlCanvas.build(), Matchers.containsString("3 Sub Titulo 3"));
        assertThat(htmlCanvas.build(), Matchers.containsString("4 Sub Titulo 4"));
    }

    @Test
    public void shouldContainsMultipleTitleLevel() throws Exception {
        htmlCanvas.addSubtitle("Titulo");
        htmlCanvas.addSubtitle("Sub Titulo");
        DocumentCanvas childCanvas = htmlCanvas.addChild();
        childCanvas.addSubtitle("Sub Sub Titulo");
        childCanvas.addSubtitle("Sub Sub Titulo");
        htmlCanvas.addSubtitle("Sub Titulo");
        childCanvas = htmlCanvas.addChild();
        childCanvas.addSubtitle("Sub Sub Titulo");
        childCanvas.addSubtitle("Sub Sub Titulo");
        assertThat(htmlCanvas.build(), Matchers.containsString("Titulo"));
        assertThat(htmlCanvas.build(), Matchers.containsString("1 Sub Titulo"));
        assertThat(htmlCanvas.build(), Matchers.containsString("1.1 Sub Sub Titulo"));
        assertThat(htmlCanvas.build(), Matchers.containsString("1.2 Sub Sub Titulo"));
        assertThat(htmlCanvas.build(), Matchers.containsString("2 Sub Titulo"));
        assertThat(htmlCanvas.build(), Matchers.containsString("2.1 Sub Sub Titulo"));
        assertThat(htmlCanvas.build(), Matchers.containsString("2.2 Sub Sub Titulo"));
    }

    @Test
    public void shouldIngoreNewChildWithoutTitle() throws Exception {
        htmlCanvas.addSubtitle("Titulo");
        htmlCanvas.addSubtitle("Sub Titulo");
        DocumentCanvas childCanvas = htmlCanvas.addChild();
        childCanvas.addSubtitle("Sub Sub Titulo");
        DocumentCanvas childCanvas2 = htmlCanvas.addChild();
        childCanvas2.addSubtitle("Sub Sub Titulo");
        assertThat(htmlCanvas.build(), Matchers.containsString("Titulo"));
        assertThat(htmlCanvas.build(), Matchers.containsString("1 Sub Titulo"));
        assertThat(htmlCanvas.build(), Matchers.containsString("1.1 Sub Sub Titulo"));
        assertThat(htmlCanvas.build(), Matchers.containsString("1.2 Sub Sub Titulo"));
        assertEquals(childCanvas, childCanvas2);
    }

    @Test
    public void shouldCreateHeadersWithCorrectTagLevel() throws Exception {
        htmlCanvas.addSubtitle("Titulo");
        assertTrue(htmlCanvas.build().contains("<h1>Titulo"));
        htmlCanvas.addSubtitle("Titulo 2");
        htmlCanvas.addSubtitle("Titulo 3");
        assertTrue(htmlCanvas.build().contains("<h2>1 Titulo 2"));
        assertTrue(htmlCanvas.build().contains("<h2>2 Titulo 3"));
        DocumentCanvas childCanvas = htmlCanvas.addChild();
        childCanvas.addSubtitle("Titulo 4");
        assertTrue(htmlCanvas.build().contains("<h3>2.1 Titulo 4"));
        childCanvas = childCanvas.addChild();
        childCanvas.addSubtitle("Titulo 5");
        assertTrue(htmlCanvas.build().contains("<h4>2.1.1 Titulo 5"));
    }
}