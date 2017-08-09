package org.opensingular.ws.wkhtmltopdf.client;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.opensingular.lib.commons.dto.HtmlToPdfDTO;
import org.opensingular.lib.commons.pdf.HtmlToPdfConverter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

public class MockHtmlToPdfConverter implements HtmlToPdfConverter {

    @Override
    public Optional<File> convert(HtmlToPdfDTO htmlToPdfDTO) {
        return Optional.ofNullable(mock()).map(Path::toFile);
    }

    @Override
    public InputStream convertStream(HtmlToPdfDTO htmlToPdfDTO) {
        return null;
    }

    /**
     * Extraido de com modificacoes
     * https://pdfbox.apache.org/1.8/cookbook/documentcreation.html
     */
    private Path mock() {

        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage();
            PDFont font = PDType1Font.COURIER;
            Path tempFile = Files.createTempFile("singular-mock" + UUID.randomUUID(), ".pdf");

            int fontSize = 12;
            int tx = 100;
            int ty = 700;

            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.newLineAtOffset(tx, ty);
            contentStream.showText("Aplicação em modo de desenvolvimento.");
            contentStream.endText();
            contentStream.close();

            document.save(tempFile.toFile());

            return tempFile;

        } catch (IOException ex) {
            getLogger().error("Não foi possivel escrever o pdf mock", ex);
        }

        return null;
    }

}
