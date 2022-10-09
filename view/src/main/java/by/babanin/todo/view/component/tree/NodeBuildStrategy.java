package by.babanin.todo.view.component.tree;

public interface NodeBuildStrategy<C, N extends Node<C, N>> {

    C getRootComponent();

    N createRoot(C component);

    N createNode(N parent, C component);

    boolean isChild(C parentComponent, C childComponent);

    boolean isRoot(C component);
}
