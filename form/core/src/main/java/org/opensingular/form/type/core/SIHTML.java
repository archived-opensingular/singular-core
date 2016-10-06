package org.opensingular.form.type.core;

import org.opensingular.form.SingularFormException;
import org.opensingular.form.SISimple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class SIHTML extends SISimple<String> implements SIComparable<String> {


    public void fillFromInputStream(final InputStream in) {
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            final StringBuilder sb   = new StringBuilder();
            String              line = reader.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = reader.readLine();
            }
            this.setValue(sb.toString());
        } catch (IOException ex) {
            throw new SingularFormException("Ocorreu um erro ao ler o modelo do parecer");
        }
    }

}
