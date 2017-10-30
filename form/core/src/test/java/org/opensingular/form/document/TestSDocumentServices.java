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

package org.opensingular.form.document;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TestCaseForm;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.form.type.core.attachment.STypeAttachment;
import org.opensingular.lib.commons.context.RefService;
import org.opensingular.lib.commons.context.ServiceRegistryLocator;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class TestSDocumentServices extends TestCaseForm {

    private STypeComposite<?> groupingType;
    private SIAttachment      fileFieldInstance;
    private SDocument         document;

    public TestSDocumentServices(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Before
    public void setUp() {
        ServiceRegistryLocator.setup(new ServiceRegistryLocator());
        createTypes(createTestPackage());
        createInstances();
    }

    private void createTypes(PackageBuilder pb) {
        groupingType = pb.createCompositeType("Grouping");
        groupingType.addField("anexo", STypeAttachment.class);
        groupingType.addFieldInteger("justIgnoreThis");
    }

    private void createInstances() {
        SIComposite instance = (SIComposite) groupingType.newInstance();
        document = instance.getDocument();
        fileFieldInstance = (SIAttachment) instance.getAllChildren().iterator().next();
    }

    @SuppressWarnings({"rawtypes", "serial"})
    private RefService ref(final Object provider) {
        return new RefService() {
            public Object get() {
                return provider;
            }
        };
    }

    @Test
    public void findsRegisteredServiceByName() {
        final Object provider = new Object();
        document.bindLocalService("something", Object.class, ref(provider));

        assertThat(document.lookupLocalService("something", Object.class).orElse(null))
                .isSameAs(provider);
    }

    @Test
    public void doesNotConfusesNames() {
        document.bindLocalService("something", Object.class, ref(new Object()));

        assertThat(document.lookupLocalService("nothing", Object.class).orElse(null))
                .isNull();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findsRegisteredServiceByClass() {
        final Object provider = new Object();
        document.bindLocalService(Object.class, ref(provider));

        assertThat(document.lookupLocalService(Object.class).orElse(null)).isSameAs(provider);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findsRegisteredServiceByClassWhenIsSubtype() {
        final Integer provider = new Integer(1);
        document.bindLocalService(Integer.class, ref(provider));

        assertThat(document.lookupLocalService(Number.class).orElse(null)).isSameAs(provider);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = Exception.class)
    public void rejectsFindByClassWhenThereAreMoreThanOneOptions() {
        final Object provider = new Object();
        document.bindLocalService(Object.class, ref(provider));
        document.bindLocalService(Object.class, ref(provider));

        document.lookupLocalService(Object.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void doesNotAceptsSubclasses() {
        document.bindLocalService(Object.class, ref(new Object()));

        assertThat(document.lookupLocalService(String.class).orElse(null)).isNull();
    }

    @Test
    public void usesAddedRegistriesForLookupByName() {
        Object          provider = new Object();
        document.bindLocalService("another", Object.class, () -> provider);


        assertThat(document.lookupLocalService("another", Object.class).orElse(null))
                .isEqualTo(provider);


    }

    @Test
    public void usesAddedRegistriesForLookupByClass() {
        Object                  provider = new Object();
        document.bindLocalService(Object.class, () -> provider);

        assertThat(document.lookupLocalService(Object.class).orElse(null)).isEqualTo(provider);
    }

}
