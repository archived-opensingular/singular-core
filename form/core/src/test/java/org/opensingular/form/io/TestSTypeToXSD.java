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

package org.opensingular.form.io;

import org.junit.Test;
import org.opensingular.form.ICompositeType;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SType;
import org.opensingular.form.io.sample.STypeExemplo;

public class TestSTypeToXSD {


    @Test
    public void testPercorrerSType() {
        STypeExemplo exemplo = SDictionary.create().getType(STypeExemplo.class);
        printRecursivo(exemplo);


        //        System.out.println(FormXSDUtil.toXsd(exemplo, FormToXSDConfig.newForWebServiceDefinition()));
    }

    private void printRecursivo(SType<?> type) {
        System.out.println(type.getName());
        if (type instanceof ICompositeType) {
            ICompositeType compositeType = (ICompositeType) type;
            for (SType<?> sType : compositeType.getContainedTypes()) {
                printRecursivo(sType);
            }
        }
    }
}
