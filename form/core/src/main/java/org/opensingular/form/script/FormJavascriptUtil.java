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

import org.opensingular.form.RefService;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.document.SDocument;
import org.opensingular.lib.commons.internal.function.SupplierUtil;

import javax.script.*;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Classe utilitária para a execução de scripts Javascript com acesso a instância como parte da estrutura de dados
 * disponível ao script.
 *
 * @author Daniel C. Bordin
 */
public class FormJavascriptUtil {

    static final String KEY_INST = "_inst";

    private static Supplier<ScriptEngine> engineSupplier;

    private FormJavascriptUtil() {}

    /**
     * Devolve uma instância cacheada da engine de execução de Javascript.
     */
    private static ScriptEngine getEngine() {
        if (engineSupplier == null) {
            synchronized (FormJavascriptUtil.class) {
                if (engineSupplier == null) {
                    engineSupplier = SupplierUtil.cached(() -> {
                        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
                        if (!(engine instanceof Compilable)) {
                            throw new SingularFormException("Esperado que a engine " + engine + " fosse compilável");
                        }
                        return engine;
                    });
                }
            }
        }
        return engineSupplier.get();
    }

    /**
     * Cria uma versão pre-compilada do script, a qual aumenta significativamente a performance se for se executado mais
     * de uma vez.
     */
    public static CompiledScript compile(String script) {
        try {
            return ((Compilable) getEngine()).compile(script);
        } catch (ScriptException e) {
            throw new SingularFormException("Erro compilando javascript", e);
        }
    }

    /**
     * Compila o script informado e o executa dentro do contexto da instância informada.
     */
    public static Object compileAndEval(SInstance instance, String script) {
        CompiledScript compiled = compile(script);
        try {
            SDocument document = instance.getDocument();
            RuntimeDocumentScript runtime = document.lookupLocalService(RuntimeDocumentScript.class);
            if (runtime == null) {
                runtime = new RuntimeDocumentScript(document);
                document.bindLocalService(RuntimeDocumentScript.class, RefService.ofToBeDescartedIfSerialized(runtime));
            }
            Object result = compiled.eval(runtime.createBindings(instance));
            if (result instanceof JSWrapperInstance) {
                return ((JSWrapperInstance) result).getInstance();
            }
            return result;
        } catch (ScriptException e) {
            throw new SingularFormException("Erro executando javascript", e, instance);
        }
    }
}
