package br.net.mirante.singular.form.mform.core;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.io.FormSerializationUtil;
import br.net.mirante.singular.form.mform.io.MformPersistenciaXML;
import br.net.mirante.singular.form.util.xml.MElement;
import br.net.mirante.singular.form.util.xml.MParser;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class STypeCompositeTest {

    private STypeComposite<? extends SIComposite> baseType, subStuff;
    private STypeString name, content;

    @Before public void setup() {
        SDictionary dict = SDictionary.create();
        PackageBuilder pkt = dict.createNewPackage("pkt");
        baseType = pkt.createCompositeType("baseType");
        name = baseType.addFieldString("name");
        subStuff = baseType.addFieldComposite("subStuff");
        content = subStuff.addFieldString("content");
    }

    @Test public void setCompositeValue() throws Exception{
        SIComposite original = baseType.newInstance();
        original.getDescendant(name).setValue("My first name");
        original.getDescendant(content).setValue("My first content");

        assertThat(xml(original)).contains("My first name").contains("My first content");

        String backup = xml(original.getDescendant(subStuff));

        assertThat(xml(original.getDescendant(subStuff)))
                .doesNotContain("My first name").contains("My first content");

        original.getDescendant(name).setValue("My second name");
        original.getDescendant(content).setValue("My second content");

        assertThat(original.getDescendant(name).getValue()).isEqualTo("My second name");
        assertThat(original.getDescendant(content).getValue()).isEqualTo("My second content");

        original.getDescendant(subStuff)
                .setValue(MformPersistenciaXML.fromXML(subStuff, MParser.parse(backup)));

        assertThat(original.getDescendant(name).getValue()).isEqualTo("My second name");
        assertThat(original.getDescendant(content).getValue()).isEqualTo("My first content");

    }

    private String xml(SIComposite original) {
        return MformPersistenciaXML.toXML(original).toString();
    }

}
