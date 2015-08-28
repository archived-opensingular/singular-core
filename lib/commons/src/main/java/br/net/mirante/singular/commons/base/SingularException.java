package br.net.mirante.singular.commons.base;

/**
 * The base class of all runtime exceptions for Singular.
 *
 */
public class SingularException extends RuntimeException{

    /**
     * Constructs a new <code>SingularException</code> without specified
     * detail message.
     */
    public SingularException() {
        super();
    }

    /**
     * Constructs a new <code>SingularException</code> with specified
     * detail message.
     *
     * @param msg the error message
     */
    public SingularException(String msg) {
        super(msg);
    }

    /**
     * Constructs a new <code>SingularException</code> with specified
     * cause <code>Throwable</code>.
     *
     * @param cause the exception or error that caused this exception to be
     * thrown
     */
    public SingularException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new <code>SingularException</code> with specified
     * detail message and cause <code>Throwable</code>.
     *
     * @param msg    the error message
     * @param cause  the exception or error that caused this exception to be
     * thrown
     */
    public SingularException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
