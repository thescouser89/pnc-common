package org.jboss.pnc.common;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Optional;

/**
 * @author <a href="mailto:matejonnet@gmail.com">Matej Lazar</a>
 */
public class Json {

    public static <T extends Number> Optional<T> getNumber(JsonNode node, String path) {
        JsonNode jsonNode = node.at(path);
        if (jsonNode.isMissingNode()) {
            return Optional.empty();
        } else {
            return Optional.of((T) jsonNode.numberValue());
        }
    }

    public static Optional<String> getText(JsonNode rootNode, String path) {
        JsonNode jsonNode = rootNode.at(path);
        if (jsonNode.isMissingNode()) {
            return Optional.empty();
        } else {
            return Optional.of(jsonNode.asText());
        }
    }
}
