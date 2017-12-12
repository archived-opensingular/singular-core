/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.flow.core.property;

import org.junit.Assert;
import org.junit.Test;
import org.opensingular.internal.lib.commons.test.SingularTestUtil;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author Daniel C. Bordin
 * @since 2017-10-27
 */
public class MetaDataEnabledTest {

    private static MetaDataKey<Boolean> KEY_WITH_DEFAULT = MetaDataKey.of("withDefault", Boolean.class, Boolean.FALSE);
    private static MetaDataKey<Boolean> KEY_WITHOUT_DEFAULT = MetaDataKey.of("withoutDefault", Boolean.class);

    @Test
    public void testDefaultIfValueIsNotSet() {

        MyClassWithMetaData c = new MyClassWithMetaData();

        Assert.assertFalse(c.getMetaDataOpt().isPresent());
        Assert.assertFalse(c.getMetaDataValueOpt(KEY_WITH_DEFAULT).get());
        Assert.assertFalse(c.getMetaDataValue(KEY_WITH_DEFAULT));

        Assert.assertFalse(c.getMetaDataOpt().isPresent());

        Assert.assertFalse(c.getMetaDataValueOpt(KEY_WITHOUT_DEFAULT).isPresent());
        SingularTestUtil.assertException(() -> c.getMetaDataValue(KEY_WITHOUT_DEFAULT),
                "don't have a default value configured");

        Assert.assertFalse(c.getMetaDataOpt().isPresent());
    }

    @Test
    public void testDefaultIfValueIsSet() {

        MyClassWithMetaData c = new MyClassWithMetaData();
        c.setMetaDataValue(KEY_WITH_DEFAULT, Boolean.TRUE);
        c.setMetaDataValue(KEY_WITHOUT_DEFAULT, Boolean.TRUE);

        Assert.assertTrue(c.getMetaDataOpt().isPresent());

        Assert.assertTrue(c.getMetaDataValueOpt(KEY_WITH_DEFAULT).get());
        Assert.assertTrue(c.getMetaDataValue(KEY_WITH_DEFAULT));

        Assert.assertTrue(c.getMetaDataValueOpt(KEY_WITHOUT_DEFAULT).get());
        SingularTestUtil.assertException(() -> c.getMetaDataValue(KEY_WITHOUT_DEFAULT),
                "don't have a default value configured");
    }

    @Test
    public void testDefaultIfValueIsSetAndThenRemoved() {

        MyClassWithMetaData c = new MyClassWithMetaData();
        c.setMetaDataValue(KEY_WITH_DEFAULT, Boolean.TRUE);
        c.setMetaDataValue(KEY_WITHOUT_DEFAULT, Boolean.TRUE);
        c.setMetaDataValue(KEY_WITH_DEFAULT, null);
        c.setMetaDataValue(KEY_WITHOUT_DEFAULT, null);

        Assert.assertFalse(c.getMetaDataValueOpt(KEY_WITH_DEFAULT).get());
        Assert.assertFalse(c.getMetaDataValue(KEY_WITH_DEFAULT));

        Assert.assertFalse(c.getMetaDataValueOpt(KEY_WITHOUT_DEFAULT).isPresent());
        SingularTestUtil.assertException(() -> c.getMetaDataValue(KEY_WITHOUT_DEFAULT),
                "don't have a default value configured");
    }


    private static class MyClassWithMetaData implements MetaDataEnabled {

        private MetaDataMap metaDataMap;

        @Nonnull
        @Override
        public MetaDataMap getMetaData() {
            if (metaDataMap == null) {
                metaDataMap = new MetaDataMap();
            }
            return metaDataMap;
        }

        @Nonnull
        @Override
        public Optional<MetaDataMap> getMetaDataOpt() {
            return Optional.ofNullable(metaDataMap);
        }
    }

}