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

package org.opensingular.lib.wicket.util.model;

import static org.junit.Assert.*;
import static org.opensingular.lib.wicket.util.util.Shortcuts.*;

import java.util.Arrays;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.junit.Test;
import org.opensingular.lib.wicket.util.WicketUtilTester;

import com.google.common.collect.ImmutableMap;

public class NullOrEmptyModelTest {

    @Test
    public void test() {
        @SuppressWarnings({ "unchecked", "rawtypes" })
        IModel<Object> value = (IModel) $m.ofValue();

        NullOrEmptyModel model = new NullOrEmptyModel(value);
        assertTrue(model.getObject());

        value.setObject(1);
        assertFalse(model.getObject());
        assertTrue(model.not().getObject());

        value.setObject("");
        assertTrue(model.getObject());

        value.setObject("A");
        assertFalse(model.getObject());
        
        value.setObject(Arrays.asList());
        assertTrue(model.getObject());
        
        value.setObject(Arrays.asList('x'));
        assertFalse(model.getObject());
        
        value.setObject(ImmutableMap.of());
        assertTrue(model.getObject());
        
        value.setObject(ImmutableMap.of(1, 2));
        assertFalse(model.getObject());
        
        new WicketUtilTester();
        
        value.setObject(new Label("label"));
        assertTrue(model.getObject());
        
        value.setObject(new Label("label", "..."));
        assertFalse(model.getObject());
        
        model.detach();
    }

}
