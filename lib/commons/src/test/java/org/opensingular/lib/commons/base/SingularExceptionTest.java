/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

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
