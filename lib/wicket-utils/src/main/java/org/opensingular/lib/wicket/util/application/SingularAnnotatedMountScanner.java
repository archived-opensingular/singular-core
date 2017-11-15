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

package org.opensingular.lib.wicket.util.application;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.protocol.http.WebApplication;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.scan.SingularClassPathScanner;
import org.wicketstuff.annotation.mount.MountPath;
import org.wicketstuff.annotation.scan.AnnotatedMountList;
import org.wicketstuff.annotation.scan.AnnotatedMountScanner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Look for {@link MountPath} annotations in the entire classpath
 * and mount the correspondent wicket page to the path.
 * It also validates if the path is already registered an throws an {@link SingularException} in that case.
 */
public class SingularAnnotatedMountScanner {

    private List<Class<?>> lookupPages() {
        List<Class<?>> classes = SingularClassPathScanner
                .get()
                .findClassesAnnotatedWith(MountPath.class)
                .stream()
                .collect(Collectors.toList());
        validatePaths(classes);
        return classes;
    }

    private void validatePaths(List<Class<?>> classes) {
        Map<String, Class<?>> mountPaths = new HashMap<>();
        List<String>          paths      = new ArrayList<>();
        for (Class<?> clazz : classes) {
            paths.clear();
            paths.add(clazz.getAnnotation(MountPath.class).value());
            paths.addAll(Arrays.asList(clazz.getAnnotation(MountPath.class).alt()));
            for (String path : paths) {
                path = StringUtils.removeStart(StringUtils.removeEnd(path, "/"), "/");
                if (mountPaths.containsKey(path)) {
                    throw new SingularException(
                            String
                                    .format("Duas ou mais classes possuem o mesmo valor ou valor alternativo de @MountPath. Classes %s  e %s",
                                            clazz.getName(),
                                            mountPaths.get(path).getName())
                    );
                }
                mountPaths.put(path, clazz);
            }
        }
    }

    public void mountPages(WebApplication application) {
        new CustomAnnotatedMountScanner()
                .scanList(lookupPages())
                .mount(application);
    }

    private static class CustomAnnotatedMountScanner extends AnnotatedMountScanner {
        @Override
        public AnnotatedMountList scanList(List<Class<?>> mounts) {
            return super.scanList(mounts);
        }
    }
}
