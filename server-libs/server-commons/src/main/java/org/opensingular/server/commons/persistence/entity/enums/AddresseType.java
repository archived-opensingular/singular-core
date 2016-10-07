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

package org.opensingular.server.commons.persistence.entity.enums;

import javax.mail.Message.RecipientType;

public enum AddresseType {

    TO("To", RecipientType.TO),
    CC("Cc", RecipientType.CC),
    BCC("Bcc", RecipientType.BCC)
    ;

    public static final String CLASS_NAME = "org.opensingular.server.commons.persistence.entity.enums.AddresseType";

    private final String cod;
    private final RecipientType recipientType;

    AddresseType(String cod, RecipientType recipientType) {
        this.cod = cod;
        this.recipientType = recipientType;
    }

    public static AddresseType valueOfEnum(String cod) {
        for (AddresseType tipo : AddresseType.values()) {
            if (cod.equals(tipo.getCod())) {
                return tipo;
            }
        }
        return null;
    }

    public String getCod() {
        return cod;
    }

    public RecipientType getRecipientType() {
        return recipientType;
    }
}
