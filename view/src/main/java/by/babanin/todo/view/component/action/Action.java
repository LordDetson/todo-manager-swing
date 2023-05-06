package by.babanin.todo.view.component.action;

import java.awt.event.ActionEvent;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.KeyStroke;

public class Action extends AbstractAction {

    private Consumer<ActionEvent> actionImpl;

    public String getId() {
        return (String) getValue(ACTION_COMMAND_KEY);
    }

    public void setId(String id) {
        putValue(ACTION_COMMAND_KEY, id);
    }

    public String getName() {
        return (String) getValue(NAME);
    }

    public void setName(String name) {
        putValue(NAME, name);
    }

    public String getToolTip() {
        return (String) getValue(SHORT_DESCRIPTION);
    }

    public void setToolTip(String toolTip) {
        putValue(SHORT_DESCRIPTION, toolTip);
    }

    public Icon getSmallIcon() {
        return (Icon) getValue(SMALL_ICON);
    }

    public void setSmallIcon(Icon smallIcon) {
        putValue(SMALL_ICON, smallIcon);
    }

    public Icon getLargeIcon() {
        return (Icon) getValue(LARGE_ICON_KEY);
    }

    public void setLargeIcon(Icon largeIcon) {
        putValue(LARGE_ICON_KEY, largeIcon);
    }

    public KeyStroke getAccelerator() {
        return (KeyStroke) getValue(ACCELERATOR_KEY);
    }

    public void setAccelerator(KeyStroke accelerator) {
        putValue(ACCELERATOR_KEY, accelerator);
    }

    public int getMnemonic() {
        Object value = getValue(MNEMONIC_KEY);
        if(value == null) {
            return -1;
        }
        return (int) value;
    }

    public void setMnemonic(int mnemonic) {
        putValue(MNEMONIC_KEY, mnemonic);
    }

    public boolean isSelected() {
        Object value = getValue(SELECTED_KEY);
        if(value == null) {
            return false;
        }
        return (boolean) getValue(SELECTED_KEY);
    }

    public void setSelected(boolean selected) {
        putValue(SELECTED_KEY, selected);
    }

    public void setAction(Consumer<ActionEvent> action) {
        this.actionImpl = action;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        actionImpl.accept(e);
    }

    @Override
    public String toString() {
        String id = getId();
        if(id != null) {
            return id;
        }
        String name = getName();
        if(name != null) {
            return name;
        }
        return super.toString();
    }

    public static ActionBuilder<Action> builder() {
        return builder(Action::new);
    }

    public static <T extends Action> ActionBuilder<T> builder(Supplier<T> actionFactory) {
        return new ActionBuilder<>(actionFactory);
    }

    public static class ActionBuilder<T extends Action> {

        private final Supplier<T> actionFactory;

        private String id;
        private String name;
        private String toolTip;
        private Icon smallIcon;
        private Icon largeIcon;
        private KeyStroke accelerator;
        private Integer mnemonic;
        private Boolean enable;
        private Boolean selected;
        private Consumer<ActionEvent> actionImpl;

        public ActionBuilder(Supplier<T> actionFactory) {
            this.actionFactory = actionFactory;
        }

        public ActionBuilder<T> id(String id) {
            this.id = id;
            return this;
        }

        public ActionBuilder<T> name(String name) {
            this.name = name;
            return this;
        }

        public ActionBuilder<T> toolTip(String toolTip) {
            this.toolTip = toolTip;
            return this;
        }

        public ActionBuilder<T> smallIcon(Icon smallIcon) {
            this.smallIcon = smallIcon;
            return this;
        }

        public ActionBuilder<T> largeIcon(Icon largeIcon) {
            this.largeIcon = largeIcon;
            return this;
        }

        public ActionBuilder<T> accelerator(KeyStroke accelerator) {
            this.accelerator = accelerator;
            return this;
        }

        public ActionBuilder<T> mnemonic(int mnemonic) {
            this.mnemonic = mnemonic;
            return this;
        }

        public ActionBuilder<T> disable() {
            this.enable = false;
            return this;
        }

        public ActionBuilder<T> enable() {
            this.enable = true;
            return this;
        }

        public ActionBuilder<T> selected(boolean selected) {
            this.selected = selected;
            return this;
        }

        public ActionBuilder<T> action(Consumer<ActionEvent> action) {
            this.actionImpl = action;
            return this;
        }

        public T build() {
            T action = actionFactory.get();
            if(id != null) {
                action.setId(id);
            }
            if(name != null) {
                action.setName(name);
            }
            if(toolTip != null) {
                action.setToolTip(toolTip);
            }
            if(smallIcon != null) {
                action.setSmallIcon(smallIcon);
            }
            if(largeIcon != null) {
                action.setLargeIcon(largeIcon);
            }
            if(accelerator != null) {
                action.setAccelerator(accelerator);
            }
            if(mnemonic != null) {
                action.setMnemonic(mnemonic);
            }
            if(enable != null) {
                action.setEnabled(enable);
            }
            if(selected != null) {
                action.setSelected(selected);
            }
            if(this.actionImpl != null) {
                action.setAction(this.actionImpl);
            }
            return action;
        }
    }
}
