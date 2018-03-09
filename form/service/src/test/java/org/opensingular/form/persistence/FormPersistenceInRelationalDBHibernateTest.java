package org.opensingular.form.persistence;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.opensingular.form.persistence.Criteria.isLike;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInfoPackage;
import org.opensingular.form.SInfoType;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.io.HashUtil;
import org.opensingular.form.persistence.FormPersistenceInRelationalDBHibernateTest.TestPackage.Category;
import org.opensingular.form.persistence.FormPersistenceInRelationalDBHibernateTest.TestPackage.Form;
import org.opensingular.form.persistence.FormPersistenceInRelationalDBHibernateTest.TestPackage.Master;
import org.opensingular.form.persistence.relational.BLOBConverter;
import org.opensingular.form.persistence.relational.CLOBConverter;
import org.opensingular.form.persistence.relational.IntegerConverter;
import org.opensingular.form.support.TestFormSupport;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.form.type.core.attachment.STypeAttachment;
import org.opensingular.form.type.ref.STypeRef;
import org.opensingular.internal.lib.commons.util.TempFileProvider;

/**
 * @author Edmundo Andrade
 */
public class FormPersistenceInRelationalDBHibernateTest extends TestFormSupport {

    private static FormPersistenceInRelationalDB<Category, SIComposite> categoryRepository;
    private FormPersistenceInRelationalDB<Form, SIComposite> repoForm;
    private FormPersistenceInRelationalDB<Master, SIComposite> repoMaster;
    private FormPersistenceInRelationalDB<Category, SIComposite> repoCategory;
    private FormPersistenceInRelationalDB<Customer, SIComposite> repoCustomer;
    private FormPersistenceInRelationalDB<Phone, SIComposite> repoPhone;
    private FormPersistenceInRelationalDB<CustomerOneToMany, SIComposite> repoOneToManyCustomer;
    private FormPersistenceInRelationalDB<PhoneOneToMany, SIComposite> repoOneToManyPhone;

    @Before
    public void setUp() {
        repoForm = new FormPersistenceInRelationalDB<>(db, documentFactory, Form.class);
        repoMaster = new FormPersistenceInRelationalDB<>(db, documentFactory, Master.class);
        repoCategory = new FormPersistenceInRelationalDB<>(db, documentFactory, Category.class);
        categoryRepository = repoCategory;
        repoCustomer = new FormPersistenceInRelationalDB<>(db, documentFactory, Customer.class);
        repoPhone = new FormPersistenceInRelationalDB<>(db, documentFactory, Phone.class);
        repoOneToManyCustomer = new FormPersistenceInRelationalDB<>(db, documentFactory, CustomerOneToMany.class);
        repoOneToManyPhone = new FormPersistenceInRelationalDB<>(db, documentFactory, PhoneOneToMany.class);
    }

