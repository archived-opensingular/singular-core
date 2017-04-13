package org.opensingular.lib.commons.pdf;

import org.junit.Test;

public class PDFExceptionTest {

    @Test(expected = SingularPDFException.class)
    public void withoutArgumentTest(){
        throw new SingularPDFException();
    }

    @Test(expected = SingularPDFException.class)
    public void withThrowableTest(){
        throw new SingularPDFException(new NullPointerException());
    }

    @Test(expected = SingularPDFException.class)
    public void withMsgAndThrowableTest(){
        throw new SingularPDFException("nullTest", new NullPointerException());
    }
}
