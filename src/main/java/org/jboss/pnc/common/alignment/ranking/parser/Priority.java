package org.jboss.pnc.common.alignment.ranking.parser;

import org.jboss.pnc.common.alignment.ranking.tokenizer.LogicToken;
import org.jboss.pnc.common.alignment.ranking.tokenizer.Token;

import static java.text.MessageFormat.format;
import static org.jboss.pnc.common.alignment.ranking.tokenizer.TokenType.LOGIC;
import static org.jboss.pnc.common.alignment.ranking.tokenizer.TokenType.COMMA;
import static org.jboss.pnc.common.alignment.ranking.tokenizer.TokenType.SORT_BY;
import static org.jboss.pnc.common.alignment.ranking.tokenizer.TokenType.LPAREN;
import static org.jboss.pnc.common.alignment.ranking.tokenizer.TokenType.RPAREN;

public interface Priority {
    int getPriority();

    default int getPriority(Token token) {
        if (token.tokenType == LOGIC) {
            var logicToken = (LogicToken) token;
            switch (logicToken.logicType) {
                case OR:
                    return 1;
                case AND:
                    return 2;
            }
        } else if (token.tokenType == COMMA) {
            // COMMA is considered the same as OR for Predicate Compilers
            return 1;
        } else if (token.tokenType == SORT_BY) {
            return 3;
        } else if (token.tokenType == LPAREN || token.tokenType == RPAREN) {
            return 4;
        }

        throw new IllegalArgumentException(format("Token {0} doesn't have a priority.", token.tokenType));
    }

}