    @Test
    public void basicPersistenceWithGeneratedKey() {
        db.exec("CREATE TABLE CATEGORY (ID INT IDENTITY, NAME VARCHAR(200) NOT NULL, PRIMARY KEY (ID))");
        db.exec("CREATE TABLE FORM (CODE INT IDENTITY, NAME VARCHAR(200) NOT NULL, MAJORCATEGORY INT, MINORCATEGORY INT, OBS CLOB, ATTACHMENT BLOB, TEXT_FILE CLOB, PRIMARY KEY (CODE), FOREIGN KEY (MAJORCATEGORY) REFERENCES CATEGORY(ID), FOREIGN KEY (MINORCATEGORY) REFERENCES CATEGORY(ID))");
        //
        SIComposite categoryA1 = createCategoryInstance("Category A1");
        repoCategory.insert(categoryA1, null);
        SIComposite categoryA2 = createCategoryInstance("Category A2");
        repoCategory.insert(categoryA2, null);
        //
        FormKey firstKey = repoForm.insert(
                createFormInstance("My form", "Observaçãozinha", categoryA1, categoryA2, "test.pdf", "test.txt"), null);
        assertEquals("CODE$Integer$1", firstKey.toStringPersistence());
        assertEquals(1, repoForm.countAll());
        assertEquals(1, repoForm.loadAll().size());
        //
        Object code = ((FormKeyRelational) firstKey).getColumnValue("CODE");
        assertEquals("My form", db.query("SELECT NAME FROM FORM WHERE CODE = ?", asList(code)).get(0)[0]);
        //
        SIComposite loaded = repoForm.load(firstKey);
        assertEquals("My form", loaded.getValue("name"));
        assertEquals("1", loaded.getField("majorCategory").getValue("key"));
        assertEquals("Category A1", loaded.getField("majorCategory").getValue("display"));
        assertEquals("2", loaded.getField("minorCategory").getValue("key"));
        assertEquals("Category A2", loaded.getField("minorCategory").getValue("display"));
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
        assertEquals(firstKey, FormKey.fromInstance(loaded));
        //
        loaded.setValue("name", "My document");
        loaded.getField("attachment").clearInstance();
        repoForm.update(loaded, null);
        loaded = repoForm.load(firstKey);
        assertEquals("My document", loaded.getValue("name"));
        assertNull(((SIAttachment) loaded.getField("attachment")).getAttachmentRef());
        //
        repoForm.insert(createFormInstance("Second form", null, categoryA1, categoryA2, null, null), null);
        repoForm.insert(createFormInstance("Third form", null, categoryA1, categoryA2, null, null), null);
        assertEquals(3, repoForm.countAll());
        List<SIComposite> page1 = repoForm.loadAll(0, 2);
        assertEquals(2, page1.size());
        assertEquals("CODE$Integer$1", FormKey.fromInstance(page1.get(0)).toStringPersistence());
        assertEquals("CODE$Integer$2", FormKey.fromInstance(page1.get(1)).toStringPersistence());
        List<SIComposite> page2 = repoForm.loadAll(2, 2);
        assertEquals(1, page2.size());
        assertEquals("CODE$Integer$3", FormKey.fromInstance(page2.get(0)).toStringPersistence());
        //
        repoForm.delete(firstKey);
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
        Object code = ((FormKeyRelational) insertedKey).getColumnValue("ID");
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
        SIComposite masterExample = (SIComposite) documentFactory.createInstance(RefType.of(Master.class));
        masterExample.setValue("name", "Master Y");
        assertEquals(0, repoMaster.list(masterExample).size());
        masterExample.setValue("name", "Master X");
        assertEquals(1, repoMaster.list(masterExample).size());
        Master masterType = (Master) masterExample.getType();
        assertEquals(0, repoMaster.list(isLike(masterType.name, "% Y")).size());
        assertEquals(1, repoMaster.list(isLike(masterType.name, "% X")).size());
        //
        repoMaster.delete(insertedKey);
        assertEquals(0, repoMaster.countAll());
        assertEquals(0, repoMaster.loadAll().size());
    }

    @Test
    public void manyToManyPersistence() {
        db.exec("CREATE TABLE CUSTOMER (ID INT IDENTITY, NAME VARCHAR(200) NOT NULL, PRIMARY KEY (ID))");
        db.exec("CREATE TABLE PHONE (ID INT IDENTITY, NUMBER VARCHAR(20) NOT NULL, PRIMARY KEY (ID))");
        db.exec("CREATE TABLE CUSTOMER_PHONE (CustomerId INT NOT NULL, PhoneId INT NOT NULL, PRIMARY KEY (CustomerId, PhoneId), FOREIGN KEY (CustomerId) REFERENCES CUSTOMER(ID), FOREIGN KEY (PhoneId) REFERENCES PHONE(ID))");
        //
        SIComposite customer = (SIComposite) documentFactory.createInstance(RefType.of(Customer.class));
        customer.setValue("name", "Robert");
        addPhone("+55 (61) 99999-9999", customer);
        addPhone("+55 (85) 99999-9999", customer);
        FormKey insertedKey = repoCustomer.insert(customer, null);
        assertEquals(1, repoCustomer.countAll());
        assertEquals(2, repoPhone.countAll());
        //
        Object code = ((FormKeyRelational) insertedKey).getColumnValue("ID");
        List<Object[]> tuples = db.query(
                "SELECT B.NUMBER FROM CUSTOMER_PHONE A INNER JOIN PHONE B ON A.PhoneId=B.ID WHERE A.CustomerId = ?",
                asList(code));
        assertEquals(2, tuples.size());
        assertEquals("+55 (61) 99999-9999", tuples.get(0)[0]);
        assertEquals("+55 (85) 99999-9999", tuples.get(1)[0]);
        //
        SIComposite loaded = repoCustomer.load(insertedKey);
        assertEquals("Robert", loaded.getValue("name"));
        assertEquals(insertedKey, FormKey.fromInstance(loaded));
        SIList<?> phones = loaded.getFieldList("phones");
        assertEquals(2, phones.size());
        assertEquals("+55 (61) 99999-9999", phones.get(0).getValue("number"));
        assertEquals("+55 (85) 99999-9999", phones.get(1).getValue("number"));
        //
        repoCustomer.delete(insertedKey);
        assertEquals(0, repoCustomer.countAll());
        assertEquals(0, repoPhone.countAll());
    }

