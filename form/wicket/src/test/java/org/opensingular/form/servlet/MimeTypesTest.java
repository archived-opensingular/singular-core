package org.opensingular.form.servlet;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class MimeTypesTest {

    @Test
    public void getMimeTypeForExtension(){
        assertThat(MimeTypes.getMimeTypeForExtension("tei")).isEqualTo("application/tei+xml");
        assertThat(MimeTypes.getMimeTypeForExtension("mp4a")).isEqualTo("audio/mp4");
        assertThat(MimeTypes.getMimeTypeForExtension("html")).isEqualTo("text/html");
        assertThat(MimeTypes.getMimeTypeForExtension("pdf")).isEqualTo("application/pdf");
    }

    @Test
    public void getExtensionForMimeType(){
        assertThat(MimeTypes.getExtensionsForMimeType("application/tei+xml")).contains("tei");
        assertThat(MimeTypes.getExtensionsForMimeType("audio/mp4")).contains("mp4a");
        assertThat(MimeTypes.getExtensionsForMimeType("text/html")).contains("html");
        assertThat(MimeTypes.getExtensionsForMimeType("application/pdf")).contains("pdf");
    }
}
