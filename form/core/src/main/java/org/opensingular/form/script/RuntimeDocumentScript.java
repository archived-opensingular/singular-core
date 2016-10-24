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

package org.opensingular.form.script;

import org.opensingular.form.*;
import org.opensingular.form.document.SDocument;

import javax.script.Bindings;
import java.util.HashMap;
import java.util.Map;

/**
 * Representa o contexto de execução de um script. O mesmo faz cache para otimização da execução.
 *
 * @author Daniel Bordin
 */
final class RuntimeDocumentScript {

    /** Documento ao qual pertence esse contexto de execução do script. */
    private final SDocument document;

    /** Cache de wrappers de instância. Importante serem os mesmos para permitir destectar referência circular. */
    private final Map<Integer, JSWrapperInstance<?>> wrappers = new HashMap<>();

    public RuntimeDocumentScript(SDocument document) {
        this.document = document;
    }

    /** Retorna um wrapepr para a instância informada. È feito cache para os wrappers das instâncias. */
    public JSWrapperInstance<?> wrapper(SInstance instance) {
        if (instance.getDocument() != document) {
            throw new SingularFormException("Instance referente a outro document", instance);
        }
        Integer id = instance.getId();
        JSWrapperInstance<?> w = wrappers.get(id);

        if (w == null) {
            if (instance instanceof SISimple) {
                w = new JSWrapperSimple(this, (SISimple<?>) instance);
            } else if (instance instanceof SIComposite) {
                w = new JSWrapperComposite(this, (SIComposite) instance);
            } else if (instance instanceof SIList) {
                w = new JSWrapperList(this, (SIList<?>) instance);
            } else {
                w = new JSWrapperInstance<SInstance>(this, instance);
            }
            wrappers.put(id, w);
        }
        return w;
    }

    /** Cria um contexto de execução de script para a instância informada. */
    public Bindings createBindings(SInstance instance) {
        if (instance instanceof SIComposite) {
            return new BindingsSIComposite((JSWrapperComposite) wrapper(instance));
        }
        throw new SingularFormException(
                "Processador javascript não preparado para tratar instancia da classe " + instance.getClass(),
                instance);
    }
}
