package br.net.mirante.singular.form.mform.core;

import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.util.comuns.SIAnoMes;
import br.net.mirante.singular.form.mform.util.comuns.STypeAnoMes;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.time.YearMonth;
import java.util.Arrays;

import static org.fest.assertions.api.Assertions.assertThat;


@RunWith(value = Parameterized.class)
public class STypeYearMonthTest {

    private static STypeAnoMes type;

    private final YearMonth result;
    private final String persistent;
    private final SIAnoMes value;

    public STypeYearMonthTest(String input, YearMonth result, String persistent){

        this.value = type.novaInstancia();
        this.value.setValue(input);
        this.result = result;
        this.persistent = persistent;
    }

    @BeforeClass
    public static void setupType(){
        SDictionary dict = SDictionary.create();
        type = dict.getType(STypeAnoMes.class);
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
        assertThat(value.toStringPersistencia()).isEqualTo(persistent);
    }

}
