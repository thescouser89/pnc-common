package org.jboss.pnc.common.alignment.ranking.tokenizer;

import org.jboss.pnc.api.enums.Qualifier;
import org.jboss.pnc.common.alignment.ranking.Logic;
import org.jboss.pnc.common.alignment.ranking.Order;
import org.jboss.pnc.common.alignment.ranking.exception.ValidationException;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GenericTokenizer implements Tokenizer {

    static final byte EOL = 25;
    private final Set<TokenType> allowedTokens;

    private final Set<TokenType> charKindTokens;
    private final Set<Character> charKindChars;

    private final Set<TokenType> literalKindTokens;

    private final Set<TokenType> enumTokens;

    private final Set<TokenType> stringKindTypes;

    private final Set<Character> blacklistedChars;

    private final StringBuilder buffer;
    private TokenType tokenType;
    private char character;
    private int position;
    private boolean incPosition;
    private final String input;
    private int savePos;

    public GenericTokenizer(String input) {
        this(input, EnumSet.allOf(TokenType.class), Collections.emptySet());
    }

    public GenericTokenizer(String input, Set<TokenType> allowedTokens) {
        this(input, allowedTokens, Collections.emptySet());
    }

    public GenericTokenizer(String input, Set<TokenType> allowedTokens, Set<Character> blacklistedChars) {
        this.input = input;
        this.buffer = new StringBuilder(255);
        this.character = '\0';
        this.position = 0;

        this.allowedTokens = allowedTokens;
        this.charKindTokens = allowedTokens.stream()
                .filter(tt -> tt.kind == TokenKind.CHAR)
                .collect(Collectors.toSet());
        this.charKindChars = charKindTokens.stream().map(t -> t.charLiteral).collect(Collectors.toSet());
        this.literalKindTokens = allowedTokens.stream()
                .filter(tt -> tt.kind == TokenKind.LITERAL)
                .collect(Collectors.toSet());
        this.enumTokens = allowedTokens.stream().filter(tt -> tt.kind == TokenKind.ENUM).collect(Collectors.toSet());

        this.stringKindTypes = Stream.concat(literalKindTokens.stream(), enumTokens.stream())
                .collect(Collectors.toSet());

        this.blacklistedChars = blacklistedChars;

        // don't increase position for first char
        this.incPosition = false;

        nextChar();
    }

    private char get() {
        return character;
    }

    private char nextChar() {
        if (incPosition) {
            position++;
        }
        changeChar();

        return character;
    }

    private boolean hasChar() {
        return position < input.length();
    }

    private void changeChar() {
        if (blacklistedChars.contains(character)) {
            throw new ValidationException("Character '" + character + "' is not allowed.", position);
        }
        if (hasChar()) {
            character = input.charAt(position);
            incPosition = true;
        } else {
            character = EOL;
            incPosition = false;
        }
    }

    private boolean accept(char character) {
        if (this.character == character) {
            nextChar();

            return true;
        }

        return false;
    }

    private boolean acceptEither(char... chars) {
        if (isCurChar(chars)) {
            nextChar();

            return true;
        }

        return false;
    }

    private boolean acceptIf(Predicate<Character> condition) {
        if (condition.test(get())) {
            nextChar();

            return true;
        }

        return false;
    }

    private void rollback(int pos) {
        this.position = pos;
        this.incPosition = false;

        // load char without changing position
        nextChar();
    }

    private boolean acceptString(String string) {
        if (string == null || string.length() == 0) {
            return false;
        }

        int savePosition = position;

        for (char c : string.toCharArray()) {
            if (!isCurChar(c)) {
                rollback(savePosition);

                return false;
            }

            nextChar();
        }

        return true;
    }

    private boolean isCurChar(char... chars) {
        for (char c : chars) {
            if (character == c) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean hasNext() {
        return hasChar() && !justWhitespace();
    }

    private boolean justWhitespace() {
        int pos = position;
        skipWhitespaces();

        if (!hasChar()) {
            rollback(pos);
            return true;
        }
        return false;
    }

    @Override
    public Token next() {
        buffer.setLength(0);
        this.savePos = position;

        skipWhitespaces();

        if (!hasChar()) {
            throw new ValidationException("Just whitespace remaining. No next Token available.", savePos, position);
        }

        savePos = position;
        bufferWord();
        if (buffer.length() > 0) {
            if (!tryParseStringToken()) {
                throw new ValidationException("Unrecognized word '" + buffer.toString() + "'.", savePos, position);
            }
        } else {
            if (!tryParseCharToken()) {
                throw new ValidationException("Unrecognized character '" + get() + "'.", savePos, position);
            }
        }

        switch (tokenType) {
            case QVALUE:
                String[] qValue = buffer.toString().split(":", 2);

                Qualifier qualifier = Qualifier.valueOf(sanitize(qValue[0]));
                String[] values = qValue[1].trim().split("\\s+");

                return new QualifierToken(savePos, position, qualifier, values);
            case LOGIC:
                Logic logic = Logic.valueOf(sanitize(buffer.toString()));
                return new LogicToken(savePos, position, logic);
            case ORDER:
                Order order = Order.valueOf(sanitize(buffer.toString()));
                return new OrderToken(savePos, position, order);
            case LPAREN:
                return new Token(savePos, position, TokenType.LPAREN);
            case RPAREN:
                return new Token(savePos, position, TokenType.RPAREN);
            case SORT_BY:
                return new Token(savePos, position, TokenType.SORT_BY);
            case COMMA:
                return new Token(savePos, position, TokenType.COMMA);
            default:
                throw new IllegalArgumentException("No parsing defined for token type " + tokenType.name());
        }
    }

    private String sanitize(String input) {
        return input.toUpperCase().replace('-', '_');
    }

    private boolean tryParseCharToken() {
        for (var type : TokenType.values()) {
            switch (type) {
                case QVALUE:
                case LOGIC:
                case SORT_BY:
                case ORDER:
                    continue;
                case COMMA:
                case LPAREN:
                case RPAREN:
                    if (isCurChar(type.charLiteral)) {
                        accept(type.charLiteral);
                        tokenType = type;
                        return true;
                    }
                    continue;
                default:
                    return false;
            }
        }
        return false;
    }

    private boolean tryParseStringToken() {
        for (var type : stringKindTypes) {
            switch (type) {
                case LOGIC:
                case SORT_BY:
                case ORDER:
                    if (bufferHas(type)) {
                        tokenType = type;
                        return true;
                    }
                    break;
                case QVALUE:
                    if (bufferHas(TokenType.QVALUE)) {
                        // QUALIFIER__:__VALUES
                        tokenType = TokenType.QVALUE;
                        Qualifier qualifier = bufferGet(Qualifier.class); // QUALIFIER
                        skipWhitespaces();
                        if (!isCurChar(':')) {
                            throw new ValidationException(
                                    "Could not find ':' after parsing Qualifier.",
                                    savePos,
                                    position);
                        }
                        put(); // add ':' to buffer
                        nextChar(); // go to VALUES

                        skipWhitespaces();
                        if (isCurChar('\'', '\"')) { // Quoted VALUES
                            bufferQuotedString(qualifier.parts);
                        } else {
                            for (int part = 0; part < qualifier.parts; part++) { // UNQuoted VALUES
                                skipWhitespaces();
                                if (!bufferWord()) {
                                    throw new ValidationException(
                                            "Qualifier " + qualifier.name() + " requires " + qualifier.parts
                                                    + " word/s.",
                                            savePos,
                                            position);
                                }
                                // add space between parts unless on last iteration
                                if (!(part == qualifier.parts - 1)) {
                                    buffer.append(' ');
                                }
                            }
                        }
                        return true;
                    }
                case LPAREN: // char tokens are handled on empty buffer
                case RPAREN:
                case COMMA:
                    continue;
                default:
                    return false;
            }
        }
        return false;
    }

    private void bufferQuotedString(int parts) {
        if (!isCurChar('\'', '\"')) {
            return;
        }
        char quote = get();
        nextChar();
        int save = buffer.length();

        while (acceptAndPutIf(ch -> ch != quote && hasChar())) {
        }

        if (!hasChar()) {
            throw new ValidationException("Reached the end of buffer without ending quote " + quote, savePos, position);
        }

        if (parts > 1) {
            String quotedString = buffer.substring(save).trim();
            String[] split = quotedString.split("\\s+");
            if (split.length != parts) {
                throw new ValidationException(
                        "Quoted string should have exactly " + parts + " amount of words.",
                        savePos,
                        position);
            }
        }
        nextChar();
    }

    private boolean acceptAndPutIf(Predicate<Character> condition) {
        if (condition.test(get())) {
            put();
            nextChar();

            return true;
        }

        return false;
    }

    private void skipWhitespaces() {
        while (acceptIf(Character::isWhitespace)) {
        }
    }

    private <T extends Enum<T>> T bufferGet(Class<T> enumClass) {
        return T.valueOf(enumClass, sanitize(buffer.toString()));
    }

    private <T extends Enum<T>> boolean bufferHas(Class<T> enumClass) {
        try {
            T.valueOf(enumClass, sanitize(buffer.toString()));
        } catch (IllegalArgumentException exception) {
            return false;
        }
        return true;
    }

    private boolean bufferHas(TokenType tokenType) {
        switch (tokenType.kind) {
            case LITERAL:
                return tokenType.literal.equals(sanitize(buffer.toString()));
            case ENUM:
                return tokenType.getEnumValues().contains(sanitize(buffer.toString()));
            case CHAR:
                throw new IllegalArgumentException("Don't use buffer for character-tokens.");
            default:
                throw new IllegalArgumentException("Unknown TokenKind.");
        }
    }

    private boolean bufferWord() {
        boolean filledBuffer = false;
        skipWhitespaces();
        while (acceptAndPutIf(ch -> !(Character.isWhitespace(ch) || isSpecial()))) {
            filledBuffer = true;
        }
        return filledBuffer;
    }

    private boolean isSpecial() {
        switch (get()) {
            case '"':
            case '\'':
            case EOL:
                return true;
            default:
                return charKindChars.contains(get()) // TokenKind.CHAR characters
                        || (enumTokens.contains(TokenType.QVALUE) && get() == ':'); // With QVALUE ':' is considered
                                                                                    // spec
        }
    }

    private void put() {
        buffer.append(get());
    }
}
