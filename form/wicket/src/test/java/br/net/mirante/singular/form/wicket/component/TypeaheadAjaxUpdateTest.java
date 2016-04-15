package br.net.mirante.singular.form.wicket.component;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.options.SOptionsConfig;
import br.net.mirante.singular.form.mform.options.SOptionsProvider;
import br.net.mirante.singular.form.wicket.helpers.SingularFormBaseTest;
import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;
import org.apache.wicket.markup.html.form.FormComponent;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Optional;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Verifica se é possivel selecionar um valor apos
 * o componente de typeahead ser atualizado via ajax por outro componente.
 *
 * Ultima execução : 06/04/2016
 * Falhou: Sim
 */
@Ignore
public class TypeaheadAjaxUpdateTest extends SingularFormBaseTest {

    STypeString                 genero;
    STypeComposite<SIComposite> pessoa;

    @Override
    protected void buildBaseType(STypeComposite<?> baseType) {

        genero = baseType.addFieldString("genero");
        genero.withSelectionOf("Feminino", "Masculino");

        pessoa = baseType.addFieldComposite("pessoa");

        final STypeInteger idade = pessoa.addFieldInteger("id");
        final STypeString  nome  = pessoa.addFieldString("descricao");

        pessoa.withSelectionFromProvider(nome, new SOptionsProvider() {
            @Override
            public SIList<? extends SInstance> listOptions(SInstance optionsInstance, String filter) {
                final SIList<?>        list  = optionsInstance.getType().newList();
                final Optional<String> value = optionsInstance.findNearestValue(genero, String.class);
                if (value.isPresent()) {
                    if (value.get().equals("Masculino")) {
                        final SIComposite danilo = (SIComposite) list.addNew();
                        danilo.setValue(idade, 20);
                        danilo.setValue(nome, "Danilo");
                    } else {
                        final SIComposite francielle = (SIComposite) list.addNew();
                        francielle.setValue(idade, 19);
                        francielle.setValue(nome, "Francielle");
                    }
                    return list;
                }
                return null;
            }
        });
        pessoa.withView(SViewAutoComplete::new);
        pessoa.asAtrBasic()
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

    private void setAndCheckValue(FormComponent input) {
        SOptionsConfig optionsConfig = ((IMInstanciaAwareModel) input.getModel()).getMInstancia().getOptionsConfig();
        assertThat(optionsConfig.listSelectOptions()).hasSize(1);
        String key = optionsConfig.listSelectOptions()
                .keySet()
                .stream()
                .findFirst().orElseThrow(() -> new AssertionError("Lista vazia"));
        form.setValue(input, key);
        tester.executeAjaxEvent(input, "change");
        assertThat(input.getModel().getObject()).isNotNull();
    }
}
