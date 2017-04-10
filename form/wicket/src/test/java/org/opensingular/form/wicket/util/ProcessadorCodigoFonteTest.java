package org.opensingular.form.wicket.util;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class ProcessadorCodigoFonteTest {
    private String mockCodigoFonte(){
        StringBuilder builder = new StringBuilder();

        builder.append("/** javadoc do arquivo\n")
            .append("end javadoc\n")
            .append("*/\n")
            .append("public class MockClass implements Serializable{\n")
            .append("/**\n")
            .append("String mock\n")
            .append("*/\n")
            .append("public String mockString;\n")
            .append("public Integer mockInteger;\n")

            .append("public MockClass(){}\n")

            .append("//@destacar:bloco\n")
            .append("public string getMockString(){\n")
            .append("return mockString;\n")
            .append("}\n")
            .append("//@destacar:fim\n")

            .append("// @destacar:bloco\n")
            .append("public string getMockInteger(){\n")
            .append("return mockInteger;\n")
            .append("}\n")
            .append("// @destacar:fim\n")

            .append("//@formatter\n")
            .append("public void emptyMethod(){}\n")


            .append("//@destacar\n")
            .append("// empty line\n")

            .append("// @destacar\n")
            .append("// empty line2\n")

            .append("}\n")
            ;

        return builder.toString();
    }

    @Test
    public void testProcessadorCodigoFonte(){
        ProcessadorCodigoFonte processadorCodigoFonte = new ProcessadorCodigoFonte(mockCodigoFonte());

        processadorCodigoFonte.getFonteProcessado();

        assertThat(processadorCodigoFonte.getLinhasParaDestacar()).hasSize(8);
        assertThat(processadorCodigoFonte.getJavadoc()).isEqualTo("end javadoc\n");
        assertThat(processadorCodigoFonte.getFonteProcessado()).isNotNull();
    }
}
