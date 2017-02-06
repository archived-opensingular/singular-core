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

package org.opensingular.singular.form.showcase.component.form.interaction;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.type.core.STypeBoolean;
import org.opensingular.form.type.core.STypeDate;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.singular.form.showcase.component.CaseItem;
import org.opensingular.singular.form.showcase.component.Group;

/**
 * Exibe campos visíveis dinamicamente.
 */
@CaseItem(componentName = "Enabled, Visible, Required", subCaseName = "Visible", group = Group.INTERACTION)
public class CaseInteractionVisiblePackage extends SPackage {

    public STypeComposite<?> testForm;
    public STypeBoolean visible;
    public STypeComposite<SIComposite> record;
    public STypeString recordText;
    public STypeDate recordDate;

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        super.onLoadPackage(pb);

        testForm = pb.createCompositeType("testForm");

        visible = testForm.addFieldBoolean("visible");

        record = testForm.addFieldComposite("record");
        recordText = record.addFieldString("text");
        recordDate = record.addFieldDate("date");

        visible.asAtr().label("Visible");

        record.asAtr()
                .visible(ins -> ins.findNearestValue(visible, Boolean.class).orElse(Boolean.FALSE))
                .dependsOn(visible);

        recordText.asAtr()
                .label("Text")
                .asAtrBootstrap().colPreference(3);

        recordDate.asAtr()
                .label("Date")
                .asAtrBootstrap().colPreference(2);
    }
}
