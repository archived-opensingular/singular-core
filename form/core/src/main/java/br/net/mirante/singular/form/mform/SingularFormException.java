package br.net.mirante.singular.form.mform;

public class SingularFormException extends RuntimeException {

    public SingularFormException() {
    }

    public SingularFormException(String msg) {
        super(msg);
    }

    public SingularFormException(String msg, Exception e) {
        super(msg, e);
    }
}
