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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortState;
import org.apache.wicket.extensions.markup.html.repeater.tree.ISortableTreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.Node;
import org.apache.wicket.extensions.markup.html.repeater.util.SingleSortState;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.opensingular.form.util.diff.DiffInfo;
import org.opensingular.form.util.diff.DiffType;
import org.opensingular.form.util.diff.DocumentDiff;
import org.opensingular.lib.wicket.util.datatable.BSDataTableBuilder;
import org.opensingular.lib.wicket.util.datatable.BSTableTree;
import org.opensingular.lib.wicket.util.datatable.column.BSFolder;
import org.opensingular.lib.wicket.util.datatable.column.BSTreeColumn;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$m;

public class DiffVisualizer extends Panel {

    private DocumentDiff documentDiff;
    private BSTableTree<DiffInfo, Integer> tableTree;

    public DiffVisualizer(String id, DocumentDiff documentDiff) {
        super(id);
        this.documentDiff = documentDiff;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        createTreeTable();
    }

    private void createTreeTable() {

        BSTreeColumn<DiffInfo, Integer> treeColumn = new DiffBSTreeColumn($m.ofValue("Item"));
        treeColumn.setContentLabelFunction(DiffInfo::getLabel);

        tableTree = new BSDataTableBuilder<>(createProvider())
                .setBorderedTable(false)
                .setStripedRows(false)
                .appendColumn(treeColumn)
                .appendPropertyColumn($m.ofValue("Detalhe"), "detail")
                .buildTree("tree");

        tableTree.setModel(new DiffModel(documentDiff.getDiffRoot()));
        queue(tableTree);
    }

    private ISortableTreeProvider<DiffInfo, Integer> createProvider() {
        return new ISortableTreeProvider<DiffInfo, Integer>() {
            private final SingleSortState<Integer> state = new SingleSortState<>();

            @Override
            public ISortState<Integer> getSortState() {
                return state;
            }

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
                return $m.ofValue(object);
            }

            @Override
            public void detach() {

            }
        };
    }

    private static class DiffModel implements IModel<Set<DiffInfo>> {

        private Set<DiffInfo> diffs;

        public DiffModel(DiffInfo diffRoot) {
            this.diffs = collectAll(diffRoot);
        }

        private Set<DiffInfo> collectAll(DiffInfo diffRoot) {
            Set<DiffInfo> diffs = new HashSet<>();

            diffs.add(diffRoot);

            if (diffRoot.hasChildren()) {
                for (DiffInfo diffInfo : diffRoot.getChildren()) {
                    diffs.addAll(collectAll(diffInfo));
                }
            }

            return diffs;
        }

        @Override
        public Set<DiffInfo> getObject() {
            return diffs;
        }

        @Override
        public void setObject(Set<DiffInfo> diffs) {
            this.diffs = diffs;
        }

        @Override
        public void detach() {

        }
    }

    private static class DiffBSTreeColumn extends BSTreeColumn<DiffInfo, Integer> {

        public DiffBSTreeColumn(IModel<String> displayModel) {
            super(displayModel);
        }

        public DiffBSTreeColumn(IModel<String> displayModel, Integer sortProperty) {
            super(displayModel, sortProperty);
        }

        @Override
        protected Component newNodeComponent(String componentId, IModel<DiffInfo> model) {
            return new Node<DiffInfo>(componentId, getTree(), model) {
                @Override
                protected Component createContent(String id, IModel<DiffInfo> model) {
                    return newContentComponent(id, model);
                }

                @Override
                protected MarkupContainer createJunctionComponent(String id) {

                    MarkupContainer junction = super.createJunctionComponent(id);
                    junction.setVisible(getModelObject().hasChildren());
                    return junction;
                }
            };
        }

        @Override
        protected Component newContentComponent(String componentId, IModel<DiffInfo> model) {
            return new BSFolder<DiffInfo>(componentId, getTree(), model) {
                @Override
                protected IModel<?> newLabelModel(IModel<DiffInfo> model) {
                    return $m.map(model, DiffInfo::getLabel);
                }

                @Override
                protected String getIconStyleClass() {
                    DiffType type = getModelObject().getType();

                    if (DiffType.CHANGED_NEW == type) {
                        return "glyphicon glyphicon-plus diff-icon-new";
                    } else if (DiffType.CHANGED_CONTENT == type) {
                        return "glyphicon glyphicon-pencil diff-icon-changed";
                    } else if (DiffType.CHANGED_DELETED == type) {
                        return "glyphicon glyphicon-minus diff-icon-deleted";
                    } else {
                        return "";
                    }
                }
            };
        }
    }
}
