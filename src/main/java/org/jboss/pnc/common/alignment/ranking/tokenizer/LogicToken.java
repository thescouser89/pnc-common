package org.jboss.pnc.common.alignment.ranking.tokenizer;

import lombok.EqualsAndHashCode;
import org.jboss.pnc.common.alignment.ranking.Logic;

@EqualsAndHashCode(callSuper = false) // compare just logicType
public class LogicToken extends Token {
    public final Logic logicType;

    public LogicToken(int pos, int endPos, Logic logicType) {
        super(pos, endPos, TokenType.LOGIC);
        this.logicType = logicType;
    }

    @Override
    public String toString() {
        return logicType.name().toLowerCase();
    }

}
