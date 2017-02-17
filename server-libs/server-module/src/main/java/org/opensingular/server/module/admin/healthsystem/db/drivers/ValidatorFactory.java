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
package org.opensingular.server.module.admin.healthsystem.db.drivers;

/**
 * Created by vitor.rego on 17/02/2017.
 */
public class ValidatorFactory {
    public static IValidatorDatabase getDriver(String driverDialect) throws Exception{
        for (ValidatorEnums value: ValidatorEnums.values()) {
            if(driverDialect.contains(value.toString().toLowerCase())){
                return value.getDriver();
            }
        }
        throw new Exception("Driver não encontrado");
    }
}
