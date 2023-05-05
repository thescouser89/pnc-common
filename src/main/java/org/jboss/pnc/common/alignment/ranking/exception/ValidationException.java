package org.jboss.pnc.common.alignment.ranking.exception;

import org.jboss.pnc.common.alignment.ranking.tokenizer.Token;

public class ValidationException extends RuntimeException {

    private Integer pos;
    private Integer endPos;
    private Token token;

    public ValidationException(String message, int pos, int endPos) {
        super(message);
        this.pos = pos;
        this.endPos = endPos;
    }

    public ValidationException(String message, int pos) {
        super(message);
        this.pos = pos;
    }

    public ValidationException(String message, Token token) {
        super(message);
        this.token = token;
        this.pos = token.pos;
        this.endPos = token.endPos;
    }

    public ValidationException(String message, Throwable cause, Token token) {
        super(message, cause);
        this.token = token;
        this.pos = token.pos;
        this.endPos = token.endPos;
    }

    public ValidationException(String message) {
        super(message);
    }

    private String positionString() {
        if (pos != null && endPos != null) {
            return "(chars " + pos + ':' + endPos + ")";
        } else if (pos != null) {
            return "(char " + pos + ")";
        }
        return "";
    }

    @Override
    public String getMessage() {
        if (token != null) {
            return "Encountered problem with " + token + " " + positionString() + ". Message: " + super.getMessage();
        }

        if (pos != null) {
            return "Encountered problem while parsing " + positionString() + ". Message: " + super.getMessage();
        }

        return super.getMessage();
    }
}
