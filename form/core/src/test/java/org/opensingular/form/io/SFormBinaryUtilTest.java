package org.opensingular.form.io;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.TestCaseForm;
import org.opensingular.internal.lib.commons.util.DebugOutput;
import org.opensingular.internal.lib.commons.util.DebugOutputTable;
import org.opensingular.internal.lib.commons.util.SingularIOUtils;
import org.opensingular.internal.lib.commons.xml.MElement;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author Daniel C. Bordin
 * @since 2018-10-12
 */
@RunWith(Parameterized.class)
public class SFormBinaryUtilTest extends TestCaseForm {

    public SFormBinaryUtilTest(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test
    public void simple() {
        SInstance original = createTestInstance(5, 5, 5, 2, 3, 4);
        assertInstance(writeAndRead(original)).isEquivalentInstance(original).isNotSameAs(original);
    }

    @Test
    @Ignore
    public void testPerformance() {
        testPerformance(createTestInstance(5, 5, 5, 0, 0, 0), 100);
        testPerformance(createTestInstance(5, 5, 5, 0, 0, 0), 100);
        testPerformance(createTestInstance(5, 40, 15, 1, 3, 4), 100);
        testPerformance(createTestInstance(5, 40, 15, 1, 3, 4), 100);
        testPerformance(createTestInstance(10, 80, 30, 4, 4, 6), 100);
        testPerformance(createTestInstance(10, 80, 30, 4, 4, 6), 100);
    }

    private void testPerformance(@Nonnull SInstance instance, int repetitions) {
        List<Supplier<Integer>> cases = new ArrayList<>();
        cases.add(() -> {
            MElement xml = SFormXMLUtil.toXMLPreservingRuntimeEdition(instance);
            byte[] contentSerialized = serialize(xml);
            MElement xml2 = deserialize(contentSerialized);
            SFormXMLUtil.fromXML(instance.getType().newInstance(), xml2);
            return contentSerialized.length;
        });
        cases.add(() -> {
            String xml = SFormXMLUtil.toXMLPreservingRuntimeEdition(instance).toStringExato();
            byte[] contentSerialized = serialize(xml);
            String xml2 = deserialize(contentSerialized);
            SFormXMLUtil.fromXML(instance.getType().newInstance(), xml2);
            return contentSerialized.length;
        });
        cases.add(() -> {
            byte[] content = SFormBinaryUtil.writePreservingRuntimeEdition(instance);
            byte[] contentSerialized = serialize(content);
            byte[] content2 = deserialize(contentSerialized);
            SFormBinaryUtil.read(instance.getType().newInstance(), content2);
            return contentSerialized.length;
        });

        runTest(cases, repetitions);
    }

    private void runTest(List<Supplier<Integer>> cases, int repetitions) {
        DebugOutput out = new DebugOutput();
        DebugOutputTable table = out.table();
        table.addColumn(8);
        table.addColumn(8);
        table.addColumn(10);
        table.addColumn(8);
        table.addColumn(8);
        table.addColumn(8);

        cases.forEach(c -> table.addValue(SingularIOUtils.humanReadableByteCount(c.get())));
        cases.forEach(c -> {
            long inicio = System.currentTimeMillis();
            for (int i = 0; i < repetitions; i++) {
                int v = c.get();
            }
            long fim = System.currentTimeMillis();
            table.addValue(SingularIOUtils.humanReadableMilliSeconds(fim - inicio, true));
        });
        table.println();
    }

    private byte[] serialize(Object obj) {
        try {
            ByteArrayOutputStream out1 = new ByteArrayOutputStream();
            ObjectOutputStream out2 = new ObjectOutputStream(out1);
            out2.writeObject(obj);
            out2.close();

            return out1.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T deserialize(byte[] content) {
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(content))) {
            return (T) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    private SInstance writeAndRead(@Nonnull SInstance original) {
        byte[] content = SFormBinaryUtil.writePreservingRuntimeEdition(original);
        SInstance newer = original.getType().newInstance();
        SFormBinaryUtil.read(newer, content);
        return newer;
    }

    private SInstance createTestInstance(int fieldBoolean, int fieldString, int fieldInteger, int qtdList,
            int qtdFieldList, int qtdElementsList) {
        STypeComposite<SIComposite> dataType = createTestPackage().createCompositeType("data");

        for (int i = 0; i < fieldBoolean; i++) {
            dataType.addFieldBoolean("boolean" + i);
        }
        for (int i = 0; i < fieldString; i++) {
            dataType.addFieldString("string" + i);
        }
        for (int i = 0; i < fieldInteger; i++) {
            dataType.addFieldInteger("integer" + i);
        }

        for (int i = 0; i < qtdList; i++) {
            STypeList<STypeComposite<SIComposite>, SIComposite> listType = dataType.addFieldListOfComposite("list" + i,
                    "elementList" + i);
            for (int j = 0; j < qtdFieldList; j++) {
                listType.getElementsType().addFieldString("string_" + i + "_" + j);
            }
        }

        SIComposite data = dataType.newInstance();

        for (int i = 0; i < fieldBoolean; i++) {
            data.setValue("boolean" + i, i % 2 == 0);
        }
        for (int i = 0; i < fieldString; i++) {
            data.setValue("string" + i, "value " + i + " value value");
        }
        for (int i = 0; i < fieldInteger; i++) {
            data.setValue("integer" + i, i);
        }
        for (int i = 0; i < qtdList; i++) {
            SIList<SIComposite> list = data.getFieldList("list" + i, SIComposite.class);
            for (int e = 0; e < qtdElementsList; e++) {
                SIComposite r = list.addNew();
                for (int j = 0; j < qtdFieldList; j++) {
                    r.setValue("string_" + i + "_" + j, "valuevalue" + i + j);
                }

            }
        }

        setAttributes(data);
        return data;
    }

    private void setAttributes(SInstance data) {
        data.asAtr().visible(true);
        data.getChildren().forEach(this::setAttributes);
    }


}