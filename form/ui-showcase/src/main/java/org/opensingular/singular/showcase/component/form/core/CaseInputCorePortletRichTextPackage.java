package org.opensingular.singular.showcase.component.form.core;

import org.opensingular.singular.form.PackageBuilder;
import org.opensingular.singular.form.SPackage;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.type.core.STypeHTML;
import org.opensingular.singular.showcase.component.CaseItem;
import org.opensingular.singular.showcase.component.Group;

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