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

package org.opensingular.form.wicket.mapper.richtext;

import static org.opensingular.lib.wicket.util.util.Shortcuts.$m;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SInstance;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.mapper.StringMapper;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSControls;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSLabel;
import org.opensingular.lib.wicket.util.output.BOutputPanel;

public class PortletRichTextMapper extends StringMapper {

    @Override
    public Component appendInput(WicketBuildContext ctx, BSControls formGroup, IModel<String> labelModel) {
        return ctx.getContainer().newComponent(id -> new PortletRichTextPanel(id, ctx));
    }

    @Override
    protected Component appendReadOnlyInput(WicketBuildContext ctx, BSControls formGroup, IModel<String> labelModel) {
        final IModel<? extends SInstance> model = ctx.getModel();
        final SInstance mi = model.getObject();
        
        final BOutputPanel comp = new BOutputPanel(mi.getName(), $m.ofValue(getReadOnlyFormattedText(ctx, model)));
        String frame = comp.getId();
        
        String markupId = comp.getMarkupId();
        formGroup.newTemplateTag(tt -> 
            "<div style='text-align:center;' class='well'><iframe style='width: 785px;border: 0;height: 350px;background-color:white;' id='"+markupId+"_frame'><html><body></body></html></iframe></div>"
            +"<div wicket:id='"+frame+"' style='display:none;'></div>"
            + "<script>var iframe = document.getElementById('"+markupId+"_frame');"
            + "var frameDoc = iframe.contentWindow? iframe.contentWindow.document : iframe.document;"
            + "frameDoc.open();"
            + "frameDoc.writeln('<style>body{margin: 0;}</style>'+document.getElementById('"+markupId+"').innerHTML);"
            + "frameDoc.close();document.getElementById('"+markupId+"').innerHTML='';"
            + "</script>").add(comp);
        comp.getOutputTextLabel().setEscapeModelStrings(false);
        return comp;
    }

    @Override
    protected void configureLabel(WicketBuildContext ctx, IModel<String> labelModel, boolean hintNoDecoration, BSLabel label) {
        label.setVisible(false);
    }

}