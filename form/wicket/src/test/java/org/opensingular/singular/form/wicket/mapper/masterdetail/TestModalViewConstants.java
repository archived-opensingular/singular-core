package org.opensingular.singular.form.wicket.mapper.masterdetail;

import org.opensingular.form.view.SViewListByMasterDetail;
import org.opensingular.singular.util.wicket.modal.BSModalBorder;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class TestModalViewConstants {

    /**
     * Verifica se os tipos de tamanho definidos na view de mestre detalhe possuem correspondencia no
     * componente wicket utilizado pelo mapper
     */
    @Test
    public void testSizeOptions() {
        BSModalBorder.Size.valueOf(new SViewListByMasterDetail().autoSize().getModalSize());
        BSModalBorder.Size.valueOf(new SViewListByMasterDetail().largeSize().getModalSize());
        BSModalBorder.Size.valueOf(new SViewListByMasterDetail().mediumSize().getModalSize());
        BSModalBorder.Size.valueOf(new SViewListByMasterDetail().smallSize().getModalSize());
        BSModalBorder.Size.valueOf(new SViewListByMasterDetail().fullSize().getModalSize());
    }

    /**
     * Verifica se todos os tipos de tamanho disponibilizados no componente de modal
     * utilizado pelo mapper estão disponíveis na view
     */
    @Test
    public void testViewOptions() {
        BSModalBorder.Size[] sizes = new BSModalBorder.Size[]{
                BSModalBorder.Size.valueOf(new SViewListByMasterDetail().autoSize().getModalSize()),
                BSModalBorder.Size.valueOf(new SViewListByMasterDetail().largeSize().getModalSize()),
                BSModalBorder.Size.valueOf(new SViewListByMasterDetail().mediumSize().getModalSize()),
                BSModalBorder.Size.valueOf(new SViewListByMasterDetail().smallSize().getModalSize()),
                BSModalBorder.Size.valueOf(new SViewListByMasterDetail().fullSize().getModalSize())
        };
        Arrays.sort(sizes);
        Arrays.sort(BSModalBorder.Size.values());
        Assert.assertArrayEquals(sizes, BSModalBorder.Size.values());
    }
}
