package org.opensingular.lib.commons.pdf;

import java.io.InputStream;

import org.opensingular.lib.commons.dto.HtmlToPdfDTO;
import org.opensingular.lib.commons.util.Loggable;
/**
 * Conversor de Html para PDF <br>
 * 
 */
public interface HtmlToPdfConverter extends Loggable {

    /**
     * Converte o {@link HtmlToPdfDTO} em pdf e retorna um stream para recuperar o arquivo pdf.
     * 
     * @param htmlToPdfDTO
     * @return
     */
    InputStream convert(HtmlToPdfDTO htmlToPdfDTO);

}