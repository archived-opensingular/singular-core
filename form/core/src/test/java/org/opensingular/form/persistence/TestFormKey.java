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

package org.opensingular.form.persistence;

import org.junit.Assert;
import org.junit.Test;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.internal.lib.commons.test.SingularTestUtil;

import javax.annotation.Nonnull;

/**
 * @author Daniel C. Bordin on 16/03/2017.
 */
public class TestFormKey {

    @Test
    public void readWithOutSetting() throws Exception {
        SIComposite instance = createInstance();
        testKeyEquals(null, instance);
        testKeyEquals(null, instance.getField("nome"));
    }

    @Test
    public void setAndSetBackToNull() throws Exception {
        SIComposite instance = createInstance();
        FormKey k = new FormKeyInt(10);
        FormKey.set(instance, k);
        testKeyEquals(k, instance);
        testKeyEquals(k, instance.getField("nome"));

        FormKey.set(instance, null);
        testKeyEquals(null, instance);
        testKeyEquals(null, instance.getField("nome"));
    }

    @Test
    public void setAndChanging() throws Exception {
        SIComposite instance = createInstance();

        FormKey k1 = new FormKeyInt(10);
        FormKey.set(instance, k1);
        testKeyEquals(k1, instance);
        testKeyEquals(k1, instance.getField("nome"));

        FormKey k2 = new FormKeyLong(20);
        FormKey.set(instance, k2);
        testKeyEquals(k2, instance);
        testKeyEquals(k2, instance.getField("nome"));
        testKeyNotEquals(k1, instance);
        testKeyNotEquals(k1, instance.getField("nome"));
    }

    @Test
    public void setChildMustSetRoot() {
        SIComposite instance = createInstance();
        FormKey k = new FormKeyInt(30);
        FormKey.set(instance.getField("nome"), k);
        testKeyEquals(k, instance);
        testKeyEquals(k, instance.getField("nome"));
    }

    private void testKeyEquals(FormKey expected, SInstance instance) {
        if (expected == null) {
            SingularTestUtil.assertException(() -> FormKey.from(instance), SingularNoFormKeyException.class);
            SingularTestUtil.assertException(() -> FormKey.from(instance.getDocument()), SingularNoFormKeyException.class);
            Assert.assertFalse(FormKey.fromOpt(instance).isPresent());
            Assert.assertFalse(FormKey.fromOpt(instance.getDocument()).isPresent());
        } else {
            Assert.assertEquals(expected, FormKey.from(instance));
            Assert.assertEquals(expected, FormKey.from(instance.getDocument()));
            Assert.assertEquals(expected, FormKey.fromOpt(instance).get());
            Assert.assertEquals(expected, FormKey.fromOpt(instance.getDocument()).get());
        }
    }

    private void  testKeyNotEquals(@Nonnull FormKey notExpected, SInstance instance) {
        Assert.assertNotEquals(notExpected, FormKey.from(instance));
        Assert.assertNotEquals(notExpected, FormKey.from(instance.getDocument()));
        Assert.assertNotEquals(notExpected, FormKey.fromOpt(instance).get());
        Assert.assertNotEquals(notExpected, FormKey.fromOpt(instance.getDocument()).get());
    }

    private SIComposite createInstance() {
        SDictionary dictionary = SDictionary.create();
        PackageBuilder pkg = dictionary.createNewPackage("teste");
        STypeComposite<SIComposite> pessoa = pkg.createCompositeType("pessoa");
        pessoa.addFieldString("nome");
        pessoa.addFieldInteger("idade");
        return pessoa.newInstance();
    }

}