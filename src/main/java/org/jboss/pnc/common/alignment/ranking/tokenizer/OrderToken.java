package org.jboss.pnc.common.alignment.ranking.tokenizer;

import lombok.EqualsAndHashCode;
import org.jboss.pnc.common.alignment.ranking.Order;

@EqualsAndHashCode(callSuper = false) // compare just order
public class OrderToken extends Token {
    public final Order order;

    public OrderToken(int pos, int endPos, Order order) {
        super(pos, endPos, TokenType.ORDER);
        this.order = order;
    }

    @Override
    public String toString() {
        return order.name();
    }
}