    @Test
    public void oneToManyPersistence() {
        db.exec("CREATE TABLE ADDRESS1M (ID INT AUTO_INCREMENT, NUMBER VARCHAR(200), PRIMARY KEY (ID))");
        db.exec("CREATE TABLE CUSTOMER1M (ID VARCHAR(100), NAME VARCHAR(200) NOT NULL, ADDRESS INT, PRIMARY KEY (ID), FOREIGN KEY (ADDRESS) REFERENCES ADDRESS1M(ID))");
        db.exec("CREATE TABLE PHONE1M (ID INT IDENTITY, NUMBER VARCHAR(20) NOT NULL, CustomerId VARCHAR(100) NOT NULL, PRIMARY KEY (ID), FOREIGN KEY (CustomerId) REFERENCES CUSTOMER1M(ID))");
        //
        SIComposite customer = (SIComposite) documentFactory.createInstance(RefType.of(CustomerOneToMany.class));
        customer.setValue("name", "Robert");
        customer.setValue("address.number", "1");
        addPhone("+55 (61) 99999-9999", customer);
        addPhone("+55 (85) 99999-9999", customer);
        customer.setValue("id", "e525899b-4302-4112-a41b-aa30e82cee91");
        FormKey insertedKey = repoOneToManyCustomer.insert(customer, null);
        assertEquals("ID$String$e525899b-4302-4112-a41b-aa30e82cee91", insertedKey.toStringPersistence());
        assertEquals(1, repoOneToManyCustomer.countAll());
        assertEquals(2, repoOneToManyPhone.countAll());
        //
        SIComposite loaded = repoOneToManyCustomer.load(insertedKey);
        assertEquals("Robert", loaded.getValue("name"));
        assertEquals(insertedKey, FormKey.fromInstance(loaded));
        SIList<?> phones = loaded.getFieldList("phones");
        assertEquals(2, phones.size());
        assertEquals("+55 (61) 99999-9999", phones.get(0).getValue("number"));
        assertEquals("+55 (85) 99999-9999", phones.get(1).getValue("number"));
        assertEquals("1", loaded.getValue("address.number"));
        //
        repoOneToManyCustomer.delete(insertedKey);
        assertEquals(0, repoOneToManyCustomer.countAll());
        assertEquals(0, repoOneToManyPhone.countAll());
    }

    private SIComposite createCategoryInstance(String name) {
        SIComposite instance = (SIComposite) documentFactory.createInstance(RefType.of(Category.class));
        instance.setValue("name", name);
        return instance;
    }

