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

package org.opensingular.form.sample;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.view.SViewTab;
import org.opensingular.lib.commons.base.SingularProperties;

@SInfoType(spackage = FormTestPackage.class, name = "STypeFormTest")
public class STypeFormTest extends STypeComposite<SIComposite> {//NOSONAR


    public final static boolean OBRIGATORIO       = !SingularProperties.get().isTrue(SingularProperties.SINGULAR_DEV_MODE);
    public final static int     QUANTIDADE_MINIMA = OBRIGATORIO ? 1 : 0;

    public STypeCompositeWithListField compositeWithListField;
    public STypeAnotherComposite       anotherComposite;


    @Override
    protected void onLoadType(TypeBuilder tb) {

        this.asAtr().label("Foo")
                .displayString("Bar");


        compositeWithListField = this.addField("compositeWithListField", STypeCompositeWithListField.class);
        anotherComposite = this.addField("anotherComposite", STypeAnotherComposite.class);


        SViewTab tabbed = new SViewTab();

        tabbed.addTab("compositeWithListField", "Embarcações").add(compositeWithListField);
        tabbed.addTab("anexoCA", "Esquema Operacional").add(anotherComposite);
        withView(tabbed);


    }
}

