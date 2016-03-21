package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.SViewSelectionBySearchModal;
import br.net.mirante.singular.form.mform.options.SOptionsProvider;
import br.net.mirante.singular.form.mform.options.SSelectionableInstance;
import br.net.mirante.singular.form.wicket.helpers.SingularFormBaseTest;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.List;

import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Enclosed.class)
public class STypeSelectItemModalSearchTest {

    private static class Base extends SingularFormBaseTest {
        protected STypeComposite selectType;
        protected SViewSelectionBySearchModal view;
        protected STypeSimple nomeUF;

        protected void buildBaseType(STypeComposite group) {
            selectType = group.addFieldComposite("originUF");
            selectType.addFieldString("id");
            nomeUF = selectType.addFieldString("nome");
            selectType.addFieldInteger("population").as(AtrBasic::new).label("População");
            selectType.addFieldInteger("areasqrkm").as(AtrBasic::new).label("Área");
            selectType.addFieldInteger("phonecode").as(AtrBasic::new).label("DDD");
            selectType.addFieldDecimal("gdp").as(AtrBasic::new).label("PIB");
            selectType.addFieldDecimal("hdi").as(AtrBasic::new).label("IDH");
            view = (SViewSelectionBySearchModal)
                    selectType.setView(SViewSelectionBySearchModal::new);
        }

        protected SIList<?> novoProvider(SSelectionableInstance... selects) {
            SIList lista = selectType.newList();
            for (SSelectionableInstance s : selects) {
                lista.addElement(s);
            }
            return lista;
        }

        protected SSelectionableInstance federaldistrict() {
            SIComposite df = (SIComposite) selectType.newInstance();
            df.setValue("id", "DF");
            df.setValue("nome", "Distrito Federal");
            df.setValue("population", 2852372);
            df.setValue("areasqrkm", 5802);
            df.setValue("phonecode", 61);
            df.setValue("gdp", 189800000000l);
            df.setValue("hdi", 0.824);
            return df;
        }

        protected SSelectionableInstance goias() {
            SIComposite go = (SIComposite) selectType.newInstance();
            go.setValue("id", "GO");
            go.setValue("nome", "Goiás");
            go.setValue("population", 6155998);
            go.setValue("areasqrkm", 340086);
            go.setValue("phonecode", 62);
            go.setValue("gdp", 57091000000l);
            go.setValue("hdi", 0.735);
            return go;
        }

        protected void clickOpenLink() {
            List<Component> search_link1 = findTag(form.getForm(), "search_link", AjaxLink.class);
            tester.executeAjaxEvent(search_link1.get(0), "onclick");
        }
    }

    public static class Default extends Base  {

        @Override
        protected void buildBaseType(STypeComposite group) {
            super.buildBaseType(group);
            selectType.withSelectionFromProvider(nomeUF,
                    (SOptionsProvider) inst -> novoProvider(federaldistrict(), goias()));
        }

        @Test public void showModalWhenClicked() {
            tester.assertContainsNot("Buscar");
            clickOpenLink();

            tester.assertContains("Buscar");

            tester.assertContains("Distrito Federal");
            tester.assertContains("Goiás");
        }
    }


    public static class WithAditionalFields extends Base {

        @Override
        protected void buildBaseType(STypeComposite group) {
            super.buildBaseType(group);
            selectType.withSelectionFromProvider(nomeUF,
                    (SOptionsProvider) inst -> novoProvider(federaldistrict(), goias()));
            view.setAdditionalFields("population", "phonecode");
        }

        @Test
        public void showModalWithExtrafields() {
            tester.assertContainsNot("Buscar");

            clickOpenLink();

            tester.assertContains("Buscar");

            tester.assertContains("População");
            tester.assertContains("DDD");

            tester.assertContains("Distrito Federal");
            tester.assertContains("2852372");
            tester.assertContains("61");
            tester.assertContains("Goiás");
            tester.assertContains("6155998");
            tester.assertContains("62");
        }
    }

}
