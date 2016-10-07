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

package org.opensingular.form.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.opensingular.form.SType;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Daniel on 08/06/2016.
 */
public class SViewByBlock extends AbstractSViewByBlock {

    private final List<Block> blocks = new ArrayList<>();
    private final String name;

    public SViewByBlock(String name) {
        this.name = name;
    }

    public SViewByBlock() {
        this(null);
    }

    public BlockBuilder newBlock() {
        return newBlock(StringUtils.EMPTY);
    }

    public BlockBuilder newBlock(String blockName) {
        Block b = new Block(blockName);
        blocks.add(b);
        return new BlockBuilder(b, this);
    }
    /**
     * Cria um bloco para cada tipo informado
     * 
     * @param types
     * @return this
     */
    public SViewByBlock newBlockPerType(Collection<SType<?>> types){
        types.stream().forEach(field -> newBlock().add(field));
        return this;
    }
    
    @Override
    public List<Block> getBlocks() {
        return blocks;
    }

    public String getName() {
        return name;
    }

    public static class BlockBuilder {

        private final Block        block;
        private final SViewByBlock sViewByBlock;

        public BlockBuilder(Block block, SViewByBlock sViewByBlock) {
            this.block = block;
            this.sViewByBlock = sViewByBlock;
        }

        public BlockBuilder add(SType type) {
            return add(type.getNameSimple());
        }

        public BlockBuilder add(String...typeNames) {
            for (String typeName : typeNames) {
                block.getTypes().add(typeName);
            }
            return this;
        }

        public BlockBuilder newBlock() {
            return sViewByBlock.newBlock();
        }

        public BlockBuilder newBlock(String blockName) {
            return sViewByBlock.newBlock(blockName);
        }
    }
}

