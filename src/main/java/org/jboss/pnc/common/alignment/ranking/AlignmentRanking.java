package org.jboss.pnc.common.alignment.ranking;

import lombok.AllArgsConstructor;
import org.jboss.pnc.api.dependencyanalyzer.dto.QualifiedVersion;
import org.jboss.pnc.common.alignment.ranking.compiler.RankCompiler;
import org.jboss.pnc.common.alignment.ranking.exception.ValidationException;
import org.jboss.pnc.common.alignment.ranking.parser.DefaultParser;
import org.jboss.pnc.common.alignment.ranking.compiler.Compiler;
import org.jboss.pnc.common.alignment.ranking.parser.InternalNode;
import org.jboss.pnc.common.alignment.ranking.parser.Parser;
import org.jboss.pnc.common.alignment.ranking.tokenizer.OrderToken;
import org.jboss.pnc.common.alignment.ranking.tokenizer.GenericTokenizer;
import org.jboss.pnc.common.alignment.ranking.tokenizer.Token;
import org.jboss.pnc.common.alignment.ranking.tokenizer.TokenType;
import org.jboss.pnc.common.alignment.ranking.tokenizer.Tokenizer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.jboss.pnc.api.constants.Defaults.RANK_DELIMITER;

public class AlignmentRanking implements Comparator<QualifiedVersion> {
    private static final EnumSet<TokenType> ALLOWED_TOKENS = EnumSet.of(
            TokenType.QVALUE,
            TokenType.ORDER,
            TokenType.LOGIC,
            TokenType.LPAREN,
            TokenType.RPAREN,
            TokenType.SORT_BY);
    public static final Set<Character> BLACKLISTED_CHARS = Set.of(RANK_DELIMITER);
    private final Parser parser;
    private Compiler<Comparator<QualifiedVersion>> compiler;

    private final List<RankHolder> ranks;

    public AlignmentRanking(List<String> alignmentRanks, Comparator<String> versionStringComparator)
            throws ValidationException {
        this.parser = new DefaultParser();
        this.compiler = new RankCompiler(versionStringComparator);

        // on EMPTY of NULL, generate at least SUFFIX-VERSION rank which is default behaviour
        this.ranks = compile(alignmentRanks == null ? List.of() : alignmentRanks);
    }

    private List<RankHolder> compile(List<String> alignmentRanks) throws ValidationException {
        List<RankHolder> rankHolders = new ArrayList<>();

        boolean foundDefault = false;
        for (int idx = 0; idx < alignmentRanks.size(); idx++) {
            String rank = alignmentRanks.get(idx);

            // Tokens
            Tokenizer tokenizer = new GenericTokenizer(rank, ALLOWED_TOKENS, BLACKLISTED_CHARS);
            List<Token> tokens = new ArrayList<>();
            while (tokenizer.hasNext()) {
                tokens.add(tokenizer.next());
            }

            // Try to find mandatory single SUFFIX-VERSION rank
            if (tokens.size() == 1 && tokens.get(0) instanceof OrderToken) {
                foundDefault = true;
                if (idx != alignmentRanks.size() - 1) {
                    throw new ValidationException("SUFFIX-VERSION is not last rank.");
                }
            }

            // Syntax tree
            InternalNode rootNode = parser.generateParseTree(tokens);

            // Compiled comparator
            Comparator<QualifiedVersion> comparator = compiler.compile(rootNode);

            rankHolders.add(new RankHolder(rank, tokens, rootNode, comparator));
        }

        // add Default SUFFIX-VERSION if not present
        if (!foundDefault) {
            rankHolders.addAll(compile(List.of("SUFFIX-VERSION")));
        }

        return rankHolders;
    }

    public List<List<Token>> getRanksAsTokens() {
        List<List<Token>> tokenizedRanks = new ArrayList<>();
        ranks.forEach(rank -> tokenizedRanks.add(rank.tokens));

        return tokenizedRanks;
    }

    public boolean isDefault() {
        return ranks.size() == 1 && ranks.get(0).rank.equals("SUFFIX-VERSION");
    }

    public void overrideVersionComparator(Comparator<String> versionComparator) {
        this.compiler = new RankCompiler(versionComparator);
        recompile();
    }

    private void recompile() {
        for (int i = 0; i < ranks.size(); i++) {
            RankHolder rank = ranks.get(i);
            ranks.set(i, new RankHolder(rank.rank, rank.tokens, rank.root, compiler.compile(rank.root)));
        }
    }

    @Override
    public int compare(QualifiedVersion ver1, QualifiedVersion ver2) {
        int result = 0;
        for (RankHolder rank : ranks) {
            result = rank.comparator.compare(ver1, ver2);

            if (result != 0) {
                return result;
            }
        }
        return result;
    }

    @AllArgsConstructor
    private static class RankHolder {
        private final String rank;
        private final List<Token> tokens;
        private final InternalNode root;
        private final Comparator<QualifiedVersion> comparator;
    }
}
