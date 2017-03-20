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

package org.opensingular.flow.test.support;

/**
 * Contêm os parâmetros de execução de um conjunto de teste. Basicamente, permite que o mesmo teste seja executado com
 * diferentes parâmetros de configuração. Para tanto deverá ser usado em conjunto com a anotação {@link
 * org.junit.runners.Parameterized.Parameters} na classe de teste parametrizada.
 *
 * @author Daniel C. Bordin on 18/03/2017.
 */
public class FlowTestConfig {

    private final String springProfile;
    private final String bdProperties;

    public FlowTestConfig(String springProfile, String bdProperties) {
        this.springProfile = springProfile;
        this.bdProperties = bdProperties;
    }

    public String getSpringProfile() {
        return springProfile;
    }

    public String getBdProperties() {
        return bdProperties;
    }

    public String toString() {
        return "springProfile='" + springProfile + "' bdProperties='" + bdProperties + "'";
    }
}
