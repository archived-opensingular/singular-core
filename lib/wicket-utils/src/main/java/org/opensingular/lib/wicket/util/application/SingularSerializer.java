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

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.wicket.serialize.ISerializer;
import org.nustaq.serialization.FSTConfiguration;
import org.opensingular.lib.commons.scan.SingularClassPathScanner;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * Wraps the FST serializer combined with the LZ4 compression algorithm
 */
public class SingularSerializer implements ISerializer {

    private final FSTConfiguration    conf         = FSTConfiguration.createDefaultConfiguration();
    private final LZ4Factory          factory      = LZ4Factory.fastestInstance();
    private final LZ4Compressor       compressor   = factory.fastCompressor();
    private final LZ4FastDecompressor decompressor = factory.fastDecompressor();

    public SingularSerializer(String... packages) {
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
        byte[] content = conf.asByteArray(object);
        int length = content.length;
        byte[] lengthBytes = ByteBuffer.allocate(4).putInt(length).array();
        return ArrayUtils.addAll(lengthBytes, compressor.compress(content));
    }

    @Override
    public Object deserialize(byte[] data) {
        int length = ByteBuffer.wrap(ArrayUtils.subarray(data, 0, 4)).getInt();
        byte[] content = ArrayUtils.subarray(data, 4, data.length);
        return conf.asObject(decompressor.decompress(content, length));
    }
}