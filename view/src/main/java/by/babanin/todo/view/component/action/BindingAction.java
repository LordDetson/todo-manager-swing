package by.babanin.todo.view.component.action;

import static javax.swing.JComponent.WHEN_FOCUSED;

import javax.swing.JComponent;
import javax.swing.KeyStroke;

import org.apache.commons.lang3.StringUtils;

import by.babanin.todo.view.exception.ViewException;

public class BindingAction extends Action {

    public BindingAction(JComponent owner) {
        this(owner, WHEN_FOCUSED);
    }

    public BindingAction(JComponent owner, int condition) {
        addPropertyChangeListener(event -> {
            if(event.getPropertyName().equals(javax.swing.Action.ACCELERATOR_KEY)) {
                String id = getId();
                KeyStroke accelerator = getAccelerator();
                if(StringUtils.isBlank(id)) {
                    throw new ViewException("Id is blank");
                }
                if(accelerator == null) {
                    throw new ViewException("Accelerator is null");
                }
                owner.getInputMap(condition).put(accelerator, id);
                owner.getActionMap().put(id, BindingAction.this);
            }
        });
    }
}
