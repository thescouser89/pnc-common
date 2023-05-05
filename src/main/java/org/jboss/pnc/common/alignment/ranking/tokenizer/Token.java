package org.jboss.pnc.common.alignment.ranking.tokenizer;

public class Token {
    public final int pos;
    public final int endPos;
    public final TokenType tokenType;

    public Token(int pos, int endPos, TokenType tokenType) {
        this.pos = pos;
        this.endPos = endPos;
        this.tokenType = tokenType;
    }

    @Override
    public String toString() {
        switch (tokenType.kind) {
            case CHAR:
                return String.valueOf(tokenType.charLiteral);
            case LITERAL:
                return tokenType.literal;
            case ENUM: // should be handled and overriden in respective parents classes (QualifierToken, LogicToken...)
            default:
                throw new UnsupportedOperationException("Either Unknown TokenKind or Enum not overriden.");
        }
    }
}
