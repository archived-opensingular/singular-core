package org.opensingular.lib.commons.base;

import static org.opensingular.lib.commons.util.PropertiesUtils.*;

import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;

import org.opensingular.lib.commons.base.SingularProperties;
import org.opensingular.lib.commons.base.SingularPropertiesImpl;
import org.opensingular.lib.commons.util.TempFileUtils;

/**
 * Properties file is stored in /src/test/java/resources/singular.properties
 */
public class SingularPropertiesTest {

    static final String MOCK_PROPERTY_KEY             = "mock.property";
    static final String MOCK_PROPERTY_CLASSPATH_VALUE = "Mock Property";
    static final String MOCK_PROPERTY_FILE_VALUE      = "Mock Disck Property";

    @Test
    public void loadFromClasspath() throws Exception {
        SingularPropertiesImpl.Tester.runInSandbox(impl -> {
            impl.reload();
            Assert.assertEquals(MOCK_PROPERTY_CLASSPATH_VALUE, SingularProperties.get().getProperty(MOCK_PROPERTY_KEY));
        });
    }

    @Test
    public void loadFromFile() throws Exception {
        SingularPropertiesImpl.Tester.runInSandbox(impl -> {

            TempFileUtils.withFileInTempDir(Paths.get("conf", "singular.properties"), (baseDir, propertiesFile) -> {

                store(properties(MOCK_PROPERTY_KEY, MOCK_PROPERTY_FILE_VALUE), propertiesFile, "iso-8859-1");
                impl.setSingularServerHome(baseDir.getCanonicalPath());
                impl.reload();

                Assert.assertEquals(MOCK_PROPERTY_FILE_VALUE, SingularProperties.get().getProperty(MOCK_PROPERTY_KEY));

            });
        });
        SingularPropertiesImpl.get().setSingularServerHome(null);
        SingularPropertiesImpl.get().reload();

    }

    @Test
    public void getPropertyWithDefaultOptionTest(){
        SingularProperties singularProperties = SingularProperties.get();

        String property = singularProperties.getProperty("notExist", "defaultValue");
        Assert.assertEquals("defaultValue", property);
    }
}