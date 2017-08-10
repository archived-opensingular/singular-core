package org.opensingular.lib.commons.report;

import java.io.Serializable;

public interface ReportFilter extends Serializable{

    /**
     * Load the XML into the filter
     * @param XML the content
     */
    void load(String XML);

    /**
     * Extract XML content
     * @return the content
     */
    String dumpXML();

    /**
     * Set parameters
     * @param key the param key
     * @param val the param value
     */
    void setParam(String key, Object val);

    /**
     * Get the value of a parameter
     * @param key the param key
     * @return the value
     */
    Object getParam(String key);
}