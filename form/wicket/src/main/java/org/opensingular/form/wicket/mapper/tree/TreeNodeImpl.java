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

package org.opensingular.form.wicket.mapper.tree;

import org.opensingular.lib.commons.lambda.IFunction;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class TreeNodeImpl implements TreeNode {

    private TreeNode root;
    private final Serializable node;
    private int level;
    private final IFunction<Serializable, Object> idFunction;
    private final IFunction<Serializable, String> displayFunction;
    private final IFunction<Serializable, List<Serializable>> childrenProvider;

    public TreeNodeImpl(TreeNode root, Serializable node, int level, IFunction<Serializable, Object> idFunction,
                        IFunction<Serializable, String> displayFunction,
                        IFunction<Serializable, List<Serializable>> childrenProvider) {
        this.root = root;
        this.node = node;
        this.level = level;
        this.idFunction = idFunction;
        this.displayFunction = displayFunction;
        this.childrenProvider = childrenProvider;
    }

    @Override
    public boolean isLeaf() {
        return !hasChildren();
    }

    @Override
    public boolean hasChildren() {
        return !CollectionUtils.isEmpty(getChildrens());
    }

    @Override
    public TreeNode getRoot() {
        return root;
    }

    @Override
    public boolean isRoot() {
        return getRoot() == null;
    }

    @Override
    public int getLevel() {
        if (isRoot()) {
            return 0;
        }
        return level;
    }

    @Override
    public Serializable getId() {
        return (Serializable) idFunction.apply(node);
    }

    @Override
    public String getDisplayLabel() {
        return displayFunction.apply(node);
    }

    @Override
    public List<TreeNode> getChildrens() {
        return childrenProvider.apply(node).stream().map(child ->
                new TreeNodeImpl(getRoot() != null ? getRoot() : this, child, getLevel() + 1,
                        idFunction, displayFunction, childrenProvider))
                .collect(Collectors.toList());
    }

    @Override
    public Serializable getValue() {
        return node;
    }
}
