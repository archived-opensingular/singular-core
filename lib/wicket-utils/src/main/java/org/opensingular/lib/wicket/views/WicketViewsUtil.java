/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package org.opensingular.lib.wicket.views;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.request.Response;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.commons.views.ViewGenerator;
import org.opensingular.lib.commons.views.ViewMultiGenerator;
import org.opensingular.lib.commons.views.ViewOutput;

import javax.annotation.Nonnull;

/**
 * Utility class with methods to jhelp to generate views with Wicket.
 *
 * @author Daniel C. Bordin on 23/07/2017.
 */
public class WicketViewsUtil {

    public static void add(MarkupContainer parent, String id, ISupplier<ViewGenerator> tableSupplir) {
        parent.add(new WebComponent(id) {
            @Override
            public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
                ViewOutputHtmlFromWicket vOut = new ViewOutputHtmlFromWicket(this.getRequestCycle());
                ViewGenerator generator = tableSupplir.get();
                generator = resolveGenerator(generator, vOut);

                generator.generateView(vOut);

                vOut.flush();

                Response response = getRequestCycle().getResponse();
                response.write("<ul>");
                for (int i = 0; i < 5; i++)
                    response.write("<li>test</li>");
                response.write("</ul>");
            }
        });

    }

    private static ViewGenerator resolveGenerator(@Nonnull ViewGenerator generator, ViewOutput vOut) {
        if (generator instanceof ViewMultiGenerator) {
            return ((ViewMultiGenerator<?>) generator).getGeneratorFor(vOut);
        }
        return generator;
    }
}
