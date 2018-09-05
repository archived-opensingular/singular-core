package org.opensingular.internal.lib.commons.test;

import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.opensingular.internal.lib.commons.test.SingularTestUtil.SingularTestException;
import org.opensingular.lib.commons.base.SingularException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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
}