    private SIComposite createFormInstance(String name, String observation, SIComposite majorCategory,
            SIComposite minorCategory, String attachmentFileName, String textAttachmentFileName) {
        Category categoryType = (Category) majorCategory.getType();
        Integer majorCategoryId = (Integer) ((FormKeyRelational) FormKey.fromInstance(majorCategory))
                .getColumnValue("ID");
        String majorCategoryName = (String) majorCategory.getValue(categoryType.name);
        Integer minorCategoryId = (Integer) ((FormKeyRelational) FormKey.fromInstance(minorCategory))
                .getColumnValue("ID");
        String minorCategoryName = (String) minorCategory.getValue(categoryType.name);
        SIComposite formInstance = (SIComposite) documentFactory.createInstance(RefType.of(Form.class));
        Form formType = (Form) formInstance.getType();
        formInstance.setValue(formType.name, name);
        formInstance.setValue(formType.observation, observation);
        formInstance.getField(formType.majorCategory).setValue(formType.majorCategory.key, majorCategoryId);
        formInstance.getField(formType.majorCategory).setValue(formType.majorCategory.display, majorCategoryName);
        formInstance.getField(formType.minorCategory).setValue(formType.minorCategory.key, minorCategoryId);
        formInstance.getField(formType.minorCategory).setValue(formType.minorCategory.display, minorCategoryName);
        if (attachmentFileName != null) {
            SIAttachment attachmentField = ((SIAttachment) formInstance.getField("attachment"));
            TempFileProvider.create(this, tempFileProvider -> {
                byte[] sampleContent = attachmentFileName.getBytes("UTF-8");
                File tempFile = tempFileProvider.createTempFile(sampleContent);
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
                byte[] content = text.toString().getBytes(Charset.forName("UTF-8"));
                File tempFile = tempFileProvider.createTempFile(content);
                bigTextFile.setContent(textAttachmentFileName, tempFile, content.length,
                        HashUtil.toSHA1Base16(content));
            });
        }
        return formInstance;
    }

    private SIComposite addDetail(String item, SIComposite master) {
        return master.getFieldList("details", SIComposite.class).addNew(instance -> instance.setValue("item", item));
    }

    private SIComposite addPhone(String number, SIComposite customer) {
        return customer.getFieldList("phones", SIComposite.class)
                .addNew(instance -> instance.setValue("number", number));
    }

    @SInfoPackage(name = "testPackage")
    public static final class TestPackage extends SPackage {
        @SInfoType(name = "Category", spackage = TestPackage.class)
        public static final class Category extends STypeComposite<SIComposite> {
            public STypeString name;

            @Override
            protected void onLoadType(TypeBuilder tb) {
                asAtr().label("Category");
                name = addFieldString("name");
                // relational mapping
                asSQL().table().tablePK("ID");
                name.asSQL().column();
            }
        }

        @SInfoType(name = "CategoryRef", spackage = TestPackage.class)
        public static class CategoryRef extends STypeRef<SIComposite> {
            @Override
            protected String getKeyValue(SIComposite instance) {
                return FormKeyRelational.columnValuefromInstance("ID", instance).toString();
            }

            @Override
            protected String getDisplayValue(SIComposite instance) {
                return instance.getValue(Category.class, c -> c.name);
            }

            @Override
            protected List<SIComposite> loadValues(SDocument document) {
                return categoryRepository.loadAll();
            }

            public void bindForeignColumn(String keyColumn) {
                // relational mapping
                key.asSQL().column(keyColumn).columnConverter(IntegerConverter::new);
                display.asSQL().foreignColumn("name", keyColumn, Category.class);
            }
        }

        @SInfoType(name = "Form", spackage = TestPackage.class)
        public static final class Form extends STypeComposite<SIComposite> {
            public STypeString name;
            public STypeString observation;
            public CategoryRef majorCategory;
            public CategoryRef minorCategory;
            public STypeAttachment attachment;
            public STypeAttachment bigTextFile;

