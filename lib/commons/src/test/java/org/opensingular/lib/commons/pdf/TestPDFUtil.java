/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.lib.commons.pdf;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Daniel C. Bordin on 22/01/2017.
 */
public class TestPDFUtil {

    private static final String SIMPLE_EXAMPLE_HTML = "<html><body>Hello Word</body><htmll>";

    @Before
    public void setupEnviroment() {
        Assume.assumeTrue(!"false".equals(System.getProperty("singular.test.native.enabled")));
        PDFUtil.clearHome();
        File dir;
        if (PDFUtil.isWindows()) {
            dir = new File("src\\test\\wkhtmltopdf\\windows");
        } else if (PDFUtil.isMac()){
            dir = new File("src/test/wkhtmltopdf/mac");
        } else {
            dir = new File("src/test/wkhtmltopdf/unix");
        }
        System.setProperty(PDFUtil.SINGULAR_WKHTML2PDF_HOME, dir.getAbsolutePath());
    }

    @Test
    public void testHomeDirNotConfigured() {
        PDFUtil.clearHome();
        System.clearProperty(PDFUtil.SINGULAR_WKHTML2PDF_HOME);
        try {
            PDFUtil.getInstance().convertHTML2PDF(SIMPLE_EXAMPLE_HTML);
            fail();
        } catch (SingularPDFException e) {
            assertException(e, "property 'singular.wkhtml2pdf.home' not set");
        }
    }

    @Test
    public void testWrongHomeDirConfigured() {
        try {
            PDFUtil.clearHome();
            System.setProperty(PDFUtil.SINGULAR_WKHTML2PDF_HOME, File.separator + "xpto");
            File result = PDFUtil.getInstance().convertHTML2PDF(SIMPLE_EXAMPLE_HTML);
            System.out.println(result.getAbsolutePath());
            fail();
        } catch (SingularPDFException e) {
            assertException(e, "configured for a directory that nos exists");
        }
    }

    @Test
    public void testSimpleToPdf() {
        File result = PDFUtil.getInstance().convertHTML2PDF(SIMPLE_EXAMPLE_HTML);
        assertPDF(result, 1);
    }

    @Test
    public void testSimpleToPng() {
        File result = PDFUtil.getInstance().convertHTML2PNG(SIMPLE_EXAMPLE_HTML);
        assertFile(result, ".png");
    }

    @Test
    public void testMergeSimple() throws FileNotFoundException {
        File pg1 = PDFUtil.getInstance().convertHTML2PDF(SIMPLE_EXAMPLE_HTML);
        File pg2 = PDFUtil.getInstance().convertHTML2PDF(SIMPLE_EXAMPLE_HTML);
        File pg3 = PDFUtil.getInstance().convertHTML2PDF(SIMPLE_EXAMPLE_HTML);

        File result = PDFUtil.getInstance().merge(
                Lists.newArrayList(new FileInputStream(pg1), new FileInputStream(pg2), new FileInputStream(pg3)));
        assertPDF(result, 3);
    }

    private void assertPDF(File file, int expectedPages) {
        assertPDF(file, expectedPages, true);
    }

    private void assertPDF(File file, int expectedPages, boolean delete) {
        assertFile(file, ".pdf");
        assertEquals(expectedPages, countPages(file));
        if(delete) {
            if(!file.delete()) {
                fail();
            }
        }
    }

    private int countPages(File file) {
        try (RandomAccessBufferedFileInputStream in = new RandomAccessBufferedFileInputStream(file)) {
            PDFParser parser = new PDFParser(in);
            parser.parse();
            PDDocument doc = parser.getPDDocument();
            int pages = doc.getNumberOfPages();
            doc.close();
            return pages;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void assertFile(File result, String expectedExtension) {
        assertTrue(result.exists());
        assertTrue(result.length() > 0);
        assertTrue(result.getName().endsWith(expectedExtension));
    }


    private void assertException(Exception e, String expectedContent) {
        if (!e.getMessage().contains(expectedContent)) {
            assertEquals(expectedContent, e.getMessage());
        }
    }

    @Test
    public void createPDFSepartingHeaderBodyAndFooterTest(){
        String header = "<header><title>Test</title></header>";
        String body = "<body><div><span>Some string to fill</span></div><body>";
        String footer = "<footer></footer>";

        PDFUtil pdfUtil = PDFUtil.getInstance();
        pdfUtil.setPageOrientation(PDFUtil.PageOrientation.PAGE_LANDSCAPE);
        pdfUtil.setPageSize(PDFUtil.PageSize.PAGE_A4);
        pdfUtil.setJavascriptDelay(0);

        File instance = pdfUtil.convertHTML2PDF(header, body, footer, new ArrayList<>());

        Assert.assertTrue(instance.exists());
        instance.delete();
    }
}
