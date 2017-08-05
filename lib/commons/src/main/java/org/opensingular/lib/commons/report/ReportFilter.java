package org.opensingular.lib.commons.report;

import java.io.Serializable;

public interface ReportFilter extends Serializable{

    void load(String XML);

    String dumpXML();

}