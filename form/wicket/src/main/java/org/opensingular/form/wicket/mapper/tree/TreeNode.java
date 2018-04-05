package org.opensingular.form.wicket.mapper.tree;

import java.io.Serializable;
import java.util.List;

public interface TreeNode<T extends TreeNode> extends Serializable {

    boolean isLeaf();

    boolean hasChildren();

    TreeNode getRoot();

    boolean isRoot();

    int getLevel();

    Serializable getId();

    String getDisplayLabel();

    List<T> getChildrens();

    Serializable getValue();

}
