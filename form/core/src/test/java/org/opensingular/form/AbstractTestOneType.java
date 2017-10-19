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

package org.opensingular.form;

import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;

/**
 * Apoio para teste voltado para uma única classe de SType
 *
 * @author Daniel C. Bordin
 */
public abstract class AbstractTestOneType<TYPE extends SType<?>, INSTANCE extends SInstance> extends TestCaseForm {

    private final Class<TYPE> typeClass;

    public AbstractTestOneType(TestFormConfig testFormConfig, Class<TYPE> typeClass) {
        super(testFormConfig);
        this.typeClass = typeClass;
    }

    protected final INSTANCE newInstance() {
        final Class<TYPE> c = typeClass;
        RefType refType = RefType.of(() -> createTestDictionary().getType(c));
        return (INSTANCE) SDocumentFactory.empty().createInstance(refType);
    }
}
