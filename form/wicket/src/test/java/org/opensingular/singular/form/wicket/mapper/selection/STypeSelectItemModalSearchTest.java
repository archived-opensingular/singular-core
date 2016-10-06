package org.opensingular.singular.form.wicket.mapper.selection;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.converter.ValueToSICompositeConverter;
import org.opensingular.form.provider.Config;
import org.opensingular.form.provider.FilteredProvider;
import org.opensingular.form.provider.ProviderContext;
import org.opensingular.form.view.SViewSearchModal;
import org.opensingular.singular.form.wicket.helpers.SingularFormBaseTest;
import org.opensingular.singular.form.wicket.mapper.search.SearchModalPanel;
import org.opensingular.singular.util.wicket.ajax.ActionAjaxLink;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.Button;
import org.junit.Assert;
import org.junit.Test;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class STypeSelectItemModalSearchTest extends SingularFormBaseTest {

    private STypeComposite<SIComposite> notebook;

    @Override
    protected void buildBaseType(STypeComposite<?> baseType) {

        notebook = baseType.addFieldComposite("notebook");

        notebook.addFieldString("marca");
        notebook.addFieldString("memoria");
        notebook.addFieldString("disco");
        notebook.addFieldString("sistemaOperacional");

        notebook.withView(new SViewSearchModal());
        notebook.asAtrProvider().filteredProvider(new FilteredProvider<Notebook>() {
            @Override
            public void configureProvider(Config cfg) {
                cfg.getFilter().addFieldString("marca");
                cfg.getFilter().addFieldString("sistemaOperacional");
                cfg.result().addColumn("marca", "Marca")
                        .addColumn("memoria", "Memoria")
                        .addColumn("disco", "Disco")
                        .addColumn("sistemaOperacional", "Sistema Operacional");
            }

            @Override
            public List<Notebook> load(ProviderContext<SInstance> context) {
                return Arrays.asList(new Notebook("Apple", "4GB", "1T", "OSX"), new Notebook("Samsug", "8GB", "1TB", "ArchLinux"));
            }
        });

        notebook.asAtrProvider().converter((ValueToSICompositeConverter<Notebook>) (ins, note) -> {
            ins.setValue("marca", note.marca);
            ins.setValue("memoria", note.memoria);
            ins.setValue("disco", note.disco);
            ins.setValue("sistemaOperacional", note.sistemaOperacional);
        });
    }

    private static class Notebook implements Serializable {

        String marca;
        String memoria;
        String disco;
        String sistemaOperacional;

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
    public void testSelection() {

        Button link = findOnForm(Button.class, form.getForm(), al -> al.getId().equals(SearchModalPanel.MODAL_TRIGGER_ID))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o link para abertura da modal"));

        tester.executeAjaxEvent(link, "click");

        List<AjaxLink> links = findOnForm(ActionAjaxLink.class, form.getForm(),
                al -> al.getId().equals("link"))
                .collect(Collectors.toList());

        tester.executeAjaxEvent(links.get(0), "click");

        final SIComposite currentInstance = page.getCurrentInstance();
        final SIComposite notebok         = (SIComposite) currentInstance.getField(notebook.getNameSimple());

        Assert.assertEquals(notebok.getField("marca").getValue(), "Apple");
        Assert.assertEquals(notebok.getField("memoria").getValue(), "4GB");
        Assert.assertEquals(notebok.getField("disco").getValue(), "1T");
        Assert.assertEquals(notebok.getField("sistemaOperacional").getValue(), "OSX");


    }
}
