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

package org.opensingular.form;

/**
 * Representa os flags booleanos e suas respectivas posições em um mapa de bits.
 * Usa o mapa para economizar memoria;
 *
 * @author Daniel C. Bordin
 */
public enum InstanceFlags {

    RemovendoInstancia, IsAtributo;

    private int bit;

    public int bit() {
        return bit;
    }

    static {
        // Escolha um bit para flag
        int pos = 0;
        for (InstanceFlags flag : InstanceFlags.values()) {
            flag.bit = 1 << pos;
            pos++;
        }
    }

}
