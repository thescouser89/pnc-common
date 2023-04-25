package org.jboss.pnc.common.alignment.ranking.tokenizer;

import java.util.Iterator;

public interface Tokenizer extends Iterator<Token> {
    boolean hasNext();

    Token next();
}
