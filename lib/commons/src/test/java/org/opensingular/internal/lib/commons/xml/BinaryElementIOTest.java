package org.opensingular.internal.lib.commons.xml;

import org.junit.Test;

import javax.annotation.Nonnull;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Daniel C. Bordin
 * @since 2018-10-02
 */
public class BinaryElementIOTest {

    @Test
    public void empty() throws IOException {
        MElement test = MElement.newInstance("empty");
        verifyBinary(test);
    }

    @Test
    public void basic() throws IOException {
        MElement test = MElement.newInstance("empty");
        test.addElement("child", "value1");
        test.addElement("child", "value2");
        test.addElement("child", "value1");
        test.addElement("child");
        verifyBinary(test);
    }

    @Test
    public void attrs() throws IOException {
        MElement test = MElement.newInstance("empty");
        test.setAttribute("att1", "x");
        test.setAttribute("att2", "");
        test.setAttribute("att3", "x");
        test.setAttribute("att4", " ");
        test.setAttribute("att5", "y");
        test.addElement("child", "value1").setAttribute("att1", "k");
        test.addElement("child").setAttribute("att6", "y");
        verifyBinary(test);
    }

    @Test
    public void specialCharacters() throws IOException {
        String specialValue = "!@#$%&*ã~eó çâ 'a' \"b\" <AA>";
        MElement test = MElement.newInstance("ação");
        test.setAttribute("saúde", specialValue);
        test.addElement("child", specialValue);
        verifyBinary(test);
    }

    private void verifyBinary(MElement original) throws IOException {
        byte[] content = BinaryElementIO.write(original);
        MElement current = BinaryElementIO.read(content);
    }

    private void verifyEquals(@Nonnull MElement original, @Nonnull MElement current) {
        String originalContent = original.toStringExato();
        String currentContent = current.toStringExato();
        assertThat(currentContent).isEqualTo(originalContent);
    }
}