package by.babanin.todo.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;
import by.babanin.todo.view.util.GUIUtils;

public class TodoFrame extends JFrame {

    private JPanel contentPanel;

    public TodoFrame() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(contentPanel);
        setSize(GUIUtils.getLargeFrameSize());
        setLocationRelativeTo(null);
        setTitle(Translator.toLocale(TranslateCode.TODO_FRAME_TITLE));
    }
}
