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

package org.opensingular.form.dependson;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.sample.FormTestPackage;
import org.opensingular.form.type.core.SIInteger;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.country.brazil.STypeUF;

@SInfoType(spackage = FormTestPackage.class, name = "STypeDependsOnByClass")
public class STypeDependsOnByClass extends STypeComposite<SIComposite> {//NOSONAR

    public STypeUF uf1;
    public STypeUF uf2;
    public STypeString nome;
    public STypeInteger updateListenerCalls;


    @Override
    protected void onLoadType(TypeBuilder tb) {

        uf1 = this.addField("uf1", STypeUF.class);
        uf2 = this.addField("uf2", STypeUF.class);
        updateListenerCalls = this.addFieldInteger("updateListenerCalls");
        updateListenerCalls.withInitListener(s -> s.setValue(0));

        nome = this.addFieldString("nome");
        nome.asAtr().dependsOn(STypeUF.class);
        nome.withUpdateListener(s -> {
            SIInteger counter = s.findNearest(updateListenerCalls).get();
            counter.setValue(counter.getInteger() + 1);
        });

    }
}

