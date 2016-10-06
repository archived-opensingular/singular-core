package br.net.mirante.singular.showcase.component.form.core;

import org.opensingular.singular.form.PackageBuilder;
import org.opensingular.singular.form.SPackage;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.type.core.STypeHTML;
import org.opensingular.singular.form.view.SViewByPortletRichText;
import br.net.mirante.singular.showcase.component.CaseItem;
import br.net.mirante.singular.showcase.component.Group;

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