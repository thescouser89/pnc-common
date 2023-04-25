package org.jboss.pnc.common.alignment.ranking.parser;

import org.jboss.pnc.common.alignment.ranking.tokenizer.Token;

public abstract class AbstractNode implements Node {

    private InternalNode parent;

    private final Token token;

    protected AbstractNode(InternalNode parent, Token token) {
        this.parent = parent;
        this.token = token;
    }

    @Override
    public Token getToken() {
        return token;
    }

    @Override
    public InternalNode getParent() {
        return parent;
    }

    @Override
    public void setParent(InternalNode parent) {
        this.parent = parent;
    }
}
