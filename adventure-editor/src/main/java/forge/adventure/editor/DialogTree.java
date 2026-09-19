package forge.adventure.editor;

import forge.adventure.data.DialogData;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DialogTree extends JPanel {
    private JTree dialogTree;
    private JScrollPane scrollPane;


    public DialogTree(){
        setLayout(new BorderLayout());
        // Create the JTree component
        dialogTree = new JTree();
        dialogTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        addSelectionListener();
        // Create a scroll pane to contain the JTree
        scrollPane = new JScrollPane(dialogTree);
        // Add the scroll pane to the panel
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadDialog(DialogData dialogData){
//        rootNode = buildBranches(dialogData);
//        ((DefaultTreeModel)dialogTree.getModel()).setRoot(rootNode);
        ((DefaultTreeModel)dialogTree.getModel()).setRoot(buildBranches(dialogData));
    }

    public DefaultMutableTreeNode buildBranches(DialogData dialogData)
    {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(dialogData);
        for (DialogData option : dialogData.options){
            node.add(buildBranches(option));
        }
        return node;
    }

    private final List<TreeSelectionListener> selectionListeners = new ArrayList<>();

    public void addSelectionListener(){
        //subscribe to valueChanged, change to that object in edit pane
        dialogTree.getSelectionModel().addTreeSelectionListener(this::emitChanged);

    }

    public void addSelectionListener(final TreeSelectionListener listener) {
        selectionListeners.remove(listener); //ensure listener not added multiple times
        selectionListeners.add(listener);
    }

    protected void emitChanged(TreeSelectionEvent evt) {
        if (selectionListeners != null && selectionListeners.size() > 0) {
            for (TreeSelectionListener listener : selectionListeners) {
                listener.valueChanged(evt);
            }
        }
    }

    public void replaceCurrent(){
        if (dialogTree.getSelectionPath() == null || dialogTree.getSelectionPath().getLastPathComponent() == null)
            return;
        dialogTree.updateUI();
    }

    public DialogData getSelectedData() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) dialogTree.getLastSelectedPathComponent();
        if (selectedNode != null) {
            return (DialogData) selectedNode.getUserObject();
        }
        return null;
    }

    public void removeSelectedData() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) dialogTree.getLastSelectedPathComponent();

        DialogData parentData = (DialogData) ((DefaultMutableTreeNode)selectedNode.getParent()).getUserObject();
        parentData.options = Arrays.stream(parentData.options).filter(q -> q != selectedNode.getUserObject()).toArray(DialogData[]::new);
       ((DefaultTreeModel) dialogTree.getModel()).removeNodeFromParent(selectedNode);

        setSelectedData(parentData);
    }
    public void setSelectedData(DialogData data) {
        List<TreePath> expanded = snapshotExpandedPaths();
        DefaultMutableTreeNode node = findNode((DefaultMutableTreeNode)dialogTree.getModel().getRoot(), data);
        if (node != null) {
            dialogTree.setSelectionPath(new TreePath(node.getPath()));
        }
        restoreExpandedPaths(expanded);
    }

    private List<TreePath> snapshotExpandedPaths() {
        List<TreePath> expanded = new ArrayList<>();
        Object root = dialogTree.getModel().getRoot();
        if (root == null) {
            return expanded;
        }
        java.util.Enumeration<TreePath> descendants = dialogTree.getExpandedDescendants(new TreePath(root));
        if (descendants != null) {
            while (descendants.hasMoreElements()) {
                expanded.add(descendants.nextElement());
            }
        }
        return expanded;
    }

    private void restoreExpandedPaths(List<TreePath> expanded) {
        for (TreePath path : expanded) {
            dialogTree.expandPath(path);
        }
    }

    private DefaultMutableTreeNode findNode(DefaultMutableTreeNode parent, DialogData data) {
        // Search for the node with the given data object in the subtree rooted at the parent node
        if (parent.getUserObject() == data) {
            return parent;
        }
        for (int i = 0; i < parent.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) parent.getChildAt(i);
            DefaultMutableTreeNode result = findNode(child, data);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}
