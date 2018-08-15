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
 * Decorates a {@link ISerializer} with the LZ4 compression algorithm
 * Default constructor configures the {@link FSTSerializer}
 */
public class LZ4Serializer implements ISerializer {


    private final LZ4Factory          factory      = LZ4Factory.fastestInstance();
    private final LZ4Compressor       compressor   = factory.fastCompressor();
    private final LZ4FastDecompressor decompressor = factory.fastDecompressor();
    private final ISerializer serializer;

    public LZ4Serializer(ISerializer serializer) {
        this.serializer = serializer;
    }

    public LZ4Serializer() {
        this(new FSTSerializer());
    }

    @Override
    public byte[] serialize(Object object) {
        byte[] content = serializer.serialize(object);
        int length = content.length;
        byte[] lengthBytes = ByteBuffer.allocate(4).putInt(length).array();
        return ArrayUtils.addAll(lengthBytes, compressor.compress(content));
    }

    @Override
    public Object deserialize(byte[] data) {
        int length = ByteBuffer.wrap(ArrayUtils.subarray(data, 0, 4)).getInt();
        byte[] content = ArrayUtils.subarray(data, 4, data.length);
        return serializer.deserialize(decompressor.decompress(content, length));
    }
}