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
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;

import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.opensingular.internal.lib.commons.test.SingularTestUtil.assertException;

/**
 * @author Daniel on 05/04/2017.
 */
public class FormKeyManagerTest {

    @Test
    public void keyFromString() throws Exception {
        FormKeyManager<FormKeyInt> fm = new FormKeyManager<>(FormKeyInt.class, null);
        Assert.assertEquals((Integer) 10, fm.keyFromString("10").intValue());

        assertException(() -> fm.keyFromString("XX"), SingularFormPersistenceException.class,
                "Erro criando FormKey para o valor");
    }

    @Test
    public void keyFromObject() throws Exception {
        FormKeyManager<FormKeyLong> fm = new FormKeyManager<>(FormKeyLong.class, null);
        Assert.assertEquals((Long) 10L, fm.keyFromObject("10").longValue());
        Assert.assertEquals((Long) 10L, fm.keyFromObject(10).longValue());
        Assert.assertEquals((Long) 10L, fm.keyFromObject(10L).longValue());
        Assert.assertEquals((Integer) 10, fm.keyFromObject(new FormKeyLong(10)).intValue());

        assertException(() -> fm.keyFromObject(new FormKeyInt("10")),
                SingularFormPersistenceException.class, "Erro convertendo valor");

        assertException(() -> fm.keyFromObject("XX"), SingularFormPersistenceException.class,
                "Erro convertendo valor");
        assertException(() -> fm.keyFromObject(new Date()), SingularFormPersistenceException.class,
                "Erro convertendo valor");
    }

    @Test
    public void checkKeyOrException() throws Exception {
        FormKeyManager<FormKeyInt> fm = new FormKeyManager<>(FormKeyInt.class, null);
        FormKeyInt key = new FormKeyInt(20);
        Assert.assertSame(key, fm.validKeyOrException(key));

        assertException(() -> fm.validKeyOrException(null), SingularFormPersistenceException.class,
                "O FormKey não pode ser null");
        assertException(() -> fm.validKeyOrException(new FormKeyLong(20)), SingularFormPersistenceException.class,
                "A chave encontrada incompatível");
    }

    @Test
    public void readKeyAttributeOrException() throws Exception {
        SInstance instance = cretateInstance();
        FormKeyManager<FormKeyInt> fm = new FormKeyManager<>(FormKeyInt.class, null);
        //Read Null
        assertException(() -> fm.readFormKeyOrException(instance), SingularFormPersistenceException.class, "Era esperado que a instância tivesse");
        //Read Wrong Class
        FormKey.set(instance, new FormKeyLong(10));
        assertException(() -> fm.readFormKeyOrException(instance), SingularFormPersistenceException.class, "A chave encontrada incompatível");
        //Ok
        FormKey key = new FormKeyInt(1);
        FormKey.set(instance, key);
        Assert.assertSame(key, fm.readFormKeyOrException(instance));
    }

    @Test
    public void readKeyAttributeOptional_and_isPresent() throws Exception {
        SInstance instance = cretateInstance();
        FormKeyManager<FormKeyInt> fm = new FormKeyManager<>(FormKeyInt.class, null);

        //Read Null
        assertFalse(fm.readFormKeyOptional(instance).isPresent());
        assertFalse(fm.isPersistent(instance));

        //Read Wrong Class
        FormKey.set(instance, new FormKeyLong(10));
        assertException(() -> fm.readFormKeyOptional(instance), SingularFormPersistenceException.class, "A chave encontrada incompatível");
        assertException(() -> fm.isPersistent(instance), SingularFormPersistenceException.class, "A chave encontrada incompatível");

        //Ok
        FormKey key = new FormKeyInt(1);
        FormKey.set(instance, key);
        Assert.assertSame(key, fm.readFormKeyOptional(instance).get());
        assertTrue(fm.isPersistent(instance));
    }

    private SIComposite cretateInstance() {
        STypeComposite<SIComposite> block = SDictionary.create().createNewPackage("test").createCompositeType("block");
        block.addFieldString("nome");
        return block.newInstance();
    }

    @Test
    public void wrongFormKey_withoutProperConstructor() {
        assertException(() -> new FormKeyManager<>(FormKeyWrong1.class, null), SingularFormPersistenceException.class,
                "Erro tentando obter o construtor");
    }

    @Test
    public void wrongFormKey_withoutConvertMethod() {
        assertException(() -> new FormKeyManager<>(FormKeyWrong2.class, null), SingularFormPersistenceException.class,
                "Erro tentando obter o metodo convertToKey");
    }

    @Test
    public void wrongFormKey_withWrongConvertMethod() {
        //It is not static
        assertException(() -> new FormKeyManager<>(FormKeyWrong3.class, null), SingularFormPersistenceException.class,
                "não é compatível com a assintura de método esperado");
        //It is not public
        assertException(() -> new FormKeyManager<>(FormKeyWrong4.class, null), SingularFormPersistenceException.class,
                "Erro tentando obter o metodo convertToKey");
        //Wrong return type
        assertException(() -> new FormKeyManager<>(FormKeyWrong5.class, null), SingularFormPersistenceException.class,
                "não é compatível com a assintura de método esperado");
    }

    public static class FormKeyWrong1 extends AbstractFormKey<Integer> {

        public FormKeyWrong1() {
            super((String) null);
        }

        @Override
        protected Integer parseValuePersistenceString(String persistenceString) {
            return null;
        }

        public static FormKeyWrong1 convertToKey(Object objectValueToBeConverted) {
            return null;
        }
    }

    public static class FormKeyWrong2 extends AbstractFormKey<Integer> {

        public FormKeyWrong2(String value) {
            super(value);
        }

        @Override
        protected Integer parseValuePersistenceString(String persistenceString) {
            return null;
        }
    }

    public static class FormKeyWrong3 extends AbstractFormKey<Integer> {

        public FormKeyWrong3(String value) {
            super(value);
        }

        @Override
        protected Integer parseValuePersistenceString(String persistenceString) {
            return null;
        }

        public FormKeyWrong3 convertToKey(Object objectValueToBeConverted) {
            return null;
        }
    }

    public static class FormKeyWrong4 extends AbstractFormKey<Integer> {

        public FormKeyWrong4(String value) {
            super(value);
        }

        @Override
        protected Integer parseValuePersistenceString(String persistenceString) {
            return null;
        }

        protected static FormKeyWrong4 convertToKey(Object objectValueToBeConverted) {
            return null;
        }
    }

    public static class FormKeyWrong5 extends AbstractFormKey<Integer> {

        public FormKeyWrong5(String value) {
            super(value);
        }

        @Override
        protected Integer parseValuePersistenceString(String persistenceString) {
            return null;
        }

        public static FormKeyLong convertToKey(Object objectValueToBeConverted) {
            return null;
        }
    }

}