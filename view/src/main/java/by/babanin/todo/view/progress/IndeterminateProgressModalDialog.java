package by.babanin.todo.view.progress;

import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import by.babanin.todo.view.util.GUIUtils;

public final class IndeterminateProgressModalDialog extends JDialog {

    private static final int DEFAULT_WIDTH = 350;

    public IndeterminateProgressModalDialog() {
        super(GUIUtils.getActiveWindow(), "", ModalityType.APPLICATION_MODAL);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setMinimumSize(new Dimension(DEFAULT_WIDTH, progressBar.getMinimumSize().height));
        progressBar.setPreferredSize(progressBar.getMinimumSize());
        JPanel panel = new JPanel();
        panel.add(progressBar);

        setContentPane(panel);
        setResizable(false);
        setUndecorated(true);
        pack();
        setLocationRelativeTo(getOwner());
        setMinimumSize(getSize());
    }
}
