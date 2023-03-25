package by.babanin.todo.view.about;

import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import by.babanin.todo.view.component.custom.CustomDialog;
import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;
import by.babanin.todo.view.util.GUIUtils;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public final class ShowAboutAction extends AbstractAction {

    private final transient AboutInfo aboutInfo;
    private final FlatSVGIcon appLogoIcon;

    public ShowAboutAction(AboutInfo aboutInfo, FlatSVGIcon aboutActionIcon, FlatSVGIcon appLogoIcon) {
        super(Translator.toLocale(TranslateCode.MAIN_MENU_ABOUT), aboutActionIcon);
        this.aboutInfo = aboutInfo;
        this.appLogoIcon = appLogoIcon;
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
        dialog.setIconImage(appLogoIcon.getImage());

        dialog.setVisible(true);
    }
}
