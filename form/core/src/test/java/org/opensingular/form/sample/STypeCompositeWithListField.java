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
import org.opensingular.form.STypeList;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.view.SViewByBlock;

@SInfoType(spackage = FormTestPackage.class,  name = "STypeCompositeWithListField")
public class STypeCompositeWithListField extends STypeComposite<SIComposite> {


    public static final String EMBARCACOES_FIELD_NAME = "theList";
    public STypeList<STypeFirstListElement, SIComposite> theList;

    @Override
    protected void onLoadType(TypeBuilder tb) {

        theList = this.addFieldListOf(EMBARCACOES_FIELD_NAME, STypeFirstListElement.class);
        theList.withMiniumSizeOf(1);
        theList.withInitListener(list -> list.addNew());
        theList.asAtr().label("Embarcações");

        this.withView(new SViewByBlock(), v -> v.newBlock("Foo Foo").add(theList));
    }
}
