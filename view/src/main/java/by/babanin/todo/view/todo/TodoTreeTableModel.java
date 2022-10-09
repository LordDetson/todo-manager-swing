package by.babanin.todo.view.todo;

import java.util.Optional;
import java.util.function.Function;

import by.babanin.todo.model.Todo;
import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.view.component.DynamicTableModel;
import by.babanin.todo.view.component.tree.DynamicTreeTableModel;
import by.babanin.todo.view.component.tree.Node;

public class TodoTreeTableModel extends DynamicTreeTableModel<Todo, TodoNode> {

    private static final String VALUE_KEY = "value";

    public TodoTreeTableModel(ComponentRepresentation<Todo> representation) {
        super(representation);
    }

    @Override
    protected TodoNode createRoot() {
        return new RootTodoNode();
    }

    @Override
    protected TodoNode createNode(TodoNode parent, Object value) {
        TodoNode node = new TodoNode();
        node.setParent(parent);
        if(value instanceof Todo todo) {
            node.setComponent(todo);
        }
        else {
            node.put(VALUE_KEY, value);
        }
        return node;
    }

    @Override
    protected Optional<TodoNode> findNode(TodoNode parent, Object value) {
        Function<TodoNode, Object> valueGetter = value instanceof Todo ? Node::getComponent : node -> node.get(VALUE_KEY);
        return parent.getChildren().stream()
                .filter(node -> valueGetter.apply(node).equals(value))
                .findFirst();
    }

    @Override
    public Object getValueAt(Object node, int column) {
        TodoNode todoNode = (TodoNode) node;
        Todo component = todoNode.getComponent();
        Object value = null;
        if(component != null) {
            DynamicTableModel<Todo> model = getModel();
            value = model.getValueAt(model.indexOf(component), column);
        }
        else if(isTreeColumn(column)) {
            if(node == getRoot()) {
                value = "root";
            }
            else {
                value = todoNode.get(VALUE_KEY).toString();
            }
        }
        return value;
    }

    private static final class RootTodoNode extends TodoNode {

        @Override
        public boolean isRoot() {
            return true;
        }
    }
}