            @Override
            protected void onLoadType(TypeBuilder tb) {
                asAtr().label("Formulary");
                name = addFieldString("name");
                observation = addFieldString("observation");
                majorCategory = addField("majorCategory", CategoryRef.class);
                minorCategory = addField("minorCategory", CategoryRef.class);
                attachment = addField("attachment", STypeAttachment.class);
                attachment.asAtr().allowedFileTypes("pdf", "docx", "xlsx", "jpeg", "png", "mp3", "mp4");
                bigTextFile = addFieldAttachment("bigTextFile");
                // relational mapping
                asSQL().table("FORM").tablePK("CODE");
                asSQL().addTableFK("majorCategory", Category.class);
                asSQL().addTableFK("minorCategory", Category.class);
                name.asSQL().column();
                observation.asSQL().column("OBS").columnConverter(CLOBConverter::new);
                majorCategory.bindForeignColumn("MAJORCATEGORY");
                minorCategory.bindForeignColumn("MINORCATEGORY");
                attachment.asSQL().column().columnConverter(BLOBConverter::new);
                bigTextFile.asSQL().column("TEXT_FILE").columnConverter(CLOBConverter::new);
                // TODO
                // attachment.asSQL().column("IMAGE_CONTENT").columnConverter(
                // new BLOBConverter("IMAGE_NAME","IMAGE_HASH","IMAGE_SIZE"));
            }
        }

        @SInfoType(name = "Master", spackage = TestPackage.class)
        public static final class Master extends STypeComposite<SIComposite> {
            public STypeString name;
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

    @SInfoType(name = "Customer", spackage = TestPackage.class)
    public static final class Customer extends STypeComposite<SIComposite> {
        public STypeString name;
        public STypeList<Phone, SIComposite> phones;

        @Override
        protected void onLoadType(TypeBuilder tb) {
            asAtr().label("Customer");
            name = addFieldString("name");
            phones = addFieldListOf("phones", Phone.class);
            // relational mapping
            asSQL().table().tablePK("ID");
            name.asSQL().column();
            phones.asSQL().manyToMany("CUSTOMER_PHONE", "customerId", "phoneId");
        }
    }

    @SInfoType(name = "Phone", spackage = TestPackage.class)
    public static final class Phone extends STypeComposite<SIComposite> {
        public STypeString number;

        @Override
        protected void onLoadType(TypeBuilder tb) {
            asAtr().label("Phone");
            number = addFieldString("number");
            // relational mapping
            asSQL().table().tablePK("ID");
            number.asSQL().column();
        }
    }

    @SInfoType(name = "CustomerOneToMany", spackage = TestPackage.class)
    public static final class CustomerOneToMany extends STypeComposite<SIComposite> {
        public STypeString id;
        public STypeString name;
        public STypeList<PhoneOneToMany, SIComposite> phones;
        public AddressOneToMany address;

        @Override
        protected void onLoadType(TypeBuilder tb) {
            asAtr().label("Customer");
            id = addFieldString("id");
            id.withInitListener(x -> x.setValue("BY_TRIGGER_TIB_TB_VIAJANTE"));
            id.asAtr().visible(false);
            name = addFieldString("name");
            phones = addFieldListOf("phones", PhoneOneToMany.class);
            address = addField("address", AddressOneToMany.class);
            // relational mapping
            asSQL().table("CUSTOMER1M").tablePK("ID").addTableFK("ADDRESS", AddressOneToMany.class);
            id.asSQL().column("ID");
            name.asSQL().column();
        }
    }

    @SInfoType(name = "PhoneOneToMany", spackage = TestPackage.class)
    public static final class PhoneOneToMany extends STypeComposite<SIComposite> {
        public STypeString number;

        @Override
        protected void onLoadType(TypeBuilder tb) {
            asAtr().label("Phone");
            number = addFieldString("number");
            // relational mapping
            asSQL().table("PHONE1M").tablePK("ID").addTableFK("CustomerId", CustomerOneToMany.class);
            number.asSQL().column();
        }
    }

    @SInfoType(name = "AddressOneToMany", spackage = TestPackage.class)
    public static final class AddressOneToMany extends STypeComposite<SIComposite> {
        public STypeString number;

        @Override
        protected void onLoadType(TypeBuilder tb) {
            asAtr().label("Number");
            number = addFieldString("number");
            // relational mapping
            asSQL().table("ADDRESS1M").tablePK("ID");
            number.asSQL().column("NUMBER");
        }
    }
}
