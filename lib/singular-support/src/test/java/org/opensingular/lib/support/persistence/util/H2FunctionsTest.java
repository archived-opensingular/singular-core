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

package org.opensingular.lib.support.persistence.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;

public class H2FunctionsTest {

    @Test
    public void diffTest(){
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.YEAR, 2017);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Calendar calendarToCompare = (Calendar) calendar.clone();
        calendarToCompare.set(Calendar.DAY_OF_MONTH, 1);
        calendarToCompare.set(Calendar.MONTH, Calendar.FEBRUARY);

        Double diff = H2Functions.dateDiffInDays(calendarToCompare.getTime(), calendar.getTime());

        Assert.assertEquals(31, diff.intValue());

        Assert.assertEquals(0, H2Functions.dateDiffInDays(null, null).intValue());
    }

    @Test
    public void notNullTest(){
        Assert.assertNotNull(new H2Functions());
    }
}
