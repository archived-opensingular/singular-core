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

package org.opensingular.lib.commons.table;

import java.io.File;
import java.io.IOException;

/**
 * Facilita a geração e exibição do resultado dos arquivos gerados.
 *
 * @author Daniel C. Bordin on 21/07/2017.
 */
public class OutputTestHelper {

    public static void showFileOnDesktopForUserAndWaitOpening(File arq) {
        showFileOnDesktopForUser(arq, 5000);
    }

    public static void showFileOnDesktopForUser(File arq, int waitTimeMilliAfterCall) {
        if (!arq.exists() || arq.isDirectory()) {
            throw new RuntimeException("Não existe o arquivo " + arq.getAbsolutePath());
        }
        try {
            Runtime.getRuntime().exec("cmd /c start " + arq);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        waitMilli(waitTimeMilliAfterCall);
    }

    public static void waitMilli( int waitTimeMilli) {
        if (waitTimeMilli > 0) {
            try {
                Thread.sleep(waitTimeMilli);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
