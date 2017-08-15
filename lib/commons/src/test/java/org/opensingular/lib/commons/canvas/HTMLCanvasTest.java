package org.opensingular.lib.commons.canvas;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class HTMLCanvasTest {

    HTMLCanvas htmlCanvas;

    @Before
    public void setUp() throws Exception {
        htmlCanvas = new HTMLCanvas(true);
    }

    @Test
    public void shouldReturnEmpty() throws Exception {
        assertEquals("<div></div>", htmlCanvas.build());
    }

    @Test
    public void shouldContainsTitle() {
        htmlCanvas.addTitle("Danilo");
        assertThat(htmlCanvas.build(), Matchers.containsString("Danilo"));
    }

    @Test
    public void shouldContainsTitleFirstTitleLevel() {
        htmlCanvas.addTitle("Titulo");
        htmlCanvas.addTitle("Sub Titulo");
        htmlCanvas.addTitle("Sub Titulo 2");
        htmlCanvas.addTitle("Sub Titulo 3");
        htmlCanvas.addTitle("Sub Titulo 4");
        assertThat(htmlCanvas.build(), Matchers.containsString("Titulo"));
        assertThat(htmlCanvas.build(), Matchers.containsString("1 Sub Titulo"));
        assertThat(htmlCanvas.build(), Matchers.containsString("2 Sub Titulo 2"));
        assertThat(htmlCanvas.build(), Matchers.containsString("3 Sub Titulo 3"));
        assertThat(htmlCanvas.build(), Matchers.containsString("4 Sub Titulo 4"));
    }

    @Test
    public void shouldContainsMultipleTitleLevel() throws Exception {
        htmlCanvas.addTitle("Titulo");
        htmlCanvas.addTitle("Sub Titulo");
        DocumentCanvas childCanvas = htmlCanvas.newChild();
        childCanvas.addTitle("Sub Sub Titulo");
        childCanvas.addTitle("Sub Sub Titulo");
        htmlCanvas.addTitle("Sub Titulo");
        childCanvas = htmlCanvas.newChild();
        childCanvas.addTitle("Sub Sub Titulo");
        childCanvas.addTitle("Sub Sub Titulo");
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
        htmlCanvas.addTitle("Titulo");
        htmlCanvas.addTitle("Sub Titulo");
        DocumentCanvas childCanvas = htmlCanvas.newChild();
        childCanvas.addTitle("Sub Sub Titulo");
        DocumentCanvas childCanvas2 = htmlCanvas.newChild();
        childCanvas2.addTitle("Sub Sub Titulo");
        assertThat(htmlCanvas.build(), Matchers.containsString("Titulo"));
        assertThat(htmlCanvas.build(), Matchers.containsString("1 Sub Titulo"));
        assertThat(htmlCanvas.build(), Matchers.containsString("1.1 Sub Sub Titulo"));
        assertThat(htmlCanvas.build(), Matchers.containsString("1.2 Sub Sub Titulo"));
        assertEquals(childCanvas, childCanvas2);
    }

    @Test
    public void shouldCreateHeadersWithCorrectTagLevel() throws Exception {
        htmlCanvas.addTitle("Titulo");
        assertTrue(htmlCanvas.build().contains("<h1>Titulo"));
        htmlCanvas.addTitle("Titulo 2");
        htmlCanvas.addTitle("Titulo 3");
        assertTrue(htmlCanvas.build().contains("<h2>1 Titulo 2"));
        assertTrue(htmlCanvas.build().contains("<h2>2 Titulo 3"));
        DocumentCanvas childCanvas = htmlCanvas.newChild();
        childCanvas.addTitle("Titulo 4");
        assertTrue(htmlCanvas.build().contains("<h3>2.1 Titulo 4"));
        childCanvas = childCanvas.newChild();
        childCanvas.addTitle("Titulo 5");
        assertTrue(htmlCanvas.build().contains("<h4>2.1.1 Titulo 5"));
    }
}