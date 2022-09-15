package by.babanin.todo.view.exception;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.apache.commons.lang3.StringUtils;

import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;
import by.babanin.todo.view.util.GUIUtils;
import by.babanin.todo.image.IconResources;

public final class ExceptionDialog extends JDialog {

    private static final String TO_SHOW_DETAILS_SUFFIX = " >>";
    private static final String TO_CLOSE_DETAILS_SUFFIX = " <<";
    private static final int DEFAULT_WIDTH = 450;
    private static final int EXPANDED_HEIGHT = 250;
    private static final int DEFAULT_EMPTY_SPACE_SIZE = 10;
    private static final int DEFAULT_ICON_SIZE = 48;
    private static final Pattern LINE_REGEX = Pattern.compile("\\r?\\n", Pattern.MULTILINE);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");

    private final String message;
    private final String details;

    private JPanel messagePanel;
    private JButton okButton;
    private JButton saveButton;
    private JToggleButton showDetailsButton;
    private JPanel buttonsPanel;
    private JScrollPane detailsPanel;

    private ExceptionDialog(Throwable throwable) {
        super(GUIUtils.getActiveWindow(), Translator.toLocale(TranslateCode.ERROR), Dialog.DEFAULT_MODALITY_TYPE);
        String msg = throwable.getMessage();
        if(StringUtils.isBlank(msg)) {
            msg = "Unexpected error occurred: " + throwable.getClass().getSimpleName();
        }
        this.message = msg;
        this.details = GUIUtils.stacktraceToString(throwable);

        initializeComponents();
        addListeners();
        placeComponents();

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        pack();
        setLocationRelativeTo(GUIUtils.getActiveWindow());
        setMinimumSize(getSize());
    }

    private void initializeComponents() {
        initializeMessagePanel();
        initializeButtonsPanel();
        initializeDetailsPanel();
    }

    private void initializeMessagePanel() {
        JLabel label = new JLabel(IconResources.getIcon("error", DEFAULT_ICON_SIZE), SwingConstants.CENTER);
        JPanel imagePanel = new JPanel();
        imagePanel.add(label);
        imagePanel.setBorder(BorderFactory.createEmptyBorder(DEFAULT_EMPTY_SPACE_SIZE, DEFAULT_EMPTY_SPACE_SIZE,
                0, DEFAULT_EMPTY_SPACE_SIZE));

        JTextArea textArea = new JTextArea(message);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createEmptyBorder(DEFAULT_EMPTY_SPACE_SIZE, 0, 0, DEFAULT_EMPTY_SPACE_SIZE));

        messagePanel = new JPanel(new BorderLayout());
        messagePanel.add(imagePanel, BorderLayout.WEST);
        messagePanel.add(textArea, BorderLayout.CENTER);
        messagePanel.setPreferredSize(new Dimension(
                Math.max(messagePanel.getPreferredSize().width, DEFAULT_WIDTH), messagePanel.getPreferredSize().height));
    }

    private void initializeButtonsPanel() {
        okButton = new JButton(Translator.toLocale(TranslateCode.EXCEPTION_OK_BUTTON_TEXT));
        getRootPane().setDefaultButton(okButton);
        saveButton = new JButton(Translator.toLocale(TranslateCode.EXCEPTION_SAVE_BUTTON_TEXT));
        showDetailsButton = new JToggleButton();
        showDetailsButton.setSelected(false);
        updateShowDetailsButtonText();

        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.LINE_AXIS));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(DEFAULT_EMPTY_SPACE_SIZE, DEFAULT_EMPTY_SPACE_SIZE,
                DEFAULT_EMPTY_SPACE_SIZE, DEFAULT_EMPTY_SPACE_SIZE));
        buttonsPanel.add(Box.createHorizontalGlue());
        buttonsPanel.add(okButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(DEFAULT_EMPTY_SPACE_SIZE, 0)));
        buttonsPanel.add(saveButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(DEFAULT_EMPTY_SPACE_SIZE, 0)));
        buttonsPanel.add(showDetailsButton);
    }

    private void initializeDetailsPanel() {

        JTextArea detailsArea = new JTextArea();
        detailsArea.setMargin(new Insets(8, 8, 8, 8));
        detailsArea.setRows(10);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setTabSize(2);
        detailsArea.setEditable(false);
        detailsArea.setText(details != null ? details : "");

        detailsPanel = new JScrollPane(detailsArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        detailsPanel.setVisible(false);
    }

    private void addListeners() {
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent event) {
                doClose();
            }
        });
        okButton.addActionListener(event -> doClose());
        saveButton.addActionListener(event -> saveDetails());
        showDetailsButton.addActionListener(event -> toggleDetails());
    }

    private void placeComponents() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(messagePanel, BorderLayout.NORTH);
        panel.add(buttonsPanel, BorderLayout.PAGE_END);
        contentPanel.add(panel, BorderLayout.NORTH);
        contentPanel.add(detailsPanel, BorderLayout.CENTER);
        setContentPane(contentPanel);
    }

    private void doClose() {
        dispose();
    }

    private void toggleDetails() {
        boolean showDetails = showDetailsButton.isSelected();
        detailsPanel.setVisible(showDetails);
        updateShowDetailsButtonText();
        if(showDetails) {
            setSize(Math.max(getWidth(), DEFAULT_WIDTH), getHeight() + EXPANDED_HEIGHT);
            validate();
        }
        else {
            pack();
        }
    }

    private void updateShowDetailsButtonText() {
        showDetailsButton.setText(getShowDetailsButtonText());
    }

    private String getShowDetailsButtonText() {
        return Translator.toLocale(TranslateCode.EXCEPTION_SHOW_DETAILS_BUTTON_TEXT)
                + (showDetailsButton.isSelected()
                ? TO_CLOSE_DETAILS_SUFFIX
                : TO_SHOW_DETAILS_SUFFIX);
    }

    private void saveDetails() {
        File file = new File(LocalDateTime.now().format(DATE_TIME_FORMATTER) + ".log");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setSelectedFile(file);
        if(fileChooser.showSaveDialog(getOwner()) == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            try(PrintWriter writer = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)))) {
                for(String line : LINE_REGEX.split(details)) {
                    writer.println(line);
                }
                writer.flush();
            }
            catch(FileNotFoundException e) {
                throw new ViewException(e);
            }
        }
    }

    public static void display(Throwable throwable) {
        ExceptionDialog exceptionDialog = new ExceptionDialog(throwable);
        if(SwingUtilities.isEventDispatchThread()) {
            exceptionDialog.setVisible(true);
        }
        else {
            EventQueue.invokeLater(() -> exceptionDialog.setVisible(true));
        }
    }
}
