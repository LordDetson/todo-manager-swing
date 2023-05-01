package by.babanin.todo.view;

import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.WindowConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import by.babanin.todo.preferences.PreferenceAware;
import by.babanin.todo.preferences.PreferencesGroup;
import by.babanin.todo.view.preference.DimensionPreference;
import by.babanin.todo.view.preference.IntegerPreference;
import by.babanin.todo.view.preference.PointPreference;
import by.babanin.todo.view.util.GUIUtils;

@Component
public class MainFrame extends JFrame implements PreferenceAware<PreferencesGroup> {

    private static final String MAIN_FRAME_SIZE_KEY = "mainFrameSize";
    private static final String MAIN_FRAME_EXTENDED_STATE_KEY = "mainFrameExtendedState";
    private static final String MAIN_FRAME_LOCATION_KEY = "mainFrameLocation";
    private static final String TO_DO_PANEL_KEY = "toDoPanel";

    private final TodoPanel todoPanel;

    public MainFrame(TodoPanel todoPanel) throws HeadlessException {
        this.todoPanel = todoPanel;
        setName("mainFrame");
        GUIUtils.setMainWindow(this);
        setContentPane(this.todoPanel);

        setMinimumSize(GUIUtils.getHalfFrameSize());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        GUIUtils.addPreferenceSupport(this);
    }

    @Override
    @Autowired
    public void setJMenuBar(JMenuBar menubar) {
        super.setJMenuBar(menubar);
    }

    @Override
    @Value("#{'${about.product.name}' + ' ' + '${about.product.version}'}")
    public void setTitle(String title) {
        super.setTitle(title);
    }

    @Override
    @Autowired
    public void setIconImage(Image appLogoImage) {
        super.setIconImage(appLogoImage);
    }

    public void load() {
        todoPanel.load();
    }

    @Override
    public void apply(PreferencesGroup preferencesGroup) {
        preferencesGroup.get(MAIN_FRAME_SIZE_KEY)
                        .ifPresentOrElse(preference -> setSize(((DimensionPreference) preference).getDimension()),
                                () -> setSize(GUIUtils.getLargeFrameSize()));
        preferencesGroup.get(MAIN_FRAME_EXTENDED_STATE_KEY)
                .ifPresent(preference -> setExtendedState(((IntegerPreference) preference).getValue()));
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
        IntegerPreference extendedStatePreference = new IntegerPreference();
        extendedStatePreference.setValue(getExtendedState());
        PointPreference pointPreference = new PointPreference();
        pointPreference.setPoint(getLocation());
        PreferencesGroup preferencesGroup = new PreferencesGroup();
        if(extendedStatePreference.getValue() == Frame.MAXIMIZED_BOTH) {
            preferencesGroup.put(MAIN_FRAME_EXTENDED_STATE_KEY, extendedStatePreference);
        }
        else {
            preferencesGroup.put(MAIN_FRAME_SIZE_KEY, dimensionPreference);
        }
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
}
