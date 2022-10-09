package by.babanin.todo.view.component.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.function.BiFunction;

public class Node<C, N extends Node<C, N>> {

    private N parent;
    private List<N> children;
    private C component;
    private Properties properties;

    public boolean isRoot() {
        return parent == null;
    }

    public N getParent() {
        return parent;
    }

    @SuppressWarnings("unchecked")
    public void setParent(N parent) {
        this.parent = parent;
        parent.addChildWithoutAddingParent((N) this);
    }

    protected void addChildWithoutAddingParent(N child) {
        if(child != null) {
            if(children == null) {
                children = new ArrayList<>();
            }
            children.add(child);
        }
    }

    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }

    public List<N> getChildren() {
        List<N> result = Collections.emptyList();
        if(hasChildren()) {
            result = new ArrayList<>(children);
        }
        return result;
    }

    public List<N> getChildrenRecursively() {
        List<N> result = new ArrayList<>();
        collectChildrenRecursively(this, result);
        return result;
    }

    private void collectChildrenRecursively(Node<C, N> currentNode, List<N> result) {
        if(currentNode.hasChildren()) {
            List<N> childrenOfCurrentNode = currentNode.getChildren();
            result.addAll(childrenOfCurrentNode);
            for(N child : childrenOfCurrentNode) {
                collectChildrenRecursively(child, result);
            }
        }
    }

    public List<Node<C, N>> getPath() {
        List<Node<C, N>> path = new ArrayList<>();
        Node<C, N> node = this;
        path.add(node);
        while(!node.isRoot()) {
            node = node.getParent();
            path.add(0, node);
        }
        return path;
    }

    @SuppressWarnings("unchecked")
    public void setChildren(List<N> children) {
        if(children != null && !children.isEmpty()) {
            clearChildren();
            this.children = new ArrayList<>();
            for(N child : children) {
                child.setParentWithoutAddingChild((N) this);
            }
        }
    }

    protected void setParentWithoutAddingChild(N parent) {
        if(!this.parent.equals(parent)) {
            this.parent = parent;
        }
    }

    @SuppressWarnings("unchecked")
    public N addChild(N child) {
        if(child != null) {
            if(children == null) {
                children = new ArrayList<>();
            }
            children.add(child);
            child.setParentWithoutAddingChild((N) this);
        }
        return child;
    }

    @SuppressWarnings("unchecked")
    public void addChildren(Collection<? extends N> children) {
        if(children != null && !children.isEmpty()) {
            if(this.children == null) {
                this.children = new ArrayList<>();
            }
            this.children.addAll(children);
            for(N child : children) {
                child.setParentWithoutAddingChild((N) this);
            }
        }
    }

    public void remove(N child) {
        if(child != null) {
            if(children == null) {
                children = new ArrayList<>();
            }
            children.remove(child);
            child.setParentWithoutAddingChild(null);
        }
    }

    public void remove(Collection<? extends N> children) {
        if(children != null && !children.isEmpty()) {
            if(this.children == null) {
                this.children = new ArrayList<>();
            }
            this.children.removeAll(children);
            for(N child : children) {
                child.setParentWithoutAddingChild(null);
            }
        }
    }

    private void clearChildren() {
        if(children != null) {
            for(N child : children) {
                child.setParentWithoutAddingChild(null);
            }
            children = null;
        }
    }

    public boolean hasComponent() {
        return component != null;
    }

    public C getComponent() {
        return component;
    }

    public void setComponent(C component) {
        this.component = component;
    }

    /**
     * Performing the operation (<code>reducer</code>) for each node while keeping the intermediate result
     *
     * @param reducer is operation for each node. Decryption of Generic type:
     *                &lt;N - current node, R - previous result of previous iteration, R - result of current iteration&gt;
     * @param initial is starting value
     * @param <R>     is result type of reducer
     * @return is final result
     */
    @SuppressWarnings("unchecked")
    public <R> R reduce(BiFunction<N, R, R> reducer, R initial) {
        R result = reducer.apply((N) this, initial);
        if(hasChildren()) {
            for(N child : children) {
                result = child.reduce(reducer, result);
            }
        }
        return result;
    }

    public void put(String key, Object value) {
        if(properties == null) {
            properties = new Properties();
        }
        properties.put(key, value);
    }

    public Object get(String key) {
        Object value = null;
        if(properties != null) {
            value = properties.get(key);
        }
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        Node<?, ?> node = (Node<?, ?>) o;
        return Objects.equals(component, node.component) && Objects.equals(properties, node.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(component, properties);
    }
}
