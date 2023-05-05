package org.jboss.pnc.common.alignment.ranking.parser;

import org.jboss.pnc.common.alignment.ranking.tokenizer.Token;

import java.util.List;

public class UnaryNode extends AbstractNode implements InternalNode {

    private Node child;

    public UnaryNode() {
        this(null, null, null);
    }

    public UnaryNode(Token token) {
        this(null, token, null);
    }

    public UnaryNode(InternalNode parent, Token token, Node child) {
        super(parent, token);
        this.child = child;
    }

    @Override
    public int childrenCount() {
        return (child == null) ? 0 : 1;
    }

    @Override
    public List<Node> getChildren() {
        return List.of(child);
    }

    public void setChild(Node child) {
        child.setParent(this);
        this.child = child;
    }

    public Node getChild() {
        return child;
    }

    @Override
    public void removeChild(Node child) {
        if (this.child == child) {
            this.child = null;
            if (child.getParent() == this) {
                child.setParent(null);
            }
        }
    }

    @Override
    public void switchChild(Node badChild, Node goodChild) {
        if (child == badChild) {
            removeChild(badChild);
            setChild(goodChild);
        }
    }
}
