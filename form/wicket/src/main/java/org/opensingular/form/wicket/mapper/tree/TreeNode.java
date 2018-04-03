package org.opensingular.form.wicket.mapper.tree;

import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.List;

public interface TreeNode<T extends TreeNode> extends Serializable {
    
    default boolean isLeaf() {
        return !hasChildren();
    }

    default boolean hasChildren() {
        return getParent() == null
                || !CollectionUtils.isEmpty(getChildrens());
    }

    default TreeNode getRoot() {
        if (isRoot()) {
            return this;
        }
        return getParent().getRoot();
    }

    default boolean isRoot() {
        return getParent() == null;
    }

    default int getLevel() {
        if (this.isRoot()) {
            return 0;
        } else {
            return getParent().getLevel() + 1;
        }
    }

    default T addChild(T child) {
        child.setParent(this);
        getChildrens().add(child);
        return child;
    }

   Long getId();

   String getDisplayLabel();

    T getParent();
    
    void setParent(T parent);
    
    List<T> getChildrens();
}
