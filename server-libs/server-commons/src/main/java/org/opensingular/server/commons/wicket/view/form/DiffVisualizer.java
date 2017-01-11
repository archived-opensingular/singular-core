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

package org.opensingular.server.commons.wicket.view.form;

import java.util.Collections;
import java.util.Iterator;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.tree.DefaultNestedTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.Folder;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.opensingular.form.util.diff.DiffInfo;
import org.opensingular.form.util.diff.DocumentDiff;
import org.opensingular.lib.wicket.util.util.WicketUtils;

public class DiffVisualizer extends Panel {

    private transient DocumentDiff documentDiff;
    private DefaultNestedTree<DiffInfo> tree;

    public DiffVisualizer(String id, DocumentDiff documentDiff) {
        super(id);
        this.documentDiff = documentDiff;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        createTree();
    }

    private void createTree() {
        tree = new DefaultNestedTree<DiffInfo>("tree", createProvider()) {

            @Override
            protected Component newContentComponent(String id, IModel node) {
                return new Folder<DiffInfo>(id, this, node) {
                    @Override
                    protected IModel<?> newLabelModel(IModel model) {
                        DiffInfo info = (DiffInfo) model.getObject();
                        return WicketUtils.$m.ofValue(info.getLabel());
                    }

                    @Override
                    protected boolean isClickable() {
                        return false;
                    }
                };
            }

            @Override
            public State getState(DiffInfo diffInfo) {
                return State.EXPANDED;
            }
        };
        add(tree);
    }

    private ITreeProvider<DiffInfo> createProvider() {
        return new ITreeProvider<DiffInfo>() {
            @Override
            public Iterator<? extends DiffInfo> getRoots() {
                return Collections.singletonList(documentDiff.getDiffRoot()).iterator();
            }

            @Override
            public boolean hasChildren(DiffInfo node) {
                return !node.getChildren().isEmpty();
            }

            @Override
            public Iterator<? extends DiffInfo> getChildren(DiffInfo node) {
                return node.getChildren().iterator();
            }

            @Override
            public IModel<DiffInfo> model(DiffInfo object) {
                return new DiffModel(object);
            }

            @Override
            public void detach() {

            }
        };
    }

    private class DiffModel extends LoadableDetachableModel<DiffInfo> {

        private final Integer id;

        public DiffModel(DiffInfo object) {
            this.id= object.getId();
        }

        @Override
        protected DiffInfo load() {
            return documentDiff.getById(id);
        }
    }
}
