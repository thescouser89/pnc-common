package org.jboss.pnc.common.alignment.ranking.parser;

import org.jboss.pnc.common.alignment.ranking.tokenizer.Token;

public class LeafNode extends AbstractNode implements Node {

    public LeafNode(Token token) {
        super(null, token);
    }
}
