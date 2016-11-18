package org.opensingular.server.commons.spring.security.config.cas.util;

/**
 * The type Sso config exception.
 */
public class SSOConfigException extends RuntimeException {
    /**
     * Instantiates a new Sso config exception.
     *
     * @param s the s
     * @param e the e
     */
    public SSOConfigException(String s, Exception e) {
        super(s, e);
    }
}
