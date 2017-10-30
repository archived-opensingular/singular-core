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

package org.opensingular.form.wicket.component;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.provider.SimpleProvider;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensingular.form.wicket.AjaxUpdateListenersFactory.SINGULAR_PROCESS_EVENT;
/**
 * Verifica se é possivel selecionar um valor apos
 * o componente de typeahead ser atualizado via ajax por outro componente.
 * <p>
 * Ultima execução : 07/03/2017
 * Falhou: Não que eu saiba
 */
public class TypeaheadAjaxUpdateTest {
    private SingularDummyFormPageTester tester;

    private static STypeString                 genero;
    private static STypeComposite<SIComposite> pessoa;

    private static void buildBaseType(STypeComposite<?> baseType) {

        genero = baseType.addFieldString("genero");
        genero.selectionOf("Feminino", "Masculino");

        pessoa = baseType.addFieldComposite("pessoa");

        final STypeInteger idade = pessoa.addFieldInteger("idade");
        final STypeString  nome  = pessoa.addFieldString("nome");

        pessoa.autocompleteOf(Pessoa.class)
                .id(Pessoa::getNome)
                .display("${nome}")
                .autoConverterOf(Pessoa.class)
                .simpleProvider((SimpleProvider<Pessoa, SIComposite>) ins -> {
                    final Optional<String> genero1 = ins.findNearestValue(TypeaheadAjaxUpdateTest.genero);
                    if (genero1.isPresent()) {
                        if (genero1.get().equals("Masculino")) {
                            return Collections.singletonList(Pessoa.of(20, "Danilo"));
                        } else {
                            return Collections.singletonList(Pessoa.of(19, "Francielle"));
                        }
                    }
                    return null;
                });

        pessoa.asAtr()
                .dependsOn(genero)
                .visible(ins -> ins.findNearestValue(genero, String.class).isPresent());
    }

    @Before
    public void setUp(){
        tester = new SingularDummyFormPageTester();
        tester.getDummyPage().setTypeBuilder(TypeaheadAjaxUpdateTest::buildBaseType);
        tester.getDummyPage().setAsEditView();
        tester.startDummyPage();
    }

    @Test
    public void assertVisibility() {
        Component pessoaComponent = tester.getAssertionsForm().getSubComponentWithType(pessoa).getTarget();
        tester.assertInvisible(pessoaComponent.getPageRelativePath());

        tester.getAssertionsForm().getSubComponentWithType(pessoa).assertSInstance();

        DropDownChoice dropDown = tester.getAssertionsForm().getSubComponents(DropDownChoice.class).get(0).getTarget(DropDownChoice.class);

        tester.newFormTester().select(getFormRelativePath(dropDown), 0);
        tester.executeAjaxEvent(dropDown, SINGULAR_PROCESS_EVENT);

        tester.assertVisible(pessoaComponent.getPageRelativePath());
    }

    @Test
    public void assertUpdate() {
        DropDownChoice dropDownGenero =  tester.getAssertionsForm()
                .getSubComponents(DropDownChoice.class).get(0).getTarget(DropDownChoice.class);

        {
            tester.newFormTester().select(getFormRelativePath(dropDownGenero), 1);
            tester.executeAjaxEvent(dropDownGenero, SINGULAR_PROCESS_EVENT);

            setAndCheckValue();
        }

        {
            tester.newFormTester().select(getFormRelativePath(dropDownGenero), 0);
            tester.executeAjaxEvent(dropDownGenero, SINGULAR_PROCESS_EVENT);

            setAndCheckValue();
        }
    }

    private void setAndCheckValue() {
        Component inputNameComponent = tester.getAssertionsForm().getSubComponents(TextField.class).get(1).getTarget();

        tester.newFormTester().setValue(inputNameComponent, "Danilo");
        tester.executeAjaxEvent(inputNameComponent, SINGULAR_PROCESS_EVENT);

        List<SInstance> listITems = (List<SInstance>) tester.getAssertionsForm()
                .getSubComponentWithType(pessoa).assertSInstance().getTarget().getValue();
        assertThat(listITems.get(1).getValue()).isNotNull();
    }

    private String getFormRelativePath(FormComponent component) {
        return component.getPath().replace(component.getForm().getRootForm().getPath() + ":", StringUtils.EMPTY);
    }

    public static class Pessoa implements Serializable {

        private Integer idade;
        private String  nome;

        public static Pessoa of(Integer idade, String nome) {
            final Pessoa p = new Pessoa();
            p.idade = idade;
            p.nome = nome;
            return p;
        }

        public Integer getIdade() {
            return idade;
        }

        public void setIdade(Integer idade) {
            this.idade = idade;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }
    }

}
