package org.jboss.pnc.common.alignment.ranking.parser;

import org.jboss.pnc.common.alignment.ranking.tokenizer.Token;

import java.util.List;

public interface Parser {

    public InternalNode generateParseTree(List<Token> tokens);
}
