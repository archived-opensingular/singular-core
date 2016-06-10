package br.net.mirante.singular.form.view;

import br.net.mirante.singular.form.SType;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

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

        public BlockBuilder add(String typeName) {
            block.getTypes().add(typeName);
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

