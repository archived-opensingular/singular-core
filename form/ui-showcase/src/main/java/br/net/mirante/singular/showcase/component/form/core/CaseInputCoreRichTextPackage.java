package br.net.mirante.singular.showcase.component.form.core;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.type.core.STypeHTML;
import br.net.mirante.singular.form.view.SViewByPortletRichText;
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