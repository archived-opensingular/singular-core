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

package org.opensingular.form.view;

import org.opensingular.form.SDictionary;
import org.opensingular.form.SIList;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.form.type.core.attachment.STypeAttachment;
import org.junit.Test;
import org.opensingular.form.view.SViewAttachmentList;
import org.opensingular.form.view.ViewResolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ViewRuleTypeListOfAttachmentTest {

    @Test
    public void testIfResolvedViewIsSViewAttachmentList() throws Exception {
        final SIList<SIAttachment> attachments = SDictionary
                .create()
                .createNewPackage("test")
                .createListTypeOf("list", STypeAttachment.class).newInstance();
        assertEquals(SViewAttachmentList.class, ViewResolver.resolve(attachments).getClass());
    }

    @Test
    public void testIfResolvedViewIsNotSViewAttachmentList() throws Exception {
        final SIList<SIString> nomAttachments = SDictionary
                .create()
                .createNewPackage("test")
                .createListTypeOf("list", STypeString.class).newInstance();
        assertNotEquals(SViewAttachmentList.class, ViewResolver.resolve(nomAttachments).getClass());
    }
}