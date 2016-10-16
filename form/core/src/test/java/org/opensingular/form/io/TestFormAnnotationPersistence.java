package org.opensingular.form.io;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.*;
import org.opensingular.form.internal.xml.MElement;

import java.util.Optional;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author Daniel Bordin
 */

@RunWith(Parameterized.class)
public class TestFormAnnotationPersistence extends TestCaseForm {

    public TestFormAnnotationPersistence(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test
    public void testAnnotationForCompositeFieldThatIsNotPersisted() {
        SIComposite instance = (SIComposite) createSerializableTestInstance("teste.order", pacote -> {
            STypeComposite<? extends SIComposite> order = pacote.createCompositeType("order");
            order.addFieldString("description");
            order.addFieldString("complement").asAtrAnnotation().setAnnotated();

            STypeComposite<? extends SIComposite> address = order.addFieldComposite("address");
            address.asAtrAnnotation().setAnnotated();
            address.addFieldString("street").asAtrAnnotation().setAnnotated();

            STypeList<STypeComposite<SIComposite>, SIComposite> itens = order.addFieldListOfComposite("itens", "item");
            itens.asAtrAnnotation().setAnnotated();
            STypeComposite<SIComposite> item = itens.getElementsType();
            item.asAtrAnnotation().setAnnotated();
            item.addFieldString("name").asAtrAnnotation().setAnnotated();
            item.addFieldString("qtd").asAtrAnnotation().setAnnotated();
        });
        instance.setValue("description", "Travel supplies");
        instance.getField("description").asAtrAnnotation().text("ok");
        instance.getField("complement").asAtrAnnotation().text("fill the complement");
        instance.getField("address").asAtrAnnotation().text("fill the address");
        instance.getField("address.street").asAtrAnnotation().text("fill the street");

        SIList<SIComposite> itens = (SIList<SIComposite>) instance.getFieldList("itens");
        SIComposite item = itens.addNew();
        item.getField("qtd").asAtrAnnotation().text("qtd[0] is blank");
        item = itens.addNew();
        item.getField("name").setValue("passaport");
        item.getField("name").asAtrAnnotation().text("ok");
        item.getField("qtd").asAtrAnnotation().text("qtd[1] is blank");

        MElement xmlInstance = MformPersistenciaXML.toXML(instance);
        xmlInstance.printTabulado();
        MElement xmlAnnotation = MformPersistenciaXML.annotationToXml(instance).get();
        xmlAnnotation.printTabulado();

        //It's expected to be only persisted the field with value
        assertEquals(2,xmlInstance.countFilhos());
        assertEquals("Travel supplies",xmlInstance.getValor("description"));
        assertEquals(1,xmlInstance.getElement("itens").countFilhos());

        assertInstance(instance).isAnnotationTextEquals("description","ok");
        assertInstance(instance).isAnnotationTextEquals("complement","fill the complement");
        assertInstance(instance).isAnnotationTextEquals("address","fill the address");
        assertInstance(instance).isAnnotationTextEquals("address.street","fill the street");
        assertInstance(instance).isList("itens",2);
        assertInstance(instance).isAnnotationTextEquals("itens[0].qtd","qtd[0] is blank");
        assertInstance(instance).isAnnotationTextEquals("itens[1].qtd","qtd[1] is blank");
        assertInstance(instance).isAnnotationTextEquals("itens[1].name","ok");

        assertEquals(7,xmlAnnotation.countFilhos());

        SIComposite instance2 = MformPersistenciaXML.fromXML(instance.getType(), xmlInstance);
        MformPersistenciaXML.annotationLoadFromXml(instance2, xmlAnnotation);

        assertInstance(instance2).isAnnotationTextEquals("description","ok");
        assertInstance(instance2).isAnnotationTextEquals("complement","fill the complement");
        assertInstance(instance2).isAnnotationTextEquals("address","fill the address");
        assertInstance(instance2).isAnnotationTextEquals("address.street","fill the street");
        assertInstance(instance2).isList("itens",1);
        assertInstance(instance2).isAnnotationTextEquals("itens[0].qtd","qtd[1] is blank");
        assertInstance(instance2).isAnnotationTextEquals("itens[0].name","ok");
    }
}
