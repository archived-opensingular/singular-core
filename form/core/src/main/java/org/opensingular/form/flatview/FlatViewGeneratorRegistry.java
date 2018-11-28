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

package org.opensingular.form.flatview;

import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.aspect.AspectRef;
import org.opensingular.form.aspect.SingleAspectRegistry;
import org.opensingular.form.flatview.mapper.BlockFlatViewGenerator;
import org.opensingular.form.flatview.mapper.SelectionFlatViewGenerator;
import org.opensingular.form.flatview.mapper.TabFlatViewGenerator;
import org.opensingular.form.flatview.mapper.TableFlatViewGenerator;
import org.opensingular.form.type.core.STypeMonetary;
import org.opensingular.form.type.core.attachment.STypeAttachment;
import org.opensingular.form.type.country.brazil.STypeUF;
import org.opensingular.form.view.SView;
import org.opensingular.form.view.SViewByBlock;
import org.opensingular.form.view.SViewSelectionBySelect;
import org.opensingular.form.view.SViewTab;
import org.opensingular.form.view.list.SViewListByTable;

import javax.annotation.Nonnull;

/**
 * Register of the default implementations of the aspect {@link FlatViewGenerator#ASPECT_FLAT_VIEW_GENERATOR}.
 *
 * @author Daniel C. Bordin on 12/08/2017.
 */
public class FlatViewGeneratorRegistry extends SingleAspectRegistry<FlatViewGenerator, Class<? extends SView>> {

    public FlatViewGeneratorRegistry(@Nonnull AspectRef<FlatViewGenerator> aspectRef) {
        super(aspectRef, new SViewQualifierQualifierStrategy());
        add(STypeSimple.class, SISimpleFlatViewGenerator::new);
        add(STypeMonetary.class, SIMonetaryFlatViewGenerator::new);
        add(STypeAttachment.class, SIAttachmentFlatViewGenerator::new);
        add(STypeUF.class, UFFlatViewGenerator::new);
        add(STypeList.class, SViewListByTable.class, TableFlatViewGenerator::new);
        add(STypeList.class, SIListFlatViewGenerator::new);
        add(STypeComposite.class, SViewByBlock.class, BlockFlatViewGenerator::new);
        add(STypeComposite.class, SViewTab.class, TabFlatViewGenerator::new);
        add(STypeComposite.class, SViewSelectionBySelect.class, SelectionFlatViewGenerator::new);
        add(STypeComposite.class, SICompositeFlatViewGenerator::new);
    }
}
