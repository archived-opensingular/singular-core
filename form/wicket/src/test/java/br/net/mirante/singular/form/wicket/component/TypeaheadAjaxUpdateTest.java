package br.net.mirante.singular.form.wicket.component;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.provider.SimpleProvider;
import br.net.mirante.singular.form.type.core.STypeInteger;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.wicket.helpers.SingularFormBaseTest;
import org.apache.wicket.markup.html.form.FormComponent;
import org.junit.Test;

import java.io.Serializable;
import java.util.Collections;
import java.util.Optional;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Verifica se é possivel selecionar um valor apos
 * o componente de typeahead ser atualizado via ajax por outro componente.
 * <p>
 * Ultima execução : 08/04/2016
 * Falhou: Não que eu saiba
 */
public class TypeaheadAjaxUpdateTest extends SingularFormBaseTest {

    STypeString                 genero;
    STypeComposite<SIComposite> pessoa;

    @Override
    protected void buildBaseType(STypeComposite<?> baseType) {

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
                    final Optional<String> genero1 = ins.findNearestValue(TypeaheadAjaxUpdateTest.this.genero);
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

    @Test
    public void assertVisibility() {
        final FormComponent selecaoGenero = findFirstFormComponentsByType(page.getForm(), genero);
        final FormComponent input         = findFirstFormComponentsByType(page.getForm(), pessoa);
        tester.assertInvisible(input.getPageRelativePath());
        form.select(getFormRelativePath(selecaoGenero), 0);
        tester.executeAjaxEvent(selecaoGenero, "change");
        tester.assertVisible(input.getPageRelativePath());
    }

    @Test
    public void assertUpdate() {

        FormComponent selecaoGenero = findFirstFormComponentsByType(page.getForm(), genero);
        FormComponent input         = findFirstFormComponentsByType(page.getForm(), pessoa);

        {
            form.select(getFormRelativePath(selecaoGenero), 1);
            tester.executeAjaxEvent(selecaoGenero, "change");
            setAndCheckValue(input);
        }

        {
            form.select(getFormRelativePath(selecaoGenero), 0);
            tester.executeAjaxEvent(selecaoGenero, "change");
            setAndCheckValue(input);
        }

    }

    @Test
    public void assertSave() {

        FormComponent selecaoGenero = findFirstFormComponentsByType(page.getForm(), genero);
        FormComponent input         = findFirstFormComponentsByType(page.getForm(), pessoa);

        {
            form.select(getFormRelativePath(selecaoGenero), 1);
            tester.executeAjaxEvent(selecaoGenero, "change");
            setAndCheckValue(input);
        }

        {
            form.select(getFormRelativePath(selecaoGenero), 0);
            tester.executeAjaxEvent(selecaoGenero, "change");
            setAndCheckValue(input);
        }

        assertThat(input.getModel().getObject()).isNotNull();
    }

    private void setAndCheckValue(FormComponent input) {
        form.setValue(input, "Danilo");
        tester.executeAjaxEvent(input, "change");
        assertThat(input.getModel().getObject()).isNotNull();
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
