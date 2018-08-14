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

package org.opensingular.form.wicket.mapper.masterdetail;

import org.junit.Assert;
import org.junit.Test;
import org.opensingular.form.view.list.SViewListByMasterDetail;
import org.opensingular.lib.wicket.util.modal.BSModalBorder;

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
