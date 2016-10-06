package org.opensingular.form.wicket.mapper.selection;

import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.provider.Config;
import org.opensingular.form.provider.FilteredProvider;
import org.opensingular.form.provider.ProviderContext;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SViewSearchModal;
import org.opensingular.form.wicket.IWicketComponentMapper;
import org.opensingular.form.wicket.helpers.SingularFormBaseTest;
import org.opensingular.form.wicket.mapper.search.SearchModalPanel;
import org.opensingular.lib.wicket.util.ajax.ActionAjaxLink;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.FormComponent;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class SearchModalMapperTest extends SingularFormBaseTest {

    private STypeString mandatoryField;
    private STypeString dependentField;

    @Override
    protected void buildBaseType(STypeComposite<?> baseType) {

        mandatoryField = baseType.addFieldString("mandatoryField", true);

        mandatoryField.withView(new SViewSearchModal());
        mandatoryField.asAtrProvider().filteredProvider(new FilteredProvider<String>() {
            @Override
            public void configureProvider(Config cfg) {
                cfg.getFilter().addFieldString("search");
                cfg.result().addColumn("String");
            }

            @Override
            public List<String> load(ProviderContext<SInstance> context) {
                return Arrays.asList("1", "2");
            }
        });
        dependentField = baseType.addFieldString("dependentField");
        dependentField.asAtr().dependsOn(mandatoryField);
        dependentField.asAtr().visible(ins -> StringUtils.isNotEmpty(ins.findNearestValue(mandatoryField, String.class).orElse(null)));

    }

    @Test
    public void testIfChooseValueInModelUpdatesDependentComponent() {

        FormComponent dependentFieldComp = findFormComponentsByType(dependentField)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o componente dependente"));

        FormComponent mandatoryFieldComp = findFormComponentsByType(mandatoryField)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o componente mandatorio"));

        tester.assertInvisible(dependentFieldComp.getPageRelativePath());

        Button link = findOnForm(Button.class, form.getForm(), al -> al.getId().equals(SearchModalPanel.MODAL_TRIGGER_ID))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o link para abertura da modal"));

        tester.executeAjaxEvent(link, "click");

        List<AjaxLink> links = findOnForm(ActionAjaxLink.class, form.getForm(),
                al -> al.getId().equals("link"))
                .collect(Collectors.toList());

        tester.executeAjaxEvent(links.get(0), "click");

        tester.assertModelValue(mandatoryFieldComp.getPageRelativePath(), "1");

        tester.executeAjaxEvent(mandatoryFieldComp, IWicketComponentMapper.SINGULAR_PROCESS_EVENT);

        tester.assertVisible(dependentFieldComp.getPageRelativePath());

    }
}