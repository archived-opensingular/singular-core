/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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
