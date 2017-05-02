package org.opensingular.lib.commons.base;

import org.junit.Assert;
import org.junit.Test;

public class SingularExceptionTest {

    @Test(expected = SingularException.class)
    public void throwDefaultException(){
        throw new SingularException();
    }

    @Test(expected = SingularException.class)
    public void throwableException(){
        throw  new SingularException(new NullPointerException());
    }

    @Test
    public void rethrowException(){
        Assert.assertNotNull(SingularException.rethrow(new NullPointerException()));
    }

    @Test
    public void rethrowExceptionWithMsg(){
        SingularException singularException = new SingularException(new NullPointerException());

        Assert.assertNotNull(SingularException.rethrow("erro", singularException));
    }

    @Test
    public void containsEntryTest(){
        SingularException singularException = new SingularException(new NullPointerException());
        Assert.assertFalse(singularException.containsEntry("NotValidValue"));
    }

    @Test
    public void addMsgToException(){
        SingularException singularException = new SingularException(new NullPointerException());

        singularException.add("new line to exception");
        singularException.add("label", "value");
        singularException.add("label" , null);

        String message = singularException.getMessage();

        Assert.assertTrue(message.contains("java.lang.NullPointerException"));
        Assert.assertTrue(message.contains(": new line to exception"));
        Assert.assertTrue(message.contains("label: value"));
        Assert.assertTrue(message.contains("label: null"));
    }
}
