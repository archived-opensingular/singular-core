package org.opensingular.internal.lib.commons.test;

import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.junit.ComparisonFailure;
import org.junit.Test;
import org.opensingular.internal.lib.commons.test.SingularTestUtil.SingularTestException;
import org.opensingular.lib.commons.base.SingularException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.opensingular.internal.lib.commons.test.SingularTestUtil.unzipFromResource;
import static org.opensingular.internal.lib.commons.test.SingularTestUtil.unzipFromResourceIfNecessary;

/**
 * @author Daniel C. Bordin
 * @since 2018-08-27
 */
public class SingularTestUtilTest {

    @Test
    public void unzipResource() {
        testContent(unzipFromResource(getClass(), "test.zip", "test.txt"));

        Assertions.assertThatThrownBy(() -> unzipFromResource(getClass(), "test.txt", "test.123")).isExactlyInstanceOf(
                SingularTestException.class).hasMessageContaining("Wasn't found the entry test.123");
        Assertions.assertThatThrownBy(() -> unzipFromResource(getClass(), "test.123", "test.123")).isExactlyInstanceOf(
                SingularTestException.class).hasMessageContaining("Resource 'test.123' not found");
        Assertions.assertThatThrownBy(() -> unzipFromResource(getClass(), "test.zip", "test.123")).isExactlyInstanceOf(
                SingularTestException.class).hasMessageContaining("Wasn't found the entry test.123");

        testContent(unzipFromResourceIfNecessary(getClass(), "test.zip", "txt"));
        testContent(unzipFromResourceIfNecessary(getClass(), "test.txt", "txt"));
        testContent(unzipFromResourceIfNecessary(getClass(), "test.txt", "123"));
        testContent(unzipFromResourceIfNecessary(getClass(), "/test.zip", "txt"));
        Assertions.assertThatThrownBy(() -> unzipFromResourceIfNecessary(getClass(), "test.zip", "test.txt"))
                .isExactlyInstanceOf(SingularTestException.class).hasMessageContaining(
                "Wasn't found the entry test.test.txt");
        Assertions.assertThatThrownBy(() -> unzipFromResourceIfNecessary(getClass(), "test.123", "txt"))
                .isExactlyInstanceOf(SingularTestException.class).hasMessageContaining("Resource 'test.123' not found");
    }

    private void testContent(InputStream in) {
        try (InputStream in2 = in) {
            List<String> content = IOUtils.readLines(in2, "UTF-8");
            Assertions.assertThat(content.size()).isGreaterThanOrEqualTo(1);
            Assertions.assertThat(content.get(0)).contains("123");
        } catch (IOException e) {
            throw SingularException.rethrow(e);
        }
    }

    @Test
    public void matchMaps() {
        SingularTestUtil.matchAndCompare(Collections.emptyMap(), Collections.emptyMap());

        Map<String, String> mp1 = new HashMap<>();
        mp1.put("A", "AA");
        mp1.put("B", "BB");
        SingularTestUtil.matchAndCompare(mp1, mp1);

        Assertions.assertThatThrownBy(() -> SingularTestUtil.matchAndCompare(Collections.emptyMap(), mp1))
                .isExactlyInstanceOf(AssertionError.class).hasMessageContaining(
                "the following keys in current map weren't expected: [A, B]");

        Assertions.assertThatThrownBy(() -> SingularTestUtil.matchAndCompare(mp1, Collections.emptyMap()))
                .isExactlyInstanceOf(AssertionError.class).hasMessageContaining(
                "the following expected key weren't found in current: [A, B]");


        Map<String, String> mp2 = new HashMap<>();
        mp2.put("B", "BB");
        mp2.put("C", "CC");

        Assertions.assertThatThrownBy(() -> SingularTestUtil.matchAndCompare(mp1, mp2)).isExactlyInstanceOf(
                AssertionError.class).hasMessageContaining("the following keys in current map weren't expected: [C]")
                .hasMessageContaining("the following expected key weren't found in current: [A]");

        Assertions.assertThatThrownBy(() -> SingularTestUtil.matchAndCompare(mp2, mp1)).isExactlyInstanceOf(
                AssertionError.class).hasMessageContaining("the following keys in current map weren't expected: [A]")
                .hasMessageContaining("the following expected key weren't found in current: [C]");

        Map<String, String> mp3 = new HashMap<>();
        mp3.put("B", "BBB");
        mp3.put("C", "CC");

        Assertions.assertThatThrownBy(() -> SingularTestUtil.matchAndCompare(mp1, mp3)).isExactlyInstanceOf(
                ComparisonFailure.class).hasMessageContaining("expected:<\"BB[]\"> but was:<\"BB[B]\"");
    }
}