package by.babanin.todo.integration;

import org.springframework.stereotype.Component;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;

import by.babanin.ext.settings.SettingsSubscriber;
import by.babanin.ext.settings.SettingsUpdateEvent;
import by.babanin.ext.settings.style.AccentColorManager;
import by.babanin.ext.settings.style.FontManager;
import by.babanin.ext.settings.style.StyleSetting;
import by.babanin.ext.settings.style.ThemeManager;

@Component
public class StyleHandler implements SettingsSubscriber {

    @Override
    public void handleSettingsUpdateEvent(SettingsUpdateEvent event) {
        if(event.getSetting() instanceof StyleSetting styleSetting) {
            FlatAnimatedLafChange.showSnapshot();

            ThemeManager.apply(styleSetting.getThemeInfo().source());
            FontManager.apply(styleSetting.getFontFamily(), FontManager.getDefaultFont().getStyle(), styleSetting.getFontSize());
            AccentColorManager.apply();
            FlatLaf.updateUI();

            FlatAnimatedLafChange.hideSnapshotWithAnimation();
        }
    }
}