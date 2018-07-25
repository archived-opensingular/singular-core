/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.singular.flow.schedule.quartz;

import java.util.function.Supplier;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.listeners.SchedulerListenerSupport;

/**
 * Listener para aguardar a execução de todos os jbos agendados para então fazer o shutdown do quartz.
 * Esse listener permite que os resultados possam ser verificados sem condição de corrida.
 * Esse listener deve ser registrado antes que qualquer job seja agendado.
 */
public class WaitForShutdownListener extends SchedulerListenerSupport {

    private Supplier<Scheduler> schedulerSupplier;
    private Object lock = new Object();
    private int jobs = 0;

    public WaitForShutdownListener(Supplier<Scheduler> schedulerSupplier){
        this.schedulerSupplier = schedulerSupplier;
    }

    @Override
    public void jobScheduled(Trigger trigger) {
        synchronized (lock) {
            jobs++;
        }
    }

    @Override
    public void triggerFinalized(Trigger trigger) {
        synchronized (lock) {
            if (--jobs == 0) {
                try {
                    schedulerSupplier.get().shutdown();
                } catch (SchedulerException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }

    }

    @Override
    public void schedulerShutdown() {
        synchronized (lock) {
            lock.notify();
        }
    }

    public void waitForShutdown() throws InterruptedException {
        synchronized (lock) {
            if (jobs > 0) {
                lock.wait();
            }
        }
    }
}
