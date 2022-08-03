package by.babanin.todo.view.component.form;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneLayout;
import javax.swing.border.Border;

import by.babanin.todo.representation.ReportField;
import by.babanin.todo.view.util.GUIUtils;

public class TextAreaFormRow extends FormRow<String> {

    private static final int DEFAULT_COLUMNS = 32;
    private static final int DEFAULT_ROWS = 8;

    private final JTextArea textArea;

    public TextAreaFormRow(ReportField field) {
        this(field, DEFAULT_ROWS, DEFAULT_COLUMNS);
    }

    public TextAreaFormRow(ReportField field, int rows, int columns) {
        super(field);
        textArea = new JTextArea(rows, columns);
        GUIUtils.addChangeListener(textArea, e -> stateChanged());
    }

    @Override
    public JComponent getInputComponent() {
        return textArea;
    }

    @Override
    public JComponent wrap(JComponent component) {
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setLayout(new CustomScrollPaneLayout());
        return scrollPane;
    }

    @Override
    public String getValue() {
        return textArea.getText();
    }

    @Override
    public void setValue(String value) {
        textArea.setText(value);
    }

    private static final class CustomScrollPaneLayout extends ScrollPaneLayout {
        @Override
        public Dimension preferredLayoutSize(Container parent) {
            /* Sync the (now obsolete) policy fields with the
             * JScrollPane.
             */
            JScrollPane scrollPane = (JScrollPane)parent;
            vsbPolicy = scrollPane.getVerticalScrollBarPolicy();
            hsbPolicy = scrollPane.getHorizontalScrollBarPolicy();

            Insets insets = parent.getInsets();
            int prefWidth = insets.left + insets.right;
            int prefHeight = insets.top + insets.bottom;

            /* Note that viewport.getViewSize() is equivalent to
             * viewport.getView().getPreferredSize() modulo a null
             * view or a view whose size was explicitly set.
             */

            Dimension extentSize = null;

            if (viewport != null) {
                extentSize = viewport.getPreferredSize();
            }

            /* If there's a viewport add its preferredSize.
             */

            if (extentSize != null) {
                prefWidth += extentSize.width;
                prefHeight += extentSize.height;
            }

            /* If there's a JScrollPane.viewportBorder, add its insets.
             */

            Border viewportBorder = scrollPane.getViewportBorder();
            if (viewportBorder != null) {
                Insets vpbInsets = viewportBorder.getBorderInsets(parent);
                prefWidth += vpbInsets.left + vpbInsets.right;
                prefHeight += vpbInsets.top + vpbInsets.bottom;
            }

            /* If a header exists and it's visible, factor its
             * preferred size in.
             */

            if ((rowHead != null) && rowHead.isVisible()) {
                prefWidth += rowHead.getPreferredSize().width;
            }

            if ((colHead != null) && colHead.isVisible()) {
                prefHeight += colHead.getPreferredSize().height;
            }

            return new Dimension(prefWidth, prefHeight);
        }
    }
}
