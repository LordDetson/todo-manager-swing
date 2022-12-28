package by.babanin.todo.view.about;

import java.awt.Dialog.ModalityType;

import javax.swing.JDialog;

import by.babanin.todo.image.IconResources;
import by.babanin.todo.view.component.custom.CustomDialog;
import by.babanin.todo.view.exception.ViewException;
import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;
import by.babanin.todo.view.util.GUIUtils;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AboutDialog {

    private static final int DIALOG_ICON_SIZE = 16;

    private static AboutInfo aboutInfo;

    public static void setAboutInfo(AboutInfo aboutInfo) {
        if(AboutDialog.aboutInfo != null) {
            throw new ViewException("aboutInfo is already exist");
        }
        AboutDialog.aboutInfo = aboutInfo;
    }

    public static void show() {
        if(AboutDialog.aboutInfo == null) {
            throw new ViewException("aboutInfo is not exist");
        }

        String title = Translator.toLocale(TranslateCode.ABOUT_TITLE).formatted(aboutInfo.getProduct().getName());
        JDialog dialog = new CustomDialog(GUIUtils.getMainWindow(), title, ModalityType.APPLICATION_MODAL);
        dialog.setContentPane(new AboutPanel(aboutInfo));
        dialog.setResizable(false);
        dialog.pack();
        dialog.setLocationRelativeTo(dialog.getOwner());
        dialog.setIconImage(IconResources.getIcon("transparent_check_hexagon", DIALOG_ICON_SIZE).getImage());

        dialog.setVisible(true);
    }
}
