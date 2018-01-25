package org.opensingular.form.persistence;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.*;
import org.opensingular.form.document.RefType;
import org.opensingular.form.io.HashUtil;
import org.opensingular.form.persistence.FormPersistenceInRelationalDBHibernateTest.TestPackage.Form;
import org.opensingular.form.persistence.FormPersistenceInRelationalDBHibernateTest.TestPackage.Master;
import org.opensingular.form.persistence.relational.BLOBConverter;
import org.opensingular.form.persistence.relational.CLOBConverter;
import org.opensingular.form.support.TestFormSupport;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.form.type.core.attachment.STypeAttachment;
import org.opensingular.internal.lib.commons.util.TempFileProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Edmundo Andrade
 */
public class FormPersistenceInRelationalDBHibernateTest extends TestFormSupport {

    private   FormPersistenceInRelationalDB<Form, SIComposite>   repoForm;
    private   FormPersistenceInRelationalDB<Master, SIComposite> repoMaster;

    @Before
    public void setUp() {
        repoForm = new FormPersistenceInRelationalDB<>(db, documentFactory, Form.class);
        repoMaster = new FormPersistenceInRelationalDB<>(db, documentFactory, Master.class);
    }

    @Test
    public void basicPersistenceWithGeneratedKey() {
        db.exec("CREATE TABLE FORM (CODE INT IDENTITY, NAME VARCHAR(200) NOT NULL, OBS CLOB, ATTACHMENT BLOB, TEXT_FILE CLOB, PRIMARY KEY (CODE))");
        //
        FormKey firtsKey = repoForm.insert(createFormInstance("My form", "Observaçãozinha", "test.pdf", "test.txt"), null);
        assertEquals("CODE$Integer$1", firtsKey.toStringPersistence());
        assertEquals(1, repoForm.countAll());
        assertEquals(1, repoForm.loadAll().size());
        //
        Object code = ((FormKeyRelational) firtsKey).getColumnValue("CODE");
        assertEquals("My form", db.query("SELECT NAME FROM FORM WHERE CODE = ?", asList(code)).get(0)[0]);
        //
        SIComposite loaded = repoForm.load(firtsKey);
        assertEquals("My form", loaded.getValue("name"));
        IAttachmentRef attachmentRef = ((SIAttachment) loaded.getField("attachment")).getAttachmentRef();
        assertEquals(8, attachmentRef.getSize());
        try {
            try (InputStream input = attachmentRef.getContentAsInputStream()) {
                assertEquals("test.pdf", IOUtils.toString(input, "UTF-8"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        assertEquals("Observaçãozinha", loaded.getValue("observation"));
        assertEquals(firtsKey, FormKey.fromInstance(loaded));
        //
        loaded.setValue("name", "My document");
        loaded.getField("attachment").clearInstance();
        repoForm.update(loaded, null);
        loaded = repoForm.load(firtsKey);
        assertEquals("My document", loaded.getValue("name"));
        assertNull(((SIAttachment) loaded.getField("attachment")).getAttachmentRef());
        //
        repoForm.insert(createFormInstance("Second form", null, null, null), null);
        repoForm.insert(createFormInstance("Third form", null, null, null), null);
        assertEquals(3, repoForm.countAll());
        List<SIComposite> page1 = repoForm.loadAll(0, 2);
        assertEquals(2, page1.size());
        assertEquals("CODE$Integer$1", FormKey.fromInstance(page1.get(0)).toStringPersistence());
        assertEquals("CODE$Integer$2", FormKey.fromInstance(page1.get(1)).toStringPersistence());
        List<SIComposite> page2 = repoForm.loadAll(2, 2);
        assertEquals(1, page2.size());
        assertEquals("CODE$Integer$3", FormKey.fromInstance(page2.get(0)).toStringPersistence());
        //
        repoForm.delete(firtsKey);
        assertEquals(2, repoForm.countAll());
        assertEquals(2, repoForm.loadAll().size());
    }

    @Test
    public void masterDetailPersistenceWithGeneratedKey() {
        db.exec("CREATE TABLE MASTER (ID INT IDENTITY, NAME VARCHAR(200) NOT NULL, PRIMARY KEY (ID))");
        db.exec("CREATE TABLE DETAIL (ID INT IDENTITY, MASTER INT NOT NULL, ITEM VARCHAR(80) NOT NULL, PRIMARY KEY (ID), FOREIGN KEY (MASTER) REFERENCES MASTER(ID))");
        //
        SIComposite master = (SIComposite) documentFactory.createInstance(RefType.of(Master.class));
        master.setValue("name", "Master X");
        addDetail("Item 1", master);
        addDetail("Item 2", master);
        addDetail("Item 3", master);
        FormKey insertedKey = repoMaster.insert(master, null);
        assertEquals("ID$Integer$1", insertedKey.toStringPersistence());
        assertEquals(1, repoMaster.countAll());
        assertEquals(1, repoMaster.loadAll().size());
        //
        Object         code   = ((FormKeyRelational) insertedKey).getColumnValue("ID");
        List<Object[]> tuples = db.query("SELECT ITEM FROM DETAIL WHERE MASTER = ?", asList(code));
        assertEquals(3, tuples.size());
        assertEquals("Item 1", tuples.get(0)[0]);
        assertEquals("Item 2", tuples.get(1)[0]);
        assertEquals("Item 3", tuples.get(2)[0]);
        //
        SIComposite loaded = repoMaster.load(insertedKey);
        assertEquals("Master X", loaded.getValue("name"));
        assertEquals(insertedKey, FormKey.fromInstance(loaded));
        SIList<?> details = loaded.getFieldList("details");
        assertEquals(3, details.size());
        assertEquals("Item 1", details.get(0).getValue("item"));
        assertEquals("Master X", details.get(0).getValue("masterDisplay"));
        //
        repoMaster.delete(insertedKey);
        assertEquals(0, repoMaster.countAll());
        assertEquals(0, repoMaster.loadAll().size());
    }

    private SIComposite createFormInstance(String name, String observation, String attachmentFileName, String textAttachmentFileName) {
        SIComposite formInstance = (SIComposite) documentFactory.createInstance(RefType.of(Form.class));
        formInstance.setValue("name", name);
        formInstance.setValue("observation", observation);
        if (attachmentFileName != null) {
            SIAttachment attachmentField = ((SIAttachment) formInstance.getField("attachment"));
            TempFileProvider.create(this, tempFileProvider -> {
                byte[] sampleContent = attachmentFileName.getBytes("UTF-8");
                File   tempFile      = tempFileProvider.createTempFile(sampleContent);
                attachmentField.setContent(attachmentFileName, tempFile, sampleContent.length,
                        HashUtil.toSHA1Base16(sampleContent));
            });
        }
        if (textAttachmentFileName != null) {
            SIAttachment bigTextFile = ((SIAttachment) formInstance.getField("bigTextFile"));
            TempFileProvider.create(this, tempFileProvider -> {
                StringBuilder text = new StringBuilder();
                for (int i = 0; i < 200; i++) {
                    text.append("Super texto bem grande com 200 linhas \n");
                }
                byte[] content  = text.toString().getBytes(Charset.forName("UTF-8"));
                File   tempFile = tempFileProvider.createTempFile(content);
                bigTextFile.setContent(textAttachmentFileName, tempFile, content.length,
                        HashUtil.toSHA1Base16(content));
            });
        }
        return formInstance;
    }

    private SIComposite addDetail(String item, SIComposite master) {
        return master.getFieldList("details", SIComposite.class).addNew(instance -> instance.setValue("item", item));
    }

    @SInfoPackage(name = "testPackage")
    public static final class TestPackage extends SPackage {
        @SInfoType(name = "Form", spackage = TestPackage.class)
        public static final class Form extends STypeComposite<SIComposite> {
            public STypeString     name;
            public STypeString     observation;
            public STypeAttachment attachment;
            public STypeAttachment bigTextFile;

            @Override
            protected void onLoadType(TypeBuilder tb) {
                asAtr().label("Formulary");
                name = addFieldString("name");
                observation = addFieldString("observation");
                attachment = addField("attachment", STypeAttachment.class);
                attachment.asAtr().allowedFileTypes("pdf", "docx", "xlsx", "jpeg", "png", "mp3", "mp4");
                bigTextFile = addFieldAttachment("bigTextFile");

                // relational mapping
                asSQL().table("FORM").tablePK("CODE");
                name.asSQL().column();
                observation.asSQL().column("OBS").columnConverter(CLOBConverter::new);
                attachment.asSQL().column().columnConverter(BLOBConverter::new);
                bigTextFile.asSQL().column("TEXT_FILE").columnConverter(CLOBConverter::new);
                // TODO
                // attachment.asSQL().column("IMAGE_CONTENT").columnConverter(
                // new BLOBConverter("IMAGE_NAME","IMAGE_HASH","IMAGE_SIZE"));
            }
        }

        @SInfoType(name = "Master", spackage = TestPackage.class)
        public static final class Master extends STypeComposite<SIComposite> {
            public STypeString                    name;
            public STypeList<Detail, SIComposite> details;

            @Override
            protected void onLoadType(TypeBuilder tb) {
                asAtr().label("Master entity");
                name = addFieldString("name");
                details = addFieldListOf("details", Detail.class);
                // relational mapping
                asSQL().table().tablePK("ID");
                name.asSQL().column();
            }
        }

        @SInfoType(name = "Detail", spackage = TestPackage.class)
        public static final class Detail extends STypeComposite<SIComposite> {
            public STypeString item;
            public STypeString masterDisplay;

            @Override
            protected void onLoadType(TypeBuilder tb) {
                item = addFieldString("item");
                masterDisplay = addField("masterDisplay", STypeString.class);
                asAtr().label("Detail entity");
                // relational mapping
                asSQL().table().tablePK("ID");
                asSQL().addTableFK("MASTER", Master.class);
                item.asSQL().column();
                masterDisplay.asSQL().foreignColumn("name", "MASTER", Master.class);
            }
        }
    }
}
