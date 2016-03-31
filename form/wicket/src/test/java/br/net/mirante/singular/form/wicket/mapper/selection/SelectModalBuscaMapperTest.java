package br.net.mirante.singular.form.wicket.mapper.selection;

import java.util.List;
import java.util.stream.Collectors;

import br.net.mirante.singular.util.wicket.ajax.ActionAjaxLink;
import br.net.mirante.singular.util.wicket.datatable.column.BSActionPanel;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.FormComponent;
import org.junit.Test;

import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.basic.view.SViewSelectionBySearchModal;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.wicket.helpers.SingularFormBaseTest;


public class SelectModalBuscaMapperTest extends SingularFormBaseTest {

    STypeString mandatoryField;
    STypeString dependentField;

    @Override
    protected void buildBaseType(STypeComposite<?> baseType) {

        mandatoryField = baseType.addFieldString("mandatoryField", true).withSelectionOf("1", "2")
                .withView(SViewSelectionBySearchModal::new).cast();

        dependentField = baseType.addFieldString("dependentField");
        dependentField.asAtrBasic().dependsOn(mandatoryField);
        dependentField.asAtrBasic().visivel(ins -> StringUtils.isNotEmpty(ins.findNearestValue(mandatoryField, String.class).orElse(null)));

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

        AjaxLink link = findOnForm(AjaxLink.class, form.getForm(), al -> al.getId().equals("search_link"))
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