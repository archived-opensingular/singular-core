package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.basic.view.SViewSearchModal;
import br.net.mirante.singular.form.mform.converter.ValueToSInstanceConverter;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.provider.FilteredPagedProvider;
import br.net.mirante.singular.form.wicket.helpers.SingularFormBaseTest;
import br.net.mirante.singular.util.wicket.ajax.ActionAjaxLink;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.FormComponent;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class SearchModalMapperTest extends SingularFormBaseTest {

    STypeString mandatoryField;
    STypeString dependentField;

    @Override
    protected void buildBaseType(STypeComposite<?> baseType) {

        mandatoryField = baseType.addFieldString("mandatoryField", true);

        mandatoryField.withView(new SViewSearchModal());
        mandatoryField.asAtrProvider().provider(new FilteredPagedProvider<String>() {
            @Override
            public void loadFilterDefinition(STypeComposite<?> filter) {
                filter.addFieldString("search");
            }

            @Override
            public Long getSize(SInstance rootInstance, SInstance filter) {
                return 2L;
            }

            @Override
            public List<String> load(SInstance rootInstance, SInstance filter, long first, long count) {
                return Arrays.asList("1", "2");
            }

            @Override
            public List<Column> getColumns() {
                return Collections.singletonList(Column.of("String"));
            }
        });
        mandatoryField.asAtrProvider().converter(new ValueToSInstanceConverter() {
            @Override
            public void toInstance(SInstance ins, Object obj) {
                ins.setValue(obj);
            }
        });
        dependentField = baseType.addFieldString("dependentField");
        dependentField.asAtrBasic().dependsOn(mandatoryField);
        dependentField.asAtrBasic().visible(ins -> StringUtils.isNotEmpty(ins.findNearestValue(mandatoryField, String.class).orElse(null)));

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

        Button link = findOnForm(Button.class, form.getForm(), al -> al.getId().equals("modalTrigger"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o link para abertura da modal"));

        tester.executeAjaxEvent(link, "click");

        List<AjaxLink> links = findOnForm(ActionAjaxLink.class, form.getForm(),
                al -> al.getId().equals("link"))
                .collect(Collectors.toList());

        tester.executeAjaxEvent(links.get(0), "click");

        tester.assertModelValue(mandatoryFieldComp.getPageRelativePath(), "1");
        tester.assertVisible(dependentFieldComp.getPageRelativePath());

    }
}