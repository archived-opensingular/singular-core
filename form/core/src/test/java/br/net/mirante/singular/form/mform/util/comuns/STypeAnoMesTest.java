package br.net.mirante.singular.form.mform.util.comuns;

import static org.fest.assertions.api.Assertions.assertThat;

import java.time.YearMonth;

import org.junit.Test;

public class STypeAnoMesTest {
    
    STypeAnoMes type = new STypeAnoMes();
    YearMonth dec2015 = YearMonth.of(2015, 12),
              jan2015 = YearMonth.of(2015, 1),
              jan01 = YearMonth.of(1, 1)
              ;

    @Test public void formatingString(){
        assertThat(type.toStringPersistencia(dec2015)).isEqualTo("122015");
        assertThat(type.toStringPersistencia(jan2015)).isEqualTo("012015");
        assertThat(type.toStringPersistencia(jan01)).isEqualTo("010001");
    }
    
    @Test public void convertingValueString(){
        assertThat(packUnpackString(dec2015)).isEqualTo(dec2015);
        assertThat(packUnpackString(jan2015)).isEqualTo(jan2015);
        assertThat(packUnpackString(jan01)).isEqualTo(jan01);
    }
    
    @Test public void convertingValueInteger(){
        assertThat(packUnpackInteger(dec2015)).isEqualTo(dec2015);
        assertThat(packUnpackInteger(jan2015)).isEqualTo(jan2015);
        assertThat(packUnpackInteger(jan01)).isEqualTo(jan01);
    }

    private YearMonth packUnpackString(YearMonth value) {
        String generatedStr = type.toStringPersistencia(value);
        return type.converterNaoNativoNaoString(generatedStr);
    }
    
    private YearMonth packUnpackInteger(YearMonth value) {
        String generatedStr = type.toStringPersistencia(value);
        Integer generatedInt = Integer.parseInt(generatedStr);
        return type.converterNaoNativoNaoString(generatedInt);
    }
    
}
