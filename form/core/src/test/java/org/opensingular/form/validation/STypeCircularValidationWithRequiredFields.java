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

package org.opensingular.form.validation;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.sample.FormTestPackage;
import org.opensingular.form.type.core.STypeString;

@SInfoType(spackage = FormTestPackage.class, name = "STypeCircularValidationWithRequiredFields")
public class STypeCircularValidationWithRequiredFields extends STypeComposite<SIComposite> {//NOSONAR

    public STypeString whoever;
    public STypeString whatever;
    public STypeString whatelse;


    @Override
    protected void onLoadType(TypeBuilder tb) {
        whoever = this.addFieldString("whoever");
        whoever.asAtr().required(true);

        whatever = this.addFieldString("whatever");
        whatever.addInstanceValidator(val -> {
            if (val.getInstance().findNearest(whoever).get().isEmptyOfData()) {
                val.error("whatever with error");
            }
        });

        whatelse = this.addFieldString("whatelse");
        whatelse.asAtr().required(true);

        //full cyclic dependencies
        whatever.asAtr().dependsOn(whoever);
        whatever.asAtr().dependsOn(whatelse);

        whoever.asAtr().dependsOn(whatever);
        whoever.asAtr().dependsOn(whatelse);

        whatelse.asAtr().dependsOn(whoever);
        whatelse.asAtr().dependsOn(whatever);

    }
}