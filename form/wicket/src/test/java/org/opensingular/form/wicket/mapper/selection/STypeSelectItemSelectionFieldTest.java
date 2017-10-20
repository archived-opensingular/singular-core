/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.wicket.mapper.selection;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.provider.SSimpleProvider;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.wicket.helpers.AssertionsWComponentList;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;

import static org.fest.assertions.api.Assertions.assertThat;

public class STypeSelectItemSelectionFieldTest {
    private SingularDummyFormPageTester tester;

    private static STypeComposite selectType;
    private static STypeSimple    nomeUF;
    private static STypeString    idUF;

    private static void buildBaseTypeBase(STypeComposite<?> baseType){
        selectType = (STypeComposite) baseType.addFieldComposite("originUF");
        idUF = selectType.addFieldString("id");
        nomeUF = selectType.addFieldString("nome");

        selectType.selection()
                .id(idUF)
                .display(nomeUF)
                .simpleProvider(newProviderFrom(newSelectItem("DF", "Distrito Federal"), newSelectItem("SP", "São Paulo")));
    }

    private static Pair newSelectItem(String id, String descricao) {
        return Pair.of(id, descricao);
    }

    private static SSimpleProvider newProviderFrom(Pair... pairs) {
        return builder -> {
            for (Pair p : pairs) {
                builder.add().set(idUF, p.getKey()).set(nomeUF, p.getValue());
            }
        };
    }

    @Before
    public void setUp(){
        tester = new SingularDummyFormPageTester();
        tester.getDummyPage().setTypeBuilder(STypeSelectItemSelectionFieldTest::buildBaseTypeBase);
    }

    @Test
    public void rendersField() {
        tester.startDummyPage();

        tester.assertEnabled(
                tester.getAssertionsForm().getSubCompomentWithId("originUF").getTarget().getPageRelativePath());
    }

    @Test
    public void rendersAnDropDownWithSpecifiedOptionsByName(){
        tester.startDummyPage();

        AssertionsWComponentList dropDownAssertion = tester.getAssertionsForm().getSubComponents(DropDownChoice.class);
        dropDownAssertion.isSize(1);

        DropDownChoice choices = dropDownAssertion.get(0).getTarget(DropDownChoice.class);

        assertThat(choices.getChoices()).hasSize(2);
        assertThat(choices.getChoiceRenderer().getIdValue(choices.getChoices().get(0), 0)).isEqualTo("DF");
        assertThat(choices.getChoiceRenderer().getDisplayValue(choices.getChoices().get(0))).isEqualTo("Distrito Federal");
        assertThat(choices.getChoiceRenderer().getIdValue(choices.getChoices().get(1), 1)).isEqualTo("SP");
        assertThat(choices.getChoiceRenderer().getDisplayValue(choices.getChoices().get(1))).isEqualTo("São Paulo");
    }

    @Test
    public void submitsSelectedValue() {
        tester.startDummyPage();

        tester.newFormTester()
                .select(getFormRelativePath((DropDownChoice)tester.getAssertionsForm().getSubCompomentWithId("originUF").getTarget()), 0)
                .submit();

        tester.getAssertionsForm().getSubCompomentWithType(selectType).assertSInstance().field(idUF.getNameSimple()).isValueEquals("DF");
    }

    @Test
    public void rendersAnDropDownWithDanglingOptions() {
        tester.getDummyPage().addInstancePopulator(instance->{
            SIComposite value = (SIComposite) instance.getAllFields().get(0);
            value.setValue("id", "GO");
            value.setValue("nome", "Goias");
        });
        tester.startDummyPage();

        DropDownChoice choices = tester.getAssertionsForm()
                .getSubComponents(DropDownChoice.class).get(0).getTarget(DropDownChoice.class);

        assertThat(choices.getChoiceRenderer().getIdValue(choices.getChoices().get(0), 0)).isEqualTo("GO");
        assertThat(choices.getChoiceRenderer().getDisplayValue(choices.getChoices().get(0))).isEqualTo("Goias");
    }

    @Test
    public void alsoWorksWhenFieldIsMandatory() {
        tester.getDummyPage().setTypeBuilder( tb->{
            buildBaseTypeBase(tb);
            selectType.asAtr().required(true);
        });
        tester.startDummyPage();

        tester.newFormTester()
                .select(getFormRelativePath((DropDownChoice)tester.getAssertionsForm().getSubCompomentWithId("originUF").getTarget()), 0)
                .submit();

        tester.getAssertionsForm().getSubCompomentWithType(selectType).assertSInstance().field(idUF.getNameSimple()).isValueEquals("DF");
    }

    @Test
    public void verifiyIfSelectLabelIsCorrect() {
        tester.startDummyPage();

        tester.newFormTester()
                .select(getFormRelativePath((DropDownChoice)tester.getAssertionsForm().getSubCompomentWithId("originUF").getTarget()), 0)
                .submit();

        tester.getAssertionsForm().getSubCompomentWithType(selectType)
                .assertSInstance().field(nomeUF.getNameSimple()).isValueEquals("Distrito Federal");
    }

    private String getFormRelativePath(FormComponent component) {
        return component.getPath().replace(component.getForm().getRootForm().getPath() + ":", StringUtils.EMPTY);
    }
}
