package by.babanin.todo.ui.about;

import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JDialog;

import org.springframework.stereotype.Component;

import by.babanin.ext.component.action.Action;
import by.babanin.ext.component.util.GUIUtils;
import by.babanin.ext.component.util.IconRegister;
import by.babanin.ext.message.Translator;
import by.babanin.todo.ui.translat.AppTranslateCode;

@Component
public final class ShowAboutAction extends Action {

    private static final String CLOSE_ABOUT_DIALOG_KEY = "closeAboutDialog";

    private final transient AboutInfo aboutInfo;

    public ShowAboutAction(AboutInfo aboutInfo) {
        setName(Translator.toLocale(AppTranslateCode.MAIN_MENU_ABOUT));
        setSmallIcon(IconRegister.get("i", 12));
        setMnemonic(KeyEvent.VK_A);
        this.aboutInfo = aboutInfo;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String title = Translator.toLocale(AppTranslateCode.ABOUT_TITLE).formatted(aboutInfo.getProduct().getNameWithVersion());
        JDialog dialog = new JDialog(GUIUtils.getMainWindow(), title, ModalityType.APPLICATION_MODAL);
        dialog.setContentPane(new AboutPanel(aboutInfo));
        dialog.setResizable(false);
        dialog.pack();
        dialog.setLocationRelativeTo(dialog.getOwner());
        GUIUtils.addCloseActionOnEscape(dialog, CLOSE_ABOUT_DIALOG_KEY);

        dialog.setVisible(true);
    }
}
