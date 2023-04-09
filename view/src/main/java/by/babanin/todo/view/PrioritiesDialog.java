package by.babanin.todo.view;

import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import by.babanin.todo.preferences.PreferenceAware;
import by.babanin.todo.preferences.PreferencesGroup;
import by.babanin.todo.view.preference.DimensionPreference;
import by.babanin.todo.view.preference.PointPreference;
import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;
import by.babanin.todo.view.util.GUIUtils;
import jakarta.annotation.PostConstruct;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class PrioritiesDialog extends JDialog implements PreferenceAware<PreferencesGroup> {

    private static final String PRIORITIES_DIALOG_CLOSING_ACTION_KEY = "closePrioritiesDialog";
    private static final String SIZE_KEY = "prioritiesDialogSize";
    private static final String LOCATION_KEY = "prioritiesDialogLocation";

    protected PrioritiesDialog(java.awt.Component owner) {
        super(JOptionPane.getFrameForComponent(owner), true);
        setName("prioritiesDialog");
        setTitle(Translator.toLocale(TranslateCode.PRIORITY_FRAME_TITLE));
        GUIUtils.addCloseActionOnEscape(this, PRIORITIES_DIALOG_CLOSING_ACTION_KEY);
        GUIUtils.addPreferenceSupport(this);
    }

    @PostConstruct
    void init() {
        setContentPane(createPriorityCrudTablePanel());
    }

    @Lookup
    protected abstract PriorityCrudTablePanel createPriorityCrudTablePanel();

    @Override
    public PriorityCrudTablePanel getContentPane() {
        return (PriorityCrudTablePanel) super.getContentPane();
    }

    @Override
    public void apply(PreferencesGroup preferencesGroup) {
        preferencesGroup.get(SIZE_KEY)
                .ifPresent(preference -> {
                    Dimension size = ((DimensionPreference) preference).getDimension();
                    setMinimumSize(size);
                    setSize(size);
                });
        preferencesGroup.get(LOCATION_KEY)
                .ifPresentOrElse(preference -> setLocation(((PointPreference) preference).getPoint()),
                        () -> setLocationRelativeTo(getOwner()));
        preferencesGroup.get(getContentPane().getKey())
                .ifPresent(preference -> getContentPane().apply((PreferencesGroup) preference));
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
        preferencesGroup.put(getContentPane().getKey(), getContentPane().createCurrentPreference());
        return preferencesGroup;
    }

    @Override
    public PreferencesGroup createDefaultPreference() {
        DimensionPreference dimensionPreference = new DimensionPreference();
        dimensionPreference.setDimension(GUIUtils.getSmallFrameSize());
        PreferencesGroup preferencesGroup = new PreferencesGroup();
        preferencesGroup.put(SIZE_KEY, dimensionPreference);
        preferencesGroup.put(getContentPane().getKey(), getContentPane().createDefaultPreference());
        return preferencesGroup;
    }

    @Override
    public String getKey() {
        return getName();
    }
}