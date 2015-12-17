package br.net.mirante.singular.form.mform;


import br.net.mirante.singular.commons.base.SingularException;

public class SingularFormException extends SingularException {

    public SingularFormException() {
    }

    public SingularFormException(String msg) {
        super(msg);
    }

    public SingularFormException(String msg, Throwable cause) {
        super(msg, cause);
    }


    public SingularFormException(String msg, Exception e) {
        super(msg, e);
    }
}
