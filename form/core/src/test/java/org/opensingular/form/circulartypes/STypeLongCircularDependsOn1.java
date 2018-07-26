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

package org.opensingular.form.circulartypes;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.sample.FormTestPackage;
import org.opensingular.form.type.core.STypeString;

@SInfoType(spackage = FormTestPackage.class, name = "STypeLongCircularDependsOn1")
public class STypeLongCircularDependsOn1 extends STypeComposite<SIComposite> {//NOSONAR


    public STypeString whatever1;

    public STypeLongCircularDependsOn2 sTypeLongCircularDependsOn2;


    @Override
    protected void onLoadType(TypeBuilder tb) {
        whatever1 = this.addFieldString("whatever1");
        whatever1.asAtr().dependsOn();

        sTypeLongCircularDependsOn2 = this.addField("sTypeLongCircularDependsOn2", STypeLongCircularDependsOn2.class);

        whatever1.asAtr().dependsOn(sTypeLongCircularDependsOn2.whatever1);

        whatever1.withUpdateListener(siString -> siString.setValue(siString.getValue() + "1"));
    }
}
