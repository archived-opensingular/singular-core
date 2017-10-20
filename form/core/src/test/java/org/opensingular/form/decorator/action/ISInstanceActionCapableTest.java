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

package org.opensingular.form.decorator.action;

import static java.util.Arrays.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import org.junit.Test;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

public class ISInstanceActionCapableTest {

    @Test
    public void test() {

        ListMultimap<Integer, ISInstanceActionsProvider> providers = Multimaps.newListMultimap(new LinkedHashMap<>(), ArrayList::new);
        ISInstanceActionCapable iac = new ISInstanceActionCapable() {
            @Override
            public void addSInstanceActionsProvider(int sortPosition, ISInstanceActionsProvider provider) {
                providers.put(sortPosition, provider);
            }
        };
        ISInstanceActionsProvider p0 = (t, i) -> Arrays.asList();
        ISInstanceActionsProvider p1 = (t, i) -> Arrays.asList();
        ISInstanceActionsProvider p2 = (t, i) -> Arrays.asList();
        ISInstanceActionsProvider p3 = (t, i) -> Arrays.asList();

        iac.addSInstanceActionsProvider(p0);
        iac.addSInstanceActionsProvider(p1);
        iac.addSInstanceActionsProvider(2, p2);
        iac.addSInstanceActionsProvider(3, p3);

        assertEquals(asList(p0, p1), providers.get(Integer.MIN_VALUE));
        assertEquals(asList(p2), providers.get(2));
        assertEquals(asList(p3), providers.get(3));
    }

}
