package by.babanin.todo.view.settings;

public interface Setting extends Cloneable {

    void update(Setting setting);

    Setting clone();
}
