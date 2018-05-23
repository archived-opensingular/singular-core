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

package org.opensingular.form;

import org.junit.Assert;
import org.junit.Test;
import org.opensingular.form.type.core.STypeBoolean;
import org.opensingular.form.type.core.STypeString;

public class TestSTypes {

    private SType<?> getSTypeSubTypeSample() {
        PackageBuilder pb = SDictionary.create().createNewPackage("teste");

        STypeComposite<?> tipoPedido = pb.createCompositeType("pedido");
        tipoPedido.addFieldString("nome");
        tipoPedido.addFieldString("descr");
        tipoPedido.addFieldString("prioridade");
        tipoPedido.addFieldListOf("clientes", STypeString.class);
        STypeComposite<?> tipoItem = tipoPedido.addFieldListOfComposite("itens", "item").getElementsType();
        tipoItem.addFieldString("nome");
        STypeBoolean subType = tipoItem.addFieldBoolean("urgente");

        return subType;
    }


    @Test
    public void testPathFromRoot() {
        Assert.assertEquals("pedido.itens.item.urgente", STypes.getPathFromRoot(getSTypeSubTypeSample()));
    }
}
