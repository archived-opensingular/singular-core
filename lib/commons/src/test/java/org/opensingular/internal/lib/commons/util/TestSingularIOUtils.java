/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.internal.lib.commons.util;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Daniel Bordin on 12/02/2017.
 */
public class TestSingularIOUtils {

    @Test
    public void testFormatBytes() {
        assertFormatBytes(0, "0 B", "0 B", "0 B", "0 B");
        assertFormatBytes(27, "27 B", "27 B", "27 B", "27 B");
        assertFormatBytes(999, "999 B", "999 B", "1 KB", "1 KB");
        assertFormatBytes(1000, "1.0 kB", "1000 B", "1 KB", "1 KB");
        assertFormatBytes(1023, "1.0 kB", "1023 B", "1 KB", "1 KB");
        assertFormatBytes(1024, "1.0 kB", "1.0 KiB", "1 KB", "1 KB");
        assertFormatBytes(1728, "1.7 kB", "1.7 KiB", "2 KB", "2 KB");
        assertFormatBytes(110592, "110.6 kB", "108.0 KiB", "111 KB", "108 KB");
        assertFormatBytes(7077888, "7.1 MB", "6.8 MiB", "7 MB", "7 MB");
        assertFormatBytes(452984832, "453.0 MB", "432.0 MiB", "453 MB", "432 MB");
        assertFormatBytes(28991029248L, "29.0 GB", "27.0 GiB", "29 GB", "27 GB");
        assertFormatBytes(1855425871872L, "1.9 TB", "1.7 TiB", "2 TB", "2 TB");
        assertFormatBytes(9223372036854775807L, "9.2 EB", "8.0 EiB", "9223372 TB", "8388608 TB");
    }

    private void assertFormatBytes(long bytes, String expectedSI, String expectedBinary, String expectedRoundSI,
            String expectedRoundBinary) {
        char decimal = String.format("%.1f", 0.1).charAt(1);

        assertFormat(expectedSI, SingularIOUtils.humanReadableByteCount(bytes, true), decimal);
        assertFormat(expectedSI, SingularIOUtils.humanReadableByteCount(bytes), decimal);
        assertFormat(expectedBinary, SingularIOUtils.humanReadableByteCount(bytes, false), decimal);
        assertFormat(expectedRoundSI, SingularIOUtils.humanReadableByteCountRound(bytes), decimal);
        assertFormat(expectedRoundSI, SingularIOUtils.humanReadableByteCountRound(bytes, true), decimal);
        assertFormat(expectedRoundBinary, SingularIOUtils.humanReadableByteCountRound(bytes, false), decimal);
    }

    private void assertFormat(String expectedFormat, String currentFormat, char decimalSeparator) {
        int pos = expectedFormat.indexOf('.');
        if (pos != -1 && expectedFormat.charAt(pos) != decimalSeparator) {
            expectedFormat = expectedFormat.replace('.', decimalSeparator);
        }
        assertEquals(expectedFormat, currentFormat);
    }

    @Test
    public void testMilisFormat() {
        assertFormatMillis(0, "0 ms");
        assertFormatMillis(999, "999 ms");
        assertFormatMillis(1000, "1.0 seconds");
        assertFormatMillis(1045, "1.0 seconds");
        assertFormatMillis(1090, "1.1 seconds");
        assertFormatMillis(1190, "1.2 seconds");
        assertFormatMillis(10990, "11.0 seconds");
        assertFormatMillis(1000 * 60, "1.0 minutes");
        assertFormatMillis((long) (1000 * 60 * 1.1 + 1), "1.1 minutes");
        assertFormatMillis(1000 * 60 * 60, "1.0 hours");
        assertFormatMillis(1000 * 60 * 60 * 24, "1.0 days");
    }

    private void assertFormatMillis(long millis, String expectedFormat) {
        char decimal = String.format("%.1f", 0.1).charAt(1);
        assertFormat(expectedFormat, SingularIOUtils.humanReadableMiliSeconds(millis), decimal);
    }

    @Test
    public void serializeAndDeserializeTest(){
        String test = "One simple String to serialize";

        String result = SingularIOUtils.serializeAndDeserialize(test);

        Assert.assertEquals(test, result);
    }
}
