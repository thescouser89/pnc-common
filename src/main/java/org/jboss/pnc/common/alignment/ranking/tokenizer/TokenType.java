package org.jboss.pnc.common.alignment.ranking.tokenizer;

import org.jboss.pnc.api.enums.Qualifier;
import org.jboss.pnc.common.alignment.ranking.Logic;
import org.jboss.pnc.common.alignment.ranking.Order;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public enum TokenType {
    QVALUE(Qualifier.class),
    ORDER(Order.class),
    LOGIC(Logic.class),
    LPAREN('('),
    RPAREN(')'),
    COMMA(','),
    SORT_BY("SORT_BY");

    public final TokenKind kind;

    // raw unparametrized class because Java Enums can't have generics in enum definition, and therefore we lose enum
    // type
    private final Class<? extends Enum<?>> declaringEnum;
    public final char charLiteral;
    public final String literal;

    public List<String> getEnumValues() {
        if (declaringEnum != null) {
            return Arrays.stream(declaringEnum.getEnumConstants()).map(Enum::toString).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    TokenType(String literal) {
        this.kind = TokenKind.LITERAL;
        this.literal = literal;
        declaringEnum = null;
        charLiteral = '\0';
    }

    TokenType(char character) {
        this.kind = TokenKind.CHAR;
        this.literal = null;
        declaringEnum = null;
        charLiteral = character;
    }

    TokenType(Class<? extends Enum<?>> enumeration) {
        this.kind = TokenKind.ENUM;
        this.literal = null;
        declaringEnum = enumeration;
        charLiteral = '\0';
    }
}
