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

package org.opensingular.lib.wicket.util.debugbar.contributor;

import org.apache.wicket.Page;
import org.apache.wicket.devutils.debugbar.IDebugBarContributor;
import org.apache.wicket.devutils.debugbar.StandardDebugPanel;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.lang.Bytes;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;

@SuppressWarnings("serial")
public class JVMContributor extends StandardDebugPanel {

    public JVMContributor(String id) {
        super(id);
    }

    public static final IDebugBarContributor DEBUG_BAR_CONTRIB = (id, debugBar) -> new JVMContributor(id);

    @Override
    protected IModel<String> getDataModel() {
        MemoryUsage mu =ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        MemoryUsage muNH =ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
        return Model.of(
//                " |   Init :"+bytesToString(mu.getInit()).toString()+
//                " |   Max :"+bytesToString(mu.getMax())+
                  "MU H: "+bytesToString(mu.getUsed())+" NH: "+bytesToString(muNH.getUsed())
//                  "MC H: "+bytesToString(mu.getCommitted())+" NH: "+bytesToString(muNH.getCommitted())
//                " |   Init NH :"+bytesToString(muNH.getInit())+
//                " |   Max NH :"+bytesToString(muNH.getMax())+
//                " |   Used NH:"+bytesToString(muNH.getUsed())+
                  );
    }
    
    private String bytesToString(long bytes){
        if (bytes < 0){
            return "?";
        } else {
            return Bytes.bytes(bytes).toString();
        }
    }
    
    @Override
    protected BookmarkablePageLink<Void> createLink(final String id)
    {
        BookmarkablePageLink<Void> bookmarkablePageLink = super.createLink(id);
        bookmarkablePageLink.setEnabled(false);
        return bookmarkablePageLink;
    }

    @Override
    protected ResourceReference getImageResourceReference() {
        return new PackageResourceReference(JVMContributor.class, "java.png");
    }

    @Override
    protected Class<? extends Page> getLinkPageClass() {
        return WebPage.class;
    }

}
