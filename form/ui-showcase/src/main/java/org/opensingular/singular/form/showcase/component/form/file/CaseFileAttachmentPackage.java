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

package org.opensingular.singular.form.showcase.component.form.file;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.type.basic.AtrBootstrap;
import org.opensingular.form.type.core.attachment.STypeAttachment;
import org.opensingular.singular.form.showcase.component.CaseItem;
import org.opensingular.singular.form.showcase.component.Group;
import org.opensingular.singular.form.showcase.component.Resource;

/**
 * Campo para anexar arquivos
 */
@CaseItem(componentName = "Attachment", group = Group.FILE,
resources = @Resource(PageWithAttachment.class))
public class CaseFileAttachmentPackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        STypeAttachment anexo = tipoMyForm.addField("anexo", STypeAttachment.class);
        anexo.asAtr().label("Anexo");
        anexo.asAtr().required(true);
        anexo.as(AtrBootstrap.class).colPreference(6);

        STypeAttachment foto = tipoMyForm.addField("foto", STypeAttachment.class);
        foto.asAtr().label("Foto").required(false).allowedFileTypes("jpg", "image/png");
        foto.as(AtrBootstrap.class).colPreference(6);
        
//        tipoMyForm.addField("a1", STypeAttachment.class).asAtr().label("a1");
//        tipoMyForm.addField("a2", STypeAttachment.class).asAtr().label("a2");
//        tipoMyForm.addField("a3", STypeAttachment.class).asAtr().label("a3");

    }
}
