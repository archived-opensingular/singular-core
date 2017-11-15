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

package org.opensingular.form.wicket.mapper.attachment;

import org.junit.Test;
import org.opensingular.form.SType;
import org.opensingular.form.type.core.attachment.STypeAttachment;
import org.opensingular.form.view.SViewAttachmentImageTooltip;
import org.opensingular.form.wicket.helpers.SingularFormDummyPageTester;

import java.io.IOException;

@SuppressWarnings("rawtypes")
public class AttachmentImageMapperTest {

    @Test
    public void testRenderComponent() throws IOException {
        SingularFormDummyPageTester tester = new SingularFormDummyPageTester();
        tester.getDummyPage().setTypeBuilder(tb->tb.addField("imgFile", STypeAttachment.class));
        tester.getDummyPage().setAsEditView();

        tester.startDummyPage();

        tester.getAssertionsForm().getSubComponentWithId("fileUpload").isNotNull();
    }

    @Test
    public void testRenderTooltipMapper() throws IOException {
        SingularFormDummyPageTester tester = new SingularFormDummyPageTester();
        tester.getDummyPage().setTypeBuilder(tb->{
            SType imgFile = tb.addField("imgFile", STypeAttachment.class);
            imgFile.setView(SViewAttachmentImageTooltip::new);
        });
        tester.getDummyPage().setAsEditView();

        tester.startDummyPage();

        tester.getAssertionsForm().getSubComponentWithId("fileUpload").assertSInstance().isNotNull();
    }
}
