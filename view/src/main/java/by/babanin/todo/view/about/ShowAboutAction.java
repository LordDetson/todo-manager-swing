package by.babanin.todo.view.about;

import java.awt.Dialog.ModalityType;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JDialog;

import org.springframework.stereotype.Component;

import by.babanin.todo.view.component.custom.CustomDialog;
import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;
import by.babanin.todo.view.util.GUIUtils;

@Component
public final class ShowAboutAction extends AbstractAction {

    private final transient AboutInfo aboutInfo;
    private final Image appLogoImage;

    public ShowAboutAction(AboutInfo aboutInfo, Icon aboutActionIcon, Image appLogoImage) {
        super(Translator.toLocale(TranslateCode.MAIN_MENU_ABOUT), aboutActionIcon);
        this.aboutInfo = aboutInfo;
        this.appLogoImage = appLogoImage;
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String title = Translator.toLocale(TranslateCode.ABOUT_TITLE).formatted(aboutInfo.getProduct().getNameWithVersion());
        JDialog dialog = new CustomDialog(GUIUtils.getMainWindow(), title, ModalityType.APPLICATION_MODAL);
        dialog.setContentPane(new AboutPanel(aboutInfo));
        dialog.setResizable(false);
        dialog.pack();
        dialog.setLocationRelativeTo(dialog.getOwner());
        dialog.setIconImage(appLogoImage);

        dialog.setVisible(true);
    }
}
