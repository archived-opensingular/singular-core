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

package org.opensingular.form.type.util;

import org.opensingular.form.SIList;
import org.opensingular.form.SingularFormException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;

public abstract class AbstractLatlongStrategy implements LatlongStrategy {

    @Override
    public void parseFile(InputStream inputStream, SIList<SILatitudeLongitude> latLongs) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line = reader.readLine();
            while (line != null) {
                parseLine(latLongs, line);
                line = reader.readLine();
            }
        } catch (Exception e) {
            throw SingularFormException.rethrow("Não foi possível ler o arquivo com latitude e longitude", e);
        }

    }

    public abstract SILatitudeLongitude parseLine(SIList<SILatitudeLongitude> latLongs, String line);

    public BigDecimal parseValue(String value) {
        return new BigDecimal(value.replaceAll("\\.", "").replaceAll(",", "."));
    }
}
