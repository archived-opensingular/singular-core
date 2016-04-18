package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.basic.view.SViewSearchModal;
import br.net.mirante.singular.form.mform.converter.ValueToSInstanceConverter;
import br.net.mirante.singular.form.mform.provider.FilteredPagedProvider;
import br.net.mirante.singular.form.wicket.helpers.SingularFormBaseTest;
import br.net.mirante.singular.util.wicket.ajax.ActionAjaxLink;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.Button;
import org.junit.Assert;
import org.junit.Test;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class STypeSelectItemModalSearchTest extends SingularFormBaseTest {

    STypeComposite<SIComposite> notebook;

    @Override
    protected void buildBaseType(STypeComposite<?> baseType) {

        notebook = baseType.addFieldComposite("notebook");

        notebook.addFieldString("marca");
        notebook.addFieldString("memoria");
        notebook.addFieldString("disco");
        notebook.addFieldString("sistemaOperacional");

        notebook.withView(new SViewSearchModal());
        notebook.asAtrProvider().provider(new FilteredPagedProvider<Notebook>() {
            @Override
            public void loadFilterDefinition(STypeComposite<?> filter) {
                filter.addFieldString("marca");
                filter.addFieldString("sistemaOperacional");
            }

            @Override
            public Long getSize(SInstance rootInstance, SInstance filter) {
                return 2L;
            }

            @Override
            public List<Notebook> load(SInstance rootInstance, SInstance filter, long first, long count) {
                return Arrays.asList(new Notebook("Apple", "4GB", "1T", "OSX"), new Notebook("Samsug", "8GB", "1TB", "ArchLinux"));
            }

            @Override
            public List<Column> getColumns() {
                return Arrays.asList(
                        Column.of("marca", "Marca"),
                        Column.of("memoria", "Memoria"),
                        Column.of("disco", "Disco"),
                        Column.of("sistemaOperacional", "Sistema Operacional")
                );
            }
        });
        notebook.asAtrProvider().converter(new ValueToSInstanceConverter<Notebook>() {
            @Override
            public void toInstance(SInstance ins, Notebook note) {
                ((SIComposite) ins).setValue("marca", note.marca);
                ((SIComposite) ins).setValue("memoria", note.memoria);
                ((SIComposite) ins).setValue("disco", note.disco);
                ((SIComposite) ins).setValue("sistemaOperacional", note.sistemaOperacional);
            }
        });
    }

    private static class Notebook implements Serializable {

        protected String marca;
        protected String memoria;
        protected String disco;
        protected String sistemaOperacional;

        Notebook(String marca, String memoria, String disco, String sistemaOperacional) {
            this.marca = marca;
            this.memoria = memoria;
            this.disco = disco;
            this.sistemaOperacional = sistemaOperacional;
        }

        public String getMarca() {
            return marca;
        }

        public String getMemoria() {
            return memoria;
        }

        public String getDisco() {
            return disco;
        }

        public String getSistemaOperacional() {
            return sistemaOperacional;
        }
    }

    @Test
    public void testSelection(){

        Button link = findOnForm(Button.class, form.getForm(), al -> al.getId().equals("modalTrigger"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o link para abertura da modal"));

        tester.executeAjaxEvent(link, "click");

        List<AjaxLink> links = findOnForm(ActionAjaxLink.class, form.getForm(),
                al -> al.getId().equals("link"))
                .collect(Collectors.toList());

        tester.executeAjaxEvent(links.get(0), "click");

        final SIComposite currentInstance = page.getCurrentInstance();
        final SIComposite notebok = (SIComposite) currentInstance.getField(notebook.getNameSimple());

        Assert.assertEquals(notebok.getField("marca").getValue(), "Apple");
        Assert.assertEquals(notebok.getField("memoria").getValue(), "4GB");
        Assert.assertEquals(notebok.getField("disco").getValue(), "1T");
        Assert.assertEquals(notebok.getField("sistemaOperacional").getValue(), "OSX");


    }
}
