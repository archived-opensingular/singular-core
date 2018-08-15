/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package org.opensingular.form.wicket.mapper.masterdetail;

import org.opensingular.form.SIComposite;

import javax.annotation.Nonnull;
import java.time.YearMonth;

public class TestSIMasterDetail extends SIComposite {


    @Nonnull
    @Override
    public STypeTestMasterDetail getType() {
        return (STypeTestMasterDetail) super.getType();
    }

    public YearMonth getDataInicio() {
        return getField(getType().inicio).getValue();
    }

    public void setDataInicio(YearMonth dataInicio) {
        getField(getType().inicio).setValue(dataInicio);
    }
}
