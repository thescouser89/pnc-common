package org.jboss.pnc.common.alignment.ranking.parser;

import org.jboss.pnc.common.alignment.ranking.tokenizer.Token;

import java.util.List;

public class BinaryNode extends AbstractNode implements InternalNode, Priority {

    private Node leftChild;

    private Node rightChild;

    private final int priority;

    public BinaryNode(Token token) {
        this(null, token);
    }

    public BinaryNode(InternalNode parent, Token token) {
        super(parent, token);
        this.priority = getPriority(token);
    }

    @Override
    public int childrenCount() {
        int count = 0;

        if (leftChild != null) {
            count++;
        }
        if (rightChild != null) {
            count++;
        }
        return count;
    }

    @Override
    public List<Node> getChildren() {
        return List.of(leftChild, rightChild);
    }

    @Override
    public int getPriority() {
        return priority;
    }

    public Node getLeftChild() {
        return leftChild;
    }

    public Node getRightChild() {
        return rightChild;
    }

    public void setRightChild(Node node) {
        node.setParent(this);
        this.rightChild = node;
    }

    public void setLeftChild(Node node) {
        node.setParent(this);
        this.leftChild = node;
    }

    @Override
    public void removeChild(Node child) {
        if (leftChild == child) {
            leftChild = null;
            if (child.getParent() == this) {
                child.setParent(null);
            }
        } else if (rightChild == child) {
            rightChild = null;
            if (child.getParent() == this) {
                child.setParent(null);
            }
        }
    }

    @Override
    public void switchChild(Node badChild, Node goodChild) {
        if (leftChild == badChild) {
            removeChild(badChild);
            setLeftChild(goodChild);
        } else if (rightChild == badChild) {
            removeChild(badChild);
            setRightChild(goodChild);
        }
    }
}
