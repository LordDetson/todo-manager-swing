package by.babanin.todo.view.component.form;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import by.babanin.todo.preferences.PreferenceAware;
import by.babanin.todo.preferences.PreferencesGroup;
import by.babanin.todo.view.preference.DimensionPreference;
import by.babanin.todo.view.preference.PointPreference;
import by.babanin.todo.view.translat.Translator;

public class FormDialog<C> extends JDialog implements PreferenceAware<PreferencesGroup> {

    private static final String SIZE_KEY = "prioritiesDialogSize";
    private static final String LOCATION_KEY = "prioritiesDialogLocation";

    public FormDialog(Component owner, ComponentForm<C> form, String titleCode) {
        super(JOptionPane.getFrameForComponent(owner), true);
        Class<C> componentClass = form.getComponentRepresentation().getComponentClass();
        String componentCaption = Translator.getComponentCaption(componentClass);
        setTitle(Translator.toLocale(titleCode).formatted(componentCaption));
        setContentPane(form);
        form.setOwner(this);
    }

    @Override
    public void apply(PreferencesGroup preferencesGroup) {
        preferencesGroup.get(SIZE_KEY)
                .ifPresentOrElse(preference -> {
                    Dimension size = ((DimensionPreference) preference).getDimension();
                    setMinimumSize(size);
                    setSize(size);
                }, this::pack);
        preferencesGroup.get(LOCATION_KEY)
                .ifPresentOrElse(preference -> setLocation(((PointPreference) preference).getPoint()),
                        () -> setLocationRelativeTo(getOwner()));
    }

    @Override
    public PreferencesGroup createCurrentPreference() {
        DimensionPreference dimensionPreference = new DimensionPreference();
        dimensionPreference.setDimension(getSize());
        PointPreference pointPreference = new PointPreference();
        pointPreference.setPoint(getLocation());
        PreferencesGroup preferencesGroup = new PreferencesGroup();
        preferencesGroup.put(SIZE_KEY, dimensionPreference);
        preferencesGroup.put(LOCATION_KEY, pointPreference);
        return preferencesGroup;
    }

    @Override
    public PreferencesGroup createDefaultPreference() {
        return new PreferencesGroup();
    }

    @Override
    public String getKey() {
        return getName();
    }
}
