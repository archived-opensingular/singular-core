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

package org.opensingular.lib.commons.util;

import org.junit.Assert;
import org.junit.Test;
import org.opensingular.lib.commons.base.SingularException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ObjectUtilsTest {

    @Test
    public void IsAllNullTest(){
        String test = null;
        Assert.assertTrue(ObjectUtils.isAllNull(null, null, null, test));

        test = "not null anymore";
        Assert.assertFalse(ObjectUtils.isAllNull(null, null, null, test));
    }

    @Test
    public void newInstanceTest() {
        assertThatThrownBy(() -> ObjectUtils.newInstance("xpto", Collection.class)).isExactlyInstanceOf(
                SingularException.class).hasMessageContaining("Error loading class 'xpto'").hasCauseInstanceOf(
                ClassNotFoundException.class);

        assertThatThrownBy(() -> ObjectUtils.newInstance(String.class.getName(), Collection.class)).isExactlyInstanceOf(
                SingularException.class).hasMessageContaining(
                "The asked class 'java.lang.String' doesn't extends class 'java.util.Collection'");

        assertThatThrownBy(() -> ObjectUtils.newInstance(List.class.getName(), Collection.class)).isExactlyInstanceOf(
                SingularException.class).hasMessageContaining("Fail to instantiate class 'java.util.List'")
                .hasCauseInstanceOf(InstantiationException.class);

        assertThat(ObjectUtils.newInstance(ArrayList.class.getName(), Collection.class)).isNotNull();
    }

    @Test
    public void loadClassTest() {
        assertThatThrownBy(() -> ObjectUtils.loadClass("xpto", Collection.class)).isExactlyInstanceOf(
                SingularException.class).hasMessageContaining("Error loading class 'xpto'").hasCauseInstanceOf(
                ClassNotFoundException.class);

        assertThatThrownBy(() -> ObjectUtils.loadClass(String.class.getName(), Collection.class)).isExactlyInstanceOf(
                SingularException.class).hasMessageContaining(
                "The asked class 'java.lang.String' doesn't extends class 'java.util.Collection'");

        assertThat(ObjectUtils.loadClass(List.class.getName(), Collection.class)).isAssignableFrom(List.class);
        assertThat(ObjectUtils.loadClass(ArrayList.class.getName(), Collection.class)).isAssignableFrom(
                ArrayList.class);
    }

    @Test
    public void notNull() {
        String s1 = "1";
        String s2 = "2";
        Assert.assertEquals(s1, ObjectUtils.notNull(s1, s2));
        Assert.assertEquals(s2, ObjectUtils.notNull(null, s2));
        assertThatThrownBy(() -> ObjectUtils.notNull(s1, null)).isExactlyInstanceOf(SingularException.class)
                .hasMessageContaining("DefaultValue can't be null");
        assertThatThrownBy(() -> ObjectUtils.notNull(null, null)).isExactlyInstanceOf(SingularException.class)
                .hasMessageContaining("DefaultValue can't be null");
    }
}
