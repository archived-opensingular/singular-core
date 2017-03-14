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
        assertThat(MimeTypes.getExtensionForMimeType("application/tei+xml")).isEqualTo("tei");
        assertThat(MimeTypes.getExtensionForMimeType("audio/mp4")).isEqualTo("mp4a");
        assertThat(MimeTypes.getExtensionForMimeType("text/html")).isEqualTo("html");
        assertThat(MimeTypes.getExtensionForMimeType("application/pdf")).isEqualTo("pdf");
    }
}
