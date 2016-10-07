package org.opensingular.singular.form.showcase.component.form.core;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.type.core.STypeHTML;
import org.opensingular.singular.form.showcase.component.CaseItem;
import org.opensingular.singular.form.showcase.component.Group;

@CaseItem(componentName = "HTML", subCaseName = "Editor Rico", group = Group.INPUT)
public class CaseInputCorePortletRichTextPackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        final STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");
        final STypeHTML         parecer    = tipoMyForm.addField("parecer", STypeHTML.class);
        parecer
                .asAtr()
                .label("Parecer Técnico");
    }

}