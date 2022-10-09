package by.babanin.todo.view.component.tree;

import java.awt.Component;
import java.util.function.Supplier;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * A DefaultTreeCellRenderer which can optionally skip drawing all icons.
 */
public class TreeCellRenderer extends DefaultTreeCellRenderer {

    private final transient Supplier<TreeTableModel> treeTableModelSupplier;
    private final transient Supplier<Boolean> allowShowIconsSupplier;

    public TreeCellRenderer(Supplier<TreeTableModel> treeTableModelSupplier, Supplier<Boolean> allowShowIconsSupplier) {
        this.treeTableModelSupplier = treeTableModelSupplier;
        this.allowShowIconsSupplier = allowShowIconsSupplier;
    }

    @Override
    public Icon getClosedIcon() {
        return getIfAllowShowIcons(super::getClosedIcon);
    }

    @Override
    public Icon getDefaultClosedIcon() {
        return getIfAllowShowIcons(super::getDefaultClosedIcon);
    }

    @Override
    public Icon getDefaultLeafIcon() {
        return getIfAllowShowIcons(super::getDefaultLeafIcon);
    }

    @Override
    public Icon getDefaultOpenIcon() {
        return getIfAllowShowIcons(super::getDefaultOpenIcon);
    }

    @Override
    public Icon getLeafIcon() {
        return getIfAllowShowIcons(super::getLeafIcon);
    }

    @Override
    public Icon getOpenIcon() {
        return getIfAllowShowIcons(super::getOpenIcon);
    }

    private boolean isAllowShowIcons() {
        return Boolean.TRUE.equals(allowShowIconsSupplier.get());
    }

    private Icon getIfAllowShowIcons(Supplier<Icon> iconSupplier) {
        return isAllowShowIcons() ? iconSupplier.get() : null;
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
        Component component = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        TreeTableModel treeTableModel = treeTableModelSupplier.get();
        if(treeTableModel != null) {
            value = treeTableModel.getValueAt(value, treeTableModel.getTreeColumn());
            if(value != null) {
                setText(value.toString());
            }
        }
        return component;
    }
}
