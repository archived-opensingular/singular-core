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
package org.opensingular.server.commons.admin.healthsystem.validation.database;

import org.opensingular.lib.support.spring.util.ApplicationContextProvider;

/**
 * Created by vitor.rego on 17/02/2017.
 */
public enum ValidatorFactory {
    ORACLE("Oracle"){
        @Override
        public IValidatorDatabase getDriver() {
            return ApplicationContextProvider.get().getBean(ValidatorOracle.class);
        }
    };

    public abstract IValidatorDatabase getDriver();

    private String descricao;

    ValidatorFactory(String descricao){
        this.descricao = descricao;
    }

    public static IValidatorDatabase getDriver(String driverDialect) throws Exception{
        for (ValidatorFactory value: ValidatorFactory.values()) {
            if(driverDialect.contains(value.descricao)){
                return value.getDriver();
            }
        }
        throw new Exception("Driver não encontrado");
    }
}
