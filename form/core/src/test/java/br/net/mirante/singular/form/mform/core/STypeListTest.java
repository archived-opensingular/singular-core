package br.net.mirante.singular.form.mform.core;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SList;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeLista;
import br.net.mirante.singular.form.mform.io.MformPersistenciaXML;
import br.net.mirante.singular.form.util.xml.MParser;

public class STypeListTest {

    private STypeComposite<? extends SIComposite> baseType;
    private STypeString name, content;
    private STypeLista<STypeComposite<SIComposite>, SIComposite> listType;

    @Before
    public void setup() {
        SDictionary dict = SDictionary.create();
        PacoteBuilder pkt = dict.criarNovoPacote("pkt");
        baseType = pkt.createTipoComposto("baseType");
        name = baseType.addCampoString("name");
        listType = baseType.addCampoListaOfComposto("listField", "subStuff");
        content = listType.getTipoElementos().addCampoString("content");
    }

    @Test
    public void setCompositeValue() throws Exception{
        SIComposite original = baseType.novaInstancia();
        original.getDescendant(name).setValor("My first name");
        SIComposite e1 = original.getDescendant(listType).addNovo();
        e1.getDescendant(content).setValor("My first content");

        assertThat(xml(original)).contains("My first name").contains("My first content");

        String backup = xml(original.getDescendant(listType));

        assertThat(backup).doesNotContain("My first name").contains("My first content");

        original.getDescendant(name).setValor("My second name");
        e1.getDescendant(content).setValor("My second content");

        assertThat(xml(original)).contains("My second name").contains("My second content");

        SList<SIComposite> fromBackup = MformPersistenciaXML.fromXML(listType, MParser.parse(backup));
        original.getDescendant(listType).setValor(fromBackup);

        assertThat(xml(original)).contains("My second name").contains("My first content");

    }

    private String xml(SInstance original) {
        return MformPersistenciaXML.toXML(original).toString();
    }
}
