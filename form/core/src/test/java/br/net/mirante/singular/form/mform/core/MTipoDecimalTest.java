package br.net.mirante.singular.form.mform.core;

import br.net.mirante.singular.form.mform.core.MTipoDecimal;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.math.BigDecimal;

import static org.fest.assertions.api.Assertions.assertThat;

public class MTipoDecimalTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    MTipoDecimal type = new MTipoDecimal();

    @Test
    public void stringConversions() {
        assertThat(type.converter(null)).isNull();
        assertThat(type.converter("")).isNull();

        assertThat(type.converter("0")).isEqualTo(BigDecimal.ZERO);
        assertThat(type.converter("1")).isEqualTo(BigDecimal.ONE);
        assertThat(type.converter("-1")).isEqualTo(new BigDecimal("-1"));
        assertThat(type.converter("1,9")).isEqualTo(new BigDecimal("1.9"));
        assertThat(type.converter("00000000000001,9")).isEqualTo(new BigDecimal("1.9"));
        assertThat(type.converter("00.000.000.000.001,9")).isEqualTo(new BigDecimal("1.9"));
        assertThat(type.converter("-1,9")).isEqualTo(new BigDecimal("-1.9"));
    }

    @Test
    public void numberConversions() {
        assertThat(type.converter(0L)).isEqualTo(BigDecimal.ZERO);
        assertThat(type.converter(1L)).isEqualTo(BigDecimal.ONE);
        assertThat(type.converter(-1L)).isEqualTo(new BigDecimal("-1"));
        assertThat(type.converter(5.5)).isEqualTo(new BigDecimal("5.5"));
    }

    @Test
    public void unknownTypeException() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage(Matchers.containsString("não consegue converter o valor"));

        type.converter(new File(""));
    }

    @Test
    public void unparseableString() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage(Matchers.containsString("não consegue converter o valor"));

        type.converter("1XPTO");
    }

    @Test
    public void wrongSymbols() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage(Matchers.containsString("não consegue converter o valor"));

        type.converter("00,000,000,000,001.9");
    }
}
