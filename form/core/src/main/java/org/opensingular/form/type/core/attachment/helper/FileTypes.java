package org.opensingular.form.type.core.attachment.helper;

import org.opensingular.form.type.basic.AtrBasic;

/**
 * Class that contains just constants for the Files Types.
 *
 * @see AtrBasic#allowedFileTypes(String...)
 */
public class FileTypes {
    
    //IMAGE
    public static final String PNG = "png";
    public static final String GIF = "gif";
    public static final String JPG = "JPG";

    //OFFICE Doc: https://docs.microsoft.com/pt-br/deployoffice/compat/office-file-format-reference
    public static final String DOC = "doc";    //Documento do Word 97-2003
    public static final String DOCX = "docx";  //Documento do Word
    public static final String PPTX = "pptx";  //PowerPoint Presentation
    public static final String PPT = "ppt";    //PowerPoint 97-2003 Presentation
    public static final String XLSX = "xlsx";  //Excel

    //OTHERS
    public static final String KML = "kml";
    public static final String PDF = "pdf";
    public static final String XML = "xml";

}
