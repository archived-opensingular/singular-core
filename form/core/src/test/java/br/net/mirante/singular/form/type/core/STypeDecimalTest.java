package br.net.mirante.singular.form.type.core;

import br.net.mirante.singular.form.AbstractTestOneType;
import org.opensingular.singular.form.type.core.SIBigDecimal;
import org.opensingular.singular.form.type.core.STypeDecimal;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.math.BigDecimal;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class STypeDecimalTest extends AbstractTestOneType<STypeDecimal, SIBigDecimal> {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    STypeDecimal type = new STypeDecimal();

    public STypeDecimalTest(TestFormConfig testFormConfig) {
        super(testFormConfig, STypeDecimal.class);
    }

    @Test
    public void stringConversions() {
        assertThat(type.convert(null)).isNull();
        assertThat(type.convert("")).isNull();

        assertThat(type.convert("0")).isEqualTo(BigDecimal.ZERO);
        assertThat(type.convert("1")).isEqualTo(BigDecimal.ONE);
        assertThat(type.convert("-1")).isEqualTo(new BigDecimal("-1"));
        assertThat(type.convert("1,9")).isEqualTo(new BigDecimal("1.9"));
        assertThat(type.convert("00000000000001,9")).isEqualTo(new BigDecimal("1.9"));
        assertThat(type.convert("00.000.000.000.001,9")).isEqualTo(new BigDecimal("1.9"));
        assertThat(type.convert("-1,9")).isEqualTo(new BigDecimal("-1.9"));
    }

    @Test
    public void numberConversions() {
        assertThat(type.convert(0L)).isEqualTo(BigDecimal.ZERO);
        assertThat(type.convert(1L)).isEqualTo(BigDecimal.ONE);
        assertThat(type.convert(-1L)).isEqualTo(new BigDecimal("-1"));
        assertThat(type.convert(5.5)).isEqualTo(new BigDecimal("5.5"));
    }

    @Test
    public void unknownTypeException() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage(Matchers.containsString("não consegue converter o valor"));

        type.convert(new File(""));
    }

    @Test
    public void unparseableString() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage(Matchers.containsString("não consegue converter o valor"));

        type.convert("1XPTO");
    }

    @Test
    public void wrongSymbols() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage(Matchers.containsString("não consegue converter o valor"));

        type.convert("00,000,000,000,001.9");
    }
}
