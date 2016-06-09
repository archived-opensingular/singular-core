package br.net.mirante.singular.form.view;

import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.SingularFormException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 08/06/2016.
 */
public class SViewByBlock extends AbstractSViewByBlock {

    private final List<Block> blocks = new ArrayList<>();
    private final     String name;
    private transient Block  currentBlock;

    public SViewByBlock(String name) {
        this.name = name;
    }

    public SViewByBlock addNewBlock() {
        return addNewBlock(StringUtils.EMPTY);
    }

    public SViewByBlock addNewBlock(String blockName) {
        currentBlock = new Block(blockName);
        blocks.add(currentBlock);
        return this;
    }

    public SViewByBlock addToBlock(SType type) {
        return addToBlock(type.getNameSimple());
    }

    public SViewByBlock addToBlock(String typeName) {
        if (currentBlock == null) {
            throw new SingularFormException("Nenhum bloco foi adicionado ainda.");
        }
        currentBlock.getTypes().add(typeName);
        return this;
    }

    @Override
    public List<Block> getBlocks() {
        return blocks;
    }

    public String getName() {
        return name;
    }

}