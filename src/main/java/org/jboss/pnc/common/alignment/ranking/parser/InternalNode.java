package org.jboss.pnc.common.alignment.ranking.parser;

import java.util.List;

public interface InternalNode extends Node {
    int childrenCount();

    List<Node> getChildren();

    void removeChild(Node child);

    void switchChild(Node badChild, Node goodChild);
}
