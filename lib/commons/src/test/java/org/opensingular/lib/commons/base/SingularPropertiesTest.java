package org.opensingular.lib.commons.base;

import com.google.common.base.Preconditions;
import org.junit.Assert;
import org.junit.Test;
import org.opensingular.internal.lib.commons.util.TempFileProvider;
import org.opensingular.lib.commons.lambda.IBiConsumerEx;
import org.opensingular.lib.commons.util.TempFileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.opensingular.lib.commons.util.PropertiesUtils.properties;
import static org.opensingular.lib.commons.util.PropertiesUtils.store;

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

            withFileInTempDir(Paths.get("conf", "singular.properties"), (baseDir, propertiesFile) -> {

                store(properties(MOCK_PROPERTY_KEY, MOCK_PROPERTY_FILE_VALUE), propertiesFile, "iso-8859-1");
                impl.setSingularServerHome(baseDir.getCanonicalPath());
                impl.reload();

                Assert.assertEquals(MOCK_PROPERTY_FILE_VALUE, SingularProperties.get().getProperty(MOCK_PROPERTY_KEY));

            });
        });
        SingularPropertiesImpl.get().setSingularServerHome(null);
        SingularPropertiesImpl.get().reload();

    }

    private static void withFileInTempDir(Path relativePath, IBiConsumerEx<File, File, IOException> callback){
        Preconditions.checkArgument(!relativePath.isAbsolute());
        Preconditions.checkArgument(relativePath.getNameCount() > 0);
        TempFileProvider.create(TempFileUtils.class, tmpProvider -> {
            File dir = tmpProvider.createTempDir();
            Path dirPath = dir.toPath();
            Path filePath = dirPath.resolve(relativePath);
            filePath.getParent().toFile().mkdirs();
            File file = filePath.toFile();
            callback.accept(dir,file);
        });
    }

    @Test
    public void getPropertyWithDefaultOptionTest(){
        SingularProperties singularProperties = SingularProperties.get();

        String property = singularProperties.getProperty("notExist", "defaultValue");
        Assert.assertEquals("defaultValue", property);
    }
}