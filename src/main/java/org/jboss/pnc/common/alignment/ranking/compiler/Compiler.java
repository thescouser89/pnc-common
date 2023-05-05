package org.jboss.pnc.common.alignment.ranking.compiler;

import org.jboss.pnc.common.alignment.ranking.parser.InternalNode;

public interface Compiler<T> {
    T compile(InternalNode rootNode);
}
