package org.jboss.pnc.common.alignment.ranking.tokenizer;

import lombok.EqualsAndHashCode;
import org.jboss.pnc.api.dto.validation.ValidationResult;
import org.jboss.pnc.api.enums.Qualifier;
import org.jboss.pnc.common.alignment.ranking.exception.ValidationException;

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
        ValidationResult validation = qualifier.validate(parts);
        if (!validation.isValid()) {
            throw new ValidationException(validation.getValidationError(), this);
        }
    }

    @Override
    public String toString() {
        return qualifier.name() + ':' + String.join(" ", parts);
    }
}
