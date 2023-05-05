package org.jboss.pnc.common.alignment.ranking.parser;

import org.jboss.pnc.common.alignment.ranking.tokenizer.Token;

public interface Node {
    Token getToken();

    InternalNode getParent();

    void setParent(InternalNode parent);

}
