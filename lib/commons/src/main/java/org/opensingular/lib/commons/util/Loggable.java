package org.opensingular.lib.commons.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Loggable {

    default Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }

}
