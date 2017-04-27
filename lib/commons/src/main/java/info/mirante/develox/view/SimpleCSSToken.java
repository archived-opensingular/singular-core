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

package info.mirante.develox.view;

/**
 * Representa um token tipado de um CSS.
 *
 * @author Bruno Pedroso
 */
final class SimpleCSSToken {

    private int tipo_;
    private String texto_;

    public SimpleCSSToken(int tipo, String texto) {
        tipo_ = tipo;
        texto_ = texto;
    }

    public int getTipo() {
        return tipo_;
    }

    public String getTexto() {
        return texto_;
    }

    public String toString() {
        return "CSSToken [" + getTipo() + ", " + getTexto() + "]";
    }

}
