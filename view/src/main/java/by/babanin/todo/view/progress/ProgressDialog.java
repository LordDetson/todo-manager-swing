package by.babanin.todo.view.progress;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import by.babanin.todo.task.Task;
import by.babanin.todo.view.util.GUIUtils;

public final class ProgressDialog extends JDialog {

    private static final int DEFAULT_WIDTH = 350;

    private ProgressDialog() {
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

    public static void initiate(Task<?> task) {
        ProgressDialog progressDialog = new ProgressDialog();

        task.addFinishListener(components -> progressDialog.dispose());
        task.addExceptionListener(exception -> progressDialog.dispose());

        EventQueue.invokeLater(() -> progressDialog.setVisible(true));
    }
}
