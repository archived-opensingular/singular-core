package org.opensingular.singular.form.showcase.component.form.core;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.type.core.STypeHTML;
import org.opensingular.form.view.SViewByPortletRichText;
import org.opensingular.singular.form.showcase.component.CaseItem;
import org.opensingular.singular.form.showcase.component.Group;

/**
 * Permite a formatação de texto utilizando HTML.
 */
@CaseItem(componentName = "HTML", subCaseName = "Editor Rico em Nova Aba", group = Group.INPUT)
public class CaseInputCoreRichTextPackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        final STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");
        final STypeHTML         parecer    = tipoMyForm.addField("parecer", STypeHTML.class);
        parecer.setView(SViewByPortletRichText::new);
        parecer
                .asAtr()
                .label("Parecer Técnico");

    }

}