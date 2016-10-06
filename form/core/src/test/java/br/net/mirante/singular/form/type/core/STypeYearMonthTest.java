package br.net.mirante.singular.form.type.core;

import org.opensingular.singular.form.SDictionary;
import org.opensingular.singular.form.type.util.SIYearMonth;
import org.opensingular.singular.form.type.util.STypeYearMonth;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.time.YearMonth;
import java.util.Arrays;

import static org.fest.assertions.api.Assertions.assertThat;


@RunWith(value = Parameterized.class)
public class STypeYearMonthTest {

    private static STypeYearMonth type;

    private final YearMonth result;
    private final String persistent;
    private final SIYearMonth value;

    public STypeYearMonthTest(String input, YearMonth result, String persistent){

        this.value = type.newInstance();
        this.value.setValue(input);
        this.result = result;
        this.persistent = persistent;
    }

    @BeforeClass
    public static void setupType(){
        SDictionary dict = SDictionary.create();
        type = dict.getType(STypeYearMonth.class);
    }

    @Parameterized.Parameters(name = "{index}: ({0})")
    public static Iterable<Object[]> data1() {
        return Arrays.asList(new Object[][] {
                { "", null , null },
                { "11/2016", YearMonth.of(2016,11) , "11/2016" },
//                { "1/2016", YearMonth.of(2016,1) , "01/2016" },
//                { "12016", YearMonth.of(2016,1) , "01/2016" },
//                { "012016", YearMonth.of(2016,1) , "01/2016" },
        });
    }

    @Test public void convertInputToData() {
        assertThat(value.getValue()).isEqualTo(result);
    }

    @Test public void convertInputToPersistent(){
        assertThat(value.toStringPersistence()).isEqualTo(persistent);
    }

}
