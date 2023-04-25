package org.jboss.pnc.common.alignment.ranking;

import org.jboss.pnc.api.dependencyanalyzer.dto.QualifiedVersion;
import org.jboss.pnc.common.alignment.ranking.compiler.Compiler;
import org.jboss.pnc.common.alignment.ranking.compiler.PredicateCompiler;
import org.jboss.pnc.common.alignment.ranking.exception.ValidationException;
import org.jboss.pnc.common.alignment.ranking.parser.DefaultParser;
import org.jboss.pnc.common.alignment.ranking.parser.InternalNode;
import org.jboss.pnc.common.alignment.ranking.parser.Parser;
import org.jboss.pnc.common.alignment.ranking.tokenizer.GenericTokenizer;
import org.jboss.pnc.common.alignment.ranking.tokenizer.Token;
import org.jboss.pnc.common.alignment.ranking.tokenizer.TokenType;
import org.jboss.pnc.common.alignment.ranking.tokenizer.Tokenizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class AlignmentPredicate implements Predicate<QualifiedVersion> {
    private static final EnumSet<TokenType> ALLOWED_TOKENS = EnumSet.of(TokenType.QVALUE, TokenType.COMMA);

    private final Parser parser;
    private final Compiler<Predicate<QualifiedVersion>> compiler;
    private List<Token> tokens;
    private InternalNode root;
    private Predicate<QualifiedVersion> predicate;

    public AlignmentPredicate(String query) throws ValidationException {
        this(query, ver -> true);
    }

    public AlignmentPredicate(String query, Predicate<QualifiedVersion> defaultBehaviour) throws ValidationException {
        this.parser = new DefaultParser();
        this.compiler = new PredicateCompiler();
        compile(query, defaultBehaviour);
    }

    private void compile(String query, Predicate<QualifiedVersion> defaultBehaviour) throws ValidationException {
        if (query == null) {
            this.tokens = Collections.emptyList();
            this.predicate = defaultBehaviour;
            return;
        }

        Tokenizer tokenizer = new GenericTokenizer(query, ALLOWED_TOKENS);

        // Tokens
        this.tokens = new ArrayList<>();
        while (tokenizer.hasNext()) {
            tokens.add(tokenizer.next());
        }

        // Syntax tree
        this.root = parser.generateParseTree(tokens);

        // Compiled predicate
        this.predicate = compiler.compile(root);
    }

    public boolean isEmpty() {
        return tokens.isEmpty();
    }

    public List<Token> getTokens() {
        return tokens;
    }

    @Override
    public boolean test(QualifiedVersion version) {
        return predicate.test(version);
    }
}
