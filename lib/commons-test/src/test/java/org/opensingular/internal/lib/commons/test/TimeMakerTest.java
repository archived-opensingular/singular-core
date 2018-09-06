package org.opensingular.internal.lib.commons.test;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.opensingular.internal.lib.commons.xml.ConversorDataISO8601;

import javax.annotation.Nonnull;
import java.util.Date;

import static org.junit.Assert.assertTrue;

/**
 * @author Daniel C. Bordin
 * @since 2018-08-28
 */
public class TimeMakerTest {

    @Test
    public void testTimeMaker() {
        TimeMaker tm = new TimeMaker("2018-08-28");
        assertDate("2018-08-28", tm);
        assertDate("2018-08-28T00:01:00", tm.incMinutes(1));
        assertDate("2018-08-28T00:01:00", tm);
        assertDate("2018-08-28T01:00:00", tm.incMinutes(59));
        assertDate("2018-08-28T01:00:00", tm);

        tm = new TimeMaker();
        assertTrue(new Date().getTime() - tm.get().getTime() <= 500);
    }

    private void assertDate(@Nonnull String expected, @Nonnull TimeMaker tm) {
        assertDate(expected, tm.get());
    }

    private void assertDate(@Nonnull String expected, @Nonnull Date dt) {
        Assertions.assertThat(ConversorDataISO8601.format(dt)).isEqualTo(expected);
    }
}