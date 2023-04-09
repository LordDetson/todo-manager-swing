package by.babanin.todo.view;

import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import by.babanin.todo.preferences.PreferenceAware;
import by.babanin.todo.preferences.PreferencesGroup;
import by.babanin.todo.view.about.AboutInfo;
import by.babanin.todo.view.preference.DimensionPreference;
import by.babanin.todo.view.preference.PointPreference;
import by.babanin.todo.view.util.GUIUtils;
import jakarta.annotation.PostConstruct;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class MainFrame extends JFrame implements PreferenceAware<PreferencesGroup> {

    private static final String MAIN_FRAME_SIZE_KEY = "mainFrameSize";
    private static final String MAIN_FRAME_LOCATION_KEY = "mainFrameLocation";
    private static final String TO_DO_PANEL_KEY = "toDoPanel";

    private final AboutInfo aboutInfo;
    private final FlatSVGIcon appLogoIcon;
    private TodoPanel todoPanel;

    protected MainFrame(AboutInfo aboutInfo, FlatSVGIcon appLogoIcon) throws HeadlessException {
        this.aboutInfo = aboutInfo;
        this.appLogoIcon = appLogoIcon;
        setName("mainFrame");
        GUIUtils.setMainWindow(this);
    }

    @PostConstruct
    void init() {
        setJMenuBar(createMainMenuBar());

        todoPanel = createTodoPanel();
        todoPanel.load();
        setContentPane(todoPanel);

        setMinimumSize(GUIUtils.getHalfFrameSize());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle(this.aboutInfo.getProduct().getNameWithVersion());
        setIconImage(this.appLogoIcon.getImage());
        GUIUtils.addPreferenceSupport(this);
    }

    @Override
    public void apply(PreferencesGroup preferencesGroup) {
        preferencesGroup.get(MAIN_FRAME_SIZE_KEY)
                .ifPresent(preference -> {
                    DimensionPreference dimensionPreference = (DimensionPreference) preference;
                    setSize(dimensionPreference.getDimension());
                });
        preferencesGroup.get(MAIN_FRAME_LOCATION_KEY)
                .ifPresentOrElse(preference -> setLocation(((PointPreference) preference).getPoint()),
                        () -> setLocationRelativeTo(null));
        preferencesGroup.get(TO_DO_PANEL_KEY)
                .ifPresent(preference -> todoPanel.apply((PreferencesGroup) preference));
    }

    @Override
    public PreferencesGroup createCurrentPreference() {
        DimensionPreference dimensionPreference = new DimensionPreference();
        dimensionPreference.setDimension(getSize());
        PointPreference pointPreference = new PointPreference();
        pointPreference.setPoint(getLocation());
        PreferencesGroup preferencesGroup = new PreferencesGroup();
        preferencesGroup.put(MAIN_FRAME_SIZE_KEY, dimensionPreference);
        preferencesGroup.put(MAIN_FRAME_LOCATION_KEY, pointPreference);
        preferencesGroup.put(TO_DO_PANEL_KEY, todoPanel.createCurrentPreference());
        return preferencesGroup;
    }

    @Override
    public PreferencesGroup createDefaultPreference() {
        DimensionPreference dimensionPreference = new DimensionPreference();
        dimensionPreference.setDimension(GUIUtils.getLargeFrameSize());
        PreferencesGroup preferencesGroup = new PreferencesGroup();
        preferencesGroup.put(MAIN_FRAME_SIZE_KEY, dimensionPreference);
        preferencesGroup.put(TO_DO_PANEL_KEY, todoPanel.createDefaultPreference());
        return preferencesGroup;
    }

    @Override
    public String getKey() {
        return getName();
    }

    @Lookup
    protected abstract MainMenuBar createMainMenuBar();

    @Lookup
    protected abstract TodoPanel createTodoPanel();
}
