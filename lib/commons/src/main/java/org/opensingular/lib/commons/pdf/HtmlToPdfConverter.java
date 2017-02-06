package org.opensingular.lib.commons.pdf;

import org.opensingular.lib.commons.dto.HtmlToPdfDTO;
import org.opensingular.lib.commons.util.Loggable;

import java.io.File;
import java.util.Optional;

public interface HtmlToPdfConverter extends Loggable {

    Optional<File> convert(HtmlToPdfDTO htmlToPdfDTO);

}