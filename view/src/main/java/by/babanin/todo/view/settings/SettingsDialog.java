package by.babanin.todo.view.settings;

import java.awt.Dimension;

import javax.swing.JDialog;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.formdev.flatlaf.extras.FlatSVGIcon;

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
public abstract class SettingsDialog extends JDialog implements PreferenceAware<PreferencesGroup> {

    public static final String SETTINGS_DIALOG_CLOSING_ACTION_KEY = "closePrioritiesDialog";
    private static final String SIZE_KEY = "settingsDialogSize";
    private static final String LOCATION_KEY = "settingsDialogLocation";

    protected SettingsDialog(FlatSVGIcon appLogoIcon) {
        super(GUIUtils.getMainWindow(), true);
        setName("settingsDialog");
        setTitle(Translator.toLocale(TranslateCode.MAIN_MENU_SETTINGS));
        setIconImage(appLogoIcon.getImage());
        GUIUtils.addCloseActionOnEscape(this, SETTINGS_DIALOG_CLOSING_ACTION_KEY);
    }

    @PostConstruct
    void init() {
        SettingsPanel settingsPanel = createSettingsPanel();
        settingsPanel.setCancelAction(rootPane.getActionMap().get(SettingsDialog.SETTINGS_DIALOG_CLOSING_ACTION_KEY));
        rootPane.setDefaultButton(settingsPanel.getDefaultButton());
        setContentPane(settingsPanel);
        GUIUtils.addPreferenceSupport(this);
    }

    @Lookup
    protected abstract SettingsPanel createSettingsPanel();

    @Override
    public SettingsPanel getContentPane() {
        return (SettingsPanel) super.getContentPane();
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
        dimensionPreference.setDimension(GUIUtils.getHalfFrameSize());
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
