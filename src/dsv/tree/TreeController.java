package dsv.tree;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

public class TreeController {

    // ---- NODE CLASS ----
    public static class Node {
        int val;
        Node left, right;

        Node(int val) {
            this.val = val;
        }
    }
    // --------------------

    private Node root;

    @FXML
    private Pane treePane;

    @FXML
    private TextField valueField;

    @FXML
    private Label valueLabel;

    @FXML
    void clear(ActionEvent event) {
        root = null;
        treePane.getChildren().clear();
        valueLabel.setText("Tree cleared.");
    }

    @FXML
    void delete(ActionEvent event) {
        try {
            int value = Integer.parseInt(valueField.getText());
            root = delete(root, value);
            valueLabel.setText("Deleted: " + value);
            redrawTree();
            valueField.clear();
        } catch (NumberFormatException e) {
            valueLabel.setText("Enter a valid integer.");
        }
    }

    @FXML
    void insert(ActionEvent event) {
        try {
            int val = Integer.parseInt(valueField.getText());
            root = insert(root, val);
            valueLabel.setText("Inserted: " + val);
            redrawTree();
            valueField.clear();
        } catch (NumberFormatException e) {
            valueLabel.setText("Enter a valid integer.");
        }
    }

    private void redrawTree() {
        treePane.getChildren().clear();
        drawTree(root, treePane.getWidth() / 2, 40, treePane.getWidth() / 4);
    }

    private void drawTree(Node node, double x, double y, double hGap) {
        if (node == null) return;

        StackPane visualNode = createNode(node.val);
        visualNode.setLayoutX(x - 20);
        visualNode.setLayoutY(y - 20);

        if (node.left != null) {
            double childX = x - hGap;
            double childY = y + 80;

            Line line = new Line(x, y, childX, childY);
            treePane.getChildren().add(line);

            drawTree(node.left, childX, childY, hGap / 2);
        }

        if (node.right != null) {
            double childX = x + hGap;
            double childY = y + 80;

            Line line = new Line(x, y, childX, childY);
            treePane.getChildren().add(line);

            drawTree(node.right, childX, childY, hGap / 2);
        }

        treePane.getChildren().add(visualNode);
    }

    private StackPane createNode(int value) {
        Circle circle = new Circle(20);
        circle.setStyle("-fx-fill: lightblue; -fx-stroke: black;");

        Text text = new Text(String.valueOf(value));

        return new StackPane(circle, text);
    }

    // ---- BST LOGIC ----

    private Node insert(Node node, int val) {
        if (node == null) return new Node(val);

        if (val < node.val)
            node.left = insert(node.left, val);
        else if (val > node.val)
            node.right = insert(node.right, val);

        return node;
    }

    private Node delete(Node node, int val) {
        if (node == null) return null;

        if (val < node.val) {
            node.left = delete(node.left, val);
        } else if (val > node.val) {
            node.right = delete(node.right, val);
        } else {
            // case 1: no child
            if (node.left == null && node.right == null) return null;

            // case 2: one child
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;

            // case 3: two children
            Node min = findMin(node.right);
            node.val = min.val;
            node.right = delete(node.right, min.val);
        }

        return node;
    }

    private Node findMin(Node node) {
        while (node.left != null) node = node.left;
        return node;
    }

}
