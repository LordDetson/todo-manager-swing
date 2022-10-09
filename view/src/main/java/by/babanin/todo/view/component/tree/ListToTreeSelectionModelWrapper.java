package by.babanin.todo.view.component.tree;

import java.util.Optional;
import java.util.function.Function;

import javax.swing.ListSelectionModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;

/**
 * ListToTreeSelectionModelWrapper extends DefaultTreeSelectionModel
 * to listen for changes in the ListSelectionModel it maintains. Once
 * a change in the ListSelectionModel happens, the paths are updated
 * in the DefaultTreeSelectionModel.
 */
public class ListToTreeSelectionModelWrapper extends DefaultTreeSelectionModel {

    private final transient Function<Integer, Optional<TreePath>> pathForRowGetter;

    /**
     * Set to true when we are updating the ListSelectionModel.
     */
    private boolean updatingListSelectionModel;

    public ListToTreeSelectionModelWrapper(Function<Integer, Optional<TreePath>> pathForRowGetter) {
        this.pathForRowGetter = pathForRowGetter;
        getListSelectionModel().addListSelectionListener(event -> updateSelectedPathsFromSelectedRows());
    }

    /**
     * Returns the list selection model. ListToTreeSelectionModelWrapper
     * listens for changes to this model and updates the selected paths
     * accordingly.
     */
    protected ListSelectionModel getListSelectionModel() {
        return listSelectionModel;
    }

    /**
     * This is overridden to set <code>updatingListSelectionModel</code>
     * and message super. This is the only place DefaultTreeSelectionModel
     * alters the ListSelectionModel.
     */
    @Override
    public void resetRowSelection() {
        doIfNotUpdating(super::resetRowSelection);
    }

    /**
     * If <code>updatingListSelectionModel</code> is false, this will
     * reset the selected paths from the selected rows in the list
     * selection model.
     */
    protected void updateSelectedPathsFromSelectedRows() {
        doIfNotUpdating(() -> {
            // This is way expensive, ListSelectionModel needs an
            // enumerator for iterating.
            int min = listSelectionModel.getMinSelectionIndex();
            int max = listSelectionModel.getMaxSelectionIndex();

            clearSelection();
            if(min != -1 && max != -1) {
                for(int row = min; row <= max; row++) {
                    if(listSelectionModel.isSelectedIndex(row)) {
                        pathForRowGetter.apply(row).ifPresent(this::addSelectionPath);
                    }
                }
            }
        });
    }

    private void doIfNotUpdating(Runnable runner) {
        if(!updatingListSelectionModel) {
            updatingListSelectionModel = true;
            try {
                runner.run();
            }
            finally {
                updatingListSelectionModel = false;
            }
        }
    }
}
