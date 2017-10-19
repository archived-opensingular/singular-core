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

import org.opensingular.form.aspect.AspectEntry;
import org.opensingular.form.document.SDocument;
import org.opensingular.internal.lib.commons.xml.MElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * PARA USO INTERNO DA API APENAS. Dá acesso a estrutura internas do form. Os métodos aqui disponibilizados não deve ser
 * utilizados fora do core do form, pois poderão ser removidos ou ter seu comportamento alterado no futuro.
 *
 * @author Daniel C. Bordin
 */
public final class InternalAccess {

    public static final InternalAccess INTERNAL = new InternalAccess();

    private InternalAccess() {}

    /**
     * @see {@link SInstance#addUnreadInfo(MElement)}
     */
    public void addUnreadInfo(SInstance instance, MElement xmlInfo) {
        instance.addUnreadInfo(xmlInfo);
    }

    /**
     * @see {@link SInstance#getUnreadInfo()}
     */
    public List<MElement> getUnreadInfo(SInstance instance) {
        return instance.getUnreadInfo();
    }

    /**
     * @see {@link SType#setAttributeValueSavingForLatter(String, String)}
     */
    public void setAttributeValueSavingForLatter(@Nonnull SType<?> target, @Nonnull String attributeName,
            @Nullable String value) {
        target.setAttributeValueSavingForLatter(attributeName, value);
    }

    /**
     * @see {@link SInstance#setAttributeValueSavingForLatter(String, String)}
     */
    public void setAttributeValueSavingForLatter(@Nonnull SInstance target, @Nonnull String attributeName,
            @Nullable String value) {
        target.setAttributeValueSavingForLatter(attributeName, value);
    }

    /** @see {@link SType#newInstance(boolean, SDocument)}  */
    public SInstance newInstance(@Nonnull SType target, boolean executeInstanceInitListeners,@Nonnull SDocument owner) {
        return target.newInstance(executeInstanceInitListeners, owner);
    }

    /** @see {@link SType#getAspectDirect(int)} */
    @Nullable
    public final AspectEntry<?,?> getAspectDirect(@Nonnull SType target, int index) {
        return target.getAspectDirect(index);
    }
}
