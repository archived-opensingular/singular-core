package org.opensingular.form;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeDate;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;

import java.util.NoSuchElementException;

@RunWith(Parameterized.class)
public class TestSInstance_getValue extends TestCaseForm {

    public static final String  BRUCE_WAYNE    = "Bruce Wayne";
    public static final Integer AGE_34         = 34;
    public static final String  MOUNTAIN_DRIVE = "1007 Mountain Drive";
    public static final Integer ZIP_CODE_11111 = 11111;
    public static final String  GOTHAM_CITY    = "Gotham";
    public static final String  COUNTRY_US     = "US";

    public TestSInstance_getValue(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    private SDictionary myDictionary;
    private MyPackage myPackage;

    @Before
    public void setUp() {
        myDictionary = SDictionary.create();
        myPackage = myDictionary.loadPackage(MyPackage.class);
    }

    @Test
    public void test_getValue() {
        SIComposite form = myPackage.form.newInstance();

        assertNull(form.getValue(MyTypeForm.class, t1 -> t1.name));
        assertNull(form.getValue(MyTypeForm.class, t1 -> t1.age));
        assertNull(form.getValue(MyTypeForm.class, t1 -> t1.homeAddress.streetAddress));
        assertNull(form.getValue(MyTypeForm.class, t1 -> t1.homeAddress.zipCode));
        assertNull(form.getValue(MyTypeForm.class, t1 -> t1.homeAddress.city.name));
        assertNull(form.getValue(MyTypeForm.class, t1 -> t1.homeAddress.city.country));

        populateTestForm(form);

        assertEquals(BRUCE_WAYNE    , form.getValue(MyTypeForm.class, t -> t.name                       ));
        assertEquals(AGE_34         , form.getValue(MyTypeForm.class, t -> t.age                        ));
        assertEquals(MOUNTAIN_DRIVE , form.getValue(MyTypeForm.class, t -> t.homeAddress.streetAddress  ));
        assertEquals(ZIP_CODE_11111 , form.getValue(MyTypeForm.class, t -> t.homeAddress.zipCode        ));
        assertEquals(GOTHAM_CITY    , form.getValue(MyTypeForm.class, t -> t.homeAddress.city.name      ));
        assertEquals(COUNTRY_US     , form.getValue(MyTypeForm.class, t -> t.homeAddress.city.country   ));

        assertNull(form.getValue(MyTypeForm.class, t1 -> t1.workAddress.streetAddress));
        assertNull(form.getValue(MyTypeForm.class, t1 -> t1.workAddress.zipCode));
        assertNull(form.getValue(MyTypeForm.class, t1 -> t1.workAddress.city.name));
        assertNull(form.getValue(MyTypeForm.class, t1 -> t1.workAddress.city.country));
    }

    @Test
    public void test_findValue() {
        SIComposite form = myPackage.form.newInstance();

        assertFalse(form.findValue(MyTypeForm.class, t -> t.name                        ).isPresent());
        assertFalse(form.findValue(MyTypeForm.class, t -> t.age                         ).isPresent());
        assertFalse(form.findValue(MyTypeForm.class, t -> t.homeAddress.streetAddress   ).isPresent());
        assertFalse(form.findValue(MyTypeForm.class, t -> t.homeAddress.zipCode         ).isPresent());
        assertFalse(form.findValue(MyTypeForm.class, t -> t.homeAddress.city.name       ).isPresent());
        assertFalse(form.findValue(MyTypeForm.class, t -> t.homeAddress.city.country    ).isPresent());

        populateTestForm(form);

        assertEquals(BRUCE_WAYNE    , form.findValue(MyTypeForm.class, t -> t.name                      ).get());
        assertEquals(AGE_34         , form.findValue(MyTypeForm.class, t -> t.age                       ).get());
        assertEquals(MOUNTAIN_DRIVE , form.findValue(MyTypeForm.class, t -> t.homeAddress.streetAddress ).get());
        assertEquals(ZIP_CODE_11111 , form.findValue(MyTypeForm.class, t -> t.homeAddress.zipCode       ).get());
        assertEquals(GOTHAM_CITY    , form.findValue(MyTypeForm.class, t -> t.homeAddress.city.name     ).get());
        assertEquals(COUNTRY_US     , form.findValue(MyTypeForm.class, t -> t.homeAddress.city.country  ).get());

        assertFalse(form.findValue(MyTypeForm.class, t -> t.workAddress.streetAddress   ).isPresent());
        assertFalse(form.findValue(MyTypeForm.class, t -> t.workAddress.zipCode         ).isPresent());
        assertFalse(form.findValue(MyTypeForm.class, t -> t.workAddress.city.name       ).isPresent());
        assertFalse(form.findValue(MyTypeForm.class, t -> t.workAddress.city.country    ).isPresent());
    }

    @Test
    public void test_simple_type_getValue() {
        SIString str = myPackage.resolveType(STypeString.class).newInstance();

        assertNull(str.getValue(STypeString.class, t1 -> t1));

        str.setValue("ABC", STypeString.class, t -> t);
        assertEquals("ABC", str.getValue(STypeString.class, t1 -> t1));
    }

    @Test(expected = NoSuchElementException.class)
    public void test_sibling_type_getValue() {
        MyTypeForm myForm = myPackage.resolveType(MyTypeForm.class);
        SIComposite str = myForm.homeAddress.newInstance();

        String value = str.getValue(MyTypeAddress.class, t -> myForm.workAddress.city.name);
        assertNull(value);
    }

    @Test(expected = ClassCastException.class)
    public void test_wrong_root_type() {
        SIComposite form = myPackage.form.newInstance();
        form.getValue(MyTypeAddress.class, t -> t.city.name);
    }

    @Test(expected = NoSuchElementException.class)
    public void test_invalid_target_type() {
        SIComposite form = myPackage.form.newInstance();
        form.getValue(MyTypeForm.class, t -> myPackage.resolveType(STypeDate.class));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Shared
    ///////////////////////////////////////////////////////////////////////////

    private void populateTestForm(SIComposite form) {
        form.setValue(BRUCE_WAYNE   , MyTypeForm.class, t -> t.name                     );
        form.setValue(AGE_34        , MyTypeForm.class, t -> t.age                      );
        form.setValue(MOUNTAIN_DRIVE, MyTypeForm.class, t -> t.homeAddress.streetAddress);
        form.setValue(ZIP_CODE_11111, MyTypeForm.class, t -> t.homeAddress.zipCode      );
        form.setValue(GOTHAM_CITY   , MyTypeForm.class, t -> t.homeAddress.city.name    );
        form.setValue(COUNTRY_US    , MyTypeForm.class, t -> t.homeAddress.city.country );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Package declaration
    ///////////////////////////////////////////////////////////////////////////

    @SInfoPackage(name = MyPackage.NAME)
    public static class MyPackage extends SPackage {
        public static final String NAME = "myPackage";
        private MyTypeForm form;
        private MyTypeAddress address;
        private MyTypeCity city;

        @Override
        protected void onLoadPackage(PackageBuilder pb) {
            super.onLoadPackage(pb);
            city = pb.createType(MyTypeCity.class);
            address = pb.createType(MyTypeAddress.class);
            form = pb.createType(MyTypeForm.class);
        }
    }

    @SInfoType(spackage = MyPackage.class)
    public static class MyTypeForm extends STypeComposite<SIComposite> {
        public STypeString name;
        public STypeInteger age;
        public MyTypeAddress homeAddress;
        public MyTypeAddress workAddress;

        @Override
        protected void onLoadType(TypeBuilder tb) {
            name = addFieldString("name");
            age = addFieldInteger("age");
            homeAddress = addField("homeAddress", MyTypeAddress.class);
            workAddress = addField("workAddress", MyTypeAddress.class);
        }
    }

    @SInfoType(spackage = MyPackage.class)
    public static class MyTypeAddress extends STypeComposite<SIComposite> {
        public STypeString streetAddress;
        public STypeInteger zipCode;
        public MyTypeCity city;

        @Override
        protected void onLoadType(TypeBuilder tb) {
            streetAddress = addFieldString("streetAddress");
            zipCode = addFieldInteger("zipCode");
            city = addField("city", MyTypeCity.class);
        }
    }

    @SInfoType(spackage = MyPackage.class)
    public static class MyTypeCity extends STypeComposite<SIComposite> {
        public STypeString name;
        public STypeString country;

        @Override
        protected void onLoadType(TypeBuilder tb) {
            name = addFieldString("name");
            country = addFieldString("country");
        }
    }

}
