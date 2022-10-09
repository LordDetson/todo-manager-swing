package by.babanin.todo.view.component.tree;

import java.util.Collection;
import java.util.List;

public class TreeStructureBuilder<C, N extends Node<C, N>> {

    private final NodeBuildStrategy<C, N> nodeBuildStrategy;

    public TreeStructureBuilder(NodeBuildStrategy<C, N> nodeBuildStrategy) {
        this.nodeBuildStrategy = nodeBuildStrategy;
    }

    public N build(Collection<C> components) {
        C rootComponent = nodeBuildStrategy.getRootComponent();
        return buildRecursive(null, rootComponent, components);
    }

    private N buildRecursive(N parent, C component, Collection<C> components) {
        N node = parent == null ? nodeBuildStrategy.createRoot(component) : nodeBuildStrategy.createNode(parent, component);
        List<C> directChildComponents = components.stream()
                .filter(c -> nodeBuildStrategy.isChild(component, c))
                .toList();
        for(C childComponent : directChildComponents) {
            node.addChild(buildRecursive(node, childComponent, components));
        }
        return node;
    }
}
