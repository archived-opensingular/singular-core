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

package org.opensingular.form.type.country.brazil;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.lib.commons.util.Loggable;

import java.util.function.Predicate;

@SInfoType(name = "Conta", spackage = SPackageCountryBrazil.class)
public class STypeBankAccount extends STypeComposite<SIComposite> implements Loggable {

    public STypeString agencia;
    public STypeString conta;
    public STypeString banco;

    @Override
    protected void onLoadType(TypeBuilder tb) {
        asAtr().displayString("Banco:  ${banco!} | Agência: ${agencia!} | Conta Corrente: ${conta!}");
        banco = this.addFieldString("banco");
        banco.asAtr().maxLength(12).required(requiredChildrenWhenParentIsRequired())
                .label("Banco").asAtrBootstrap().colPreference(2);

        agencia = this.addFieldString("agencia");
        agencia.asAtr().label("Agência").maxLength(12).required(requiredChildrenWhenParentIsRequired());
        agencia.asAtrBootstrap().colPreference(2);

        conta = this.addFieldString("conta");
        conta.asAtr().label("Conta").maxLength(12).required(requiredChildrenWhenParentIsRequired());
        conta.asAtrBootstrap().colPreference(2);

    }

    private Predicate<SInstance> requiredChildrenWhenParentIsRequired() {
        return s -> s.getParent().isRequired();
    }
}
