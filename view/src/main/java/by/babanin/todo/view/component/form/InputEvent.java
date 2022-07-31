package by.babanin.todo.view.component.form;

import javax.swing.event.ChangeEvent;

public class InputEvent extends ChangeEvent {

    private boolean valid;
    private boolean hasWarning;

    public InputEvent(Object source) {
        super(source);
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isValid() {
        return valid;
    }

    public void setHasWarning(boolean hasWarning) {
        this.hasWarning = hasWarning;
    }

    public boolean hasWarning() {
        return hasWarning;
    }
}
