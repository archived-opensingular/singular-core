package br.net.mirante.singular.commons.util;

import static br.net.mirante.singular.commons.util.ConversionUtils.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class ConversionUtilsTest {

    private static final long DEFAULT_VALUE = -1L;

    @Test
    public void testToLongHumane() {
        //@formatter:off
        assertEquals( 10L                            , toLongHumane("     10", DEFAULT_VALUE));
        assertEquals( 10000L                         , toLongHumane("  10000", DEFAULT_VALUE));
        assertEquals( 10000L                         , toLongHumane(" 10.000", DEFAULT_VALUE));
        assertEquals( 10000L                         , toLongHumane(" 10_000", DEFAULT_VALUE));
        assertEquals( 10000L                         , toLongHumane(" 10,000", DEFAULT_VALUE));
        assertEquals( 10L * 1024                     , toLongHumane("   10k ", DEFAULT_VALUE));
        assertEquals( 10L * 1024                     , toLongHumane("   10kb", DEFAULT_VALUE));
        assertEquals( 10L * 1024 * 1024              , toLongHumane("   10m ", DEFAULT_VALUE));
        assertEquals( 10L * 1024 * 1024              , toLongHumane("   10mb", DEFAULT_VALUE));
        assertEquals( 10L * 1024 * 1024 * 1024       , toLongHumane("   10g ", DEFAULT_VALUE));
        assertEquals( 10L * 1024 * 1024 * 1024       , toLongHumane("   10gb", DEFAULT_VALUE));
        assertEquals( 10L * 1024 * 1024 * 1024 * 1024, toLongHumane("   10t ", DEFAULT_VALUE));
        assertEquals( 10L * 1024 * 1024 * 1024 * 1024, toLongHumane("   10tb", DEFAULT_VALUE));
        assertEquals(-10L                            , toLongHumane("-    10", DEFAULT_VALUE));
        assertEquals(-10000L                         , toLongHumane("- 10000", DEFAULT_VALUE));
        assertEquals(-10000L                         , toLongHumane("-10.000", DEFAULT_VALUE));
        assertEquals(-10000L                         , toLongHumane("-10_000", DEFAULT_VALUE));
        assertEquals(-10000L                         , toLongHumane("-10,000", DEFAULT_VALUE));
        assertEquals(-10L * 1024                     , toLongHumane("-  10k ", DEFAULT_VALUE));
        assertEquals(-10L * 1024                     , toLongHumane("-  10kb", DEFAULT_VALUE));
        assertEquals(-10L * 1024 * 1024              , toLongHumane("-  10m ", DEFAULT_VALUE));
        assertEquals(-10L * 1024 * 1024              , toLongHumane("-  10mb", DEFAULT_VALUE));
        assertEquals(-10L * 1024 * 1024 * 1024       , toLongHumane("-  10g ", DEFAULT_VALUE));
        assertEquals(-10L * 1024 * 1024 * 1024       , toLongHumane("-  10gb", DEFAULT_VALUE));
        assertEquals(-10L * 1024 * 1024 * 1024 * 1024, toLongHumane("-  10t ", DEFAULT_VALUE));
        assertEquals(-10L * 1024 * 1024 * 1024 * 1024, toLongHumane("-  10tb", DEFAULT_VALUE));
        
        assertEquals( 10L * 1024                     , toLongHumane("   10K ", DEFAULT_VALUE));
        assertEquals( 10L * 1024                     , toLongHumane("   10KB", DEFAULT_VALUE));
        assertEquals( 10L * 1024 * 1024              , toLongHumane("   10M ", DEFAULT_VALUE));
        assertEquals( 10L * 1024 * 1024              , toLongHumane("   10MB", DEFAULT_VALUE));
        assertEquals( 10L * 1024 * 1024 * 1024       , toLongHumane("   10G ", DEFAULT_VALUE));
        assertEquals( 10L * 1024 * 1024 * 1024       , toLongHumane("   10GB", DEFAULT_VALUE));
        assertEquals( 10L * 1024 * 1024 * 1024 * 1024, toLongHumane("   10T ", DEFAULT_VALUE));
        assertEquals( 10L * 1024 * 1024 * 1024 * 1024, toLongHumane("   10TB", DEFAULT_VALUE));
        assertEquals(  5L * 1024                     , toLongHumane("   5K ", DEFAULT_VALUE));
        assertEquals(  5L * 1024                     , toLongHumane("   5KB", DEFAULT_VALUE));
        assertEquals(  5L * 1024 * 1024              , toLongHumane("   5M ", DEFAULT_VALUE));
        assertEquals(  5L * 1024 * 1024              , toLongHumane("   5MB", DEFAULT_VALUE));
        assertEquals(  5L * 1024 * 1024 * 1024       , toLongHumane("   5G ", DEFAULT_VALUE));
        assertEquals(  5L * 1024 * 1024 * 1024       , toLongHumane("   5GB", DEFAULT_VALUE));
        assertEquals(  5L * 1024 * 1024 * 1024 * 1024, toLongHumane("   5T ", DEFAULT_VALUE));
        assertEquals(  5L * 1024 * 1024 * 1024 * 1024, toLongHumane("   5TB", DEFAULT_VALUE));

        //@formatter:on
    }

    @Test
    public void testToLongHumane_invalid() {
        //@formatter:off
        assertEquals(DEFAULT_VALUE, toLongHumane(      "abc", DEFAULT_VALUE));
        assertEquals(DEFAULT_VALUE, toLongHumane("10k10m10g", DEFAULT_VALUE));
        assertEquals(DEFAULT_VALUE, toLongHumane(    "1234c", DEFAULT_VALUE));
        assertEquals(DEFAULT_VALUE, toLongHumane(      "...", DEFAULT_VALUE));
        assertEquals(DEFAULT_VALUE, toLongHumane(         "", DEFAULT_VALUE));
        assertEquals(DEFAULT_VALUE, toLongHumane(       null, DEFAULT_VALUE));
        assertEquals(DEFAULT_VALUE, toLongHumane(      "___", DEFAULT_VALUE));
        assertEquals(DEFAULT_VALUE, toLongHumane(     "_123", DEFAULT_VALUE));
        assertEquals(DEFAULT_VALUE, toLongHumane("@#))$!#$%", DEFAULT_VALUE));
        //@formatter:on
    }
}
