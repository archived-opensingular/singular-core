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
import org.apache.wicket.pageStore.IDataStore;

import java.nio.ByteBuffer;

/**
 * Decorates a {@link IDataStore} with the LZ4 compression algorithm
 * Default constructor configures the {@link FSTSerializer}
 */
public class LZ4DataStore implements IDataStore {

    private LZ4Compressor       compressor;
    private LZ4FastDecompressor decompressor;
    private IDataStore          dataStore;

    public LZ4DataStore(IDataStore dataStore) {
        LZ4Factory factory = LZ4Factory.fastestInstance();
        this.dataStore = dataStore;
        this.compressor = factory.fastCompressor();
        this.decompressor = factory.fastDecompressor();
    }


    @Override
    public byte[] getData(String sessionId, int id) {
        byte[] data    = dataStore.getData(sessionId, id);
        int    length  = ByteBuffer.wrap(ArrayUtils.subarray(data, 0, 4)).getInt();
        byte[] content = ArrayUtils.subarray(data, 4, data.length);
        return decompressor.decompress(content, length);
    }

    @Override
    public void removeData(String sessionId, int id) {
        dataStore.removeData(sessionId, id);
    }

    @Override
    public void removeData(String sessionId) {
        dataStore.removeData(sessionId);
    }

    @Override
    public void storeData(String sessionId, int id, byte[] data) {
        int    length      = data.length;
        byte[] lengthBytes = ByteBuffer.allocate(4).putInt(length).array();
        dataStore.storeData(sessionId, id, ArrayUtils.addAll(lengthBytes, compressor.compress(data)));
    }

    @Override
    public void destroy() {
        dataStore.destroy();
        compressor = null;
        decompressor = null;
        dataStore = null;
    }

    @Override
    public boolean isReplicated() {
        return dataStore.isReplicated();
    }

    @Override
    public boolean canBeAsynchronous() {
        return dataStore.canBeAsynchronous();
    }
}