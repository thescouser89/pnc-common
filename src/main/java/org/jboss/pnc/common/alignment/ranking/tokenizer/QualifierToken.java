package org.jboss.pnc.common.alignment.ranking.tokenizer;

import lombok.EqualsAndHashCode;
import org.jboss.pnc.api.enums.Qualifier;

@EqualsAndHashCode(callSuper = false) // compare just qualifier and parts
public class QualifierToken extends Token {
    public final Qualifier qualifier;
    public final String[] parts;

    public QualifierToken(int pos, int endPos, Qualifier qualifier, String[] parts) {
        super(pos, endPos, TokenType.QVALUE);
        this.qualifier = qualifier;
        this.parts = parts;
        assertParts();
    }

    private void assertParts() {
        if (qualifier.parts != parts.length) {
            throw new IllegalArgumentException("Illegal amount of input.");
        }
    }

    @Override
    public String toString() {
        return qualifier.name() + ':' + String.join(" ", parts);
    }
}
