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

package org.opensingular.studio.core.util;

import net.vidageek.mirror.dsl.Mirror;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.wicketstuff.annotation.mount.MountPath;

public class StudioWicketUtils {

    /**
     * Monta a URL completa a partir da pagina informada e do "path"
     * <p>
     * A pagina deve estar anotada com @MountPath e com o parametro ${path} em seu valor,
     * por exemplo, caso a entrada possua uma pagina que esteja anotada com @MountPath("/page/${path}")
     * e um path com valor de "foo/bar", o valor retornado será "/page/foo/bar"
     *
     * @param annotatedPage classe com path parameter anotada com @MountPath
     * @param pathURI       o path que será substituido na URL
     * @return a url completa
     */
    public static String getMergedPathIntoURL(Class<? extends WebPage> annotatedPage, String pathURI) {
        String[] paths = pathURI.split("/");
        String moutedPathWithPathParameter = "";
        if (paths.length > 0) {
            String mountPath = getMountPath(annotatedPage);
            if (mountPath == null) {
                throw new NotAnnotatedWithMountPathException(annotatedPage);
            }
            moutedPathWithPathParameter = mountPath.replace("${path}", pathURI);
        }
        return getServerContextPath() + moutedPathWithPathParameter;
    }

    public static String getMountPath(Class<? extends WebPage> page) {
        return new Mirror().on(page).reflect().annotation(MountPath.class).atClass().value();
    }

    public static String getServerContextPath() {
        return WebApplication.get().getServletContext().getContextPath();
    }

    private static class NotAnnotatedWithMountPathException extends WicketRuntimeException {
        NotAnnotatedWithMountPathException(Class<? extends WebPage> page) {
            super("A Pagina " + page + " não está anotada com @MountPath");
        }
    }
}
