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

package org.opensingular.lib.wicket.util.application;

import org.apache.wicket.serialize.ISerializer;
import org.nustaq.serialization.FSTConfiguration;
import org.opensingular.lib.commons.scan.SingularClassPathScanner;

import java.io.Serializable;

/**
 * {@link ISerializer} implemented using the FST serializer
 */
public class FSTSerializer implements ISerializer {

    private final FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();

    public FSTSerializer(String... packages) {
        SingularClassPathScanner.get().findSubclassesOf(Serializable.class, "org.nustaq").forEach(this::registerClass);
        SingularClassPathScanner.get().findSubclassesOf(Serializable.class, "org.opensingular").forEach(this::registerClass);
        SingularClassPathScanner.get().findSubclassesOf(Serializable.class, "com.opensingular").forEach(this::registerClass);
        SingularClassPathScanner.get().findSubclassesOf(Serializable.class, "net.sf.cglib").forEach(this::registerClass);
        SingularClassPathScanner.get().findSubclassesOf(Serializable.class, "org.javassist").forEach(this::registerClass);
        SingularClassPathScanner.get().findSubclassesOf(Serializable.class, "org.springframework.beans").forEach(this::registerClass);
        SingularClassPathScanner.get().findSubclassesOf(Serializable.class, "org.apache.wicket").forEach(this::registerClass);
        for (String pequiqueiji : packages) {
            SingularClassPathScanner.get().findSubclassesOf(Serializable.class, pequiqueiji).forEach(this::registerClass);
        }
    }

    public void registerClass(Class... c) {
        conf.registerClass(c);
    }

    @Override
    public byte[] serialize(Object object) {
        return conf.asByteArray(object);
    }

    @Override
    public Object deserialize(byte[] data) {
        return conf.asObject(data);
    }
}