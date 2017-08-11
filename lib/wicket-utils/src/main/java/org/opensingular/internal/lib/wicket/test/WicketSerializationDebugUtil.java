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

package org.opensingular.internal.lib.wicket.test;

import com.google.common.base.Throwables;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.application.IComponentOnAfterRenderListener;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.opensingular.internal.lib.commons.util.SingularIOUtils;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.base.SingularProperties;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.util.logging.Logger;

/**
 * Método para apoio ao debug em tempo de desenvolvimento da correta capacidade de serialização das páginas Wicket.
 * <p>ATENÇÃO: ESSE DEBUG É UMA OPERAÇÃO CUSTOSA. ATIVE-O APENAS EM DESENVOLVIMENTO.</p>
 * Deve ser usado da seguinte forma para ativar o uso dentro da classe que faz a configuração do application do wicket:
 * <pre>
 *    WicketSerializationDebugUtil.configurePageSerializationDebug(wicketApplication, this.getClass());
 * </pre>
 * Veja mais em {@link #configurePageSerializationDebug(Application, Class)}
 *
 * @author Daniel Bordin on 12/02/2017.
 */
public class WicketSerializationDebugUtil {

    private WicketSerializationDebugUtil() {}

    /**
     * Adiciona o verificado de página serializáveis na aplicação se estiver no modo de desenvolvimento. Veja mais em
     * {@link #configurePageSerializationDebug(Application, Class)}.
     */
    public static void configurePageSerializationDebugIfInDevelopmentMode(Application application,
            Class<?> targetClassLog) {
        if (SingularProperties.get().isTrue(SingularProperties.SINGULAR_DEV_MODE)) {
            configurePageSerializationDebug(application, targetClassLog);
        }
    }

    /**
     * Adiciona listener na aplicação para fazer o teste se ao final de cada renderização de página, a página é
     * serializável. Passa a gera uma informação de tamanho e tempo da serialização no log da aplicação. Gera um log de
     * exception se a página não for serializável.
     *
     * @param application    A ser adicionada a verificação
     * @param targetClassLog Classe para o qual será gerado os logs da verificação
     */
    public static void configurePageSerializationDebug(Application application, Class<?> targetClassLog) {
        //Configura os listeners para verificação da serialização
        DebugSerializationListener debugger = new DebugSerializationListener(targetClassLog);
        application.getComponentOnAfterRenderListeners().add(debugger);
        application.getRequestCycleListeners().add(new DebugSerializationRequestCycleListeners(debugger));
        //Ativa a pilha de serialização detalhada
        if (!"true".equals(System.getProperty("sun.io.serialization.extendedDebugInfo"))) {
            System.setProperty("sun.io.serialization.extendedDebugInfo", "true");
        }
    }

    /** Apenas para implementação de JUnit. */
    final static String getLastVerificationResult(Application application) {
        for (IComponentOnAfterRenderListener listener : application.getComponentOnAfterRenderListeners()) {
            if (listener instanceof DebugSerializationListener) {
                return ((DebugSerializationListener) listener).lastVerification;
            }
        }
        return null;
    }

    /** Listener da execução do ciclo de vida da chamada para verifica necessidades de teste de serialização. */
    private static class DebugSerializationRequestCycleListeners extends AbstractRequestCycleListener {
        private final DebugSerializationListener debugger;

        public DebugSerializationRequestCycleListeners(DebugSerializationListener debugger) {
            this.debugger = debugger;
        }

        @Override
        public void onBeginRequest(RequestCycle cycle) {
            super.onBeginRequest(cycle);
            debugger.clearCurrentThread();
        }

        @Override
        public void onEndRequest(RequestCycle cycle) {
            debugger.runSerializationIfNecessary();
            super.onEndRequest(cycle);
        }
    }

    /** Listener Wicket para executar a verificação da página como serializável. */
    private static class DebugSerializationListener implements IComponentOnAfterRenderListener {

        private final ThreadLocal<Component> componentThreadLocal = new ThreadLocal<>();
        private final Logger logger;
        private String lastVerification;

        public DebugSerializationListener(Class<?> targetClassLog) {
            logger = Logger.getLogger(targetClassLog.getName());
        }

        /** Limpa a requesição antes de começar um novo ciclo. */
        public void clearCurrentThread() {
            componentThreadLocal.remove();
        }

        /**
         * Método chamado pelo listener da aplicação wicket para verificar se a página que acabou de ser redenderizada é
         * serializável.
         */
        @Override
        public void onAfterRender(Component c) {
            Component current = componentThreadLocal.get();
            if (c instanceof Page) {
                componentThreadLocal.set(c);
                tryComponentSerialization(c);
                return;
            } else if (current == null) {
                componentThreadLocal.set(c);
                return;
            } else if (current instanceof Page) {
                return;
            }
            for (Component parent = c.getParent(); parent != null; parent = parent.getParent()) {
                if (parent == current) {
                    return; //The new component is inside of the current one
                }
            }
            componentThreadLocal.set(c);
        }

        /** Verifica se ficou alguma execução de serialização pendente para testar. */
        public void runSerializationIfNecessary() {
            Component c = componentThreadLocal.get();
            if (c != null) {
                componentThreadLocal.remove();
                if (!(c instanceof Page)) {
                    //Não foi chamada de página, mas uma chamada Ajax, então chamara a serialização agora
                    tryComponentSerialization(c);
                }
            }
        }

        private void tryComponentSerialization(Component c) {
            //Serialization
            long time = System.currentTimeMillis();
            byte[] result = c.getApplication().getFrameworkSettings().getSerializer().serialize(c);
            time = System.currentTimeMillis() - time;

            String msg = "Serialization: target=" + c.getClass().getName() + " size=" +
                    (result == null ? "EXCEPTION" : SingularIOUtils.humanReadableByteCount(result.length)) +
                    " serialization=" + SingularIOUtils.humanReadableMiliSeconds(time);
            try {
                if (result == null) {
                    throw new SingularException("Erro serializando a página " + c.getClass().getName() +
                            ". Verifique o log para obter a pilha de erro da serialização.");
                }

                //Deserialization
                time = System.currentTimeMillis();
                Object last = readAllObjects(result);
                time = System.currentTimeMillis() - time;

                msg += " deserialization=" + SingularIOUtils.humanReadableMiliSeconds(time);
                Class<?> classLast = (last == null ? null : last.getClass());
                if (c.getClass() != classLast) {
                    msg += " !!!! DESERIALIZATED CLASS NOT OF EXPECTED TYPE result=" + classLast;
                }
            } finally {
                lastVerification = msg;
                logger.info(msg);
            }
        }

        /** Lê todos os objetos serialziados, retornando o último. */
        private Object readAllObjects(byte[] content) {
            Object last = null;
            try {
                ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(content));
                Object o;
                do {
                    o = in.readObject();
                    if (o != null) {
                        last = o;
                    }
                } while (o != null);
            } catch (Exception e) {
                if (! (e instanceof EOFException)) {
                    Throwables.throwIfUnchecked(e);
                    throw SingularException.rethrow(e);
                }
            }
            return last;
        }
    }

}
